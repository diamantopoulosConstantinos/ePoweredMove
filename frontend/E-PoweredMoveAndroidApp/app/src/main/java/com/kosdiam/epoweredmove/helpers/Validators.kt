package com.kosdiam.epoweredmove.helpers

import android.content.Context
import android.text.Editable
import android.util.Patterns
import com.kosdiam.epoweredmove.R

class Validators {

    fun validateName(username: Editable, context: Context): Boolean {
        if(username.isBlank() || username.length > context.resources.getInteger(R.integer.name_max_length)){
            return false
        }
        return true
    }

    fun validateText(text: Editable, context: Context): Boolean {
        if(text.isBlank() || text.length > context.resources.getInteger(R.integer.text_max_length)){
            return false
        }
        return true
    }

    fun validateEmail(email: Editable): Boolean {
        if(email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }
        return true
    }

    fun validatePhone(phone: Editable): Boolean {
        if(phone.isBlank() || !Patterns.PHONE.matcher(phone).matches()){
            return false
        }
        return true
    }

    fun validatePassword(password: Editable, context: Context): Boolean {
        if(password.isBlank() || password.length < context.resources.getInteger(R.integer.password_min_length) || password.length > context.resources.getInteger(R.integer.password_max_length)){
            return false
        }
        return true
    }

    fun confirmPasswords(password0: Editable, password1: Editable): Boolean {
        if(password0.toString() != password1.toString()){
            return false
        }
        return true
    }


}