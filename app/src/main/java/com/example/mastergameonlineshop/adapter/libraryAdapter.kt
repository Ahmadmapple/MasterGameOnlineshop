package com.example.mastergameonlineshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mastergameonlineshop.databinding.ItemLibraryBinding
import com.example.mastergameonlineshop.model.LibraryItem

class LibraryAdapter : RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>() {

    private val libraryList = mutableListOf<LibraryItem>()

    fun updateLibrary(newLibraryItems: List<LibraryItem>) {
        libraryList.clear()
        libraryList.addAll(newLibraryItems)
        notifyDataSetChanged()
    }

    inner class LibraryViewHolder(private val binding: ItemLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LibraryItem) {
            binding.libraryName.text = item.name
            binding.libraryImage.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = ItemLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibraryViewHolder(binding)
    }

    override fun getItemCount(): Int = libraryList.size

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(libraryList[position])
    }
}
