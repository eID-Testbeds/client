CV Naming Conventions
=====================

Name schema for certificate holder reference:
	CVCA:			DECVCAeIDCT{nnnnn} where {nnnnn} = consecutive number (beginning with 00001 for root CVCA).
				DECVCAfor{nnnnn} unknown (from pki view) CVCA certificate. {nnnnn} like above.
				
	CVCA (LINK):		DECVCAeIDCTL0{s}0{l} where {s} = set number and {l} letter, both from specification.
			
	DV:			DEDVeIDCT00{s}0{l} where {s} = set number and {l} letter, both from specification.
	
	AT (TERM):		DEATeIDCT00{s}0{l} where {s} = set number and {l} letter, both from specification.
	
Name schema for file names:
	All certificate files should have the same name as given in specification. If certificate is not named in specification (like root CVCA) its named by its holder reference.
	Keys and descriptions have the same file name as the respective certificate. 
	
	Extensions:
		CV certificate:				*.cvcert
		Private key:				*_KEY.pkcs8
		Public key:				*_KEY.bin
		Certificate description:		*_DESC.bin


X.509 Naming Conventions
========================		

Name schema for file names:
	All certificate files should have the same name as given in specification. Corresponding keys are stored at IPSmallJava\certificategeneration\certs, too.
	The name of the keys is identical to the certificates with "_KEY" appended.
	
	Extensions:
		CA:					TR03124-2-TLS-CA-*.crt
		CA Private key:				TR03124-2-TLS-CA-*_KEY.pem

		Other TLS certificate:			*.der
		Other private key:			*_KEY.der