package com.example.mastergameonlineshop.mainactivityfragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.R
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mastergameonlineshop.GamePage
import com.example.mastergameonlineshop.adapter.GameAdapter
import com.example.mastergameonlineshop.databinding.FragmentHomeBinding
import com.example.mastergameonlineshop.model.Game

class Home : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var searchView: SearchView
    private lateinit var gameList: List<Game>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        searchView = binding.searchView

        // Set searchView icon colors and text color
        val searchIconId = searchView.context.resources.getIdentifier("android:id/search_button", null, null)
        val searchIcon = searchView.findViewById<ImageView>(searchIconId)
        searchIcon.setColorFilter(Color.WHITE)

        val closeButtonId = searchView.context.resources.getIdentifier("android:id/search_close_btn", null, null)
        val closeButton = searchView.findViewById<ImageView>(closeButtonId)
        closeButton.setColorFilter(Color.WHITE)

        val editText = searchView.findViewById<EditText>(R.id.search_src_text)
        editText?.setTextColor(Color.WHITE)
        editText?.setHintTextColor(Color.YELLOW)

        // Game List Setup
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        gameList = getGameList()
        val gameImages = getGameImages()

        gameAdapter = GameAdapter(gameList, gameImages) { game ->
            val intent = Intent(context, GamePage::class.java).apply {
                putExtra("GAME_NAME", game.name)
                putExtra("GAME_PRICE", game.price)
                putExtra("GAME_IMAGE_RES_ID", game.imageResId)
                putExtra("GAME_DEVELOPER", game.developer)
                putExtra("GAME_DESCRIPTION", game.description)
            }
            startActivity(intent)
        }

        recyclerView.adapter = gameAdapter

        // Search filtering
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                gameAdapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                gameAdapter.filter.filter(newText)
                return true
            }
        })

        return binding.root
    }

    private fun getGameList(): List<Game> {
        val gameNames = resources.getStringArray(com.example.mastergameonlineshop.R.array.name_of_the_game)
        val gamePrices = resources.getStringArray(com.example.mastergameonlineshop.R.array.price_of_the_game)
        val gameImages = getGameImages()
        val gameDevelopers = resources.getStringArray(com.example.mastergameonlineshop.R.array.game_developer)
        val gameDescriptions = resources.getStringArray(com.example.mastergameonlineshop.R.array.description_of_the_game)

        return gameNames.indices.map { i ->
            val imageResId = resources.getIdentifier(gameImages[i], "drawable", context?.packageName)
            Game(
                name = gameNames[i],
                price = gamePrices[i],
                imageResId = imageResId,
                developer = gameDevelopers[i],
                description = gameDescriptions[i]
            )
        }
    }

    private fun getGameImages(): List<String> {
        return resources.getStringArray(com.example.mastergameonlineshop.R.array.game_images).toList()
    }
}