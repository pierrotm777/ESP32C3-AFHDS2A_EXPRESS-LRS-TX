# AF2A_C3 — DUAL RF AFHDS2A + CRSF / ExpressLRS

> **Current project scope:** one ESP32-C3, two selectable RF backends: **AFHDS2A via A7105 / XL7105-D03B** or **CRSF via an external ExpressLRS TX module**. Only one RF backend is active at a time.

# AF2A_C3 — FlySky AFHDS2A Transmitter for ESP32-C3 + A7105
Standalone **FlySky AFHDS2A** / **Express LRS CRSF** transmitter module for ESP32-C3, based on the DIY-Multiprotocol project, with PPM input, telemetry, XANY/RCUL support, OLED display and configurable failsafe.

> [!IMPORTANT]  
> **Dual-RF extension:** the current development branch can now use either the original **AFHDS2A / A7105** RF backend or an external **ExpressLRS transmitter module driven by CRSF**.  
> The ESP32-C3 does **not** implement the ExpressLRS RF protocol itself. In ELRS mode it sends and receives **CRSF serial frames** to/from a separate ELRS-capable RF module.  
> Only one RF backend is active at a time.

Current dual-RF architecture:

```text
PPM / SWEEP / XANY
        |
        v
     ESP32-C3
       |   \
       |    \ UART CRSF
       |     \
       |      +--> external ELRS TX module --> ExpressLRS receiver
       |
       +--> SPI --> A7105 / XL7105-D03B --> AFHDS2A receiver
```

## Overview

