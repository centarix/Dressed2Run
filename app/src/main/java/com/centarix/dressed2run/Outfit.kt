package com.centarix.dressed2run

import androidx.annotation.ColorInt
import androidx.annotation.StringRes

enum class BodyRegion(@StringRes val label: Int) {
    HEAD(R.string.region_head),
    ARM(R.string.region_arm),
    LOWER(R.string.region_lower),
    FEET(R.string.region_feet),
}

/** Declared in draw order, innermost first; waistbands come before the jacket so it covers them. */
enum class ClothingLayer(val region: BodyRegion, @StringRes val label: Int, @ColorInt val color: Int) {
    BEANIE(BodyRegion.HEAD, R.string.layer_beanie, 0xFF8E24AA.toInt()),
    BASE_LAYER(BodyRegion.ARM, R.string.layer_base_layer, 0xFF1E88E5.toInt()),
    TIGHTS(BodyRegion.LOWER, R.string.layer_tights, 0xFF212121.toInt()),
    SHORTS(BodyRegion.LOWER, R.string.layer_shorts, 0xFF43A047.toInt()),
    JACKET(BodyRegion.ARM, R.string.layer_jacket, 0xFFF4511E.toInt()),
    GLOVES(BodyRegion.ARM, R.string.layer_gloves, 0xFF37474F.toInt()),
    SOCKS(BodyRegion.FEET, R.string.layer_socks, 0xFFFAFAFA.toInt()),
    SHOES(BodyRegion.FEET, R.string.layer_shoes, 0xFFFFB300.toInt()),
}

data class Outfit(val layers: Set<ClothingLayer> = emptySet()) {

    fun isWearing(layer: ClothingLayer) = layer in layers

    fun with(layer: ClothingLayer, worn: Boolean) = Outfit(if (worn) layers + layer else layers - layer)

    /** Worn layers in draw order (innermost first). */
    fun orderedLayers(): List<ClothingLayer> = ClothingLayer.entries.filter { it in layers }

    fun layersFor(region: BodyRegion): List<ClothingLayer> = orderedLayers().filter { it.region == region }

    companion object {
        val COLD_WEATHER = Outfit(ClothingLayer.entries.toSet() - ClothingLayer.SHORTS)
    }
}
