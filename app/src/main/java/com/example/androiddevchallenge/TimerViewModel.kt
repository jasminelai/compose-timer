package com.example.androiddevchallenge

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class TimerViewModel : ViewModel() {
    private val _time = MutableLiveData(0L)
    val time: LiveData<Long> = _time

    private val _timerState = MutableLiveData(TimerState.Cleared)
    val timerState: LiveData<TimerState> = _timerState

    private val compositeDisposable = CompositeDisposable()
    private val timerObservable = Observable.interval(1L, TimeUnit.SECONDS)

    fun clearTimer() {
        _time.value = 0L
        _timerState.value = TimerState.Cleared
        compositeDisposable.clear()
    }

    fun startTimer() {
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }

        val disposable = timerObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( {
                time.value?.let { currentTime ->
                    if (currentTime > 0) {
                        _time.value = currentTime - 1L
                    }
                }
            },
                {
                    _timerState.value = TimerState.Paused
                })
        compositeDisposable.add(disposable)
        _timerState.value = TimerState.Running
    }

    fun pauseTimer() {
        compositeDisposable.clear()
        _timerState.value = TimerState.Paused
    }

    fun modifyTime(increment: Boolean, amount: Long) {
        time.value?.let { it ->
            if (increment) {
                _time.value = it + amount
            } else if (it > 0) {
                _time.value = it - amount
            }
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}