package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database

class ProductDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)

        // 1. Get the ID passed from Home
        val productId = arguments?.getInt("productId") ?: -1
        val product = Database.products.find { it.id == productId }

        // 2. Set the UI data
        product?.let {
            view.findViewById<TextView>(R.id.tvDetailName).text = it.name
            view.findViewById<TextView>(R.id.tvDetailPrice).text = "₱${it.price}"
            view.findViewById<TextView>(R.id.tvDetailDescription).text = it.description
            view.findViewById<TextView>(R.id.tvDetailSeller).text = "Seller: ${it.seller}"
        }

        // 3. Haggle Button Logic
        view.findViewById<Button>(R.id.btnHaggle).setOnClickListener {
            Toast.makeText(context, "Hangyo system opening... Propose your price!", Toast.LENGTH_SHORT).show()
            // We can build a custom dialog for this in the next step!
        }

        // 4. Add to Cart Logic (Feature 6: 15-min lock)
        view.findViewById<Button>(R.id.btnAddToCart).setOnClickListener {
            Toast.makeText(context, "Item locked for 15 mins. Check your cart!", Toast.LENGTH_LONG).show()
        }

        return view
    }
}