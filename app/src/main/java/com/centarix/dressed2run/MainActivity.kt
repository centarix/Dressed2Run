package com.centarix.dressed2run

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val listView: ListView = findViewById(R.id.listview)
        listView.adapter = CustomAdapter(this, generateFakeValues())

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_runner_avatar -> {
            startActivity(Intent(this, RunnerAvatarActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun generateFakeValues() : List<String> {
        val data = ArrayList<String>()
        for(i in 0..100) {
            data.add("$i element")
        }
        return data
    }

    class CustomAdapter(context: Context, private var values: List<String>) :
            ArrayAdapter<String>(context, R.layout.list_item_view, values) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup) : View {
            val rowView = LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false)
            val textView: TextView = rowView.findViewById(R.id.text_list_element)
            textView.text = values[position]
            return rowView
        }
    }
}
