package com.example.safebackhome.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.safebackhome.R

class RegisterFragment2 : Fragment() {

    lateinit var fnameText : EditText
    lateinit var lnameText : EditText
    lateinit var pinText : EditText
    lateinit var fakePinText : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        declareViews()
    }

    private fun declareViews(){
        fnameText = requireView().findViewById<EditText>(R.id.register_editfirstname)
        lnameText = requireView().findViewById<EditText>(R.id.register_editlastname)
        pinText = requireView().findViewById<EditText>(R.id.register_editPin)
        fakePinText = requireView().findViewById<EditText>(R.id.register_editFakePin)
    }
}