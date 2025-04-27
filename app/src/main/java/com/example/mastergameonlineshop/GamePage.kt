package com.example.mastergameonlineshop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.content.ContextCompat
import com.example.mastergameonlineshop.databinding.ActivityGamePageBinding
import com.example.mastergameonlineshop.adapter.CommentAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GamePage : AppCompatActivity() {

    private lateinit var binding: ActivityGamePageBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var commentAdapter: CommentAdapter

    private var currentButtonAction: ButtonAction = ButtonAction.ADD_TO_CART

    enum class ButtonAction {
        ADD_TO_CART,
        VIEW_CART,
        VIEW_LIBRARY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGamePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("GAME_NAME")
        val price = intent.getStringExtra("GAME_PRICE")
        val imageResId = intent.getIntExtra("GAME_IMAGE_RES_ID", 0)
        val developer = intent.getStringExtra("GAME_DEVELOPER")
        val description = intent.getStringExtra("GAME_DESCRIPTION")

        binding.gamePageText.text = name
        binding.gamePageDeveloper.text = developer
        binding.gamePagePrices.text = price
        binding.gamePageImage.setImageResource(imageResId)
        binding.gamePageDescription.text = description

        setSupportActionBar(binding.gamepageToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.gamepageToolbar.navigationIcon?.setTint(getColor(R.color.darker_yellow))

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Setup RecyclerView Comment
        commentAdapter = CommentAdapter()
        binding.recyclerViewComments.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewComments.adapter = commentAdapter

        val gameId = name ?: "UnknownGame"

        firestore.collection("comments")
            .document(gameId)
            .collection("commentList")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val commentList = snapshot?.documents?.mapNotNull { doc ->
                    val username = doc.getString("username")
                    val comment = doc.getString("comment")
                    if (username != null && comment != null) "$username: $comment" else null
                } ?: listOf()

                commentAdapter.updateComments(commentList)
            }

        binding.buttonSubmitComment.setOnClickListener {
            val commentText = binding.editTextComment.text.toString()
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            if (commentText.isNotEmpty()) {
                firestore.collection("users").document(userId).get().addOnSuccessListener { doc ->
                    val username = doc.getString("username") ?: "Anonymous"
                    val commentData = hashMapOf(
                        "userId" to userId,
                        "username" to username,
                        "comment" to commentText,
                        "timestamp" to System.currentTimeMillis()
                    )

                    firestore.collection("comments")
                        .document(gameId)
                        .collection("commentList")
                        .add(commentData)

                    binding.editTextComment.setText("")
                }
            }
        }

        // Cek status: library atau cart
        name?.let { checkItemStatus(it) }

        binding.addtocartButton.setOnClickListener {
            if (name == null) return@setOnClickListener

            when (currentButtonAction) {
                ButtonAction.VIEW_LIBRARY -> {
                    Toast.makeText(this, "You already own this game!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("open_library", true)
                    startActivity(intent)
                    finish()
                }

                ButtonAction.VIEW_CART -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("open_cart", true)
                    startActivity(intent)
                    finish()
                }

                ButtonAction.ADD_TO_CART -> {
                    val userId = auth.currentUser?.uid ?: return@setOnClickListener

                    val cartItem = hashMapOf(
                        "gameName" to name,
                        "gamePrice" to price,
                        "gameImageResId" to imageResId,
                        "timestamp" to System.currentTimeMillis()
                    )

                    firestore.collection("carts")
                        .document(userId)
                        .collection("cartItems")
                        .add(cartItem)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
                            binding.addtocartButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
                            binding.addtocartButton.text = "View item in cart"
                            currentButtonAction = ButtonAction.VIEW_CART
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add to cart.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun checkItemStatus(gameName: String) {
        val userId = auth.currentUser?.uid ?: return

        // Cek di Library dulu
        firestore.collection("library")
            .document(userId)
            .collection("libraryItems")
            .whereEqualTo("gameName", gameName)
            .get()
            .addOnSuccessListener { libraryDocs ->
                if (!libraryDocs.isEmpty) {
                    binding.addtocartButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
                    binding.addtocartButton.text = "View item in library"
                    currentButtonAction = ButtonAction.VIEW_LIBRARY
                } else {
                    // Kalau belum, cek di Cart
                    firestore.collection("carts")
                        .document(userId)
                        .collection("cartItems")
                        .whereEqualTo("gameName", gameName)
                        .get()
                        .addOnSuccessListener { cartDocs ->
                            if (!cartDocs.isEmpty) {
                                binding.addtocartButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue)
                                binding.addtocartButton.text = "View item in cart"
                                currentButtonAction = ButtonAction.VIEW_CART
                            }
                        }
                }
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
