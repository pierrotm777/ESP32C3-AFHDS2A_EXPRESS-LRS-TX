package crazydude.com.telemetry.protocol

import crazydude.com.telemetry.protocol.decoder.DataDecoder
import crazydude.com.telemetry.protocol.decoder.LTMDataDecoder
import crazydude.com.telemetry.protocol.decoder.LinkTestDataDecoder
import kotlin.experimental.xor

/*
 This is fake protocol for testing link quality.
 'Protocol' expects stream of increasing bytes.
 Number of errors is output as Distance.
 */

class LinkTestProtocol : Protocol {

    constructor(dataListener: DataDecoder.Listener) : super(LinkTestDataDecoder(dataListener))
    constructor(dataDecoder: DataDecoder) : super(dataDecoder)

    private var d : UByte= 0u;
    private var errorCount = 0;
    private var hitCount = 0;
    private var lastEvent = 0L;

    override fun process(data: Int) {
        val d1 = data.toUByte()
        if ( d1 != this.d ) {
            this.errorCount++
            this.hitCount = 0
            this.d = d1;
        }
        else {
            this.hitCount++;
        }
        this.d++;

        if ((System.currentTimeMillis() - lastEvent) > 100 ) {
            lastEvent = System.currentTimeMillis();

            dataDecoder.decodeData( Protocol.Companion.TelemetryData( DISTANCE, this.errorCount, null ))
            if ( hitCount > 1000 ) {
                dataDecoder.decodeData(Protocol.Companion.TelemetryData(RSSI, 100, null))
            }
        }
    }
}