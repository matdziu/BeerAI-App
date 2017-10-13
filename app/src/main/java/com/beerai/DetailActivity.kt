package com.beerai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    lateinit var beer: Beer

    companion object {

        val BEER_DATA_KEY = "beer-data-key"

        @JvmStatic
        fun start(context: Context, beer: Beer) {
            val startIntent = Intent(context, DetailActivity::class.java)
            startIntent.putExtra(BEER_DATA_KEY, beer)
            context.startActivity(startIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        beer = intent.getSerializableExtra(BEER_DATA_KEY) as Beer

        beer_name_text_view.text = beer.name
        beer_description_text_view.text = beer.description
    }
}