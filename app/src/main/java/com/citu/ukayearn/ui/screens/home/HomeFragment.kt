package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Category
import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.data.models.Store
import com.citu.ukayearn.ui.screens.profile.ProfileBottomSheetFragment
import com.citu.ukayearn.ui.util.AssetImageLoader

class HomeFragment : Fragment() {
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchProductAdapter: ProductAdapter
    private lateinit var searchStoreAdapter: StoreShowcaseAdapter
    private lateinit var sectionTitle: TextView
    private lateinit var scrollView: NestedScrollView
    private lateinit var searchInput: EditText
    private lateinit var searchResultsPanel: View
    private lateinit var searchScopeButton: TextView
    private lateinit var searchCategoriesButton: TextView
    private lateinit var priceFilterButton: TextView
    private lateinit var clearSearchButton: View
    private lateinit var clearPriceFilterButton: View
    private lateinit var searchItemsHeader: TextView
    private lateinit var searchShopsHeader: TextView
    private lateinit var emptySearchItems: TextView
    private lateinit var emptySearchShops: TextView
    private lateinit var normalHomeViews: List<View>
    private var searchScope = SearchScope.ALL
    private var selectedPriceRange = PriceRange.ALL
    private val selectedCategories = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        scrollView = view.findViewById(R.id.homeScrollView)
        sectionTitle = view.findViewById(R.id.tvFlashSale)
        searchInput = view.findViewById(R.id.etHomeSearch)
        searchResultsPanel = view.findViewById(R.id.searchResultsPanel)
        searchScopeButton = view.findViewById(R.id.btnSearchScope)
        searchCategoriesButton = view.findViewById(R.id.btnSearchCategories)
        priceFilterButton = view.findViewById(R.id.btnPriceFilter)
        clearSearchButton = view.findViewById(R.id.btnClearSearch)
        clearPriceFilterButton = view.findViewById(R.id.btnClearPriceFilter)
        searchItemsHeader = view.findViewById(R.id.tvSearchItemsHeader)
        searchShopsHeader = view.findViewById(R.id.tvSearchShopsHeader)
        emptySearchItems = view.findViewById(R.id.tvSearchEmptyItems)
        emptySearchShops = view.findViewById(R.id.tvSearchEmptyShops)
        sectionTitle.text = getString(R.string.newest_finds)

        AssetImageLoader.load(
            view.findViewById(R.id.ivNewCollectionPreview),
            Database.newCollectionPreviewImage
        )

