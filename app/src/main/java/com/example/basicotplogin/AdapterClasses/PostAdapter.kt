package com.example.basicotplogin.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.CreatePost
import com.example.basicotplogin.Fragments.APIService
import com.example.basicotplogin.ModelClasses.Posts
import com.example.basicotplogin.Notifications.*
import com.example.basicotplogin.PostMsgChatActivity
import com.example.basicotplogin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import java.util.Arrays.copyOfRange
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class PostAdapter (mContext: Context,
                   mUsers: List<Posts>,
                   isAdmin: Boolean
) : RecyclerView.Adapter<PostAdapter.ViewHolder?>()
{

    private val mContext: Context
    private val mUsers: List<Posts>
    private var isAdmin: Boolean
    private var firebaseUser = FirebaseAuth.getInstance().currentUser
    private var apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(
        APIService::class.java)

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
        var chatSender: RelativeLayout?
        var menuClick: ImageView?
        var like_btn: ImageView?
        var like_txt: TextView?
        var raise_hand_bar: RelativeLayout?
        var hand_display: ImageView?
        var hand_display_txt: TextView?

        init {
            userDataTxt = itemView.findViewById(R.id.post_text_display)
            userDataImage = itemView.findViewById(R.id.post_image_display)
            userProfileImage = itemView.findViewById(R.id.profile_image_post_display)
            userProfileName = itemView.findViewById(R.id.user_group_name_display)
            chatSender = itemView.findViewById(R.id.chat_display)
            menuClick = itemView.findViewById(R.id.user_action_menu)
            like_btn = itemView.findViewById(R.id.like_display)
            like_txt = itemView.findViewById(R.id.like_display_txt)
            raise_hand_bar = itemView.findViewById(R.id.raise_hand_bar)
            hand_display = itemView.findViewById(R.id.hand_display)
            hand_display_txt = itemView.findViewById(R.id.hand_display_txt)

        }

    }

    private fun sendNotification(username: String?, msg: String) {
        var receiverId = "JRomE1xpxqNJ70g8bum87hXVGJk1"
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children)
                {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)
                    val data = Data(
                        firebaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        "$username: $msg",
                        "User_Interested",
                        receiverId
                    )

                    val sender = Sender(data, token!!.getToken().toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object  : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: retrofit2.Response<MyResponse>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()!!.success != 1){
                                        Toast.makeText(mContext, "Failed, Nothing Happended", Toast.LENGTH_LONG).show()
                                    }
                                    else {}
                                }
                                else{}
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                Toast.makeText(mContext, "Failed to push notification", Toast.LENGTH_LONG).show()
                            }
                        })
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: Posts = mUsers[position]

        //hide unhide mark as interested
        if(!user.getIsCamp().equals("true")){
            holder.raise_hand_bar!!.visibility = View.GONE
        }

        if(!user.getImage().equals("null")) {
            holder.userDataImage!!.visibility = View.VISIBLE
            holder.userDataTxt.text = user.getData()
            Picasso.get().load(user.getImage()).into(holder.userDataImage)
        }
        else
        {
            holder.userDataImage!!.visibility = View.GONE
            holder.userDataTxt.text = user.getData()

        }

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

        var refLike = FirebaseDatabase.getInstance().reference.child("Likes").child(user.getPostId().toString())
        refLike.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.hasChild(firebaseUser!!.uid)){
                   holder.like_btn!!.setImageResource(R.drawable.liked)
               }
                holder.like_txt!!.setText(p0.childrenCount.toString())
            }
        })

        var refInterest = FirebaseDatabase.getInstance().reference.child("Interested").child(user.getPostId().toString())
        refInterest.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(firebaseUser!!.uid)){
                    holder.hand_display!!.setImageResource(R.drawable.handup)
                    holder.hand_display_txt!!.setText("Interested")
                    holder.hand_display_txt!!.setTextColor(Color.BLUE)
                }
            }
        })

        holder.like_btn!!.setOnClickListener {
            holder.like_btn!!.setImageResource(R.drawable.liked)

            var refLike = FirebaseDatabase.getInstance().reference.child("Likes")
                .child(user.getPostId().toString()).child(FirebaseAuth.getInstance().currentUser!!.uid)
            val mapLikes = HashMap<String, Any>()
            mapLikes["id"] = FirebaseAuth.getInstance().currentUser!!.uid
            refLike.updateChildren(mapLikes)
        }
        holder.raise_hand_bar!!.setOnClickListener {
                holder.hand_display!!.setImageResource(R.drawable.handup)
                holder.hand_display_txt!!.setText("Interested")
                holder.hand_display_txt!!.setTextColor(Color.BLUE)

                var refLike = FirebaseDatabase.getInstance().reference.child("Interested")
                    .child(user.getPostId().toString())
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                val mapLikes = HashMap<String, Any>()
                mapLikes["id"] = FirebaseAuth.getInstance().currentUser!!.uid
                refLike.updateChildren(mapLikes)
                var temp = user.getTime().toString().slice(0..10) //IntRange(0 to 3)
                sendNotification(
                    user.getSenderName() + " has shown Interest on Post : " + temp,
                    user.getPostId().toString()
                )
        }

        holder.chatSender!!.setOnClickListener {
            val intent =  Intent(mContext, PostMsgChatActivity::class.java)
            intent.putExtra("postId", user.getPostId())
            mContext.startActivity(intent)
        }

        if(FirebaseAuth.getInstance().currentUser!!.uid.equals(user.getSenderId().toString())) {
            holder.menuClick!!.visibility = View.VISIBLE
            holder.menuClick!!.setOnClickListener {
                val option = arrayOf<CharSequence>(
                    "Edit Post",
                    "Delete Post",
                    "Cancel"
                )
                val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                builder.setTitle("What do u want?")
                builder.setItems(option, DialogInterface.OnClickListener { dialog, position ->
                    if (position == 0) {
                        val intent = Intent(mContext, CreatePost::class.java)
                        intent.putExtra("id", user.getPostId())
                        intent.putExtra("image", user.getImage())
                        intent.putExtra("data", user.getData())
                        intent.putExtra("group", user.getGroup())
                        mContext.startActivity(intent)
                    }
                    if (position == 1) {
                        FirebaseDatabase.getInstance().reference.child("Posts")
                            .child(user.getSenderId().toString()).child(user.getPostId().toString())
                            .removeValue()
                    }
                    if (position == 2) {
                        return@OnClickListener
                    }
                })

                builder.show()
            }
        }


    }


}