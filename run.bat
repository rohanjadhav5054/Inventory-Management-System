@echo off
REM Compile all Java files
echo Compiling Java source files...
javac -cp "lib\mysql-connector-j-8.3.0.jar;src" -d bin src\**\*.java

IF %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

REM Run the main class
echo Running the Inventory Management System...
java -cp "bin;lib\mysql-connector-j-8.3.0.jar" Main

pause