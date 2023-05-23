package com.kosdiam.epoweredmove.views.menu

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.models.User
import com.kosdiam.epoweredmove.models.enums.MessageType
import com.kosdiam.epoweredmove.services.UserService
import retrofit2.Call
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var userNameText: TextView
    private lateinit var userEmailText: TextView
    private lateinit var userSurnameText: TextView
    private lateinit var userPhoneText: TextView

    private lateinit var parentActivity: MenuActivity

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_FIREBASE_TOKEN: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        userNameText = view.findViewById(R.id.user_name)
        userEmailText = view.findViewById(R.id.user_email)
        userSurnameText = view.findViewById(R.id.user_surname)
        userPhoneText = view.findViewById(R.id.user_phone)

        if(activity!=null){
            parentActivity = activity as MenuActivity
        }

        PREF_FIREBASE_TOKEN= getString(R.string.shared_pref_firebase_token)
        mSharedPreferences = parentActivity.getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)

        fetchUser()
        return view
    }

    private fun fetchUser() {
        parentActivity.setProgressBarVisibility(View.VISIBLE)
        val userService = UserService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
        val call = userService.userService.getUser(parentActivity.getFirebaseAuth().currentUser!!.uid)
        call.enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.body() != null){
                    val currentUser = response.body()!!
                    userNameText.text = currentUser.name
                    userEmailText.text = currentUser.email
                    userPhoneText.text = currentUser.phone
                    userSurnameText.text = currentUser.surname
                    parentActivity.setProgressBarVisibility(View.GONE)
                }
                else{
                    parentActivity.showMessage(MessageType.ERROR_FETCHING_USER)
                    parentActivity.setViewPager(0)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                parentActivity.showMessage(MessageType.ERROR_FETCHING_USER)
                parentActivity.setViewPager(0)
            }

        })
    }

}