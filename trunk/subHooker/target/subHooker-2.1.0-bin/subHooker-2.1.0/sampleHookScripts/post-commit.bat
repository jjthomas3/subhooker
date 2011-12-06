@ECHO OFF
SETLOCAL
CD "%~dp0\subHooker-1.0.2\bin"
call pimp.bat post %1 %2 users
Exit %ERRORLEVEL%