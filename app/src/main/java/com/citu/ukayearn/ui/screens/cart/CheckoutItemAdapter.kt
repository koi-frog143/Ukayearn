package com.citu.ukayearn.ui.screens.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.ui.util.AssetImageLoader

class CheckoutItemAdapter(
    private val items: List<CheckoutItem>,
    private val showReceivedAction: Boolean = false,
    private val onReceivedClicked: ((CheckoutItem) -> Unit)? = null
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
        AssetImageLoader.load(holder.ivImage, item.product.imageUrl)
        holder.tvPrice.text = context.getString(
            R.string.price_format,
            Database.calculateItemTotal(item.product, item.quantity)
        )
        holder.btnReceived.visibility = if (showReceivedAction) View.VISIBLE else View.GONE
        holder.btnReceived.setOnClickListener {
            onReceivedClicked?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class CheckoutItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivCheckoutProductImage)
        val tvName: TextView = itemView.findViewById(R.id.tvCheckoutProductName)
        val tvSeller: TextView = itemView.findViewById(R.id.tvCheckoutSeller)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvCheckoutQuantity)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCheckoutPrice)
        val btnReceived: TextView = itemView.findViewById(R.id.btnReceived)
    }
}