**AF2A_C3** is an experimental FlySky **AFHDS2A / EspressLRS transmitter** built around an **ESP32-C3 Super Mini with OLED** and an **A7105 or ELRS 2.4 GHz RF transceiver**.  
The project reuses the proven AFHDS2A/A7105 protocol logic from the [Multiprotocol (MPM)](https://github.com/pascallanger/diy-multiprotocol-tx-module) project and adapts it to run directly on an ESP32-C3.  
Its main purpose is to take a standard **PPM signal from an RC transmitter**, convert the channels to the MPM internal format, and transmit them over the **FlySky AFHDS2A** protocol to a compatible receiver.  
The current development and test receiver is a **FlySky FS-iA6B** or a **FlySky FS-iA10B** or a **Rx ELRS PWM** receiver.  
The project also receives and decodes AFHDS2A telemetry from the receiver.  
Telemetry can be inspected from the serial console with the `tlog` command and selected information can be displayed on the integrated OLED or an [Telemetry Viewer]() Android application .  

> [!NOTE]  
I took the opportunity to update adapt Romanlut's version for AFHDS2A telemetry.  
Telemetry Viewer – key changes
Fork based on Android Taranis SmartPort Telemetry.
• Updated for Android 14.
• Modernized build setup: Gradle 8.7, AGP 8.5.2, Kotlin 1.9.24, Android SDK 34.
• Retained Google Maps / GPS support.
• Retained UVC camera support using UVCAndroid backend 1.0.13.
• Added FlySky AFHDS2A support.
• AFHDS2A telemetry reception via USB and Bluetooth LE.
• Support for MULTI frames 0x06 and 0x0C.
• Added RC channel forwarding via MULTI frame 0x0D.
• Display and update of channels CH1 through CH8.
• Decoding of key AFHDS2A data: voltages, current, capacity, RSSI, LQ, SNR, GPS, altitude, speed, vario, and attitude (when provided).
• Google Maps API key is now loaded from `local.properties` or an environment variable, rather than being hardcoded in the source.  
Todo:  
AFHDS2A temperature, RPM, and pressure data still need to be added to the Android interface.

Base version validated: v16.
---

## Main Features

### AFHDS2A transmitter

- FlySky **AFHDS2A** protocol
- A7105 2.4 GHz transceiver
- Original MPM AFHDS2A RF state machine retained as much as possible
- 16-channel MPM internal channel array
- AFHDS2A transmission of channels 1 to 14
- Receiver ID storage
- Persistent transmitter ID
- Manual or automatic transmitter ID generation
- Standard AFHDS2A binding
- 16-channel frequency hopping table
- High-resolution ESP32 RF scheduler using `esp_timer`
- RF timing diagnostics

### ExpressLRS / CRSF transmitter backend

The firmware also contains a second RF backend for **ExpressLRS** operation.

The ESP32-C3 side uses **CRSF** only. The actual ExpressLRS over-the-air modulation and RF transmission are handled by a separate ELRS-capable transmitter module.

Current implementation:

- selectable **AFHDS2A** or **ELRS/CRSF** RF backend;
- RF backend selection stored in ESP32 Preferences;
- change of RF backend followed by a clean ESP32 restart;
- same internal `Channel_data[16]` source used by both RF backends;
- PPM, SWEEP and XANY therefore feed AFHDS2A and CRSF from the same channel data;
- CRSF `RC_CHANNELS_PACKED` frame type `0x16`;
- 16 CRSF channels packed as 11-bit values;
- current RC frame rate: approximately **250 Hz**;
- current UART speed: **400000 baud**;
- separate UART TX and RX wires;
- CRSF TX on **GPIO9**;
- CRSF RX on **GPIO2**;
- AFHDS2A remains the default RF backend on a fresh configuration.

The target architecture is:

```text
ESP32-C3 GPIO9 (CRSF TX) ---> ELRS module RX
ESP32-C3 GPIO2 (CRSF RX) <--- ELRS module TX
ESP32-C3 GND              ---- ELRS module GND
```

- ExpressLRS **CRSF** protocol
- Express Rx as Transmitter (Need a **Tx ELRS** firmware)  
- Based on the CapnBry library
- [RculCrsfSerial](https://github.com/pierrotm777/MyArduinoLibraries/tree/main/Rcul_Modded_Libs/RculCrsfSerial), 16-channel library channel array (Fork from [CapnBry library](https://github.com/CapnBry/CRServoF))  

The power input of the external ELRS module must follow the requirements of the exact module used. Do not assume that every ELRS receiver/module can be powered directly from 3.3 V.

RF backend commands:

```text
rf
rf ds2a
rf elrs
```

Accepted compatibility aliases may also include:

```text
rf afhds2a
rf af2a
rf crsf
```

`rf` without an argument displays the currently selected backend.

> [!NOTE]  
> The external ELRS hardware must support being used/programmed as a transmitter and must expose a suitable CRSF UART interface. The first hardware target is a small ELRS/Nano-class module reprogrammed for TX operation.

---
### PPM input

The normal control source is a PPM stream connected to the ESP32-C3.

Current implementation:

- PPM input on GPIO2
- Positive or negative PPM polarity
- Polarity stored in ESP32 Preferences
- Automatic PPM-loss detection
- Default PPM timeout: 100 ms
- ESP32_PPM library currently reads up to 8 physical PPM channels
- AFHDS2A itself still supports channels 1 to 14
- ESPRESS LRS itself still supports channels 1 to 16
- Internal `sweep` mode can exercise all 14 channels without a PPM source

### Motor safety

Channel 3 is used as the default motor/throttle channel.

Two independent safety mechanisms can force it to **1000 µs**:

1. loss of the PPM signal for more than 100 ms;
2. GPIO0 pulled LOW.

The motor-safety channel is protected from XANY channel assignment.

### Receiver failsafe programming

The firmware enables the original MPM AFHDS2A receiver-failsafe programming mechanism.

Default programmed behaviour:

- motor channel: **1000 µs**
- all other channels: **HOLD**

The failsafe values are stored by the AFHDS2A receiver itself and are intended to be applied by the receiver after loss of the RF link.

---

## AFHDS2A Telemetry

The firmware includes a passive AFHDS2A telemetry decoder.

The original MPM AFHDS2A RF state machine still owns the RF exchange. The telemetry helper observes the received A7105 FIFO data and decodes useful sensor values without printing from the high-priority RF callback.

Telemetry reception can be enabled or disabled and the choice is saved in ESP32 Preferences.

```text
tlm on
tlm off
tlm
```

### `tlog`

The command:

```text
tlog
```

prints the captured and decoded AFHDS2A telemetry.

This is especially useful for checking telemetry returned by the **FlySky FS-iA10B** and connected FlySky/iBUS-compatible sensors.

The telemetry decoder currently recognizes, among others:

| Sensor | AFHDS2A ID | Unit / format |
|---|---:|---|
| Receiver voltage | `0x00` | V × 100 |
| Temperature | `0x01` | `(raw - 400) / 10 °C` |
| Motor RPM | `0x02` | RPM |
| External/model voltage V1 | `0x03` | V × 100 |
| Battery current | `0x05` | A × 100 |
| Capacity / fuel | `0x06` | raw 16-bit |
| RPM | `0x07` | RPM |
| Vertical speed / climb rate | `0x09` | m/s × 100 |
| GPS course / heading | `0x0A` | degrees × 100 |
| GPS satellites | `0x0B` | satellite count |
| Vertical speed | `0x12` | m/s × 100 |
| GPS ground speed | `0x13` | m/s × 100 |
| Pressure / vario | `0x41` | pressure + temperature |
| GPS latitude | `0x80` | degrees × 10⁷ |
| GPS longitude | `0x81` | degrees × 10⁷ |
| GPS altitude | `0x82` | metres × 100 |
| Vario altitude | `0x83` | metres × 100 |
| FlySky altitude | `0xF9` | metres |
| RX SNR | `0xFA` | raw |
| RX noise | `0xFB` | raw |
| RX RSSI | `0xFC` | raw |
| RX error rate | `0xFE` | used for link quality |

Both normal **AA** sensor records and extended **AC** telemetry records are handled.

Useful telemetry commands:

```text
tlm on
tlm off
tlm
tlog
tclear
```

---

## Binding (A7105 only)

Binding can be started in two ways.

### Bind button (A7105 only)

GPIO10 is the bind input and is active LOW.

Hold the bind button during power-up to enter binding immediately.

### Serial console

```text
bind
```

starts an AFHDS2A binding attempt.

A bind attempt is supervised by the ESP32 wrapper and has a maximum duration of approximately 30 seconds.

The original MPM AFHDS2A handshake determines whether binding actually succeeded.

The receiver ID is stored in ESP32 Preferences.

Diagnostic command:

```text
blog
```

shows the last captured bind log.

---

## Transmitter ID

The AFHDS2A transmitter ID is persistent.

Available commands:

```text
txid
txid new
txid auto
txid 1234ABCD
```

### `txid new`

Creates and stores a new random transmitter ID.

A new transmitter identity changes the AFHDS2A hopping sequence, therefore the receiver must be bound again.

### `txid auto`

Generates a deterministic ID from the ESP32-C3 eFuse/MAC identity.

### Manual ID

Example:

```text
txid 1234ABCD
```

sets a specific 32-bit hexadecimal transmitter ID.

---

## OLED Display

The target ESP32-C3 Super Mini board includes a small SSD1306 OLED.

Current configuration:

| Function | GPIO |
|---|---:|
| I2C SDA | GPIO5 |
| I2C SCL | GPIO6 |
| OLED address | `0x3C` |
| I2C speed | 100 kHz |

The display uses **U8g2** and the current firmware is configured for the SSD1306 **72 × 40** display used on the target board.

The OLED can show information such as:

- AFHDS2A RF state
- binding status
- PPM state
- telemetry state
- link quality
- RSSI
- telemetry packet information

OLED state is stored in Preferences.

```text
oled on
oled off
oled
```

The OLED defaults to OFF on a fresh Preferences store.

---

## A7105 Connection

The project uses the A7105 in **3-wire SPI mode**.

There is intentionally no separate MISO line. The SDIO pin is bidirectional.

| A7105 function | ESP32-C3 |
|---|---:|
| SCK | GPIO4 |
| SDIO / bidirectional data | GPIO8 |
| CSN | GPIO7 |

The RF code performs an A7105 reset/register test at startup.

If the radio does not answer correctly, the firmware reports an A7105 error and does not start normal RF operation.

> Check the electrical requirements of your exact A7105 RF module before connecting it. The ESP32-C3 GPIOs are 3.3 V logic.

---

## Rx Nano ELRS Connection

The project use a Rx Nano receiver used as Tx ELRS transmitter.  
| ELRS function | ESP32-C3 |
|---|---:|
| RX | GPIO3 |
| TX | GPIO9 |


## Complete ESP32-C3 Pin Assignment

| Function | GPIO | Notes |
|---|---:|---|
| PPM input | GPIO1 | Trainer input |
| PPM input | GPIO2 | RC channel input |
| A7105 SCK | GPIO4 | 3-wire SPI |
| OLED / PCF SDA | GPIO5 | shared I2C |
| OLED / PCF SCL | GPIO6 | shared I2C |
| A7105 CSN | GPIO7 | chip select |
| A7105 SDIO | GPIO8 | bidirectional data |
| Bind button | GPIO10 | active LOW |
| Motor safety | GPIO0 | active LOW, forces CH3 to 1000 µs |
| CRSF RX from ELRS module | GPIO3 | UART RX, ELRS backend |
| CRSF TX to ELRS module | GPIO9 | UART TX, ELRS backend |
| Status LED | GPIO20 | RF/bind status |
| Security Trainer | GPIO21 | On/Off trainer |
The current dual-RF PCB therefore keeps the A7105 SPI bus and the CRSF UART physically separate. The A7105 and ELRS backends are not intended to transmit simultaneously.

Reference ESP32-C3 assignment used by the dual-RF firmware:

```text
GPIO0   Motor safety
GPIO1   PPM input trainer
GPIO2   PPM input
GPIO3   CRSF RX
GPIO4   A7105 SCK
GPIO5   I2C SDA
GPIO6   I2C SCL
GPIO7   A7105 CSN
GPIO8   A7105 SDIO
GPIO9   CRSF TX
GPIO10  Bind button

GPIO20  Status LED
GPIO21  Security trainer
```
---

## XANY / RCUL Support

The firmware includes two RCUL X-Any bridges using `RcTxSerial`.

### XANY1

- mode: **SW8**
- default channel: **CH5**
- eight ON/OFF bits
- two repetitions
- can receive its eight physical switch inputs from a PCF8574/PCF8574A

### XANY2

- mode: **SW8 + PROP**
- default channel: **CH6**
- eight ON/OFF bits
- one proportional byte
- two repetitions

Both XANY instances:

- can be independently enabled or disabled;
- store their state in Preferences;
- have configurable output channels;
- are prevented from using the motor-safety channel;
- are generated outside the RF timing callback.

Example commands:

```text
xany1
xany1 on
xany1 off
xany1 ch5
xany1 sw 5A

xany2
xany2 on
xany2 off
xany2 ch6
xany2 sw 5A
xany2 prop 128
```

---

## PCF8574 / PCF8574A SW8 Input

A PCF8574 or PCF8574A can provide eight physical switch inputs for XANY1.

Wiring convention:

```text
P0 -> SW1
P1 -> SW2
...
P7 -> SW8
```

Each switch closes to GND.

The inputs are therefore **active LOW**.

The firmware:

- releases all PCF8574 ports HIGH for input use;
- scans PCF8574A addresses `0x38` to `0x3F` first;
- also accepts PCF8574 addresses `0x20` to `0x27`;
- never claims `0x3C` or `0x3D`, which are reserved for the OLED;
- polls every 5 ms;
- applies approximately 10 ms debounce.

Commands:

```text
pcf
pcf scan
```

---

## RF Timing

Accurate timing is important for the AFHDS2A protocol.

The preferred scheduler is the ESP32 high-resolution `esp_timer`.

The original variable MPM callback timing is preserved, including the approximately:

```text
1700 µs
2150 µs
```

protocol intervals used by the AFHDS2A state machine.

A loop-based scheduler remains available as an automatic fallback if the ESP timer cannot be created.

RF diagnostic commands:

```text
rflog
rfclear
```

These report callback timing, jitter, execution time, overruns and scheduler resynchronizations.

---

## Internal Sweep Test

For bench testing without a PPM source:

```text
sweep
```

enables an internal sweep on AFHDS2A channels 1 to 14.

This is a temporary diagnostic mode only.

Return to normal PPM input with:

```text
ppm
```

Every reboot returns to the normal PPM input mode.

---

## PPM Polarity

The selected PPM edge is stored in Preferences.

Commands:

```text
ppm pol
ppm pos
ppm neg
```

or:

```text
ppm +
ppm -
```

Changing the PPM polarity stores the new setting and reboots the ESP32 cleanly.

Default on a fresh configuration:

```text
positive / rising edge
```

---

## Serial Console

Default serial speed:

```text
115200 baud
```

### Main configuration commands

```text
?                 Show status

h                 Configuration help
hf                Full help including diagnostics

rf                Show selected RF backend
rf ds2a           Select AFHDS2A / A7105 and restart
rf elrs           Select ExpressLRS / CRSF and restart

btsimu            Show BLE telemetry simulator state
btsimu on         Enable BLE telemetry simulator
btsimu off        Disable BLE telemetry simulator
id                Display TX/RX IDs

txid              Display saved TX ID
txid new          Generate a new random TX ID
txid auto         Use board-derived TX ID
txid 1234ABCD     Set a manual hexadecimal TX ID

bind              Start AFHDS2A binding

ppm               Return to normal PPM input
ppm pol           Show PPM polarity
ppm pos           Save positive PPM polarity
ppm neg           Save negative PPM polarity

fs                Show receiver failsafe configuration
fs send           Re-arm receiver failsafe programming

tlm               Show telemetry reception state
tlm on            Enable telemetry reception
tlm off           Disable telemetry reception

oled              Show OLED state
oled on           Enable OLED
oled off          Disable OLED

xany1             Show XANY1 status
xany1 on/off      Enable or disable XANY1
xany1 ch5         Select XANY1 output channel
xany1 sw 5A       Manual SW8 test value

xany2             Show XANY2 status
xany2 on/off      Enable or disable XANY2
xany2 ch6         Select XANY2 output channel
xany2 sw 5A       Manual SW8 test value
xany2 prop 128    Manual proportional test value

pcf               Show PCF8574 status
pcf scan          Scan the I2C bus for PCF8574(A)
```

### RF-specific console help

The console help is separated according to the active RF backend:

- common commands are always shown;
- AFHDS2A/A7105-only commands are shown when the DS2A backend is active;
- CRSF/ELRS-only commands are shown when the ELRS backend is active;
- `h` and `hf` therefore avoid displaying irrelevant commands for the other RF backend.

The backend selector itself remains available from either mode.
### Diagnostic commands

```text
sweep             Internal CH1..CH14 sweep
blog              Show last bind log
tlog              Show AFHDS2A telemetry log
tclear            Clear telemetry log
d                 Toggle debug output
ch                Show CH1..CH14 pulse widths
hop               Show the 16 hopping frequencies
rflog             Show RF timer diagnostics
rfclear           Clear RF timer diagnostics
```

---

## Saved Preferences

The project uses the ESP32 `Preferences` storage namespace:

```text
mpm_afh
```

Persistent settings include, depending on the firmware version:
- transmitter ID (AHFDS2A only)
- receiver/bind state (AHFDS2A only)
- PPM polarity
- telemetry RX ON/OFF
- OLED ON/OFF
- XANY1 ON/OFF
- XANY2 ON/OFF
- XANY1 output channel
- XANY2 output channel
- selected RF backend: AFHDS2A/DS2A or ELRS/CRSF

This allows normal operating settings to survive a reboot.

---

## Typical First Test

A simple first bench test can be performed as follows.

1. Connect the A7105 or ELRS module.
2. Connect the PPM source to GPIO2.
3. Connect the receiver with servos or a safe test load.
4. Open the serial terminal at 115200 baud.
5. Power the receiver in bind mode.
6. Start binding:

```text
bind
```

7. Check the status:

```text
?
```

8. Enable telemetry reception:

```text
tlm on
```

9. Inspect the received FlySky telemetry:

```text
tlog
```

10. Verify motor safety before connecting any propulsion system.

---

## Bluetooth LE Telemetry — Work in Progress

> [!IMPORTANT]  
> **Dual-RF branch update:** BLE telemetry is now also used by the CRSF/ELRS backend. A CRSF telemetry simulator has been validated end-to-end with the Android Telemetry Viewer. The original text below is retained because it documents the earlier AFHDS2A-only development stage.
A **Bluetooth Low Energy telemetry extension** is being developed separately.

The goal is to allow the ESP32-C3 transmitter module to connect directly to an **Android phone** and forward the telemetry received from the FlySky receiver.

The intended data path is:

```text
FlySky/ELRS sensors
      |
      v
FlySky/ELRS PWM receiver (FS-iA6B or ELRS pWM)
      |
      | AFHDS2A/ELRS telemetry
      v
A7105/ELRS + ESP32-C3
      |
      | Bluetooth Low Energy
      v
Android phone
      |
      v
Telemetry application
```

The important design rule is that the Bluetooth extension must **not disturb the working AFHDS2A RF timing or the existing telemetry decoder**.

The existing `tlog` data is therefore the reference: the BLE bridge will forward the same validated telemetry information that is already received successfully from the FS-iA10B.

Planned phone-side presentation includes values such as:

- model battery voltage;
- receiver voltage;
- current and consumed capacity;
- RPM (ELRS only);
- temperatures;
- RSSI / link quality;
- GPS latitude and longitude;
- GPS altitude;
- ground speed;
- heading;
- satellite count;
- vario information.

The BLE work is intentionally kept separate from the stable AFHDS2A transmitter functionality until it has been validated.

---

## CRSF / ELRS Bluetooth telemetry and simulator

The same `btsimu on/off` command is used for both RF backends. The simulator automatically follows the selected RF mode.

### AFHDS2A mode

When AFHDS2A is selected, the existing MULTI/AFHDS2A telemetry simulation path is retained.

### ELRS / CRSF mode

When ELRS is selected, the ESP32-C3 generates **raw standard CRSF telemetry frames** and forwards them over BLE to the Android application.

The CRSF simulator currently exercises frames such as:

- Link Statistics;
- Battery Sensor;
- GPS;
- Vario;
- Attitude.

This path has been tested without an ELRS RF module and validates:
```text
ESP32-C3
   |
   | raw CRSF telemetry
   v
Bluetooth LE
   |
   v
Telemetry Viewer Android
```
The Android application already contains CRSF decoding/display support. During the CRSF simulator test, the expected CRSF telemetry fields were successfully activated in the interface.

This simulator is useful for separating Android/BLE debugging from the later physical ELRS RF-module tests.

> [!NOTE]  
> Successful `btsimu` operation validates the ESP32-C3 -> BLE -> Android CRSF path. It does not by itself validate the external ELRS transmitter module, RF link or returned over-the-air telemetry.

---
## Source File Structure

```text
ESP32_C3_BTLE_RFMODE_CHOICE.ino
    Main ESP32-C3 application
    PPM input
    Preferences
    console
    bind supervision
    failsafe
    OLED
    RF scheduler

AFHDS2A_a7105.ino
    Original/adapted MPM AFHDS2A protocol logic

A7105_SPI.ino
    A7105 register, FIFO, power and RF functions

SPI.ino
    ESP32-C3 3-wire SPI implementation

Telemetry_AFHDS2A.ino
    Passive telemetry capture
    telemetry decoder
    tlog diagnostics

Xany_SW8.ino
    RCUL / RcTxSerial XANY1 and XANY2 bridges

PCF8574_SW8.ino
    Eight physical switch inputs for XANY1

Convert.ino
    MPM-compatible conversion helpers

MPM_ESP32C3_Compat.h
    Compatibility definitions required by the original MPM source

iface_a7105.h
    A7105 register and interface definitions

RF_Config.h
    RF backend selection/configuration
    AFHDS2A / CRSF mode definitions

CRSF_Main.h
CRSF_Main.ino
    CRSF UART interface
    RC_CHANNELS_PACKED generation
    CRSF RX handling
    ELRS backend processing

BT_LE.h
    Bluetooth LE declarations used by the telemetry bridge
```

---

## Required Arduino Libraries

The project currently uses:

- **ESP32 Arduino core**
- **ESP32_PPM**
- **U8g2**
- **elapsedMillis**
- **RCUL / RcTxSerial** for XANY support
- **ESP32_PPM** for output PPM with XANY support
- **RculCrsfSerial** for CRSF channel transport / CRSF serial handling in the ELRS backend

ESP32 core components used directly include:

- `Preferences`
- `Wire`
- `esp_timer`
- `esp_random`

CRSF library integration notes for the ESP32-C3 build:

- the 16-channel packer must flush bytes while `bitsInScratch >= 8`;
- RCUL synchronization is asserted only after a valid `RC_CHANNELS_PACKED (0x16)` frame is decoded;
- ESP32 endian helper macros such as `htobe16`, `be16toh`, `htobe32` and `be32toh` must not be redefined when already supplied by the platform.
Make sure the required libraries are installed before compiling.

---

## Design Philosophy

This project deliberately tries to keep the original MPM AFHDS2A protocol code recognizable.

Where possible:

- the MPM variable names are preserved;
- the MPM RF state machine remains responsible for AFHDS2A;
- ESP32-specific functions are implemented around it;
- telemetry diagnostics observe the RF traffic rather than rewriting the protocol;
- serial output is kept out of the high-priority RF callback;
- optional features are processed from the normal ESP32 loop whenever possible.

The same philosophy is used for the ELRS extension:

- the existing AFHDS2A/A7105 path is kept intact;
- CRSF is implemented in separate RF configuration and CRSF source files;
- the common PPM/SWEEP/XANY channel generation remains upstream of both RF backends;
- only the selected backend owns the active RF output path;
- CRSF/ELRS additions should not change the proven AFHDS2A timing behaviour.
This makes comparison with upstream MPM code easier and reduces the risk of introducing timing regressions.

---

## Safety

This firmware controls RC equipment and may operate motors, vehicles, boats, aircraft or other moving models.

Always:

- perform initial tests without a propeller or other dangerous load;
- verify channel order and direction;
- verify PPM-loss behaviour;
- verify the GPIO0 motor-safety input;
- verify the receiver failsafe after binding;
- confirm correct RF range before real operation;
- check that optional debug or telemetry features do not affect control reliability.
- verify that only the intended RF backend is active before operating a model;
- verify the voltage and current requirements of the external ELRS TX module before connection;
- perform an independent range/failsafe test after changing from AFHDS2A to ELRS or back.																			 

The project is provided for experimental and development use. The user remains responsible for validating the complete installation before operating a real model.

---

## Credits
> [!NOTE]  
This project contains and adapts AFHDS2A/A7105 protocol work from the **Multiprotocol (MPM)** project.  
The original version of [Telemetry Viewer](https://github.com/CrazyDude1994/android-taranis-smartport-telemetry) was created by CrazyDude1994.  
Another version on a [Telemetry Viewer](https://github.com/RomanLut/android-taranis-smartport-telemetry) fork by Romanlut (v1.6.3) that includes several options as well as fixes.  
This project is based on [Telemetry Viewer](https://github.com/juricabi/android-taranis-smartport-telemetry) fork by juricabi, no Google Maps (no need Google key), add new mapas type.  

The original source files retain their upstream copyright and GNU GPL notices.

Additional ESP32-C3 integration in this project includes:

- PPM input;
- ESP32 high-resolution RF scheduling;
- persistent Preferences storage;
- AFHDS2A telemetry diagnostics;
- OLED status display;
- motor safety;
- receiver failsafe programming;
- RCUL XANY support;
- PCF8574 switch input support;
- ongoing Bluetooth LE telemetry development.
- selectable AFHDS2A / CRSF-ELRS RF backend;
- CRSF RC channel generation using the RCUL/RculCrsfSerial integration;
- CRSF telemetry simulation and BLE forwarding.

When redistributing modified versions, preserve the original licensing and attribution notices contained in the source files.

---

## Project Status

The project is under active development.

Current focus:

1. stable AFHDS2A transmission on ESP32-C3;
1. CRSF work in progress;
1. reliable telemetry reception from the FlySky FS-iA10B;
1. safe PPM/failsafe operation;
1. XANY/physical switch integration;
1. Bluetooth LE telemetry forwarding to an Android phone.

Feedback, telemetry captures and hardware test results are useful for further development.

 ---
## Dual-RF / ELRS Development Status

The AFHDS2A/A7105 backend remains the established and tested RF path.

The CRSF/ExpressLRS extension currently has the following status:

- CRSF firmware integration compiles on ESP32-C3;
- RF backend selection is persistent and can be changed with `rf ds2a` / `rf elrs`;
- the CRSF backend uses the same 16-channel internal data as PPM/SWEEP/XANY;
- `RC_CHANNELS_PACKED (0x16)` generation is implemented;
- CRSF BLE telemetry simulation is operational;
- the Android Telemetry Viewer CRSF display path has been validated with `btsimu`;
- physical external ELRS TX-module wiring and over-the-air RC/telemetry validation remain hardware test steps.

Recommended ELRS hardware-validation sequence:

```text
1. Select: rf elrs
2. Verify ESP32-C3 <-> ELRS-module UART wiring and supply
3. Verify CRSF RC channel transmission
4. Verify receiver control / channel order / failsafe
5. Verify returned CRSF telemetry
6. Verify BLE forwarding of real returned telemetry
7. Perform range and reliability tests before model use
```

The intended final system remains:

```text
                    +--> A7105 / XL7105-D03B --> AFHDS2A
PPM / XANY --> C3 --|
                    +--> CRSF UART --> ELRS TX module --> ExpressLRS
```

Only one RF backend is selected for normal operation at a time.
