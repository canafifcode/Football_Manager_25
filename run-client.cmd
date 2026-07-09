@echo off
rem Starts the game client (JavaFX GUI). Start the server first, then run this
rem once per user - e.g. two windows signed in as different managers.
set "JDK=C:\Users\acer\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64"
cd /d "%~dp0"
start "FM25 Client" "%JDK%\bin\java.exe" --module-path "lib\javafx;out" -m com.example.fm25/com.example.fm25.controller.Football_Manager
