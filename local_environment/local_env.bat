@echo off

SET SCRIPT_NAME=%~n0%~x0
SET PREV_DIR=%cd%
SET SCRIPT_DIR=%~dp0

REM Create or empty startup.log
echo > %SCRIPT_DIR%\startup.log

set BA_VERSION=

:parameter_loop
if not "%1"=="" (
    if "%1"=="start" (
        shift
        call :setup %*
    ) else if "%1"=="stop" (
        call :stop
    ) else if "%1"=="status" (
        echo status
    ) else (
        call :help
        goto :eof
    )

) else (
    call :help
    goto :eof
)
goto :eof

:help
    echo Usage: %SCRIPT_NAME% ^<command^> ^<options^>
    echo Commands:
    echo start                               Start the local environment
    echo   -ba ^<ba-version^>                  BA version. Required.
    echo stop                                Stop the local environment
    echo status                              Print the local environment status
    echo
exit /b

:stop
    echo Stopping environment ...
    chdir %SCRIPT_DIR%
    docker-compose rm -sf
    chdir %PREV_DIR%
exit /b

:setup
    break > %SCRIPT_DIR%\startup.log
    echo Setting up local environment ...
    call :setup_loop %*
    rem call :update_images
    call :start_environment
exit /b

:setup_loop
if not "%1"=="" (
    if "%1"=="-ba" (
        if "%2%"=="" (
            echo Error^! BA version NOT specified ^(missing -ba parameter^)^!
            call :help
            goto :eof
        )
        SET BA_VERSION=%2
        shift
    )
    shift
    goto :setup_loop
)
exit /b

:update_images
    docker pull registry.docker.hcom/hotels/checkito:latest >> %SCRIPT_DIR%\startup.log 2>&1
    echo Pulling checkito ...
    if errorlevel 1 (
        echo Login to Docker
        docker login registry.docker.hcom
        docker pull registry.docker.hcom/hotels/checkito:latest >> %SCRIPT_DIR%\startup.log 2>&1
    )
    echo Pulling styxpres ...
    docker pull registry.docker.hcom/hotels/styxpres:release >> %SCRIPT_DIR%\startup.log 2>&1
    echo Pulling cws ...
    docker pull registry.docker.hcom/hotels/cws:latest >> %SCRIPT_DIR%\startup.log 2>&1

    echo done
exit /b

:start_environment
    echo Starting environment in new window...
    chdir %SCRIPT_DIR%
    start "Running local environment|BA %BA_VERSION% ..." "docker-compose" up --no-color >> %SCRIPT_DIR%\startup.log
    chdir %PREV_DIR%
exit /b
