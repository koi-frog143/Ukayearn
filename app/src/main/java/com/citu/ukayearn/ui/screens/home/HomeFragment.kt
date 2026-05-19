package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Category
import com.citu.ukayearn.ui.screens.profile.ProfileBottomSheetFragment
import com.citu.ukayearn.ui.util.AssetImageLoader

class HomeFragment : Fragment() {
    private lateinit var productAdapter: ProductAdapter
    private lateinit var sectionTitle: TextView
    private lateinit var scrollView: NestedScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        scrollView = view.findViewById(R.id.homeScrollView)
        sectionTitle = view.findViewById(R.id.tvFlashSale)
        sectionTitle.text = getString(R.string.newest_finds)

        AssetImageLoader.load(
            view.findViewById(R.id.ivNewCollectionPreview),
            Database.newCollectionPreviewImage
        )

        view.findViewById<RecyclerView>(R.id.rvStores).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = StoreShowcaseAdapter(Database.stores) { store ->
                val bundle = Bundle()
                bundle.putString(ShopFragment.ARG_STORE_ID, store.id)
                androidx.navigation.Navigation.findNavController(view).navigate(R.id.nav_shop, bundle)
            }
        }

        view.findViewById<RecyclerView>(R.id.rvCategories).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoryChipAdapter(Database.categories) { category ->
                showCategory(category)
            }
        }

        // Find the RecyclerView in our layout
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProducts)

        // Set it to display items in a grid with 2 columns
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Get our hardcoded products and attach the adapter
        productAdapter = ProductAdapter(Database.newCollectionProducts())
        recyclerView.adapter = productAdapter

        view.findViewById<View>(R.id.btnShopNewCollection).setOnClickListener {
            showNewCollection(scrollToList = true)
        }

        view.findViewById<View>(R.id.bannerCard).setOnClickListener {
            showNewCollection(scrollToList = true)
        }

        view.findViewById<View>(R.id.tvSeeAllCategories).setOnClickListener {
            showCategory(Database.categories.first())
        }

        view.findViewById<View>(R.id.tvSeeAllStores).setOnClickListener {
            scrollView.post {
                scrollView.smoothScrollTo(0, view.findViewById<RecyclerView>(R.id.rvStores).top)
            }
        }

        view.findViewById<View>(R.id.btnProfile).setOnClickListener {
            ProfileBottomSheetFragment().show(parentFragmentManager, ProfileBottomSheetFragment.TAG)
        }

        return view
    }

    private fun showCategory(category: Category) {
        val products = if (category.id == "all") {
            Database.products
        } else {
            Database.products.filter { product ->
                product.category.equals(category.name, ignoreCase = true)
            }
        }
        sectionTitle.text = if (category.id == "all") {
            getString(R.string.all_finds)
        } else {
            getString(R.string.category_finds_format, category.name)
        }
        productAdapter.submitList(products)
    }

    private fun showNewCollection(scrollToList: Boolean = false) {
        sectionTitle.text = getString(R.string.newest_finds)
        productAdapter.submitList(Database.newCollectionProducts())
        if (scrollToList) {
            scrollView.post {
                scrollView.smoothScrollTo(0, sectionTitle.top)
            }
        }
    }
}
