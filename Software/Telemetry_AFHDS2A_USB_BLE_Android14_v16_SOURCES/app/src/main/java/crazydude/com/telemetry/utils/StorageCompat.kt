package crazydude.com.telemetry.utils

import android.content.Context
import android.os.Environment
import java.io.File

object StorageCompat {
    /**
     * App-scoped external Documents/TelemetryLogs directory.
     * No READ/WRITE_EXTERNAL_STORAGE permission is required on Android 10+.
     */
    fun telemetryLogDir(context: Context): File {
        val external = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val base = external ?: context.filesDir
        return File(base, "TelemetryLogs").apply { mkdirs() }
    }
}
