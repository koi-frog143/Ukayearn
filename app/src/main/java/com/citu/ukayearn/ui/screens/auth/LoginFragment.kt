package com.citu.ukayearn.ui.screens.auth

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
        val btnTogglePassword = view.findViewById<ImageButton>(R.id.btnTogglePassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val tvSignup = view.findViewById<TextView>(R.id.tvSignup)

        var isPasswordVisible = false

        // Toggle password visibility
        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            etPassword.setSelection(etPassword.text.length)
        }

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

        tvSignup.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        return view
    }
}