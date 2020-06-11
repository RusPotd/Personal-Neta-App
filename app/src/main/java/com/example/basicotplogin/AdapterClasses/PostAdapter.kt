package com.example.basicotplogin.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.MessageChatActivity
import com.example.basicotplogin.ModelClasses.ChatHist
import com.example.basicotplogin.ModelClasses.Posts
import com.example.basicotplogin.PostMsgChatActivity
import com.example.basicotplogin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class PostAdapter (mContext: Context,
                   mUsers: List<Posts>,
                   isAdmin: Boolean
) : RecyclerView.Adapter<PostAdapter.ViewHolder?>()
{

    private val mContext: Context
    private val mUsers: List<Posts>
    private var isAdmin: Boolean

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isAdmin = isAdmin
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.post_display, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userDataTxt: TextView
        var userDataImage: ImageView?
        var userProfileImage: ImageView?
        var userProfileName: TextView
        var chatSender: ImageView?

        init {
            userDataTxt = itemView.findViewById(R.id.post_text_display)
            userDataImage = itemView.findViewById(R.id.post_image_display)
            userProfileImage = itemView.findViewById(R.id.profile_image_post_display)
            userProfileName = itemView.findViewById(R.id.user_group_name_display)
            chatSender = itemView.findViewById(R.id.chat_display)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Posts = mUsers[position]

        if(!user.getImage().equals("null")) {
            holder.userDataImage!!.visibility = View.VISIBLE
            holder.userDataTxt.text = user.getData()
            try {
                Picasso.get().load(user.getSenderImage()).into(holder.userProfileImage)
            }catch (e: Exception){
                Picasso.get().load("@drawable/ic_profile").into(holder.userProfileImage)
            }
            if(isAdmin){
                holder.userProfileName.text = user.getSenderName()+" posted to "+user.getGroup()
            }
            else{
                holder.userProfileName.text = user.getSenderName()+" posted"
            }
            Picasso.get().load(user.getImage()).into(holder.userDataImage)
        }
        else
        {
            holder.userDataImage!!.visibility = View.GONE
            try {
                Picasso.get().load(user.getSenderImage()).into(holder.userProfileImage)
            }catch (e: Exception){
                Picasso.get().load("@drawable/ic_profile").into(holder.userProfileImage)
            }
            if(isAdmin){
                holder.userProfileName.text = user.getSenderName()+" posted to "+user.getGroup()
            }
            else{
                holder.userProfileName.text = user.getSenderName()+" posted"
            }
            holder.userDataTxt.text = user.getData()

        }

        holder.chatSender!!.setOnClickListener {
            val intent =  Intent(mContext, PostMsgChatActivity::class.java)
            intent.putExtra("postId", user.getPostId())
            mContext.startActivity(intent)

        }


    }


}