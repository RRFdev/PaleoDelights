package com.rrdsolutions.paleodelights.ui.uploadmenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.rrdsolutions.paleodelights.MenuModel
import com.rrdsolutions.paleodelights.R
import kotlinx.android.synthetic.main.fragment_uploadmenu.*

//import com.rrdsolutions.paleodelights.repositories.MenuObject2

class UploadMenuFragment: Fragment() {

    lateinit var viewmodel: UploadMenuViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val root = inflater.inflate(R.layout.fragment_uploadmenu, container, false)
        viewmodel = ViewModelProviders.of(this).get(UploadMenuViewModel::class.java)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener{
//            resulttext.text = "Submitting..."
//            saveToFirebase(){it->
//                if (it == true){
//                    resulttext.text = "Menu file submitted"
//                }
//                else {
//                    resulttext.text = "Failure! Menu file not submitted"
//                }
//            }
//            loadnew(){
//                Log.d("dbtest", it.toString())
//            }
//            MenuModel.loadMenuFromFirebase {
//                Log.d("MenuModel", "Function completed")
//            }
        }

    }

    fun saveToFirebase(callback: (Boolean) -> Unit){

        val savedmenu = Gson().toJson(viewmodel.menu)

        val hashed = hashMapOf(
            "saved menu" to savedmenu
        )

        val db = FirebaseFirestore.getInstance()
            .collection("mobile app data").document("menu items")

        db.set(hashed)
            .addOnSuccessListener{
                //resulttext.text = "Menu file submitted"
                callback(true)
            }
            .addOnFailureListener{
                //resulttext.text = "Failure! Menu file not submitted"
                callback(false)
            }

    }





    fun loadnew(callback: (Boolean)->Unit){

        data class Result(
           val name: String,
           val image: String,
           val price: Long
        )
        val resultlist = mutableListOf<Result>()
        FirebaseFirestore.getInstance().collection("foods").get()
                .addOnSuccessListener{documents->
                    Log.d("dbtest", "data loaded")
                    for (document in documents){
                        val name = document.id
                        val image = document.getString("image") as String
                        val price = document.getLong("price") as Long

                        val result = Result(name, image, price)
                        resultlist.add(result)
                        callback(true)
                    }
                    Log.d("dbtest", resultlist[0].name)
                }




    }








}