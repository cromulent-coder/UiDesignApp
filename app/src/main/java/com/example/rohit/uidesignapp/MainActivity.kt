package com.example.rohit.uidesignapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.rohitupreti.pinedittext.PinText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var pinText = findViewById<PinText>(R.id.pinText)

        pinText.text = "HelloWorld";

    }
}
