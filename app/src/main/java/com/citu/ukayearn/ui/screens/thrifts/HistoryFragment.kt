package com.citu.ukayearn.ui.screens.thrifts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.ui.screens.profile.ProfileBottomSheetFragment
import com.citu.ukayearn.ui.util.AssetImageLoader

class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_seller_orders, container, false)
        refreshOrders(view)
        return view
    }

    private fun refreshOrders(view: View) {
        val rvOrders = view.findViewById<RecyclerView>(R.id.rvSellerOrders)
        val tvEmpty = view.findViewById<TextView>(R.id.tvNoOrders)
        val statusFilter = orderStatusArgument()

        view.findViewById<TextView>(R.id.tvOrderTitle).text = when (statusFilter) {
            Database.OrderStatus.PENDING_SHIPMENT -> "To Ship"
            Database.OrderStatus.SHIPPED -> "To Receive"
            Database.OrderStatus.COMPLETED -> "Completed"
            null -> "My Purchases"
        }
        tvEmpty.text = when (statusFilter) {
            Database.OrderStatus.PENDING_SHIPMENT -> "No orders waiting for shipment."
            Database.OrderStatus.SHIPPED -> "No orders to receive."
            Database.OrderStatus.COMPLETED -> "No completed orders yet."
            null -> "You haven't made any purchases yet!"
        }

        val myOrders = Database.orders
            .filter { it.buyerUsername == Database.currentUsername }
            .filter { statusFilter == null || it.status == statusFilter }

        if (myOrders.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
            rvOrders.layoutManager = LinearLayoutManager(context)
            rvOrders.adapter = BuyerOrderAdapter(myOrders) { order ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Order Received")
                    .setMessage("Confirm you have received ${order.product.name}?")
                    .setPositiveButton("Confirm") { _, _ ->
                        Database.markOrderCompleted(order.id)
                        Database.sendReceivedPromptToSeller(
                            order.product.seller,
                            getString(R.string.item_received_prompt, order.product.name)
                        )
                        Toast.makeText(context, "Order complete.", Toast.LENGTH_SHORT).show()
                        refreshOrders(view)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun orderStatusArgument(): Database.OrderStatus? {
        val statusName = arguments?.getString(ProfileBottomSheetFragment.ORDER_STATUS_ARG) ?: return null
        return runCatching { Database.OrderStatus.valueOf(statusName) }.getOrNull()
    }

    inner class BuyerOrderAdapter(
        private val orders: List<Database.Order>,
        private val onReceiveClicked: (Database.Order) -> Unit
    ) : RecyclerView.Adapter<BuyerOrderAdapter.OrderViewHolder>() {

        inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivImage: ImageView = view.findViewById(R.id.ivCheckoutProductImage)
            val tvName: TextView = view.findViewById(R.id.tvCheckoutProductName)
            val tvStatus: TextView = view.findViewById(R.id.tvCheckoutSeller)
            val tvQuantity: TextView = view.findViewById(R.id.tvCheckoutQuantity)
            val tvPrice: TextView = view.findViewById(R.id.tvCheckoutPrice)
            val btnReceive: TextView = view.findViewById(R.id.btnReceived)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_product, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            val context = holder.itemView.context
            val totalSpent = order.product.price * order.quantity

            holder.tvName.text = order.product.name
            holder.tvQuantity.text = context.getString(R.string.checkout_quantity_format, order.quantity)
            holder.tvPrice.text = context.getString(R.string.price_format, totalSpent)
            AssetImageLoader.load(holder.ivImage, order.product.imageUrl)

            when (order.status) {
                Database.OrderStatus.PENDING_SHIPMENT -> {
                    holder.tvStatus.text = "Status: Pending shipment"
                    holder.btnReceive.visibility = View.GONE
                    holder.btnReceive.setOnClickListener(null)
                }
                Database.OrderStatus.SHIPPED -> {
                    holder.tvStatus.text = "Status: Shipped"
                    holder.btnReceive.visibility = View.VISIBLE
                    holder.btnReceive.text = getString(R.string.received)
                    holder.btnReceive.setOnClickListener { onReceiveClicked(order) }
                }
                Database.OrderStatus.COMPLETED -> {
                    holder.tvStatus.text = "Status: Completed"
                    holder.btnReceive.visibility = View.GONE
                    holder.btnReceive.setOnClickListener(null)
                }
            }
        }

        override fun getItemCount() = orders.size
    }
}
