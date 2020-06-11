package com.example.basicotplogin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.ui.AppBarConfiguration
import androidx.viewpager.widget.ViewPager
import com.example.basicotplogin.MainFragments.ChatFragment
import com.example.basicotplogin.MainFragments.MainFragment
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.Notifications.Token
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*


class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var refreshToken: String= ""
    var Admin: Boolean = false
    var refAdmin: DatabaseReference? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser


        //Rest of code
        refreshToken = FirebaseInstanceId.getInstance().token!!
        updateToken(refreshToken)

        val toolbar: Toolbar =
            findViewById(R.id.toolbar_main)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        Navmenu.setOnClickListener {
            nav_view.visibility = View.VISIBLE
        }

        val navView: NavigationView = findViewById(R.id.nav_view)
        val header = navView.getHeaderView(0)
        val menu_main = navView.menu

        header.findViewById<ImageView>(R.id.backMain).setOnClickListener {
            nav_view.visibility = View.GONE
        }

        refAdmin = FirebaseDatabase.getInstance().reference.child("Admin")
        //CHeck Admin
        refAdmin!!.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if(p0.child("uid").value!!.equals(firebaseUser!!.uid)){
                    Admin = true
                    menu_main.findItem(R.id.nav_view_admin).setVisible(false)
                    menu_main.findItem(R.id.nav_new_complaint).setVisible(false)
                    menu_main.findItem(R.id.nav_my_complaints).setVisible(false)
                    val user: Users? = p0.getValue(Users::class.java)       //create user of instance Users class
                    //user_name.text = user!!.getUsername()
                    //Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile_image).into(profile_image_settings)
                    header.findViewById<TextView>(R.id.NavName).text = user!!.getUsername()
                    header.findViewById<TextView>(R.id.NavPhone).text = user.getPhone()
                    Picasso.get().load(user.getProfile()).into(header.findViewById<CircleImageView>(R.id.NavProfile))

                    val tabLayout: TabLayout = findViewById(R.id.tab_layout_main)
                    val viewPager: ViewPager = findViewById(R.id.view_pager_main)
                    val viewPagerAdater = ViewPagerAdapter(supportFragmentManager)
                    viewPagerAdater.addFragment(MainFragment(), "Home")
                    viewPagerAdater.addFragment(ChatFragment(), "Chats")
                    viewPager.adapter = viewPagerAdater
                    tabLayout.setupWithViewPager(viewPager)
                }

                if(Admin==false) {
                    menu_main.findItem(R.id.nav_manage_users).setVisible(false)
                    menu_main.findItem(R.id.nav_all_complaints).setVisible(false)
                    menu_main.findItem(R.id.nav_create_group).setVisible(false)
                    menu_main.findItem(R.id.nav_manage_group).setVisible(false)

                    val tabLayout: TabLayout = findViewById(R.id.tab_layout_main)
                    val viewPager: ViewPager = findViewById(R.id.view_pager_main)
                    val viewPagerAdater = ViewPagerAdapter(supportFragmentManager)
                    viewPagerAdater.addFragment(MainFragment(), "Home")
                    viewPager.adapter = viewPagerAdater
                    tabLayout.setupWithViewPager(viewPager)

                    refUsers =
                        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                    refUsers!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                val user: Users? =
                                    p0.getValue(Users::class.java)       //create user of instance Users class

                                //user_name.text = user!!.getUsername()
                                //Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile_image).into(profile_image_settings)
                                header.findViewById<TextView>(R.id.NavName).text = user!!.getUsername()
                                header.findViewById<TextView>(R.id.NavPhone).text = user.getPhone()
                                Picasso.get().load(user.getProfile()).into(header.findViewById<CircleImageView>(R.id.NavProfile))
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        header.findViewById<CircleImageView>(R.id.NavProfile).setOnClickListener {
            val intent =  Intent(this@MainActivity, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            if(Admin){
                intent.putExtra("admin", firebaseUser!!.uid)
            }
            startActivity(intent)
            finish()
        }

        addMenuItems(menu_main)             //add menu itemclicklistener

    }

    private fun addMenuItems(menuMain: Menu) {
        menuMain.findItem(R.id.nav_manage_group).setOnMenuItemClickListener {
            val intent =  Intent(this@MainActivity, all_broadcast::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_create_group).setOnMenuItemClickListener {
            val intent =  Intent(this@MainActivity, create_broadcast::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_setting).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_my_complaints).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, MyPostsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_new_complaint).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, CreatePost::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_all_complaints).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, ViewAllComplaints::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_manage_users).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, manageUsers::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_feedback).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, FeedBack::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_terms).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, Terms::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_about).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, AboutUs::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }

        menuMain.findItem(R.id.nav_updates).setOnMenuItemClickListener {
            val intent = Intent(this@MainActivity, CheckUpdates::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            true
        }


    }

    private fun updateToken(refreshToken: String?)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val token = Token(refreshToken!!)
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens").child(firebaseUser!!.uid).setValue(token)
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager){

        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments =  ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_menu_visiters, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.log_out -> {
                if(Admin){
                    val mapUsername = HashMap<String, Any>()
                    mapUsername["logged"] = "false"
                    FirebaseDatabase.getInstance().reference.child("Admin").updateChildren(mapUsername).addOnCompleteListener {
                        val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        FirebaseAuth.getInstance().signOut()
                    }
                }
                else {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                true
            }
            R.id.refresh -> {
                this.recreate()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}