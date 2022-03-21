package com.example.safebackhome.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.safebackhome.models.Contact
import com.example.safebackhome.R

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var fullName: TextView
    var phoneNumber: TextView

    fun bind(contact: Contact) {
        fullName.text = contact.fullName
        phoneNumber.text = contact.phoneNumber
    }

    init {
        fullName = itemView.findViewById(R.id.contact_name)
        phoneNumber = itemView.findViewById(R.id.contact_number)
    }
}