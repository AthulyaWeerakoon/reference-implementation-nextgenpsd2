Generate Transport certs.

1. Follow the step under "Generating test PSD2 eIDAS certificates"(if not generated) and "Generating Qualified Website Authentication Certificate (QWAC)" in doc https://docs.google.com/document/d/1wgDb256NFyssInhA2oGWESOCto69MqUqIGGAT1JXkjo/edit?usp=sharing. This step will generate qwac.pem and qwac.key files.
2. Generate p12 using the generated PEM and KEY.
	openssl pkcs12 -export -out transport.p12 -in qwac.pem -inkey qwac.key
3. Generate jks file.
	keytool -v -importkeystore -srckeystore transport.p12  -srcstoretype PKCS12 -destkeystore transport.jks -deststoretype JKS
4.  Change the alias to tpp1-transport
	keytool -changealias -keystore transport.jks -alias 1
