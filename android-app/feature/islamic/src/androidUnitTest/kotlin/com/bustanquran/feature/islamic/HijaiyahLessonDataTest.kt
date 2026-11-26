package com.bustanquran.feature.islamic

import com.bustanquran.core.data.database.model.LetterEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class HijaiyahLessonDataTest {

    private fun letter(formsJson: String = "{}") = LetterEntity(
        id = 3,
        char = "ت",
        name = "Ta",
        formsJson = formsJson,
        makhrajNote = "Ujung lidah",
        exampleWord = "تمر",
    )

    @Test
    fun `forms json supplies all four shapes when present`() {
        val forms = parseLetterForms(
            formsJson = """{"isolated":"ت","initial":"تـ","medial":"ـتـ","final":"ـت"}""",
            glyph = "ت",
        )

        assertEquals(listOf("ت", "تـ", "ـتـ", "ـت"), forms.tiles)
    }

    @Test
    fun `empty json falls back to the tatweel joined shapes`() {
        val forms = parseLetterForms(formsJson = "{}", glyph = "ت")

        assertEquals("ت", forms.isolated)
        assertEquals("تـ", forms.initial)
        assertEquals("ـتـ", forms.medial)
        assertEquals("ـت", forms.finalForm)
    }

    @Test
    fun `right joining letters get no forward stroke`() {
        val alif = parseLetterForms(formsJson = "{}", glyph = "ا")

        assertEquals(listOf("ا", "ا", "ـا", "ـا"), alif.tiles)
    }

    @Test
    fun `malformed json still yields the four tiles`() {
        val forms = parseLetterForms(formsJson = "not json", glyph = "ت")

        assertEquals(4, forms.tiles.size)
        assertEquals("ت", forms.tiles.first())
    }

    @Test
    fun `entity maps to a lesson letter in design order`() {
        val letter = letter().toHijaiyahLetter()

        assertEquals(3, letter.id)
        assertEquals("ت", letter.glyph)
        assertEquals("Ta", letter.name)
        assertEquals("Ujung lidah", letter.makhrajNote)
        assertEquals("تمر", letter.exampleWord)
        assertEquals("ت", letter.forms.tiles.first())
    }

    @Test
    fun `lesson data counts its letters`() {
        val data = HijaiyahLessonData(letters = listOf(letter().toHijaiyahLetter()))

        assertEquals(1, data.total)
        assertEquals(false, data.isEmpty)
        assertEquals(true, EmptyHijaiyahLessonData.isEmpty)
    }
}
