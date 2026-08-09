# 1. Load Auth domain environment variables
Get-Content .\svc\aut\.env.local | ForEach-Object {
    if ($_ -match '^\s*([^#=]+)=(.+)$') {
        [System.Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim())
    }
}

# 2. Boot the /vec microservice
.\gradlew.bat :svc:aut:sgu:usn:vec:bootRun