@echo off
REM Compila e abre a agenda com interface grafica (Windows). Uso: run-gui.bat
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out src\agenda\*.java
if errorlevel 1 exit /b 1
java -cp out agenda.Gui
