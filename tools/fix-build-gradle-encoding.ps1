$ErrorActionPreference = "Stop"

$buildFile = Join-Path (Get-Location) "build.gradle"

if (-not (Test-Path $buildFile)) {
    throw "build.gradle was not found in the current directory. Run this script from the Wayfinder project root."
}

# Read raw bytes and remove UTF-8 BOM if present.
$bytes = [System.IO.File]::ReadAllBytes($buildFile)

if ($bytes.Length -ge 3 -and
    $bytes[0] -eq 0xEF -and
    $bytes[1] -eq 0xBB -and
    $bytes[2] -eq 0xBF) {

    $bytes = $bytes[3..($bytes.Length - 1)]
    Write-Host "Removed UTF-8 BOM from build.gradle."
}

$content = [System.Text.Encoding]::UTF8.GetString($bytes)

# Ensure the JUnit Platform launcher dependency exists.
if ($content -notmatch "org\.junit\.platform:junit-platform-launcher") {
    $dependencyLine = "    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'"

    $junitPattern = "(?m)^(\s*testImplementation\s+['""][^'""]*junit[^'""]*['""][^\r\n]*)$"

    if ($content -match $junitPattern) {
        $content = [regex]::Replace(
            $content,
            $junitPattern,
            "`$1`r`n$dependencyLine",
            1
        )
    }
    elseif ($content -match "(?m)^\s*dependencies\s*\{") {
        $content = [regex]::Replace(
            $content,
            "(?m)^(\s*dependencies\s*\{\s*)$",
            "`$1`r`n$dependencyLine",
            1
        )
    }
    else {
        throw "Could not locate a dependencies { } block in build.gradle."
    }

    Write-Host "Added JUnit Platform launcher dependency."
}
else {
    Write-Host "JUnit Platform launcher dependency already present."
}

# IMPORTANT: write UTF-8 without BOM.
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($buildFile, $content, $utf8NoBom)

Write-Host ""
Write-Host "build.gradle is now UTF-8 without BOM."
Write-Host ""
Write-Host "Run:"
Write-Host "  .\gradlew.bat clean test"
Write-Host "  .\gradlew.bat build"
