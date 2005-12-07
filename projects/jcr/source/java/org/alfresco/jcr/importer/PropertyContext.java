package org.alfresco.jcr.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alfresco.repo.importer.view.ElementContext;
import org.alfresco.repo.importer.view.NodeContext;
import org.alfresco.service.namespace.QName;

public class PropertyContext extends ElementContext
{
    private NodeContext parentContext;
    private QName propertyName;
    private QName propertyType;
    
    private List<StringBuffer> values = new ArrayList<StringBuffer>();
    
    
    public PropertyContext(QName elementName, NodeContext parentContext, QName propertyName, QName propertyType)
    {
        super(elementName, parentContext.getDictionaryService(), parentContext.getImporter());
        this.parentContext = parentContext;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public NodeContext getNode()
    {
        return parentContext;
    }
    
    public QName getName()
    {
        return propertyName;
    }

    public QName getType()
    {
        return propertyType;
    }
    
    public boolean isMultiValue()
    {
        return values.size() > 1;
    }
    
    public boolean isNull()
    {
        return values.size() == 0;
    }
    
    public List<StringBuffer> getValues()
    {
        return values;
    }
    
    public void addValue()
    {
        values.add(new StringBuffer(2048));
    }
    
    
    public void appendCharacters(char[] ch, int start, int length)
    {
        StringBuffer buffer = values.get(values.size() -1);
        buffer.append(ch, start, length);
    }
    
}
