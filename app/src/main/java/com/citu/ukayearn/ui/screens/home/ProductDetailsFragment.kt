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

        val productId = arguments?.getInt(ARG_PRODUCT_ID) ?: -1
        val product = Database.products.find { it.id == productId }

        product?.let {
            view.findViewById<TextView>(R.id.tvDetailName).text = it.name
            view.findViewById<TextView>(R.id.tvDetailPrice).text = getString(R.string.price_format, it.price)
            view.findViewById<TextView>(R.id.tvDetailDescription).text = it.description
            view.findViewById<TextView>(R.id.tvDetailSeller).text = getString(R.string.seller_format, it.seller)
        }

        view.findViewById<Button>(R.id.btnHaggle).setOnClickListener {
            Toast.makeText(context, R.string.haggle_opening_message, Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btnAddToCart).setOnClickListener {
            Toast.makeText(context, R.string.item_locked_message, Toast.LENGTH_LONG).show()
        }

        return view
    }

    companion object {
        const val ARG_PRODUCT_ID = "productId"
    }
}