        val openStore: (Store) -> Unit = { store ->
            val bundle = Bundle()
            bundle.putString(ShopFragment.ARG_STORE_ID, store.id)
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.nav_shop, bundle)
        }

        val visibleStores = Database.stores.filter { !Database.isCurrentUserSellerFor(it.name) }
        view.findViewById<RecyclerView>(R.id.rvStores).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = StoreShowcaseAdapter(visibleStores, openStore)
        }

        searchStoreAdapter = StoreShowcaseAdapter(emptyList(), openStore)
        view.findViewById<RecyclerView>(R.id.rvSearchStores).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = searchStoreAdapter
        }

        view.findViewById<RecyclerView>(R.id.rvCategories).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoryChipAdapter(Database.categories) { category ->
                showCategory(category)
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProducts)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val visibleProducts = Database.newCollectionProducts().filter { !Database.isCurrentUserSellerFor(it.seller) }
        productAdapter = ProductAdapter(visibleProducts)
        recyclerView.adapter = productAdapter

        searchProductAdapter = ProductAdapter(emptyList())
        view.findViewById<RecyclerView>(R.id.rvSearchProducts).apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = searchProductAdapter
            isNestedScrollingEnabled = false
        }

        normalHomeViews = listOf(
            view.findViewById(R.id.bannerCard),
            view.findViewById(R.id.tvStoresHeader),
            view.findViewById(R.id.featuredIconContainer),
            view.findViewById(R.id.tvSeeAllStores),
            view.findViewById(R.id.rvStores),
            view.findViewById(R.id.tvCategoryHeader),
            view.findViewById(R.id.categoryIconHeader),
            view.findViewById(R.id.rvCategories),
            view.findViewById(R.id.tvFlashSale),
            view.findViewById(R.id.newFindsIconHeader),
            view.findViewById(R.id.rvProducts)
        )

        bindSearchControls()

        view.findViewById<View>(R.id.btnShopNewCollection).setOnClickListener {
            showNewCollection(scrollToList = true)
        }

        view.findViewById<View>(R.id.bannerCard).setOnClickListener {
            showNewCollection(scrollToList = true)
        }

        view.findViewById<View>(R.id.tvSeeAllStores).setOnClickListener {
            androidx.navigation.Navigation.findNavController(view).navigate(R.id.nav_shop_dashboard)
        }

        // ✅ Bulletproof Safe UI check to prevent crashing on Login
        val btnProfile = view.findViewById<View>(R.id.btnProfile)

        try {
            if (btnProfile is ImageView) {
                val profileUri = Database.currentProfileImageUri()
                if (!profileUri.isNullOrBlank()) {
                    btnProfile.setImageURI(android.net.Uri.parse(profileUri))
                    btnProfile.imageTintList = null
                    btnProfile.post {
                        btnProfile.outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: android.graphics.Outline) {
                                outline.setOval(0, 0, view.width, view.height)
                            }
                        }
                        btnProfile.clipToOutline = true
                    }
                }
            }
        } catch (e: Exception) {
            // Silently ignore UI cast errors to prevent app from crashing
        }

        btnProfile?.setOnClickListener {
            ProfileBottomSheetFragment().show(parentFragmentManager, ProfileBottomSheetFragment.TAG)
        }

        return view
    }

    private fun bindSearchControls() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.visibility = if (s.isNullOrBlank()) View.GONE else View.VISIBLE
                updateSearchResults()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        clearSearchButton.setOnClickListener {
            searchInput.text.clear()
        }

        searchScopeButton.setOnClickListener {
            PopupMenu(requireContext(), searchScopeButton).apply {
                menu.add(getString(R.string.search_all))
                menu.add(getString(R.string.search_items))
                menu.add(getString(R.string.search_shops))
                setOnMenuItemClickListener { item ->
                    searchScope = when (item.title.toString()) {
                        getString(R.string.search_items) -> SearchScope.ITEMS
                        getString(R.string.search_shops) -> SearchScope.SHOPS
                        else -> SearchScope.ALL
                    }
                    searchScopeButton.text = item.title
                    updateSearchResults(forceVisible = searchScope != SearchScope.ALL)
                    true
                }
                show()
            }
        }

        searchCategoriesButton.setOnClickListener {
            val categories = Database.categories.filter { it.id != "all" }
            val names = categories.map { it.name }.toTypedArray()
            val checked = categories.map { selectedCategories.contains(it.name) }.toBooleanArray()
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.filter_categories)
                .setMultiChoiceItems(names, checked) { _, which, isChecked ->
                    val category = categories[which].name
                    if (isChecked) selectedCategories.add(category) else selectedCategories.remove(category)
                }
                .setPositiveButton(R.string.apply) { _, _ ->
                    searchCategoriesButton.text = if (selectedCategories.isEmpty()) {
                        getString(R.string.filter_categories)
                    } else {
                        getString(R.string.filter_categories_count, selectedCategories.size)
                    }
                    updateSearchResults(forceVisible = selectedCategories.isNotEmpty())
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        priceFilterButton.setOnClickListener {
            PopupMenu(requireContext(), priceFilterButton).apply {
                PriceRange.entries.forEach { range ->
                    menu.add(getString(range.labelRes))
                }
                setOnMenuItemClickListener { item ->
                    selectedPriceRange = PriceRange.entries.first {
                        getString(it.labelRes) == item.title.toString()
                    }
                    priceFilterButton.text = item.title
                    clearPriceFilterButton.visibility =
                        if (selectedPriceRange == PriceRange.ALL) View.GONE else View.VISIBLE
                    searchScope = SearchScope.ITEMS
                    searchScopeButton.text = getString(R.string.search_items)
                    updateSearchResults(forceVisible = true)
                    scrollToSearchItems()
                    true
                }
                show()
            }
        }

        clearPriceFilterButton.setOnClickListener {
            selectedPriceRange = PriceRange.ALL
            priceFilterButton.text = getString(R.string.sort_price_all)
            clearPriceFilterButton.visibility = View.GONE
            updateSearchResults()
        }
    }

    private fun updateSearchResults(forceVisible: Boolean = false) {
        val query = searchInput.text.toString().trim()
        val hasQuery = query.isNotBlank()
        val hasPriceFilter = selectedPriceRange != PriceRange.ALL
        val hasScopeFilter = searchScope != SearchScope.ALL
        val hasCategoryFilter = selectedCategories.isNotEmpty()
        val shouldShowResults = forceVisible || hasQuery || hasPriceFilter || hasScopeFilter || hasCategoryFilter

        searchResultsPanel.visibility = if (shouldShowResults) View.VISIBLE else View.GONE
        normalHomeViews.forEach { it.visibility = if (shouldShowResults) View.GONE else View.VISIBLE }
        if (!shouldShowResults) return

        val matchingStores = if (searchScope == SearchScope.ITEMS) {
            emptyList()
        } else {
            Database.stores.filter { store -> store.matches(query) && !Database.isCurrentUserSellerFor(store.name) }
        }
        val matchingProducts = if (searchScope == SearchScope.SHOPS) {
            emptyList()
        } else {
            Database.products
                .filter { product -> product.matches(query) && !Database.isCurrentUserSellerFor(product.seller) }
                .filter { product -> selectedCategories.isEmpty() || selectedCategories.all { it in product.categories } }
                .filter { product -> selectedPriceRange.contains(product.price) }
                .sortedBy { it.price }
        }

        searchStoreAdapter.submitList(matchingStores)
        searchProductAdapter.submitList(matchingProducts)

        val showShops = searchScope != SearchScope.ITEMS
        val showItems = searchScope != SearchScope.SHOPS
        searchShopsHeader.visibility = if (showShops) View.VISIBLE else View.GONE
        requireView().findViewById<RecyclerView>(R.id.rvSearchStores).visibility =
            if (showShops && matchingStores.isNotEmpty()) View.VISIBLE else View.GONE
        emptySearchShops.visibility =
            if (showShops && matchingStores.isEmpty()) View.VISIBLE else View.GONE

        searchItemsHeader.visibility = if (showItems) View.VISIBLE else View.GONE
        requireView().findViewById<RecyclerView>(R.id.rvSearchProducts).visibility =
            if (showItems && matchingProducts.isNotEmpty()) View.VISIBLE else View.GONE
        emptySearchItems.visibility =
            if (showItems && matchingProducts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun scrollToSearchItems() {
        scrollView.post {
            scrollView.smoothScrollTo(0, searchItemsHeader.top)
        }
    }

    private fun showCategory(category: Category) {
        val baseProducts = Database.products.filter { !Database.isCurrentUserSellerFor(it.seller) }
        val products = if (category.id == "all") {
            baseProducts
        } else {
            baseProducts.filter { product ->
                product.categories.any { it.equals(category.name, ignoreCase = true) }
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
        productAdapter.submitList(Database.newCollectionProducts().filter { !Database.isCurrentUserSellerFor(it.seller) })
        if (scrollToList) {
            scrollView.post {
                scrollView.smoothScrollTo(0, sectionTitle.top)
            }
        }
    }

    private fun Store.matches(query: String): Boolean {
        if (query.isBlank()) return true
        return name.contains(query, ignoreCase = true) ||
                location.contains(query, ignoreCase = true) ||
                tagline.contains(query, ignoreCase = true)
    }

    private fun Product.matches(query: String): Boolean {
        if (query.isBlank()) return true
        return name.contains(query, ignoreCase = true) ||
                seller.contains(query, ignoreCase = true) ||
                categories.any { it.contains(query, ignoreCase = true) } ||
                description.contains(query, ignoreCase = true)
    }

    private enum class SearchScope {
        ALL,
        ITEMS,
        SHOPS
    }

    private enum class PriceRange(val labelRes: Int, val min: Double, val max: Double?) {
        ALL(R.string.sort_price_all, 0.0, null),
        UNDER_500(R.string.sort_price_under_500, 0.0, 499.99),
        FROM_500_TO_999(R.string.sort_price_500_999, 500.0, 999.99),
        FROM_1000_TO_1999(R.string.sort_price_1000_1999, 1000.0, 1999.99),
        FROM_2000_UP(R.string.sort_price_2000_plus, 2000.0, null);

        fun contains(price: Double): Boolean {
            return price >= min && (max == null || price <= max)
        }
    }
}