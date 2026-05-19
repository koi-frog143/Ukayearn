package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Store
import com.citu.ukayearn.ui.util.AssetImageLoader

class ShopDashboardAdapter(
    private val stores: List<Store>
) : RecyclerView.Adapter<ShopDashboardAdapter.ShopDashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopDashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_dashboard, parent, false)
        return ShopDashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopDashboardViewHolder, position: Int) {
        val store = stores[position]
        val products = Database.products.filter { it.seller == store.name }.take(3)
        val context = holder.itemView.context

        holder.tvName.text = store.name
        holder.tvMeta.text = context.resources.getQuantityString(
            R.plurals.store_listing_count,
            products.size,
            products.size
        )
        AssetImageLoader.load(holder.ivStore, store.imageUrl)

        holder.rowHeader.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ShopFragment.ARG_STORE_ID, store.id)
            }
            Navigation.findNavController(it).navigate(R.id.nav_shop, bundle)
        }
        holder.rvProducts.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = ProductAdapter(products)
        }
    }

    override fun getItemCount(): Int = stores.size

    class ShopDashboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rowHeader: View = itemView.findViewById(R.id.rowShopHeader)
        val ivStore: ImageView = itemView.findViewById(R.id.ivDashboardStore)
        val tvName: TextView = itemView.findViewById(R.id.tvDashboardStoreName)
        val tvMeta: TextView = itemView.findViewById(R.id.tvDashboardStoreMeta)
        val rvProducts: RecyclerView = itemView.findViewById(R.id.rvDashboardProducts)
    }
}
