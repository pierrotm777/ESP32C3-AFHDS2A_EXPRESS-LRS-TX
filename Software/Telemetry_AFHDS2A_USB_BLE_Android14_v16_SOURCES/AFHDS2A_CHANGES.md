# FlySky AFHDS2A telemetry - test integration

This tree is based on `android-taranis-smartport-telemetry-hx1.6.3`.

## Added protocol

`Afhds2aProtocol.kt` accepts the standard MULTI-module serial envelope:

```
4D 50 06 LEN DATA...    # MP, AFHDS2A normal / AA
4D 50 0C LEN DATA...    # MP, AFHDS2A extended / AC
```

The MULTI payload starts with TX_RSSI followed by the FlySky sensor area. TXID/RXID are not transmitted in this format.

Supported display mappings in this first test version:

- receiver/external/cell voltage
- current
- fuel/capacity
- heading / COG / yaw
- vertical speed
- GPS state, latitude, longitude and GPS altitude
- barometric altitude
- ground speed
- distance
- roll / pitch
- RX error-rate converted to link quality
- RX RSSI in dBm
- SNR
- AC aggregate GPS (`0xFD`), voltage (`0xF0`) and attitude (`0xEF`) records

Temperature, RPM, pressure-derived altitude and FlySky flight-mode-specific display fields are intentionally left for a later UI extension because the original application has no direct widgets/callbacks for all of them.

## Transport integration

AFHDS2A is added to automatic protocol detection and can use the application's existing transports:

- USB serial (`UsbDataPoller`)
- Bluetooth Classic SPP (`BluetoothDataPoller`)
- BLE (`BluetoothLeDataPoller`)
- binary log playback (`LogPlayer`)

For ESP32-C3 and ESP32-S3, use USB serial or BLE: these chips do not support Bluetooth Classic SPP.

## Recommended ESP32 output

Do not invent a second framing protocol. Forward AFHDS2A telemetry as the same MULTI envelope above on USB or BLE. This keeps the Android parser transport-independent and allows recorded byte streams to be replayed unchanged.
