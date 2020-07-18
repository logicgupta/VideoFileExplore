package com.logic.myvideofiles

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.logic.myvideofiles.adapter.VideoAdapter
import com.logic.myvideofiles.model.VideoModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    private val PERMISSION_READ = 0
   private lateinit var videoArrayList: ArrayList<VideoModel>

   private val adapter:VideoAdapter by lazy {
        VideoAdapter(object:VideoAdapter.CallBack{
            override fun playVideo(uri: Uri) {
                val intent= Intent(this@MainActivity,PlayVideoActivity::class.java)
                intent.putExtra("uri",uri.toString())
                startActivity(intent)
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkPermission()) {
            videoList();
        }

    }

    fun videoList() {
        recycler_view.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_view.itemAnimator = DefaultItemAnimator()
        videoArrayList = ArrayList()
        getVideos()
    }


    //get video files from storage
    fun getVideos() {
        val contentResolver = contentResolver
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val duration: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                val data: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val videoModel = VideoModel(title,videoDuration =timeConversion(duration.toLong())
                    ,videoUri =(Uri.parse(data))  )
                Log.e("bvjh",title)
                videoArrayList.add(videoModel)
            } while (cursor.moveToNext())
        }
        adapter.setCtxt(this@MainActivity)
        adapter.setData(videoArrayList)
        recycler_view.adapter = adapter
    }


    //time conversion
    fun timeConversion(value: Long): String {
        val videoTime: String
        val dur = value.toInt()
        val hrs = dur / 3600000
        val mns = dur / 60000 % 60000
        val scs = dur % 60000 / 1000
        videoTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return videoTime
    }

    //runtime storage permission
    fun checkPermission(): Boolean {
        val READ_EXTERNAL_PERMISSION =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_READ
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_READ -> {
                if (grantResults.isNotEmpty() && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(
                            applicationContext,
                            "Please allow storage permission",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        videoList()
                    }
                }
            }
        }
    }
}