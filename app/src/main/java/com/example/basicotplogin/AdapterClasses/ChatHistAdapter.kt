package com.example.basicotplogin.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.basicotplogin.MessageChatActivity
import com.example.basicotplogin.ModelClasses.ChatHist
import com.example.basicotplogin.R
import com.example.basicotplogin.ViewImageFullScreen
import com.example.basicotplogin.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.receiver_message_view.view.*
import kotlinx.android.synthetic.main.sender_message_view.view.*
import java.util.*


class ChatHistAdapter (mContext: Context,
                        mUsers: List<ChatHist>,
                        isChatCheck: Boolean
) : RecyclerView.Adapter<ChatHistAdapter.ViewHolder?>()
{

    private val mContext: Context
    private val mUsers: List<ChatHist>
    private var isChatCheck: Boolean
    private var firebaseUser: FirebaseUser? = null

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType == 1){
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.sender_message_view, parent, false)
            view.findViewById<TextView>(R.id.user_name).visibility = View.GONE
            view.findViewById<CircleImageView>(R.id.user_image).visibility = View.GONE
            view.layout_right.getLayoutParams()
            ViewHolder(view)
        }
        else
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.receiver_message_view, parent, false)
            view.findViewById<TextView>(R.id.user_name).visibility = View.GONE
            view.findViewById<CircleImageView>(R.id.user_image).visibility = View.GONE
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        val user: ChatHist = mUsers[i] //create user of instance Users class

        //image-message right side
        if(user.getSender().equals(firebaseUser!!.uid)){

            if(user.getMessage() == "sent you an image."){
                holder.userChatImageRight!!.visibility = View.VISIBLE
                holder.userChatTxt.visibility = View.GONE
                Picasso.get().load(user.getUrl()).into(holder.userChatImageRight)

                holder.userChatImageRight!!.setOnClickListener {
                    val intent =  Intent(mContext, ViewImageFullScreen::class.java)
                    intent.putExtra("url", user.getUrl().toString())
                    mContext.startActivity(intent)
                }
            }
            else{
                holder.userChatTxt.visibility = View.VISIBLE
                holder.userChatTxt.text = user.getMessage()
            }

            //Do not implement message delete for users due to security issue
            /*holder.itemView.setOnLongClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
                builder.setTitle("Delete Message?")

                builder.setPositiveButton("Delete For All"){ dialog, which ->
                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["deleted"] = "true"
                    FirebaseDatabase.getInstance().reference.child("Chats").child(user.getReceiver().toString()).child(user.getKey().toString()).updateChildren(messageHashMap)
                    FirebaseDatabase.getInstance().reference.child("Chats").child(user.getSender().toString()).child(user.getKey().toString()).updateChildren(messageHashMap)
                }
                builder.setNeutralButton("Delete For Me") { dialog, which ->
                    FirebaseDatabase.getInstance().reference.child("Chats").child(user.getSender().toString()).child(user.getKey().toString()).removeValue()
                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    return@setNegativeButton
                }

                builder.show()
                true
            }*/
        }
        //image-message left side
        else {

            if (user.getMessage() == "sent you an image.") {
                holder.userChatImageLeft!!.visibility = View.VISIBLE
                holder.userChatTxt.visibility = View.GONE
                Picasso.get().load(user.getUrl()).into(holder.userChatImageLeft)

                holder.userChatImageLeft!!.setOnClickListener {
                    val intent =  Intent(mContext, ViewImageFullScreen::class.java)
                    //intent.putExtra("other", "other")
                    intent.putExtra("url", user.getUrl().toString())
                    mContext.startActivity(intent)
                }

            } else {
                holder.userChatTxt.visibility = View.VISIBLE
                holder.userChatTxt.text = user.getMessage()
            }
        }

    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userChatTxt: TextView
        var userChatImageLeft: ImageView?
        var userChatImageRight: ImageView?


        init {
            userChatTxt = itemView.findViewById(R.id.user_chat)
            userChatImageLeft = itemView.findViewById(R.id.user_chat_image_left)
            userChatImageRight = itemView.findViewById(R.id.user_chat_image_right)

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