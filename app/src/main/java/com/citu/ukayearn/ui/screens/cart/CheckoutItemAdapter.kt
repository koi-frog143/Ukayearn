package com.citu.ukayearn.ui.screens.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database

class CheckoutItemAdapter(
    private val items: List<CheckoutItem>
) : RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_product, parent, false)
        return CheckoutItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        holder.tvName.text = item.product.name
        holder.tvSeller.text = item.product.seller
        holder.tvQuantity.text = context.getString(R.string.checkout_quantity_format, item.quantity)
        holder.tvPrice.text = context.getString(
            R.string.price_format,
            Database.effectiveCartUnitPrice(item.product) * item.quantity
        )
    }

    override fun getItemCount(): Int = items.size

    class CheckoutItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvCheckoutProductName)
        val tvSeller: TextView = itemView.findViewById(R.id.tvCheckoutSeller)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvCheckoutQuantity)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCheckoutPrice)
    }
}
