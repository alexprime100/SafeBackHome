package com.example.safebackhome.viewHolders

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.safebackhome.models.Contact
import com.example.safebackhome.R
import org.w3c.dom.Text

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var fullName: TextView
    var phoneNumber: TextView
    var removeButton : Button
    var favoriteButton : Button

    fun bind(contact: Contact) {
        fullName.text = contact.fullName
        phoneNumber.text = contact.phoneNumber
    }

    init {
        fullName = itemView.findViewById(R.id.contact_name)
        phoneNumber = itemView.findViewById(R.id.contact_number)
        removeButton = itemView.findViewById(R.id.contact_remove_button)
        favoriteButton = itemView.findViewById(R.id.contact_favorite_button)
    }
}