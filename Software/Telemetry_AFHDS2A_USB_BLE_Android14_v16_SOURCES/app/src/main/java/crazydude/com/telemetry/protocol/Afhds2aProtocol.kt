package crazydude.com.telemetry.protocol

import crazydude.com.telemetry.protocol.decoder.Afhds2aDataDecoder
import crazydude.com.telemetry.protocol.decoder.DataDecoder

/**
 * FlySky AFHDS2A telemetry received through a MULTI-module serial envelope.
 *
 * Wire format:
 *   'M' 'P' TYPE LENGTH PAYLOAD...
 *
 * TYPE 0x06 = AFHDS2A normal telemetry (AA)
 * TYPE 0x0C = AFHDS2A extended telemetry (AC)
 *
 * For both types the first payload byte is MULTI TX_RSSI.  The remaining
 * payload contains either seven 4-byte AA sensor records or variable-length
 * AC sensor records.  TXID and RXID are deliberately omitted by MULTI.
 */
class Afhds2aProtocol : Protocol {

    constructor(dataListener: DataDecoder.Listener) : super(Afhds2aDataDecoder(dataListener))
    constructor(dataDecoder: DataDecoder) : super(dataDecoder)

    private enum class State {
        IDLE, MP_HEADER2, MP_TYPE, MP_LENGTH, MP_DATA
    }

    companion object {
        private const val MP_HEADER1 = 0x4d // 'M'
        private const val MP_HEADER2 = 0x50 // 'P'
        private const val MP_TYPE_AFHDS2A = 0x06
        private const val MP_TYPE_AFHDS2A_AC = 0x0c
        private const val MAX_MP_PAYLOAD = 64

        private const val ID_RX_VOLTAGE = 0x00
        private const val ID_EXT_VOLTAGE = 0x03
        private const val ID_CELL_VOLTAGE = 0x04
        private const val ID_BAT_CURRENT = 0x05
        private const val ID_FUEL = 0x06
        private const val ID_HEADING = 0x08
        private const val ID_CLIMB_RATE = 0x09
        private const val ID_COG = 0x0a
        private const val ID_GPS_STATUS = 0x0b
        private const val ID_ROLL = 0x0f
        private const val ID_PITCH = 0x10
        private const val ID_YAW = 0x11
        private const val ID_VERTICAL_SPEED = 0x12
        private const val ID_GROUND_SPEED = 0x13
        private const val ID_GPS_DISTANCE = 0x14
        private const val ID_GPS_LAT = 0x80
        private const val ID_GPS_LON = 0x81
        private const val ID_GPS_ALT = 0x82
        private const val ID_ALT = 0x83
        private const val ID_RX_SNR = 0xfa
        private const val ID_RX_RSSI = 0xfc
        private const val ID_RX_ERROR_RATE = 0xfe
        private const val ID_END = 0xff

        // AC aggregate sensors
        private const val ID_ACC_FULL = 0xef
        private const val ID_VOLT_FULL = 0xf0
        private const val ID_GPS_FULL = 0xfd
    }

    private var state = State.IDLE
    private var packetType = 0
    private var payloadLength = 0
    private var payloadIndex = 0
    private val payload = IntArray(MAX_MP_PAYLOAD)

    override fun process(data: Int) {
        val value = data and 0xff

        when (state) {
            State.IDLE -> {
                if (value == MP_HEADER1) state = State.MP_HEADER2
            }

            State.MP_HEADER2 -> {
                state = if (value == MP_HEADER2) State.MP_TYPE else State.IDLE
            }

            State.MP_TYPE -> {
                if (value == MP_TYPE_AFHDS2A || value == MP_TYPE_AFHDS2A_AC) {
                    packetType = value
                    state = State.MP_LENGTH
                } else {
                    state = State.IDLE
                }
            }

            State.MP_LENGTH -> {
                if (value in 1..MAX_MP_PAYLOAD) {
                    payloadLength = value
                    payloadIndex = 0
                    state = State.MP_DATA
                } else {
                    state = State.IDLE
                }
            }

            State.MP_DATA -> {
                payload[payloadIndex++] = value
                if (payloadIndex >= payloadLength) {
                    processPacket()
                    state = State.IDLE
                    payloadIndex = 0
                }
            }
        }
    }

    private fun processPacket() {
        if (payloadLength < 1) return

        // MULTI's own A7105 receive RSSI is 0..255.  Keep the app's legacy
        // RSSI display in its expected 0..100 range.
        val txRssiPercent = (payload[0] * 100 / 255).coerceIn(0, 100)
        emit(RSSI, txRssiPercent)

        if (packetType == MP_TYPE_AFHDS2A) {
            processAaPayload()
        } else {
            processAcPayload()
        }
    }

    private fun processAaPayload() {
        var index = 1
        var sensorCount = 0

        while (index + 3 < payloadLength && sensorCount < 7) {
            val id = payload[index]
            if (id == ID_END) break

            val raw16 = payload[index + 2] or (payload[index + 3] shl 8)
            processSensor(id, raw16, 2, null)
            index += 4
            sensorCount++
        }
    }

