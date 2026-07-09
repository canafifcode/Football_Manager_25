@echo off
rem Starts the transfer-market server (port 7564). Run this FIRST and keep it open.
set "JDK=C:\Users\acer\.vscode\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64"
cd /d "%~dp0"
"%JDK%\bin\java.exe" --module-path "lib\javafx;out" -m com.example.fm25/com.example.fm25.Server.Server
