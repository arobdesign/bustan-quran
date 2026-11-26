package com.bustanquran.core.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerControllerTest {

    private fun makeCtrl(onPlay: (Int, Int) -> Unit = { _, _ -> }): PlayerController {
        return PlayerController(onPlayFile = onPlay)
    }

    @Test
    fun `PER_AYAH repeat=1 advances each ayah and finishes`() {
        val played = mutableListOf<Pair<Int, Int>>()
        val ctrl = makeCtrl { s, a -> played.add(s to a) }
        ctrl.load(surahId = 112, totalAyahs = 4, loopMode = LoopMode.PER_AYAH, targetRepeats = 1)
        ctrl.play()
        assertEquals(112 to 1, played.last())
        ctrl.onAyahCompleted(); assertEquals(112 to 2, played.last())
        ctrl.onAyahCompleted(); assertEquals(112 to 3, played.last())
        ctrl.onAyahCompleted(); assertEquals(112 to 4, played.last())
        ctrl.onAyahCompleted()
        assertTrue(ctrl.getState().isFinished)
    }

    @Test
    fun `PER_AYAH repeat=3 loops each ayah 3 times then advances`() {
        val played = mutableListOf<Pair<Int, Int>>()
        val ctrl = makeCtrl { s, a -> played.add(s to a) }
        ctrl.load(112, 2, LoopMode.PER_AYAH, 3)
        ctrl.play()
        repeat(2) { ctrl.onAyahCompleted() }
        assertEquals(3, ctrl.getState().currentRepeat)
        ctrl.onAyahCompleted()
        assertEquals(2, ctrl.getState().currentAyah)
        assertEquals(1, ctrl.getState().currentRepeat)
    }

    @Test
    fun `PER_SURAH repeat=2 plays whole surah twice`() {
        val ctrl = makeCtrl()
        ctrl.load(112, 4, LoopMode.PER_SURAH, 2)
        ctrl.play()
        repeat(4) { ctrl.onAyahCompleted() }
        assertEquals(1, ctrl.getState().currentAyah)
        assertEquals(2, ctrl.getState().currentRepeat)
        repeat(4) { ctrl.onAyahCompleted() }
        assertTrue(ctrl.getState().isFinished)
    }

    @Test
    fun `seekToAyah resets repeat counter`() {
        val ctrl = makeCtrl()
        ctrl.load(112, 5, LoopMode.PER_AYAH, 3)
        ctrl.play()
        ctrl.onAyahCompleted(); ctrl.onAyahCompleted()
        assertEquals(3, ctrl.getState().currentRepeat)
        ctrl.seekToAyah(3)
        assertEquals(3, ctrl.getState().currentAyah)
        assertEquals(1, ctrl.getState().currentRepeat)
    }

    @Test
    fun `speed clamped between 0_5 and 2`() {
        val ctrl = makeCtrl()
        ctrl.load(112, 4)
        ctrl.setSpeed(0.1f); assertEquals(0.5f, ctrl.getState().speed, 0.001f)
        ctrl.setSpeed(5f); assertEquals(2.0f, ctrl.getState().speed, 0.001f)
        ctrl.setSpeed(1.5f); assertEquals(1.5f, ctrl.getState().speed, 0.001f)
    }

    @Test
    fun `infinite loop PER_AYAH never finishes`() {
        val ctrl = makeCtrl()
        ctrl.load(112, 1, LoopMode.PER_AYAH, 0)
        ctrl.play()
        repeat(50) { ctrl.onAyahCompleted() }
        assertFalse(ctrl.getState().isFinished)
        assertEquals(51, ctrl.getState().currentRepeat)
    }

    @Test
    fun `displayPosition shows correct text`() {
        val ctrl = makeCtrl()
        ctrl.load(112, 4, LoopMode.PER_AYAH, 3)
        ctrl.play()
        assertEquals("Ayah 1 of 4 · Repeat 1/3", ctrl.getState().displayPosition)
    }
}
