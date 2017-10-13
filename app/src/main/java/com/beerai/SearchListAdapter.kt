package com.beerai

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_beer.view.*


class SearchListAdapter : RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {

    var beerList: MutableList<Beer> = ArrayList()

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(beerList[position].name)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_beer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return beerList.size
    }

    fun update(updatedList: List<Beer>) {
        beerList.clear()
        beerList.addAll(updatedList)
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(beerName: String) {
            view.beer_name_text_view.text = beerName
        }
    }
}