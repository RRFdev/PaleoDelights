package com.rrdsolutions.paleodelights
import android.content.Context
import android.util.Log
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class Tests {

    @Test
    fun test() {
        fun add(x : Int, y: Int): Int{
            return (x + y)
        }

        assertEquals(4, add(1, 3))
    }

    @Test
    fun testString(){
        fun test():String{
            return "Haha"
        }

        assertEquals("Haha", test())
    }

}