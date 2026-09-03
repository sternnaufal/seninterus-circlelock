package com.seninterus.circlelock

import com.seninterus.circlelock.engine.LevelGenerator
import com.seninterus.circlelock.model.AnimationType
import com.seninterus.circlelock.model.GameMode
import com.seninterus.circlelock.model.SkinType
import org.junit.Assert.*
import org.junit.Test

class LevelGeneratorTest {

    @Test
    fun `level 1-3 generates 2 circles`() {
        for (level in 1..3) {
            val circles = LevelGenerator.generateLevelCircles(level)
            assertEquals("Level $level should have 2 circles", 2, circles.size)
        }
    }

    @Test
    fun `level 4-8 generates 3 circles`() {
        for (level in 4..8) {
            val circles = LevelGenerator.generateLevelCircles(level)
            assertEquals("Level $level should have 3 circles", 3, circles.size)
        }
    }

    @Test
    fun `level 9-15 generates 4 circles`() {
        for (level in 9..15) {
            val circles = LevelGenerator.generateLevelCircles(level)
            assertEquals("Level $level should have 4 circles", 4, circles.size)
        }
    }

    @Test
    fun `level 16+ generates 5 circles`() {
        for (level in 16..20) {
            val circles = LevelGenerator.generateLevelCircles(level)
            assertEquals("Level $level should have 5 circles", 5, circles.size)
        }
    }

    @Test
    fun `circles have unique ids`() {
        val circles = LevelGenerator.generateLevelCircles(10)
        val ids = circles.map { it.id }.toSet()
        assertEquals("All circle IDs should be unique", circles.size, ids.size)
    }

    @Test
    fun `circles have increasing radii`() {
        val circles = LevelGenerator.generateLevelCircles(10)
        for (i in 1 until circles.size) {
            assertTrue("Circle $i radius should be larger than circle ${i-1}",
                circles[i].radius > circles[i-1].radius)
        }
    }

    @Test
    fun `LINKED mode sets linkedCircleId`() {
        val circles = LevelGenerator.generateLevelCircles(10, GameMode.LINKED)
        val firstCircle = circles.first()
        assertNotNull("First circle should have linkedCircleId in LINKED mode", firstCircle.linkedCircleId)
    }

    @Test
    fun `non-LINKED mode has null linkedCircleId`() {
        val circles = LevelGenerator.generateLevelCircles(10, GameMode.STANDARD)
        circles.forEach { circle ->
            assertNull("Circle should not have linkedCircleId in STANDARD mode", circle.linkedCircleId)
        }
    }

    @Test
    fun `rotationSpeed decreases with level`() {
        val lowLevelCircles = LevelGenerator.generateLevelCircles(1)
        val highLevelCircles = LevelGenerator.generateLevelCircles(20)
        assertTrue("High level should have lower rotation speed",
            highLevelCircles.first().rotationSpeed <= lowLevelCircles.first().rotationSpeed)
    }

    @Test
    fun `rotationSpeed never below 0_4`() {
        for (level in 1..50) {
            val circles = LevelGenerator.generateLevelCircles(level)
            circles.forEach { circle ->
                assertTrue("Rotation speed should be at least 0.4, got ${circle.rotationSpeed}",
                    circle.rotationSpeed >= 0.4f)
            }
        }
    }
}
