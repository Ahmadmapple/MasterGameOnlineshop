package com.example.mastergameonlineshop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mastergameonlineshop.model.CartItem
import com.example.mastergameonlineshop.databinding.ItemCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(
    private val onItemRemoved: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartList = mutableListOf<CartItem>()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun updateCart(newCart: List<CartItem>) {
        cartList.clear()
        cartList.addAll(newCart)
        notifyDataSetChanged()
    }

    fun getCartItems(): List<CartItem> {
        return cartList
    }

    fun clearCart() {
        cartList.clear()
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.cartItemName.text = item.name
            binding.cartItemPrice.text = item.price
            binding.cartItemImage.setImageResource(item.imageResId)

            binding.buttonDelete.setOnClickListener {
                val userId = auth.currentUser?.uid ?: return@setOnClickListener
                val itemName = item.name

                firestore.collection("carts")
                    .document(userId)
                    .collection("cartItems")
                    .whereEqualTo("gameName", itemName)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            document.reference.delete()
                        }
                        Toast.makeText(binding.root.context, "Item removed from cart", Toast.LENGTH_SHORT).show()

                        // Remove from the local list and update RecyclerView
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            cartList.removeAt(position)
                            notifyItemRemoved(position)

                            val removedPrice = item.price
                                .replace("Rp ", "")
                                .replace(".", "")
                                .toIntOrNull() ?: 0

                            // Call callback to fragment
                            onItemRemoved(removedPrice)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(binding.root.context, "Failed to remove item", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartList.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }
}
