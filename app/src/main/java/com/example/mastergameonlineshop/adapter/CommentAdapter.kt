package com.example.mastergameonlineshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mastergameonlineshop.R

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val comments = mutableListOf<String>()

    fun updateComments(newComments: List<String>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val commentText: TextView = view.findViewById(R.id.comment_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.commentText.text = comments[position]
    }

    override fun getItemCount() = comments.size
}