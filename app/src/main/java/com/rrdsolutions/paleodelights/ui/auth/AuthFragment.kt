package com.rrdsolutions.paleodelights.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.rrdsolutions.paleodelights.R
import com.rrdsolutions.paleodelights.ui.deliverystatus.DNService
import com.rrdsolutions.paleodelights.ui.menudetail.MenuDetailViewModel

class AuthFragment : Fragment() {

    val vm: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signup()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {

            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val phonenumber = user.phoneNumber as String

                    vm.setPhoneNumber(phonenumber)

                    vm.checkDelivery{deliveryPresent->
                        if (deliveryPresent){
                            activity?.startService(Intent(activity, DNService::class.java))

                        }
                    }
                    view?.let {
                        Navigation.findNavController(it)
                            .navigate(R.id.action_login)
                    }

                }
            }
            else{
                if (response == null) activity?.finish()
            }

        }
    }

    fun signup(){
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null){
            val provider = arrayListOf(
                AuthUI.IdpConfig.PhoneBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(provider)
                    .setTheme(R.style.LoginTheme2)
                    .build(), 1)
        }
        else{
            view?.let {
                Navigation.findNavController(it)
                    .navigate(R.id.action_login)

            }
        }
    }
}