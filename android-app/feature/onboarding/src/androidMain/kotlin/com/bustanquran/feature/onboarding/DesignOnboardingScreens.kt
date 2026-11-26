package com.bustanquran.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bustanquran.core.data.database.model.ChildProfileEntity
import com.bustanquran.core.ui.components.AnimalAvatar
import com.bustanquran.core.ui.components.AnimalAvatarImage
import com.bustanquran.core.ui.components.BackButton
import com.bustanquran.core.ui.components.BustanAppLogo
import com.bustanquran.core.ui.components.BustanArt
import com.bustanquran.core.ui.components.BustanCard
import com.bustanquran.core.ui.components.BustanIcons
import com.bustanquran.core.ui.components.BustanPrimaryButton
import com.bustanquran.core.ui.components.BustanShadowedClickable
import com.bustanquran.core.ui.components.bustanShadow
import com.bustanquran.core.ui.components.HudHudMascot
import com.bustanquran.core.ui.theme.BustanAmberText
import com.bustanquran.core.ui.theme.BustanBlueArt
import com.bustanquran.core.ui.theme.BustanBlueArtDeep
import com.bustanquran.core.ui.theme.BustanBorder
import com.bustanquran.core.ui.theme.BustanBorderSoft
import com.bustanquran.core.ui.theme.BustanButter
import com.bustanquran.core.ui.theme.BustanCoral
import com.bustanquran.core.ui.theme.BustanDashed
import com.bustanquran.core.ui.theme.BustanInk
import com.bustanquran.core.ui.theme.BustanMint
import com.bustanquran.core.ui.theme.BustanMuted
import com.bustanquran.core.ui.theme.BustanParentBg
import com.bustanquran.core.ui.theme.BustanSaffron
import com.bustanquran.core.ui.theme.BustanSand
import com.bustanquran.core.ui.theme.BustanShapeField
import com.bustanquran.core.ui.theme.BustanShapePill
import com.bustanquran.core.ui.theme.BustanShapeProfile
import com.bustanquran.core.ui.theme.BustanTeal
import com.bustanquran.core.ui.theme.BustanTealDark
import com.bustanquran.core.ui.theme.BustanTealLight
import com.bustanquran.core.ui.theme.BustanTextStyles
import com.bustanquran.core.ui.theme.BustanWhite

/**
 * Artboards 44, 10, 11 and 01, rebuilt from
 * `design/source/{Loading,Onboarding,ParentGate,Main}.dc.html`.
 *
 * Every size, radius and colour below is the CSS value from those files
 * (1 CSS px = 1 dp, font-size px = sp). Copy lives in `res/values/strings.xml`
 * so the Arabic, French and German bundles stay in sync.
 */
// ─── 44 · Loading ────────────────────────────────────────────────────────────

@Composable
fun LoadingScreen(
    onLoaded: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        onLoaded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanTeal)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = BustanArt.AppLogoOnSand,
            contentDescription = stringResource(R.string.app_name),
            tint = Color.Unspecified,
            modifier = Modifier.size(104.dp),
        )
        Text(
            text = stringResource(R.string.app_name),
            style = BustanTextStyles.titleLarge.copy(fontSize = 28.sp, lineHeight = 31.sp),
            color = BustanWhite,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(BustanSaffron.copy(alpha = 1f - index * 0.35f)),
                )
            }
        }
        Text(
            text = stringResource(R.string.loading_bismillah),
            style = BustanTextStyles.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = BustanMint,
        )
    }
}

// ─── 10 · Onboarding (welcome + language) ────────────────────────────────────

private data class LanguageOption(val code: String, val label: String, val isArabic: Boolean = false)

private val OnboardingLanguages = listOf(
    LanguageOption("en", "English"),
    LanguageOption("ar", "العربية", isArabic = true),
    LanguageOption("fr", "Français"),
    LanguageOption("de", "Deutsch"),
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onContinue: () -> Unit,
    onSignIn: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    OnboardingContent(
        selectedLanguage = uiState.selectedLanguage,
        onSelectLanguage = viewModel::selectLanguage,
        onContinue = onContinue,
        onSignIn = onSignIn,
    )
}

