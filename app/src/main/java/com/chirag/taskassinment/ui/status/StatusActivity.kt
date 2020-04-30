package com.chirag.taskassinment.ui.status

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.chirag.taskassinment.R
import com.chirag.taskassinment.TaskAssignment
import com.chirag.taskassinment.custom_components.StatusCallback
import com.chirag.taskassinment.custom_components.views.CustomConstraintView
import com.chirag.taskassinment.data.model.StatusItemList
import com.danikula.videocache.CacheListener
import com.danikula.videocache.HttpProxyCacheServer
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_status.*
import java.io.File


class StatusActivity : AppCompatActivity(), StatusCallback, CacheListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)


        //image to be loaded from the internet
        val statusImageView = ImageView(this)

        //video to be loaded from the internet
        val statusVideoView = VideoView(this)

        CustomConstraintView(
            this,
            statusImageView,
            statusVideoView,
            StatusItemList.listOfStatusItem(),
            constraintContainer,
            this
        ).start()
    }

    override fun done() {
        Toast.makeText(applicationContext, "Finished!", Toast.LENGTH_LONG).show()
    }

    override fun onNextCalled(view: View, customConstraintView: CustomConstraintView, index: Int) {
        if (view is VideoView) {
            customConstraintView.pause(true)
            playVideo(view, index, customConstraintView)
        } else if (view is ImageView) {
            customConstraintView.pause(true)
            val url = customConstraintView.getCurrentItem(index).url
            Picasso.get()
                .load(url)
                .into(view, object : Callback {
                    override fun onSuccess() {
                        customConstraintView.resume()
                        /*Toast.makeText(
                            applicationContext,
                            "Image loaded from the internet",
                            Toast.LENGTH_LONG
                        ).show()*/
                    }

                    override fun onError(e: Exception?) {
                        Toast.makeText(applicationContext, e?.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                        e?.printStackTrace()
                    }
                })
        }
    }

    private fun playVideo(
        videoView: VideoView,
        index: Int,
        customConstraintView: CustomConstraintView
    ) {
        val videoUrl = customConstraintView.getCurrentItem(index).url
        startVideo(videoUrl, videoView)

        videoView.setOnInfoListener(object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // Here the video starts
                    customConstraintView.editDurationAndResume(index, (videoView.duration) / 1000)
                    /*Toast.makeText(
                        applicationContext,
                        "Video loaded from the internet",
                        Toast.LENGTH_LONG
                    ).show()*/
                    return true
                }
                return false
            }
        })
    }

    private fun startVideo(url: String, videoView: VideoView) {
        val httpProxyCacheServer: HttpProxyCacheServer = TaskAssignment.httpProxyCacheServer
        httpProxyCacheServer.registerCacheListener(this, url)
        Log.d("TAG", "getProxy URL : " + httpProxyCacheServer.getProxyUrl(url))
        videoView.setVideoPath(httpProxyCacheServer.getProxyUrl(url))
        videoView.start()
    }


    override fun onCacheAvailable(cacheFile: File?, url: String?, percentsAvailable: Int) {
        Log.d("TAG", "cached File Name ${cacheFile?.name}")
        Log.d("TAG", "cached File URL $url")
        Log.d("TAG", "cached File percentsAvailable $percentsAvailable")
    }
}
