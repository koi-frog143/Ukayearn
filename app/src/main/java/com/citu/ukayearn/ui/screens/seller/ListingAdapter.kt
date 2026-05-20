package com.citu.ukayearn.ui.screens.seller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.ui.util.AssetImageLoader

class ListingAdapter(
    private var products: List<Product>,
    private val onMarkSoldOut: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seller_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context

        holder.tvName.text = product.name
        holder.tvPrice.text = context.getString(R.string.price_format, product.price)
        holder.tvStock.text = if (product.stock > 0) "Stock: ${product.stock}" else "Out of Stock"
        AssetImageLoader.load(holder.ivImage, product.imageUrl)

        // Disable Sold Out button if already out of stock
        holder.btnSoldOut.isEnabled = product.stock > 0
        holder.btnSoldOut.alpha = if (product.stock > 0) 1.0f else 0.5f

        holder.btnSoldOut.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Mark as Sold Out")
                .setMessage("Are you sure you want to mark ${product.name} as sold out?")
                .setPositiveButton("Yes") { _, _ -> onMarkSoldOut(product) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete ${product.name}? This cannot be undone.")
                .setPositiveButton("Delete") { _, _ -> onDelete(product) }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        this.products = newProducts
        notifyDataSetChanged()
    }

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivListingImage)
        val tvName: TextView = itemView.findViewById(R.id.tvListingName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvListingPrice)
        val tvStock: TextView = itemView.findViewById(R.id.tvListingStock)
        val btnSoldOut: Button = itemView.findViewById(R.id.btnMarkSoldOut)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteListing)
    }
}