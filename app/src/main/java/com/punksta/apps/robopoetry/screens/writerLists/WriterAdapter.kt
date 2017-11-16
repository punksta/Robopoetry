package com.punksta.apps.robopoetry.screens.writerLists

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.Entity
import com.punksta.apps.robopoetry.entity.WriterInfo

/**
 * Created by stanislav on 1/2/17.
 */

class WriterVH(root: View, val name: TextView, val count: TextView, val deliver: View) : RecyclerView.ViewHolder(root)

class WriterAdapter(var items: MutableList<WriterInfo>, val listener: Entity.() -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return typeWriter
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WriterVH -> {
                val item = items[position]
                val res = holder.itemView.context.resources

                holder.count.text = res.getQuantityString(R.plurals.poem_plural, item.poemCount, item.poemCount)
                holder.name.text = item.name
                holder.itemView?.setOnClickListener {
                    listener(item)
                }
            }
        }
    }

    private val typeWriter = 2

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        typeWriter -> {
            LayoutInflater.from(parent!!.context).inflate(R.layout.item_writer, parent, false)
                    .let { WriterVH(it, it.findViewById(R.id.name), it.findViewById(R.id.count), it.findViewById(R.id.deliver)) }

        }
        else -> throw IllegalArgumentException("unknown type")
    }


    override fun getItemCount(): Int = items.count()

    fun change(list: List<WriterInfo>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}