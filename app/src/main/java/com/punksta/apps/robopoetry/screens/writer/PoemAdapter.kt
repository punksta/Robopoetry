package com.punksta.apps.robopoetry.screens.writer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.EntityItem
import com.punksta.apps.robopoetry.entity.Poem

/**
 * Created by stanislav on 1/2/17.
 */


class PoemViewHolder(view: View, val name: TextView, val year: TextView, val cutTextView: TextView) : RecyclerView.ViewHolder(view)

class PoemAdapter(val items: MutableList<EntityItem>, val poemListener: (EntityItem) -> Unit) : RecyclerView.Adapter<PoemViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: PoemViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is Poem -> {
                holder.name.text = item.name
                holder.year.text = item.year
                holder.cutTextView.text = item.сutText
                holder.itemView.setOnClickListener { poemListener(item) }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        val item = items[position]

        return when (item) {
            is Poem -> item.id.hashCode().toLong()
            else -> super.getItemId(position)
        }
    }

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PoemViewHolder =
            LayoutInflater.from(parent!!.context).inflate(R.layout.item_poem, parent, false)
                    .let {
                        PoemViewHolder(it,
                                it.findViewById(R.id.name),
                                it.findViewById(R.id.year),
                                it.findViewById(R.id.text)
                        )
                    }

    fun update(list: List<Poem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

}