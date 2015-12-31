@echo off
setlocal

set PROJECT=auth-agent
set JAVA_VERSION="1.8"

set AUTH_AGENT_HOME=%~dp0..\
cd %AUTH_AGENT_HOME%

:validate
for /F %%v in ('echo %1^|findstr "^start$ ^stop$ ^restart$ ^status"') do set COMMAND=%%v
if "%COMMAND%" == "" (
    echo Usage: %0 { start : stop : restart : status }
    exit /b 1
)
call :%COMMAND%
goto :eof

:start
call :status
if not "%pid%" == "" (
    echo Start was canceled
    goto :eof
)
goto :findJava
goto :eof

:run
echo AUTH_AGENT_HOME=%cd%
start "%PROJECT%" "%JAVA_CMD%" -classpath conf;lib/%PROJECT%.jar org.springframework.boot.loader.JarLauncher
goto :eof

:stop
call :status
if not "%pid%" == "" (
    taskkill /pid %pid%
)
goto :eof

:status
set pid=
for /F "tokens=2 skip=2" %%i in ('tasklist /fi "imagename eq java*" /fi "windowtitle eq %PROJECT%"') do set pid=%%i
if "%pid%" == "" (
    echo %PROJECT% is NOT running
) else (
    echo %PROJECT% (pid %pid%^) is running...
)
goto :eof

:restart
call :stop
call :start
goto :eof

:findJava
set JAVA_CMD=
if not "%JAVA_HOME%" == "" if exist "%JAVA_HOME%\bin\java.exe" (
    echo Found java executable in JAVA_HOME
    set JAVA_CMD=%JAVA_HOME%\bin\java
    call :checkJava
)
if "%JAVA_CMD%" == "" if not "%JAVA%" == "" if exist "%JAVA%" (
    echo Found java executable by JAVA
    set JAVA_CMD=%JAVA%
    call :checkJava
)
if "%JAVA_CMD%" == "" (
    java -version >nul 2>&1 && (
        echo Found java executable in PATH
        set JAVA_CMD=java
        call :checkJava
    )
)
if "%JAVA_CMD%" == "" (
    echo Cannot find Java %JAVA_VERSION:"=%. Please set JAVA_HOME, JAVA executable or put java in your PATH.
    exit /b 1
)
goto :eof

:checkJava
if not "%JAVA_CMD%" == "" (
    echo JAVA_CMD=%JAVA_CMD%
    for /f "tokens=4" %%v in ('"%JAVA_CMD%" -fullversion 2^>^&1') do (
        if "%%v" GTR "%JAVA_VERSION%" (
            echo Java version %%v
            goto :run
        ) else (
            set JAVA_CMD=
            echo Java version %%v is less than required %JAVA_VERSION%
        )
    )
)
goto :eof