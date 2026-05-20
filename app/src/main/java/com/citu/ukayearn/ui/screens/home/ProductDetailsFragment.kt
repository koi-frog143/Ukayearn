package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.CartItem
import com.citu.ukayearn.ui.util.AssetImageLoader

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
            AssetImageLoader.load(view.findViewById<ImageView>(R.id.ivDetailImage), it.imageUrl)
        }

        view.findViewById<Button>(R.id.btnHaggle).setOnClickListener {
            product?.let { currentProduct ->
                // Automatically add to cart if not already there
                val existingItem = Database.cartItems.find { it.product.id == currentProduct.id }
                if (existingItem == null) {
                    Database.cartItems.add(CartItem(currentProduct, 1))
                    Toast.makeText(context, "${currentProduct.name} added to cart for Haggle", Toast.LENGTH_SHORT).show()
                }

                // Redirect to Haggle section
                findNavController().navigate(R.id.nav_haggle)
            }
        }

        view.findViewById<Button>(R.id.btnAddToCart).setOnClickListener {
            product?.let { currentProduct ->
                val existingItem = Database.cartItems.find { it.product.id == currentProduct.id }
                if (existingItem != null) {
                    // Limit adding beyond stock
                    if (existingItem.quantity < currentProduct.stock) {
                        existingItem.quantity++
                        Toast.makeText(context, "${currentProduct.name} quantity increased to ${existingItem.quantity}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Maximum stock reached", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (currentProduct.stock > 0) {
                        Database.cartItems.add(CartItem(currentProduct, 1))
                        Toast.makeText(context, "${currentProduct.name} added to cart", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Item is out of stock", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    companion object {
        const val ARG_PRODUCT_ID = "productId"
    }
}