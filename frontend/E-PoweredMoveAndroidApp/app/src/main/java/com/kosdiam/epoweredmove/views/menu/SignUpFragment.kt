package com.kosdiam.epoweredmove.views.menu

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.edit
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.helpers.SensorStatus
import com.kosdiam.epoweredmove.helpers.Validators
import com.kosdiam.epoweredmove.models.User
import com.kosdiam.epoweredmove.models.enums.MessageType
import com.kosdiam.epoweredmove.services.UserService
import retrofit2.Call
import retrofit2.Response

class SignUpFragment : Fragment() {
    private lateinit var signUpBtn: MaterialButton
    private lateinit var nameText: EditText
    private lateinit var surnameText: EditText
    private lateinit var phoneText: EditText
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var repeatPasswordText: EditText
    private lateinit var signInText: TextView

    private lateinit var parentActivity: MenuActivity

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_FIREBASE_TOKEN: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        signUpBtn = view.findViewById(R.id.sign_up_local)
        nameText = view.findViewById(R.id.name_input)
        surnameText = view.findViewById(R.id.surname_input)
        phoneText = view.findViewById(R.id.phone_input)
        emailText = view.findViewById(R.id.email_input)
        passwordText = view.findViewById(R.id.password_input)
        repeatPasswordText = view.findViewById(R.id.repeat_password_input)
        signInText = view.findViewById(R.id.sign_in_text)

        if(activity!=null){
            parentActivity = activity as MenuActivity
        }

        PREF_FIREBASE_TOKEN= getString(R.string.shared_pref_firebase_token)
        mSharedPreferences = parentActivity.getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)

        signInText.setOnClickListener {
            parentActivity.setViewPager(1)
        }

        signUpBtn.setOnClickListener {
            signUp()
        }
        return view
    }

    private fun signUp() {
        parentActivity.setProgressBarVisibility(View.VISIBLE)
        val sensorStatus = SensorStatus()
        if(!sensorStatus.isNetOnline(parentActivity)){
            parentActivity.showMessage(MessageType.NO_INTERNET)
            return
        }

        if(validate()){
            parentActivity.getFirebaseAuth().createUserWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    //set token to shared prefs, send request to create user and recreate activity
                    it.result.user!!.getIdToken(true).addOnCompleteListener {token ->
                        mSharedPreferences.edit {
                            putString(PREF_FIREBASE_TOKEN , token.result.token)
                        }
                        val user = User()
                        user.id = it.result.user!!.uid
                        user.email = emailText.text.toString()
                        user.name = nameText.text.toString()
                        user.surname = surnameText.text.toString()
                        user.phone = phoneText.text.toString()
                        val userService = UserService()
                        val call = userService.userService.createUserLocally(user)
                        call.enqueue(object : retrofit2.Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                parentActivity.recreate()
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                parentActivity.showMessage(MessageType.ERROR_CREATE_ACCOUNT)
                            }

                        })
                    }
                }
                else{
                    parentActivity.showMessage(MessageType.ERROR_CREATE_ACCOUNT)
                }
            }
        }
        else{
            parentActivity.setProgressBarVisibility(View.GONE)
            return
        }
    }

    private fun validate(): Boolean{
        val validators = Validators()
        var valid = true
        if(!validators.validateName(nameText.text, parentActivity)){
            nameText.error = parentActivity.getString(R.string.not_valid_name)
            valid = false
        }
        else{
            nameText.error = null
        }

        if(!validators.validateName(surnameText.text, parentActivity)){
            surnameText.error = parentActivity.getString(R.string.not_valid_name)
            valid = false
        }
        else{
            surnameText.error = null
        }

        if(!validators.validateEmail(emailText.text)){
            emailText.error = parentActivity.getString(R.string.not_valid_email)
            valid = false
        }
        else{
            emailText.error = null
        }

        if(!validators.validatePhone(phoneText.text)){
            phoneText.error = parentActivity.getString(R.string.not_valid_phone)
            valid = false
        }
        else{
            phoneText.error = null
        }

        if(!validators.validatePassword(passwordText.text, parentActivity)){
            passwordText.error = parentActivity.getString(R.string.not_valid_pass_length)
            valid = false
        }
        else{
            passwordText.error = null
        }

        if(!validators.validatePassword(repeatPasswordText.text, parentActivity)){
            repeatPasswordText.error = parentActivity.getString(R.string.not_valid_pass_length)
            valid = false
        }
        else if(!validators.confirmPasswords(passwordText.text, repeatPasswordText.text)){
            repeatPasswordText.error = parentActivity.getString(R.string.passwords_not_match)
            valid = false
        }
        else{
            repeatPasswordText.error = null
        }
        return valid
    }
}