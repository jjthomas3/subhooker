@ECHO OFF
SETLOCAL
@CD %~dp0
CALL %~dp0\subHooker-1.0.2\bin\pimp.bat pre %1 %2 informatics
EXIT %ERRORLEVEL%