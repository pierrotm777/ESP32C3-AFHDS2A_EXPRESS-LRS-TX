# Telemetry Viewer 2.6.0 – Settings Guide

This document explains the options currently available in **Settings** in Telemetry Viewer 2.6.0 with **AFHDS2A** support.

> **AFHDS2A note**
>
> AFHDS2A does not require a dedicated option on this screen. The protocol is detected automatically when a connection is established, just like CRSF, FrSky S.PORT, GHST, MAVLink, and the other supported protocols.

---

## 1. General settings

### Telemetry logging RAW

**Default: enabled**

Records the raw telemetry stream to a `.tlm` file.

Files are stored in the public folder:

```text
TelemetryLogs
```

They can later be replayed with Telemetry Viewer's **Playback** feature.

This is the most useful format when an exact replay of the received telemetry stream is required.

---

### Telemetry logging to CSV

**Default: enabled**

Records decoded telemetry data to a `.csv` file in an OpenTX/EdgeTX-like format.

The CSV is also used during playback to restore information such as:

- flight time;
- recorded operator position;
- converted telemetry values.

Files are stored in the `TelemetryLogs` folder.

---

### Location while the app is away

Shows whether Telemetry Viewer is allowed to obtain the phone's GPS location while the app is not in the foreground or while the screen is off.

This permission is useful when **Record operator position** is enabled.

Typical states are:

- **not allowed**: operator position cannot be recorded;
- **allowed only while using the app**: recording may stop when the app goes into the background;
- **allowed all the time**: operator position can continue to be recorded for the whole flight.

Tapping this row opens the appropriate Android settings.

---

### Record operator position

**Default: enabled**  
**Depends on: Telemetry logging to CSV**

Writes the operator phone's position to the CSV:

- latitude;
- longitude;
- GPS accuracy;
- phone heading when available.

During Playback, Telemetry Viewer can then display where the operator was actually standing during the recorded flight.

---

### Background compass

**Default: enabled**  
**Depends on: Record operator position**

Keeps recording the direction the phone is facing while the screen is off or the app is in the background.

This preserves operator heading information in the flight log.

---

### Lock screen orientation

**Default: No lock**

Forces the screen orientation if desired:

| Setting | Effect |
|---|---|
| No lock | Android chooses automatically |
| Portrait | Portrait orientation |
| Landscape | Landscape orientation |
| Reverse Landscape | Reversed landscape |

---

### Automatically reconnect a lost radio link

**Default: enabled**

After a **Bluetooth Classic, BLE, or USB serial** link drops, the app keeps trying to reconnect for roughly one minute.

For USB, when the cable or adapter returns, the app attempts to resume the same flight and log.

For our ESP32-C3 using **BLE**, leaving this enabled is recommended.

---

### Automatically reconnect lost network connection

**Default: enabled**

The equivalent reconnection option for **Wi-Fi TCP/UDP** telemetry.

It is independent of Bluetooth/BLE/USB reconnection.

---

### Voice messages on connection status changes

**Default: enabled**

Plays voice announcements when the connection state changes, for example when a link connects or disconnects.

---

# 2. Map markers

## Model

**Default: Quad**

Selects how the model is drawn on the map and in the 3D view:

- Quad;
- Plane or wing;
- Helicopter.

This setting does not change the telemetry protocol.

---

## Model color

Selects the color used for the model on the map and in 3D.

---

## Show clock

**Default: enabled**

Displays:

- the current time during a live flight;
- the recorded time during Playback.

---

## My position color

Selects the color of the arrow showing the phone's current position.

---

## Show home line

**Default: enabled**

Draws a line between the model and the phone's current position.

It is useful for quickly seeing the direction back to a landed or lost model.

---

## Home line color

Selects the color and optional transparency of the line between the model and the phone.

Depends on **Show home line**.

---

## Show recorded operator

**Default: enabled**

During Playback, shows the recorded operator position from the original flight.

This is separate from the phone's current position.

---

## Recorded operator color

Selects the color used for the recorded operator marker.

---

## Show operator line

**Default: enabled**

During Playback, draws a line from the model to the recorded operator position.

---

## Operator line color

Selects the color and transparency of the recorded operator line.

