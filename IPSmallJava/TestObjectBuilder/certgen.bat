@echo off
echo *** Certificate generation script ***
cd %1
call settings.cmd
cd %2
set PATH=%CYGWINDIR%;%PATH%
dos2unix.exe create_all.sh
bash.exe create_all.sh %3 2>&1
exit