package com.citu.ukayearn.ui.screens.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.CartItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CheckoutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val checkoutItems = CheckoutDraft.items
        val subtotal = checkoutItems.sumOf { Database.calculateItemTotal(it.product, it.quantity) }
        val delivery = if (checkoutItems.isNotEmpty()) Database.deliveryFee else 0.0
        val buyerProtection = if (checkoutItems.isNotEmpty()) Database.buyerProtectionFee else 0.0
        val total = subtotal + delivery + buyerProtection

        view.findViewById<RecyclerView>(R.id.rvCheckoutItems).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CheckoutItemAdapter(checkoutItems)
        }

        view.findViewById<TextView>(R.id.tvCheckoutSubtotal).text =
            getString(R.string.price_format, subtotal)
        view.findViewById<TextView>(R.id.tvCheckoutDelivery).text =
            getString(R.string.price_format, delivery)
        view.findViewById<TextView>(R.id.tvCheckoutProtection).text =
            getString(R.string.price_format, buyerProtection)
        view.findViewById<TextView>(R.id.tvCheckoutTotal).text =
            getString(R.string.price_format, total)
        view.findViewById<TextView>(R.id.tvEstimatedDelivery).text =
            getString(R.string.estimated_delivery_format, estimatedDeliveryDate())

        val addressInput = view.findViewById<EditText>(R.id.etDeliveryAddress)
        val phoneInput = view.findViewById<EditText>(R.id.etPhoneNumber)
        val landmarkInput = view.findViewById<EditText>(R.id.etLandmark)
        Database.currentDeliveryDetails()?.let { details ->
            addressInput.setText(details.address)
            phoneInput.setText(details.phone)
            landmarkInput.setText(details.landmark)
        }

        view.findViewById<Button>(R.id.btnPlaceOrder).setOnClickListener {
            val address = addressInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val landmark = landmarkInput.text.toString().trim()

            if (address.isEmpty() || phone.isEmpty() || landmark.isEmpty()) {
                Toast.makeText(requireContext(), R.string.delivery_details_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Database.currentDeliveryDetails() == null) {
                askToSaveAddressThenPlaceOrder(address, phone, landmark)
            } else {
                placeOrder()
            }
        }
    }

    private fun askToSaveAddressThenPlaceOrder(address: String, phone: String, landmark: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.save_checkout_address_title)
            .setMessage(R.string.save_checkout_address_message)
            .setPositiveButton(R.string.save_address) { _, _ ->
                Database.saveCurrentDeliveryDetails(address, phone, landmark)
                placeOrder()
            }
            .setNegativeButton(R.string.not_now) { _, _ ->
                placeOrder()
            }
            .show()
    }

    private fun placeOrder() {
        Database.placeOrder(CheckoutDraft.items.map {
            CartItem(product = it.product, quantity = it.quantity)
        })
        CheckoutDraft.items = emptyList()
        Toast.makeText(requireContext(), R.string.order_placed_cod, Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.nav_cart)
    }

    private fun estimatedDeliveryDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, ESTIMATED_DELIVERY_DAYS)
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendar.time)
    }

    companion object {
        private const val ESTIMATED_DELIVERY_DAYS = 3
        private const val DATE_FORMAT = "MMM d, yyyy"
    }
}
