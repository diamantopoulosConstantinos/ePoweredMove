package com.kosdiam.epoweredmove.views.menu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.helpers.SensorStatus
import com.kosdiam.epoweredmove.models.enums.MessageType

class MenuActivity : AppCompatActivity() {
    private lateinit var fragmentContainer: FrameLayout

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var progressBarView: View
    private lateinit var toolbarText: TextView
    private lateinit var profileShortcut: RelativeLayout

    private lateinit var starterIntent: Intent

    private var thirdWindowOpen = false

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: StorageReference

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_FIREBASE_TOKEN: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        starterIntent = intent
        setContentView(R.layout.activity_menu)

        fragmentContainer = findViewById(R.id.fragmentContainer)
        progressBarView = findViewById(R.id.progress_bar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        toolbarText = findViewById(R.id.toolbar_text)
        profileShortcut = findViewById(R.id.profile_shortcut)

        PREF_FIREBASE_TOKEN= getString(R.string.shared_pref_firebase_token)
        mSharedPreferences = getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance().reference

        configNavigationToolbar()
        checkNetConnection()
        setProperFragments()

        profileShortcut.setOnClickListener {
            setViewPager(3)
        }
    }

    override fun onStart() {
        if(firebaseAuth.currentUser != null){
            firebaseAuth.currentUser!!.getIdToken(true).addOnCompleteListener {token ->
                mSharedPreferences.edit {
                    putString(PREF_FIREBASE_TOKEN , token.result.token)
                }
            }
        }
        super.onStart()
    }

    private fun configNavigationToolbar() {
        //set and config toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setProperFragments(){
        if(firebaseAuth.currentUser != null) {
            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.map -> {setViewPager(0)}
                    R.id.reservation -> {setViewPager(1)}
                    R.id.garage -> {setViewPager(2)}
                    R.id.profile -> {setViewPager(3)}
                    R.id.sign_out -> {
                        firebaseAuth.signOut()
                        recreate()
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            navigationView.menu.findItem(R.id.map).isVisible = true
            navigationView.menu.removeItem(R.id.sign_in)
            navigationView.menu.removeItem(R.id.sign_up)
            navigationView.menu.findItem(R.id.profile).isVisible = true
            navigationView.menu.findItem(R.id.sign_out).isVisible = true
            navigationView.menu.findItem(R.id.garage).isVisible = true
            navigationView.menu.findItem(R.id.reservation).isVisible = true

            profileShortcut.visibility = View.VISIBLE

            //init map fragment
            setViewPager(0)
        }
        else {
            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.map -> {setViewPager(0)}
                    R.id.sign_in -> {setViewPager(1)}
                    R.id.sign_up -> {setViewPager(2)}
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
            navigationView.menu.findItem(R.id.map).isVisible = true
            navigationView.menu.findItem(R.id.sign_in).isVisible = true
            navigationView.menu.findItem(R.id.sign_up).isVisible = true
            navigationView.menu.removeItem(R.id.profile)
            navigationView.menu.removeItem(R.id.sign_out)
            navigationView.menu.removeItem(R.id.reservation)
            navigationView.menu.removeItem(R.id.garage)

            //init map fragment
            setViewPager(0)
        }
        setProgressBarVisibility(View.GONE)
    }

    fun setViewPager(fragmentNum: Int? = 0, backPressed: Boolean = false){
        val selectedElementId: Int
        //change text of toolbar
        if(firebaseAuth.currentUser != null) {
            toolbarText.text = getString(
                when(fragmentNum){
                    0 -> {selectedElementId = R.id.map; if(!backPressed) addFragment(MapFragment()); R.string.nav_map}
                    1 -> {selectedElementId = R.id.reservation; if(!backPressed) addFragment(ReservationFragment()); R.string.nav_reservation}
                    2 -> {selectedElementId = R.id.garage; if(!backPressed) addFragment(GarageFragment()); R.string.nav_garage}
                    3 -> {selectedElementId = R.id.profile; if(!backPressed) addFragment(ProfileFragment()); R.string.nav_profile}
                    else -> {selectedElementId = R.id.map; if(!backPressed) addFragment(MapFragment()); R.string.nav_map}
                })
        }
        else {
            toolbarText.text = getString(
                when(fragmentNum){
                    0 -> {selectedElementId = R.id.map; if(!backPressed) addFragment(MapFragment()); R.string.nav_map}
                    1 -> {selectedElementId = R.id.sign_in; if(!backPressed) addFragment(SignInFragment()); R.string.sign_in}
                    2 -> {selectedElementId = R.id.sign_up; if(!backPressed) addFragment(SignUpFragment()); R.string.sign_up}
                    else -> {selectedElementId = R.id.map; if(!backPressed) addFragment(MapFragment()); R.string.nav_map}
                })
        }

        setKeepScreenOn(selectedElementId)

        //set selected element in navigation view
        navigationView.menu.forEach { item ->
            item.subMenu?.forEach { subItem ->
                subItem.isChecked = subItem.itemId == selectedElementId
            }
            item.isChecked = item.itemId == selectedElementId
        }
    }

    private fun checkNetConnection() {
        val sensorStatus = SensorStatus()
        if(!sensorStatus.isNetOnline(this)){
            showMessage(MessageType.NO_INTERNET)
        }
    }

    fun showMessage(messageResponse: MessageType){
        Toast.makeText(this, messageResponse.getString(this), Toast.LENGTH_LONG).show()
        setProgressBarVisibility(View.GONE)
    }

    fun setProgressBarVisibility(newState: Int){
        progressBarView.visibility = newState
    }

    fun addFragment(addedFragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, addedFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun setThirdWindowOpen(newState: Boolean) {
        thirdWindowOpen = newState
    }

    override fun onBackPressed() {
        //close drawer if it is open
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else if(thirdWindowOpen){
            return
        }
        else if(supportFragmentManager.backStackEntryCount > 1){
            when(supportFragmentManager.fragments[supportFragmentManager.backStackEntryCount-2]){
                null -> finish()
                is MapFragment -> setViewPager(0, true)
                is SignInFragment -> setViewPager(if(firebaseAuth.currentUser == null) 1 else 0, true)
                is SignUpFragment -> setViewPager(if(firebaseAuth.currentUser == null) 2 else 0, true)
                is ReservationFragment -> setViewPager(if(firebaseAuth.currentUser == null) 1 else 0, true)
                is GarageFragment -> setViewPager(if(firebaseAuth.currentUser == null) 2 else 0, true)
                is ProfileFragment -> setViewPager(if(firebaseAuth.currentUser != null) 3 else 0, true)
            }
            supportFragmentManager.popBackStack()
        }
        else if(supportFragmentManager.backStackEntryCount == 1){
            supportFragmentManager.popBackStack().also {
                onBackPressedDispatcher.onBackPressed()
            }
        }
        else{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun getFirebaseAuth(): FirebaseAuth{
        return firebaseAuth
    }

    fun getStorageReference(): StorageReference{
        return firebaseStorage
    }

    //keep only for map fragment
    private fun setKeepScreenOn(selectedElementId: Int) {
        if(selectedElementId == R.id.map){
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}