package com.euzhene.comranet.util

import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService

class NetworkConnection{

    companion object {
         var onAvailable:(()->Unit)? = null
         var onCapabilitiesChanged:(()->Unit)? = null
         var onLost:(()->Unit)? = null

        private val networkRequest by lazy {
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
        }

        private val networkCallback by lazy {
            object : ConnectivityManager.NetworkCallback() {
                // network is available for use
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    onAvailable?.invoke()
                }

                // Network capabilities have changed for the network
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    onCapabilitiesChanged?.invoke()

                }

                // lost network connection
                override fun onLost(network: Network) {
                    super.onLost(network)
                    onLost?.invoke()
                }
            }
        }

        fun initNetworkStatus(context: Context) {
            val connectivityManager = context.getSystemService<ConnectivityManager>()!!
            connectivityManager.requestNetwork(networkRequest, networkCallback)
        }

    }
}