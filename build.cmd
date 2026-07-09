@echo off
rem Compiles the project into the "out" folder using the JDK bundled with the
rem VS Code Java extension (no system JDK is installed on this machine).
set "JDK=C:\Users\acer\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64"
cd /d "%~dp0"

if exist out rmdir /s /q out
dir /s /b src\main\java\*.java > sources.tmp
"%JDK%\bin\javac.exe" --module-path lib\javafx -d out @sources.tmp
set ERR=%ERRORLEVEL%
del sources.tmp
if not %ERR%==0 (
    echo BUILD FAILED
    exit /b %ERR%
)
xcopy /e /y /q src\main\resources\* out\ >nul
echo Build OK
