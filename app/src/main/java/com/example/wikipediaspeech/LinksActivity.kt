package com.example.wikipediaspeech

import kotlinx.android.synthetic.main.activity_links.*

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

import kotlinx.android.synthetic.main.activity_links.toolbar

class LinksActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var links: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        val title = intent.getStringExtra(PageActivity.EXTRA_PAGE_NAME)
        setToolbar(title!!)

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        links = intent.getStringArrayListExtra(PageActivity.EXTRA_PAGE_LINK_LIST)
        links.setListView()
    }

    private fun ArrayList<String>.setListView() {
        listView = findViewById(R.id.link_list_view)
        val adapter = ArrayAdapter(this@LinksActivity, R.layout.line_item, this)
        listView.adapter = adapter
        val context = this@LinksActivity
        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapter: AdapterView<*>, view: View, position: Int, itemId: Long) {
                val intent = Intent(context, PageActivity::class.java).apply {
                    putExtra(PageActivity.EXTRA_PAGE_NAME, links.get(position))
                }
                startActivity(intent)
            }
        })
    }

    private fun setToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setTitle(title)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item!!)
    }
}
