package com.example.mastergameonlineshop.mainactivityfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mastergameonlineshop.adapter.LibraryAdapter
import com.example.mastergameonlineshop.databinding.FragmentLibraryBinding
import com.example.mastergameonlineshop.model.LibraryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Library : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var libraryAdapter: LibraryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryAdapter = LibraryAdapter()
        binding.mylibraryRecycle.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.mylibraryRecycle.adapter = libraryAdapter

        loadLibraryItems()
    }

    private fun loadLibraryItems() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("library")
            .document(userId)
            .collection("libraryItems")
            .get()
            .addOnSuccessListener { snapshot ->
                val libraryItems = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("gameName")
                    val imageResId = doc.getLong("gameImageResId")?.toInt()

                    if (name != null && imageResId != null) {
                        LibraryItem(name, imageResId)
                    } else {
                        null
                    }
                }

                libraryAdapter.updateLibrary(libraryItems)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
