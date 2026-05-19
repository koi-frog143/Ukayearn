package com.citu.ukayearn.ui.screens.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.models.CartItem
import com.citu.ukayearn.data.models.Product

class CartItemAdapter(
    private val items: List<CartItem>,
    private val onQuantityChanged: () -> Unit,
    private val onItemClicked: (Product) -> Unit
) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.tvSeller.text = item.product.seller
        holder.tvName.text = item.product.name
        holder.tvDescription.text = item.product.description
        holder.tvPrice.text = context.getString(R.string.price_format, item.product.price)
        holder.tvQuantity.text = item.quantity.toString()

        holder.itemView.setOnClickListener {
            onItemClicked(item.product)
        }

        holder.btnDecreaseQuantity.setOnClickListener {
            if (item.quantity > MIN_QUANTITY) {
                item.quantity -= 1
                notifyChangedPosition(holder)
                onQuantityChanged()
            }
        }

        holder.btnIncreaseQuantity.setOnClickListener {
            item.quantity += 1
            notifyChangedPosition(holder)
            onQuantityChanged()
        }
    }

    override fun getItemCount(): Int = items.size

    private fun notifyChangedPosition(holder: CartItemViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position)
        }
    }

    class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSeller: TextView = itemView.findViewById(R.id.tvCartSeller)
        val tvName: TextView = itemView.findViewById(R.id.tvCartProductName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvCartProductDescription)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvCartQuantity)
        val btnDecreaseQuantity: TextView = itemView.findViewById(R.id.btnDecreaseQuantity)
        val btnIncreaseQuantity: TextView = itemView.findViewById(R.id.btnIncreaseQuantity)
    }

    companion object {
        private const val MIN_QUANTITY = 1
    }
}
