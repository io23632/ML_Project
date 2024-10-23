package com.hamzaahmed0196.datacollectionfordrinking.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hamzaahmed0196.datacollectionfordrinking.databinding.ItemRowBinding


class ActivityAdaptor (private val items: MutableList<ActivityModel>) :
    RecyclerView.Adapter<ActivityAdaptor.ViewHolder>() {
        private lateinit var binding : ItemRowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityAdaptor.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRowBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityAdaptor.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: ItemRowBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bind(item : ActivityModel) {
            binding.apply {
                textView.text = item.name
                imageView.setImageResource(item.image)
            }
        }
    }
}