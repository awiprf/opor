# ============================================================
# run.ps1 - Universal microservice runner for the grd monorepo
# ============================================================
# Usage:
#   .\run.ps1 :svc:aut:sgu:usn:vec                          # one
#   .\run.ps1 :svc:aut:sgu:usn:vec :svc:pay:txn:inv:chk     # several
#   .\run.ps1 -All                                           # all
#
# Convention:
#   Domain .env.local lives at  svc\<domain>\.env.local
# ============================================================

param(
    [switch]$All,
    [Parameter(Position = 0, ValueFromRemainingArguments = $true)]
    [string[]]$GradlePaths
)

# ---------------------------------------------------------------
# Resolve the list of services to run
# ---------------------------------------------------------------
if ($All) {
    $settingsFile = Join-Path $PSScriptRoot 'settings.gradle.kts'
    if (-not (Test-Path $settingsFile)) {
        Write-Error "settings.gradle.kts not found"
        exit 1
    }
    $GradlePaths = @()
    foreach ($line in (Get-Content $settingsFile)) {
        if ($line -match '"(svc:[^"]+)"') {
            $GradlePaths += $matches[1]
        }
    }
    if ($GradlePaths.Count -eq 0) {
        Write-Error 'No svc: modules found in settings.gradle.kts'
        exit 1
    }
    Write-Host "[run] --all: found $($GradlePaths.Count) service(s)" -ForegroundColor Magenta
}

if (-not $GradlePaths -or $GradlePaths.Count -eq 0) {
    Write-Host 'Usage:' -ForegroundColor Yellow
    Write-Host '  .\run.ps1 :svc:aut:sgu:usn:vec                        # one service'
    Write-Host '  .\run.ps1 :svc:aut:sgu:usn:vec :svc:pay:txn:inv:chk   # several'
    Write-Host '  .\run.ps1 -All                                         # all services'
    exit 0
}

# ---------------------------------------------------------------
# Helper: load .env.local for a domain into current process env
# ---------------------------------------------------------------
function Load-DomainEnv {
    param([string]$domain)
    $envPath = Join-Path $PSScriptRoot ('svc\' + $domain + '\.env.local')
    if (Test-Path $envPath) {
        foreach ($line in (Get-Content $envPath)) {
            if ($line -match '^\s*([^#=]+)=(.+)$') {
                [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim())
            }
        }
        return $envPath
    }
    return $null
}

# ---------------------------------------------------------------
# Helper: extract domain from a Gradle path
# ---------------------------------------------------------------
function Get-Domain {
    param([string]$gradlePath)
    $normalized = $gradlePath.TrimStart(':')
    $segments = $normalized -split ':'
    if ($segments[0] -ne 'svc' -or $segments.Length -lt 2) {
        Write-Error ('Invalid path. Expected :svc:<domain>:..., got: ' + $gradlePath)
        return $null
    }
    return $segments[1]
}

# ---------------------------------------------------------------
# Single service: run in foreground (same terminal)
# ---------------------------------------------------------------
if ($GradlePaths.Count -eq 1) {
    $path   = $GradlePaths[0]
    $domain = Get-Domain $path
    if (-not $domain) { exit 1 }

    $loaded = Load-DomainEnv $domain
    if ($loaded) {
        Write-Host ('[run] Loaded env: ' + $loaded) -ForegroundColor Cyan
    } else {
        Write-Warning ('[run] No .env.local found for domain: ' + $domain)
    }

    $normalized = $path.TrimStart(':')
    $gradleTask = ':' + $normalized + ':bootRun'
    Write-Host ('[run] Booting: ' + $gradleTask) -ForegroundColor Green
    & (Join-Path $PSScriptRoot 'gradlew.bat') $gradleTask
    exit $LASTEXITCODE
}

# ---------------------------------------------------------------
# Multiple services: each in its own terminal window
# ---------------------------------------------------------------
Write-Host ('[run] Launching ' + $GradlePaths.Count + ' service(s) in separate windows...') -ForegroundColor Magenta

foreach ($path in $GradlePaths) {
    $domain = Get-Domain $path
    if (-not $domain) { continue }

    $normalized  = $path.TrimStart(':')
    $gradleTask  = ':' + $normalized + ':bootRun'
    $envRelPath  = 'svc\' + $domain + '\.env.local'
    $svcName     = ($normalized -split ':')[-1]

    # Build child script from an array of lines (avoids all escaping issues)
    $lines = @(
        ('Set-Location ' + "'" + $PSScriptRoot + "'")
        ('$envPath = ' + "'" + '.\' + $envRelPath + "'")
        'if (Test-Path $envPath) {'
        '    foreach ($ln in (Get-Content $envPath)) {'
        '        if ($ln -match ''^\s*([^#=]+)=(.+)$'') {'
        '            [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim())'
        '        }'
        '    }'
        ('    Write-Host ''[run] Loaded env: ' + $envRelPath + ''' -ForegroundColor Cyan')
        '}'
        ('Write-Host ''[run] Booting: ' + $gradleTask + ''' -ForegroundColor Green')
        ('& ''' + (Join-Path $PSScriptRoot 'gradlew.bat') + ''' ' + $gradleTask)
        'Read-Host ''Press Enter to close'''
    )
    $childScript = $lines -join "`n"
    $encoded = [Convert]::ToBase64String([System.Text.Encoding]::Unicode.GetBytes($childScript))

    Write-Host ('  [' + $svcName + ']  ' + $gradleTask + '  (env: ' + $envRelPath + ')') -ForegroundColor Green
    Start-Process powershell -ArgumentList '-NoExit', '-EncodedCommand', $encoded
}

Write-Host ''
Write-Host '[run] All services launched. Close individual windows to stop them.' -ForegroundColor Magenta
