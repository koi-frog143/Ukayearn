package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Store
import com.citu.ukayearn.ui.util.AssetImageLoader

class ShopFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_shop, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeId = arguments?.getString(ARG_STORE_ID)
        val store = Database.stores.find { it.id == storeId } ?: Database.stores.first()
        val storeProducts = Database.products.filter { it.seller == store.name }
        bindStore(view, store, storeProducts.size)
        bindProducts(view, storeProducts)

        view.findViewById<ImageButton>(R.id.btnBackShop).setOnClickListener {
            findNavController().navigateUp()
        }
        view.findViewById<Button>(R.id.btnShopChat).setOnClickListener {
            findNavController().navigate(R.id.nav_chat)
        }
        view.findViewById<Button>(R.id.btnShopFollow).setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.followed_shop_format, store.name), Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btnShopReport).setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.report_submitted, store.name), Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindStore(view: View, store: Store, listingCount: Int) {
        AssetImageLoader.load(view.findViewById(R.id.ivShopCover), store.imageUrl)
        AssetImageLoader.load(view.findViewById(R.id.ivShopAvatar), store.imageUrl)

        view.findViewById<TextView>(R.id.tvShopName).text = store.name
        view.findViewById<TextView>(R.id.tvShopLocation).text = store.location
        view.findViewById<TextView>(R.id.tvShopTagline).text = store.tagline
        view.findViewById<TextView>(R.id.tvShopJoined).text = store.joinedDate
        view.findViewById<TextView>(R.id.tvShopRating).text = getString(R.string.shop_rating_format, store.rating)
        view.findViewById<TextView>(R.id.tvShopListings).text = resources.getQuantityString(
            R.plurals.store_listing_count,
            listingCount,
            listingCount
        )
        view.findViewById<TextView>(R.id.tvShopFollowers).text = getString(R.string.shop_followers_format, store.followers)
        view.findViewById<TextView>(R.id.tvShopResponse).text = getString(R.string.shop_response_rate_format, store.responseRate)
    }

    private fun bindProducts(view: View, products: List<com.citu.ukayearn.data.models.Product>) {
        view.findViewById<RecyclerView>(R.id.rvShopProducts).apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = ProductAdapter(products)
            isNestedScrollingEnabled = false
        }
    }

    companion object {
        const val ARG_STORE_ID = "storeId"
    }
}
