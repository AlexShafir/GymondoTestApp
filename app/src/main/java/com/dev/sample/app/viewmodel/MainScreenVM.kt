package com.dev.sample.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.sample.data.remote.PicsumItem
import com.dev.sample.data.repository.IPicsumRepository
import com.dev.sample.data.repository.ReqError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenVM @Inject constructor(
    private val picsumRepo: IPicsumRepository
): ViewModel() {

    sealed class State<out T> {
        object None : State<Nothing>()
        object Loading : State<Nothing>()
        data class Success<T>(val data: T) : State<T>()
        data class Error(val exception: ReqError) : State<Nothing>()
    }

    private val mState = MutableStateFlow<State< List<PicsumItem>> >(State.None)
    val state = mState.asStateFlow()

    fun getImages() {
        viewModelScope.launch {
            mState.value = State.Loading

            try {
                val images = picsumRepo.getDefaultImages()
                mState.value = State.Success(images)
            } catch (e: ReqError) {
                mState.value = State.Error(e)
            }
        }
    }

}