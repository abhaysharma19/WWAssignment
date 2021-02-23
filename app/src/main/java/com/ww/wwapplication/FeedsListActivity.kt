package com.ww.wwapplication

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ww.wwapplication.glide.GlideApp

import com.ww.wwapplication.model.Feed
import com.ww.wwapplication.viewmodel.FeedListViewModel
import kotlinx.android.synthetic.main.activity_feed_list.*


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [FeedDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class FeedsListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    lateinit var feedListViewModel:FeedListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_list)
        feedListViewModel = ViewModelProvider(this).get(FeedListViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
        feedListViewModel.showLoader.observe(this, Observer {
            if (it)
                search_progress.visibility = VISIBLE
            else
                search_progress.visibility = GONE
        })

        feedListViewModel.feedList.observe(this, Observer {
            if(it != null && it.size>0) {
                no_feeds.visibility = GONE
                setupRecyclerView(findViewById(R.id.item_list), it)
            }else {
                no_feeds.visibility = VISIBLE
            }
        })

        feedListViewModel.fetchFeeds()

    }

    private fun setupRecyclerView(recyclerView: RecyclerView, feedList:List<Feed>) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, feedList, twoPane,feedListViewModel)
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: FeedsListActivity,
                                        private val values: List<Feed>,
                                        private val twoPane: Boolean,
                                        feedListViewModel:FeedListViewModel) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Feed
                if(item.description == null || item.publishedAt == null){
                    return@OnClickListener
                }
                if (twoPane) {
                    val fragment = FeedDetailFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable(FeedDetailFragment.ARG_ITEM, item)
                        }
                    }

                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, FeedDetailActivity::class.java).apply {
                        putExtra(FeedDetailFragment.ARG_ITEM, item)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            GlideApp.with(parentActivity)
                .load(item.urlToImage)
                .centerCrop()
                .into(holder.idView);

            holder.contentView.text = item.title

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: AppCompatImageView = view.findViewById(R.id.id_image)
            val contentView: TextView = view.findViewById(R.id.content)
        }
    }
}