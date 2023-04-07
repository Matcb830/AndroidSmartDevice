package fr.isen.orso.androidsmartdevice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.orso.androidsmartdevice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var bindingInstance: ActivityMainBinding

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        bindingInstance = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingInstance.root)

        bindingInstance.start.setOnClickListener {
            val newIntent = Intent(this, ScanActivity::class.java)
            startActivity(newIntent)
        }
    }
}