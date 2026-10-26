package com.centarix.dressed2run

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OutfitTest {
    @Test
    fun everyRegionHasAtLeastOneLayer() {
        BodyRegion.entries.forEach { region ->
            assertTrue(region.name, ClothingLayer.entries.any { it.region == region })
        }
    }

    @Test
    fun layersAreOrderedInnermostFirstRegardlessOfInsertionOrder() {
        val outfit = Outfit().with(ClothingLayer.JACKET, true).with(ClothingLayer.BASE_LAYER, true)
        assertEquals(listOf(ClothingLayer.BASE_LAYER, ClothingLayer.JACKET), outfit.layersFor(BodyRegion.ARM))
    }

    @Test
    fun removingALayerLeavesTheOthers() {
        val outfit = Outfit.COLD_WEATHER.with(ClothingLayer.JACKET, false)
        assertFalse(outfit.isWearing(ClothingLayer.JACKET))
        assertTrue(outfit.isWearing(ClothingLayer.BASE_LAYER))
        assertFalse(outfit.isWearing(ClothingLayer.SHORTS))
    }
}
