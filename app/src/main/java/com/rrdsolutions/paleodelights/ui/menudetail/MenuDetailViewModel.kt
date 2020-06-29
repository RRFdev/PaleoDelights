package com.rrdsolutions.paleodelights.ui.menudetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rrdsolutions.paleodelights.Menu

class MenuDetailViewModel(private var ssh: SavedStateHandle): ViewModel() {

    lateinit var menu: Menu

    var counter = ssh.getLiveData("counter", 0)

    fun setCounterValue(i:Int){
        ssh.set("counter", i)
    }
    fun increaseValue(){
        var counter = counter.value as Int
        counter++
        setCounterValue(counter)
    }
    fun decreaseValue(){
        var counter = counter.value as Int
        if (counter>0){
            counter--
            setCounterValue(counter)
        }
    }

    var bool = ssh.get<Boolean>("bool")?:false
    fun setbool(i:Boolean){
        bool = i
        ssh.set("bool", bool)
    }

}