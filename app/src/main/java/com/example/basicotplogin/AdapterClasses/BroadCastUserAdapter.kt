package com.example.basicotplogin.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.MainActivity
import com.example.basicotplogin.MessageChatActivity
//import com.example.basicotplogin.MessageChatActivity
import com.squareup.picasso.Picasso
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.R
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*

class BroadCastUserAdapter (mContext: Context,
                   mUsers: List<Users>,
                   isChatCheck: Boolean,
                            broadCastName: String
) : RecyclerView.Adapter<BroadCastUserAdapter.ViewHolder?>()
{

    private val mContext: Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean
    private var broadCastName : String


    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
        this.broadCastName = broadCastName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false )
        return BroadCastUserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: Users = mUsers[i] //create user of instance Users class
        holder.userNameTxt.text = user!!.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile_image).into(holder.profileImageView)

        holder.itemView.setOnClickListener{
            //what to do when user clicks
            if(isChatCheck){
                holder.userItem.setBackgroundColor(Color.WHITE)
                isChatCheck = false
                FirebaseDatabase.getInstance().reference.child("BroadCastDetails").child(broadCastName).child(user.getUID().toString()).removeValue()
            }
            else{
                holder.userItem.setBackgroundColor(Color.LTGRAY)
                isChatCheck = true
                val mapUsername = HashMap<String, Any>()
                mapUsername["id"] = user.getUID().toString()
                FirebaseDatabase.getInstance().reference.child("BroadCastDetails").child(broadCastName).child(user.getUID().toString()).updateChildren(mapUsername)
            }

        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var lastMessageTxt: TextView
        var userItem: RelativeLayout

        init {
            userNameTxt = itemView.findViewById(R.id.username_search)
            profileImageView = itemView.findViewById(R.id.profileimage_search)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
            userItem = itemView.findViewById(R.id.user_item)

        }

    }



}