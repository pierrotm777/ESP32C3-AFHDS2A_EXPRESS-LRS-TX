$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

function Patch-File {
    param(
        [Parameter(Mandatory=$true)][string]$RelativePath,
        [Parameter(Mandatory=$true)][array]$Replacements
    )

    $path = Join-Path $root $RelativePath
    if (-not (Test-Path $path)) {
        throw "Fichier introuvable: $RelativePath"
    }

    $backup = "$path.bak_v16_2"
    if (-not (Test-Path $backup)) {
        Copy-Item $path $backup
    }

    $text = [System.IO.File]::ReadAllText($path)

    foreach ($pair in $Replacements) {
        $old = [string]$pair[0]
        $new = [string]$pair[1]

        if ($text.Contains($old)) {
            $text = $text.Replace($old, $new)
        }
        elseif ($text.Contains($new)) {
            # Deja corrige: rien a faire.
        }
        else {
            throw "Motif attendu introuvable dans $RelativePath : $old"
        }
    }

    [System.IO.File]::WriteAllText(
        $path,
        $text,
        (New-Object System.Text.UTF8Encoding($false))
    )
    Write-Host "OK  $RelativePath"
}

Write-Host "============================================================"
Write-Host " Telemetry Android 14 v16.2 - Correctifs Kotlin / AndroidX"
Write-Host "============================================================"
Write-Host ""

Patch-File "app\build.gradle" @(
    @("implementation 'com.google.android:flexbox:3.0.0'",
      "implementation 'com.google.android.flexbox:flexbox:3.0.0'")
)

Patch-File "app\src\main\java\crazydude\com\telemetry\manager\PreferenceManager.kt" @(
    @('sharedPreferences.getString(it.name + "_position", it.position),',
      'sharedPreferences.getString(it.name + "_position", it.position) ?: it.position,')
)

Patch-File "app\src\main\java\crazydude\com\telemetry\maps\google\GoogleMapWrapper.kt" @(
    @('val marker = GoogleMarker(googleMarker, context)',
      'val marker = GoogleMarker(googleMarker ?: throw IllegalStateException("Unable to add Google Maps marker"), context)'),
    @('override fun onMapReady(googleMap: GoogleMap?) {' + "`r`n" + '        this.googleMap = googleMap!!',
      'override fun onMapReady(googleMap: GoogleMap) {' + "`r`n" + '        this.googleMap = googleMap'),
    @('override fun onSaveInstanceState(outState: Bundle?) {' + "`r`n" + '        mapView.onSaveInstanceState(outState)',
      'override fun onSaveInstanceState(outState: Bundle?) {' + "`r`n" + '        outState?.let { mapView.onSaveInstanceState(it) }')
)

Patch-File "app\src\main\java\crazydude\com\telemetry\service\DataService.kt" @(
    @('it?.sessionId.let { sendTelemetryData(it) }',
      'it.sessionId?.let { sessionId -> sendTelemetryData(sessionId) }')
)

Patch-File "app\src\main\java\crazydude\com\telemetry\ui\HorizonView.kt" @(
    @('override fun onDraw(canvas: Canvas?) {' + "`r`n" + '        super.onDraw(canvas)' + "`r`n`r`n" + '        canvas?.let {',
      'override fun onDraw(canvas: Canvas) {' + "`r`n" + '        super.onDraw(canvas)' + "`r`n`r`n" + '        canvas.let {')
)

Patch-File "app\src\main\java\crazydude\com\telemetry\ui\RCWidget.kt" @(
    @('override fun onDraw(canvas: Canvas?) {' + "`r`n" + '        super.onDraw(canvas)' + "`r`n`r`n" + '        canvas?.let {',
      'override fun onDraw(canvas: Canvas) {' + "`r`n" + '        super.onDraw(canvas)' + "`r`n`r`n" + '        canvas.let {')
)

Patch-File "app\src\main\java\crazydude\com\telemetry\ui\PrefsFragment.kt" @(
    @('preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)',
      'preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(listener)'),
    @('preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)',
      'preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)')
)

Patch-File "app\src\main\java\crazydude\com\telemetry\ui\MapsActivity.kt" @(
    @('clipboardManager.primaryClip = ClipData.newPlainText("Location", posString)',
      'clipboardManager.setPrimaryClip(ClipData.newPlainText("Location", posString))'),
    @('progressDialog.getWindow().setFlags(',
      'progressDialog.window?.setFlags('),
    @('progressDialog.getWindow().decorView.systemUiVisibility = 0',
      'progressDialog.window?.decorView?.systemUiVisibility = 0'),
    @('progressDialog.getWindow().decorView.systemUiVisibility =',
      'progressDialog.window?.decorView?.systemUiVisibility ='),
    @('progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);',
      'progressDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);'),
    @('seekbar.progress = nextPosition',
      'seekBar.progress = nextPosition'),
    @('DataDecoder.Companion.FlyMode.RATE -> {' + "`r`n" + '                mode.text = mode.text.toString() + " | Geo"',
      'DataDecoder.Companion.FlyMode.GEO -> {' + "`r`n" + '                mode.text = mode.text.toString() + " | Geo"'),
    @('RequestWritePermissionSequenceType.EXPORT_KML -> showExportKMLDialog1()' + "`r`n" + '                    }',
      'RequestWritePermissionSequenceType.EXPORT_KML -> showExportKMLDialog1()' + "`r`n" + '                        RequestWritePermissionSequenceType.NONE -> Unit' + "`r`n" + '                    }'),
    @('dialog.getWindow().setFlags(',
      'dialog.window?.setFlags('),
    @('dialog.getWindow().decorView.systemUiVisibility = 0',
      'dialog.window?.decorView?.systemUiVisibility = 0'),
    @('dialog.getWindow().decorView.systemUiVisibility =',
      'dialog.window?.decorView?.systemUiVisibility ='),
    @('dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);',
      'dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);')
)

Write-Host ""
Write-Host "Correctifs appliques."
Write-Host "Des sauvegardes .bak_v16_2 ont ete creees."
Write-Host ""
Write-Host "Lancement de Gradle..."
Write-Host ""

& ".\gradlew.bat" clean assembleDebug
$code = $LASTEXITCODE

Write-Host ""
if ($code -eq 0) {
    Write-Host "============================================================"
    Write-Host " BUILD SUCCESSFUL"
    Write-Host " APK:"
    Write-Host " app\build\outputs\apk\debug\app-debug.apk"
    Write-Host "============================================================"
} else {
    Write-Host "============================================================"
    Write-Host " La compilation a encore rencontre une erreur."
    Write-Host " Envoyez-moi la fin du nouveau log."
    Write-Host "============================================================"
}
exit $code
