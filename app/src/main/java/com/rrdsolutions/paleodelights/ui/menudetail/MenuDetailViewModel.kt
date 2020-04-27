package com.rrdsolutions.paleodelights.ui.menudetail


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MenuDetailViewModel: ViewModel() {


    var counter = MutableLiveData<Int>().apply{value = 0}

}