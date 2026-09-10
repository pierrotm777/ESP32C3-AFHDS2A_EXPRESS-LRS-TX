$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$local = Join-Path $root "local.properties"

$secure = Read-Host "Entrez votre cle Google Maps API (saisie masquee)" -AsSecureString
$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try {
    $key = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
}

if ($key -notmatch '^AIza[0-9A-Za-z_-]{35}$') {
    throw "Format de cle Google API invalide. La cle doit commencer par AIza et contenir 39 caracteres."
}

$lines = @()
if (Test-Path $local) {
    $lines = Get-Content $local | Where-Object { $_ -notmatch '^MAPS_API_KEY=' }
}
else {
    $lines += 'sdk.dir=C\:\\Android\\Sdk'
}

$lines += "MAPS_API_KEY=$key"
Set-Content -Path $local -Value $lines -Encoding ASCII
$key = $null
Write-Host ""
Write-Host "Cle Maps enregistree dans local.properties (fichier ignore par Git)." -ForegroundColor Green
Write-Host "Elle ne doit pas etre publiee ni envoyee dans le chat."
