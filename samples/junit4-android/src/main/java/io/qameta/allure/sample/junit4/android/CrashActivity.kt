package io.qameta.allure.sample.junit4.android

import android.os.Bundle
import android.widget.Button
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CrashActivity : AppCompatActivity() {

    private val crashImmediate: Button
        get() = findViewById(R.id.button_crash_immediate)
    private val crashAsync: Button
        get() = findViewById(R.id.button_crash_async)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        crashImmediate.setOnClickListener {
            throw IllegalStateException("Crashing on purpose!")
        }
        crashAsync.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                Thread.sleep(ASYNC_CRASH_TIME_MS)
                throw IllegalStateException("Crashing async on purpose!")
            }
        }
    }

    companion object {
        @VisibleForTesting
        val ASYNC_CRASH_TIME_MS: Long = TimeUnit.SECONDS.toMillis(1)
    }
}
