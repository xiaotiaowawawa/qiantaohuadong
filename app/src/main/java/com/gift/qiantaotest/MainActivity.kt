package com.gift.qiantaotest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.collection.SimpleArrayMap
import androidx.coordinatorlayout.widget.CoordinatorLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val arrayMap=SimpleArrayMap<Int,String>()
    }
}