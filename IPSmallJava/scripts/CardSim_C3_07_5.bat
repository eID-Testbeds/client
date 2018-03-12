@echo off
call CardSim-settings.cmd
java -jar GTCardSimCtrl.jar -w %GT_WORKSPACE% -is "GT Scripts BSI TR03105 Part 5.2/testsuites/Data/CFG.CERTS.TA/CFG.DFLT.EAC.IS" -at "GT Scripts BSI TR03105 Part 5.2/testsuites/Data/CFG.CERTS.TA/CFG.DFLT.EAC.AT" -p %CERT_DIR%\\CERT_CV_CVCA_4_5.cvcert %DATE%
pause