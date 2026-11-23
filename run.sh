#!/bin/sh
# Compila e executa a agenda (Linux / macOS). Uso: ./run.sh
cd "$(dirname "$0")" || exit 1
mkdir -p out
javac -encoding UTF-8 -d out src/agenda/*.java || exit 1
java -cp out agenda.Main
