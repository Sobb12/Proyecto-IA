package com.example

import com.example.data.SproutRepository
import com.example.data.local.PlantProfileEntity
import com.example.model.PlantMood
import com.example.model.PlantType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class HydrationStateTest {

    @Test
    fun hydrationDecay_transitionsThroughHealthyThirstyCriticalAndGrowing() {
        val baseTime = 1_700_000_000_000L
        val fernProfile = PlantProfileEntity(
            plantName = "Fernie",
            plantTypeName = PlantType.FERN.name, // 72h interval
            lastWateredTimestamp = baseTime
        )

        // Freshly watered (100% -> HEALTHY)
        val fresh = SproutRepository.computeHydrationMetrics(fernProfile, nowMillis = baseTime)
        assertEquals(100f, fresh.hydrationPercent, 0.1f)
        assertEquals(PlantMood.HEALTHY, fresh.mood)

        // 36h elapsed -> 50% hydration -> THIRSTY (30% - 60%)
        val thirstyProfile = fernProfile.copy(virtualOffsetMillis = TimeUnit.HOURS.toMillis(36))
        val thirsty = SproutRepository.computeHydrationMetrics(thirstyProfile, nowMillis = baseTime)
        assertEquals(50f, thirsty.hydrationPercent, 0.1f)
        assertEquals(PlantMood.THIRSTY, thirsty.mood)

        // 60h elapsed -> ~16.7% hydration -> CRITICAL (<30%)
        val criticalProfile = fernProfile.copy(virtualOffsetMillis = TimeUnit.HOURS.toMillis(60))
        val critical = SproutRepository.computeHydrationMetrics(criticalProfile, nowMillis = baseTime)
        assertTrue(critical.hydrationPercent < 30f)
        assertEquals(PlantMood.CRITICAL, critical.mood)

        // Growing animation overrides mood right after watering
        val growing = SproutRepository.computeHydrationMetrics(
            fernProfile,
            nowMillis = baseTime,
            isGrowingAnimation = true
        )
        assertEquals(PlantMood.GROWING, growing.mood)
    }
}
