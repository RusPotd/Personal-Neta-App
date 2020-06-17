package com.example.basicotplogin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.ui.AppBarConfiguration
import androidx.viewpager.widget.ViewPager
import com.example.basicotplogin.Fragments.ChatsFragment
import com.example.basicotplogin.MainFragments.MainFragment
import com.example.basicotplogin.ModelClasses.Users
import com.example.basicotplogin.Notifications.Token
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.processphoenix.ProcessPhoenix
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    var refUsers: DatabaseReference? = null
    var refuserContact: String = ""
    var firebaseUser: FirebaseUser? = null
    var AdminUid: String? = null
    var refreshToken: String= ""
    var Admin: Boolean = false
    var refAdmin: DatabaseReference? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val RECORD_REQUEST_CODE = 1

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var menuHam: Menu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                RECORD_REQUEST_CODE)
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        firebaseUser = FirebaseAuth.getInstance().currentUser

        //Rest of code
        refreshToken = FirebaseInstanceId.getInstance().token!!
        updateToken(refreshToken)

        val toolbar: Toolbar =
            findViewById(R.id.toolbar_main)                 //create a back button on top of toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        toolbar.getOverflowIcon()!!.setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);

        drawer = findViewById(R.id.drawer_layout)
        var NavView = drawer.findViewById<NavigationView>(R.id.nav_view)
        menuHam = NavView.menu
        menuHam.setGroupVisible(R.id.user_group, false)
        menuHam.setGroupVisible(R.id.admin_group, false)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        refAdmin = FirebaseDatabase.getInstance().reference.child("Admin")
        //CHeck Admin
        refAdmin!!.addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                AdminUid = p0.child("uid").value.toString()
                if(p0.child("uid").value!!.equals(firebaseUser!!.uid)){
                    Admin = true
                    menuHam.setGroupVisible(R.id.admin_group, true)

                    val user: Users? = p0.getValue(Users::class.java)       //create user of instance Users class

                    refuserContact = user!!.getPhone().toString()
                    drawer.findViewById<TextView>(R.id.NavName).text = user.getUsername()
                    drawer.findViewById<TextView>(R.id.NavPhone).text = user.getPhone()
                    Picasso.get().load(user.getProfile()).into(drawer.findViewById<CircleImageView>(R.id.NavProfile))

                    val tabLayout: TabLayout = findViewById(R.id.tab_layout_main)
                    val viewPager: ViewPager = findViewById(R.id.view_pager_main)
                    val viewPagerAdater = ViewPagerAdapter(supportFragmentManager)
                    viewPagerAdater.addFragment(MainFragment(), "Home")
                    viewPagerAdater.addFragment(ChatsFragment(), "Recent Chats")
                    viewPager.adapter = viewPagerAdater
                    tabLayout.setupWithViewPager(viewPager)

                    drawer.findViewById<CircleImageView>(R.id.NavProfile).setOnClickListener {
                        val intent =  Intent(this@MainActivity, SettingsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        if(Admin){
                            intent.putExtra("admin", firebaseUser!!.uid)
                        }
                        startActivity(intent)
                        finish()
                    }

                    drawer.findViewById<ImageView>(R.id.backMain).setOnClickListener {
                        onBackPressed()
                    }
                }

                if(Admin==false) {
                    menuHam.setGroupVisible(R.id.user_group, true)

                    val tabLayout: TabLayout = findViewById(R.id.tab_layout_main)
                    val viewPager: ViewPager = findViewById(R.id.view_pager_main)
                    val viewPagerAdater = ViewPagerAdapter(supportFragmentManager)
                    viewPagerAdater.addFragment(MainFragment(), "Home")
                    viewPager.adapter = viewPagerAdater
                    tabLayout.visibility = View.GONE
                    //tabLayout.setupWithViewPager(viewPager)

                    refUsers =
                        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                    refUsers!!.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                val user: Users? =
                                    p0.getValue(Users::class.java)       //create user of instance Users class

                                refuserContact = user!!.getPhone().toString()
                                drawer.findViewById<TextView>(R.id.NavName).text = user.getUsername()
                                drawer.findViewById<TextView>(R.id.NavPhone).text = user.getPhone()
                                Picasso.get().load(user.getProfile()).into(drawer.findViewById<CircleImageView>(R.id.NavProfile))

                                drawer.findViewById<CircleImageView>(R.id.NavProfile).setOnClickListener {
                                    val intent =  Intent(this@MainActivity, SettingsActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    if(Admin){
                                        intent.putExtra("admin", firebaseUser!!.uid)
                                    }
                                    startActivity(intent)
                                    finish()
                                }

                                drawer.findViewById<ImageView>(R.id.backMain).setOnClickListener {
                                    onBackPressed()
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {}
                    })
                }

            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        //check for updates
        val versionName = BuildConfig.VERSION_NAME

        var refUpdate = FirebaseDatabase.getInstance().reference.child("Update")
        refUpdate.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (!p0.child("version").value!!.equals(versionName) and !(intent.hasExtra("notUpdate"))) {
                    val intent = Intent(this@MainActivity, CheckUpdates::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        })

        //check if user is banned!!!
        var refBan = FirebaseDatabase.getInstance().reference.child("Banned")
        refBan.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                for (element in p0.children){
                    if(refuserContact.length>1) {
                        if (element.key.toString().equals(refuserContact)) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have got banned from Admin, Please contact Admin",
                                Toast.LENGTH_LONG
                            ).show()
                            FirebaseAuth.getInstance().signOut()
                        }
                    }
                }
            }
        })

        //check internet network access
        if(isNetworkAvailable().equals(null) or isNetworkAvailable().equals(false)){
            Toast.makeText(this@MainActivity, "This app requires Internet access!! Please Check Connection", Toast.LENGTH_LONG).show()
        }

        //manage exceptions
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            ProcessPhoenix.triggerRebirth(this@MainActivity);
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                             permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@MainActivity, "This app requires Storage Permission!! Please Allow", Toast.LENGTH_LONG).show()
                    //exitProcess(0)
                }
            }
        }
    }

    private fun updateToken(refreshToken: String?)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val token = Token(refreshToken!!)
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(firebaseUser!!.uid).setValue(token)
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val activeNetworkInfo: NetworkInfo = connectivityManager.getActiveNetworkInfo()!!
        }
        catch(e: Exception){
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

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

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_view_admin -> {
                if (AdminUid!!.length > 2) {
                    val intent = Intent(this@MainActivity, viewInfo::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("visit_uid", AdminUid)
                    intent.putExtra("admin", "admin")
                    startActivity(intent)
                    finish()
                }
            }

            R.id.nav_manage_group -> {
                val intent = Intent(this@MainActivity, all_broadcast::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_create_group ->  {
                val intent = Intent(this@MainActivity, create_broadcast::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_setting -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_my_complaints -> {
                val intent = Intent(this@MainActivity, MyPostsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_new_complaint -> {
                val intent = Intent(this@MainActivity, CreatePost::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_all_complaints -> {
                val intent = Intent(this@MainActivity, ViewAllComplaints::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_manage_users ->  {
                val intent = Intent(this@MainActivity, manageUsers::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_feedback  ->  {
                val intent = Intent(this@MainActivity, FeedBack::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_terms ->  {
                val intent = Intent(this@MainActivity, Terms::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_about -> {
                val intent = Intent(this@MainActivity, AboutUs::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_updates ->  {
                val intent = Intent(this@MainActivity, CheckUpdates::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_manage_complaints_groups -> {
                val intent = Intent(this@MainActivity, ManageUserGroup::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            R.id.nav_share -> {
                var api: ApplicationInfo = applicationContext.applicationInfo
                var apkPath = api.sourceDir
                var intent: Intent = Intent(Intent.ACTION_SEND)
                intent.setType("application/vnd.android.package-archive")
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(apkPath)))
                startActivity(Intent.createChooser(intent, "ShareVia"))
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}