package com.chirag.taskassinment.ui.status

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chirag.taskassinment.R
import com.chirag.taskassinment.custom_components.StatusCallback
import com.chirag.taskassinment.custom_components.views.CustomConstraintView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_status.*


class StatusActivity : AppCompatActivity(), StatusCallback {

    private lateinit var statusActivityViewModel: StatusActivityViewModel
    private lateinit var customConstraintView: CustomConstraintView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        initViewModel()
        initStatusComponents()
    }

    private fun initStatusComponents() {
        statusActivityViewModel.getStatusList().observe(this, Observer {
            //image to be loaded from the internet
            val statusImageView = ImageView(this)
            statusImageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            statusImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            //video to be loaded from the internet
            val statusVideoView = VideoView(this)
            statusVideoView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            statusVideoView.foregroundGravity = Gravity.CENTER

            customConstraintView = CustomConstraintView(
                this,
                statusImageView,
                statusVideoView,
                it,
                constraintContainer,
                this
            )
            customConstraintView.start()
        })
    }

    private fun initViewModel() {
        statusActivityViewModel = ViewModelProvider(
            this,
            StatusViewModelFactory()
        ).get(StatusActivityViewModel::class.java)
    }

    override fun done() {
        Toast.makeText(applicationContext, "Finished!", Toast.LENGTH_LONG).show()
    }

    override fun onNextCalled(view: View, customConstraintView: CustomConstraintView, index: Int) {
        if (view is VideoView) {
            customConstraintView.pause(true)
            playVideo(view, index, customConstraintView)
        } else if (view is ImageView) {
            loadImage(view, index, customConstraintView)
        }
    }

    private fun loadImage(
        imageView: ImageView,
        index: Int,
        customConstraintView: CustomConstraintView
    ) {
        customConstraintView.pause(true)
        val url = customConstraintView.getCurrentItem(index).url
        Picasso.get()
            .load(url)
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    customConstraintView.resume()
                }

                override fun onError(e: Exception?) {
                    Toast.makeText(applicationContext, e?.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                    e?.printStackTrace()
                }
            })
    }

    private fun playVideo(
        videoView: VideoView,
        index: Int,
        customConstraintView: CustomConstraintView
    ) {
        val videoUrl = customConstraintView.getCurrentItem(index).url
        videoView.setOnInfoListener(object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // Here the video starts
                    customConstraintView.editDurationAndResume(index, (videoView.duration) / 1000)
                    return true
                }
                return false
            }
        })
        setVideoViewDimension(videoView)
        startVideo(videoUrl, videoView)
    }

    private fun setVideoViewDimension(videoView: VideoView): VideoView {
        // Adjust the size of the video
        // so it fits on the screen
        val videoProportion = 1.5f
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        val screenProportion =
            screenHeight.toFloat() / screenWidth.toFloat()
        val layoutParams: ViewGroup.LayoutParams = videoView.layoutParams
        if (videoProportion < screenProportion) {
            layoutParams.height = screenHeight
            layoutParams.width = (screenHeight.toFloat() / videoProportion).toInt()
        } else {
            layoutParams.width = screenWidth
            layoutParams.height = (screenWidth.toFloat() * videoProportion).toInt()
        }
        videoView.layoutParams = layoutParams
        return videoView
    }

    private fun startVideo(url: String, videoView: VideoView) {
        val proxyUrl = statusActivityViewModel.getProxyVideoUrl(url)
        videoView.setVideoPath(proxyUrl)
        videoView.start()
    }

}
