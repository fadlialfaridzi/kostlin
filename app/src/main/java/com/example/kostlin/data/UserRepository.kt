package com.example.kostlin.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserRepository(private val context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    fun isUserLoggedIn(): Boolean {
        return prefs.contains("logged_in_email")
    }

    fun getLoggedInUser(): User? {
        val email = prefs.getString("logged_in_email", null) ?: return null
        return getUserByEmail(email)
    }

    fun setLoggedInUser(email: String) {
        prefs.edit().putString("logged_in_email", email).apply()
    }

    fun clearLoggedInUser() {
        prefs.edit().remove("logged_in_email").apply()
    }
    
    fun saveUser(user: User) {
        val usersJson = prefs.getString("users", "[]")
        val type = object : TypeToken<MutableList<User>>() {}.type
        val users: MutableList<User> = gson.fromJson(usersJson, type) ?: mutableListOf()
        
        // Check if user already exists
        val existingIndex = users.indexOfFirst { it.email == user.email }
        if (existingIndex != -1) {
            users[existingIndex] = user
        } else {
            users.add(user)
        }
        
        prefs.edit().putString("users", gson.toJson(users)).apply()
    }
    
    fun getUserByEmail(email: String): User? {
        val usersJson = prefs.getString("users", "[]")
        val type = object : TypeToken<List<User>>() {}.type
        val users: List<User> = gson.fromJson(usersJson, type) ?: emptyList()
        return users.find { it.email == email }
    }
    
    fun validateLogin(email: String, password: String): Boolean {
        val user = getUserByEmail(email)
        return user?.password == password
    }
    
    fun saveOTP(email: String, otp: String) {
        prefs.edit().putString("otp_$email", otp).apply()
    }
    
    fun validateOTP(email: String, otp: String): Boolean {
        val savedOTP = prefs.getString("otp_$email", "")
        return savedOTP == otp
    }
    
    fun updatePassword(email: String, newPassword: String) {
        val user = getUserByEmail(email)
        user?.let {
            saveUser(it.copy(password = newPassword))
        }
    }
}

