package de.obddiag

import org.junit.Assert.*
import org.junit.Test

class ObdParserTest {
    @Test fun decodeDtc() {
        assertEquals("P0133", ObdParser.decodeDtc("0133"))
        assertEquals("C0123", ObdParser.decodeDtc("4123"))
        assertEquals("B0123", ObdParser.decodeDtc("8123"))
        assertEquals("U0123", ObdParser.decodeDtc("C123"))
    }
    @Test fun dtcsCan() {
        assertEquals(listOf("P0133", "P0171"), ObdParser.parseDtcs("43 02 01 33 01 71\r\r"))
    }
    @Test fun dtcsNonCan() {
        assertEquals(listOf("P0133"), ObdParser.parseDtcs("43 01 33 00 00 00 00\r\r"))
    }
    @Test fun dtcsNone() {
        assertTrue(ObdParser.parseDtcs("43 00\r\r").isEmpty())
    }
    @Test fun rpm() {
        val d = ObdParser.parseMode01("41 0C 1A F8\r\r", "0C")!!
        assertEquals(1726.0, ObdParser.livePids.first { it.code == "0C" }.calc(d), 0.01)
    }
    @Test fun coolant() {
        val d = ObdParser.parseMode01("41 05 7B\r", "05")!!
        assertEquals(83.0, ObdParser.livePids.first { it.code == "05" }.calc(d), 0.01)
    }
    @Test fun noData() { assertTrue(ObdParser.isNoData("NO DATA\r\r")) }
    @Test fun vinCan() {
        val raw = "014\r0: 49 02 01 57 50 30\r1: 5A 5A 5A 39 39 5A\r2: 54 53 33 39 32 31 32 34\r\r"
        assertEquals("WP0ZZZ99ZTS392124", ObdParser.parseVin(raw))
    }
    @Test fun voltage() { assertEquals("12.6V", ObdParser.parseVoltage("12.6V\r")) }
    @Test fun readiness() {
        // A=0x82 (MIL an, 2 Fehler), B=0x07 (3 Tests unterstützt, fertig), C=0x65, D=0x04
        val r = ObdParser.parseReadiness("41 01 82 07 65 04\r\r")!!
        assertTrue(r.milOn); assertEquals(2, r.dtcCount)
        assertEquals("bereit", r.monitors.first { it.first == "Komponenten" }.second)
        assertEquals("bereit", r.monitors.first { it.first == "Katalysator" }.second)
        assertEquals("nicht bereit", r.monitors.first { it.first == "Verdunstungssystem (EVAP)" }.second)
        assertEquals("nicht unterstützt", r.monitors.first { it.first == "Sekundärluft" }.second)
    }
    @Test fun freezeFrame() {
        val d = ObdParser.parseFreezeFrame("42 0C 00 0F A0\r", "0C")!!
        assertEquals(1000.0, ObdParser.livePids.first { it.code == "0C" }.calc(d), 0.01)
    }
    @Test fun battery() { assertEquals("voll geladen", ObdParser.batteryRating(12.7)) }
}
