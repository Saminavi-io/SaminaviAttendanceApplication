# Test Email Configuration Script
# This script tests if the email configuration is working properly

Write-Host "=== Testing Email Configuration ===" -ForegroundColor Green
Write-Host ""

# Base URL for your application
$baseUrl = "http://localhost:9080"

# Test employee credentials
$testEmployee = @{
    username = "ajay.g"
    password = "password123"
}

# Function to login and get JWT token
function Get-JwtToken {
    param($username, $password)
    
    $loginBody = @{
        username = $username
        password = $password
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
        return $response.token
    }
    catch {
        Write-Host "Failed to login for $username" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

# Function to make authenticated request
function Invoke-AuthenticatedRequest {
    param($url, $method, $token)
    
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    try {
        return Invoke-RestMethod -Uri $url -Method $method -Headers $headers
    }
    catch {
        Write-Host "Request failed: $url" -ForegroundColor Red
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

Write-Host "1. Testing login..." -ForegroundColor Yellow
$token = Get-JwtToken -username $testEmployee.username -password $testEmployee.password

if ($token) {
    Write-Host "   Login successful!" -ForegroundColor Green
    
    Write-Host "2. Testing clock-in (this will trigger email notifications)..." -ForegroundColor Yellow
    $clockInResult = Invoke-AuthenticatedRequest -url "$baseUrl/api/attendance/clock-in" -method "POST" -token $token
    
    if ($clockInResult) {
        Write-Host "   Clock-in successful!" -ForegroundColor Green
        Write-Host "   Email notifications should be sent to:" -ForegroundColor Cyan
        Write-Host "   - Employee: ajay.g@saminavi.io" -ForegroundColor Cyan
        Write-Host "   - HR: sridhar.uppu@saminavi.io" -ForegroundColor Cyan
        Write-Host "   - Manager: manager@saminavi.com" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "   Check these email addresses for notifications!" -ForegroundColor Yellow
    } else {
        Write-Host "   Clock-in failed!" -ForegroundColor Red
    }
    
    Write-Host "3. Testing clock-out..." -ForegroundColor Yellow
    Start-Sleep -Seconds 2  # Wait a bit before clock-out
    
    $clockOutResult = Invoke-AuthenticatedRequest -url "$baseUrl/api/attendance/clock-out" -method "POST" -token $token
    
    if ($clockOutResult) {
        Write-Host "   Clock-out successful!" -ForegroundColor Green
        Write-Host "   Additional email notifications sent!" -ForegroundColor Cyan
    } else {
        Write-Host "   Clock-out failed!" -ForegroundColor Red
    }
    
} else {
    Write-Host "   Login failed! Cannot proceed with email testing." -ForegroundColor Red
}

Write-Host ""
Write-Host "=== Email Test Complete ===" -ForegroundColor Green
Write-Host "Check your application logs for email sending status." -ForegroundColor Yellow
Write-Host "If emails are still failing, check:" -ForegroundColor Yellow
Write-Host "1. Elastic Email configuration" -ForegroundColor Yellow
Write-Host "2. Authorized sender email addresses" -ForegroundColor Yellow
Write-Host "3. SMTP credentials" -ForegroundColor Yellow 