package com.beerai

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar.*


class SearchActivity : AppCompatActivity() {

    val searchListAdapter = SearchListAdapter()
    val beerDatabaseRepository = BeerDatabaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        search_recycler_view.layoutManager = LinearLayoutManager(this)
        search_recycler_view.adapter = searchListAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search_item)
        val searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)
        searchItem.expandActionView()

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                finish()
                return false
            }
        })

        @Suppress("UNCHECKED_CAST")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && !query.isEmpty()) {
                    showProgressBar(true)
                    showSearchInfoText(false)
                    beerDatabaseRepository.searchForBeers(query)
                            .subscribe({
                                beerList ->
                                searchListAdapter.update(beerList as List<Beer>)
                                showProgressBar(false)
                                if (beerList.isEmpty()) {
                                    showSearchInfoText(true)
                                }
                            })
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }
        })

        return true
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            search_recycler_view.visibility = View.GONE
            progress_bar.visibility = View.VISIBLE
        } else {
            search_recycler_view.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
        }
    }

    private fun showSearchInfoText(show: Boolean) {
        if (show) {
            search_info_text_view.visibility = View.VISIBLE
        } else {
            search_info_text_view.visibility = View.GONE
        }
    }
}