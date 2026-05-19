package com.citu.ukayearn.ui.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.citu.ukayearn.R
import com.citu.ukayearn.data.models.Category

class CategoryChipAdapter(
    private val categories: List<Category>,
    private val onCategorySelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryChipAdapter.CategoryViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val context = holder.itemView.context
        val isSelected = position == selectedPosition

        holder.tvName.text = category.name
        holder.ivIcon.setImageResource(iconFor(category.id))
        holder.iconContainer.setBackgroundResource(
            if (isSelected) R.drawable.home_category_icon_bg_selected else R.drawable.home_category_icon_bg
        )
        holder.tvName.setTextColor(
            ContextCompat.getColor(context, if (isSelected) R.color.primary_maroon else R.color.black)
        )
        holder.ivIcon.imageTintList = ContextCompat.getColorStateList(
            context,
            if (isSelected) R.color.white else R.color.secondary_blue
        )
        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION || adapterPosition == selectedPosition) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = adapterPosition
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)
            onCategorySelected(categories[adapterPosition])
        }
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconContainer: FrameLayout = itemView.findViewById(R.id.categoryIconContainer)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        val tvName: TextView = itemView.findViewById(R.id.tvCategoryName)
    }

    private fun iconFor(categoryId: String): Int {
        return when (categoryId) {
            "tops" -> R.drawable.ic_shirt_24
            "bottoms" -> R.drawable.ic_pants_24
            "outerwear" -> R.drawable.ic_jacket_24
            else -> R.drawable.ic_sparkle_24
        }
    }
}
