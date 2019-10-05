package com.example.wikipediaspeech

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val WIKIPEDIA_NAME_DEFAULT = "Special:Randompage"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setClickLisnter()
    }

    private fun setClickLisnter() {
        submit.setOnClickListener { view ->
            val intent = Intent(this, PageActivity::class.java).apply {
                putExtra(PageActivity.EXTRA_PAGE_NAME, inputText())
            }
            startActivity(intent)
        }
    }

    private fun inputText(): String {
        input_title.selectAll()
        val input = input_title.getText().toString()
        return if (input.length > 0) input else WIKIPEDIA_NAME_DEFAULT
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
