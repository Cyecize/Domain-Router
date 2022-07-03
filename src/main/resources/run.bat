:: Use this file to run the app
@echo off

set libraries=.
cd lib

:: for loop iterating all files in the bin folder and concatenating a string to pass to java
SETLOCAL ENABLEDELAYEDEXPANSION
for %%i in (*) do (
	set libraries=!libraries!;lib/%%i
)

cd ../

java -cp "%libraries%" com.cyecize.domainrouter.AppStartUp
PAUSE
