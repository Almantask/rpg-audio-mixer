param(
    [Parameter(Mandatory=$true)]
    [int]$Iteration
)

$tag = "@iter$Iteration"
Write-Host "Running tests for Iteration $Iteration ($tag)..." -ForegroundColor Cyan

# Run the connected tests with the specified tag
$env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"
.\gradlew connectedAndroidTest -PcucumberTags="$tag"
