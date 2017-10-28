package com.punksta.apps.robopoetry.screens.writerLists

import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.Celebration
import com.punksta.apps.robopoetry.entity.Entity
import com.punksta.apps.robopoetry.entity.WriterInfo
import com.punksta.apps.robopoetry.ext.setTypeFace
import org.w3c.dom.Text

/**
 * Created by stanislav on 1/2/17.
 */

class WriterVH(root: View, val name: TextView, val count: TextView, val deliver: View) : RecyclerView.ViewHolder(root)
class HeaderVH(root: View, val name: TextView) : RecyclerView.ViewHolder(root)

class WriterAdapter(var headers: List<Celebration>, var items: MutableList<WriterInfo>, val listener: Entity.() -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return when  {
            headers.isNotEmpty() && position < headers.size -> headers[position].name.hashCode().toLong()
            else -> items[position - headers.size].hashCode().toLong()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when  {
            headers.isNotEmpty() && position < headers.size -> typeHeader
            else -> typeWriter
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WriterVH -> {
                val item = items[position - headers.size]
                val res = holder.itemView.context.resources

                holder.count.text = res.getQuantityString(R.plurals.poem_plural, item.poemCount, item.poemCount);
                holder.name.text = item.name
                holder.itemView?.setOnClickListener {
                    listener(item)
                }
            }
            is HeaderVH -> {
                val item = headers[position]
                holder.name.setText(item.name)
                holder.itemView?.setOnClickListener {
                    listener(item)
                }
            }
        }
    }

    val typeHeader = 1
    val typeWriter = 2

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        typeWriter -> {
            LayoutInflater.from(parent!!.context).inflate(R.layout.item_writer, parent, false)
                    .let { WriterVH(it, it.findViewById(R.id.name), it.findViewById(R.id.count), it.findViewById(R.id.deliver)) }
                    .apply {
                        name.setTypeFace("clacon.ttf")
                        count.setTypeFace("clacon.ttf")
                    }
        }
        typeHeader -> {
            LayoutInflater.from(parent!!.context).inflate(R.layout.item_celebration_8, parent, false)
                    .let { HeaderVH(it, it.findViewById(R.id.name)) }
                    .apply {
                        name.setTypeFace("clacon.ttf")
                    }
        }
        else -> throw IllegalArgumentException("unknown type")
    }


    override fun getItemCount(): Int = items.count() + headers.count()

    fun change(list: List<WriterInfo>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}