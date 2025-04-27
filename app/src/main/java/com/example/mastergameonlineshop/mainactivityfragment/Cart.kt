package com.example.mastergameonlineshop.mainactivityfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mastergameonlineshop.adapter.CartAdapter
import com.example.mastergameonlineshop.databinding.FragmentCartBinding
import com.example.mastergameonlineshop.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Cart : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter
    private var totalPriceValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartAdapter = CartAdapter { removedPrice ->
            decreaseTotalPrice(removedPrice)
        }
        binding.mycartRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.mycartRecycle.adapter = cartAdapter

        binding.mycartCheckout.setOnClickListener {
            checkoutCartItems()
        }

        loadCartItems()
    }

    private fun loadCartItems() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("carts")
            .document(userId)
            .collection("cartItems")
            .get()
            .addOnSuccessListener { snapshot ->
                if (!isAdded || _binding == null) return@addOnSuccessListener

                val cartItems = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("gameName")
                    val price = doc.getString("gamePrice")
                    val imageResId = doc.getLong("gameImageResId")?.toInt()

                    if (name != null && price != null && imageResId != null) {
                        CartItem(name, price, imageResId)
                    } else {
                        null
                    }
                }

                cartAdapter.updateCart(cartItems)

                totalPriceValue = cartItems.sumOf {
                    it.price.replace("Rp ", "").replace(".", "").toInt()
                }
                updateTotalPriceText()

                checkIfCartEmpty()
            }
    }

    private fun checkoutCartItems() {
        val userId = auth.currentUser?.uid ?: return

        val cartItems = cartAdapter.getCartItems()

        if (cartItems.isEmpty()) return

        val batch = firestore.batch()

        for (item in cartItems) {
            // Tambahkan ke library
            val libraryRef = firestore.collection("library")
                .document(userId)
                .collection("libraryItems")
                .document()

            batch.set(libraryRef, mapOf(
                "gameName" to item.name,
                "gameImageResId" to item.imageResId
            ))

            // Hapus dari cart
            val cartRef = firestore.collection("carts")
                .document(userId)
                .collection("cartItems")
                .whereEqualTo("gameName", item.name)

            cartRef.get().addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    batch.delete(doc.reference)
                }
                // Commit semua perubahan setelah semua data siap
                batch.commit().addOnSuccessListener {
                    cartAdapter.clearCart()
                    totalPriceValue = 0
                    updateTotalPriceText()
                    checkIfCartEmpty()
                }
            }
        }
    }

    private fun decreaseTotalPrice(amount: Int) {
        totalPriceValue -= amount
        if (totalPriceValue < 0) totalPriceValue = 0
        updateTotalPriceText()
        checkIfCartEmpty()
    }

    private fun updateTotalPriceText() {
        if (totalPriceValue == 0 && cartAdapter.itemCount == 0) {
            binding.totalPrice.text = "Total Price: "
        } else {
            binding.totalPrice.text = "Total Price: Rp ${"%,d".format(totalPriceValue).replace(",", ".")}"
        }
    }

    private fun checkIfCartEmpty() {
        if (cartAdapter.itemCount == 0) {
            binding.totalPrice.text = "Total Price: "
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
