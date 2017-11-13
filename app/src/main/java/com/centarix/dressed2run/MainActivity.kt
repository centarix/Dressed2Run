package com.centarix.dressed2run

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView: ListView = findViewById(R.id.listview)
        listView.adapter = CustomAdapter(this, generateFakeValues())

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
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?) : View {
            val rowView = LayoutInflater.from(context).inflate(R.layout.list_item_view, parent, false)
            val textView: TextView = rowView.findViewById(R.id.text_list_element)
            textView.text = values[position]
            return rowView
        }
    }
}
