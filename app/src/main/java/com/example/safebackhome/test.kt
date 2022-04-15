package com.example.safebackhome;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.safebackhome.models.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

public class test {

    public void delete(Contact contact){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collection = firestore.collection("Contacts");
        Query query = collection.whereEqualTo("UserId", contact.getUserId()).whereEqualTo("ContactFullName", contact.getFullName());

    }
}