/** Stateless body, so the debug catalog and previews can render it directly. */
@Composable
fun OnboardingContent(
    selectedLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onContinue: () -> Unit,
    onSignIn: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize().background(BustanTeal).clipToBounds()) {
        // Decorative circles, `position: absolute` in the design.
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-120).dp)
                .size(360.dp)
                .clip(CircleShape)
                .background(BustanTealLight),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-60).dp, y = 90.dp)
                .size(14.dp)
                .clip(CircleShape)
                .background(BustanSaffron),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-110).dp, y = 150.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(BustanButter),
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 96.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                HudHudMascot(size = 110.dp)
                Text(
                    text = stringResource(R.string.onboarding_title),
                    style = BustanTextStyles.displayLarge,
                    color = BustanWhite,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.onboarding_subtitle),
                    style = BustanTextStyles.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = BustanMint,
                    textAlign = TextAlign.Center,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(BustanSand)
                    .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_choose_language),
                    style = BustanTextStyles.labelLarge,
                    color = BustanInk,
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OnboardingLanguages.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { language ->
                                LanguageTile(
                                    language = language,
                                    selected = selectedLanguage == language.code,
                                    onClick = { onSelectLanguage(language.code) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.onboarding_coming_soon),
                    style = BustanTextStyles.captionSoft,
                    color = BustanMuted,
                )
                Spacer(modifier = Modifier.weight(1f))
                BustanPrimaryButton(
                    text = stringResource(R.string.onboarding_get_started),
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 44.dp)
                        .clip(BustanShapePill)
                        .clickable(onClick = onSignIn),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_have_account),
                        style = BustanTextStyles.labelMediumStrong,
                        color = BustanTeal,
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageTile(
    language: LanguageOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .clip(BustanShapeField)
            .background(BustanWhite)
            .border(
                width = if (selected) 3.dp else 2.dp,
                color = if (selected) BustanTeal else BustanBorder,
                shape = BustanShapeField,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = language.label,
            style = if (language.isArabic) BustanTextStyles.arabicUi else BustanTextStyles.bodyMedium,
            color = BustanInk,
        )
    }
}
// ─── 11 · Parent gate ────────────────────────────────────────────────────────

@Composable
fun ParentGateScreen(
    viewModel: OnboardingViewModel,
    onPassed: () -> Unit,
    onCancelled: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    ParentGateContent(
        questionText = uiState.parentGateChallenge.questionText,
        options = uiState.parentGateChallenge.options,
        failedCount = uiState.parentGateFailedCount,
        onAnswer = { answer -> if (viewModel.submitParentGateAnswer(answer)) onPassed() },
        onCancelled = onCancelled,
    )
}

/** Stateless body, so the debug catalog and previews can render it directly. */
@Composable
fun ParentGateContent(
    questionText: String,
    options: List<Int>,
    failedCount: Int,
    onAnswer: (Int) -> Unit,
    onCancelled: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BustanParentBg)
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        BackButton(onClick = onCancelled)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(BustanInk),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = BustanIcons.Lock,
                    contentDescription = null,
                    tint = BustanWhite,
                    modifier = Modifier.size(36.dp),
                )
            }
            Text(
                text = stringResource(R.string.parent_gate_title),
                style = BustanTextStyles.headlineLarge,
                color = BustanInk,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.parent_gate_subtitle),
                style = BustanTextStyles.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = BustanMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 300.dp),
            )
        }

        BustanCard(
            modifier = Modifier.fillMaxWidth(),
            shape = BustanShapeProfile,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Text(
                text = questionText,
                style = BustanTextStyles.labelMediumStrong,
                color = BustanMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                options.forEach { option ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BustanSand)
                            .border(2.dp, BustanBorder, RoundedCornerShape(18.dp))
                            .clickable { onAnswer(option) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = option.toString(),
                            style = BustanTextStyles.displayMedium,
                            color = BustanInk,
                        )
                    }
                }
            }
        }

        if (failedCount > 0) {
            Text(
                text = stringResource(R.string.parent_gate_wrong),
                style = BustanTextStyles.caption,
                color = BustanCoral,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.parent_gate_footnote),
            style = BustanTextStyles.captionSoft,
            color = BustanMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
// ─── 01 · Choose a child profile ─────────────────────────────────────────────

@Composable
fun MainScreen(
    viewModel: OnboardingViewModel,
    onProfileSelected: (String) -> Unit,
    onAddChildClicked: () -> Unit,
    onParentDashboardClicked: () -> Unit
) {
    val profiles by viewModel.profiles.collectAsState()
    MainContent(
        profiles = profiles,
        onProfileSelected = onProfileSelected,
        onAddChildClicked = onAddChildClicked,
        onParentDashboardClicked = onParentDashboardClicked,
    )
}

/** Stateless body, so the debug catalog and previews can render it directly. */
@Composable
fun MainContent(
    profiles: List<ChildProfileEntity>,
    onProfileSelected: (String) -> Unit,
    onAddChildClicked: () -> Unit,
    onParentDashboardClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(BustanSand).clipToBounds()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 120.dp, y = (-170).dp)
                .size(320.dp)
                .clip(CircleShape)
                .background(BustanMint),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-70).dp, y = 60.dp)
                .size(180.dp)
                .clip(CircleShape)
                .background(BustanButter),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BustanAppLogo(size = 72.dp)
                Text(
                    text = stringResource(R.string.app_name),
                    style = BustanTextStyles.displayMedium,
                    color = BustanTeal,
                )
                Text(
                    text = stringResource(R.string.main_who_is_learning),
                    style = BustanTextStyles.bodyLarge,
                    color = BustanInk,
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(profiles, key = { it.id }) { profile ->
                    ChildProfileCard(
                        profile = profile,
                        onClick = { onProfileSelected(profile.id) },
                    )
                }
                item {
                    AddChildTile(onClick = onAddChildClicked)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 48.dp)
                        .clip(BustanShapePill)
                        .background(BustanWhite)
                        .border(2.dp, BustanInk, BustanShapePill)
                        .clickable(onClick = onParentDashboardClicked)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = BustanIcons.Lock,
                            contentDescription = null,
                            tint = BustanInk,
                            modifier = Modifier.size(20.dp),
                        )
                        Text(
                            text = stringResource(R.string.main_for_parents),
                            style = BustanTextStyles.labelLarge,
                            color = BustanInk,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.main_footnote),
                    style = BustanTextStyles.captionSoft,
                    color = BustanMuted,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
