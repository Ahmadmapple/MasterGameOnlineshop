package com.example.mastergameonlineshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.mastergameonlineshop.model.Game
import com.example.mastergameonlineshop.databinding.ItemGameBinding

class GameAdapter(
    private val originalList: List<Game>,
    private val gameImages: List<String>,
    private val onItemClickListener: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>(), Filterable {

    private var filteredList: List<Game> = originalList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = filteredList[position]
        val imageName = gameImages[originalList.indexOf(game)]
        holder.bind(game, imageName)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtered = if (constraint.isNullOrBlank()) {
                    originalList
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    originalList.filter { it.name.lowercase().contains(filterPattern) }
                }

                return FilterResults().apply {
                    values = filtered
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as List<Game>
                notifyDataSetChanged()
            }
        }
    }

    class GameViewHolder(
        private val binding: ItemGameBinding,
        private val onItemClickListener: (Game) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game, imageName: String) {
            binding.gameName.text = game.name
            binding.gamePrice.text = game.price
            val imageResId = binding.root.context.resources.getIdentifier(
                imageName,
                "drawable",
                binding.root.context.packageName
            )
            binding.gameImage.setImageResource(imageResId)

            itemView.setOnClickListener {
                onItemClickListener(game)
            }
        }
    }
}