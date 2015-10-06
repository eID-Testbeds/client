CV-Certificate creation
=======================

Build:
	Run Ant script (build_CVCertGenCLI.xml). This builds the certificate generator CVCertGenCLI.jar and copies XSD schema for validation of certificate configuration to build directory.

Run:
	Start CVCertGenCLI.jar with "java -jar CVCertGenCLI.jar -?" for detailed instructions.
	
	"-d" parameter sets a reference date for certificate generation. If this parameter is not set, current date will be used.
	
	CVCertGenCLI.jar needs a XSD schema for validation of certificate configuration with filename "cvcert.xsd" in same directory.
	
Run Scripts:
	There are several run scripts for certification generation. Certificates (and keys and description files) are stored in "./certs". For use in test cases they have to be copied to respective folder.
	
	Just execute them to generate certificate sets.
	
	WARNING: If certificates (and keys or description files) already exists, they will be overwritten! So only execute root CVCA generation if your really want to generate a new one!!!
	
	Do only use certificate chains if all certificates were build successfully!
	
Configuration of certificates in XML files:
	To generate new keys use generate="true" flag in <key> configuration. To use existing keys set flag to false or leave out.
	For generating CVCA certificates use domainParam="true" in publicKey element in <cert> configuration to store domain parameters in certificate. By default no domain parameters are stored.
	If date is not given by a fix value, the relative date of effDateOffset relates to relative date from CVCertGenCLI (see -d parameter). expDateOffset relates to effDate.
	
	For further configuration details see example configuration or XSD schema.
	
Name schema for certificate holder reference:
	CVCA:			DECVCAeIDCT{nnnnn} where {nnnnn} = consecutive number (beginning with 00001 for root CVCA).
					DECVCAfor{nnnnn} unknown (from pki view) CVCA certificate. {nnnnn} like above.
				
	CVCA (LINK):	DECVCAeIDCTL0{s}0{l} where {s} = set number and {l} letter, both from specification.
			
	DV:				DEDVeIDCT00{s}0{l} where {s} = set number and {l} letter, both from specification.
	
	AT (TERM):		DEATeIDCT00{s}0{l} where {s} = set number and {l} letter, both from specification.
	
Name schema for file names:
	All certificate files should have the same name as given in specification. If certificate is not named in specification (like root CVCA) its named by its holder reference.
	Keys and descriptions have the same file name as the respective certificate. 
	
	Extensions:
		CV certificate:				*.cvcert
		Private key:				*_KEY.pkcs8
		(Public key:					*_KEY.bin)
		Certificate description:	*_DESC.bin
	
	
	
X509 certificate creation
=========================	

Preconditions:
--------------
	- cygwin with openssl installation available

	
1) Creation of CA (including CA-certificate)

Run Script:
	- script is available in perforce at //BSI/eCardAPIConf/develop/main/IPSmallJava/certificategeneration/create_X509_CA.sh
	- make sure that line endings are UNIX style e.g. by calling "dos2unix create_X509_CA.sh"
	- execute script
	
	
2) Creation of SSL/TLS certificates

Run Script:
	- script is available in perforce at //BSI/eCardAPIConf/develop/main/IPSmallJava/certificategeneration/create_X509_Certificates.sh
	- make sure that line endings are UNIX style e.g. by calling "dos2unix create_X509_Certificates.sh"
	- execute script

TLS Certificates are automatically generated (without user interaction) and stored at IPSmallJava\certificategeneration\certs

Name schema for file names:
	All certificate files should have the same name as given in specification. Corresponding keys are stored at IPSmallJava\certificategeneration\certs, too.
	The name of the keys is identical to the certificates with "_KEY" appended.
	
	Extensions:
		TLS certificate:			*.der
		Private key:				*.der
		CRL:						*.crl
	
