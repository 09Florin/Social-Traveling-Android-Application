package com.example.testsample2_api34
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class SouvenirAdapter(var souvenirList: List<Souvenir>, private val showDescription: Boolean) :
    RecyclerView.Adapter<SouvenirAdapter.SouvenirViewHolder>() {

    inner class SouvenirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val souvenirLogo: ImageView = itemView.findViewById(R.id.souvenirLogoImageView)
        val souvenirName: TextView = itemView.findViewById(R.id.souvenirNameText)
        val souvenirDescription: TextView = itemView.findViewById(R.id.souvenirDescriptionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SouvenirViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_souvenir, parent, false)
        return SouvenirViewHolder(view)
    }

    override fun onBindViewHolder(holder: SouvenirViewHolder, position: Int) {
        val souvenir : Souvenir = souvenirList[position]
        holder.souvenirName.text = souvenirList[position].souvenirName
        holder.souvenirDescription.text = souvenirList[position].souvenirDescription
        holder.souvenirDescription.visibility = if (showDescription) View.VISIBLE else View.GONE

        souvenir.souvenirLogo.let {
            Picasso.get().load(it).into(holder.souvenirLogo)
        }

    }

    override fun getItemCount(): Int {
        return souvenirList.size
    }

    fun updateData(newSouvenirs: List<Souvenir>) {
        souvenirList = newSouvenirs
        notifyDataSetChanged()
        Log.d("SouvenirAdapter", "Souvenir List Size: ${souvenirList.size}")
    }
}