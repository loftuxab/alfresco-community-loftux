/*
 * Copyright 2005-2014 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.dictionary.constraint.NumericRangeConstraint;
import org.alfresco.repo.dictionary.constraint.StringLengthConstraint;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.InvalidQNameException;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.log4j.Logger;

import com.alfresco.officeservices.docproc.DocumentProperty;
import com.alfresco.officeservices.docproc.DocumentPropertyDateFormat;
import com.alfresco.officeservices.docproc.DocumentPropertyOrigin;

public class StandardServerPropertiesProvider extends StandardDataModelMapper implements ServerPropertiesProvider
{
    
    protected String titleProperty;
    
    protected QName titlePropertyQName = null;
    
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void afterPropertiesSet() throws Exception
    {
        super.afterPropertiesSet();
        try
        {
            titlePropertyQName = QName.createQName(titleProperty);
        }
        catch(InvalidQNameException e)
        {
            logger.error("titleProperty is not a valid QName",e);
        }
    }
    
    @Override
    public Map<QName, DocumentProperty> getServerPropertiesMapping(NodeRef nodeRef)
    {
        logger.debug("getServerProperties for Node "+nodeRef);
        HashMap<QName, DocumentProperty> result = new HashMap<QName, DocumentProperty>();
        Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
        QName nodeTypeQname = nodeService.getType(nodeRef);
        logger.debug("Node Type: "+nodeTypeQname);
        ClassDefinition classDefinition = dictionaryService.getClass(nodeTypeQname);
        addAlfrescoPropertiesToDocumentProperties(properties, classDefinition.getProperties(), result);
        Set<QName> aspectsQNames = nodeService.getAspects(nodeRef);
        for(QName aspectQname : aspectsQNames)
        {
            logger.debug("Aspect Type: "+aspectQname);
            if(!dataModelMappingConfiguration.isAspectMapped(aspectQname))
            {
                logger.debug("    ignored.");
                continue;
            }
            AspectDefinition aspectDefinition = dictionaryService.getAspect(aspectQname);
            addAlfrescoPropertiesToDocumentProperties(properties, aspectDefinition.getProperties(), result);
        }
        return result;
    }

    protected void addAlfrescoPropertiesToDocumentProperties(Map<QName, Serializable> properties, Map<QName, PropertyDefinition> propertyDefinitions, HashMap<QName, DocumentProperty> result)
    {
        for(Map.Entry<QName, PropertyDefinition> propEntry : propertyDefinitions.entrySet())
        {
            PropertyDefinition propDef = propEntry.getValue();
            logger.debug("    Property: "+propDef.getName());
            if(!dataModelMappingConfiguration.isPropertyMapped(propDef.getName()))
            {
                logger.debug("        ignored.");
                continue;
            }
            if( (titleProperty != null) && titleProperty.equals(propDef.getName().toString()) )
            {
                logger.debug("        is title property. ignored.");
                continue;
            }
            DocumentProperty documentProperty = convertToDocumentProperty(propDef, properties.get(propDef.getName()));
            if(documentProperty == null)
            {
                logger.debug("        cannot be converted to SP data model. ignored.");
                continue;
            }
            result.put(propDef.getName(), documentProperty);
        }
    }
    
    protected DocumentProperty convertToDocumentProperty(PropertyDefinition propDef, Serializable value)
    {
        if(propDef.isMultiValued())
        {
            return null;
        }
        String displayName = propDef.getTitle(dictionaryService);
        if(displayName == null)
        {
            displayName = propDef.getName().toPrefixString(namespaceService);
        }
        String name = displayNameToXmlName(displayName);
        String description = propDef.getDescription(dictionaryService);
        if(description == null)
        {
            description = "";
        }
        DocumentPropertyOrigin origin = DocumentPropertyOrigin.SERVER;
        boolean readOnly = propDef.isProtected();
        boolean mandatory = readOnly ? false : propDef.isMandatory(); // MNT-13196: read-only properties are always optional
        DataTypeDefinition dtd = propDef.getDataType();
        logger.debug("        internalName="+name+" displayName="+displayName+" dataType="+dtd.getName()+" mandatory="+mandatory+" readOnly="+readOnly);
        if(dtd.getName().equals(DataTypeDefinition.TEXT))
        {
            Pair<ListOfValuesConstraint, Boolean> listOfValuesConstraintResult =findConstraint(propDef.getConstraints(), ListOfValuesConstraint.class);
            if(listOfValuesConstraintResult.getFirst() != null)
            {
                if(listOfValuesConstraintResult.getSecond().booleanValue())
                {
                    return null;
                }
                ListOfValuesConstraint listOfValuesConstraint = listOfValuesConstraintResult.getFirst();
                List<String> listOfDisplayLabels = new ArrayList<String>();
                String displayValue = null;
                for (String allowedValue : listOfValuesConstraint.getAllowedValues())
                {
                    String displayLabel = listOfValuesConstraint.getDisplayLabel(allowedValue, dictionaryService);
                    listOfDisplayLabels.add(displayLabel);
                    if ((value != null) && value.equals(allowedValue))
                    {
                        displayValue = displayLabel;
                    }
                }
                return DocumentProperty.createChoiceDocumentProperty(name, displayName, description, origin, mandatory, readOnly, displayValue, listOfDisplayLabels);
            }
            Pair<StringLengthConstraint,Boolean> stringLengthConstraintResult = findConstraint(propDef.getConstraints(), StringLengthConstraint.class);
            if(stringLengthConstraintResult.getSecond().booleanValue())
            {
                return null;
            }
            Integer minTextLength = null;
            Integer maxTextLength = null;
            if(stringLengthConstraintResult.getFirst() != null)
            {
                StringLengthConstraint stringLengthConstraint = stringLengthConstraintResult.getFirst();
                if(stringLengthConstraint.getMinLength() != 0)
                {
                    minTextLength = Integer.valueOf(stringLengthConstraint.getMinLength());
                }
                if(stringLengthConstraint.getMaxLength() != Integer.MAX_VALUE)
                {
                    maxTextLength = Integer.valueOf(stringLengthConstraint.getMaxLength());
                }
            }
            return DocumentProperty.createTextDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (String)value, minTextLength, maxTextLength, false);
        }
        else if(dtd.getName().equals(DataTypeDefinition.MLTEXT))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            Integer minTextLength = null;
            Integer maxTextLength = null;
            return DocumentProperty.createTextDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (String)value, minTextLength, maxTextLength, true);
        }
        else if(dtd.getName().equals(DataTypeDefinition.INT))
        {
            int precision = 0;
            Number minValue = null;
            Number maxValue = null;
            Pair<NumericRangeConstraint, Boolean> numericRangeConstraintResult = findConstraint(propDef.getConstraints(), NumericRangeConstraint.class);
            if(numericRangeConstraintResult.getSecond().booleanValue())
            {
                return null;
            }
            if(numericRangeConstraintResult.getFirst() != null)
            {
                NumericRangeConstraint numericRangeConstraint = numericRangeConstraintResult.getFirst();
                minValue = Double.valueOf(numericRangeConstraint.getMinValue());
                maxValue = Double.valueOf(numericRangeConstraint.getMaxValue());
            }
            else
            {
                minValue = Integer.valueOf(Integer.MIN_VALUE);
                maxValue = Integer.valueOf(Integer.MAX_VALUE);
            }
            return DocumentProperty.createNumberDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (Integer)value, precision, minValue, maxValue);
        }
        else if(dtd.getName().equals(DataTypeDefinition.LONG))
        {
            int precision = 0;
            Number minValue = null;
            Number maxValue = null;
            Pair<NumericRangeConstraint, Boolean> numericRangeConstraintResult = findConstraint(propDef.getConstraints(), NumericRangeConstraint.class);
            if(numericRangeConstraintResult.getSecond().booleanValue())
            {
                return null;
            }
            if(numericRangeConstraintResult.getFirst() != null)
            {
                NumericRangeConstraint numericRangeConstraint = numericRangeConstraintResult.getFirst();
                minValue = Double.valueOf(numericRangeConstraint.getMinValue());
                maxValue = Double.valueOf(numericRangeConstraint.getMaxValue());
            }
            else
            {
                minValue = Long.valueOf(Long.MIN_VALUE);
                maxValue = Long.valueOf(Long.MAX_VALUE);
            }
            return DocumentProperty.createNumberDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (Long)value, precision, minValue, maxValue);
        }
        else if(dtd.getName().equals(DataTypeDefinition.FLOAT))
        {
            int precision = 5;
            Number minValue = null;
            Number maxValue = null;
            Pair<NumericRangeConstraint, Boolean> numericRangeConstraintResult = findConstraint(propDef.getConstraints(), NumericRangeConstraint.class);
            if(numericRangeConstraintResult.getSecond().booleanValue())
            {
                return null;
            }
            if(numericRangeConstraintResult.getFirst() != null)
            {
                NumericRangeConstraint numericRangeConstraint = numericRangeConstraintResult.getFirst();
                minValue = Double.valueOf(numericRangeConstraint.getMinValue());
                maxValue = Double.valueOf(numericRangeConstraint.getMaxValue());
            }
            return DocumentProperty.createNumberDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (Float)value, precision, minValue, maxValue);
        }
        else if(dtd.getName().equals(DataTypeDefinition.DOUBLE))
        {
            int precision = 10;
            Number minValue = null;
            Number maxValue = null;
            Pair<NumericRangeConstraint, Boolean> numericRangeConstraintResult = findConstraint(propDef.getConstraints(), NumericRangeConstraint.class);
            if(numericRangeConstraintResult.getSecond().booleanValue())
            {
                return null;
            }
            if(numericRangeConstraintResult.getFirst() != null)
            {
                NumericRangeConstraint numericRangeConstraint = numericRangeConstraintResult.getFirst();
                minValue = Double.valueOf(numericRangeConstraint.getMinValue());
                maxValue = Double.valueOf(numericRangeConstraint.getMaxValue());
            }
            return DocumentProperty.createNumberDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (Double)value, precision, minValue, maxValue);
        }
        else if(dtd.getName().equals(DataTypeDefinition.DATE))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            return DocumentProperty.createDateTimeDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (Date)value, DocumentPropertyDateFormat.DATE_ONLY);
        }
        else if(dtd.getName().equals(DataTypeDefinition.DATETIME))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            return DocumentProperty.createDateTimeDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (Date)value, DocumentPropertyDateFormat.DATE_TIME);
        }
        else if(dtd.getName().equals(DataTypeDefinition.BOOLEAN))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            return DocumentProperty.createBooleanDocumentProperty(name, displayName, description, origin, mandatory, readOnly, (Boolean)value);
        }
        return null;
    }

    public QName getDocumentTitlePropertyName()
    {
        return titlePropertyQName;
    }

    public String getDocumentTitle(NodeRef nodeRef)
    {
        if(titlePropertyQName == null)
        {
            return null;
        }
        Serializable value = nodeService.getProperty(nodeRef, titlePropertyQName);
        if(value == null)
        {
            return null;
        }
        String stringValue = value.toString();
        if( (stringValue == null) || stringValue.isEmpty() )
        {
            return null;
        }
        return stringValue;
    }

    public String getTitleProperty()
    {
        return titleProperty;
    }

    public void setTitleProperty(String titleProperty)
    {
        this.titleProperty = titleProperty;
    }

}
