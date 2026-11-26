# P-09 — Kerangka game

Create a reusable game framework in :feature:games:
- GameHost composable: top bar (back, title, round x/5), voice instruction with replay button, hint system, Level 1 (hints) / Level 2 (no hints), result -> GameResult(gameId, profileId, correct, total, durationSec) sent to RewardEngine.
- GameItem source from content.db game_content; no content hard-coded in Kotlin.
- Rules: never game-over, wrong answer -> hint + retry, Qur'an text never used as a shootable or falling object.
- GameList screen with stage tabs (3-4, 5-6, 7-10) matching artboard GameList.
Provide one sample game (Balon Huruf) fully implemented on the framework as the reference.
