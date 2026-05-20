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
import android.widget.ViewFlipper
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.User

class LoginFragment : Fragment() {

    private var isSignupPasswordVisible = false
    private var isSignupConfirmPasswordVisible = false
    private var isLoginPasswordVisible = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val authFlipper = view.findViewById<ViewFlipper>(R.id.authFlipper)

        authFlipper.setInAnimation(context, R.anim.slide_in_right)
        authFlipper.setOutAnimation(context, R.anim.slide_out_left)

        // ----------- SIGN UP VIEWS -----------
        val etSignupName = view.findViewById<EditText>(R.id.etSignupName)
        val etSignupUsername = view.findViewById<EditText>(R.id.etSignupUsername)
        val etSignupPassword = view.findViewById<EditText>(R.id.etSignupPassword)
        val etSignupConfirmPassword = view.findViewById<EditText>(R.id.etSignupConfirmPassword)

        val btnSignupTogglePassword = view.findViewById<ImageButton>(R.id.btnSignupTogglePassword)
        val btnSignupToggleConfirmPassword = view.findViewById<ImageButton>(R.id.btnSignupToggleConfirmPassword)

        val tvSignupError = view.findViewById<TextView>(R.id.tvSignupError)
        val btnSignup = view.findViewById<Button>(R.id.btnSignup)
        val tvSwitchToLogin = view.findViewById<TextView>(R.id.tvSwitchToLogin)

        btnSignupTogglePassword.setOnClickListener {
            isSignupPasswordVisible = !isSignupPasswordVisible
            if (isSignupPasswordVisible) {
                etSignupPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnSignupTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etSignupPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnSignupTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            etSignupPassword.setSelection(etSignupPassword.text.length)
        }

        btnSignupToggleConfirmPassword.setOnClickListener {
            isSignupConfirmPasswordVisible = !isSignupConfirmPasswordVisible
            if (isSignupConfirmPasswordVisible) {
                etSignupConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnSignupToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etSignupConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnSignupToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            etSignupConfirmPassword.setSelection(etSignupConfirmPassword.text.length)
        }

        btnSignup.setOnClickListener {
            val name = etSignupName.text.toString().trim()
            val username = etSignupUsername.text.toString().trim()
            val password = etSignupPassword.text.toString()
            val confirmPassword = etSignupConfirmPassword.text.toString()

            when {
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() -> {
                    tvSignupError.text = getString(R.string.all_fields_required)
                    tvSignupError.visibility = View.VISIBLE
                }
                password != confirmPassword -> {
                    tvSignupError.text = getString(R.string.password_mismatch)
                    tvSignupError.visibility = View.VISIBLE
                }
                Database.users.any { it.username == username } -> {
                    tvSignupError.text = getString(R.string.username_exists)
                    tvSignupError.visibility = View.VISIBLE
                }
                else -> {
                    Database.users.add(User(username = username, name = name, pass = password))
                    Database.currentUsername = username

                    Database.cartItems.clear()
                    Database.chatMessages.clear()
                    Database.haggleOffers.clear()
                    Database.toReceiveItems.clear()

                    tvSignupError.visibility = View.GONE
                    Toast.makeText(context, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()

                    // Simple, safe navigation
                    findNavController().navigate(R.id.action_login_to_home)
                }
            }
        }

        tvSwitchToLogin.setOnClickListener {
            authFlipper.setInAnimation(context, R.anim.slide_in_right)
            authFlipper.setOutAnimation(context, R.anim.slide_out_left)
            authFlipper.displayedChild = 1
        }


        // ----------- LOG IN VIEWS -----------
        val etLoginUsername = view.findViewById<EditText>(R.id.etLoginUsername)
        val etLoginPassword = view.findViewById<EditText>(R.id.etLoginPassword)
        val btnLoginTogglePassword = view.findViewById<ImageButton>(R.id.btnLoginTogglePassword)

        val tvLoginError = view.findViewById<TextView>(R.id.tvLoginError)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val tvSwitchToSignup = view.findViewById<TextView>(R.id.tvSwitchToSignup)

        btnLoginTogglePassword.setOnClickListener {
            isLoginPasswordVisible = !isLoginPasswordVisible
            if (isLoginPasswordVisible) {
                etLoginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                btnLoginTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnLoginTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            etLoginPassword.setSelection(etLoginPassword.text.length)
        }

        btnLogin.setOnClickListener {
            val user = etLoginUsername.text.toString()
            val pass = etLoginPassword.text.toString()

            val isValid = Database.users.any { it.username == user && it.pass == pass }

            if (isValid) {
                Database.currentUsername = user
                tvLoginError.visibility = View.GONE
                // Simple, safe navigation
                findNavController().navigate(R.id.action_login_to_home)
            } else {
                tvLoginError.visibility = View.VISIBLE
            }
        }

        tvSwitchToSignup.setOnClickListener {
            authFlipper.setInAnimation(context, R.anim.slide_in_left)
            authFlipper.setOutAnimation(context, R.anim.slide_out_right)
            authFlipper.displayedChild = 0
        }

        return view
    }
}