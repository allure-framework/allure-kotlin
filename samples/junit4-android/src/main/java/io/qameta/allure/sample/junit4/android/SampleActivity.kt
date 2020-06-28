package io.qameta.allure.sample.junit4.android

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        findViewById<Button>(R.id.button_some).setOnClickListener {
            findViewById<TextView>(R.id.label_some).text = getString(R.string.after_click)
        }
    }

}