/**
 * Profile tile: avatar disc 84 dp in the tier tint, Baloo 20/700 name, and a
 * tier pill. Each tier has its own colour pair in `Main.dc.html`.
 */
@Composable
fun ChildProfileCard(
    profile: ChildProfileEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // `box-shadow: 0 2px 0 #EADFC8` around the whole tile, like the
            // design, instead of `BustanShadowedClickable` whose shadow fills
            // the grid cell before the card does.
            .bustanShadow(color = BustanBorderSoft, shape = BustanShapeProfile)
            .clip(BustanShapeProfile)
            .background(BustanWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(tierTint(profile.stage)),
            contentAlignment = Alignment.Center,
        ) {
            AnimalAvatarImage(avatar = avatarArtFor(profile.avatar), size = 56.dp)
        }
        Text(
            text = profile.nickname,
            style = BustanTextStyles.titleSmall,
            color = BustanInk,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = Modifier
                .clip(BustanShapePill)
                .background(tierTint(profile.stage))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = stringResource(tierLabelRes(profile.stage)),
                style = BustanTextStyles.caption,
                color = tierLabelColor(profile.stage),
            )
        }
    }
}

@Composable
private fun AddChildTile(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            // The dashed tile is as tall as a profile tile:
            // 20 + 84 + 8 + 23 + 8 + 26 + 20 = 189.
            .fillMaxWidth()
            .height(189.dp)
            .clip(BustanShapeProfile)
            .dashedBorder(color = BustanDashed, radius = 24.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = BustanIcons.Plus,
                contentDescription = null,
                tint = BustanMuted,
                modifier = Modifier.size(48.dp),
            )
            Text(
                text = stringResource(R.string.main_add_child),
                style = BustanTextStyles.bodyMedium,
                color = BustanMuted,
            )
        }
    }
}

private fun avatarArtFor(avatar: String): AnimalAvatar = when (avatar.lowercase()) {
    "camel" -> AnimalAvatar.Camel
    "bird", "bee", "whale" -> AnimalAvatar.Bird
    else -> AnimalAvatar.Bear
}

private fun tierTint(stage: String): Color = when (stage) {
    "3-4" -> BustanButter
    "7-10" -> BustanBlueArt
    else -> BustanMint
}

private fun tierLabelColor(stage: String): Color = when (stage) {
    "3-4" -> BustanAmberText
    "7-10" -> BustanBlueArtDeep
    else -> BustanTealDark
}

private fun tierLabelRes(stage: String): Int = when (stage) {
    "3-4" -> R.string.tier_little_stars
    "7-10" -> R.string.tier_young_hafiz
    else -> R.string.tier_explorers
}

/** `border: 2.5px dashed #CDBF9F` from the "Add child" tile. */
private fun Modifier.dashedBorder(color: Color, radius: Dp): Modifier = drawBehind {
    val stroke = Stroke(
        width = 2.5.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()), 0f),
    )
    val inset = stroke.width / 2f
    drawRoundRect(
        color = color,
        topLeft = Offset(inset, inset),
        size = Size(size.width - stroke.width, size.height - stroke.width),
        cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
        style = stroke,
    )
}





