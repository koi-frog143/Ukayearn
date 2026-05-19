package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.ui.util.AssetImageLoader

class ProductAdapter(
    products: List<Product>,
    private val destinationId: Int = R.id.nav_details
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private val productList = products.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        val context = holder.itemView.context

        holder.tvName.text = product.name
        holder.tvPrice.text = context.getString(R.string.price_format, product.price)
        holder.tvSeller.text = product.seller
        holder.tvCategory.text = product.category
        AssetImageLoader.load(holder.ivImage, product.imageUrl)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(ProductDetailsFragment.ARG_PRODUCT_ID, product.id)
            Navigation.findNavController(it).navigate(destinationId, bundle)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun submitList(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvSeller: TextView = itemView.findViewById(R.id.tvProductSeller)
        val tvCategory: TextView = itemView.findViewById(R.id.tvProductCategory)
    }
}
