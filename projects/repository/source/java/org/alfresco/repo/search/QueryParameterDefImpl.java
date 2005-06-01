/*
 * Created on 19-May-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search;

import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;
import org.dom4j.Element;
import org.dom4j.Namespace;

public class QueryParameterDefImpl implements QueryParameterDefinition
{

    private static final org.dom4j.QName ELEMENT_QNAME = new org.dom4j.QName("parameter-definition", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private static final org.dom4j.QName DEF_QNAME = new org.dom4j.QName("qname", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private static final org.dom4j.QName PROPERTY_QNAME = new org.dom4j.QName("property", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private static final org.dom4j.QName PROPERTY_TYPE_QNAME = new org.dom4j.QName("type", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    private static final org.dom4j.QName DEFAULT_VALUE = new org.dom4j.QName("default-value", new Namespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI));

    
    private QName qName;

    private PropertyDefinition propertyDefintion;

    private PropertyTypeDefinition propertyTypeDefintion;
    
    private boolean hasDefaultValue;
    
    private String defaultValue;

    public QueryParameterDefImpl(QName qName, PropertyDefinition propertyDefinition, boolean hasDefaultValue, String defaultValue)
    {
        this(qName, hasDefaultValue, defaultValue);
        this.propertyDefintion = propertyDefinition;
        this.propertyTypeDefintion = propertyDefinition.getPropertyType();
    }

    private QueryParameterDefImpl(QName qName, boolean hasDefaultValue, String defaultValue)
    {
        super();
        this.qName = qName;
        this.hasDefaultValue = hasDefaultValue;
        this.defaultValue = defaultValue;
    }
    
    public QueryParameterDefImpl(QName qName, PropertyTypeDefinition propertyTypeDefintion, boolean hasDefaultValue, String defaultValue)
    {
        this(qName, hasDefaultValue, defaultValue);
        this.propertyDefintion = null;
        this.propertyTypeDefintion = propertyTypeDefintion;
    }

    public QName getQName()
    {
        return qName;
    }

    public PropertyDefinition getPropertyDefinition()
    {
        return propertyDefintion;
    }

    public PropertyTypeDefinition getPropertyTypeDefinition()
    {
        return propertyTypeDefintion;
    }

    public static QueryParameterDefinition createParameterDefinition(Element element, DictionaryService dictionaryService,  NamespacePrefixResolver nspr)
    {

        if (element.getQName().getName().equals(ELEMENT_QNAME.getName()))
        {
            QName qName = null;
            Element qNameElement = element.element(DEF_QNAME.getName());
            if (qNameElement != null)
            {
                qName = QName.createQName(qNameElement.getText(), nspr);
            }

            PropertyDefinition propDef = null;
//            TODO: DC: This will be re-visited when notion of property is encapsulated
//                  For now default to text property type
//            Element propDefElement = element.element(PROPERTY_QNAME.getName());
//            if (propDefElement != null)
//            {
//                propDef = dictionaryService.getProperty(QName.createQName(propDefElement.getText(), nspr));
//            }

            PropertyTypeDefinition typeDef = null;
            Element typeDefElement = element.element(PROPERTY_TYPE_QNAME.getName());
            if (typeDefElement != null)
            {
                typeDef = dictionaryService.getPropertyType(QName.createQName(typeDefElement.getText(), nspr));
            }

            boolean hasDefault = false;
            String defaultValue = null;
            Element defaultValueElement = element.element(DEFAULT_VALUE.getName());
            if(defaultValueElement != null)
            {
                hasDefault = true;
                defaultValue = defaultValueElement.getText();
            }
            
            if (propDef != null)
            {
                return new QueryParameterDefImpl(qName, propDef, hasDefault, defaultValue);
            }
            else
            {
                return new QueryParameterDefImpl(qName, typeDef, hasDefault, defaultValue);
            }
        }
        else
        {
            return null;
        }
    }

    public static org.dom4j.QName getElementQName()
    {
        return ELEMENT_QNAME;
    }

    public QueryParameterDefinition getQueryParameterDefinition()
    {
        return this;
    }

    /**
     * There may be a default value which is null ie <default-value/> the empty
     * string <default-value></default-value> or no entry at all for no default
     * value
     */
    public String getDefault()
    {
        return defaultValue;
    }

    public boolean hasDefaultValue()
    {
        return hasDefaultValue;
    }

}
