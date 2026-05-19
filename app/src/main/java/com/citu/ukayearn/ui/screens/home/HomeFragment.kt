package com.citu.ukayearn.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the RecyclerView in our layout
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvProducts)

        // Set it to display items in a grid with 2 columns
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Get our hardcoded products and attach the adapter
        val adapter = ProductAdapter(Database.products)
        recyclerView.adapter = adapter

        return view
    }
}