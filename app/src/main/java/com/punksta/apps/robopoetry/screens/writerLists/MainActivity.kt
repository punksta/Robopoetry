package com.punksta.apps.robopoetry.screens.writerLists

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.beta.Beta
import com.punksta.apps.robopoetry.R
import com.punksta.apps.robopoetry.entity.Entity
import com.punksta.apps.robopoetry.entity.WriterInfo
import com.punksta.apps.robopoetry.ext.textChangesEvents
import com.punksta.apps.robopoetry.model.getModel
import com.punksta.apps.robopoetry.screens.writer.WriterActivity
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.util.*


class MainActivity : AppCompatActivity(), (Entity) -> Unit {


    fun showSourceDialog() {

        val textView = HtmlTextView(this)

        textView.typeface = ResourcesCompat.getFont(this, R.font.clacon_normal)
        textView.movementMethod = LinkMovementMethod.getInstance();
        textView.autoLinkMask
        textView.setHtml(R.raw.info, HtmlHttpImageGetter(textView))
        textView.textAlignment = TEXT_ALIGNMENT_CENTER
        val p = resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        textView.setPadding(p, p, p, p)

        textView.setBackgroundResource(R.drawable.terminal_background)
        textView.setTextColor(ContextCompat.getColor(this, R.color.terminal_text_color))
        AlertDialog.Builder(this)
                .setView(textView)
                .setPositiveButton("ok", {d, e -> d.dismiss()})
                .show()
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
        (findViewById<TextView>(R.id.filter_by_name))
        (findViewById<RecyclerView>(R.id.writers_item)).run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = WriterAdapter(ArrayList(), this@MainActivity)
        }

        Fabric.with(this, Crashlytics(), Beta())
        findViewById<View>(R.id.info_button).setOnClickListener {
            showSourceDialog()
        }
    }


    override fun onStart() {
        super.onStart()
//        if (loadad.not()) {
//            disponseLoad = getModel().queryWriters()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe { items, error ->
//                        (findViewById(R.id.writers_item) as RecyclerView).adapter = WriterAdapter(items.toMutableList(), this)
//
//                    }
//        }
        disposableUpdate?.dispose()
        disposableUpdate = (findViewById<TextView>(R.id.filter_by_name))
                .textChangesEvents(true)
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
