package com.chirag.taskassinment.custom_components.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.chirag.taskassinment.R
import com.chirag.taskassinment.custom_components.ProgressWatcher
import com.chirag.taskassinment.utility.toPixel


/**
 * Created by Chirag Sidhiwala on 30/4/20.
 */
class CustomProgressbar(
    context: Context,
    private var index: Int,
    var durationInSeconds: Int,
    private val progressWatcher: ProgressWatcher,
    @DrawableRes private var mProgressDrawable: Int = R.drawable.green_lightgrey_drawable
) : ProgressBar(
    context,
    null,
    0,
    android.R.style.Widget_Material_ProgressBar_Horizontal
) {
    private var objectAnimator = ObjectAnimator.ofInt(this, "progress", this.progress, 100)
    private var hasStarted: Boolean = false

    init {
        initProgressView()
    }

    private fun initProgressView() {

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        )

        params.marginEnd = 5f.toPixel(context)

        this.max = 100

        this.progress = 0

        this.layoutParams = params

        this.progressDrawable = ContextCompat.getDrawable(context, mProgressDrawable)

    }


    fun startProgress() {
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                progressWatcher.onProgressEnd(index)
            }

            override fun onAnimationCancel(animation: Animator?) {
                animation?.apply { removeAllListeners() }
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })
        objectAnimator.apply {
            duration = (durationInSeconds * 1000).toLong()
            start()
        }

        hasStarted = true
    }

    fun cancelProgress() {
        objectAnimator.apply {
            cancel()
        }
    }

    fun pauseProgress() {
        objectAnimator.apply {
            pause()
        }
    }

    fun resumeProgress() {
        if (hasStarted) {
            objectAnimator.apply {
                resume()
            }
        }
    }

    fun editDurationAndResume(newDurationInSecons: Int){
        this.durationInSeconds = newDurationInSecons
        cancelProgress()
        startProgress()
    }

}