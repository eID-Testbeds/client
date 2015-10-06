#!/bin/bash

CAName=TR03124-2-SSL-CA

sslca_dir=./SSLCA
certs_dir=./$CAName/certs

# rm -rf ./tmp/*
# rm -rf ./$CAName

mkdir ./$CAName
mkdir ./$CAName/certs
mkdir ./$CAName/crl
mkdir ./$CAName/newcerts
mkdir ./tmp
touch ./$CAName/index.txt
echo '0001' > ./$CAName/serial

if [ ! -f $sslca_dir/$CAName.crt ]
then
	echo "Generating SSL CA key ..."
	openssl genrsa -passout pass:1234 -des3 -out $certs_dir/cakey.pem 4096
	
	echo "Generating SSL CA certificate ..."
	openssl req -batch -passin pass:1234 -config openssl_ca.cnf -new -x509 -days 6000 -key $certs_dir/cakey.pem -out $certs_dir/cacert.pem
	
	echo "Copying root SSl CA certificate ..."
	if [ ! -d $sslca_dir ]
	then
		mkdir $sslca_dir
	fi
	cp $certs_dir/cakey.pem $sslca_dir/$CAName.key.pem
	cp $certs_dir/cacert.pem $sslca_dir/$CAName.crt
else
	echo "Copying root SSl CA certificate ..."

	cp $sslca_dir/$CAName.key.pem $certs_dir/cakey.pem
	cp $sslca_dir/$CAName.crt $certs_dir/cacert.pem
fi
