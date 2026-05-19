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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.User

class SignupFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        val btnTogglePassword = view.findViewById<ImageButton>(R.id.btnTogglePassword)
        val btnToggleConfirmPassword = view.findViewById<ImageButton>(R.id.btnToggleConfirmPassword)
        val btnSignup = view.findViewById<Button>(R.id.btnSignup)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val tvLogin = view.findViewById<TextView>(R.id.tvLogin)

        var isPasswordVisible = false
        var isConfirmPasswordVisible = false

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

        // Toggle confirm password visibility
        btnToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            if (isConfirmPasswordVisible) {
                etConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }

        btnSignup.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            when {
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    tvError.text = getString(R.string.all_fields_required)
                    tvError.visibility = View.VISIBLE
                }
                password != confirmPassword -> {
                    tvError.text = getString(R.string.password_mismatch)
                    tvError.visibility = View.VISIBLE
                }
                Database.users.any { it.username == username } -> {
                    tvError.text = getString(R.string.username_exists)
                    tvError.visibility = View.VISIBLE
                }
                else -> {
                    // Add new user to database
                    Database.users.add(User(username, password))
                    Database.currentUsername = username
                    tvError.visibility = View.GONE
                    Toast.makeText(context, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                    // Navigate back to login
                    findNavController().navigate(R.id.action_signup_to_login)
                }
            }
        }

        tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }
}
