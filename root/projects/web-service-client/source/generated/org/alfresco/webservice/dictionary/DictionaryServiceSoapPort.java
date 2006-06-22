/**
 * DictionaryServiceSoapPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.dictionary;

public interface DictionaryServiceSoapPort extends java.rmi.Remote {

    /**
     * Retrieves the class definitions of types and aspects.
     */
    public org.alfresco.webservice.types.ClassDefinition[] getClasses(org.alfresco.webservice.dictionary.ClassPredicate types, org.alfresco.webservice.dictionary.ClassPredicate aspects) throws java.rmi.RemoteException, org.alfresco.webservice.dictionary.DictionaryFault;
}
