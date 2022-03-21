package com.example.safebackhome.models

import com.google.firebase.firestore.DocumentReference

class User (var id : String, var email : String, var firstName : String, var lastName : String, var pin : String, var fakePin : String, var contacts : ArrayList<Contact> = ArrayList<Contact>()) {

    public fun toHashMap() : HashMap<String, Any>{
        var userInfo : HashMap<String, Any> = HashMap<String, Any>()
        userInfo.put("Id", id)
        userInfo.put("FirstName", firstName)
        userInfo.put("LastName", lastName)
        userInfo.put("Email", email)
        userInfo.put("PIN", pin)
        userInfo.put("FakePin", fakePin)

        return userInfo
    }
}