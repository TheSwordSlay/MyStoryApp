package com.fiqri.mystoryapp.helper.stories
import androidx.core.util.Pair
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fiqri.mystoryapp.R
import com.fiqri.mystoryapp.data.remote.response.ListStoryItem
import com.fiqri.mystoryapp.databinding.ItemStoryBinding
import com.fiqri.mystoryapp.ui.StoryDetailActivity
import com.fiqri.mystoryapp.ui.StoryListActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data!!)
    }

    class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private var storyImg: ImageView = itemView.findViewById(R.id.storyImg)
        private var uName: TextView = itemView.findViewById(R.id.uName)
        private var descText: TextView = itemView.findViewById(R.id.desc)
        fun bind(data: ListStoryItem){
            binding.uName.text = "${data.name}"
            binding.desc.text = "${data.description}"

            Glide.with(binding.storyImg.context)
                .load(data.photoUrl)
                .into(binding.storyImg)

            itemView.setOnClickListener {
                val moveToDetail = Intent(itemView.context, StoryDetailActivity::class.java)
                moveToDetail.putExtra("imgUrl", data.photoUrl)
                moveToDetail.putExtra("author", data.name)
                moveToDetail.putExtra("desc", data.description)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(storyImg, "imgPhoto"),
                        Pair(uName, "uName"),
                        Pair(descText, "descText"),
                    )
                itemView.context.startActivity(moveToDetail, optionsCompat.toBundle())
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}