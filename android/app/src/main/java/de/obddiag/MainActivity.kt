package de.obddiag

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val elm = Elm327()
    private val io = Executors.newSingleThreadExecutor()
    @Volatile private var live = false
    @Volatile private var connected = false
    private lateinit var status: TextView
    private lateinit var log: TextView
    private lateinit var liveBtn: Button
    private val actionButtons = mutableListOf<Button>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(pad, pad, pad, pad) }
        status = TextView(this).apply { textSize = 16f; text = "Nicht verbunden" }
        log = TextView(this).apply { typeface = Typeface.MONOSPACE; textSize = 14f }
        fun btn(t: String, action: () -> Unit) = Button(this).apply { text = t; setOnClickListener { action() } }
        val connect = btn("Mit Adapter verbinden") { pickDevice() }
        val dtc = btn("Fehlerspeicher lesen") { run { readDtcs() } }
        val clear = btn("Fehlerspeicher löschen") { confirmClear() }
        val vin = btn("Fahrgestellnummer (VIN) lesen") { run { readVin() } }
        liveBtn = btn("Live-Daten starten") { toggleLive() }
        actionButtons += listOf(dtc, clear, vin, liveBtn)
        actionButtons.forEach { it.isEnabled = false }
        val scroll = ScrollView(this).apply { addView(log) }
        root.addView(status); root.addView(connect)
        for (b in actionButtons) root.addView(b)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        requestBtPermission()
    }

    private fun requestBtPermission() {
        if (Build.VERSION.SDK_INT >= 31 &&
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
    }

    private fun ui(f: () -> Unit) = runOnUiThread(f)
    private fun setLog(t: String) = ui { log.text = t }
    private fun setStatus(t: String) = ui { status.text = t }

    private fun run(block: () -> Unit) {
        io.execute {
            try { block() } catch (e: Exception) { setLog("Fehler: ${e.message}") }
        }
    }

    @SuppressLint("MissingPermission")
    private fun pickDevice() {
        val adapter: BluetoothAdapter? = (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
        if (adapter == null || !adapter.isEnabled) { setStatus("Bluetooth ist aus – bitte einschalten"); return }
        if (Build.VERSION.SDK_INT >= 31 &&
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestBtPermission(); return
        }
        val devices: List<BluetoothDevice> = adapter.bondedDevices.toList()
        if (devices.isEmpty()) {
            setStatus("Kein gekoppeltes Gerät. Adapter zuerst in den Android-Bluetooth-Einstellungen koppeln (PIN meist 1234 oder 0000).")
            return
        }
        AlertDialog.Builder(this).setTitle("Adapter wählen")
            .setItems(devices.map { "${it.name} (${it.address})" }.toTypedArray()) { _, i -> connectTo(devices[i]) }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun connectTo(d: BluetoothDevice) {
        setStatus("Verbinde mit ${d.name} …")
        run {
            live = false
            elm.close()
            try {
                elm.connect(d)
                connected = true
                val v = ObdParser.parseVoltage(elm.send("ATRV"))
                setStatus("Verbunden: ${d.name}" + (v?.let { " · Batterie $it" } ?: ""))
                ui { actionButtons.forEach { it.isEnabled = true }; liveBtn.text = "Live-Daten starten" }
            } catch (e: Exception) {
                connected = false
                setStatus("Verbindung fehlgeschlagen: ${e.message}")
            }
        }
    }

    private fun readDtcs() {
        setLog("Lese Fehlerspeicher …")
        val stored = elm.send("03")
        if (ObdParser.isNoData(stored) && ObdParser.cleanLines(stored).none { it.startsWith("43") }) {
            setLog("Keine Antwort vom Steuergerät. Zündung an? Antwort: ${stored.trim()}"); return
        }
        val pending = elm.send("07")
        val s = ObdParser.parseDtcs(stored, "43")
        val p = ObdParser.parseDtcs(pending, "47")
        val sb = StringBuilder()
        sb.append("Gespeicherte Fehler: ").append(if (s.isEmpty()) "keine" else s.joinToString(", ")).append('\n')
        sb.append("Anstehende Fehler: ").append(if (p.isEmpty()) "keine" else p.joinToString(", ")).append('\n')
        sb.append("\nP = Antrieb, C = Fahrwerk, B = Karosserie, U = Netzwerk.\n")
        sb.append("Hinweis: Dies sind die genormten OBD-II-Fehlercodes (Emissionssteuergeräte).")
        setLog(sb.toString())
    }

    private fun confirmClear() {
        AlertDialog.Builder(this).setTitle("Fehlerspeicher löschen?")
            .setMessage("Löscht auch Freeze-Frame-Daten und setzt die Bereitschaftscodes zurück. Die Fehlerursache bleibt bestehen.")
            .setPositiveButton("Löschen") { _, _ ->
                run {
                    live = false
                    val r = elm.send("04")
                    setLog(if (r.contains("44")) "Fehlerspeicher gelöscht." else "Unerwartete Antwort: ${r.trim()}")
                }
            }.setNegativeButton("Abbrechen", null).show()
    }

    private fun readVin() {
        val r = elm.send("0902")
        val vin = ObdParser.parseVin(r)
        setLog(vin?.let { "VIN: $it" } ?: "VIN nicht lesbar (nicht jedes Fahrzeug unterstützt sie). Antwort: ${r.trim()}")
    }

    private fun toggleLive() {
        if (live) { live = false; liveBtn.text = "Live-Daten starten"; return }
        live = true; liveBtn.text = "Live-Daten stoppen"
        run {
            while (live && connected) {
                val sb = StringBuilder()
                for (pid in ObdParser.livePids) {
                    if (!live) break
                    val data = ObdParser.parseMode01(elm.send("01" + pid.code, 2000), pid.code)
                    sb.append(pid.label.padEnd(22))
                    if (data != null && data.size >= pid.minBytes)
                        sb.append("%.1f %s".format(pid.calc(data), pid.unit)) else sb.append("n/v")
                    sb.append('\n')
                }
                if (live) setLog(sb.toString())
            }
        }
    }

    override fun onDestroy() { live = false; elm.close(); io.shutdownNow(); super.onDestroy() }
}
