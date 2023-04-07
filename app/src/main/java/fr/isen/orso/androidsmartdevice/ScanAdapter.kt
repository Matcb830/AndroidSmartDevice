package fr.isen.orso.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.isen.orso.androidsmartdevice.databinding.ScanCellBinding

class ScanAdapter(var devices: ArrayList<BluetoothDevice>, var onDeviceClickListener: (BluetoothDevice) -> Unit) : RecyclerView.Adapter<ScanAdapter.ScanViewHolder>() {
    // Liste des valeurs RSSI pour chaque appareil Bluetooth
    private var rssiValues = arrayListOf<Int>() //
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScanCellBinding.inflate(inflater, parent, false)
        return ScanViewHolder(binding)
    }

    // Retourne le nombre d'appareils dans la liste
    override fun getItemCount(): Int = devices.size

    @SuppressLint("MissingPermission")
    // Associe les données d'un appareil Bluetooth à un ViewHolder
    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.deviceAddress.text = devices[position].address
        holder.RSSI.text = rssiValues[position].toString() // <-- Accéder à la valeur RSSI correspondante dans la liste rssiValues
        holder.deviceName.text = devices[position].name ?: "Inconnu"
        holder.itemView.setOnClickListener { onDeviceClickListener(devices[position]) }
    }

    // ViewHolder pour afficher les informations d'un appareil Bluetooth
    class ScanViewHolder(binding: ScanCellBinding): RecyclerView.ViewHolder(binding.root){
        val deviceName : TextView = binding.deviceNameTextView
        val deviceAddress : TextView = binding.deviceAddressTextView
        val RSSI : TextView = binding.rssiTextView
    }


    // Ajoute un nouvel appareil Bluetooth à la liste ou met à jour un appareil existant avec une nouvelle valeur RSS
    fun addDevice(device: BluetoothDevice, rssi: Int) {
        val deviceIndex = devices.indexOfFirst { it.address == device.address }
        if (deviceIndex != -1) {
            devices[deviceIndex] = device
            rssiValues[deviceIndex] = rssi
        } else {
            devices.add(device)
            rssiValues.add(rssi)
        }
    }

}



