@echo off
setlocal enabledelayedexpansion 

pushd %~dp0
call :load_branches

java %branches% -Xss2m -Xms2g -Xmx2g -jar project\strap\gruj_vs_sbt-launch-0.13.x.jar %*

popd 
endlocal
goto :EOF

:push_descend
pushd "%OLD_DIR%.."
if %CD%\==%OLD_DIR% popd & goto :EOF
call :load_branches
popd
goto :EOF

:load_branches
set OLD_DIR=%CD%\
call :push_descend
for %%a in ("%CD%\*.branch") do (
  set /p current=<"%%~fa"
  echo Found %%~fa := !current!
  set branches=!branches! -D%%~nxa="!current!"
) 
goto :EOF
