package com.example.safebackhome.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.safebackhome.R


class RegisterFragment1 : Fragment() {
    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var passwordText2: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        declareViews()
    }

    private fun declareViews(){
        try {
            emailText = requireView().findViewById<EditText>(R.id.register_email_edit)
            passwordText = requireView().findViewById<EditText>(R.id.register_editpassword)
            passwordText2 = requireView().findViewById<EditText>(R.id.register_editconfirmpassword)
        }
        catch (e:Exception){
            Log.e("Fragment1 Error", e.message.toString(), e)
        }
    }

}