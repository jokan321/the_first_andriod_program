package com.example.touristsights.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.touristsights.R
import com.example.touristsights.model.Sight
import java.util.prefs.NodeChangeListener

class SightAdapter(private val context: Context, private val sights: List<Sight>) : RecyclerView.Adapter<SightAdapter.ViewHolder>() {

    private var listener: ((Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        val description: TextView = view.findViewById(R.id.description)
        val kind: TextView = view.findViewById(R.id.kind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = sights[position].name
        holder.description.text = sights[position].description
        holder.kind.text = sights[position].kind
        val imageSource = context.resources.getIdentifier(
            sights[position].imageName,
            "drawable", context.packageName)
        holder.image.setImageResource(imageSource)
        holder.itemView.setOnClickListener {
            listener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = sights.size
}