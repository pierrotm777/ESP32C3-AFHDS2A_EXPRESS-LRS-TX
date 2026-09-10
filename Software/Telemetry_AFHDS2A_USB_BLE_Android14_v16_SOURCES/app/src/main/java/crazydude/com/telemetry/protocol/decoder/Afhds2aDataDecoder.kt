package crazydude.com.telemetry.protocol.decoder

import crazydude.com.telemetry.protocol.Protocol

/**
 * Decoder for FlySky AFHDS2A telemetry values forwarded by the MULTI module.
 *
 * The protocol layer normalizes sensor values into the existing TelemetryData
 * types used by the application.  This decoder only performs unit conversion
 * and keeps the GPS latitude/longitude pair together.
 */
class Afhds2aDataDecoder(listener: Listener) : DataDecoder(listener) {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var gotLatitude = false
    private var gotLongitude = false

    override fun restart() {
        latitude = 0.0
        longitude = 0.0
        gotLatitude = false
        gotLongitude = false
        listener.onDecoderRestart()
    }

    override fun decodeData(data: Protocol.Companion.TelemetryData) {
        var decoded = true

        when (data.telemetryType) {
            Protocol.VBAT_OR_CELL -> listener.onVBATOrCellData(data.data / 100f)
            Protocol.CELL_VOLTAGE -> listener.onCellVoltageData(data.data / 100f)
            Protocol.CURRENT -> listener.onCurrentData(data.data / 100f)
            Protocol.FUEL -> listener.onFuelData(data.data)

            Protocol.HEADING -> {
                var heading = data.data / 100f
                while (heading < 0f) heading += 360f
                while (heading >= 360f) heading -= 360f
                listener.onHeadingData(heading)
            }

            Protocol.RSSI -> listener.onRSSIData(data.data.coerceIn(0, 100))
            Protocol.CRSF_UP_LQ -> listener.onUpLqData(data.data.coerceIn(0, 100))
            Protocol.UP_SNR -> listener.onUPSNRData(data.data)
            Protocol.RSSI_DBM_1 -> listener.onRssiDbm1Data(data.data)

            Protocol.GPS_STATE -> {
                val satellites = data.data and 0xff
                val fix = (data.data and 0x100) != 0
                listener.onGPSState(satellites, fix)
            }

            Protocol.VSPEED -> listener.onVSpeedData(data.data / 100f)
            Protocol.ALTITUDE -> listener.onAltitudeData(data.data / 100f)
            Protocol.GPS_ALTITUDE -> listener.onGPSAltitudeData(data.data / 100f)
            Protocol.GSPEED -> listener.onGSpeedData(data.data / 100f * 3.6f)
            Protocol.DISTANCE -> listener.onDistanceData(data.data)
            Protocol.ROLL -> listener.onRollData(data.data / 100f)
            Protocol.PITCH -> listener.onPitchData(data.data / 100f)

            Protocol.GPS_LATITUDE -> {
                latitude = data.data / 10000000.0
                gotLatitude = true
                publishGpsIfReady()
            }

            Protocol.GPS_LONGITUDE -> {
                longitude = data.data / 10000000.0
                gotLongitude = true
                publishGpsIfReady()
            }

            else -> decoded = false
        }

        if (decoded) listener.onSuccessDecode()
    }

    private fun publishGpsIfReady() {
        if (gotLatitude && gotLongitude) {
            listener.onGPSData(latitude, longitude)
            gotLatitude = false
            gotLongitude = false
        }
    }
}
