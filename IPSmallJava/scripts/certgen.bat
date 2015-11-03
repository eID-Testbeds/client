@echo off
set PATH=C:\cygwin64\bin;%PATH%
cd certificategeneration
bash.exe ./create_all.sh 2014-04-07
bash.exe ./copy_all.sh

pause