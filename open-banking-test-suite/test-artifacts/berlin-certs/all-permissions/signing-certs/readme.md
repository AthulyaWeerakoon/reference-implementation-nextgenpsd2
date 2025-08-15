###### Generate Signing certs.

1. Follow the step under "Generating test PSD2 eIDAS certificates"(if not generated) and "Generating Qualified e-Seal Certificate (QSeal)
" in doc https://docs.google.com/document/d/1wgDb256NFyssInhA2oGWESOCto69MqUqIGGAT1JXkjo/edit?usp=sharing. 

- This step will generate qwac.pem and qwac.key files.
2. Generate p12 using the generated PEM and KEY.
	- `openssl pkcs12 -export -out signing.p12 -in qseal.pem -inkey qseal.key`
3. Generate jks file.
	- `keytool -v -importkeystore -srckeystore signing.p12  -srcstoretype PKCS12 -destkeystore signing.jks 
   -deststoretype JKS`
4.  Change the alias to "tpp1-signing"
	- `keytool -changealias -keystore signing.jks -alias 1`
