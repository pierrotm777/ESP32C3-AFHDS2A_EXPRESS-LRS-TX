# Privacy notes for this Android 14 fork

This fork stores telemetry logs locally on the device unless the user explicitly enables the optional UAV Radar upload feature.

Third-party services/libraries used by the current v16 source include Google Play Services / Google Maps, OpenStreetMap/osmdroid, USB serial support and UVCAndroid. Their own privacy terms may apply when their network-backed features are used.

The v16 build does **not** include the historical Firebase Analytics, Crashlytics or Fabric integrations from older versions of the project.

Google Maps requires an API key supplied locally at build time. The key is not included in this source archive.

Telemetry and CSV logs are written to application-scoped external storage. UVC recordings are written through MediaStore to `Movies/TelemetryCamera` on Android 10 and later.

The optional UAV Radar feature is disabled by default. If enabled by the user, telemetry data such as callsign/model and aircraft position may be sent to the configured UAV Radar service while the required conditions are met.

See `FORK_NOTICE.md` for project lineage, credits and the disclaimer / limitation of liability.
