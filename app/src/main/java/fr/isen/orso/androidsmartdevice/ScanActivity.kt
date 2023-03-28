package fr.isen.orso.androidsmartdevice
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.os.Looper


class ScanActivity : AppCompatActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            // Ajouter l'appareil à la liste
        }
    }

    private fun startScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            bluetoothAdapter.bluetoothLeScanner.startScan(null, scanSettings, scanCallback)

            // Arrêter le scan après un délai
            Handler(Looper.getMainLooper()).postDelayed({
                stopScan()
            }, SCAN_PERIOD)
        } else {
            Toast.makeText(this, "Permission de localisation requise pour démarrer le scan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
        } else {
            Toast.makeText(this, "Permission de localisation requise pour arrêter le scan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onDeviceClicked(device: BluetoothDevice) {
        val intent = Intent(this, DeviceDetailsActivity::class.java)
        intent.putExtra("device", device)
        startActivity(intent)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Récupération du BluetoothManager
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        if (bluetoothManager == null) {
            Toast.makeText(this, "Erreur : Bluetooth non supporté", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialisation du BluetoothAdapter
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Erreur : Bluetooth non disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        val scanImage: ImageView = findViewById(R.id.scan_image)
        val bleDevicesList: ListView = findViewById(R.id.ble_devices_list)

        fun requestEnableBluetooth() {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT)
            } else {
                Toast.makeText(
                    this,
                    "Permission Bluetooth Admin requise pour activer le Bluetooth",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        scanImage.setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT)
            } else {
                // Gérer l'interaction avec le bouton
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            // Initialiser le scan
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission a été accordée
                    Toast.makeText(this, "Permission de localisation accordée", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // La permission a été refusée
                    Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // Bluetooth activé, initialiser la liste des appareils
            } else {
                Toast.makeText(this, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_LOCATION_PERMISSION = 2
        private const val SCAN_PERIOD = 10000L // 10 secondes
        private const val PERMISSION_REQUEST_LOCATION = 1
    }
}