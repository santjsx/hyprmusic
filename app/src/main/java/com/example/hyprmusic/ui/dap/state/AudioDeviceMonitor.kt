package com.example.hyprmusic.ui.dap.state

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

/**
 * Real-time audio output monitor that observes connected hardware (Bluetooth, Wired, USB-C, Speaker)
 * and formats the display string without requiring privileged runtime Bluetooth permissions.
 */
@Composable
fun rememberConnectedAudioDevice(): String {
    val context = LocalContext.current
    val audioManager = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    var deviceName by remember {
        mutableStateOf(resolveOutputDeviceName(audioManager))
    }

    DisposableEffect(context, audioManager) {
        val callback = object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                deviceName = resolveOutputDeviceName(audioManager)
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                deviceName = resolveOutputDeviceName(audioManager)
            }
        }

        audioManager.registerAudioDeviceCallback(callback, null)

        val noisyReceiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                deviceName = resolveOutputDeviceName(audioManager)
            }
        }

        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(noisyReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(noisyReceiver, filter)
        }

        onDispose {
            audioManager.unregisterAudioDeviceCallback(callback)
            try {
                context.unregisterReceiver(noisyReceiver)
            } catch (_: Exception) {}
        }
    }

    return deviceName
}

private fun resolveOutputDeviceName(audioManager: AudioManager): String {
    try {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        // 1. Check for Active Bluetooth Outputs
        val bluetoothDevice = devices.firstOrNull { device ->
            device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
            device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && device.type == AudioDeviceInfo.TYPE_BLE_HEADSET) ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && device.type == AudioDeviceInfo.TYPE_BLE_SPEAKER)
        }

        if (bluetoothDevice != null) {
            val name = bluetoothDevice.productName?.toString()?.trim()
            if (!name.isNullOrBlank()) {
                return name
            }
            return "Bluetooth Audio"
        }

        // 2. Check for Wired Audio / USB DAC
        val wiredDevice = devices.firstOrNull { device ->
            device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
            device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
            device.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
            device.type == AudioDeviceInfo.TYPE_USB_DEVICE
        }

        if (wiredDevice != null) {
            val name = wiredDevice.productName?.toString()?.trim()
            if (!name.isNullOrBlank() && !name.contains("Android", ignoreCase = true)) {
                return name
            }
            return if (wiredDevice.type == AudioDeviceInfo.TYPE_USB_HEADSET || wiredDevice.type == AudioDeviceInfo.TYPE_USB_DEVICE) {
                "USB-C Audio"
            } else {
                "Wired Headphones"
            }
        }
    } catch (_: Exception) {}

    // 3. Fallback to Built-in Speaker
    return "Phone Speaker"
}
