package com.kosdiam.epoweredmove.views.menu

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kosdiam.epoweredmove.R
import com.kosdiam.epoweredmove.helpers.SensorStatus
import com.kosdiam.epoweredmove.helpers.Validators
import com.kosdiam.epoweredmove.models.Poi
import com.kosdiam.epoweredmove.models.User
import com.kosdiam.epoweredmove.models.enums.MessageType
import com.kosdiam.epoweredmove.services.PoiService
import com.kosdiam.epoweredmove.services.UserService
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response

class SignInFragment : Fragment() {
    private lateinit var googleSignInBtn: MaterialButton
    private lateinit var localSignInBtn: MaterialButton
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var passwordEyeImage: ImageView
    private var passwordEyePressed = false
    private lateinit var signUpText: TextView
    private lateinit var forgotPasswordText: TextView

    private lateinit var mSignInClient: GoogleSignInClient

    private lateinit var parentActivity: MenuActivity

    private lateinit var mSharedPreferences: SharedPreferences
    private var PREF_FIREBASE_TOKEN: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        googleSignInBtn = view.findViewById(R.id.sign_in_google)
        localSignInBtn = view.findViewById(R.id.sign_in_local)
        passwordEyeImage = view.findViewById(R.id.password_eye)
        passwordInput = view.findViewById(R.id.password_input)
        emailInput = view.findViewById(R.id.email_input)
        signUpText = view.findViewById(R.id.sign_up_text)
        forgotPasswordText = view.findViewById(R.id.forgot_password_text)

        if(activity!=null){
            parentActivity = activity as MenuActivity
        }

        PREF_FIREBASE_TOKEN= getString(R.string.shared_pref_firebase_token)
        mSharedPreferences = parentActivity.getSharedPreferences(getString(R.string.app_name_shared_prefs), Context.MODE_PRIVATE)

        localSignInBtn.setOnClickListener {
            signInWithLocal()
        }

        googleSignInBtn.setOnClickListener {
            signInWithGoogle()
        }

        passwordEyeImage.setOnClickListener {
            if(passwordEyePressed){
                passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordEyeImage.setImageDrawable(resources.getDrawable(R.drawable.baseline_visibility_24, context?.theme))
            }
            else{
                passwordInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passwordEyeImage.setImageDrawable(resources.getDrawable(R.drawable.baseline_visibility_off_24, context?.theme))
            }
            passwordEyePressed = !passwordEyePressed
        }

        signUpText.setOnClickListener {
            parentActivity.setViewPager(1)
        }

