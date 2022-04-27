package com.example.safebackhome.viewHolders

import android.content.res.Resources
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.safebackhome.models.Contact
import com.example.safebackhome.R
import org.w3c.dom.Text

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var fullName: TextView
    var phoneNumber: TextView
    var removeButton : Button
    var favoriteButton : ImageButton

    fun bind(contact: Contact) {
        fullName.text = contact.fullName
        phoneNumber.text = contact.phoneNumber
        /*if (contact.isFavorite )
            favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.ic_favorite_foreground, null ))
        else
            favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.ic_notfavorite_foreground, null ))*/
    }

    init {
        fullName = itemView.findViewById(R.id.contact_name)
        phoneNumber = itemView.findViewById(R.id.contact_number)
        removeButton = itemView.findViewById(R.id.contact_remove_button)
        favoriteButton = itemView.findViewById(R.id.contact_favorite_button)
    }
}