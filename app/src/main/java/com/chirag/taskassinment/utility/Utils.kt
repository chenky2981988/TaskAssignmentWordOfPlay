package com.chirag.taskassinment.utility

import android.content.Context
import android.util.TypedValue
import java.io.File
import java.io.IOException


/**
 * Created by Chirag Sidhiwala on 30/4/20.
 */
fun Float.toPixel(mContext: Context): Int {
    val r = mContext.resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        r.displayMetrics
    ).toInt()
}
fun getVideoCacheDir(context: Context): File{
    return File(context.externalCacheDir, CACHE_DIRECTORY_NAME)
}

@Throws(IOException::class)
fun cleanVideoCacheDir(context: Context) {
    val videoCacheDir: File = getVideoCacheDir(context)
    cleanDirectory(videoCacheDir)
}

@Throws(IOException::class)
private fun cleanDirectory(file: File) {
    if (!file.exists()) {
        return
    }
    val contentFiles = file.listFiles()
    if (contentFiles != null) {
        for (contentFile in contentFiles) {
            delete(contentFile)
        }
    }
}

@Throws(IOException::class)
private fun delete(file: File) {
    if (file.isFile && file.exists()) {
        deleteOrThrow(file)
    } else {
        cleanDirectory(file)
        deleteOrThrow(file)
    }
}

@Throws(IOException::class)
private fun deleteOrThrow(file: File) {
    if (file.exists()) {
        val isDeleted = file.delete()
        if (!isDeleted) {
            throw IOException(
                String.format(
                    "File %s can't be deleted",
                    file.absolutePath
                )
            )
        }
    }
}
