package crazydude.com.telemetry.protocol

import crazydude.com.telemetry.protocol.decoder.DataDecoder

class ProtocolDetector(private val callback: Callback) {

    private val hits = arrayOf(0, 0, 0, 0, 0, 0, 0)
    private val sportProtocol =
        FrSkySportProtocol(object : DataDecoder.Companion.DefaultDecodeListener() {
            override fun onSuccessDecode() {
                hits[0]++
            }
        })
    private val crsfProtocol =
        CrsfProtocol(object : DataDecoder.Companion.DefaultDecodeListener() {
            override fun onSuccessDecode() {
                hits[1]++
            }
        })
    private val ltmProtocol =
        LTMProtocol(object : DataDecoder.Companion.DefaultDecodeListener() {
            override fun onSuccessDecode() {
                hits[2]++
            }
        })
    private val mavLinkProtocol =
        MAVLinkProtocol(object : DataDecoder.Companion.DefaultDecodeListener() {
            override fun onSuccessDecode() {
                hits[3]++
            }
        })
    private val mavLink2Protocol =
        MAVLink2Protocol(object : DataDecoder.Companion.DefaultDecodeListener() {
            override fun onSuccessDecode() {
                hits[4]++
            }
        })

    private val linkTestProtocol =
        LinkTestProtocol(object : DataDecoder.Companion.DefaultDecodeListener() {
            override fun onSuccessDecode() {
                hits[5]++
            }
        })

    private val afhds2aProtocol =
        Afhds2aProtocol(object : DataDecoder.Companion.DefaultDecodeListener() {
            override fun onSuccessDecode() {
                hits[6]++
            }
        })

    fun feedData(data: Int) {
        sportProtocol.process(data)
        crsfProtocol.process(data)
        ltmProtocol.process(data)
        mavLinkProtocol.process(data)
        mavLink2Protocol.process(data)
        linkTestProtocol.process(data)
        afhds2aProtocol.process(data)


        hits.forEachIndexed { index, i ->
            if (i >= 2) {
                when (index) {
                    0 -> callback.onProtocolDetected(sportProtocol)
                    1 -> callback.onProtocolDetected(crsfProtocol)
                    2 -> callback.onProtocolDetected(ltmProtocol)
                    3 -> callback.onProtocolDetected(mavLinkProtocol)
                    4 -> callback.onProtocolDetected(mavLink2Protocol)
                    5 -> callback.onProtocolDetected(linkTestProtocol)
                    6 -> callback.onProtocolDetected(afhds2aProtocol)
                }
            }
        }
    }

    interface Callback {
        fun onProtocolDetected(protocol: Protocol?)
    }
}