    private fun processAcPayload() {
        var index = 1

        while (index + 2 < payloadLength) {
            val id = payload[index]
            if (id == ID_END) break

            val size = payload[index + 2]
            if (size < 0 || index + 3 + size > payloadLength) break

            val sensorData = IntArray(size)
            var i = 0
            while (i < size) {
                sensorData[i] = payload[index + 3 + i]
                i++
            }

            val raw = when {
                size >= 4 -> littleEndian32(sensorData, 0)
                size >= 2 -> sensorData[0] or (sensorData[1] shl 8)
                size == 1 -> sensorData[0]
                else -> 0
            }
            processSensor(id, raw, size, sensorData)
            index += size + 3
        }
    }

    private fun processSensor(id: Int, raw: Int, size: Int, sensorData: IntArray?) {
        when (id) {
            ID_RX_VOLTAGE, ID_EXT_VOLTAGE -> emit(VBAT_OR_CELL, raw and 0xffff)
            ID_CELL_VOLTAGE -> emit(CELL_VOLTAGE, raw and 0xffff)
            ID_BAT_CURRENT -> emit(CURRENT, raw and 0xffff)
            ID_FUEL -> emit(FUEL, raw and 0xffff)

            // The built-in compass is integer degrees while COG/YAW are x100.
            ID_HEADING -> emit(HEADING, (raw and 0xffff) * 100)
            ID_COG -> emit(HEADING, raw and 0xffff)
            ID_YAW -> emit(HEADING, signed16(raw))

            ID_CLIMB_RATE, ID_VERTICAL_SPEED -> emit(VSPEED, signed16(raw))
            ID_GROUND_SPEED -> emit(GSPEED, raw and 0xffff)
            ID_GPS_DISTANCE -> emit(DISTANCE, raw and 0xffff)
            ID_ROLL -> emit(ROLL, signed16(raw))
            ID_PITCH -> emit(PITCH, signed16(raw))

            ID_GPS_STATUS -> {
                val satellites = (raw ushr 8) and 0xff
                emit(GPS_STATE, satellites or if (satellites > 0) 0x100 else 0)
            }

            ID_GPS_LAT -> if (size >= 4) emit(GPS_LATITUDE, raw)
            ID_GPS_LON -> if (size >= 4) emit(GPS_LONGITUDE, raw)
            ID_GPS_ALT -> if (size >= 4) emit(GPS_ALTITUDE, raw)
            ID_ALT -> if (size >= 4) emit(ALTITUDE, raw)

            // Raw RSSI byte is a positive dBm magnitude in the MULTI AFHDS2A
            // implementation, so publish it on the app's dBm sensor as negative.
            ID_RX_RSSI -> emit(RSSI_DBM_1, -(raw and 0xff))
            ID_RX_SNR -> emit(UP_SNR, raw and 0xff)

            // FlySky sensor 0xFE is packet error rate.  Convert it to link
            // quality exactly as OpenTX/EdgeTX do: LQ = 100 - error rate.
            ID_RX_ERROR_RATE -> {
                val lq = (100 - (raw and 0xff)).coerceIn(0, 100)
                emit(CRSF_UP_LQ, lq)
                emit(RSSI, lq)
            }

            ID_GPS_FULL -> processGpsFull(sensorData)
            ID_VOLT_FULL -> processVoltFull(sensorData)
            ID_ACC_FULL -> processAccFull(sensorData)
        }
    }

    private fun processGpsFull(data: IntArray?) {
        if (data == null || data.size < 14) return

        val fix = data[0] != 0
        val satellites = data[1] and 0xff
        emit(GPS_STATE, satellites or if (fix) 0x100 else 0)
        emit(GPS_LATITUDE, littleEndian32(data, 2))
        emit(GPS_LONGITUDE, littleEndian32(data, 6))
        emit(GPS_ALTITUDE, littleEndian32(data, 10))
    }

    private fun processVoltFull(data: IntArray?) {
        if (data == null || data.size < 10) return

        // AC F0 packs the five normal 16-bit sensors 0x03..0x07.
        var id = ID_EXT_VOLTAGE
        var index = 0
        while (id <= 0x07 && index + 1 < data.size) {
            val value = data[index] or (data[index + 1] shl 8)
            processSensor(id, value, 2, null)
            id++
            index += 2
        }
    }

    private fun processAccFull(data: IntArray?) {
        if (data == null || data.size < 12) return

        // AC EF packs ACC_X/Y/Z + ROLL/PITCH/YAW (0x0C..0x11).
        var id = 0x0c
        var index = 0
        while (id <= ID_YAW && index + 1 < data.size) {
            val value = data[index] or (data[index + 1] shl 8)
            processSensor(id, value, 2, null)
            id++
            index += 2
        }
    }

    private fun littleEndian32(data: IntArray, index: Int): Int {
        return (data[index] and 0xff) or
            ((data[index + 1] and 0xff) shl 8) or
            ((data[index + 2] and 0xff) shl 16) or
            ((data[index + 3] and 0xff) shl 24)
    }

    private fun signed16(value: Int): Int {
        val v = value and 0xffff
        return if ((v and 0x8000) != 0) v - 0x10000 else v
    }

    private fun emit(type: Int, value: Int) {
        dataDecoder.decodeData(Protocol.Companion.TelemetryData(type, value))
    }
}
