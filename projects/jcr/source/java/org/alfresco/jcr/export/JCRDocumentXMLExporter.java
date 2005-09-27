package org.alfresco.jcr.export;

import java.io.InputStream;
import java.util.Collection;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.view.Exporter;
import org.alfresco.service.namespace.QName;

public class JCRDocumentXMLExporter implements Exporter
{

    public void start()
    {
        // TODO Auto-generated method stub
        
    }

    public void startNamespace(String prefix, String uri)
    {
        // TODO Auto-generated method stub
        
    }

    public void endNamespace(String prefix)
    {
        // TODO Auto-generated method stub
        
    }

    public void startNode(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        
    }

    public void endNode(NodeRef nodeRef)
    {
        // TODO Auto-generated method stub
        
    }

    public void startAspect(NodeRef nodeRef, QName aspect)
    {
        // TODO Auto-generated method stub
        
    }

    public void endAspect(NodeRef nodeRef, QName aspect)
    {
        // TODO Auto-generated method stub
        
    }

    public void startProperty(NodeRef nodeRef, QName property)
    {
        // TODO Auto-generated method stub
        
    }

    public void endProperty(NodeRef nodeRef, QName property)
    {
        // TODO Auto-generated method stub
        
    }

    public void value(NodeRef nodeRef, QName property, Object value)
    {
        // TODO Auto-generated method stub
        
    }

    public void value(NodeRef nodeRef, QName property, Collection values)
    {
        // TODO Auto-generated method stub
        
    }

    public void content(NodeRef nodeRef, QName property, InputStream content)
    {
        // TODO Auto-generated method stub
        
    }

    public void startAssoc(NodeRef nodeRef, QName assoc)
    {
        // TODO Auto-generated method stub
        
    }

    public void endAssoc(NodeRef nodeRef, QName assoc)
    {
        // TODO Auto-generated method stub
        
    }

    public void warning(String warning)
    {
        // TODO Auto-generated method stub
        
    }

    public void end()
    {
        // TODO Auto-generated method stub
        
    }

}
