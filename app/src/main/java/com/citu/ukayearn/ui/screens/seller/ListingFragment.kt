package com.citu.ukayearn.ui.screens.seller

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database

class ListingFragment : Fragment() {
    private lateinit var listingAdapter: ListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_listing, container, false)

        setupRecyclerView(view)

        view.findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
            showAddProductDialog()
        }

        return view
    }

    private fun setupRecyclerView(view: View) {
        val rvListings = view.findViewById<RecyclerView>(R.id.rvListings)

        listingAdapter = ListingAdapter(
            products = getSellerProducts(),
            onMarkSoldOut = { product ->
                Database.markProductSoldOut(product.id)
                refreshList()
                Toast.makeText(requireContext(), "${product.name} marked as sold out", Toast.LENGTH_SHORT).show()
            },
            onDelete = { product ->
                Database.deleteProduct(product.id)
                refreshList()
                Toast.makeText(requireContext(), "${product.name} deleted", Toast.LENGTH_SHORT).show()
            }
        )

        rvListings.layoutManager = LinearLayoutManager(requireContext())
        rvListings.adapter = listingAdapter
    }

    private fun getSellerProducts() = Database.products.filter { it.seller == Database.currentSellerName() }

    private fun refreshList() {
        listingAdapter.updateData(getSellerProducts())
    }

    private fun showAddProductDialog() {
        val context = requireContext()

        // Building the form layout programmatically
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val nameInput = EditText(context).apply { hint = "Product Name" }
        val descInput = EditText(context).apply { hint = "Description" }
        val priceInput = EditText(context).apply {
            hint = "Price"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val stockInput = EditText(context).apply {
            hint = "Stock Quantity"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(nameInput)
        layout.addView(descInput)
        layout.addView(priceInput)
        layout.addView(stockInput)

        AlertDialog.Builder(context)
            .setTitle("Add New Product")
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val desc = descInput.text.toString().trim()
                val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
                val stock = stockInput.text.toString().toIntOrNull() ?: 1

                if (name.isNotEmpty() && desc.isNotEmpty() && price > 0) {
                    // Automatically uses the new collection preview image for the capstone demo to save time
                    Database.addProduct(name, desc, price, "All Finds", Database.newCollectionPreviewImage, stock)
                    refreshList()
                    Toast.makeText(context, "Product Added Successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please fill in all fields correctly.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}