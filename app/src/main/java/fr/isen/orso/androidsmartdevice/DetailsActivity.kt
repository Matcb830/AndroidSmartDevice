package fr.isen.orso.androidsmartdevice

import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothDevice
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast

class DetailsActivity : AppCompatActivity() {
    private lateinit var loader: ProgressBar
    //initialiser l'activité
    override fun onCreate(savedState: Bundle?) {
    super.onCreate(savedState)
    setContentView(R.layout.activity_details)

    // Récupération de l'appareil Bluetooth
    val btDevice: BluetoothDevice? = intent.getParcelableExtra("device")

    if (btDevice != null) {
        // Si l'appareil Bluetooth est trouvé =  connecter lui
        establishConnection(btDevice)
    } else {
        // iverse + terminer l'activité
        Toast.makeText(this, "Appareil non trouvé", Toast.LENGTH_SHORT).show()
        finish()
    }
}

    // connecter à un appareil
    @SuppressLint("MissingPermission")
    private fun establishConnection(device: BluetoothDevice) {
        //gérer les changements d'état de connexion
        val gattHandler = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gattInstance: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gattInstance, status, newState)

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Si l'appareil est connecté, afficher un message à l'utilisateur
                    runOnUiThread {
                        Toast.makeText(this@DetailsActivity, "Connecté", Toast.LENGTH_SHORT).show()
                    }
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // Si l'appareil est déconnecté, afficher un message à l'utilisateur + terminer l'activité
                    runOnUiThread {
                        Toast.makeText(this@DetailsActivity, "Déconnecté", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        val btGatt = device.connectGatt(this, false, gattHandler)
    }
}