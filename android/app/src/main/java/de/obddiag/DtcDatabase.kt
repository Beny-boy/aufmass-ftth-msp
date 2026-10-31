package de.obddiag

import android.content.Context

/** Beschreibungen aller 9.533 genormten OBD-II-Fehlercodes (Quelle: OBDex, CC0). Herstellerspezifische Codes (z. B. P1xxx) sind nicht enthalten. */
class DtcDatabase(private val entries: Map<String, Entry>) {
    data class Entry(val code: String, val title: String, val description: String,
                     val causes: String, val symptoms: String, val difficulty: String, val costEur: String)

    fun lookup(code: String): Entry? = entries[code.uppercase()]

    fun format(code: String): String {
        val e = lookup(code) ?: return "$code\n  Herstellerspezifischer Code – keine genormte Beschreibung verfügbar.\n"
        val diff = mapOf("easy" to "einfach", "medium" to "mittel", "hard" to "schwer", "expert" to "Fachwerkstatt")
        return buildString {
            append(e.code).append(" – ").append(e.title).append('\n')
            append("  ").append(e.description).append('\n')
            if (e.causes.isNotEmpty()) append("  Mögliche Ursachen: ").append(e.causes).append('\n')
            if (e.symptoms.isNotEmpty()) append("  Symptome: ").append(e.symptoms).append('\n')
            if (e.difficulty.isNotEmpty() || e.costEur.isNotEmpty())
                append("  Reparatur: ").append(diff[e.difficulty] ?: e.difficulty)
                    .append(if (e.costEur.isNotEmpty()) ", grob ${e.costEur} €" else "").append('\n')
        }
    }

    companion object {
        fun parseLine(line: String): Entry? {
            val p = line.split('|')
            return if (p.size >= 7) Entry(p[0], p[1], p[2], p[3], p[4], p[5], p[6]) else null
        }

        fun load(ctx: Context): DtcDatabase {
            val m = HashMap<String, Entry>(12000)
            ctx.assets.open("dtc_de.txt").bufferedReader(Charsets.UTF_8).useLines { seq ->
                seq.forEach { l -> parseLine(l)?.let { m[it.code] = it } }
            }
            return DtcDatabase(m)
        }
    }
}
