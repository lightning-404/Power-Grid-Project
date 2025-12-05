@echo off
cd /d "%~dp0"
javac -d bin src\powergrid\*.java src\powergrid\**\*.java
java -cp bin powergrid.Main
pause