#!/bin/sh
# Compila e abre a agenda com interface grafica (Linux / macOS). Uso: ./run-gui.sh
cd "$(dirname "$0")" || exit 1
mkdir -p out
javac -encoding UTF-8 -d out src/agenda/*.java || exit 1
java -cp out agenda.Gui
