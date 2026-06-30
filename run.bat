@echo off
echo Compiling source code...
javac -cp "lib/*;bin" -d bin src/Exceptions/*.java src/Bank/*.java src/Data/*.java src/GUI/*.java src/Application.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Running Banking System...
java -cp "bin;lib/*" Application
pause
