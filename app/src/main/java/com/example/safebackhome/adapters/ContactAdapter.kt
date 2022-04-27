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
import kotlin.Exception

class ContactAdapter(private val contactList: ArrayList<Contact>) : RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contactList[position])
        if (contactList[position].isFavorite )
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_foreground)
        else
            holder.favoriteButton.setImageResource(R.drawable.ic_notfavorite_foreground)

        holder.removeButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                deleteContact(holder.adapterPosition)
            }
        })

        holder.favoriteButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View) {
                var contact = contactList.get(holder.adapterPosition)
                try{
                    if (contact.isFavorite){
                        removeFavorite(holder.adapterPosition, p0)
                        //holder.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.ic_notfavorite_foreground, null ))
                        //holder.favoriteButton.setImageResource(R.drawable.ic_notfavorite_foreground)
                    }
                    else {
                        addFavorite(holder.adapterPosition, p0)
                        //holder.favoriteButton.setImageDrawable(ResourcesCompat.getDrawable(Resources.getSystem(), R.drawable.ic_favorite_foreground, null ))
                        //holder.favoriteButton.setImageResource(R.drawable.ic_favorite_foreground)
                    }
                }
                catch (e:Exception){
                    Data.logger(e)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    fun removeFavorite(position: Int, view: View){
        var contact = contactList[position]
        contact.isFavorite = false
        FirebaseFirestore.getInstance().collection("Contacts").document(contact.id).update("IsFavortie", false).addOnSuccessListener {
            Log.d("Contact Debug : ", "set to not favorite")
        }
            .addOnFailureListener { e->
                Data.logger(e)
            }
        notifyDataSetChanged()
    }

    fun addFavorite(position: Int, view: View){
        var i = 0
        var contact = contactList[position]
        contact.isFavorite = true
        FirebaseFirestore.getInstance().collection("Contacts").document(contact.id)
            .update("IsFavortie", true).addOnSuccessListener {
                Log.d("Contact Debug : ", "set to favorite")
            }.addOnFailureListener { e->
                Data.logger(e)
            }
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