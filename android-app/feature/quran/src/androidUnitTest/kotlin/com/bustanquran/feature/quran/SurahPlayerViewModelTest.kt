package com.bustanquran.feature.quran

import com.bustanquran.core.audio.RecitationPlayer
import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.SurahEntity
import com.bustanquran.core.data.database.model.TranslationEntity
import com.bustanquran.core.data.repository.ContentRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * The player view model is the seam between `content.db` (real ayat from the
 * eQuran.id import) and the ExoPlayer wrapper, so these tests pin down the
 * three things the screen can get wrong: which ayah is streamed next, how the
 * repeat target loops it, and what the child sees when the MP3 fails.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SurahPlayerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val contentRepository: ContentRepository = mockk()
    private lateinit var player: FakeRecitationPlayer
    private lateinit var viewModel: SurahPlayerViewModel

    private val template = SurahPlayerViewModel.DEFAULT_AUDIO_TEMPLATE

    private fun url(ayahNo: Int, surahId: Int = 106): String =
        "https://cdn.equran.id/audio-partial/Misyari-Rasyid-Al-Afasi/" +
            surahId.toString().padStart(3, '0') + ayahNo.toString().padStart(3, '0') + ".mp3"

    private fun ayah(no: Int, arabic: String) = AyahEntity(
        surahId = 106,
        ayahNo = no,
        page = 602,
        juz = 30,
        textUthmani = arabic,
        textSimple = "",
        checksum = "irrelevant",
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        player = FakeRecitationPlayer()
        viewModel = SurahPlayerViewModel(contentRepository, player)

        every { contentRepository.getSurahById(106) } returns flowOf(
            SurahEntity(
                id = 106,
                nameAr = "قريش",
                nameTr = "Quraisy",
                ayahCount = 4,
                revelation = "makki",
                juzStart = 30,
            )
        )
        every { contentRepository.getAyahs(106) } returns flowOf(
            listOf(
                ayah(1, "لِاِيْلٰفِ قُرَيْشٍۙ"),
                ayah(2, "اٖلٰفِهِمْ رِحْلَةَ الشِّتَاۤءِ وَالصَّيْفِۚ"),
                ayah(3, "فَلْيَعْبُدُوْا رَبَّ هٰذَا الْبَيْتِۙ"),
                ayah(4, "الَّذِيْٓ اَطْعَمَهُمْ مِّنْ جُوْعٍ"),
            )
        )
        every { contentRepository.getTranslation(106, "id") } returns flowOf(
            listOf(
                TranslationEntity(106, 1, "id", "Disebabkan oleh kebiasaan orang-orang Quraisy,", "equran-id"),
                TranslationEntity(106, 2, "id", "(yaitu) kebiasaan mereka bepergian", "equran-id"),
                TranslationEntity(106, 3, "id", "maka hendaklah mereka menyembah", "equran-id"),
                TranslationEntity(106, 4, "id", "yang telah memberi makanan kepada mereka", "equran-id"),
            )
        )
        coEvery { contentRepository.getMeta(SurahPlayerViewModel.AUDIO_TEMPLATE_KEY) } returns template
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    private fun loadSurah() {
        viewModel.load(106)
    }

    @Test
    fun `load exposes the arabic text and indonesian translation from content db`() = runTest {
        loadSurah()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Quraisy", state.surahName)
        assertEquals("قريش", state.surahNameAr)
        assertEquals(4, state.ayahCount)
        assertEquals("Makki", state.revelation)
        assertEquals("لِاِيْلٰفِ قُرَيْشٍۙ", state.ayahs.first().arabicText)
        assertEquals(
            "Disebabkan oleh kebiasaan orang-orang Quraisy,",
            state.ayahs.first().translation,
        )
        assertEquals(4, state.playerState.totalAyahs)
    }

    @Test
    fun `onPlay streams the first ayah through the meta template`() = runTest {
        loadSurah()
        advanceUntilIdle()

        viewModel.onPlay()

        assertEquals(listOf(url(1)), player.played)
        assertTrue(viewModel.uiState.value.playerState.isPlaying)
    }

    @Test
    fun `finishing an ayah moves to the next one when repeat is one`() = runTest {
        loadSurah()
        advanceUntilIdle()
        viewModel.onPlay()

        player.complete()

        assertEquals(url(2), player.played.last())
        assertEquals(2, viewModel.uiState.value.playerState.currentAyah)
    }

    @Test
    fun `repeat target replays the same ayah before advancing`() = runTest {
        loadSurah()
        advanceUntilIdle()
        viewModel.onRepeatChange(3)
        viewModel.onPlay()

        player.complete()
        player.complete()
        assertEquals(url(1), player.played.last())
        assertEquals(3, viewModel.uiState.value.playerState.currentRepeat)

        player.complete()
        assertEquals(url(2), player.played.last())
    }

    @Test
    fun `next and prev only move the selection while paused`() = runTest {
        loadSurah()
        advanceUntilIdle()

        viewModel.onNext()
        viewModel.onNext()
        viewModel.onPrev()

        assertTrue(player.played.isEmpty())
        assertEquals(2, viewModel.uiState.value.playerState.currentAyah)
    }

    @Test
    fun `next while playing streams the new ayah`() = runTest {
        loadSurah()
        advanceUntilIdle()
        viewModel.onPlay()

        viewModel.onNext()

        assertEquals(url(2), player.played.last())
    }

    @Test
    fun `pause pauses the engine and clears the playing flag`() = runTest {
        loadSurah()
        advanceUntilIdle()
        viewModel.onPlay()

        viewModel.onPause()

        assertTrue(player.paused)
        assertFalse(viewModel.uiState.value.playerState.isPlaying)
    }

    @Test
    fun `play after pause resumes the same file instead of restarting it`() = runTest {
        loadSurah()
        advanceUntilIdle()
        viewModel.onPlay()
        viewModel.onPause()

        viewModel.onPlay()

        assertEquals(1, player.resumedCount)
        assertEquals(listOf(url(1)), player.played)
        assertTrue(viewModel.uiState.value.playerState.isPlaying)
    }

    @Test
    fun `speed change is applied to the engine immediately`() = runTest {
        loadSurah()
        advanceUntilIdle()
        viewModel.onPlay()

        viewModel.onSpeedChange(1.5f)

        assertEquals(1.5f, player.lastSpeed, 0.001f)
        assertEquals(1.5f, viewModel.uiState.value.playerState.speed, 0.001f)
    }

    @Test
    fun `streaming failure surfaces a retryable error`() = runTest {
        loadSurah()
        advanceUntilIdle()
        viewModel.onPlay()

        player.fail("ERROR_CODE_IO_NETWORK_CONNECTION_FAILED")

        assertNotNull(viewModel.uiState.value.audioError)
        assertFalse(viewModel.uiState.value.playerState.isPlaying)

        viewModel.onRetryAudio()
        assertNull(viewModel.uiState.value.audioError)
        assertEquals(url(1), player.played.last())
    }

    @Test
    fun `audio url pads surah and ayah to three digits`() {
        assertEquals(
            "https://cdn.equran.id/audio-partial/Misyari-Rasyid-Al-Afasi/001007.mp3",
            SurahPlayerViewModel.audioUrl(template, surahId = 1, ayahNo = 7),
        )
    }

    /** Stands in for ExoPlayer; records the URLs and fires callbacks on demand. */
    private class FakeRecitationPlayer : RecitationPlayer {
        val played = mutableListOf<String>()
        var lastSpeed: Float = 1.0f
        var paused: Boolean = false
        var resumedCount: Int = 0

        override var onCompleted: (() -> Unit)? = null
        override var onError: ((String) -> Unit)? = null

        override fun play(url: String, speed: Float) {
            played += url
            lastSpeed = speed
            paused = false
        }

        override fun pause() {
            paused = true
        }

        override fun resume() {
            resumedCount++
            paused = false
        }

        override fun setSpeed(speed: Float) {
            lastSpeed = speed
        }

        override fun stop() = Unit

        override fun release() = Unit

        fun complete() {
            onCompleted?.invoke()
        }

        fun fail(reason: String) {
            onError?.invoke(reason)
        }
    }
}

