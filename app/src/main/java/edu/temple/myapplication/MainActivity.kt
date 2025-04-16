package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream

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

    private val saveFile = "countdown"
    private lateinit var file: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        file = File(filesDir, saveFile)
        timerTextView = findViewById(R.id.textView)
        findViewById<Button>(R.id.startButton).setOnClickListener {
            onStartButtonClick()
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            onStopButtonClick()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_start -> onStartButtonClick()
            R.id.action_stop -> onStopButtonClick()
            else -> return false
        }
        return true
    }


    private fun onStartButtonClick() {
        if (isConnected) {
            var startValue = 100
            if (file.exists()) {
                startValue = file.readText().toInt()
                file.delete()
            }
            timerBinder.start(startValue)
        }
    }

    private fun onStopButtonClick() {
        if (isConnected) timerBinder.stop()
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(timerTextView.text.toString().toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}