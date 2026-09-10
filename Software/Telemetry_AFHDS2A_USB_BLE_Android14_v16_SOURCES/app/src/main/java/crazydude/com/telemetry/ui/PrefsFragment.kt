package crazydude.com.telemetry.ui

import android.content.*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import crazydude.com.telemetry.R
import crazydude.com.telemetry.manager.PreferenceManager
import crazydude.com.telemetry.utils.FileLogger
import java.io.IOException

class PrefsFragment : PreferenceFragmentCompat() {

    private lateinit var prefManager: PreferenceManager
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        updateSummary()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "settings"
        setPreferencesFromResource(R.xml.preferences, rootKey)

        prefManager = PreferenceManager(context!!)

        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
        findPreference<androidx.preference.Preference>("copy_debug_info")?.setOnPreferenceClickListener {
            context?.let {
                val clipboardManager =
                    it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                try {
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, FileLogger(it).copyLogFile()))
                } catch (e: IOException) {
                    Toast.makeText(it, "No log data available yet", Toast.LENGTH_SHORT).show()
                    return@let
                }
                Toast.makeText(it, "Debug data has been copied", Toast.LENGTH_SHORT).show()
            }
            return@setOnPreferenceClickListener false
        }
        findPreference<androidx.preference.Preference>("fork_credits")?.setOnPreferenceClickListener {
            context?.let { ctx ->
                AlertDialog.Builder(ctx)
                    .setTitle("Credits")
                    .setMessage("Original application: CrazyDude1994\nFork and major enhancements: Roman Lut\nAFHDS2A USB/BLE + Android 14 adaptation: this fork\nHistorical UVC stack: saki / Serenegiant\nModern UVC engine: HanShiYing (shiyinghan) / UVCAndroid")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
            false
        }
        findPreference<androidx.preference.Preference>("fork_disclaimer")?.setOnPreferenceClickListener {
            context?.let { ctx ->
                AlertDialog.Builder(ctx)
                    .setTitle("Disclaimer / Responsibility")
                    .setMessage("FR : Ce fork est fourni en l'etat, sans garantie. Son mainteneur decline, dans les limites permises par la loi, toute responsabilite pour tout dommage, perte de donnees, deterioration d'un modele RC, accident ou autre consequence liee a son utilisation. Verifiez toujours le fonctionnement de la telemetrie dans des conditions sures avant toute utilisation reelle.\n\nEN: This fork is provided as-is, without warranty. Use it at your own risk. Always validate telemetry safely before real-world use.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
            false
        }
        findPreference<androidx.preference.Preference>("clear_debug_info")?.setOnPreferenceClickListener {
            context?.let {
                FileLogger(it).clearLogFile()
                Toast.makeText(it, "Debug data has been cleared", Toast.LENGTH_SHORT).show()
            }
            return@setOnPreferenceClickListener false
        }
        updateSummary()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun updateSummary() {
        preferenceScreen.findPreference<androidx.preference.Preference>("callsign")?.summary = prefManager.getCallsign()
        preferenceScreen.findPreference<androidx.preference.Preference>("model")?.summary = prefManager.getModel()
    }
}