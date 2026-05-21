package com.citu.ukayearn.ui.screens.haggle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.data.models.Product
import com.citu.ukayearn.ui.util.AssetImageLoader

class HaggleFragment : Fragment() {
    private lateinit var cartProducts: List<Product>
    private lateinit var selectedProduct: Product
    private lateinit var offerInput: EditText
    private lateinit var status: TextView
    private lateinit var savings: TextView
    private lateinit var productName: TextView
    private lateinit var sellerName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productImage: ImageView
    private lateinit var selectedItemLabel: TextView
    private lateinit var currentPriceSummary: TextView
    private lateinit var offerSummary: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_haggle, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartProducts = if (Database.isCurrentUserSeller()) {
            emptyList()
        } else {
            Database.cartItems.map { it.product }
        }
        offerInput = view.findViewById(R.id.etOfferAmount)
        status = view.findViewById(R.id.tvHaggleStatus)
        savings = view.findViewById(R.id.tvOfferSavings)
        productName = view.findViewById(R.id.tvHaggleProductName)
        sellerName = view.findViewById(R.id.tvHaggleSeller)
        productPrice = view.findViewById(R.id.tvHagglePrice)
        productImage = view.findViewById(R.id.ivHaggleProductImage)
        selectedItemLabel = view.findViewById(R.id.tvSelectedItemLabel)
        currentPriceSummary = view.findViewById(R.id.tvCurrentPriceSummary)
        offerSummary = view.findViewById(R.id.tvOfferSummary)

        if (cartProducts.isEmpty()) {
            status.text = getString(R.string.haggle_empty_cart)
            view.findViewById<Button>(R.id.btnSendOffer).isEnabled = false
            return
        }

        selectedProduct = cartProducts.first()
        setupProductDropdown(view.findViewById(R.id.spCartItems), view.findViewById(R.id.pickerCartItem))
        bindSelectedProduct(selectedProduct)

        view.findViewById<TextView>(R.id.btnOfferFive).setOnClickListener { setDiscountedOffer(OFFER_FIVE_PERCENT) }
        view.findViewById<TextView>(R.id.btnOfferTen).setOnClickListener { setDiscountedOffer(OFFER_TEN_PERCENT) }
        view.findViewById<TextView>(R.id.btnOfferFifteen).setOnClickListener { setDiscountedOffer(OFFER_FIFTEEN_PERCENT) }
        view.findViewById<Button>(R.id.btnSendOffer).setOnClickListener { sendHaggleOffer() }
    }

    private fun setupProductDropdown(spinner: Spinner, picker: LinearLayout) {
        spinner.adapter = ProductDropdownAdapter(cartProducts)
        picker.setOnClickListener { spinner.performClick() }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedProduct = cartProducts[position]
                bindSelectedProduct(selectedProduct)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun bindSelectedProduct(product: Product) {
        productName.text = product.name
        sellerName.text = getString(R.string.seller_format, product.seller)
        productPrice.text = getString(R.string.price_format, product.price)
        AssetImageLoader.load(productImage, product.imageUrl)
        selectedItemLabel.text = "${product.name} - ${product.seller}"
        currentPriceSummary.text = getString(R.string.price_format, product.price)
        offerSummary.text = getString(R.string.no_offer_yet)
        offerInput.text.clear()
        savings.text = getString(R.string.offer_savings_empty)

        val existingOffer = Database.latestHaggleForProduct(product.id)
        status.text = when (existingOffer?.status) {
            Database.HaggleStatus.PENDING -> getString(R.string.haggle_sent_waiting)
            Database.HaggleStatus.APPROVED -> getString(
                R.string.haggle_approved_voucher,
                getString(R.string.price_format, existingOffer.offerPrice)
            )
            Database.HaggleStatus.DECLINED -> getString(R.string.haggle_declined_try_again)
            null -> getString(R.string.haggle_tip)
        }
    }

    private fun setDiscountedOffer(discount: Double) {
        val offer = Math.round(selectedProduct.price * (1 - discount)).toDouble()
        offerInput.setText(offer.toInt().toString())
        offerSummary.text = getString(R.string.price_format, offer)
        savings.text = getString(R.string.offer_savings_format, getString(R.string.price_format, selectedProduct.price - offer))
    }

    private fun sendHaggleOffer() {
        val offerPrice = offerInput.text.toString().trim().toDoubleOrNull()
        if (offerPrice == null || offerPrice <= 0.0 || offerPrice >= selectedProduct.price) {
            Toast.makeText(requireContext(), R.string.invalid_haggle_offer, Toast.LENGTH_SHORT).show()
            return
        }

        val finalStatus = Database.HaggleStatus.PENDING

        val offer = Database.HaggleOffer(
            id = Database.haggleOffers.size + 1,
            product = selectedProduct,
            seller = selectedProduct.seller,
            offerPrice = offerPrice,
            buyerUsername = Database.currentUsername.ifBlank { "buyer" },
            status = finalStatus
        )
        Database.haggleOffers.add(offer)

        Database.addTextChatMessage(
            selectedProduct.seller,
            Database.currentUsername.ifBlank { "buyer" },
            "Hangyo sent for ${selectedProduct.name}: ₱${offerPrice}"
        )
        Toast.makeText(requireContext(), R.string.haggle_sent_to_messages, Toast.LENGTH_SHORT).show()

        Database.markSellerConversationUnread(selectedProduct.seller)
        bindSelectedProduct(selectedProduct) // Refresh UI
    }

    companion object {
        private const val OFFER_FIVE_PERCENT = 0.05
        private const val OFFER_TEN_PERCENT = 0.10
        private const val OFFER_FIFTEEN_PERCENT = 0.15
    }

    private inner class ProductDropdownAdapter(
        products: List<Product>
    ) : ArrayAdapter<Product>(requireContext(), R.layout.item_haggle_dropdown, products) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = createProductRow(position, convertView, parent)
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = createProductRow(position, convertView, parent)

        private fun createProductRow(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: layoutInflater.inflate(R.layout.item_haggle_dropdown, parent, false)
            val product = getItem(position) ?: return view
            view.findViewById<TextView>(R.id.tvDropdownProductName).text = product.name
            view.findViewById<TextView>(R.id.tvDropdownSeller).text = product.seller
            view.findViewById<TextView>(R.id.tvDropdownPrice).text = getString(R.string.price_format, product.price)
            return view
        }
    }
}