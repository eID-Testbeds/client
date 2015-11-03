@echo off
call CardSim-settings.cmd
java -jar GTCardSimCtrl.jar -w %GT_WORKSPACE% -is "GT Scripts ePA EAC2 Reader BSI/testsuites/Data/CFG.CERTS.TA/CFG.DFLT.EAC.IS" -at "GT Scripts ePA EAC2 Reader BSI/testsuites/Data/CFG.CERTS.TA/CFG.DFLT.EAC.AT" -p %CERT_DIR%\\CERT_CV_CVCA_4_3.cvcert %DATE%
pause