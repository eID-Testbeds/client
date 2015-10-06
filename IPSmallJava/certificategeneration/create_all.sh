#!/bin/bash

#clean up
rm -r TR03124-2-SSL-CA
rm -r tmp


rm -r certs
mkdir certs

./create_X509_CA.sh
./create_X509_Certificates.sh
