#!/usr/bin/env python3
"""Import Qur'an text, translation and recitation URLs into `content.db`.

Source of truth is the **eQuran.id API v2** — the same endpoints the
`htdocs/Apps SakuSantri` Flutter app consumes (`/surat`, `/surat/{n}`):

    teksArab      -> ayah.text_uthmani      (Arabic, Uthmani spelling)
    teksLatin     -> ayah.text_simple       (transliteration, used for search)
    teksIndonesia -> translation.text_kids  (lang = 'id')
    audio{"05"}   -> meta.audio_template    (per-ayah MP3, 3-digit surah + ayah)

The script never changes the schema: it opens the bundled asset database,
replaces the rows for the imported surahs and leaves every other table
(letters, tajwid, games, duas, stories) untouched. Room's `createFromAsset`
validation compares the schema only, so keeping the DDL identical is what
lets us ship new content without a database migration.

Usage
-----
    python3 tools/scripts/fetch_quran_content.py            # write the asset db
    python3 tools/scripts/fetch_quran_content.py --check    # verify only
"""

from __future__ import annotations

import argparse
import hashlib
import json
import sqlite3
import sys
import urllib.error
import urllib.request
from pathlib import Path

API_BASE = "https://equran.id/api/v2"

REPO_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_DB = REPO_ROOT / "core/data/src/androidMain/assets/content.db"
DEFAULT_CACHE = REPO_ROOT / "tools/scripts/.cache"

# The child flow only reaches juz 30 (the surah library filters on
# `juz_start = 30`) plus Al-Fatihah, so that is the set we ship offline.
INCLUDED_SURAHS: tuple[int, ...] = (1,) + tuple(range(78, 115))

# First page of each surah in the 604-page Madani mushaf. Pages are what the
# mushaf artboard groups ayat by; the API does not expose them.
FIRST_PAGE: dict[int, int] = {
    1: 1,
    78: 582, 79: 583, 80: 585, 81: 586, 82: 587, 83: 587, 84: 589,
    85: 590, 86: 591, 87: 591, 88: 592, 89: 593, 90: 594, 91: 595,
    92: 595, 93: 596, 94: 596, 95: 597, 96: 597, 97: 598, 98: 598,
    99: 599, 100: 599, 101: 600, 102: 600, 103: 601, 104: 601,
    105: 601, 106: 602, 107: 602, 108: 602, 109: 603, 110: 603,
    111: 603, 112: 604, 113: 604, 114: 604,
}

REVELATION = {"Mekah": "makki", "Madinah": "madani"}

# Reciter "05" of the API payload (Mishary Rashid Alafasy) — the voice the
# Murottal artboard is designed around.
RECITER_KEY = "05"
RECITER_LATIN = "Misyari-Rasyid-Al-Afasi"
AUDIO_TEMPLATE = (
    f"https://cdn.equran.id/audio-partial/{RECITER_LATIN}/{{surah}}{{ayah}}.mp3"
)

SOURCE_ID = "equran-id"
CONTENT_VERSION = "1.1.0"


def fetch_json(url: str, cache_dir: Path, *, refresh: bool = False) -> dict:
    """GET a JSON document, memoised on disk so reruns do not hammer the API."""
    cache_dir.mkdir(parents=True, exist_ok=True)
    name = url.replace(API_BASE, "").strip("/").replace("/", "_") or "root"
    cached = cache_dir / f"{name}.json"
    if cached.exists() and not refresh:
        return json.loads(cached.read_text(encoding="utf-8"))

    payload = None
    for attempt in range(1, 4):
        try:
            with urllib.request.urlopen(url, timeout=60) as response:
                payload = json.loads(response.read().decode("utf-8"))
            break
        except (urllib.error.URLError, TimeoutError) as error:
            if attempt == 3:
                raise SystemExit(f"Failed to fetch {url}: {error}")
    cached.write_text(json.dumps(payload, ensure_ascii=False), encoding="utf-8")
    return payload


