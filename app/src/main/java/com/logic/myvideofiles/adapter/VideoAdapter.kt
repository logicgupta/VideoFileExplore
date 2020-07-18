package com.logic.myvideofiles.adapter
import android.content.Context
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.logic.myvideofiles.R
import com.logic.myvideofiles.model.VideoModel
import kotlinx.android.synthetic.main.item_video_list.view.*


internal  class VideoAdapter(private val callback:CallBack)
    : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

   private  val videoArrayList by lazy {
         ArrayList<VideoModel>()
     }

    var context:Context?=null

    fun setCtxt(c: Context){
        context=c
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video_list,parent,false))

    override fun getItemCount(): Int {
     return videoArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= videoArrayList[position]
        holder.bind(data.videoTitle,data.videoDuration,data.videoUri)

    }

    inner class ViewHolder (private val view:View):RecyclerView.ViewHolder(view){

        init {
            view.setOnClickListener {
                callback.playVideo(videoArrayList[adapterPosition].videoUri)
            }
        }
        fun bind(videoTitle:String,duration:String,uri: Uri){
            view.title.text=videoTitle
            view.duration.text=duration
        }

    }

    fun setData(list:ArrayList<VideoModel>){
        videoArrayList.clear()
        videoArrayList.addAll(list)
        notifyDataSetChanged()
    }

    interface CallBack{
        fun playVideo(uri:Uri)
    }

}