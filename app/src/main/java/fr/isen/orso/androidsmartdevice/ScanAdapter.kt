package fr.isen.orso.androidsmartdevice

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import fr.isen.orso.androidsmartdevice.databinding.ScanCellBinding;


class ScanAdapter(
    var devices: ArrayList<android.bluetooth.BluetoothDevice>,
    var onDeviceClickListener: (android.bluetooth.BluetoothDevice) -> Unit) : RecyclerView.Adapter<ScanAdapter.ScanViewHolder>() {

    // Crée un nouveau ViewHolder pour contenir les éléments de la vue
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScanCellBinding.inflate(inflater, parent, false)
        return ScanViewHolder(binding)
    }

    // Renvoie le nombre d'éléments dans la liste des appareils
    override fun getItemCount(): Int {
        return devices.size
    }

    // Lie les données de l'appareil
    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.deviceName.text = devices[position].name ?: "Inconnu"
        holder.deviceAddress.text = devices[position].address
        holder.itemView.setOnClickListener {
            onDeviceClickListener(devices[position])
        }
    }

    // contenir les éléments
    class ScanViewHolder(binding: ScanCellBinding) : RecyclerView.ViewHolder(binding.root) {
        val deviceName = binding.nameDevice
        val deviceAddress = binding.IPaddress
    }

    // Ajoute un nouvel appareil à la liste s'il n'est pas déjà présent
    fun addDevice(device: android.bluetooth.BluetoothDevice) {
        var shouldAddDevice = true
        devices.forEachIndexed { index, bluetoothDevice ->
            if (bluetoothDevice.address == device.address) {
                devices[index] = device
                shouldAddDevice = false
            }
        }
        if (shouldAddDevice) {
            devices.add(device)
        }
    }
}