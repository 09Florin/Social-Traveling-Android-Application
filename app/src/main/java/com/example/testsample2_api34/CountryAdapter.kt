package com.example.testsample2_api34

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class CountryAdapter(private val countryList : ArrayList<Country>, private val firebaseHandler: FirebaseHandler): RecyclerView.Adapter<CountryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_country, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val country : Country = countryList[position]

        holder.countryName.text = country.countryName
        holder.countryDescription.text = country.countryDescription

        country.logo.let {
            Picasso.get().load(it).into(holder.logoImageView)
        }

        holder.souvenirRecyclerView.setHasFixedSize(true)
        holder.souvenirRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context , 2) // 3 souvenirs per row, the next are moving to the next row (3 columns for the entire card)
        val childAdapter = SouvenirAdapter(country.souvenirList, false)
        holder.souvenirRecyclerView.adapter = childAdapter

        //Expandable functionality
        val isExpandable = country.isExpandable
        holder.souvenirRecyclerView.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.countryLayout.setOnClickListener{
            isAnyCountryExpanded(position)
            country.isExpandable = !country.isExpandable // if it is expanded, contract it and vice-versa
            notifyItemRangeChanged(position, 1)
            if (country.isExpandable && country.souvenirList.isEmpty()) {
                retrieveSouvenirsForCountry(country.countryName, position)
            }
        }
    }

    private fun isAnyCountryExpanded(position: Int){
        val temp = countryList.indexOfFirst {it.isExpandable
        }
        if (temp >= 0 && temp != position){ // check if any other country is expanded excepting the one we are on right now(temp)
            countryList[temp].isExpandable = false
            notifyItemChanged(temp)
        }
    }

    private fun retrieveSouvenirsForCountry(countryName: String?, position: Int) {
        firebaseHandler.retrieveSouvenirsForCountry(
            countryName,
            onSuccess = { souvenirs ->
                countryList[position].souvenirList.clear()
                countryList[position].souvenirList.addAll(souvenirs)
                notifyItemChanged(position)
            },
            onFailure = { exception ->
                Log.e("CountryAdapter", "Failed to retrieve souvenirs for country", exception)
            }
        )
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val countryName : TextView = itemView.findViewById(R.id.countryNameText)
        val countryDescription : TextView = itemView.findViewById(R.id.countryDescriptionText)
        val logoImageView: ImageView = itemView.findViewById(R.id.logoImageView)
        val souvenirRecyclerView : RecyclerView = itemView.findViewById(R.id.souvenirRecyclerView)
        val countryLayout : ConstraintLayout = itemView.findViewById(R.id.countryLayout)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCountries(countries: List<Country>) {
        countryList.clear()
        countryList.addAll(countries)
        notifyDataSetChanged()
    }

}