---

## Show heading line

**Default: enabled**

Draws a heading line in front of the model.

It uses heading information when the telemetry protocol provides it.

---

## Heading line color

Selects the heading line color.

---

## Loading tiles

**Default: enabled**

In the 3D view, shows a small grid that fills as terrain tiles are loaded.

This has no effect on telemetry reception.

---

# 3. Route line

## Route line color

Selects the color used for the route or track travelled by the model on the map.

---

# 4. Flight plans

## Show flight plans

**Default: enabled**

Displays imported flight plans on the map.

---

## Import flight plan

Imports a CSV file containing waypoints.

The expected format is based on coordinates such as:

```text
latitude,longitude
```

with one waypoint per line.

---

## Manage flight plans

Manages imported flight plans:

- show or hide a plan;
- change its color;
- delete it.

---

# 5. USB Serial

## Baudrate

**Default: 57600 baud**

Sets the serial speed when telemetry is received over USB.

Available values:

```text
9600
19200
38400
57600
115200
230400
400000
420000
460800
921600
```

This setting has no effect on BLE.

---

# 6. Playback

## Automatic playback start

**Default: enabled**

Automatically starts playback after a log file is loaded.

When disabled, the file is loaded but playback waits for user input.

---

# 7. Sensors

## Sensor display settings

Opens the telemetry widget configuration screen.

It allows widgets to be shown or hidden and arranged in the upper and lower areas of the main display.

Widgets available in version 2.6.0 include:

```text
Satellites
Battery
Voltage
Amperage
Rssi
Uplink / Downlink SNR
Uplink / Downlink LQ
CRSF Rate
Active Antenna
Uplink Power
Antenna RSSI values
Speed
Distance
Traveled Distance
Altitude
AirSpeed
Vertical Speed
Cell Voltage
Altitude above MSL
Throttle
Telemetry rate
RC Channels
Protocol
Phone Battery
```

Not every protocol provides every item. What is shown depends on the sensors and telemetry data actually received.

---

## Battery units

**Default: mAh**

Selects how battery capacity is displayed:

| Option | Meaning |
|---|---|
| Percentage | Percentage |
| mAh | Milliamp-hours |
| mWh | Milliwatt-hours |

---

## Voltage reported by telemetry

**Default: Battery**

Tells the app whether the received voltage represents:

- total **battery pack voltage** (`Battery`);
- one **cell voltage** (`Cell`).

This setting is mainly intended for CRSF and FrSky.

When `Cell` is selected, **Battery cells** is disabled because no pack-to-cell conversion is needed.

---

## Battery cells

**Default: Auto**

Used when telemetry reports total pack voltage.

The app divides pack voltage by the number of cells to calculate volts per cell.

Available choices:

```text
Auto
1S 2S 3S 4S 5S 6S 7S 8S
10S 12S 14S 16S
```

**Auto** attempts to identify the cell count. A fixed value is preferable when connecting a partially discharged pack because a low-voltage pack can resemble a smaller fully charged pack.

---

## Show artificial horizon view

**Default: enabled**

Shows the artificial horizon when the protocol supplies attitude information such as roll and pitch.

With AFHDS2A, it is useful only when attitude data is actually transmitted.

The original `frsky_pitch_roll` note mainly applies to INAV/FrSky configurations.

---

# 8. FlightRadar24 Nearby Aircraft

These options display real aircraft near the model on the map.

They require Internet access and are unrelated to the AFHDS2A radio link.

## Show nearby aircraft

**Default: disabled**

Enables nearby aircraft obtained from FlightRadar24.

This feature is live-only and is not shown during playback of an old flight.

---

## Search radius

**Default: 50 km**

Defines the radius in which aircraft are fetched.

Choices: 10, 25, 50, 100, or 200 km.

---

## Display altitude ceiling

**Default: 3000 m**

Hides aircraft above this altitude.

---

## Warning altitude ceiling

**Default: 1500 m**

Only aircraft below this altitude can generate proximity warnings.

The app prevents this value from being higher than **Display altitude ceiling**.

---

## Warning distance

**Default: 5000 m**

Sets the proximity distance used for aircraft warnings.

