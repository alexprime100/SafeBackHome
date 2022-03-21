package com.example.safebackhome.models

class Contact(var userId : String, var fullName: String, var phoneNumber: String){

    public fun toHashMap() : HashMap<String, Any>{
        var contactInfo = HashMap<String, Any>()
        contactInfo.put("UserId", userId)
        contactInfo.put("ContactFullName", fullName)
        contactInfo.put("ContactNumber", phoneNumber)

        return contactInfo
    }
}