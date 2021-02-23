package com.ww.wwapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ww.wwapplication.model.Feed
import com.ww.wwapplication.repository.FeedListRepository

class FeedListViewModel(application: Application): AndroidViewModel(application) {
    private val feedRepository = FeedListRepository(application)
    val showLoader:LiveData<Boolean>
    val feedList:LiveData<List<Feed>>
    val selectedFeed:LiveData<Feed>

    init {
        this.showLoader  = feedRepository.showLoader
        this.feedList = feedRepository.feedsList
        this.selectedFeed = feedRepository.selectedFeed
    }

    fun changeState(){
        feedRepository.changeState()
    }

    fun fetchFeeds(){
        feedRepository.fetchFeeds()
    }

}