package com.example.money_flow

import com.google.firebase.auth.FirebaseUser

object UserManager {
    private var currentUser: FirebaseUser? = null

    fun setCurrentUser(user: FirebaseUser) {
        currentUser=user
    }

    fun getCurrentUUser():FirebaseUser?{
        return currentUser
    }
}