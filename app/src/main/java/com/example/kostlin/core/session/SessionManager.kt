package com.example.kostlin.core.session

/**
 * Simple session manager to store current user info
 * Used for checking if user is owner of kos before booking
 */
object SessionManager {
    var userId: Int? = null
        private set
    
    var userName: String? = null
        private set
    
    var userEmail: String? = null
        private set
    
    fun setUser(id: Int, name: String, email: String) {
        userId = id
        userName = name
        userEmail = email
    }
    
    fun clear() {
        userId = null
        userName = null
        userEmail = null
    }
    
    fun isLoggedIn(): Boolean = userId != null
}
