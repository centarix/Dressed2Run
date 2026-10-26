package com.centarix.dressed2run

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RunnerAvatarActivity : AppCompatActivity() {

    private lateinit var avatar: RunnerAvatarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_runner_avatar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.avatar_root)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        avatar = findViewById(R.id.runner_avatar)
        avatar.outfit = savedInstanceState?.getStringArrayList(KEY_LAYERS)
            ?.let { names -> Outfit(names.map { ClothingLayer.valueOf(it) }.toSet()) }
            ?: Outfit.COLD_WEATHER

        val toggles: LinearLayout = findViewById(R.id.layer_toggles)
        for (region in BodyRegion.entries) {
            toggles.addView(TextView(this, null, 0, R.style.RegionHeader).apply { setText(region.label) })
            ClothingLayer.entries.filter { it.region == region }.forEach { layer ->
                toggles.addView(CheckBox(this).apply {
                    setText(layer.label)
                    isChecked = avatar.outfit.isWearing(layer)
                    setOnCheckedChangeListener { _, checked -> avatar.outfit = avatar.outfit.with(layer, checked) }
                })
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(KEY_LAYERS, ArrayList(avatar.outfit.orderedLayers().map { it.name }))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private companion object {
        const val KEY_LAYERS = "worn_layers"
    }
}
