package com.example.kaamwala.data

/**
 * Singleton Session Manager to store user session details in memory.
 */
object SessionManager {
    var token: String? = null
    var userRole: String? = null
    var userName: String? = null
    var userPhone: String? = null
    var userCity: String = "Kanpur" // Default selected city
}
