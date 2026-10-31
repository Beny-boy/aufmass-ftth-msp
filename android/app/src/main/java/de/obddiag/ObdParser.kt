package de.obddiag

/** Reine Parser-/Rechenlogik für OBD-II (SAE J1979). Keine Android-Abhängigkeit, daher unit-testbar. */
object ObdParser {

    /** Entfernt Leerzeichen, Prompt, Statuszeilen; liefert die Antwortzeilen in Großbuchstaben. */
    fun cleanLines(raw: String): List<String> =
        raw.replace(">", "").split('\r', '\n')
            .map { it.trim().uppercase().replace(" ", "") }
            .filter { it.isNotEmpty() && !it.startsWith("SEARCHING") && it != "OK" }

    fun isNoData(raw: String): Boolean {
        val s = raw.uppercase()
        return s.contains("NO DATA") || s.contains("UNABLE TO CONNECT") || s.contains("CAN ERROR") ||
            s.contains("BUS INIT") || s.contains("STOPPED") || s.contains("?")
    }

    /** Fasst Mehrzeilen-Antworten zusammen. ISO-TP-Zeilen ("0:", "1:") werden verkettet, Längenzeilen entfernt. */
    private fun payloads(raw: String): List<String> {
        val lines = cleanLines(raw)
        val framed = lines.any { Regex("^[0-9A-F]:").containsMatchIn(it) }
        return if (framed) {
            listOf(lines.filter { !Regex("^[0-9A-F]{3}$").matches(it) }
                .joinToString("") { it.replace(Regex("^[0-9A-F]:"), "") })
        } else lines
    }

    /** Dekodiert einen 2-Byte-Fehlercode (z. B. 0133 -> P0133). */
    fun decodeDtc(hex4: String): String {
        val d = hex4[0].digitToInt(16)
        val letter = "PCBU"[d shr 2]
        return "$letter${d and 3}${hex4.substring(1)}"
    }

    /** Antwort auf Modus 03 (gespeicherte) bzw. 07 (anstehende) Fehler. */
    fun parseDtcs(raw: String, responseByte: String = "43"): List<String> {
        val result = linkedSetOf<String>()
        for (p in payloads(raw)) {
            if (!p.startsWith(responseByte)) continue
            var rest = p.substring(responseByte.length)
            if (rest.length % 4 == 2) rest = rest.substring(2) // CAN: führendes Anzahl-Byte
            rest.chunked(4).filter { it.length == 4 && it != "0000" }
                .forEach { result.add(decodeDtc(it)) }
        }
        return result.toList()
    }

    /** Liefert die Datenbytes einer Modus-01-Antwort, z. B. "410C1AF8" -> [0x1A, 0xF8]. */
    fun parseMode01(raw: String, pid: String): IntArray? {
        val marker = "41" + pid.uppercase()
        val joined = payloads(raw).joinToString("")
        val i = joined.indexOf(marker)
        if (i < 0) return null
        val data = joined.substring(i + marker.length)
        if (data.length < 2) return null
        return data.chunked(2).filter { it.length == 2 }.map { it.toInt(16) }.toIntArray()
    }

    data class Pid(val code: String, val label: String, val unit: String, val minBytes: Int,
                   val calc: (IntArray) -> Double)

    val livePids = listOf(
        Pid("0C", "Motordrehzahl", "U/min", 2) { (it[0] * 256 + it[1]) / 4.0 },
        Pid("0D", "Geschwindigkeit", "km/h", 1) { it[0].toDouble() },
        Pid("05", "Kühlmitteltemperatur", "°C", 1) { it[0] - 40.0 },
        Pid("0F", "Ansauglufttemperatur", "°C", 1) { it[0] - 40.0 },
        Pid("04", "Motorlast", "%", 1) { it[0] * 100.0 / 255.0 },
        Pid("11", "Drosselklappe", "%", 1) { it[0] * 100.0 / 255.0 },
        Pid("0B", "Ansaugdruck", "kPa", 1) { it[0].toDouble() },
        Pid("2F", "Kraftstofftank", "%", 1) { it[0] * 100.0 / 255.0 },
    )

    fun parseVin(raw: String): String? {
        val ps = payloads(raw)
        val framedJoined = cleanLines(raw).any { Regex("^[0-9A-F]:").containsMatchIn(it) }
        val hex = StringBuilder()
        for (p in ps) {
            if (framedJoined) {
                val i = p.indexOf("4902")
                if (i >= 0) hex.append(p.substring(i + 6)) // 4902 + Frame-Nr.
            } else if (p.startsWith("4902") && p.length >= 6) {
                hex.append(p.substring(6))
            }
        }
        val vin = hex.toString().chunked(2).filter { it.length == 2 }
            .map { it.toInt(16).toChar() }
            .filter { it in 'A'..'Z' || it in '0'..'9' }
            .joinToString("")
        return if (vin.length == 17) vin else null
    }

    /** Spannung aus "ATRV"-Antwort, z. B. "12.6V". */
    fun parseVoltage(raw: String): String? =
        Regex("([0-9]+\\.?[0-9]*)V", RegexOption.IGNORE_CASE).find(raw)?.value
}
