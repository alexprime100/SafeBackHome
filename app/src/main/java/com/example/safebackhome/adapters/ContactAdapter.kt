package com.example.safebackhome.adapters

import com.example.safebackhome.models.Contact
import androidx.recyclerview.widget.RecyclerView
import com.example.safebackhome.viewHolders.ContactViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.safebackhome.R

class ContactAdapter(private val contactList: List<Contact>) : RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}