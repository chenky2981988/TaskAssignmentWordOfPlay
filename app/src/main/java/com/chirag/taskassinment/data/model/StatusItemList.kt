package com.chirag.taskassinment.data.model


/**
 * Created by Chirag Sidhiwala on 30/4/20.
 */
object StatusItemList {
    fun listOfStatusItem(): List<StatusItem> {
        return listOf(
            StatusItem(
                "https://i.picsum.photos/id/1011/900/1600.jpg",
                "Pictre 1",
                StatusItemType.STATUS_IMAGE
            ),
            StatusItem(
                "http://www.exit109.com/~dnn/clips/RW20seconds_1.mp4",
                "Video 1",
                StatusItemType.STATUS_VIDEO
            ),
            StatusItem(
                "https://i.picsum.photos/id/1012/900/1600.jpg",
                "Pictre 2",
                StatusItemType.STATUS_IMAGE
            ),
            StatusItem(
                "http://www.exit109.com/~dnn/clips/RW20seconds_2.mp4",
                "Video 2",
                StatusItemType.STATUS_VIDEO
            ),
            StatusItem(
                "https://i.picsum.photos/id/1013/900/1600.jpg",
                "Pictre 3",
                StatusItemType.STATUS_IMAGE
            ),
            StatusItem(
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                "Video 3",
                StatusItemType.STATUS_VIDEO
            ),
            StatusItem(
                "https://i.picsum.photos/id/1014/900/1600.jpg",
                "Pictre 4",
                StatusItemType.STATUS_IMAGE
            )
        )
    }
}