package com.example.safebackhome.adapters

import android.content.res.Resources
import android.util.Log
import com.example.safebackhome.models.Contact
import androidx.recyclerview.widget.RecyclerView
import com.example.safebackhome.viewHolders.ContactViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.safebackhome.Data
import com.example.safebackhome.R
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class ContactAdapter(private val contactList: ArrayList<Contact>) : RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
        /*if (contactList[position].isFavorite )
            holder.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.ic_favorite_foreground, null ))
        else
            holder.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.ic_notfavorite_foreground, null ))*/

        holder.removeButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                deleteContact(holder.adapterPosition)
            }
        })

        holder.favoriteButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View) {
                var contact = contactList.get(holder.adapterPosition)
                if (contact.isFavorite){
                    removeFavorite(holder.adapterPosition, p0)
                }
                else addFavorite(holder.adapterPosition, p0)
            }
        })
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun removeFavorite(position: Int, view: View){
        var contact = contactList[position]
        contact.isFavorite = false
        contactList.removeAt(position)
        contactList.add(contact)
    }

    fun addFavorite(position: Int, view: View){
        var i = 0
        var contact = contactList[position]
        contact.isFavorite = true
        while (contactList[i].isFavorite)
            i++
        contactList.removeAt(position)
        contactList.add(i, contact)
        notifyDataSetChanged()
    }

    fun addContact(contact: Contact){
        contactList.add(contact)
        notifyDataSetChanged()
    }

    private fun deleteContact(position: Int){
        var contact = contactList.get(position)
        var firestore = FirebaseFirestore.getInstance()
        try {
            firestore.collection("Contacts").document(contact.id).delete()

            contactList.removeAt(position)
            notifyDataSetChanged()
        }
        catch (e: Exception){
            Log.e("Contact Error", e.message.toString(), e)
        }
    }
}