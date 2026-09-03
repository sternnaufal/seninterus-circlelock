package com.seninterus.circlelock

import com.seninterus.circlelock.model.AnimationType
import com.seninterus.circlelock.model.GameMode
import com.seninterus.circlelock.model.SkinType
import org.junit.Assert.*
import org.junit.Test

class GameModelsTest {

    @Test
    fun `GameMode has 6 modes`() {
        assertEquals(6, GameMode.entries.size)
    }

    @Test
    fun `GameMode getDisplayName replaces underscore with space`() {
        assertEquals("TIME ATTACK", GameMode.TIME_ATTACK.getDisplayName())
        assertEquals("STANDARD", GameMode.STANDARD.getDisplayName())
    }

    @Test
    fun `GameMode descriptions are not empty`() {
        GameMode.entries.forEach { mode ->
            assertTrue("Description for ${mode.name} should not be empty", mode.description.isNotEmpty())
        }
    }

    @Test
    fun `SkinType DEFAULT has zero cost`() {
        assertEquals(0, SkinType.DEFAULT.cost)
    }

    @Test
    fun `SkinType costs are non-negative`() {
        SkinType.entries.forEach { skin ->
            assertTrue("Cost for ${skin.name} should be non-negative", skin.cost >= 0)
        }
    }

    @Test
    fun `SkinType colors are not empty`() {
        SkinType.entries.forEach { skin ->
            assertTrue("Colors for ${skin.name} should not be empty", skin.colors.isNotEmpty())
        }
    }

    @Test
    fun `SkinType has 12 skins`() {
        assertEquals(12, SkinType.entries.size)
    }

    @Test
    fun `AnimationType CLASSIC has zero cost`() {
        assertEquals(0, AnimationType.CLASSIC.cost)
    }

    @Test
    fun `AnimationType costs are non-negative`() {
        AnimationType.entries.forEach { anim ->
            assertTrue("Cost for ${anim.name} should be non-negative", anim.cost >= 0)
        }
    }

    @Test
    fun `AnimationType has 8 types`() {
        assertEquals(8, AnimationType.entries.size)
    }

    @Test
    fun `AnimationType displayNames are not empty`() {
        AnimationType.entries.forEach { anim ->
            assertTrue("DisplayName for ${anim.name} should not be empty", anim.displayName.isNotEmpty())
            assertTrue("Description for ${anim.name} should not be empty", anim.description.isNotEmpty())
        }
    }

    @Test
    fun `all SkinType displayNames are not empty`() {
        SkinType.entries.forEach { skin ->
            assertTrue("DisplayName for ${skin.name} should not be empty", skin.displayName.isNotEmpty())
        }
    }
}
