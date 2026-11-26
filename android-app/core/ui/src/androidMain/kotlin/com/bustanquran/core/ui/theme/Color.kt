package com.bustanquran.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Design tokens, copied 1:1 from `design/README.md` ("Token desain").
 *
 * Every value below appears verbatim as a CSS hex string in `design/source`,
 * so this file is the only place in the app where a colour may be written down.
 * Screens must reference these names, never a literal `Color(0x...)`.
 */

// Core palette ---------------------------------------------------------------
val BustanSand = Color(0xFFFBF6EC)
val BustanParentBg = Color(0xFFF4F1EA)
val BustanInk = Color(0xFF23302C)
val BustanMuted = Color(0xFF5B6661)
val BustanTeal = Color(0xFF0F6B5C)
val BustanTealDark = Color(0xFF0A4F44)
val BustanTealLight = Color(0xFF13806E)
val BustanMint = Color(0xFFDCEFE9)
val BustanSaffron = Color(0xFFF2B33D)
val BustanButter = Color(0xFFFCE7B8)
val BustanCoral = Color(0xFFE0734F)
val BustanSky = Color(0xFFCFE7F3)
val BustanWhite = Color(0xFFFFFFFF)

// Lines, dividers and the flat offset shadows -------------------------------
val BustanBorder = Color(0xFFE3D8C1)
val BustanBorderSoft = Color(0xFFEADFC8)
val BustanDivider = Color(0xFFEEE8DA)
val BustanDashed = Color(0xFFCDBF9F)

// Component fills used by the designs --------------------------------------
val BustanProgressTrack = Color(0xFFE8EEEC)
val BustanSegmentedTrack = Color(0xFFEFE7D6)
val BustanSwitchOff = Color(0xFFCFC7B5)
val BustanSaffronStroke = Color(0xFFC98A12)
val BustanSaffronDeep = Color(0xFFE7B96A)
val BustanSaffronFill = Color(0xFFF6D58E)
val BustanAmberText = Color(0xFF8A5A10)
val BustanCoralText = Color(0xFFB4532F)
val BustanCoralSoft = Color(0xFFF4B9A2)
val BustanCoralSoftEdge = Color(0xFFDE937A)

// Island and avatar art colours --------------------------------------------
val BustanMintArt = Color(0xFF9ED3B5)
val BustanMintArtEdge = Color(0xFF6FB48F)
val BustanLilacArt = Color(0xFFC9C3E6)
val BustanLilacArtEdge = Color(0xFFA79FD1)
val BustanBlueArt = Color(0xFFD7E8F5)
val BustanBlueArtMid = Color(0xFF5B8DB8)
val BustanBlueArtDeep = Color(0xFF1F4E79)
val BustanCamelArt = Color(0xFFE2A54B)
val BustanCamelArtEdge = Color(0xFFC9883A)
val BustanCamelFur = Color(0xFFD89B4A)
val BustanGreenArt = Color(0xFF3E8E7E)
val BustanNight = Color(0xFF1E2A4A)
val BustanNightSoft = Color(0xFF2D3A5C)
val BustanSkyDeep = Color(0xFF2E4A57)

