Alfresco Spring Properties Encryption using Jasypt and public/private keys

This project builds an executable jar utility to:
  a. Create the public and private key
  b. Generate the encrypted password
  c. Validate that the encrypted password and password match
  
  
To use this package you must
1. Copy the generated executable JAR file into your <Tomcat>bin folder
2. Generate the public and private keys with the following command
   java -jar bin/<executable jar file name>.jar initkey <extension directory>
   this will install the keys into <extension directory>alfresco/extension/enterprise alfSpringKey and alfSpringKey.pub
3. Protect the private key (alfrescoSpringKey) using chmod 600 or move it elsewhere such as a disk in a safe.
4. Generate the encrypted key using the following command
   java -jar bin/<executable jar file name>.jar encrypt <extension directory> [password]
5. add the password to alfresco-encrypted.properties (alongside alfresco-global.properties) in the following format
   db.password.enc=ENC(<enter encrypted password here>)
6. Be sure to comment out #db.password in the alfresco-global.properties
   and replace it with ${db.password.enc} instead.