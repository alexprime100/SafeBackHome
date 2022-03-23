package com.example.safebackhome.models

class Contact(var id: String, var userId : String, var fullName: String, var phoneNumber: String, var isFavortie : Boolean){

    public fun toHashMap() : HashMap<String, Any>{
        var contactInfo = HashMap<String, Any>()
        contactInfo.put("Id", id)
        contactInfo.put("UserId", userId)
        contactInfo.put("ContactFullName", fullName)
        contactInfo.put("ContactNumber", phoneNumber)
        contactInfo.put("IsFavortie", isFavortie)

        return contactInfo
    }
}