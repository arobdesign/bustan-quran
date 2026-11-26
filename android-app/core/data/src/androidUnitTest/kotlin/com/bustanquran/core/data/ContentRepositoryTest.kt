package com.bustanquran.core.data

import com.bustanquran.core.data.database.dao.AyahDao
import com.bustanquran.core.data.database.dao.GameContentDao
import com.bustanquran.core.data.database.dao.LetterDao
import com.bustanquran.core.data.database.dao.MetaDao
import com.bustanquran.core.data.database.dao.SurahDao
import com.bustanquran.core.data.database.dao.TajwidDao
import com.bustanquran.core.data.database.dao.TranslationDao
import com.bustanquran.core.data.database.dao.WordDao
import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.GameContentEntity
import com.bustanquran.core.data.database.model.SurahEntity
import com.bustanquran.core.data.repository.ContentRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ContentRepositoryTest {

    private val surahDao: SurahDao = mockk()
    private val ayahDao: AyahDao = mockk()
    private val wordDao: WordDao = mockk()
    private val translationDao: TranslationDao = mockk()
    private val letterDao: LetterDao = mockk()
    private val tajwidDao: TajwidDao = mockk()
    private val gameContentDao: GameContentDao = mockk()
    private val metaDao: MetaDao = mockk()

    private lateinit var repository: ContentRepositoryImpl

    @Before
    fun setUp() {
        repository = ContentRepositoryImpl(
            surahDao = surahDao,
            ayahDao = ayahDao,
            wordDao = wordDao,
            translationDao = translationDao,
            letterDao = letterDao,
            tajwidDao = tajwidDao,
            gameContentDao = gameContentDao,
            metaDao = metaDao
        )
    }

    @Test
    fun testGetSurahListReturnsSurahs() = runTest {
        val sampleSurahs = listOf(
            SurahEntity(106, "قريش", "Quraysh", 4, "makki", 30),
            SurahEntity(107, "الماعون", "Al-Maun", 7, "makki", 30)
        )
        every { surahDao.getAllSurahs() } returns flowOf(sampleSurahs)

        val result = repository.getSurahList().first()
        assertEquals(2, result.size)
        assertEquals("Quraysh", result[0].nameTr)
        assertEquals(106, result[0].id)
    }

    @Test
    fun testVerifyAyahChecksumValidatesAccurately() {
        val validText = "لِاِيْلٰفِ قُرَيْشٍۙ"
        val expectedSha = repository.calculateSha256(validText)

        val validAyah = AyahEntity(
            surahId = 106,
            ayahNo = 1,
            page = 602,
            juz = 30,
            textUthmani = validText,
            textSimple = validText,
            checksum = expectedSha
        )

        // Valid hash must pass
        assertTrue(repository.verifyAyahChecksum(validAyah))

        // Altered text must FAIL checksum validation (NFR-11)
        val tamperedAyah = validAyah.copy(textUthmani = "لِاِيْلٰفِ قُرَيْشٍ")
        assertFalse(repository.verifyAyahChecksum(tamperedAyah))
    }

    @Test
    fun testGetGameItemsReturnsCorrectItems() = runTest {
        val sampleItems = listOf(
            GameContentEntity("balon_huruf", "item_1", "3-4", "{\"target\":\"alif\"}")
        )
        every { gameContentDao.getGameItems("balon_huruf", "3-4") } returns flowOf(sampleItems)

        val items = repository.getGameItems("balon_huruf", "3-4").first()
        assertEquals(1, items.size)
        assertEquals("item_1", items[0].itemId)
    }
}
