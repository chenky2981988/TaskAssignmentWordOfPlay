package com.chirag.taskassinment.custom_components.views

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.chirag.taskassinment.R
import com.chirag.taskassinment.custom_components.ProgressWatcher
import com.chirag.taskassinment.custom_components.StatusCallback
import com.chirag.taskassinment.data.model.StatusItem
import com.chirag.taskassinment.data.model.StatusItemType
import kotlinx.android.synthetic.main.progress_status_view.view.*


/**
 * Created by Chirag Sidhiwala on 30/4/20.
 */
class CustomConstraintView(
    context: Context,
    private val statusImageView: ImageView,
    private val statusVideoView: VideoView,
    private var statusItemList: List<StatusItem>,
    private val passedInContainerView: ViewGroup,
    private var statusCallback: StatusCallback,
    @DrawableRes private var mProgressDrawable: Int = R.drawable.lightgrey_progress_drawable
) : ConstraintLayout(context) {

    private var currentlyShownIndex = 0
    private lateinit var currentView: View
    private var libSliderViewList = mutableListOf<CustomProgressbar>()
    private lateinit var view: View
    private var pausedState: Boolean = false
    lateinit var gestureDetector: GestureDetector

    init {
        initView()
        init()
    }

    private fun init() {
        statusItemList.forEachIndexed { index, statusItem ->

            val myProgressBar = CustomProgressbar(
                context,
                index,
                getProgressDuration(statusItem.type),
                object : ProgressWatcher {
                    override fun onProgressEnd(indexEnded: Int) {
                        currentlyShownIndex = indexEnded + 1
                        next()
                    }
                },
                mProgressDrawable
            )
            libSliderViewList.add(myProgressBar)
            view.linearProgressIndicatorLay.addView(myProgressBar)
        }
    }

    fun callPause(pause: Boolean) {
        try {
            if (pause) {
                if (!pausedState) {
                    this.pausedState = !pausedState
                    pause(false)
                }
            } else {
                if (pausedState) {
                    this.pausedState = !pausedState
                    resume()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initView() {
        view = View.inflate(context, R.layout.progress_status_view, this)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        gestureDetector = GestureDetector(context, SingleTapConfirm())

        val touchListener = object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (gestureDetector.onTouchEvent(event)) {
                    // single tap
                    if (v?.id == view.rightLay.id) {
                        next()
                    } else if (v?.id == view.leftLay.id) {
                        prev()
                    }
                    return true
                } else {
                    // your code for move and drag
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            callPause(true)
                            return true
                        }

                        MotionEvent.ACTION_UP -> {
                            callPause(false)
                            return true
                        }
                        else -> return false
                    }
                }
            }
        }

        view.leftLay.setOnTouchListener(touchListener)
        view.rightLay.setOnTouchListener(touchListener)

        this.layoutParams = params
        passedInContainerView.addView(this)
    }

    private fun show() {
        view.loaderProgressbar.visibility = View.GONE
        if (currentlyShownIndex != 0) {
            for (i in 0..Math.max(0, currentlyShownIndex - 1)) {
                libSliderViewList[i].progress = 100
                libSliderViewList[i].cancelProgress()
            }
        }

        if (currentlyShownIndex != libSliderViewList.size - 1) {
            for (i in currentlyShownIndex + 1 until libSliderViewList.size) {
                libSliderViewList[i].progress = 0
                libSliderViewList[i].cancelProgress()
            }
        }

        currentView = getView(statusItemList.get(currentlyShownIndex).type)

        libSliderViewList[currentlyShownIndex].startProgress()

        statusCallback.onNextCalled(currentView, this, currentlyShownIndex)

        view.currentlyDisplayedView.removeAllViews()
        view.currentlyDisplayedView.addView(currentView)
        /*val params = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT, 1f
        )*/
        if (currentView is ImageView) {
            (currentView as ImageView).scaleType = ImageView.ScaleType.CENTER_CROP
        }
        view.descriptionTv.text = statusItemList.get(currentlyShownIndex).description
    }

    fun start() {
        show()
    }

    fun editDurationAndResume(index: Int, newDurationInSeconds: Int) {
        view.loaderProgressbar.visibility = View.GONE
        libSliderViewList[index].editDurationAndResume(newDurationInSeconds)
    }

    fun pause(withLoader: Boolean) {
        if (withLoader) {
            view.loaderProgressbar.visibility = View.VISIBLE
        }
        libSliderViewList[currentlyShownIndex].pauseProgress()
        val currentView = getView(statusItemList.get(currentlyShownIndex).type)
        (currentView as? VideoView)?.pause()
    }

    fun resume() {
        view.loaderProgressbar.visibility = View.GONE
        libSliderViewList[currentlyShownIndex].resumeProgress()
        val currentView = getView(statusItemList.get(currentlyShownIndex).type)
        (currentView as? VideoView)?.start()
    }

    fun next() {
        try {
            if (currentView == getView(statusItemList.get(currentlyShownIndex).type)) {
                currentlyShownIndex++
                if (statusItemList.size <= currentlyShownIndex) {
                    finish()
                    return
                }
            }
            show()
        } catch (e: IndexOutOfBoundsException) {
            finish()
        }
    }

    private fun finish() {
        statusCallback.done()
        for (progressBar in libSliderViewList) {
            progressBar.cancelProgress()
            progressBar.progress = 100
        }
    }

    fun prev() {
        try {
            if (currentView == getView(statusItemList.get(currentlyShownIndex).type)) {
                currentlyShownIndex--
                if (0 > currentlyShownIndex) {
                    currentlyShownIndex = 0
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            currentlyShownIndex -= 2
        } finally {
            show()
        }
    }

    private inner class SingleTapConfirm : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            return true
        }
    }

    private fun getProgressDuration(statusType: StatusItemType): Int {
        return when (statusType) {
            StatusItemType.STATUS_IMAGE -> 3
            StatusItemType.STATUS_VIDEO -> 30
        }
    }

    private fun getView(statusType: StatusItemType): View {
        return when (statusType) {
            StatusItemType.STATUS_IMAGE -> {
                statusImageView
            }
            StatusItemType.STATUS_VIDEO -> {
                statusVideoView
            }
        }
    }

    fun getCurrentItem(position: Int): StatusItem {
        return statusItemList.get(position)
    }


}