Distance is measured from the model when the model has a known position, or from the phone when the model cannot be placed on the map.

---

## Poll interval

**Default: 10 seconds**

Controls how often FlightRadar24 aircraft positions are refreshed.

---

# 9. Video

## Video source

**Default: Off**

Selects the video source shown beside the map:

| Option | Use |
|---|---|
| Off | No video |
| USB camera (UVC) | UVC camera, video receiver, or compatible goggles over USB/OTG |
| Network stream | Video received over the network |

When video is enabled, a button in the top bar shows or hides the video pane.

---

## Stream address

Available only when **Video source = Network stream**.

The address scheme determines the stream type.

Examples:

```text
rtsp://192.168.1.10:554/stream0
http://192.168.1.20:8080/video
udp://5600
```

- `rtsp://`: RTSP stream;
- `http://`: usually MJPEG;
- `udp://`: listens for an RTP H.264/H.265 stream pushed to the phone.

The app also remembers recently used stream addresses.

---

## RTSP over UDP

**Default: disabled**

Applies only to `rtsp://` sources.

- **OFF**: RTP over TCP. More robust on a lossy link.
- **ON**: RTP over UDP. Potentially lower latency but more sensitive to packet loss.

This is a video setting. It is not the UDP transport used for telemetry.

---

# 10. Mock location

## Drone GPS as phone location

**Default: disabled**

Republishes the model's telemetry GPS position as the Android phone's own location.

Example:

```text
Model GPS
    ↓
AFHDS2A / CRSF / other telemetry
    ↓
Telemetry Viewer
    ↓
Android mock location
    ↓
Another GPS application
```

This allows another tracking app on the same phone to broadcast the model position instead of the phone position.

When the telemetry link ends, the phone returns to its real GPS position.

To use this feature, Telemetry Viewer must be selected as the **mock location app** in Android Developer options.

---

## Mock location app

Shows whether Android currently allows Telemetry Viewer to publish mock locations.

If not, tapping the row opens **Developer options**, where Telemetry Viewer can be chosen under:

```text
Select mock location app
```

If Developer options are not enabled yet, they can normally be enabled by tapping **Build number** several times in the phone information screen.

---

# 11. Diagnostics

This section is available only in **Debug** builds.

It is normally hidden in a Release build.

## Copy debug info

Copies diagnostic information to the clipboard so it can be shared for troubleshooting.

---

## Clear debug info

Clears the stored diagnostic information.

---

# 12. TCP/UDP network telemetry settings

Network telemetry parameters are intentionally **not stored as normal rows in the Settings screen**.

They are selected from the **Network telemetry** connection dialog when making a connection.

Depending on the selected mode, the app can use network transports such as:

- TCP client;
- server/listen modes where supported;
- UDP listen;
- TBS WebSocket and specific presets.

The network transport and telemetry protocol are separate layers. For example, AFHDS2A frames received over UDP can be detected as AFHDS2A exactly as when the same frames are received over BLE.

---

# 13. Telemetry Viewer 2.6.0 AFHDS2A specifics

Version 2.6.0 adds AFHDS2A detection and decoding, including MULTI frames:

```text
0x06  AFHDS2A telemetry
0x0C  extended AFHDS2A telemetry
0x0D  RC channels
```

There is currently no **AFHDS2A ON/OFF** switch in Settings because protocol detection is automatic.

Likewise, the current 2.6.0 Settings screen does not yet directly show:

```text
Connection : Bluetooth LE
Telemetry  : AFHDS2A
```

This can be added in a later version as read-only connection status information.

---

## Recommended settings for our ESP32-C3 + AFHDS2A + BLE use

| Setting | Recommendation |
|---|---|
| Telemetry logging RAW | Enabled |
| Telemetry logging to CSV | Enabled when logs are useful |
| Automatically reconnect a lost radio link | Enabled |
| Automatically reconnect lost network connection | Not important for BLE |
| Voice messages | Personal preference |
| USB Baudrate | No effect on BLE |
| Mock location | Disabled unless specifically needed |
| FlightRadar24 | Disabled unless needed |
| Video | Off unless video is used |
| Protocol | Automatically detected as AFHDS2A |

