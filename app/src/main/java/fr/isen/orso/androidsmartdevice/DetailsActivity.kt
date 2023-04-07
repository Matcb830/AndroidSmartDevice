package fr.isen.orso.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import fr.isen.orso.androidsmartdevice.databinding.ActivityDetailsBinding
import java.util.*

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private var bluetoothGatt: BluetoothGatt? = null
    private val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
    private val characteristicLedUUID = UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19")
    private val characteristicButtonUUID = UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")
    private var clickCount = 0
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialisation de l'activité et connexion au périphérique Bluetooth
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bluetoothDevice: BluetoothDevice? = intent.getParcelableExtra("device")

        bluetoothGatt = bluetoothDevice?.connectGatt(this, false, bluetoothGattCallback)
        // bluetoothGatt?.connect()

        // Gestion du clic sur les LEDs
        clickOnLed()
    }
    @SuppressLint("MissingPermission")
    override fun onStop(){
        // Fermeture de la connexion BluetoothGatt
        super.onStop()
        bluetoothGatt?.close()
    }

    @SuppressLint("MissingPermission")
    // Fonction gérant les clics sur les LEDs et l'envoi des commandes
    private fun clickOnLed(){
        // Gestion du clic sur la LED 1
        binding.ledImage1.setOnClickListener{
            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)
            if(binding.ledImage1.imageTintList == getColorStateList(R.color.teal_200)){
                binding.ledImage1.imageTintList = getColorStateList(R.color.black)
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else{
                binding.ledImage1.imageTintList = getColorStateList(R.color.teal_200)
                binding.ledImage2.imageTintList = getColorStateList(R.color.black)
                binding.ledImage3.imageTintList = getColorStateList(R.color.black)
                clickCount ++
                binding.clickCount.text="Nombre de click : : $clickCount "
                characteristic?.value = byteArrayOf(0x01)
                bluetoothGatt?.writeCharacteristic(characteristic)

            }
        }
        // Gestion du clic sur la LED 2
        binding.ledImage2.setOnClickListener{
            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if(binding.ledImage2.imageTintList == getColorStateList(R.color.teal_200)){
                binding.ledImage2.imageTintList = getColorStateList(R.color.black)
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else{
                binding.ledImage2.imageTintList = getColorStateList(R.color.teal_200)
                binding.ledImage1.imageTintList = getColorStateList(R.color.black)
                binding.ledImage3.imageTintList = getColorStateList(R.color.black)
                clickCount ++
                binding.clickCount.text="Nombre de click : : $clickCount "
                characteristic?.value = byteArrayOf(0x02)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
        }
        // Gestion du clic sur la LED 3
        binding.ledImage3.setOnClickListener{
            val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicLedUUID)

            if(binding.ledImage3.imageTintList == getColorStateList(R.color.teal_200)){
                binding.ledImage3.imageTintList = getColorStateList(R.color.black)
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else{
                binding.ledImage3.imageTintList = getColorStateList(R.color.teal_200)
                binding.ledImage1.imageTintList = getColorStateList(R.color.black)
                binding.ledImage2.imageTintList = getColorStateList(R.color.black)
                clickCount ++
                binding.clickCount.text="Nombre de click : : $clickCount "
                characteristic?.value = byteArrayOf(0x03)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
        }
    }

    private val bluetoothGattCallback = object: BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        // Méthode appelée lors d'un changement d'état de la connexion
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread{
                    displayContentConnected()
                }
                bluetoothGatt?.discoverServices()
            }
        }
        @SuppressLint("MissingPermission")

        override fun onServicesDiscovered(bluetoothGatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(bluetoothGatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val characteristicButton3 = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicButtonUUID)
                bluetoothGatt?.setCharacteristicNotification(characteristicButton3, true)
                characteristicButton3?.descriptors?.forEach { descriptor ->
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    bluetoothGatt.writeDescriptor(descriptor)
                }
            }
        }

        // Méthode appelée lorsqu'une caractéristique change de valeur
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            if (characteristic.uuid == characteristicButtonUUID) {
                val value = characteristic.value
                val clicks = value[0].toInt()
                runOnUiThread {
                    binding.clickCount.text = "Nombre de click: ${clicks.toString()}"
                }
            }
        }

    }

    // Fonction pour mettre à jour l'affichage lorsque l'appareil est connecté
    private fun displayContentConnected(){
        // Mise à jour de l'interface utilisateur
        binding.deviceConnectedStatus.text = "BLE"
        binding.ledDisplayText.text = "Affichage des LEDs"
        binding.subscribeText.isVisible = true
        binding.subscriptionCheckBox.isVisible = true
        binding.connectionProgress.isVisible = false
        binding.ledImage1.isVisible = true
        binding.ledImage2.isVisible = true
        binding.ledImage3.isVisible = true
        binding.clickCount.isVisible = true
    }
}