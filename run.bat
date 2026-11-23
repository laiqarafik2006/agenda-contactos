@echo off
REM Compila e executa a agenda (Windows). Uso: run.bat
chcp 65001 >nul
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out src\agenda\*.java
if errorlevel 1 exit /b 1
java -cp out agenda.Main
