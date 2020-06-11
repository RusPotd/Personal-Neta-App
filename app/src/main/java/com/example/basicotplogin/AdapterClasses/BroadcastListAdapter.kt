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
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.MainActivity
import com.example.basicotplogin.MessageChatActivity
import com.example.basicotplogin.ModelClasses.BroadCast
import com.example.basicotplogin.ModelClasses.EditBroadCast
import com.squareup.picasso.Picasso
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*

class BroadcastListAdapter (mContext: Context,
                            mUsers: List<Users>,
                            isChatCheck: Boolean,
                            broadCastName: String,
                            PreviousBroadcastList: ArrayList<String>
) : RecyclerView.Adapter<BroadcastListAdapter.ViewHolder?>()
{

    private val mContext: Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean
    private var broadCastName : String
    private var PreviousBroadcastList: ArrayList<String>
    private var obtained : Boolean
    private var isAdded : Boolean


    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
        this.broadCastName = broadCastName
        this.PreviousBroadcastList = PreviousBroadcastList
        this.obtained = false
        this.isAdded = false

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false )
        return BroadcastListAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: Users = mUsers[i] //create user of instance Users class
        holder.userNameTxt.text = user.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile_image).into(holder.profileImageView)

        if(PreviousBroadcastList.contains(user.getUID().toString()) and obtained.equals(false)){
            holder.userItem.setBackgroundColor(Color.LTGRAY)
            isAdded = true
        }

        holder.itemView.setOnClickListener{
            //what to do when user clicks

            if(isAdded){
                holder.userItem.setBackgroundColor(Color.WHITE)
                PreviousBroadcastList.remove(user.getUID().toString())
                isAdded = false
                obtained = true
                FirebaseDatabase.getInstance().reference.child("BroadCastDetails").child(broadCastName).child(user.getUID().toString()).removeValue()
            }
            else{
                holder.userItem.setBackgroundColor(Color.LTGRAY)
                isAdded = true
                obtained = true
                PreviousBroadcastList.add(user.getUID().toString())
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