        forgotPasswordText.setOnClickListener {
            showForgotPassword()
        }
        return view
    }

    private fun showForgotPassword() {
        val dialog = Dialog(parentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.popup_forgot_password)
        val emailInputPopup = dialog.findViewById<EditText>(R.id.email_input_popup)
        val submitResetBtnPopup = dialog.findViewById<MaterialButton>(R.id.submit_reset_password)
        val closePopup = dialog.findViewById<Button>(R.id.close_popup)

        submitResetBtnPopup.setOnClickListener {
            parentActivity.setProgressBarVisibility(View.VISIBLE)
            val sensorStatus = SensorStatus()
            if(sensorStatus.isNetOnline(parentActivity)){
                val validators = Validators()
                if(!validators.validateEmail(emailInputPopup.text)){
                    emailInputPopup.error = parentActivity.getString(R.string.not_valid_email)
                }
                else{
                    emailInputPopup.error = null
                    parentActivity.getFirebaseAuth().sendPasswordResetEmail(emailInputPopup.text.toString()).addOnCompleteListener {
                        if(it.isSuccessful){
                            parentActivity.showMessage(MessageType.SENT_EMAIL_RESET_PASS_SUCCESS)
                            dialog.dismiss()
                            parentActivity.setProgressBarVisibility(View.GONE)
                        }
                    }
                }
            }
            else{
                parentActivity.showMessage(MessageType.NO_INTERNET)
            }
            parentActivity.setProgressBarVisibility(View.GONE)
        }

        closePopup.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun signInWithLocal() {
        parentActivity.setProgressBarVisibility(View.VISIBLE)
        val sensorStatus = SensorStatus()
        if(!sensorStatus.isNetOnline(parentActivity)){
            parentActivity.showMessage(MessageType.NO_INTERNET)
            return
        }

        if(validate()){
            parentActivity.getFirebaseAuth().signInWithEmailAndPassword(emailInput.text.toString(), passwordInput.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    it.result.user!!.getIdToken(true).addOnCompleteListener {token ->
                        mSharedPreferences.edit {
                            putString(PREF_FIREBASE_TOKEN , token.result.token)
                        }
                        parentActivity.recreate()
                    }
                }
                else{
                    parentActivity.showMessage(MessageType.ERROR_INVALID_CREDENTIAL)
                }
            }
        }
        else{
            parentActivity.setProgressBarVisibility(View.GONE)
            return
        }
    }

    private fun signInWithGoogle() {
        parentActivity.setProgressBarVisibility(View.VISIBLE)
        val sensorStatus = SensorStatus()
        if(!sensorStatus.isNetOnline(parentActivity)){
            parentActivity.showMessage(MessageType.NO_INTERNET)
            return
        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mSignInClient = GoogleSignIn.getClient(parentActivity, options)

        val intent = mSignInClient.signInIntent
        startActivityForResult(intent, parentActivity.resources.getInteger(R.integer.google_auth_code))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            parentActivity.resources.getInteger(R.integer.google_auth_code) -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                if (task.isSuccessful) {
                    val acct: GoogleSignInAccount? = task.result
                    if (acct != null) {
                        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
                        parentActivity.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener {
                            if(it.isSuccessful){
                                //if user doesn't exist
                                if(it.result.additionalUserInfo != null && it.result.additionalUserInfo!!.isNewUser){
                                    //set token to shared prefs, send request to create user and recreate activity
                                    it.result.user!!.getIdToken(true).addOnCompleteListener {token ->
                                        mSharedPreferences.edit {
                                            putString(PREF_FIREBASE_TOKEN , token.result.token)
                                        }
                                        val user = User()
                                        user.id = it.result.user!!.uid
                                        user.email = it.result.user!!.email!!
                                        user.name = getNameFromAuthResult(it.result)
                                        user.phone = if(it.result.user!!.phoneNumber != null) it.result.user!!.phoneNumber!! else ""
                                        val userService = UserService(mSharedPreferences.getString(PREF_FIREBASE_TOKEN, ""))
                                        val call = userService.userService.googleSignIn(user)
                                        call.enqueue(object : retrofit2.Callback<Boolean> {
                                            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                                                parentActivity.recreate()
                                            }

                                            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                                                parentActivity.showMessage(MessageType.GOOGLE_SIGN_IN_FAILED)
                                            }

                                        })
                                    }
                                }
                                else{
                                    //set token to shared prefs and recreate activity
                                    it.result.user!!.getIdToken(true).addOnCompleteListener {token ->
                                        mSharedPreferences.edit {
                                            putString(PREF_FIREBASE_TOKEN , token.result.token)
                                        }
                                        parentActivity.recreate()
                                    }

                                }
                            }
                            else{
                                parentActivity.showMessage(MessageType.GOOGLE_SIGN_IN_FAILED)
                            }
                        }
                    }
                }
                else{
                    parentActivity.showMessage(MessageType.GOOGLE_SIGN_IN_FAILED)
                }
            }
        }
    }

    private fun getNameFromAuthResult(authResult: AuthResult): String{
        return if(authResult.additionalUserInfo!=null && authResult.additionalUserInfo!!.username!=null){
            authResult.additionalUserInfo!!.username!!
        } else{
            authResult.user!!.email!!.split("@")[0]
        }
    }

    private fun validate(): Boolean{
        val validators = Validators()
        var valid = true
        if(!validators.validateEmail(emailInput.text)){
            emailInput.error = context?.getString(R.string.not_valid_email)
            valid = false
        }
        else{
            emailInput.error = null
        }
        if(!validators.validatePassword(passwordInput.text, parentActivity)){
            passwordInput.error = context?.getString(R.string.not_valid_pass_length)
            valid = false
        }
        else{
            passwordInput.error = null
        }
        return valid
    }

}