package fr.isen.orso.androidsmartdevice

import android.content.Intent
import android.util.Log
import fr.isen.orso.androidsmartdevice.databinding.ActivityMainBinding
import android.bluetooth.BluetoothGattCallback
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothDevice
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //Initialiser l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Gestionnaire d'événements pour le bouton de démarrage
        binding.startButton.setOnClickListener {
            //Démarrer ScanActivity
            startActivity(Intent(this, ScanActivity::class.java))
        }
    }

    // Fonctions de cycle
    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("MainActivity", "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("MainActivity", "onDestroy")
        super.onDestroy()
    }
}