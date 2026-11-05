package de.obddiag

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/** Verbindung zu einem ELM327-kompatiblen Bluetooth-Classic-Adapter (SPP). Blockierend, nicht im UI-Thread aufrufen. */
class Elm327 {
    private var socket: BluetoothSocket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        val s = device.createRfcommSocketToServiceRecord(SPP_UUID)
        s.connect()
        socket = s; input = s.inputStream; output = s.outputStream
        send("ATZ", 3000)
        for (c in listOf("ATE0", "ATL0", "ATS0", "ATH0", "ATSP0")) send(c)
    }

    /** Sendet einen Befehl und liest bis zum '>'-Prompt. */
    @Synchronized
    fun send(cmd: String, timeoutMs: Long = 6000): String {
        val out = output ?: error("Nicht verbunden")
        val inp = input ?: error("Nicht verbunden")
        while (inp.available() > 0) inp.read()
        out.write((cmd + "\r").toByteArray()); out.flush()
        val sb = StringBuilder()
        val end = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < end) {
            if (inp.available() > 0) {
                val c = inp.read()
                if (c < 0) break
                if (c.toChar() == '>') return sb.toString()
                sb.append(c.toChar())
            } else Thread.sleep(20)
        }
        return sb.toString()
    }

    fun close() {
        try { socket?.close() } catch (_: Exception) {}
        socket = null; input = null; output = null
    }

    companion object {
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}
