package com.citu.ukayearn.ui.screens.haggle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database

class HaggleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_haggle, container, false)
        val product = Database.cartItems.firstOrNull()?.product ?: Database.products.first()
        val offerInput = view.findViewById<EditText>(R.id.etOfferAmount)
        val status = view.findViewById<TextView>(R.id.tvHaggleStatus)

        view.findViewById<TextView>(R.id.tvHaggleProductName).text = product.name
        view.findViewById<TextView>(R.id.tvHaggleSeller).text = getString(R.string.seller_format, product.seller)
        view.findViewById<TextView>(R.id.tvHagglePrice).text = getString(R.string.price_format, product.price)

        view.findViewById<TextView>(R.id.btnOfferFive).setOnClickListener {
            offerInput.setText(discountedOffer(product.price, OFFER_FIVE_PERCENT))
        }
        view.findViewById<TextView>(R.id.btnOfferTen).setOnClickListener {
            offerInput.setText(discountedOffer(product.price, OFFER_TEN_PERCENT))
        }
        view.findViewById<TextView>(R.id.btnOfferFifteen).setOnClickListener {
            offerInput.setText(discountedOffer(product.price, OFFER_FIFTEEN_PERCENT))
        }

        view.findViewById<Button>(R.id.btnSendOffer).setOnClickListener {
            val offer = offerInput.text.toString().trim()
            if (offer.isNotEmpty()) {
                status.text = getString(
                    R.string.offer_sent_format,
                    getString(R.string.price_format, offer.toDoubleOrNull() ?: 0.0)
                ) + "\n" + getString(R.string.seller_reviewing_offer)
            }
        }

        return view
    }

    private fun discountedOffer(price: Double, discount: Double): String {
        return Math.round(price * (1 - discount)).toString()
    }

    companion object {
        private const val OFFER_FIVE_PERCENT = 0.05
        private const val OFFER_TEN_PERCENT = 0.10
        private const val OFFER_FIFTEEN_PERCENT = 0.15
    }
}
