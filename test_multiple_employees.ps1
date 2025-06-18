# Test script for multiple employees
# This script demonstrates how to work with multiple employees in your attendance system

Write-Host "=== Saminavi Attendance System - Multiple Employee Test ===" -ForegroundColor Green
Write-Host ""

# Base URL for your application
$baseUrl = "http://localhost:9080"

# Employee credentials
$employees = @(
    @{
        username = "ajay.g"
        password = "password123"
        name = "Ajay G"
        employeeId = "EMP004"
    },
    @{
        username = "jane.smith"
        password = "password123"
        name = "Jane Smith"
        employeeId = "EMP005"
    }
)

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
        return $null
    }
}

# Test 1: Get all employees (no authentication required)
Write-Host "1. Getting all employees..." -ForegroundColor Yellow
try {
    $allEmployees = Invoke-RestMethod -Uri "$baseUrl/api/employees/all" -Method GET
    Write-Host "Total employees: $($allEmployees.Count)" -ForegroundColor Green
    foreach ($emp in $allEmployees) {
        Write-Host "  - $($emp.name) ($($emp.employeeId)) - $($emp.email)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "Failed to get all employees" -ForegroundColor Red
}
Write-Host ""

# Test 2: Get employee count
Write-Host "2. Getting employee count..." -ForegroundColor Yellow
try {
    $count = Invoke-RestMethod -Uri "$baseUrl/api/employees/count" -Method GET
    Write-Host "Employee count: $count" -ForegroundColor Green
}
catch {
    Write-Host "Failed to get employee count" -ForegroundColor Red
}
Write-Host ""

# Test 3: Test each employee individually
foreach ($emp in $employees) {
    Write-Host "=== Testing Employee: $($emp.name) ===" -ForegroundColor Magenta
    
    # Login
    Write-Host "  Logging in..." -ForegroundColor Yellow
    $token = Get-JwtToken -username $emp.username -password $emp.password
    
    if ($token) {
        Write-Host "  Login successful!" -ForegroundColor Green
        
        # Get profile
        Write-Host "  Getting profile..." -ForegroundColor Yellow
        $profile = Invoke-AuthenticatedRequest -url "$baseUrl/api/employees/profile" -method "GET" -token $token
        if ($profile) {
            Write-Host "  Profile: $($profile.name) - $($profile.email)" -ForegroundColor Cyan
        }
        
        # Clock in
        Write-Host "  Clocking in..." -ForegroundColor Yellow
        $clockInResult = Invoke-AuthenticatedRequest -url "$baseUrl/api/attendance/clock-in" -method "POST" -token $token
        if ($clockInResult) {
            Write-Host "  Clock in successful!" -ForegroundColor Green
        }
        
        # Get today's attendance
        Write-Host "  Getting today's attendance..." -ForegroundColor Yellow
        $todayAttendance = Invoke-AuthenticatedRequest -url "$baseUrl/api/attendance/today" -method "GET" -token $token
        if ($todayAttendance) {
            Write-Host "  Today's attendance: Clock in at $($todayAttendance.clockInTime)" -ForegroundColor Cyan
        }
        
        # Get attendance history
        Write-Host "  Getting attendance history..." -ForegroundColor Yellow
        $history = Invoke-AuthenticatedRequest -url "$baseUrl/api/attendance/history" -method "GET" -token $token
        if ($history) {
            Write-Host "  Attendance history: $($history.Count) records" -ForegroundColor Cyan
        }
        
    } else {
        Write-Host "  Login failed!" -ForegroundColor Red
    }
    
    Write-Host ""
}

# Test 4: Get all today's attendance (management view)
Write-Host "4. Getting all today's attendance (management view)..." -ForegroundColor Yellow
try {
    $allTodayAttendance = Invoke-RestMethod -Uri "$baseUrl/api/attendance/all-today" -Method GET
    Write-Host "Today's attendance records: $($allTodayAttendance.Count)" -ForegroundColor Green
    foreach ($att in $allTodayAttendance) {
        Write-Host "  - $($att.employee.name): Clock in at $($att.clockInTime)" -ForegroundColor Cyan
    }
}
catch {
    Write-Host "Failed to get all today's attendance" -ForegroundColor Red
}
Write-Host ""

# Test 5: Get specific employee by ID
Write-Host "5. Getting specific employee by ID..." -ForegroundColor Yellow
foreach ($emp in $employees) {
    try {
        $specificEmp = Invoke-RestMethod -Uri "$baseUrl/api/employees/$($emp.employeeId)" -Method GET
        Write-Host "  $($specificEmp.name) ($($specificEmp.employeeId)): $($specificEmp.email)" -ForegroundColor Cyan
    }
    catch {
        Write-Host "  Failed to get employee $($emp.employeeId)" -ForegroundColor Red
    }
}
Write-Host ""

Write-Host "=== Test Complete ===" -ForegroundColor Green
Write-Host "Check your email for notifications from both employees!" -ForegroundColor Yellow 