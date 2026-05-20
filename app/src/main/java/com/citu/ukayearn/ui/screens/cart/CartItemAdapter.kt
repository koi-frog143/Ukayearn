package com.citu.ukayearn.ui.screens.cart

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.ui.util.AssetImageLoader

data class CartUiItem(
    val product: Product,
    var quantity: Int,
    var isSelected: Boolean = true
)

class CartItemAdapter(
    private val items: MutableList<CartUiItem>,
    private val onCartChanged: () -> Unit,
    private val onItemClicked: (Product) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.cbSelected.setOnCheckedChangeListener(null)
        holder.cbSelected.isChecked = item.isSelected
        holder.tvSeller.text = item.product.seller
        holder.tvName.text = item.product.name
        holder.tvDescription.text = item.product.description
        AssetImageLoader.load(holder.ivImage, item.product.imageUrl)

        // Calculate totals using our new restricted logic
        val effectivePrice = Database.effectiveCartUnitPrice(item.product)
        val lineTotal = Database.calculateItemTotal(item.product, item.quantity)

        holder.tvPrice.text = if (effectivePrice < item.product.price) {
            context.getString(
                R.string.cart_haggle_price_format,
                context.getString(R.string.price_format, effectivePrice)
            )
        } else {
            context.getString(R.string.price_format, item.product.price)
        }
        holder.tvQuantity.text = item.quantity.toString()

        // Use the new restricted line total
        holder.tvLineTotal.text = context.getString(
            R.string.cart_line_total_format,
            context.getString(R.string.price_format, lineTotal)
        )

        holder.cbSelected.setOnCheckedChangeListener { _, isChecked ->
            item.isSelected = isChecked
            onCartChanged()
        }

        holder.productContent.setOnClickListener {
            onItemClicked(item.product)
        }

        holder.btnDecreaseQuantity.setOnClickListener {
            if (item.quantity > MIN_QUANTITY) {
                item.quantity -= 1
                notifyChangedPosition(holder)
                onCartChanged()
            }
        }

        holder.btnIncreaseQuantity.setOnClickListener {
            // Check against product stock limit
            if (item.quantity < item.product.stock) {
                item.quantity += 1
                notifyChangedPosition(holder)
                onCartChanged()
            } else {
                Toast.makeText(context, "Maximum stock reached", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnDelete.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val dialogView = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_confirm_delete, null)
                val dialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                dialogView.findViewById<TextView>(R.id.tvDeleteMessage).text =
                    context.getString(R.string.delete_item_message)

                dialogView.findViewById<View>(R.id.btnDeleteCancel).setOnClickListener {
                    dialog.dismiss()
                }

                dialogView.findViewById<View>(R.id.btnDeleteConfirm).setOnClickListener {
                    items.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    onCartChanged()
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun selectAll(isSelected: Boolean) {
        items.forEach { it.isSelected = isSelected }
        notifyDataSetChanged()
        onCartChanged()
    }

    fun selectedItems(): List<CartUiItem> = items.filter { it.isSelected }

    private fun notifyChangedPosition(holder: CartItemViewHolder) {
        val position = holder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            notifyItemChanged(position)
        }
    }

    class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbSelected: CheckBox = itemView.findViewById(R.id.cbCartSelected)
        val productContent: View = itemView.findViewById(R.id.productContent)
        val ivImage: ImageView = itemView.findViewById(R.id.ivCartProductImage)
        val tvSeller: TextView = itemView.findViewById(R.id.tvCartSeller)
        val tvName: TextView = itemView.findViewById(R.id.tvCartProductName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvCartProductDescription)
        val tvPrice: TextView = itemView.findViewById(R.id.tvCartPrice)
        val tvLineTotal: TextView = itemView.findViewById(R.id.tvCartLineTotal)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvCartQuantity)
        val btnDecreaseQuantity: TextView = itemView.findViewById(R.id.btnDecreaseQuantity)
        val btnIncreaseQuantity: TextView = itemView.findViewById(R.id.btnIncreaseQuantity)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteCartItem)
    }

    companion object {
        private const val MIN_QUANTITY = 1
    }
}