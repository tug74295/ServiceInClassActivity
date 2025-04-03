package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var timerTextView: TextView
    lateinit var timerBinder: TimerService.TimerBinder
    var isConnected = false

    val handler = Handler(Looper.getMainLooper()) {
        timerTextView.text = it.what.toString()
        true
    }
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(handler)
            isConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timerTextView = findViewById(R.id.textView)
        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (isConnected) timerBinder.start(
                startValue = 100
            )
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isConnected) timerBinder.stop()
        }

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE)
        }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}