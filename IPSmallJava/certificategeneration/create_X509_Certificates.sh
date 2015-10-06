#!/bin/bash

rm ./tmp/*

# generate CERT_TLS_ESERVICE_1
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_1.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_1_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_1.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_1_NSOP
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_1_NSOP.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_1_NSOP_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_1_NSOP.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_1_NSOP.der -outform DER

rm ./tmp/*

# generate CERT_TLS_TCTOKENPROVIDER_1_NSOP
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_TCTOKENPROVIDER_1_NSOP.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_TCTOKENPROVIDER_1_NSOP_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_TCTOKENPROVIDER_1_NSOP.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_TCTOKENPROVIDER_1_NSOP.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_1
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_1.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_1_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_1.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_REDIRECT_1_A
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_REDIRECT_1_A.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_REDIRECT_1_A_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_REDIRECT_1_A.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_REDIRECT_1_A.der -outform DER

rm ./tmp/*

# generate CERT_TLS_REDIRECT_1_B
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_REDIRECT_1_B.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_REDIRECT_1_B_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_REDIRECT_1_B.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_REDIRECT_1_B.der -outform DER

rm ./tmp/*

# generate CERT_TLS_REDIRECT_1_C
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_REDIRECT_1_C.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_REDIRECT_1_C_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_REDIRECT_1_C.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_REDIRECT_1_C.der -outform DER

rm ./tmp/*

# generate CERT_TLS_REDIRECT_1_D
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_REDIRECT_1_D.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_REDIRECT_1_D_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_REDIRECT_1_D.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_REDIRECT_1_D.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_2
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_2.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_2_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_2.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_2.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_2
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_2.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_2_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_2.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_2.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_A
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_A.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_A_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_A.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_A.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_B
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_B.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_B_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_B.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_B.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_C
openssl ecparam -genkey -name secp192r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_C.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_C_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_C.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_C.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_D
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_D.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_D_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_D.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_D.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_E
#openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_E.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
#openssl rsa -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_E_KEY.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_E.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
#openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_E.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_E.cnf -revoke ./tmp/newcert.pem

#rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_F
#openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_F.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
#openssl rsa -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_F_KEY.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_F.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
#openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_F.der -outform DER

#rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_G
#openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_G.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
#openssl rsa -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_G_KEY.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_G.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
#openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_G.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_G.cnf -revoke ./tmp/newcert.pem

#rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_H
#openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_H.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
#openssl rsa -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_H_KEY.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_H.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
#openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_H.der -outform DER

#rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_I
#openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_I.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
#openssl rsa -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_I_KEY.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_I.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
#openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_I.der -outform DER
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_I.cnf -revoke ./tmp/newcert.pem
#openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_I.cnf -gencrl -out ./certs/myca.crl

#rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_J
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_J.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_J_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_J.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_J.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_K
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_K.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_K_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_K.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_K.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_L
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_L.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_L_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_L.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_L.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_L_RSA3072
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_L_RSA3072.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_L_KEY_RSA3072.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_L_RSA3072.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_L_RSA3072.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_L_RSA4096
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_L_RSA4096.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_L_KEY_RSA4096.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_L_RSA4096.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_L_RSA4096.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_M
openssl ecparam -genkey -name secp224r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_M.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_M_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_M.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_M.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_M_secp256r1
openssl ecparam -genkey -name secp256r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_M.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_M_KEY_secp256r1.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_M.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_M_secp256r1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_M_secp384r1
openssl ecparam -genkey -name secp384r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_M.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_M_KEY_secp384r1.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_M.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_M_secp384r1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_M_secp521r1
openssl ecparam -genkey -name secp521r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_M.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_M_KEY_secp521r1.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_M.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_M_secp521r1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_M_brainpoolP256r1
openssl ecparam -genkey -name brainpoolP256r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_M.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_M_KEY_brainpoolP256r1.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_M.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_M_brainpoolP256r1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_M_brainpoolP384r1
openssl ecparam -genkey -name brainpoolP384r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_M.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_M_KEY_brainpoolP384r1.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_M.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_M_brainpoolP384r1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_M_brainpoolP512r1
openssl ecparam -genkey -name brainpoolP512r1 -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_M.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_M_KEY_brainpoolP512r1.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_M.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_M_brainpoolP512r1.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_N
openssl dsaparam -out ./tmp/dsaparam.pem 2048
openssl gendsa ./tmp/dsaparam.pem -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_N.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_N_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_N.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_N.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_N_DSA3072
openssl dsaparam -out ./tmp/dsaparam.pem 3072
openssl gendsa ./tmp/dsaparam.pem -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_N.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_N_KEY_DSA3072.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_N.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_N_DSA3072.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_N_DSA4096
openssl dsaparam -out ./tmp/dsaparam.pem 3072
openssl gendsa ./tmp/dsaparam.pem -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_N.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_N_KEY_DSA4096.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_N.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_N_DSA4096.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_O
openssl dsaparam -out ./tmp/dsaparam.pem 1536
openssl gendsa ./tmp/dsaparam.pem -out ./tmp/newkey.pem
openssl req -batch -passout pass:1234 -new -config openssl_CERT_TLS_ESERVICE_3_O.cnf -key ./tmp/newkey.pem -keyform PEM -out ./tmp/certreq.pem
openssl pkcs8 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_O_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_O.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_O.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_3_A
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_3_P.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_3_P_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_3_P.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_3_P.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_3_A
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_A.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_3_A_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_A.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_3_A.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_3_A_RSA3072
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_A_RSA3072.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_3_A_KEY_RSA3072.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_A_RSA3072.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_3_A_RSA3072.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_3_A_RSA4096
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_A_RSA4096.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_3_A_KEY_RSA4096.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_A_RSA4096.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_3_A_RSA4096.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_3_B
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_B.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_3_B_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_B.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_3_B.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_3_C
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_C.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_3_C_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_3_C.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_3_C.der -outform DER

rm ./tmp/*

# generate CERT_TLS_ESERVICE_4_A
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_ESERVICE_4_A.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_ESERVICE_4_A_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_ESERVICE_4_A.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_ESERVICE_4_A.der -outform DER

rm ./tmp/*

# generate CERT_TLS_EIDSERVER_4_A
openssl req -batch -new -passout pass:1234 -config openssl_CERT_TLS_EIDSERVER_4_A.cnf -keyout ./tmp/newkey.pem -out ./tmp/certreq.pem
openssl pkcs8 -passin pass:1234 -in ./tmp/newkey.pem -inform PEM -topk8 -out ./certs/CERT_TLS_EIDSERVER_4_A_KEY.der -outform DER -nocrypt
openssl ca -batch -passin pass:1234 -config openssl_CERT_TLS_EIDSERVER_4_A.cnf -out ./tmp/newcert.pem -infiles ./tmp/certreq.pem
openssl x509 -in ./tmp/newcert.pem -inform PEM -out ./certs/CERT_TLS_EIDSERVER_4_A.der -outform DER

rm ./tmp/*