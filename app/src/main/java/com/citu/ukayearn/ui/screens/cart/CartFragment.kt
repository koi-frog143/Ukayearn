package com.citu.ukayearn.ui.screens.cart

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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
    private lateinit var cartAdapter: CartItemAdapter
    private lateinit var cartItems: MutableList<CartUiItem>
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        rootView = view
        cartItems = if (Database.isCurrentUserSeller()) {
            mutableListOf()
        } else {
            Database.cartItems.map {
                CartUiItem(
                    product = it.product,
                    quantity = it.quantity,
                    isSelected = true
                )
            }.toMutableList()
        }

        cartAdapter = CartItemAdapter(
            items = cartItems,
            onCartChanged = { bindTotals(view) },
            onItemClicked = { product -> openProductDetails(product) }
        )

        startClock(view.findViewById(R.id.tvCartClock))

        view.findViewById<RecyclerView>(R.id.rvCartItems).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        view.findViewById<RecyclerView>(R.id.rvToReceiveItems).layoutManager =
            LinearLayoutManager(requireContext())
        bindToReceiveItems()
        bindCartTabs(showToReceive = false)

        view.findViewById<TextView>(R.id.tabMyCart).setOnClickListener {
            bindCartTabs(showToReceive = false)
        }
        view.findViewById<TextView>(R.id.tabToReceive).setOnClickListener {
            bindCartTabs(showToReceive = true)
        }

        view.findViewById<CheckBox>(R.id.cbSelectAll).setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAll(isChecked)
        }

        view.findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            val selectedItems = cartAdapter.selectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(context, R.string.select_items_before_checkout, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CheckoutDraft.items = selectedItems.map {
                CheckoutItem(product = it.product, quantity = it.quantity)
            }
            findNavController().navigate(R.id.action_cart_to_checkout)
        }

        bindTotals(view)
        return view
    }

    override fun onDestroyView() {
        clockRunnable?.let(clockHandler::removeCallbacks)
        clockRunnable = null
        super.onDestroyView()
    }

    private fun bindTotals(view: View) {
        val selectedItems = if (::cartAdapter.isInitialized) cartAdapter.selectedItems() else emptyList()
        val subtotal = selectedItems.sumOf { Database.calculateItemTotal(it.product, it.quantity) }
        val hasSelectedItems = selectedItems.isNotEmpty()
        val buyerProtection = if (hasSelectedItems) Database.buyerProtectionFee else 0.0
        val delivery = if (hasSelectedItems) Database.deliveryFee else 0.0
        val total = subtotal + buyerProtection + delivery
        val selectAll = view.findViewById<CheckBox>(R.id.cbSelectAll)

        selectAll.setOnCheckedChangeListener(null)
        selectAll.isChecked = cartItems.isNotEmpty() && selectedItems.size == cartItems.size
        selectAll.setOnCheckedChangeListener { _, isChecked ->
            cartAdapter.selectAll(isChecked)
        }

        view.findViewById<TextView>(R.id.tvLockedFinds).text =
            getString(R.string.cart_selected_count_format, selectedItems.size, cartItems.size)
        view.findViewById<TextView>(R.id.tvSelectedCountBadge).text =
            getString(R.string.selected_count_format, selectedItems.size)
        view.findViewById<TextView>(R.id.tvSubtotal).text = getString(R.string.price_format, subtotal)
        view.findViewById<TextView>(R.id.tvBuyerProtection).text =
            getString(R.string.price_format, buyerProtection)
        view.findViewById<TextView>(R.id.tvDelivery).text =
            getString(R.string.price_format, delivery)
        view.findViewById<TextView>(R.id.tvTotal).text = getString(R.string.price_format, total)
        view.findViewById<Button>(R.id.btnCheckout).isEnabled = hasSelectedItems
    }

    private fun bindToReceiveItems() {
        val toReceive = Database.toReceiveItems.map {
            CheckoutItem(product = it.product, quantity = it.quantity)
        }
        rootView.findViewById<RecyclerView>(R.id.rvToReceiveItems).adapter =
            CheckoutItemAdapter(
                items = toReceive,
                showReceivedAction = true,
                onReceivedClicked = { item ->
                    val prompt = getString(R.string.item_received_prompt, item.product.name)
                    Database.markToReceiveItemReceived(item.product.id)
                    Database.sendReceivedPromptToSeller(item.product.seller, prompt)
                    Toast.makeText(
                        requireContext(),
                        prompt,
                        Toast.LENGTH_LONG
                    ).show()
                    Toast.makeText(requireContext(), R.string.item_marked_received, Toast.LENGTH_SHORT).show()
                    bindToReceiveItems()
                }
            )
        rootView.findViewById<TextView>(R.id.tvToReceiveEmpty).visibility =
            if (toReceive.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun bindCartTabs(showToReceive: Boolean) {
        val cartVisibility = if (showToReceive) View.GONE else View.VISIBLE
        val receiveVisibility = if (showToReceive) View.VISIBLE else View.GONE

        rootView.findViewById<View>(R.id.cartNoticeRow).visibility = cartVisibility
        rootView.findViewById<View>(R.id.rvCartItems).visibility = cartVisibility
        rootView.findViewById<View>(R.id.voucherRow).visibility = cartVisibility
        rootView.findViewById<View>(R.id.orderSummaryCard).visibility = cartVisibility
        rootView.findViewById<View>(R.id.checkoutBar).visibility = cartVisibility

        rootView.findViewById<View>(R.id.tvToReceiveHeader).visibility = receiveVisibility
        rootView.findViewById<View>(R.id.rvToReceiveItems).visibility = receiveVisibility
        rootView.findViewById<View>(R.id.tvToReceiveEmpty).visibility =
            if (showToReceive && Database.toReceiveItems.isEmpty()) View.VISIBLE else View.GONE

        rootView.findViewById<TextView>(R.id.tabMyCart).apply {
            setBackgroundResource(if (showToReceive) android.R.color.transparent else R.drawable.premium_badge_bg)
            setTextColor(requireContext().getColor(if (showToReceive) R.color.text_muted else R.color.accent_gold))
        }
        rootView.findViewById<TextView>(R.id.tabToReceive).apply {
            setBackgroundResource(if (showToReceive) R.drawable.premium_badge_bg else android.R.color.transparent)
            setTextColor(requireContext().getColor(if (showToReceive) R.color.accent_gold else R.color.text_muted))
        }
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
