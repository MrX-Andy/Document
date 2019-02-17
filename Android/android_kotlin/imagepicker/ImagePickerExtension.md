```
@file:Suppress("HasPlatformType", "UNUSED_PARAMETER", "unused")

package org.alex.imagepicker

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.zelory.compressor.Compressor
import org.alex.extension.logW
import org.alex.util.LogTrack
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DecimalFormat

/**
 * 作者：Alex
 * 时间：2017/7/21 13:48
 * 简述：
 */
@Suppress("unused", "ConvertSecondaryConstructorToPrimary")
class ImagePickerHelper {
    private var requestCode4Camera = 0
    private var requestCode4Album = 0
    private var context: Context
    /**用来存储 系统相机 拍摄的图片 File， 携带SD卡的根目录的 全路径*/
    private lateinit var tmpDestFilePath: String
    private var imageEntity = ImageEntity()
    lateinit var imagePickerCallback: ImagePickerCallback
    private var activity: FragmentActivity? = null

    constructor (activity: FragmentActivity) {
        this.activity = activity
        context = activity
        attach()
    }

    private val pickerFragment = ImagePickerFragment()

    private fun attach() {
        val fragmentManager = activity?.supportFragmentManager
        pickerFragment.imagePickerHelper = this@ImagePickerHelper
        fragmentManager?.beginTransaction()
                ?.add(pickerFragment, ImagePickerFragment::class.java.simpleName)
                ?.commitAllowingStateLoss()
    }

    init {
        FileCache.clearImagePickerCache()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode4Camera == requestCode) {
            notifyCallback(tmpDestFilePath)
            return
        }

        if (data == null) {
            return
        }
        if (requestCode4Album == requestCode) {
            val filePath = data.data.getFilePath(context)
            notifyCallback(filePath)
            return
        }
    }

    private fun notifyCallback(filePath: String) {
        val originalFile = File(filePath)
        val compressToFile = originalFile.compressToFile(context)
        imageEntity.apply {
            originalPath = filePath
            originalSize = originalFile.length()
            compressedPath = compressToFile.absolutePath
            compressedSize = compressToFile.length()
            compressedScale = compressedSize.toDouble() / originalSize.toDouble()
        }
        imagePickerCallback.onGetImageFile(imageEntity)
    }

    /**
     * 调用 相机
     * @param tmpDestFilename  用来存储 系统相机 拍摄的图片 File， 不需要携带SD卡的根目录
     */
    fun startActivityForCamera(requestCode: Int, tmpDestFilename: String) {
        requestCode4Camera = requestCode
        val tmpDestFile: File = FileCache.createOrExistsFile(FileCache.cameraDir + tmpDestFilename)
        this@ImagePickerHelper.tmpDestFilePath = tmpDestFile.absolutePath
        val uriForFile: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriForFile = FileProvider.getUriForFile(activity, activity?.applicationContext
                    ?.packageName + ".provider", tmpDestFile)
        } else {
            uriForFile = Uri.fromFile(tmpDestFile)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
        pickerFragment.startActivityForResult(intent, requestCode)
    }

    /**
     * 调用 系统相册
     */
    fun startActivityForAlbum(requestCode: Int, tmpDestFilename: String) {
        requestCode4Album = requestCode
        val tmpDestFile: File = FileCache.createOrExistsFile(FileCache.albumDir + tmpDestFilename)
        this@ImagePickerHelper.tmpDestFilePath = tmpDestFile.absolutePath
        val uriForFile: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uriForFile = FileProvider.getUriForFile(activity, activity?.applicationContext
                    ?.packageName + ".provider", tmpDestFile)
        } else {
            uriForFile = Uri.fromFile(tmpDestFile)
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickerFragment.startActivityForResult(intent, requestCode)
    }

    @Suppress("UNUSED_VARIABLE")
    private fun startActivityForCrop(imagePath: String) {
        val file = File("xxx.jpg")
        //cropImagePath = file.absolutePath

//        val intent = Intent("com.android.camera.action.CROP")
//        intent.setDataAndType(getImageContentUri(File(imagePath)), "image/*")
//        intent.putExtra("crop", "true")
//        intent.putExtra("aspectX", config.aspectX)
//        intent.putExtra("aspectY", config.aspectY)
//        intent.putExtra("outputX", config.outputX)
//        intent.putExtra("outputY", config.outputY)
//        intent.putExtra("scale", true)
//        intent.putExtra("return-data", false)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
//        intent.putExtra("noFaceDetection", true)
//        startActivityForResult(intent, IMAGE_CROP_CODE)
    }

    private fun rotateImage(bmp: Bitmap, degrees: Float): Bitmap {
        if (degrees != 0F) {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
        }
        return bmp
    }

    /**
     * 0  正常 图片
     *  if (uri != null) {
     *  // 这里我们即将处理图片的方向
     *  val degrees = getOrientation(this, uri)
     */
    private fun getOrientation(context: Context, photoUri: Uri): Int {
        var orientation = 0
        val cursor = context.contentResolver.query(photoUri, arrayOf(MediaStore.Images.ImageColumns.ORIENTATION), null, null, null)
        if (cursor != null) {
            if (cursor.count != 1) {
                return -1
            }
            cursor.moveToFirst()
            orientation = cursor.getInt(0)
            cursor.close()
        }
        return orientation
    }

    private fun Uri.getFilePath(context: Context): String {
        val uri: Uri = this
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            if (isGooglePhotosUri(uri))
                return uri.lastPathSegment
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return ""
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String? = null, selectionArgs: Array<String>? = null): String {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun File.compressToFile(context: Context): File {
        var compressToFile: File = this
        try {
            compressToFile = Compressor.Builder(context).setMaxWidth(640F).setMaxHeight(480F).setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(FileCache.compressDir).build()
                    .compressToFile(compressToFile)
        } catch (ex: Exception) {
            ex.printStackTrace()
            LogTrack.e(ex)
        }
        return compressToFile
    }

    fun clearImagePickerCache() {
        FileCache.clearImagePickerCache()
    }

    fun onDestroy() {

        clearImagePickerCache()
        activity = null
    }

}

interface ImagePickerCallback {
    fun onGetImageFile(result: ImageEntity)
}

class ImagePickerFragment : Fragment() {
    lateinit var imagePickerHelper: ImagePickerHelper

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        imagePickerHelper.onActivityResult(requestCode, resultCode, data)
    }


    override fun onDestroy() {
        super.onDestroy()
        "kk".logW()
        imagePickerHelper.onDestroy()
    }

}

private object FileCache {
    /**
     * 路径  最后 没有  /
     */
    val imagePickerRootPath = Environment.getExternalStorageDirectory().absolutePath + "/ImagePicker/"

    /**
     * 存放 拍照产生的 图片 的 路径
     */
    val cameraDir = imagePickerRootPath + "camera/"
    val albumDir = imagePickerRootPath + "album/"
    /**
     * 存放 被压缩后的文件 的 路径
     */
    val compressDir = imagePickerRootPath + "compress/"

    fun createOrExistsFile(path: String): File {
        createOrExistsFile(File(path))
        return File(path)
    }

    private fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) {
            return file.isFile
        }
        if (!createOrExistsDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    /**
     * 清除  因为 图片压缩 造成的 SD 卡 缓存
     */
    fun clearImagePickerCache() {
        try {
            File(cameraDir).deleteRecursively()
            File(albumDir).deleteRecursively()
            File(compressDir).deleteRecursively()
        } catch (ex: Exception) {
        }
    }

    fun getFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    fun getLength(path: String?): Int {
        if (path == null || path.isEmpty()) {
            return 0

        }
        val file = File(path)
        if (!file.exists()) {
            return 0
        }
        val inputStream = FileInputStream(path)
        val length = inputStream.available()
        inputStream.close()
        return length
    }

}


class ImageEntity {

    /**
     * 图片原始路径
     * */
    var originalPath = ""
    /**
     * 图片 被压缩后的 存放路径
     * */
    var compressedPath = ""
    /**
     * 原始 文件 大小，假设之前没有001.png  第一次读原始文件大小  是为0 的
     * */
    var originalSize = 0L
    /**
     * 压缩后 文件 大小
     * */
    var compressedSize = 0L
    /**
     * 压缩比例
     */
    var compressedScale = 0.0

    private fun getFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    override fun toString(): String {
        return "ImageEntity(originalPath='$originalPath', " +
                "compressedPath='$compressedPath', " +
                "originalSize=${getFileSize(originalSize)}, " +
                "compressedSize=${getFileSize(compressedSize)}, " +
                "compressedScale=$compressedScale)"
    }
}
```
