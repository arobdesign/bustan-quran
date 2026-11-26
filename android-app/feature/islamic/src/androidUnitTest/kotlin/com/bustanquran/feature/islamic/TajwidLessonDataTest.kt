package com.bustanquran.feature.islamic

import com.bustanquran.core.data.database.model.AyahEntity
import com.bustanquran.core.data.database.model.TajwidRuleEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TajwidLessonDataTest {

    private val rules = listOf(
        rule(id = 1, name = "Ghunnah", key = "tajwid_ghunnah"),
        rule(id = 7, name = "Qalqalah", key = "tajwid_qalqalah"),
        rule(id = 8, name = "Madd Wajib/Jaiz", key = "tajwid_madd"),
    )

    private val ayahs = listOf(
        ayah(no = 2, text = "اللَّهُ الصَّمَدُ"),
        ayah(no = 1, text = "قُلْ هُوَ اللَّهُ أَحَدٌ"),
    )

    private fun rule(id: Int, name: String, key: String) = TajwidRuleEntity(
        id = id,
        name = name,
        colorHex = "#E74C3C",
        descriptionKey = key,
    )

    private fun ayah(no: Int, text: String) = AyahEntity(
        surahId = TajwidExampleSurahId,
        ayahNo = no,
        page = 604,
        juz = 30,
        textUthmani = text,
        textSimple = text,
        checksum = "sha256",
    )

    @Test
    fun `qalqalah is chosen with its position in the rule list`() {
        val state = buildTajwidLesson(
            rules = rules,
            exampleSurahName = "Al-Ikhlas",
            exampleAyahs = ayahs,
            contentLetters = listOf("ا", "ب", "ت"),
            ruleLetters = QalqalahLetters,
        )

        assertEquals("Qalqalah", state.ruleName)
        assertEquals(2, state.rulePosition)
        assertEquals(3, state.ruleTotal)
        assertEquals("tajwid_qalqalah", state.descriptionKey)
        assertEquals(QalqalahLetters, state.letters)
    }

    @Test
    fun `first ayah of the example surah is used`() {
        val state = buildTajwidLesson(
            rules = rules,
            exampleSurahName = "Al-Ikhlas",
            exampleAyahs = ayahs,
            contentLetters = emptyList(),
            ruleLetters = QalqalahLetters,
        )

        assertTrue(state.hasExample)
        assertEquals(1, state.exampleAyahNo)
        assertEquals("قُلْ هُوَ اللَّهُ أَحَدٌ", state.exampleAyah)
        assertEquals("Al-Ikhlas", state.exampleSurahName)
    }

    @Test
    fun `quick check puts the right letter between two distractors`() {
        val state = buildTajwidLesson(
            rules = rules,
            exampleSurahName = "Al-Ikhlas",
            exampleAyahs = ayahs,
            contentLetters = listOf("ا", "ق", "ت", "س"),
            ruleLetters = QalqalahLetters,
        )

        assertEquals(3, state.options.size)
        assertEquals("ا", state.options[0])
        assertEquals("ط", state.options[state.correctIndex])
        assertEquals("ت", state.options[2])
    }

    @Test
    fun `missing letters shrink the row without moving the correct index`() {
        val (options, correctIndex) = buildTajwidCheckOptions(
            contentLetters = emptyList(),
            ruleLetters = QalqalahLetters,
        )

        assertEquals(listOf("ط"), options)
        assertEquals(0, correctIndex)
    }

    @Test
    fun `no rules yields the empty state`() {
        val state = buildTajwidLesson(
            rules = emptyList(),
            exampleSurahName = "",
            exampleAyahs = emptyList(),
            contentLetters = emptyList(),
            ruleLetters = QalqalahLetters,
        )

        assertEquals(EmptyTajwidLessonState, state)
        assertTrue(state.isEmpty)
        assertEquals(false, state.hasOptions)
    }

    @Test
    fun `unknown rule set still renders its first rule`() {
        val state = buildTajwidLesson(
            rules = listOf(rule(id = 1, name = "Ghunnah", key = "tajwid_ghunnah")),
            exampleSurahName = "",
            exampleAyahs = emptyList(),
            contentLetters = emptyList(),
            ruleLetters = QalqalahLetters,
        )

        assertEquals("Ghunnah", state.ruleName)
        assertEquals(1, state.rulePosition)
        assertEquals(false, state.hasExample)
    }
}
