package com.punksta.apps.robopoetry.screens.writerLists

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.Entity
import com.punksta.apps.robopoetry.entity.WriterInfo
import com.punksta.apps.robopoetry.ext.hidekeyKoard
import com.punksta.apps.robopoetry.ext.textChangesEvents
import com.punksta.apps.robopoetry.model.getModel
import com.punksta.apps.robopoetry.screens.settings.SettingsActivity
import com.punksta.apps.robopoetry.screens.writer.WriterActivity
import com.punksta.apps.robopoetry.view.ThemeActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : ThemeActivity(), (Entity) -> Unit {


    fun showSourceDialog() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun invoke(p1: Entity) {
        when (p1) {
            is WriterInfo -> startActivity(WriterActivity.getIntent(this, p1))
        }
    }

    private var disponseLoad: Disposable? = null
    private var disposableUpdate: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        writers_item.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = WriterAdapter(ArrayList(), this@MainActivity)
        }

        info_button.setOnClickListener {
            showSourceDialog()
        }

        writers_item.setOnTouchListener({ _, _ ->
            hidekeyKoard()
            false
        })
    }


    override fun onStart() {
        super.onStart()

        disposableUpdate = (findViewById<TextView>(R.id.filter_by_name))
                .textChangesEvents(findViewById<RecyclerView>(R.id.writers_item).childCount == 0)
                .flatMap {
                    getModel().queryWriters(it).toObservable()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({list ->
                    ((findViewById<RecyclerView>(R.id.writers_item)).adapter as WriterAdapter).change(list)
                })
    }

    override fun onStop() {
        super.onStop()
        disposableUpdate?.dispose()
        disponseLoad?.dispose()
    }
}
