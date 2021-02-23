package com.ww.wwapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.ww.wwapplication.glide.GlideApp
import com.ww.wwapplication.model.Feed
import com.ww.wwapplication.viewmodel.FeedListViewModel
import kotlinx.android.synthetic.main.feed_detail.*

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [FeedsListActivity]
 * in two-pane mode (on tablets) or a [FeedDetailActivity]
 * on handsets.
 */
class FeedDetailFragment : Fragment() {

    private var feed: Feed? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM)) {
                feed = it.getParcelable(ARG_ITEM)

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.feed_detail, container, false)

        // Show the dummy content as text in a TextView.
        feed?.let {
            rootView.findViewById<TextView>(R.id.item_title).text = it.title
            rootView.findViewById<TextView>(R.id.item_publish_time).text = resources.getString(R.string.publishedAt,it.publishedAt)
            rootView.findViewById<TextView>(R.id.item_detail).text = it.description
            GlideApp.with(this)
                .load(it.urlToImage)
                .fitCenter()
                .into(rootView.findViewById<AppCompatImageView>(R.id.feed_image));
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM = "item"
    }
}