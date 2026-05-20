package com.citu.ukayearn.ui.screens.thrifts

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

class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Reusing the seller orders XML since the layout is identical (Title + List)
        val view = inflater.inflate(R.layout.fragment_seller_orders, container, false)
        view.findViewById<TextView>(R.id.tvOrderTitle).text = "My Purchases"
        refreshOrders(view)
        return view
    }

    private fun refreshOrders(view: View) {
        val rvOrders = view.findViewById<RecyclerView>(R.id.rvSellerOrders)
        val tvEmpty = view.findViewById<TextView>(R.id.tvNoOrders)
        tvEmpty.text = "You haven't made any purchases yet!"

        val myOrders = Database.orders.filter { it.buyerUsername == Database.currentUsername }

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
                        Toast.makeText(context, "Order Complete!", Toast.LENGTH_SHORT).show()
                        refreshOrders(view)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    inner class BuyerOrderAdapter(
        private val orders: List<Database.Order>,
        private val onReceiveClicked: (Database.Order) -> Unit
    ) : RecyclerView.Adapter<BuyerOrderAdapter.OrderViewHolder>() {

        inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivImage: ImageView = view.findViewById(R.id.ivCheckoutProductImage)
            val tvName: TextView = view.findViewById(R.id.tvCheckoutProductName)
            val tvStatus: TextView = view.findViewById(R.id.tvCheckoutSeller)
            val tvPrice: TextView = view.findViewById(R.id.tvCheckoutPrice)
            val btnReceive: Button = view.findViewById(R.id.btnReceived)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_product, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            holder.tvName.text = order.product.name

            val totalSpent = order.product.price * order.quantity
            holder.tvPrice.text = "Total Paid: ₱${"%.2f".format(totalSpent)}"

            AssetImageLoader.load(holder.ivImage, order.product.imageUrl)

            when (order.status) {
                Database.OrderStatus.PENDING_SHIPMENT -> {
                    holder.tvStatus.text = "Status: Pending Shipment"
                    holder.btnReceive.visibility = View.GONE
                }
                Database.OrderStatus.SHIPPED -> {
                    holder.tvStatus.text = "Status: Shipped (To Receive)"
                    holder.btnReceive.visibility = View.VISIBLE
                    holder.btnReceive.text = "Order Received"
                    holder.btnReceive.setOnClickListener { onReceiveClicked(order) }
                }
                Database.OrderStatus.COMPLETED -> {
                    holder.tvStatus.text = "Status: Completed"
                    holder.btnReceive.visibility = View.GONE
                }
            }
        }
        override fun getItemCount() = orders.size
    }
}