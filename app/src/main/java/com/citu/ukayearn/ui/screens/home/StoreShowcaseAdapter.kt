package com.citu.ukayearn.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Store
import com.citu.ukayearn.ui.util.AssetImageLoader

class StoreShowcaseAdapter(
    stores: List<Store>,
    private val onStoreClicked: (Store) -> Unit
) : RecyclerView.Adapter<StoreShowcaseAdapter.StoreViewHolder>() {
    private val storeList = stores.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_store, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        val context = holder.itemView.context
        val listingCount = Database.products.count { it.seller == store.name }

        holder.tvName.text = store.name
        holder.tvMeta.text = context.resources.getQuantityString(
            R.plurals.store_listing_count,
            listingCount,
            listingCount
        )
        AssetImageLoader.load(holder.ivImage, store.imageUrl)
        holder.itemView.setOnClickListener {
            onStoreClicked(store)
        }
    }

    override fun getItemCount(): Int = storeList.size

    fun submitList(stores: List<Store>) {
        storeList.clear()
        storeList.addAll(stores)
        notifyDataSetChanged()
    }

    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivStoreImage)
        val tvName: TextView = itemView.findViewById(R.id.tvStoreName)
        val tvMeta: TextView = itemView.findViewById(R.id.tvStoreMeta)
    }
}
