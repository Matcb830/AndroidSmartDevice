package fr.isen.orso.androidsmartdevice

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.Manifest.permission.*;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import fr.isen.orso.androidsmartdevice.databinding.ActivityMainBinding
import android.widget.ProgressBar
import fr.isen.orso.androidsmartdevice.databinding.ActivityScanBinding;

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private var mScanning = false
    private lateinit var adapter: ScanAdapter
    private val bluetoothAdapter: BluetoothAdapter? by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val handler = Handler(Looper.getMainLooper())

    // Gestion des permissions requises
    private val REQUIRED_PERMISSIONS = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            val scanBlue = BLUETOOTH_SCAN
            if (ContextCompat.checkSelfPermission(this, scanBlue) == PackageManager.PERMISSION_GRANTED) {
                Log.e("Scan", "OK")
                scanLeDevice()
            } else {
                Log.e("Scan", "Pas Ok")
            }
        }
    }

    // Callback résultats de scan
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d("scan", "result: $result")
            adapter.addDevice(result.device)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Vérification de l'état du Bluetooth
        if (bluetoothAdapter?.isEnabled == true) {
            scanDeviceWithPermissions()
            Toast.makeText(this, "bluetooth activé", Toast.LENGTH_SHORT).show()
        } else {
            handleBLENotAvailable()
            Toast.makeText(this, "bluetooth désactivé", Toast.LENGTH_SHORT).show()
        }

        // Initialisation de l'adaptateur et des éléments de la liste
        adapter = ScanAdapter(arrayListOf()) {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("device", it)
            startActivity(intent)
        }
        binding.ListDevice.adapter = adapter
    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        if (bluetoothAdapter?.isEnabled == true && allPermissionsGranted()) {
            mScanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    // Initialisation actions interface utilisateur
    private fun initToggleActions() {
        binding.scanTitle.setOnClickListener { scanLeDevice() }
        binding.ButtonScan.setOnClickListener { scanLeDevice() }
        binding.ListDevice.layoutManager = LinearLayoutManager(this)
        adapter = ScanAdapter(arrayListOf()) {
            val intent = Intent(this, ScanAdapter::class.java)
            intent.putExtra("device", it)
            startActivity(intent)
        }
        binding.ListDevice.adapter = adapter
    }

    // Gestion du Bluetooth non disponible
    private fun handleBLENotAvailable() {
        binding.scanTitle.text = getString(R.string.ble_scan_title_pause)
    }

    // Gestion des permissions pour le scan
    private fun scanDeviceWithPermissions() {
        if (allPermissionsGranted()) {
            initToggleActions()
            scanLeDevice()
        } else {
            REQUIRED_PERMISSIONS.launch(getAllPermission())
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        // Si la numérisation n'est pas en cours, commencez à numériser
        if (!mScanning) {
            handler.postDelayed({
                mScanning = false
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
                togglePlayPauseAction()
            }, SCAN_PERIOD)
            mScanning = true
            bluetoothAdapter?.bluetoothLeScanner?.startScan(leScanCallback)
        } else { // Si la numérisation est en cours, arrêtez la numérisation
            mScanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        }
        togglePlayPauseAction()
    }

    // Vérifie si toutes les autorisations sont accordées
    private fun allPermissionsGranted(): Boolean {
        val allPermissions = getAllPermission()
        return allPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED
        } || requestPermissions(allPermissions)
    }

    // Demande les autorisations nécessaires
    private fun requestPermissions(permissions: Array<String>): Boolean {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSION)
        return false
    }

    // Obtient toutes les autorisations requises
    private fun getAllPermission(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                BLUETOOTH_SCAN,
                ACCESS_FINE_LOCATION,
                BLUETOOTH_CONNECT,
                ACCESS_COARSE_LOCATION)
        } else {
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION)
        }
    }

    // Change l'icône et le texte en fonction de l'état de la numérisation
    private fun togglePlayPauseAction() {
        if (mScanning) {
            binding.scanTitle.text = getString(R.string.ble_scan_title_pause)
            binding.ButtonScan.setImageResource(R.drawable.baseline_pause_circle_outline_24)
            binding.progressBar.isIndeterminate=true
        } else {
            binding.scanTitle.text = getString(R.string.ble_scan_title_play)
            binding.ButtonScan.setImageResource(R.drawable.baseline_play_circle_outline_24)
            binding.progressBar.isIndeterminate=false
        }
    }

    // Démarre la numérisation si le Bluetooth est disponible et activé
    private fun startScan(bluetoothLeScanner: BluetoothLeScanner) {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non disponible", Toast.LENGTH_SHORT).show()
            return
        }
        if (bluetoothAdapter?.isEnabled == false) {
            Toast.makeText(this, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
            return
        }
        if (bluetoothAdapter?.isEnabled == true) {
            binding.ButtonScan.setOnClickListener() {
                togglePlayPauseAction()
                scanLeDevice()
            }
        }
    }

    // Gère les résultats
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // L'autorisation Bluetooth a été accordée
                } else {
                    // L'autorisation Bluetooth a été refusée
                    Toast.makeText(this, "Vous avez besoin du bluetooth pour utiliser l'application", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION=1
        private const val SCAN_PERIOD: Long= 10000
    }
}