package com.example.pora_realm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pora_realm.databinding.DemoLayout1Binding
import io.realm.kotlin.query.RealmResults

class PersonAdapter(private val personResults: RealmResults<PersonRealm>, private val onLongClickObject: MyOnLongClick?) :
    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    interface MyOnLongClick {
        fun onLongClick(p0: View?, position: Int)
    }

    // ViewHolder class with binding
    class PersonViewHolder(val binding: DemoLayout1Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = DemoLayout1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = personResults[position]
        holder.binding.apply {
            layoutName.text = person.name
            layoutCity.text = person.city
            uuidTag.text = person._id
        }
        holder.itemView.setOnLongClickListener {
            onLongClickObject?.onLongClick(it, position)
            true
        }
    }

    override fun getItemCount(): Int = personResults.size
}
