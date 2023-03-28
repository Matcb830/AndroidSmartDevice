package fr.isen.orso.androidsmartdevice

import android.Manifest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.aware.Characteristics
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import fr.isen.orso.androidsmartdevice.databinding.ActivityDeviceControlBinding
import fr.isen.orso.androidsmartdevice.R.layout.activity_device_control
import java.util.UUID

class DeviceControlActivity : AppCompatActivity() {
    private val SERVICE_UUID = UUID.fromString("your_service_uuid_here")
    private val CHARACTERISTIC_UUID = UUID.fromString("your_characteristic_uuid_here")
    private lateinit var deviceAddress: String
    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var binding: ActivityDeviceControlBinding


    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            runOnUiThread {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        binding.textConnectionStatus.text = "Connecté"
                        if (ActivityCompat.checkSelfPermission(
                                this@DeviceControlActivity,
                                android.Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return@runOnUiThread
                        }
                        gatt.discoverServices()
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        binding.textConnectionStatus.text = "Déconnecté"
                        setUiElementsVisibility(View.GONE)
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                runOnUiThread {
                    setUiElementsVisibility(View.VISIBLE)
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            runOnUiThread {
                when (characteristic.uuid) {
                    FIRST_NOTIFICATION_CHAR_UUID -> {
                        // Affichez la première notification (nombre de clics du bouton principal)
                        val value =
                            characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        binding.textClickCount.text = "Nombre de clics: $value"
                    }
                    SECOND_NOTIFICATION_CHAR_UUID -> {
                        // Gérez la seconde notification (nombre de clics du troisième bouton)
                    }
                }
            }
        }
    }

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
             != PackageManager.PERMISSION_GRANTED) {
             // Demander la permission BLUETOOTH si elle n'a pas été accordée
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH),
                 PERMISSION_REQUEST_CODE)
         }



         deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS) ?: ""

        binding.buttonToggleLed.setOnClickListener {
            bluetoothGatt?.getService(SERVICE_3_UUID)?.getCharacteristic(LED_CHAR_UUID)
                ?.let { characteristic ->
                    // Modifiez la valeur de la caractéristique en fonction de la façon dont vous contrôlez les LED
                    characteristic.value = byteArrayOf(0x01) // Par exemple, 0x

                }
            binding.buttonToggleLed.setOnClickListener {
                bluetoothGatt?.getService(SERVICE_3_UUID)?.getCharacteristic(LED_CHAR_UUID)?.let { characteristic ->
                    // Modifier la valeur de la caractéristique en fonction de la façon dont vous contrôlez les LED
                    characteristic.value = byteArrayOf(0x01) // Par exemple, 0x

                }
                binding.buttonReadNotifications.setOnClickListener {
                    bluetoothGatt?.getService(SERVICE_3_UUID)?.getCharacteristic(FIRST_NOTIFICATION_CHAR_UUID)?.let { characteristic ->
                        bluetoothGatt?.setCharacteristicNotification(characteristic, true)
                    }

                    bluetoothGatt?.getService(SERVICE_4_UUID)?.getCharacteristic(SECOND_NOTIFICATION_CHAR_UUID)?.let { characteristic ->
                        bluetoothGatt?.setCharacteristicNotification(characteristic, true)
                    }
                    // Gérer la lecture des notifications
                }
            }

        }
    }

        override fun onResume() {
            super.onResume()

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION_PERMISSION
                )
            } else {
                connectToDevice()
            }
        }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission accordée, activer les fonctionnalités Bluetooth ici
                } else {
                    // Permission refusée, informer l'utilisateur ici
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

        private fun connectToDevice() {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION_PERMISSION)
            } else {
                val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
                bluetoothGatt = device.connectGatt(this, false, gattCallback)
            }
        }



        override fun onPause() {
            super.onPause()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Demander la permission ACCESS_FINE_LOCATION si elle n'a pas été accordée
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION_PERMISSION)
            } else {
                connectToDevice()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                // Demander la permission BLUETOOTH si elle n'a pas été accordée
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), PERMISSION_REQUEST_CODE)
            } else {
                val characteristic = bluetoothGatt?.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_UUID)
                bluetoothGatt?.setCharacteristicNotification(characteristic, true)
            }

        }

        private fun setUiElementsVisibility(visibility: Int) {
            binding.buttonToggleLed.visibility = visibility
            binding.textClickCount.visibility = visibility
            binding.buttonReadNotifications.visibility = visibility
        }

        companion object {
            const val EXTRA_DEVICE_ADDRESS = "device_address"
            private const val REQUEST_FINE_LOCATION_PERMISSION = 1001
            private const val PERMISSION_REQUEST_CODE = 1
            private val SERVICE_3_UUID = UUID.fromString("votre_service_3_uuid")
            private val SERVICE_4_UUID = UUID.fromString("votre_service_4_uuid")
            private val LED_CHAR_UUID = UUID.fromString("votre_led_char_uuid")
            private val FIRST_NOTIFICATION_CHAR_UUID =
                UUID.fromString("votre_first_notification_char_uuid")
            private val SECOND_NOTIFICATION_CHAR_UUID =
                UUID.fromString("votre_second_notification_char_uuid")

            fun newIntent(context: Context, deviceAddress: String): Intent {
                val intent = Intent(context, DeviceControlActivity::class.java)
                intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress)
                return intent
            }
        }
}
