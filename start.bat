@echo off
echo ========================================
echo   CHAY UNG DUNG SPRING BOOT
echo ========================================
echo.
echo Dang khoi dong ung dung...
echo Vui long doi 1-2 phut de Maven tai dependencies
echo.
cd /d "%~dp0"
echo Opening browser in 10 seconds...
start "" cmd /c "timeout /t 10 & start http://localhost:8080/products"
call .\mvnw.cmd spring-boot:run
pause
