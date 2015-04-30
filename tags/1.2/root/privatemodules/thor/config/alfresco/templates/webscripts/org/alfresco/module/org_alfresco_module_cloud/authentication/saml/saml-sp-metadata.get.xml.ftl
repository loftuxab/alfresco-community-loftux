<md:EntityDescriptor entityID="${spEntityID}" xmlns:md="urn:oasis:names:tc:SAML:2.0:metadata"> 
<md:SPSSODescriptor protocolSupportEnumeration="urn:oasis:names:tc:SAML:2.0:protocol"> 
<md:KeyDescriptor use="signing"> 
<ds:KeyInfo xmlns:ds="http://www.w3.org/2000/09/xmldsig#"> 
<ds:X509Data><ds:X509Certificate> 
${cert}
</ds:X509Certificate> 
</ds:X509Data> 
</ds:KeyInfo> 
</md:KeyDescriptor> 
<md:SingleLogoutService Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="${spSloRequestUrl}" ResponseLocation="${spSloResponseUrl}"/>
<md:AssertionConsumerService isDefault="true" Binding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST" Location="${spSsoUrl}" index="0"/> 
<md:AttributeConsumingService index="0"> 
<md:ServiceName xml:lang="en"> 
AttributeContract 
</md:ServiceName> 
<md:RequestedAttribute Name="Email"/> 
</md:AttributeConsumingService> 
</md:SPSSODescriptor> 
<md:ContactPerson contactType="administrative"> 
<md:Company>Alfresco</md:Company> 
<md:EmailAddress>support@alfresco.com</md:EmailAddress> 
</md:ContactPerson> 
</md:EntityDescriptor> 
