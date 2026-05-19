package com.citu.ukayearn.ui.screens.cart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.ui.screens.home.ProductDetailsFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartFragment : Fragment() {
    private val clockHandler = Handler(Looper.getMainLooper())
    private var clockRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val cartItems = Database.cartItems

        bindTotals(view)
        startClock(view.findViewById(R.id.tvCartClock))

        view.findViewById<RecyclerView>(R.id.rvCartItems).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CartItemAdapter(
                items = cartItems,
                onQuantityChanged = { bindTotals(view) },
                onItemClicked = { product -> openProductDetails(product) }
            )
        }

        view.findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            Toast.makeText(context, R.string.checkout_coming_soon, Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onDestroyView() {
        clockRunnable?.let(clockHandler::removeCallbacks)
        clockRunnable = null
        super.onDestroyView()
    }

    private fun bindTotals(view: View) {
        val cartItems = Database.cartItems
        val subtotal = cartItems.sumOf { it.product.price * it.quantity }
        val total = subtotal + Database.buyerProtectionFee + Database.deliveryFee

        view.findViewById<TextView>(R.id.tvLockedFinds).text =
            resources.getQuantityString(R.plurals.locked_finds_count, cartItems.size, cartItems.size)
        view.findViewById<TextView>(R.id.tvSubtotal).text = getString(R.string.price_format, subtotal)
        view.findViewById<TextView>(R.id.tvBuyerProtection).text =
            getString(R.string.price_format, Database.buyerProtectionFee)
        view.findViewById<TextView>(R.id.tvDelivery).text =
            getString(R.string.price_format, Database.deliveryFee)
        view.findViewById<TextView>(R.id.tvTotal).text = getString(R.string.price_format, total)
    }

    private fun startClock(clockView: TextView) {
        val formatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

        clockRunnable = object : Runnable {
            override fun run() {
                clockView.text = formatter.format(Date())
                clockHandler.postDelayed(this, CLOCK_REFRESH_MS)
            }
        }
        clockRunnable?.run()
    }

    private fun openProductDetails(product: Product) {
        val bundle = Bundle()
        bundle.putInt(ProductDetailsFragment.ARG_PRODUCT_ID, product.id)
        findNavController().navigate(R.id.action_cart_to_details, bundle)
    }

    companion object {
        private const val CLOCK_REFRESH_MS = 1000L
        private const val TIME_FORMAT = "h:mm a"
    }
}
