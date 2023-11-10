package com.example.searchbluetooth


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.searchbluetooth.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NumberFormatException

var nombreDevice: String? = null
var adapterBlue: BluetoothAdapter? = null
var lis: ArrayList<BluetoothDevice> = ArrayList()
var listNombreDevices: ArrayList<String> = ArrayList()

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        verificarPerimisos()

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.getAdapter()

        adapterBlue = bluetoothManager.adapter

        binding.btnSearch.setOnClickListener {
            lis.clear()
            listNombreDevices.clear()

            binding.btnSearch.text = "Iniciar..."

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return@setOnClickListener
            }

            if (adapterBlue!!.isDiscovering()) {
                binding.btnSearch.text = "Cancelando Bluetooth..."
                //se cierra para volverse a abrir
                adapterBlue!!.cancelDiscovery()


            }

            binding.btnSearch.text = "Abriendo Bluetooth..."
            adapterBlue!!.startDiscovery()

        }

        val filtrar = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(nReceiber, filtrar)


    }

    var nReceiber = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {

            val action = intent!!.action
            binding.btnSearch!!.text = "Encontrado" + action
            if (BluetoothDevice.ACTION_FOUND == action) {

                //solo obtenemos los permisos del bluetooth
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    return
                }
                Toast.makeText(
                    this@MainActivity,
                    "dispositivo encontrado" + device!!.name,
                    Toast.LENGTH_SHORT
                ).show()

                scanDeviceList(device)
            }

        }

    }

    private fun scanDeviceList(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        binding.btnSearch!!.text = "Encontrado..." + device.name

        nombreDevice = device.name

        if (nombreDevice == null) {
            nombreDevice = device.address

        }

        var b: Boolean = true

        for (devic in listNombreDevices) {
            if (devic == nombreDevice.toString()) {
                b = false
            }
        }

        if (b) {
            listNombreDevices.add(nombreDevice.toString())
            lis.add(device)
            binding.btnSearch!!.text = "Buscar"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listNombreDevices)
        listDevice!!.adapter = adapter


        listDevice!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            val device1: BluetoothDevice = lis[position]
            var address: String

            try {
                address = device1.name

            } catch (e: NumberFormatException) {
                address = device.address

            }

            Toast.makeText(this, address, Toast.LENGTH_SHORT).show()

        }

        listDevice!!.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
                val device1: BluetoothDevice = lis[i]
                val address: String = device1.address
                Toast.makeText(this, address, Toast.LENGTH_SHORT).show()
                return@OnItemLongClickListener true


            }


    }


    private fun verificarPerimisos() {

        val permsRequestCode = 100
        val perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,

            )


        val accessFinePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val accessCoarsePermission =
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            val BluetoothPermission = checkSelfPermission(Manifest.permission.BLUETOOTH)
            if (BluetoothPermission == PackageManager.PERMISSION_GRANTED && accessCoarsePermission ==
                PackageManager.PERMISSION_GRANTED
            ) {

                //SE REALIZA METODO SI ES NECESARIO

            } else {

                requestPermissions(perms, permsRequestCode)
            }

        } else {

            val bluetoothPermission =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {

                //El permiso no está aceptado. Solicitar permiso de Ubicación
                bluetoothPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)

            } else {


            }
        }


    }

}