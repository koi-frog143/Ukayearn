package com.citu.ukayearn.ui.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvError = view.findViewById<TextView>(R.id.tvError)

        btnLogin.setOnClickListener {
            val user = etUsername.text.toString()
            val pass = etPassword.text.toString()

            // Check against our hardcoded Database!
            val isValid = Database.users.any { it.username == user && it.pass == pass }

            if (isValid) {
                tvError.visibility = View.GONE
                findNavController().navigate(R.id.action_login_to_home)
            } else {
                tvError.visibility = View.VISIBLE
            }
        }
        return view
    }
}