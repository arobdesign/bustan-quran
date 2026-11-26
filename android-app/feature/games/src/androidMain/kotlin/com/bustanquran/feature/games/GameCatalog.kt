package com.bustanquran.feature.games

import androidx.compose.ui.graphics.Color
import com.bustanquran.core.ui.theme.BustanBlueArt
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanMint

/**
 * The catalogue from `GameList.dc.html`: 24 games in three age stages, each
 * with the number shown on its tile, the design's English name and the
 * one-line goal. Routes are the ones already wired in `BustanQuranNavHost`.
 */
data class GameEntry(
    val number: Int,
    val route: String,
    val nameRes: Int,
    val goalRes: Int,
)

data class GameStage(
    val labelRes: Int,
    val taglineRes: Int,
    val tint: Color,
    val games: List<GameEntry>,
)

private fun game(number: Int, route: String, nameRes: Int, goalRes: Int) =
    GameEntry(number = number, route = route, nameRes = nameRes, goalRes = goalRes)

val GameStages: List<GameStage> = listOf(
    GameStage(
        labelRes = R.string.game_stage_little_stars,
        taglineRes = R.string.game_tagline_little_stars,
        tint = BustanButter,
        games = listOf(
            game(1, "balon_huruf", R.string.g1_name, R.string.g1_goal),
            game(2, "g_kebun_huruf", R.string.g2_name, R.string.g2_goal),
            game(3, "hijaiyah_lesson", R.string.g3_name, R.string.g3_goal),
            game(4, "g_bayangan", R.string.g4_name, R.string.g4_goal),
            game(5, "g_kereta_surah", R.string.g5_name, R.string.g5_goal),
            game(6, "doa_harian", R.string.g6_name, R.string.g6_goal),
            game(7, "ikan_harakat", R.string.g7_name, R.string.g7_goal),
            game(8, "g_bintang", R.string.g8_name, R.string.g8_goal),
        ),
    ),
    GameStage(
        labelRes = R.string.game_stage_explorers,
        taglineRes = R.string.game_tagline_explorers,
        tint = BustanMint,
        games = listOf(
            game(9, "g_sambung_huruf", R.string.g9_name, R.string.g9_goal),
            game(10, "g_lari_hijaiyah", R.string.g10_name, R.string.g10_goal),
            game(11, "g_ayat_hilang", R.string.g11_name, R.string.g11_goal),
            game(12, "g_urutkan_ayat", R.string.g12_name, R.string.g12_goal),
            game(13, "memori_kata", R.string.g13_name, R.string.g13_goal),
            game(14, "hafalan", R.string.g14_name, R.string.g14_goal),
            game(15, "g_pasar_huruf", R.string.g15_name, R.string.g15_goal),
            game(16, "g_tebak_surah", R.string.g16_name, R.string.g16_goal),
        ),
    ),
    GameStage(
        labelRes = R.string.game_stage_young_hafiz,
        taglineRes = R.string.game_tagline_young_hafiz,
        tint = BustanBlueArt,
        games = listOf(
            game(17, "susun_ayat", R.string.g17_name, R.string.g17_goal),
            game(18, "g_susun_arti", R.string.g18_name, R.string.g18_goal),
            game(19, "tajwid_lesson", R.string.g19_name, R.string.g19_goal),
            game(20, "sambung_ayat", R.string.g20_name, R.string.g20_goal),
            game(21, "g_kuis_kisah", R.string.g21_name, R.string.g21_goal),
            game(22, "hafalan_map", R.string.g22_name, R.string.g22_goal),
            game(23, "g_keluarga", R.string.g23_name, R.string.g23_goal),
            game(24, "mode_guru", R.string.g24_name, R.string.g24_goal),
        ),
    ),
)
