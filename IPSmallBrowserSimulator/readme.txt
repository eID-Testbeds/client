
Build:
- add build.xml to Eclipse Ant-View
- execute ant-file
- NOTE: build at least once to generate required library 'ipsmalljava.jar'

Installation:
- copy (all in one folder):
	- browsersimulator.jar
	- run_windows.bat
- extract browsersimulator.jar contents to same folder (but keep .jar)



Configuration BrowserSimulator-machine:
- either edit run_windows.bat to pass own IP as argument
- or place an entry with your IP in your hosts file for ausweisapp-browsersimulator.secunet.de
Note: latter one is most easiest during development!



Configuration testbed-machine:
similar to client-config: though browsersimulator-config is read from common/config.properties...
		browsersimulator.rmi.host=ausweisapp-browsersimulator.secunet.de
		browsersimulator.rmi.port=1099
... its value is currently set to ausweisapp-browsersimulator.secunet.de => easiest for development with different IP addresses per developer
=> edit hosts file on testbed-machine too (to same address as above, BrowserSimulator-machine-IP).


Start:
BrowserSimulator-machine:
run_windows.bat
testbed-machine:
IPSmallManager, select testcase of type 'browsersimulator'






