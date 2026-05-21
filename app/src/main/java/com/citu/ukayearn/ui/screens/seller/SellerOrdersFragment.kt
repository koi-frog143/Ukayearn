package com.citu.ukayearn.ui.screens.seller

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

class SellerOrdersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_seller_orders, container, false)
        refreshOrders(view)
        return view
    }

    private fun refreshOrders(view: View) {
        val rvOrders = view.findViewById<RecyclerView>(R.id.rvSellerOrders)
        val tvEmpty = view.findViewById<TextView>(R.id.tvNoOrders)
        val statusFilter = orderStatusArgument() ?: Database.OrderStatus.PENDING_SHIPMENT

        view.findViewById<TextView>(R.id.tvOrderTitle).text = when (statusFilter) {
            Database.OrderStatus.PENDING_SHIPMENT -> "Orders to Ship"
            Database.OrderStatus.SHIPPED -> "Shipped Orders"
            Database.OrderStatus.COMPLETED -> "Completed Sales"
        }
        tvEmpty.text = when (statusFilter) {
            Database.OrderStatus.PENDING_SHIPMENT -> "No pending orders to ship!"
            Database.OrderStatus.SHIPPED -> "No shipped orders yet."
            Database.OrderStatus.COMPLETED -> "No completed sales yet."
        }

        val orders = Database.orders.filter {
            it.sellerName == Database.currentSellerName() && it.status == statusFilter
        }

        if (orders.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
            rvOrders.layoutManager = LinearLayoutManager(context)
            rvOrders.adapter = SellerOrderAdapter(orders) { order ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Ship Order")
                    .setMessage("Confirm shipment of ${order.product.name} to ${order.buyerUsername}?")
                    .setPositiveButton("Confirm") { _, _ ->
                        Database.markOrderShipped(order.id)
                        Toast.makeText(context, "Order marked as shipped.", Toast.LENGTH_SHORT).show()
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

    inner class SellerOrderAdapter(
        private val orders: List<Database.Order>,
        private val onShipClicked: (Database.Order) -> Unit
    ) : RecyclerView.Adapter<SellerOrderAdapter.OrderViewHolder>() {

        inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivImage: ImageView = view.findViewById(R.id.ivCheckoutProductImage)
            val tvName: TextView = view.findViewById(R.id.tvCheckoutProductName)
            val tvBuyer: TextView = view.findViewById(R.id.tvCheckoutSeller)
            val tvQuantity: TextView = view.findViewById(R.id.tvCheckoutQuantity)
            val tvPrice: TextView = view.findViewById(R.id.tvCheckoutPrice)
            val btnShip: TextView = view.findViewById(R.id.btnReceived)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_product, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            val context = holder.itemView.context

            holder.tvName.text = order.product.name
            holder.tvBuyer.text = "Buyer: @${order.buyerUsername}"
            holder.tvQuantity.text = context.getString(R.string.checkout_quantity_format, order.quantity)
            holder.tvPrice.text = context.getString(R.string.price_format, order.product.price * order.quantity)
            AssetImageLoader.load(holder.ivImage, order.product.imageUrl)

            when (order.status) {
                Database.OrderStatus.PENDING_SHIPMENT -> {
                    holder.btnShip.visibility = View.VISIBLE
                    holder.btnShip.text = "Ship Now"
                    holder.btnShip.setOnClickListener { onShipClicked(order) }
                }
                Database.OrderStatus.SHIPPED -> {
                    holder.btnShip.visibility = View.VISIBLE
                    holder.btnShip.text = "Awaiting buyer"
                    holder.btnShip.setOnClickListener(null)
                }
                Database.OrderStatus.COMPLETED -> {
                    holder.btnShip.visibility = View.GONE
                    holder.btnShip.setOnClickListener(null)
                }
            }
        }

        override fun getItemCount() = orders.size
    }
}
