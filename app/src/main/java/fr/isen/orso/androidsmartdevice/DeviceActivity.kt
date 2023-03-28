package fr.isen.orso.androidsmartdevice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DeviceActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ADDRESS = "device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        val address = intent.getStringExtra(EXTRA_ADDRESS)
        findViewById<TextView>(R.id.device_address).text = address
    }
}