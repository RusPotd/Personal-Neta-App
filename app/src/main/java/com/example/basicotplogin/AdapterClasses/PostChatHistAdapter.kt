package com.example.basicotplogin.AdapterClasses

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.example.basicotplogin.ModelClasses.ChatHist
import com.example.basicotplogin.ModelClasses.PostChatHist
import com.example.basicotplogin.ModelClasses.Users

import com.example.basicotplogin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.sender_message_view.*


class PostChatHistAdapter (mContext: Context,
                           mUsers: List<PostChatHist>,
                           isChatCheck: Boolean
) : RecyclerView.Adapter<PostChatHistAdapter.ViewHolder?>()
{

    private val mContext: Context
    private val mUsers: List<PostChatHist>
    private var isChatCheck: Boolean
    private var firebaseUser: FirebaseUser? = null
    private var refUser: DatabaseReference? = null

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType == 1){
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.sender_message_view, parent, false)
            ViewHolder(view)
        }
        else
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.receiver_message_view, parent, false)
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        val user: PostChatHist = mUsers[i] //create user of instance Users class
        refUser = FirebaseDatabase.getInstance().reference.child("Users").child(user.getSender().toString())

        //image-message right side
        if(user.getSender().equals(firebaseUser!!.uid)){

            refUser!!.addValueEventListener( object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val Visituser: Users? = p0.getValue(Users::class.java)
                        Picasso.get().load(Visituser!!.getProfile()).into(holder.userProfile)
                        holder.userName.text = Visituser.getUsername()

                        holder.userChatTxt.visibility = View.VISIBLE
                        holder.userChatTxt.text = user.getMessage()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
        //image-message left side
        else {

            refUser!!.addValueEventListener( object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val Visituser: Users? = p0.getValue(Users::class.java)
                        Picasso.get().load(Visituser!!.getProfile()).into(holder.userProfile)
                        holder.userName.text = Visituser.getUsername()

                        holder.userChatTxt.visibility = View.VISIBLE
                        holder.userChatTxt.text = user.getMessage()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userChatTxt: TextView
        var userChatImageLeft: ImageView?
        var userChatImageRight: ImageView?
        var userProfile: CircleImageView?
        var userName: TextView



        init {
            userChatTxt = itemView.findViewById(R.id.user_chat)
            userChatImageLeft = itemView.findViewById(R.id.user_chat_image_left)
            userChatImageRight = itemView.findViewById(R.id.user_chat_image_right)
            userProfile = itemView.findViewById(R.id.user_image)
            userName = itemView.findViewById(R.id.user_name)

        }

    }

    override fun getItemViewType(position: Int): Int {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        return if(mUsers[position].getSender().equals(firebaseUser!!.uid)){
            1
        }
        else
        {
            0
        }
    }



}