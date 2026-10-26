package com.centarix.dressed2run

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withSave
import kotlin.math.min

/** Draws a running figure with each worn [ClothingLayer] painted over the body, back to front. */
class RunnerAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    var outfit: Outfit = Outfit()
        set(value) {
            field = value
            contentDescription = describe(value)
            invalidate()
        }

    private enum class Part { BACK_ARM, BACK_LEG, TORSO, HEAD, FRONT_LEG, FRONT_ARM }

    private class Limb(val joint: PointF, val middle: PointF, val end: PointF, val toe: PointF? = null)

    private val head = PointF(112f, 40f)
    private val neck = PointF(106f, 64f)
    private val shoulder = PointF(104f, 76f)
    private val hip = PointF(96f, 146f)
    private val frontArm = Limb(shoulder, PointF(132f, 108f), PointF(154f, 84f))
    private val backArm = Limb(shoulder, PointF(80f, 110f), PointF(62f, 136f))
    private val frontLeg = Limb(hip, PointF(132f, 186f), PointF(122f, 238f), PointF(146f, 242f))
    private val backLeg = Limb(hip, PointF(78f, 196f), PointF(46f, 222f), PointF(56f, 244f))

    private val skinColor = 0xFFE0AC69.toInt()
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

    init {
        contentDescription = describe(outfit)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        if (availableWidth <= 0 || availableHeight <= 0) return
        val factor = min(availableWidth / BOX_WIDTH, availableHeight / BOX_HEIGHT)
        canvas.withSave {
            translate(
                paddingLeft + (availableWidth - BOX_WIDTH * factor) / 2f,
                paddingTop + (availableHeight - BOX_HEIGHT * factor) / 2f,
            )
            scale(factor, factor)
            val layers = outfit.orderedLayers()
            for (part in Part.entries) {
                drawSkin(this, part)
                layers.forEach { drawLayer(this, part, it) }
            }
        }
    }

    private fun drawSkin(canvas: Canvas, part: Part) {
        when (part) {
            Part.HEAD -> {
                segment(canvas, neck, shoulder, 14f, skinColor)
                circle(canvas, head, 20f, skinColor)
            }
            Part.TORSO -> segment(canvas, shoulder, hip, 38f, skinColor)
            Part.FRONT_ARM, Part.BACK_ARM -> arm(part).let {
                limb(canvas, it, 12f, skinColor)
                circle(canvas, it.end, 7f, skinColor)
            }
            Part.FRONT_LEG, Part.BACK_LEG -> leg(part).let {
                limb(canvas, it, 15f, skinColor)
                segment(canvas, it.end, it.toe!!, 10f, skinColor)
            }
        }
    }

    private fun drawLayer(canvas: Canvas, part: Part, layer: ClothingLayer) {
        val color = layer.color
        when (layer) {
            ClothingLayer.BEANIE -> if (part == Part.HEAD) {
                fill.color = color
                canvas.drawArc(RectF(head.x - 22f, head.y - 23f, head.x + 22f, head.y + 21f), 180f, 180f, true, fill)
                segment(canvas, PointF(head.x - 21f, head.y - 2f), PointF(head.x + 21f, head.y - 2f), 7f, darken(color))
                circle(canvas, PointF(head.x, head.y - 25f), 5f, darken(color))
            }
            ClothingLayer.BASE_LAYER -> when (part) {
                Part.TORSO -> segment(canvas, shoulder, hip, 42f, color)
                Part.FRONT_ARM, Part.BACK_ARM -> limb(canvas, arm(part), 16f, color)
                else -> Unit
            }
            ClothingLayer.JACKET -> when (part) {
                Part.TORSO -> {
                    segment(canvas, PointF(shoulder.x, shoulder.y - 2f), PointF(hip.x, hip.y + 4f), 50f, color)
                    segment(canvas, PointF(shoulder.x - 2f, shoulder.y - 8f), PointF(hip.x + 2f, hip.y + 18f), 2f, darken(color))
                }
                Part.FRONT_ARM, Part.BACK_ARM -> arm(part).let {
                    segment(canvas, it.joint, it.middle, 22f, color)
                    segment(canvas, it.middle, lerp(it.middle, it.end, 0.75f), 21f, color)
                }
                else -> Unit
            }
            ClothingLayer.GLOVES -> if (part == Part.FRONT_ARM || part == Part.BACK_ARM) {
                circle(canvas, arm(part).end, 9.5f, color)
            }
            ClothingLayer.TIGHTS -> when (part) {
                Part.TORSO -> segment(canvas, PointF(hip.x + 2f, hip.y - 10f), hip, 40f, color)
                Part.FRONT_LEG, Part.BACK_LEG -> limb(canvas, leg(part), 19f, color)
                else -> Unit
            }
            ClothingLayer.SHORTS -> when (part) {
                Part.TORSO -> segment(canvas, PointF(hip.x + 2f, hip.y - 12f), PointF(hip.x, hip.y + 2f), 46f, color)
                Part.FRONT_LEG, Part.BACK_LEG -> leg(part).let { segment(canvas, it.joint, lerp(it.joint, it.middle, 0.55f), 26f, color) }
                else -> Unit
            }
            ClothingLayer.SOCKS -> if (part == Part.FRONT_LEG || part == Part.BACK_LEG) {
                leg(part).let {
                    segment(canvas, lerp(it.middle, it.end, 0.7f), it.end, 18f, color)
                    segment(canvas, it.end, it.toe!!, 13f, color)
                }
            }
            ClothingLayer.SHOES -> if (part == Part.FRONT_LEG || part == Part.BACK_LEG) {
                leg(part).let {
                    segment(canvas, lerp(it.toe!!, it.end, 1.2f), it.toe, 18f, color)
                    segment(canvas, lerp(it.toe, it.end, 1.25f), lerp(it.end, it.toe, 1.05f), 5f, darken(color))
                }
            }
        }
    }

    private fun arm(part: Part) = if (part == Part.FRONT_ARM) frontArm else backArm

    private fun leg(part: Part) = if (part == Part.FRONT_LEG) frontLeg else backLeg

    private fun limb(canvas: Canvas, limb: Limb, width: Float, color: Int) {
        segment(canvas, limb.joint, limb.middle, width, color)
        segment(canvas, limb.middle, limb.end, width, color)
    }

    private fun segment(canvas: Canvas, from: PointF, to: PointF, width: Float, color: Int) {
        stroke.color = color
        stroke.strokeWidth = width
        canvas.drawLine(from.x, from.y, to.x, to.y, stroke)
    }

    private fun circle(canvas: Canvas, center: PointF, radius: Float, color: Int) {
        fill.color = color
        canvas.drawCircle(center.x, center.y, radius, fill)
    }

    private fun describe(outfit: Outfit): String {
        val worn = outfit.orderedLayers().joinToString { context.getString(it.label) }
        return if (worn.isEmpty()) context.getString(R.string.avatar_description_none)
        else context.getString(R.string.avatar_description, worn)
    }

    private companion object {
        const val BOX_WIDTH = 200f
        const val BOX_HEIGHT = 260f

        fun lerp(a: PointF, b: PointF, t: Float) = PointF(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t)

        fun darken(color: Int): Int {
            val r = (color shr 16 and 0xFF) * 3 / 4
            val g = (color shr 8 and 0xFF) * 3 / 4
            val b = (color and 0xFF) * 3 / 4
            return (color and 0xFF000000.toInt()) or (r shl 16) or (g shl 8) or b
        }
    }
}