// Deeper teal surfaces and soft text used by the reciter screens ------------
/** `#0A5A4D`: header of `Hafalan` and `MurottalQuraisy`, back tile on teal. */
val BustanTealDeep = Color(0xFF0A5A4D)
/** `#CDE7DF`: secondary text on the deep teal header. */
val BustanMintSoft = Color(0xFFCDE7DF)
/** `#EDE4D0`: 52 dp skip circles in `Hafalan`. */
val BustanSandDeep = Color(0xFFEDE4D0)
/** `#D9CDB2`: 2 dp outline of the chips in `Hafalan` and `MurottalQuraisy`. */
val BustanBorderWarm = Color(0xFFD9CDB2)
/** `#FFF4D6`: the highlighted ayah card in `MurottalQuraisy`. */
val BustanSaffronWash = Color(0xFFFFF4D6)
/** `#4F5A55`: translation line inside an active ayah card. */
val BustanInkSoft = Color(0xFF4F5A55)
/** `#7D8A85`: ghosted ayah line and balloon strings. */
val BustanInkGhost = Color(0xFF7D8A85)
/** `#B8AE97`: blanked word placeholders. */
val BustanInkFaint = Color(0xFFB8AE97)
/** `#DDF0F8`: sky background of the letter-balloon game. */
val BustanSkyLight = Color(0xFFDDF0F8)
/** `#F7D3C5`: 6 dp ring around the coral record button in `Hafalan`. */
val BustanCoralRing = Color(0xFFF7D3C5)
/** `#F1ECE0`: the "not yet" cell of the hafalan map grid. */
val BustanSandTint = Color(0xFFF1ECE0)
/** `#8F3A1C`: the number inside a "needs review" cell of the hafalan map. */
val BustanCoralDeepText = Color(0xFF8F3A1C)
/** `#6E4708`: the number inside a "learning" cell of the hafalan map. */
val BustanAmberDeepText = Color(0xFF6E4708)
/** `#FBE3D9`: fill of a wrong answer in `SambungAyat` and the tajwid check. */
val BustanCoralWash = Color(0xFFFBE3D9)
/** `#D9C08A`: the ghost glyph inside a dashed word slot in `SusunAyat`. */
val BustanSaffronGhost = Color(0xFFD9C08A)
/** `#EFE6D2`: the filled letter behind the traced outline in `HijaiyahLesson`. */
val BustanTraceInk = Color(0xFFEFE6D2)

// Tajwid lesson surface -----------------------------------------------------
/** `#EEF3FA`: page background of `TajwidLesson`. */
val BustanTajwidBg = Color(0xFFEEF3FA)
/** `#D6DEE8`: 2 dp outline of an idle quick-check tile. */
val BustanTajwidEdge = Color(0xFFD6DEE8)
/** `#4A5560`: rule description and feedback copy on the tajwid page. */
val BustanTajwidInk = Color(0xFF4A5560)

// Translucent scrims ------------------------------------------------------
val BustanInkScrim = Color(0x5223302C)

// ---------------------------------------------------------------------------
// Legacy aliases kept so screens written before P-18 keep compiling. New code
// should use the `Bustan*` names only.
// ---------------------------------------------------------------------------
val QpkSand = BustanSand
val QpkInk = BustanInk
val QpkMuted = BustanMuted
val QpkTeal = BustanTeal
val QpkTealDark = BustanTealDark
val QpkMint = BustanMint
val QpkSaffron = BustanSaffron
val QpkButter = BustanButter
val QpkCoral = BustanCoral
val QpkSky = BustanSky
val QpkParentBg = BustanParentBg
val QpkWhite = BustanWhite
val QpkCardBorder = BustanBorder

/** Was `#FFD15C`, a colour that does not exist anywhere in the design. */
val QpkGold = BustanSaffron

// ─── Mushaf (artboards 36–38) ──────────────────────────────────────────────
// Colours read from `design/source/MushafPage.dc.html` and
// `MushafSettings.dc.html`; they are page-specific so they stay out of the
// general token list above.

/** `#F7F0E1`: the paper-like backdrop behind the mushaf page. */
val BustanMushafBg = Color(0xFFF7F0E1)

/** `#FFFDF7`: the page itself, framed by a 2 dp gold rule. */
val BustanMushafPage = Color(0xFFFFFDF7)

/** `#C9A65A`: gold frame and surah-name pill of the mushaf page. */
val BustanMushafGold = Color(0xFFC9A65A)

/** `#2E8B57` Ghunnah, `#1F6FB2` Qalqalah, `#C0392B` Mad, `#8E44AD` Ikhfa. */
val BustanTajwidGhunnah = Color(0xFF2E8B57)
val BustanTajwidQalqalah = Color(0xFF1F6FB2)
val BustanTajwidMad = Color(0xFFC0392B)
val BustanTajwidIkhfa = Color(0xFF8E44AD)

