call mvn clean install
if ERRORLEVEL 1 goto error
call mvn clean install -f samples/pom.xml
if ERRORLEVEL 1 goto error
git status
git push origin master
exit

:error
    set ERRORLEVEL=1
    echo.
    echo **** Error  ****
    echo.   
    exit /b 1
:end
