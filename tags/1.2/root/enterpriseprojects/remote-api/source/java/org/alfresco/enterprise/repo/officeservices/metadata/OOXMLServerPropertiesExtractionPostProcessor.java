package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ISO8601DateFormat;
import org.alfresco.util.Pair;
import org.apache.log4j.Logger;

import com.alfresco.officeservices.docproc.DocumentProperty;
import com.alfresco.officeservices.docproc.PropertyFormat;
import com.xaldon.officeservices.datamodel.DateFieldFormat;
import com.xaldon.officeservices.datamodel.FieldDefinition;
import com.xaldon.officeservices.datamodel.FieldType;

public class OOXMLServerPropertiesExtractionPostProcessor implements ContentPostProcessor
{
    
    protected Map<QName, FieldDefinition> propertyMapping;
    
    protected Map<QName, DocumentProperty> before;
    
    protected Map<String, String> after;
    
    protected String title;
    
    protected NodeService nodeService;
    
    protected DictionaryService dictionaryService;
    
    protected ServerPropertiesProvider serverPropertiesProvider;
    
    protected static Logger logger = Logger.getLogger(OOXMLServerPropertiesExtractionPostProcessor.class);
    
    public OOXMLServerPropertiesExtractionPostProcessor(Map<QName, FieldDefinition> propertyMapping, Map<QName, DocumentProperty> before, Map<String, String> after, String title, NodeService nodeService, DictionaryService dictionaryService, ServerPropertiesProvider serverPropertiesProvider)
    {
        this.propertyMapping = propertyMapping;
        this.before = before;
        this.after = after;
        this.title = title;
        this.nodeService = nodeService;
        this.dictionaryService = dictionaryService;
        this.serverPropertiesProvider = serverPropertiesProvider;
    }
    
    @Override
    public void execute(NodeRef nodeRef)
    {
        try
        {
            Map<QName, Serializable> nodeUpdates = new HashMap<QName, Serializable>();
            QName titlePropertyName = serverPropertiesProvider.getDocumentTitlePropertyName();
            if( (titlePropertyName != null) && (title != null) )
            {
                PropertyDefinition titlePropDef = dictionaryService.getProperty(titlePropertyName);
                if(titlePropDef != null)
                {
                    ClassDefinition titlePropClass = titlePropDef.getContainerClass();
                    if(titlePropClass.isAspect() || dictionaryService.isSubClass(nodeService.getType(nodeRef), titlePropClass.getName()))
                    {
                        nodeUpdates.put(titlePropertyName, title);
                    }
                }
            }
            for(Entry<QName, FieldDefinition> mapping : propertyMapping.entrySet())
            {
                QName alfrescoName = mapping.getKey();
                FieldDefinition fieldDef = mapping.getValue();
                if(fieldDef.isReadOnly())
                {
                    continue;
                }
                DocumentProperty propBefore = before != null ? before.get(alfrescoName) : null;
                boolean isNullBefore = (propBefore==null) || (propBefore.getValue() == null);
                String formattedValueBefore = isNullBefore ? "" : propBefore.getFormattedValueString(PropertyFormat.XML);
                String formattedNewValue = after.get(fieldDef.getName());
                if( (formattedNewValue == null) || (formattedNewValue.length()==0) )
                {
                    if(!isNullBefore)
                    {
                        nodeService.removeProperty(nodeRef, alfrescoName);
                    }
                    continue;
                }
                if(formattedValueBefore.equals(formattedNewValue))
                {
                    continue;
                }
                Serializable newValue;
                try
                {
                    newValue = convertValue(formattedNewValue, fieldDef, alfrescoName);
                }
                catch(Exception e)
                {
                    logger.error("Cannot convert value '"+formattedNewValue+"' for property "+alfrescoName,e);
                    continue;
                }
                nodeUpdates.put(alfrescoName, newValue);
            }
            if(!nodeUpdates.isEmpty())
            {
                nodeService.addProperties(nodeRef, nodeUpdates);
            }
        }
        catch(Exception e)
        {
            logger.error("Error updating properties on node.",e);
        }
    }

    protected Serializable convertValue(String formattedNewValue, FieldDefinition fieldDef, QName alfrescoName) throws ParseException
    {
        FieldType fieldType = fieldDef.getFieldType();
        switch (fieldType)
        {
            case Text:
                return (String) formattedNewValue;
            case Note:
                return (String) formattedNewValue;
            case DateTime:
                DateFieldFormat dateFieldFormat = fieldDef.getDateFormat();
                TimeZone timeZone = TimeZone.getTimeZone("UTC");
                if (dateFieldFormat.equals(DateFieldFormat.DATE_ONLY))
                {
                    // We need to parse date, time and TZ parts because of ACE-3855. See MNT-13077
                    return ISO8601DateFormat.parse(formattedNewValue, timeZone);
                }
                return ISO8601DateFormat.parse(formattedNewValue, timeZone);
            case Choice:
                String value = null;
                PropertyDefinition propDef = dictionaryService.getProperty(alfrescoName);
                Pair<ListOfValuesConstraint, Boolean> listOfValuesConstraintResult = StandardDataModelMapper.findConstraint(propDef.getConstraints(), ListOfValuesConstraint.class);
                ListOfValuesConstraint listOfValuesConstraint = listOfValuesConstraintResult.getFirst();
                if((listOfValuesConstraint != null) && !listOfValuesConstraintResult.getSecond().booleanValue())
                {
                    for (String allowedValue : listOfValuesConstraint.getAllowedValues())
                    {
                        String displayLabel = listOfValuesConstraint.getDisplayLabel(allowedValue, dictionaryService);
                        if (displayLabel.equals(formattedNewValue))
                        {
                            value = allowedValue;
                            break;
                        }
                    }
                }
                return value;
            case Boolean:
                return Boolean.valueOf(formattedNewValue.equals("1") || formattedNewValue.equalsIgnoreCase("true"));
            case Number:
            {
                DecimalFormat df = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
                return df.parse(formattedNewValue);
            }
            default:
                throw new IllegalArgumentException("Unknown data type.");
        }
    }

}
