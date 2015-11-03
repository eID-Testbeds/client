@echo off
set logfile=debug_console.log
echo ------------------------------------------ >> %logfile%
echo IPSmallJava Debug Output: %date% %time% >> %logfile%
java -Djavax.net.debug=all -jar IPSmallJava.jar >> %logfile%