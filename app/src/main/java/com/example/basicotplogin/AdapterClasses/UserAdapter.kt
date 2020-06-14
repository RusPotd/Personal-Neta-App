package com.example.basicotplogin.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.MainActivity
import com.example.basicotplogin.MessageChatActivity
//import com.example.basicotplogin.MessageChatActivity
import com.squareup.picasso.Picasso
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.R
import com.example.basicotplogin.viewInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*

class UserAdapter (mContext: Context,
                   mUsers: List<Users>,
                   isChatCheck: Boolean,
                   manageUser: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{

    private val mContext: Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean
    private var manageUser: Boolean

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
        this.manageUser = manageUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false )
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: Users = mUsers[i] //create user of instance Users class
        holder.userNameTxt.text = user!!.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile_image).into(holder.profileImageView)

        if(manageUser){
            holder.deleteUserBtn.visibility = View.VISIBLE

            holder.deleteUserBtn.setOnClickListener {
                val option = arrayOf<CharSequence>(
                    "Yes",
                    "No"
                )
                val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                builder.setTitle("Delete User ${user.getUsername()}?")
                builder.setItems(option, DialogInterface.OnClickListener { dialog, position ->
                    if (position == 0) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(user.getUID().toString()).removeValue()
                        var refUserBrdCst = FirebaseDatabase.getInstance().reference.child("BroadCastDetails")
                        refUserBrdCst.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                for(broadCast in p0.children){
                                    for(broadCastUsers in broadCast.children){
                                        if(broadCastUsers.key.toString().equals(user.getUID().toString())){
                                            FirebaseDatabase.getInstance().reference.child("BroadCastDetails").child(broadCast.key.toString()).child(broadCastUsers.key.toString()).removeValue()
                                        }
                                    }
                                }
                            }
                            override fun onCancelled(p0: DatabaseError){}
                        })
                        val mapUsername = HashMap<String, Any>()
                        mapUsername["phone"] = user.getPhone().toString()
                        mapUsername["id"] = user.getUID().toString()
                        FirebaseDatabase.getInstance().reference.child("Banned").child(user.getPhone().toString()).updateChildren(mapUsername)
                    }
                    if (position == 1) {
                        return@OnClickListener
                    }
                })
                builder.show()
            }
        }
        else{
            holder.deleteUserBtn.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val option = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do u want?")
            builder.setItems(option, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
                if (position == 1) {
                    val intent = Intent(mContext, viewInfo::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("visit_uid", user.getUID().toString())
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var lastMessageTxt: TextView
        var deleteUserBtn: ImageView

        init {
            userNameTxt = itemView.findViewById(R.id.username_search)
            profileImageView = itemView.findViewById(R.id.profileimage_search)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
            deleteUserBtn = itemView.findViewById(R.id.delete_user_btn)

        }

    }



}