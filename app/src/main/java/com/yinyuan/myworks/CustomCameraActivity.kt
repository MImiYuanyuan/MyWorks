package com.yinyuan.myworks

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
//noinspection ExifInterface
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LifecycleOwner
import com.yinyuan.myworks.R
import com.yinyuan.myworks.databinding.ActivityCustomCameraBinding
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CustomCameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomCameraBinding

    private var imageCapture: ImageCapture? = null
    private var backFile: String? = null
    private var qrTime: Long = 0
    private var provider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var isAfter = false
    private var launcher: ActivityResultLauncher<Intent>? = null

    /**
     * 是否相册相册按钮
     */
    private var isAlbum = false

    private var launcherEnclosure: ActivityResultLauncher<Intent>? = null

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 如果权限被授予
                startCamera()
            } else {
                // 如果权限被拒绝
                Toast.makeText(this, "相机权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCustomCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        setData()
        setListen()
    }

    override fun onResume() {
        super.onResume()
        binding.button.isClickable=true
    }

    private fun initView(){
        isAlbum = intent.getBooleanExtra("isAlbum", false)
        if (isAlbum) {
            binding.rlAlbum.visibility = View.VISIBLE
        } else {
            binding.rlAlbum.visibility = View.GONE
        }


        launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result?.data
                val resultCode = result?.resultCode
                if (resultCode == Activity.RESULT_OK) {
                    //裁剪反向传值返回
                    backFile = data!!.getStringExtra("imagePath")
                    val intent = Intent()
                    intent.putExtra("photoPath", backFile)
                    setResult(RESULT_OK, intent)
                    finish()
                }
        }
        launcherEnclosure=registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val data = result?.data
                if (data != null) {
                    val filePath = data.getStringExtra("filePath")
                    backFile =filePath
                    val intent = Intent()
                    intent.putExtra("photoPath", backFile)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } catch (e: Exception) {
                Log.e("附件选择出错", e.toString())
            }
        }



    }
    private fun setData(){

        if (checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            // 请求相机权限
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

    }


private fun startCamera(){
    // 1. preview
    val processCameraProvider = ProcessCameraProvider.getInstance(this)
    processCameraProvider.addListener({
        try {
            provider = processCameraProvider.get()
            val builder = Preview.Builder()
            preview = builder.build()
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(windowManager.defaultDisplay.rotation).build()
            preview!!.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            provider?.unbindAll()
            provider?.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )
        } catch (e: java.lang.Exception) {
            Log.e("cyx", "Use case binding failed", e)
        }
    }, ContextCompat.getMainExecutor(this))
}


    private fun setPhotograph(check: Boolean) {
        val photo: File = File(cacheDir.path + "/" + System.currentTimeMillis() + "back.jpg")
        if (imageCapture != null) {
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photo).build()
            imageCapture?.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        try {
//                        Uri saveUri = outputFileResults.getSavedUri();
//                        backFile = FileUtils.getFileAbsolutePath((Activity) context, saveUri);
                            backFile = photo.absolutePath
                            //解决图片旋转的问题
                            val degree: Int = getBitmapDegree(backFile)
                            val bitmap = BitmapFactory.decodeFile(backFile)
                            val newBitMap: Bitmap = rotateBitmapByDegree(bitmap, degree)
                            backFile = saveBitmapFile(this@CustomCameraActivity, newBitMap)
                            Log.i("","拍照成功地址$backFile")
                            Toast.makeText(this@CustomCameraActivity, "拍照成功地址$backFile", Toast.LENGTH_LONG).show()
//                            val intent = Intent()
//                            intent.putExtra("photoPath", backFile)
//                            setResult(RESULT_OK, intent)
//                            finish()
                        } catch (e: java.lang.Exception) {
                            Log.e("自定义相机报错",e.toString())
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("自义相机拍照失败","Photo capture failed:" + exception.localizedMessage)
                    }
                })
        }
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    fun getBitmapDegree(path: String?): Int {
        var degree = 0 //被旋转的角度
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            val exifInterface: ExifInterface = ExifInterface(path!!)
            // 获取图片的旋转信息
            val orientation: Int = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    private fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap {
        var returnBm: Bitmap? = null
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }
        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm.recycle()
        }
        return returnBm
    }

    private fun setListen(){
        binding.rlClose.setOnClickListener {
            finish()
        }
        binding.button.setOnClickListener(View.OnClickListener {
            binding.button.isClickable = false
            binding.viewFinder.postDelayed({ setPhotograph(true) }, 500)
        })
        binding.rlSwitchCamera.setOnClickListener {
            var cameraSelector: CameraSelector? = null
            if (isAfter) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                isAfter = false
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                isAfter = true
            }
            provider!!.unbindAll()
            provider!!.bindToLifecycle(
                (this@CustomCameraActivity as LifecycleOwner?)!!, cameraSelector, preview, imageCapture
            )
        }

        binding.rlAlbum.setOnClickListener {
            // TODO:  开启你的相册 
        }
    }

    private  fun saveBitmapFile(context: Context, bitmap: Bitmap): String? {
        var path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + ""
        //        String path = Environment.getExternalStorageDirectory().getPath();;
        path = path + System.currentTimeMillis() + ".png"
        Log.e("图片路径","path")
        val finalPath = path
        val file = File(finalPath) //将要保存图片的路径
        try {
            val bos = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            return finalPath
        } catch (e: IOException) {
            Log.e("转file出错", e.toString())
        }
        return null
    }
}