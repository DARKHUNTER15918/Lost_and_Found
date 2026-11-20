@echo off
setlocal
if defined JAVA_HOME goto checkMvn
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.21.10002-hotspot" (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.21.10002-hotspot"
    set "PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.21.10002-hotspot\bin;%PATH%"
)

:checkMvn
if defined M2_HOME (
    if exist "%M2_HOME%\bin\mvn.cmd" (
        "%M2_HOME%\bin\mvn.cmd" %*
        exit /b %ERRORLEVEL%
    )
)

if exist "C:\Users\LENOVO\AppData\Local\Programs\apache-maven-3.9.6\bin\mvn.cmd" (
    "C:\Users\LENOVO\AppData\Local\Programs\apache-maven-3.9.6\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)

mvn %*
exit /b %ERRORLEVEL%
