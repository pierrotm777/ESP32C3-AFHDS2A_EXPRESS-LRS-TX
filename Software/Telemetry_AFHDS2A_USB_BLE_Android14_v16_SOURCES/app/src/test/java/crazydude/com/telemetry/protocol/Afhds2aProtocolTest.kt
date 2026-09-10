package crazydude.com.telemetry.protocol

import crazydude.com.telemetry.protocol.decoder.DataDecoder
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class Afhds2aProtocolTest {

    @Test
    fun normalAaPacketOverMultiEnvelope() {
        val listener = mock(DataDecoder.Listener::class.java)
        val protocol = Afhds2aProtocol(listener)

        val payload = arrayListOf(
            200,                    // MULTI TX RSSI
            0x00, 0x00, 0xF4, 0x01, // RX voltage = 500 -> 5.00 V
            0xFE, 0x00, 10, 0x00,   // error rate 10% -> LQ 90%
            0xFC, 0x00, 55, 0x00,   // RX RSSI -> -55 dBm
            0xFF, 0x00, 0x00, 0x00  // end
        )
        while (payload.size < 29) payload.add(0xFF)

        feed(protocol, listOf(0x4D, 0x50, 0x06, 29) + payload)

        verify(listener, times(1)).onVBATOrCellData(5.0f)
        verify(listener, times(1)).onUpLqData(90)
        verify(listener, times(1)).onRssiDbm1Data(-55)
    }

    @Test
    fun extendedAcGpsPacketOverMultiEnvelope() {
        val listener = mock(DataDecoder.Listener::class.java)
        val protocol = Afhds2aProtocol(listener)

        val payload = arrayListOf(180, 0xFD, 0x00, 14, 1, 12)
        payload.addAll(le32(481234567))   // 48.1234567
        payload.addAll(le32(23456789))    // 2.3456789
        payload.addAll(le32(12345))       // 123.45 m
        while (payload.size < 29) payload.add(0xFF)

        feed(protocol, listOf(0x4D, 0x50, 0x0C, 29) + payload)

        verify(listener, times(1)).onGPSState(12, true)
        verify(listener, times(1)).onGPSData(48.1234567, 2.3456789)
        verify(listener, times(1)).onGPSAltitudeData(123.45f)
    }

    private fun feed(protocol: Afhds2aProtocol, frame: List<Int>) {
        frame.forEach { protocol.process(it) }
    }

    private fun le32(value: Int): List<Int> {
        return listOf(
            value and 0xff,
            (value ushr 8) and 0xff,
            (value ushr 16) and 0xff,
            (value ushr 24) and 0xff
        )
    }
}
