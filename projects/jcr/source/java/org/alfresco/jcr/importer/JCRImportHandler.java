package org.alfresco.jcr.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alfresco.jcr.dictionary.JCRNamespace;
import org.alfresco.jcr.session.SessionImpl;
import org.alfresco.repo.importer.ImportContentHandler;
import org.alfresco.repo.importer.Importer;
import org.alfresco.service.cmr.view.ImporterException;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.NamespaceSupport;


public class JCRImportHandler implements ImportContentHandler
{
    private Importer importer;
    
    private SessionImpl session;
    private NamespaceContext namespaceContext;
    
    private ImportContentHandler targetHandler = null;
    
    
    
    public JCRImportHandler(SessionImpl session)
    {
        this.session = session;
        this.namespaceContext = new NamespaceContext();
    }
    
    
    public void setImporter(Importer importer)
    {
        this.importer = importer;
    }


    public InputStream importStream(String content)
    {
        return targetHandler.importStream(content);
    }

    public void setDocumentLocator(Locator locator)
    {
        // NOOP
    }

    public void startDocument() throws SAXException
    {
        namespaceContext.reset();
    }

    public void endDocument() throws SAXException
    {
        targetHandler.endDocument();
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException
    {
        // ensure uri has been registered
        NamespacePrefixResolver resolver = session.getNamespaceResolver();
        Collection<String> uris = resolver.getURIs();
        if (!uris.contains(uri))
        {
            throw new ImporterException("Namespace URI " + uri + " has not been registered with the repository");
        }
        
        // register prefix within this namespace context
        namespaceContext.registerPrefix(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException
    {
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
    {
        namespaceContext.pushContext();

        // determine content handler based on first element of document
        if (targetHandler == null)
        {
            if (JCRNamespace.SV_URI.equals(uri))
            {
                targetHandler = new JCRSystemXMLHandler(session, namespaceContext);
            }
            else
            {
                // TODO: doc view handler
            }
            targetHandler.setImporter(importer);
            targetHandler.startDocument();
        }
        
        targetHandler.startElement(uri, localName, qName, atts);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        targetHandler.endElement(uri, localName, qName);
        namespaceContext.popContext();
    }

    public void characters(char[] ch, int start, int length) throws SAXException
    {
        targetHandler.characters(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
    {
        targetHandler.characters(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException
    {
        targetHandler.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException
    {
        targetHandler.skippedEntity(name);
    }

    public void warning(SAXParseException exception) throws SAXException
    {
        targetHandler.warning(exception);
    }

    public void error(SAXParseException exception) throws SAXException
    {
        targetHandler.error(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException
    {
        targetHandler.fatalError(exception);
    }

    
    /**
     * Namespace Context
     *
     * Implementation supported by NamespaceSupport which itself does not
     * handle empty uri registration.
     */
    private static class NamespaceContext implements NamespacePrefixResolver
    {
        private final NamespaceSupport context;

        private static final String REMAPPED_DEFAULT_URI = " ";


        private NamespaceContext()
        {
            context = new NamespaceSupport();
        }

        private void reset()
        {
            context.reset();
        }
        
        private void pushContext()
        {
            context.pushContext();
        }

        private void popContext()
        {
            context.popContext();
        }

        private boolean registerPrefix(String prefix, String uri)
        {
            if (NamespaceService.DEFAULT_URI.equals(uri))
            {
                uri = REMAPPED_DEFAULT_URI;
            }
            return context.declarePrefix(prefix, uri);
        }

        public String getNamespaceURI(String prefix) throws org.alfresco.service.namespace.NamespaceException
        {
            String uri = context.getURI(prefix);
            if (uri == null)
            {
                throw new org.alfresco.service.namespace.NamespaceException("Namespace prefix " + prefix + " not registered.");
            }
            if (REMAPPED_DEFAULT_URI.equals(uri))
            {
                return NamespaceService.DEFAULT_URI;
            }
            return uri;
        }

        public Collection<String> getPrefixes(String namespaceURI) throws org.alfresco.service.namespace.NamespaceException
        {
            if (NamespaceService.DEFAULT_URI.equals(namespaceURI))
            {
                namespaceURI = REMAPPED_DEFAULT_URI;
            }
            String prefix = context.getPrefix(namespaceURI);
            if (prefix == null)
            {
                if (namespaceURI.equals(context.getURI(NamespaceService.DEFAULT_PREFIX)))
                {
                    prefix = NamespaceService.DEFAULT_PREFIX;
                }
                throw new org.alfresco.service.namespace.NamespaceException("Namespace URI " + namespaceURI + " not registered.");
            }
            List<String> prefixes = new ArrayList<String>(1);
            prefixes.add(prefix);
            return prefixes;
        }

        public Collection<String> getPrefixes()
        {
            // TODO:
            return null;
        }

        public Collection<String> getURIs()
        {
            // TODO:
            return null;
        }
    }
    
}
