@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  bility startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and BILITY_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\bility-0.0.1.jar;%APP_HOME%\lib\ktor-server-netty-0.9.3.jar;%APP_HOME%\lib\ktor-html-builder-0.9.3.jar;%APP_HOME%\lib\ktor-server-host-common-0.9.3.jar;%APP_HOME%\lib\ktor-metrics-0.9.3.jar;%APP_HOME%\lib\ktor-server-sessions-0.9.3.jar;%APP_HOME%\lib\ktor-websockets-0.9.3.jar;%APP_HOME%\lib\ktor-auth-0.9.3.jar;%APP_HOME%\lib\ktor-jackson-0.9.3.jar;%APP_HOME%\lib\ktor-freemarker-0.9.3.jar;%APP_HOME%\lib\ktor-server-core-0.9.3.jar;%APP_HOME%\lib\kotlin-css-jvm-1.0.0-pre.31-kotlin-1.2.41.jar;%APP_HOME%\lib\ktor-client-auth-basic-0.9.3.jar;%APP_HOME%\lib\ktor-client-json-0.9.3.jar;%APP_HOME%\lib\ktor-client-websocket-0.9.3.jar;%APP_HOME%\lib\ktor-client-core-0.9.3.jar;%APP_HOME%\lib\ktor-http-cio-0.9.3.jar;%APP_HOME%\lib\ktor-network-0.9.3.jar;%APP_HOME%\lib\ktor-http-0.9.3.jar;%APP_HOME%\lib\ktor-utils-0.9.3.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.2.51.jar;%APP_HOME%\lib\logback-classic-1.2.1.jar;%APP_HOME%\lib\kmongo-3.8.0.jar;%APP_HOME%\lib\kmongo-coroutine-3.8.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.2.51.jar;%APP_HOME%\lib\kmongo-jackson-mapping-3.8.0.jar;%APP_HOME%\lib\kmongo-id-jackson-3.8.0.jar;%APP_HOME%\lib\jackson-module-kotlin-2.9.3.jar;%APP_HOME%\lib\kmongo-core-3.8.0.jar;%APP_HOME%\lib\kmongo-coroutine-core-3.8.0.jar;%APP_HOME%\lib\kmongo-async-shared-3.8.0.jar;%APP_HOME%\lib\kmongo-property-3.8.0.jar;%APP_HOME%\lib\kmongo-shared-3.8.0.jar;%APP_HOME%\lib\kmongo-id-3.8.0.jar;%APP_HOME%\lib\kotlin-reflect-1.2.50.jar;%APP_HOME%\lib\kotlinx-coroutines-jdk8-0.23.3.jar;%APP_HOME%\lib\kotlinx-io-jvm-0.0.10.jar;%APP_HOME%\lib\kotlinx-html-jvm-0.6.11.jar;%APP_HOME%\lib\kmongo-jackson-modules-service-loader-3.8.0.jar;%APP_HOME%\lib\kotlinx-coroutines-io-0.23.3.jar;%APP_HOME%\lib\kotlinx-coroutines-core-0.23.4.jar;%APP_HOME%\lib\kmongo-data-3.8.0.jar;%APP_HOME%\lib\kotlin-stdlib-1.2.51.jar;%APP_HOME%\lib\metrics-jvm-3.2.4.jar;%APP_HOME%\lib\metrics-core-3.2.4.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\config-1.3.1.jar;%APP_HOME%\lib\netty-codec-http2-4.1.24.Final.jar;%APP_HOME%\lib\alpn-api-1.1.3.v20160715.jar;%APP_HOME%\lib\logback-core-1.2.1.jar;%APP_HOME%\lib\kotlin-css-1.0.0-pre.31-kotlin-1.2.41.jar;%APP_HOME%\lib\json-simple-1.1.1.jar;%APP_HOME%\lib\gson-2.8.1.jar;%APP_HOME%\lib\freemarker-2.3.28.jar;%APP_HOME%\lib\kotlinx-coroutines-core-common-0.23.4.jar;%APP_HOME%\lib\atomicfu-common-0.10.3.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.2.51.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\netty-codec-http-4.1.24.Final.jar;%APP_HOME%\lib\netty-handler-4.1.24.Final.jar;%APP_HOME%\lib\junit-4.10.jar;%APP_HOME%\lib\jackson-databind-2.9.3.jar;%APP_HOME%\lib\jackson-annotations-2.9.0.jar;%APP_HOME%\lib\mongodb-driver-3.8.0.jar;%APP_HOME%\lib\bson4jackson-2.9.2.jar;%APP_HOME%\lib\netty-codec-4.1.24.Final.jar;%APP_HOME%\lib\netty-transport-4.1.24.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.24.Final.jar;%APP_HOME%\lib\hamcrest-core-1.1.jar;%APP_HOME%\lib\mongodb-driver-async-3.8.0.jar;%APP_HOME%\lib\mongodb-driver-core-3.8.0.jar;%APP_HOME%\lib\bson-3.8.0.jar;%APP_HOME%\lib\jackson-core-2.9.3.jar;%APP_HOME%\lib\netty-resolver-4.1.24.Final.jar;%APP_HOME%\lib\netty-common-4.1.24.Final.jar

@rem Execute bility
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %BILITY_OPTS%  -classpath "%CLASSPATH%" io.ktor.server.netty.DevelopmentEngine %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable BILITY_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%BILITY_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
