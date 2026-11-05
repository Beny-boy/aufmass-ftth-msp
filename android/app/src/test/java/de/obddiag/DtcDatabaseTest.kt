package de.obddiag

import org.junit.Assert.*
import org.junit.Test
import java.io.File

class DtcDatabaseTest {
    @Test fun assetIsComplete() {
        val f = File("src/main/assets/dtc_de.txt")
        val entries = f.readLines(Charsets.UTF_8).mapNotNull { DtcDatabase.parseLine(it) }.associateBy { it.code }
        assertEquals(9533, entries.size)
        val db = DtcDatabase(entries)
        assertTrue(db.lookup("P0420")!!.title.isNotEmpty())
        assertTrue(db.format("P0133").startsWith("P0133 – "))
        assertTrue(db.format("P1234").contains("Herstellerspezifisch"))
    }
}