def sha256(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def audio_url(surah_id: int, ayah_no: int) -> str:
    return AUDIO_TEMPLATE.replace("{surah}", f"{surah_id:03d}").replace(
        "{ayah}", f"{ayah_no:03d}"
    )



def build_rows(detail: dict) -> tuple[dict, list[dict], list[dict]]:
    """Map one `/surat/{n}` payload onto the three tables we fill."""
    surah_id = int(detail["nomor"])
    surah_row = {
        "id": surah_id,
        "name_ar": detail["nama"],
        "name_tr": detail["namaLatin"],
        "ayah_count": int(detail["jumlahAyat"]),
        "revelation": REVELATION.get(detail["tempatTurun"], "makki"),
        "juz_start": 1 if surah_id == 1 else 30,
    }
    page = FIRST_PAGE[surah_id]
    ayah_rows, translation_rows = [], []
    for ayah in detail["ayat"]:
        text = " ".join(ayah["teksArab"].split())
        ayah_no = int(ayah["nomorAyat"])
        ayah_rows.append(
            {
                "surah_id": surah_id,
                "ayah_no": ayah_no,
                "page": page,
                "juz": surah_row["juz_start"],
                "text_uthmani": text,
                "text_simple": " ".join(ayah.get("teksLatin", "").split()),
                "checksum": sha256(text),
            }
        )
        translation_rows.append(
            {
                "surah_id": surah_id,
                "ayah_no": ayah_no,
                "lang": "id",
                "text_kids": " ".join(ayah.get("teksIndonesia", "").split()),
                "source_id": SOURCE_ID,
            }
        )
    return surah_row, ayah_rows, translation_rows


def import_content(db_path: Path, cache_dir: Path, *, refresh: bool, check: bool) -> int:
    if not db_path.exists():
        raise SystemExit(f"content.db not found: {db_path}")

    listing = fetch_json(f"{API_BASE}/surat", cache_dir, refresh=refresh)
    available = {int(item["nomor"]) for item in listing["data"]}
    missing = [n for n in INCLUDED_SURAHS if n not in available]
    if missing:
        raise SystemExit(f"API listing is missing surahs: {missing}")

    connection = sqlite3.connect(db_path)
    total_ayahs = 0
    imported: list[int] = []
    try:
        for surah_id in INCLUDED_SURAHS:
            detail = fetch_json(
                f"{API_BASE}/surat/{surah_id}", cache_dir, refresh=refresh
            )["data"]
            surah_row, ayah_rows, translation_rows = build_rows(detail)
            if len(ayah_rows) != surah_row["ayah_count"]:
                raise SystemExit(
                    f"Surah {surah_id}: expected {surah_row['ayah_count']} ayat, "
                    f"got {len(ayah_rows)}"
                )
            imported.append(surah_id)
            total_ayahs += len(ayah_rows)
            if check:
                continue
            with connection:
                connection.execute("DELETE FROM ayah WHERE surah_id = ?", (surah_id,))
                connection.execute(
                    "DELETE FROM translation WHERE surah_id = ?", (surah_id,)
                )
                connection.execute("DELETE FROM surah WHERE id = ?", (surah_id,))
                connection.execute(
                    """INSERT INTO surah
                       (id, name_ar, name_tr, ayah_count, revelation, juz_start)
                       VALUES (:id, :name_ar, :name_tr, :ayah_count, :revelation, :juz_start)""",
                    surah_row,
                )
                connection.executemany(
                    """INSERT INTO ayah
                       (surah_id, ayah_no, page, juz, text_uthmani, text_simple, checksum)
                       VALUES (:surah_id, :ayah_no, :page, :juz, :text_uthmani, :text_simple, :checksum)""",
                    ayah_rows,
                )
                connection.executemany(
                    """INSERT INTO translation
                       (surah_id, ayah_no, lang, text_kids, source_id)
                       VALUES (:surah_id, :ayah_no, :lang, :text_kids, :source_id)""",
                    translation_rows,
                )



        if not check:
            meta = {
                "version": CONTENT_VERSION,
                "surah_count": str(len(imported)),
                "ayah_count": str(total_ayahs),
                "content_source": API_BASE,
                "audio_template": AUDIO_TEMPLATE,
                "audio_reciter": f"{RECITER_KEY}:{RECITER_LATIN}",
                "translation_lang": "id",
            }
            with connection:
                connection.executemany(
                    "INSERT OR REPLACE INTO meta (key, value) VALUES (?, ?)",
                    meta.items(),
                )
                connection.execute(
                    """INSERT OR REPLACE INTO source
                       (id, name, license, url, verified_by, verified_at)
                       VALUES (?, ?, ?, ?, ?, ?)""",
                    (
                        SOURCE_ID,
                        "eQuran.id (Kemenag text & Indonesian translation)",
                        "Free to use with attribution - https://equran.id",
                        "https://equran.id/apidev/v2",
                        "content-import",
                        "checked against the live API at import time",
                    ),
                )

        stored_surahs = connection.execute("SELECT COUNT(*) FROM surah").fetchone()[0]
        stored_ayahs = connection.execute("SELECT COUNT(*) FROM ayah").fetchone()[0]
        stored_translations = connection.execute(
            "SELECT COUNT(*) FROM translation WHERE lang = 'id'"
        ).fetchone()[0]
    finally:
        connection.close()

    mode = "verified" if check else "imported"
    print(f"{mode}: {len(imported)} surahs / {total_ayahs} ayat from {API_BASE}")
    print(
        f"database now holds {stored_surahs} surahs, {stored_ayahs} ayat, "
        f"{stored_translations} Indonesian translations"
    )
    return total_ayahs


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description="Import Qur'an content from eQuran.id")
    parser.add_argument("--db", type=Path, default=DEFAULT_DB)
    parser.add_argument("--cache", type=Path, default=DEFAULT_CACHE)
    parser.add_argument("--refresh", action="store_true", help="ignore the HTTP cache")
    parser.add_argument("--check", action="store_true", help="validate without writing")
    args = parser.parse_args(argv)
    import_content(args.db, args.cache, refresh=args.refresh, check=args.check)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
