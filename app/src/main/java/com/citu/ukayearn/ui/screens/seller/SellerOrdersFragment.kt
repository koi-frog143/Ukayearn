package com.citu.ukayearn.ui.screens.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
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

        // Only fetch orders that this seller needs to ship
        val pendingOrders = Database.orders.filter {
            it.sellerName == Database.currentSellerName() && it.status == Database.OrderStatus.PENDING_SHIPMENT
        }

        if (pendingOrders.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
            rvOrders.layoutManager = LinearLayoutManager(context)
            rvOrders.adapter = SellerOrderAdapter(pendingOrders) { order ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Ship Order")
                    .setMessage("Confirm shipment of ${order.product.name} to ${order.buyerUsername}?")
                    .setPositiveButton("Confirm") { _, _ ->
                        Database.markOrderShipped(order.id)
                        Toast.makeText(context, "Order marked as SHIPPED!", Toast.LENGTH_SHORT).show()
                        refreshOrders(view) // Refresh list
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    inner class SellerOrderAdapter(
        private val orders: List<Database.Order>,
        private val onShipClicked: (Database.Order) -> Unit
    ) : RecyclerView.Adapter<SellerOrderAdapter.OrderViewHolder>() {

        inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivImage: ImageView = view.findViewById(R.id.ivCheckoutProductImage)
            val tvName: TextView = view.findViewById(R.id.tvCheckoutProductName)
            val tvBuyer: TextView = view.findViewById(R.id.tvCheckoutSeller)
            val tvPrice: TextView = view.findViewById(R.id.tvCheckoutPrice)
            // btnReceived is implemented as a clickable TextView in the layout
            val btnShip: TextView = view.findViewById(R.id.btnReceived)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_product, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            holder.tvName.text = order.product.name
            holder.tvBuyer.text = "Buyer: @${order.buyerUsername}"
            holder.tvPrice.text = "₱${order.product.price}"
            AssetImageLoader.load(holder.ivImage, order.product.imageUrl)

            holder.btnShip.visibility = View.VISIBLE
            holder.btnShip.text = "Ship Now"
            holder.btnShip.setOnClickListener { onShipClicked(order) }
        }
        override fun getItemCount() = orders.size
    }
}