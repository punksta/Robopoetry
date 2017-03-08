package com.punksta.apps.robopoetry.screens.writerLists

import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.WriterInfo
import com.punksta.apps.robopoetry.ext.setTypeFace
import org.w3c.dom.Text

/**
 * Created by stanislav on 1/2/17.
 */

class WriterVH(root: View, val name: TextView, val count: TextView, val deliver: View) : RecyclerView.ViewHolder(root)

class WriterAdapter(var items: MutableList<WriterInfo>, val listener: WriterInfo.() -> Unit) : RecyclerView.Adapter<WriterVH>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return items[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: WriterVH, position: Int) {
        val item = items[position]
        val res = holder.itemView.context.resources

        holder.count.text = res.getQuantityString(R.plurals.poem_plural, item.poemCount, item.poemCount);
        holder.name.text = item.name
        holder.itemView?.setOnClickListener {
            listener(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): WriterVH  =
        LayoutInflater.from(parent!!.context).inflate(R.layout.item_writer, parent, false)
                .let { WriterVH(it, it.findViewById(R.id.name) as TextView, it.findViewById(R.id.count) as TextView, it.findViewById(R.id.deliver)) }
                .apply {
                    name.setTypeFace("clacon.ttf")
                    count.setTypeFace("clacon.ttf")
                }

    override fun getItemCount(): Int = items.count()
    fun change(list: List<WriterInfo>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}