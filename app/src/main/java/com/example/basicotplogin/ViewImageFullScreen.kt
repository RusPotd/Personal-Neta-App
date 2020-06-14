package com.example.basicotplogin

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_image_full_screen.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class ViewImageFullScreen : AppCompatActivity() {

    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image_full_screen)

        val toolbar : Toolbar = findViewById(R.id.toolbar_view_image)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Viewing Image Full-Screen"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }

        url = intent.getStringExtra("url")!!

        Picasso.get().load(url).into(view_full_image)

        //if(intent.hasExtra("other")) {}                                              //do storing image internally
    }

    /*
    @Throws(IOException::class)
    private fun getBitmapFromUrl(imgURL: String): Bitmap? {
        val IMGURL = URL(imgURL)
        val conn: HttpURLConnection = IMGURL.openConnection() as HttpURLConnection
        conn.setDoInput(true)
        conn.connect()
        conn.setConnectTimeout(30000)
        conn.setReadTimeout(30000)
        val I : InputStream = conn.getInputStream()
        var bmp = BitmapFactory.decodeStream(I)
        return bmp
    }

    fun saveImageToInternalStorage(mContext: Context?, bitmap: Bitmap) {
        val mTimeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mImageName = "snap_$mTimeStamp.jpg"
        val wrapper = ContextWrapper(mContext)
        var file: File = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "snap_$mImageName.jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //return Uri.parse(file.getAbsolutePath())
    } */
}