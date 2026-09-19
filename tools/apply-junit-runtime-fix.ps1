$ErrorActionPreference = "Stop"

$buildFile = Join-Path (Get-Location) "build.gradle"

if (-not (Test-Path $buildFile)) {
    throw "build.gradle was not found in the current directory. Run this script from the Wayfinder project root."
}

$content = Get-Content $buildFile -Raw

if ($content -match "org\.junit\.platform:junit-platform-launcher") {
    Write-Host "JUnit Platform launcher dependency is already present. No changes needed."
    exit 0
}

$dependencyLine = "    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'"

# Prefer placing the launcher next to an existing JUnit test dependency.
$junitPattern = "(?m)^(\s*testImplementation\s+['""][^'""]*junit[^'""]*['""][^\r\n]*)$"
if ($content -match $junitPattern) {
    $content = [regex]::Replace(
        $content,
        $junitPattern,
        "`$1`r`n$dependencyLine",
        1
    )
}
# Otherwise insert immediately after the first dependencies { line.
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

Set-Content -Path $buildFile -Value $content -Encoding UTF8

Write-Host ""
Write-Host "Added:"
Write-Host "  testRuntimeOnly 'org.junit.platform:junit-platform-launcher'"
Write-Host ""
Write-Host "Now run:"
Write-Host "  .\gradlew.bat clean test"
Write-Host "  .\gradlew.bat build"
