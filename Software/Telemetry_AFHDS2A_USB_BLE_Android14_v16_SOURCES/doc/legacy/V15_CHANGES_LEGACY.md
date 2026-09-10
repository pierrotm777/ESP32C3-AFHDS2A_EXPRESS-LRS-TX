# BLE-v15 source changes

This tree is the source-level equivalent of the validated `Telemetry_AFHDS2A_USB_BLE_test15_SettingsBLE.apk` branch.

The APK test15 itself was produced during debugging by patching/repacking the legacy hx1.6.3 APK. This source tree folds the validated behaviour back into readable source code so future versions can be edited normally.

## Validated behaviour carried into this tree

- FlySky AFHDS2A telemetry protocol parser and decoder.
- AFHDS2A over USB serial and Bluetooth LE.
- Android 12+ runtime permissions for `BLUETOOTH_SCAN` and `BLUETOOTH_CONNECT`.
- Guard around `BluetoothAdapter.getBondedDevices()` (the Android 14 crash observed on the Samsung A52 5G).
- Native Android `connectGatt()` / GATT operations retained in `BluetoothLeDataPoller`.
- `Sensor display settings` launches correctly with application id `crazydude.com.telemetr2`.
- Settings toolbar text is exactly `Settings v BLE`.
- Legacy RAW and CSV logging are disabled in v15 because the old public `TelemetryLogs` path prevents BLE connection on Android 14. Scoped-storage logging can be reintroduced later.
- `PendingIntent.FLAG_IMMUTABLE` compatibility fix retained.
- `DataService` is started with `startService()` rather than `startForegroundService()` before a telemetry connection exists.
- UVC camera modules are retained.
- Fork credits and disclaimer are retained.

## Package layout

The Android application id is:

`crazydude.com.telemetr2`

The Kotlin/Java package remains:

`crazydude.com.telemetry.*`

This is intentional.

## Version metadata

The validated patched APK inherited old hx1.6.3 version metadata. This source tree uses:

- `versionCode 32`
- `versionName "BLE-v15"`

This makes source-built packages easier to distinguish. The Settings screen is hardcoded to show `Settings v BLE`, as requested.
