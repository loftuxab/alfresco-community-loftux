package org.alfresco.enterprise.repo.officeservices.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.dictionary.constraint.NumericRangeConstraint;
import org.alfresco.repo.dictionary.constraint.StringLengthConstraint;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.xaldon.officeservices.datamodel.ContentTypeDefinition;
import com.xaldon.officeservices.datamodel.ContentTypeId;
import com.xaldon.officeservices.datamodel.DateFieldFormat;
import com.xaldon.officeservices.datamodel.FieldDefinition;
import com.xaldon.officeservices.datamodel.FieldValue;
import com.xaldon.officeservices.datamodel.Guid;

public class StandardDataModelMapper implements DataModelMapper, InitializingBean
{
    
    public static String DEFAULT_CONTENT_TYPE_GROUP = "Alfresco Document Types";

    protected NodeService nodeService;
    
    protected DictionaryService dictionaryService;
    
    protected NamespaceService namespaceService;
    
    protected DataModelMappingConfiguration dataModelMappingConfiguration;
    
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "nodeService", this.nodeService);
        PropertyCheck.mandatory(this, "dictionaryService", this.dictionaryService);
        PropertyCheck.mandatory(this, "namespaceService", this.namespaceService);
        PropertyCheck.mandatory(this, "dataModelMappingConfiguration", this.dataModelMappingConfiguration);
    }
    
    public static String GUID_MARKER_CONTENTTYPE = "0000000000000000";
    
    public static String GUID_MARKER_PROPERTY = "1111111111111111";
    
    public Guid createGuidFromQname(QName name, String marker)
    {
        if(marker.length() != 16)
        {
            throw new IllegalArgumentException("Invalid marker String");
        }
        String namespaceId = Integer.toHexString(name.getNamespaceURI().hashCode()).toUpperCase();
        while (namespaceId.length() < 8)
        {
            namespaceId = "0" + namespaceId;
        }
        String localNameId = Integer.toHexString(name.getLocalName().hashCode()).toUpperCase();
        while (localNameId.length() < 8)
        {
            localNameId = "0" + localNameId;
        }
        return Guid.parse(namespaceId + marker + localNameId);
    }
    
    public boolean isGuidMarkedAs(Guid id, String marker)
    {
        return id.toCondensedString().substring(8, 24).equals(marker);
    }

    @Override
    public Guid getContentTypeGuid(QName name)
    {
        return createGuidFromQname(name, GUID_MARKER_CONTENTTYPE);
    }

    @Override
    public Guid getPropertyGuid(QName name)
    {
        return createGuidFromQname(name, GUID_MARKER_PROPERTY);
    }
    
    @Override
    public ContentTypeId getContentTypeId(QName name)
    {
        return ContentTypeId.DOCUMENT.getChild(getContentTypeGuid(name));
    }
    
    @Override
    public ContentTypeId getContentTypeId(NodeRef nodeRef)
    {
        return ContentTypeId.DOCUMENT.getChild(Guid.parse(nodeRef.getId()));
    }

    @Override
    public FieldDefinition convertToFieldDefinition(PropertyDefinition propDef)
    {
        if(propDef.isMultiValued())
        {
            return null;
        }
        Guid id = getPropertyGuid(propDef.getName());
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
        boolean readOnly = propDef.isProtected();
        boolean mandatory = readOnly ? false : propDef.isMandatory(); // MNT-13196: read-only properties are always optional
        DataTypeDefinition dtd = propDef.getDataType();
        if(logger.isDebugEnabled())
        {
            logger.debug("convertToFieldDefinition("+propDef.getName().toString()+"): id="+id+" name="+name+" displayName="+displayName+" dataType="+dtd.getName()+" mandatory="+mandatory+" readOnly="+readOnly);
        }
        if(dtd.getName().equals(DataTypeDefinition.TEXT))
        {
            Pair<ListOfValuesConstraint, Boolean> listOfValuesConstraintResult = findConstraint(propDef.getConstraints(), ListOfValuesConstraint.class);
            if(listOfValuesConstraintResult.getFirst() != null)
            {
                if(listOfValuesConstraintResult.getSecond().booleanValue())
                {
                    return null;
                }
                ListOfValuesConstraint listOfValuesConstraint = listOfValuesConstraintResult.getFirst();
                List<String> listOfDisplayLabels = new ArrayList<String>();
                for (String allowedValue : listOfValuesConstraint.getAllowedValues())
                {
                    String displayLabel = listOfValuesConstraint.getDisplayLabel(allowedValue, dictionaryService);
                    listOfDisplayLabels.add(displayLabel);
                }
                return setShowIn(FieldDefinition.createChoiceFieldDefinition(id, name, displayName, description, mandatory, readOnly, listOfDisplayLabels), readOnly);
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
            return setShowIn(FieldDefinition.createTextFieldDefinition(id, name, displayName, description, mandatory, readOnly, minTextLength, maxTextLength), readOnly);
        }
        else if(dtd.getName().equals(DataTypeDefinition.MLTEXT))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            return setShowIn(FieldDefinition.createNoteFieldDefinition(id, name, displayName, description, mandatory, readOnly), readOnly);
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
            return setShowIn(FieldDefinition.createNumberFieldDefinition(id, name, displayName, description, mandatory, readOnly, precision, minValue, maxValue), readOnly);
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
            return setShowIn(FieldDefinition.createNumberFieldDefinition(id, name, displayName, description, mandatory, readOnly, precision, minValue, maxValue), readOnly);
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
            return setShowIn(FieldDefinition.createNumberFieldDefinition(id, name, displayName, description, mandatory, readOnly, precision, minValue, maxValue), readOnly);
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
            return setShowIn(FieldDefinition.createNumberFieldDefinition(id, name, displayName, description, mandatory, readOnly, precision, minValue, maxValue), readOnly);
        }
        else if(dtd.getName().equals(DataTypeDefinition.DATE))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            return setShowIn(FieldDefinition.createDateTimeFieldDefinition(id, name, displayName, description, mandatory, readOnly, DateFieldFormat.DATE_ONLY), readOnly);
        }
        else if(dtd.getName().equals(DataTypeDefinition.DATETIME))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            return setShowIn(FieldDefinition.createDateTimeFieldDefinition(id, name, displayName, description, mandatory, readOnly, DateFieldFormat.DATE_TIME), readOnly);
        }
        else if(dtd.getName().equals(DataTypeDefinition.BOOLEAN))
        {
            if( (propDef.getConstraints() != null) && (propDef.getConstraints().size() > 0))
            {
                return null;
            }
            return setShowIn(FieldDefinition.createBooleanFieldDefinition(id, name, displayName, description, mandatory, readOnly), readOnly);
        }
        return null;
    }

    private FieldDefinition setShowIn(FieldDefinition fieldDef, boolean readOnly)
    {
        if(readOnly)
        {
            fieldDef.setShowInEditForm(false);
            fieldDef.setShowInFileDlg(false);
            fieldDef.setShowInNewForm(false);
        }
        return fieldDef;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Constraint> Pair<T,Boolean> findConstraint(List<ConstraintDefinition> constraints, Class<T> clazz)
    {
        if(constraints == null)
        {
            return new Pair<T,Boolean>(null,Boolean.FALSE);
        }
        T constraint = null;
        boolean hasOthers = false;
        for(ConstraintDefinition constraintDefinition : constraints)
        {
            Constraint c = constraintDefinition.getConstraint();
            if(clazz.isAssignableFrom(c.getClass()))
            {
                constraint = (T)c;
            }
            else
            {
                hasOthers = true;
            }
        }
        return new Pair<T,Boolean>(constraint,Boolean.valueOf(hasOthers));
    }
    
    public static String displayNameToXmlName(String prefixString)
    {
        StringBuilder result = new StringBuilder(prefixString.length()*2);
        int numChars = prefixString.length();
        for(int i = 0; i < numChars; i++)
        {
            char c = prefixString.charAt(i);
            if( ((c >= 'A')&&(c <= 'Z')) || ((c >= 'a')&&(c <= 'z')) || ((c >= '0')&&(c <= '9')) )
            {
                result.append(c);
            }
            else
            {
                result.append('_');
                result.append('x');
                String hexC = Integer.toHexString((int)c);
                int numFill = 4 - hexC.length();
                for(int ii = 0; ii < numFill; ii++)
                {
                    result.append('0');
                }
                result.append(hexC);
                result.append('_');
            }
        }
        return result.toString();
    }
    
    @Override
    public Collection<ContentTypeDefinition> getAllContentTypes(boolean includeFields, boolean onlyInstantiable)
    {
        Collection<QName> allTypesInDocumentBranch = dictionaryService.getSubTypes(dataModelMappingConfiguration.getRootDocumentType(), true);
        ArrayList<ContentTypeDefinition> result = new ArrayList<ContentTypeDefinition>(allTypesInDocumentBranch.size());
        for(QName typeName : allTypesInDocumentBranch)
        {
            if(onlyInstantiable && !dataModelMappingConfiguration.isInstantiable(typeName))
            {
                continue;
            }
            ClassDefinition classDef = dictionaryService.getClass(typeName);
            if(classDef != null)
            {
                ContentTypeDefinition ctd = convertToContentTypeDefinition(classDef, includeFields, null);
                if(ctd != null)
                {
                    result.add(ctd);
                }
            }
        }
        return result;
    }
    
    protected ContentTypeDefinition convertToContentTypeDefinition(ClassDefinition classDef, boolean includeFields, ContentTypeId overwriteTypeId)
    {
        ContentTypeDefinition result = new ContentTypeDefinition(
                    overwriteTypeId != null ? overwriteTypeId : this.getContentTypeId(classDef.getName()),
                    classDef.getTitle(dictionaryService),
                    DEFAULT_CONTENT_TYPE_GROUP,
                    classDef.getDescription(dictionaryService));
        if(includeFields)
        {
            addPropertiesToContentTypeDefinition(classDef.getProperties(), result);
        }
        return result;
    }
    
    protected void addPropertiesToContentTypeDefinition(Map<QName,PropertyDefinition> propDefs, ContentTypeDefinition contentType)
    {
        for(PropertyDefinition propDef : propDefs.values())
        {
            if(!dataModelMappingConfiguration.isPropertyMapped(propDef.getName()))
            {
                continue;
            }
            FieldDefinition fieldDef = this.convertToFieldDefinition(propDef);
            if(fieldDef != null)
            {
                contentType.addFieldDefinition(fieldDef);
            }
        }
    }

    @Override
    public ContentTypeDefinition getContentTypeDefinition(ContentTypeId contentTypeId, boolean includeFields)
    {
        // check parameters and get root of branch in dictionary where we need to search for this content type
        QName dictionaryBranchParent = null;
        if(!contentTypeId.isGuidBased())
        {
            return null;
        }
        if(contentTypeId.getParent().equals(ContentTypeId.DOCUMENT))
        {
            dictionaryBranchParent = dataModelMappingConfiguration.getRootDocumentType();
        }
        else if(contentTypeId.getParent().equals(ContentTypeId.FOLDER))
        {
            dictionaryBranchParent = dataModelMappingConfiguration.getRootFolderType();
        }
        else
        {
            return null;
        }
        Guid contentTypeGuid = contentTypeId.getGuid();
        // A ContentTypeId can describe a single type in the dictionary or a NodeRef
        if(this.isGuidMarkedAs(contentTypeGuid, GUID_MARKER_CONTENTTYPE))
        {
            return getContentTypeDefinitionOfDictionaryType(contentTypeGuid, dictionaryBranchParent, includeFields);
        }
        else
        {
            return getContentTypeDefinitionOfNodeRef(new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, contentTypeGuid.toInnerString().toLowerCase()), includeFields);
        }
    }

    protected ContentTypeDefinition getContentTypeDefinitionOfDictionaryType(Guid contentTypeGuid, QName dictionaryBranchParent, boolean includeFields)
    {
        QName typeName = findQNameForGuid(contentTypeGuid, dictionaryBranchParent);
        if(typeName == null)
        {
            return null;
        }
        ClassDefinition classDef = dictionaryService.getClass(typeName);
        if(classDef == null)
        {
            return null;
        }
        return this.convertToContentTypeDefinition(classDef, includeFields, null);
    }
    
    protected QName findQNameForGuid(Guid contentTypeGuid, QName dictionaryBranchParent)
    {
        Collection<QName> allTypesInBranch = dictionaryService.getSubTypes(dictionaryBranchParent, true);
        for(QName typeName : allTypesInBranch)
        {
            if(contentTypeGuid.equals(this.getContentTypeGuid(typeName)))
            {
                return typeName;
            }
        }
        return null;
    }

    protected ContentTypeDefinition getContentTypeDefinitionOfNodeRef(NodeRef nodeRef, boolean includeFields)
    {
        // try to find this node
        QName nodeTypeQname;
        Set<QName> aspectsQNames;
        try
        {
            nodeTypeQname = nodeService.getType(nodeRef);
            aspectsQNames = nodeService.getAspects(nodeRef);
        }
        catch(Exception e)
        {
            return null;
        }
        // Try to resolve class
        ClassDefinition classDef = dictionaryService.getClass(nodeTypeQname);
        if(classDef == null)
        {
            return null;
        }
        // build ContentTypeDefinition from class
        ContentTypeDefinition result = this.convertToContentTypeDefinition(classDef, includeFields, this.getContentTypeId(nodeRef));
        if(includeFields)
        {
            for(QName aspectName : aspectsQNames)
            {
                if(!dataModelMappingConfiguration.isAspectMapped(aspectName))
                {
                    continue;
                }
                AspectDefinition aspectDef = dictionaryService.getAspect(aspectName);
                if(aspectDef == null)
                {
                    continue;
                }
                addPropertiesToContentTypeDefinition(aspectDef.getProperties(), result);
            }
        }
        return result;
    }
    
    @Override
    public QName getAlfrescoType(ContentTypeId contentTypeId)
    {
        if(!contentTypeId.isGuidBased())
        {
            return null;
        }
        QName dictionaryBranchParent = null;
        if(contentTypeId.getParent().equals(ContentTypeId.DOCUMENT))
        {
            dictionaryBranchParent = dataModelMappingConfiguration.getRootDocumentType();
        }
        else if(contentTypeId.getParent().equals(ContentTypeId.FOLDER))
        {
            dictionaryBranchParent = dataModelMappingConfiguration.getRootFolderType();
        }
        else
        {
            return null;
        }
        Guid contentTypeGuid = contentTypeId.getGuid();
        return findQNameForGuid(contentTypeGuid, dictionaryBranchParent);
    }

    @Override
    public Map<QName, FieldDefinition> getPropertyMapping(NodeRef nodeRef)
    {
        // try to find this node
        QName nodeTypeQname;
        Set<QName> aspectsQNames;
        try
        {
            nodeTypeQname = nodeService.getType(nodeRef);
            aspectsQNames = nodeService.getAspects(nodeRef);
        }
        catch(Exception e)
        {
            return null;
        }
        // Try to resolve class
        ClassDefinition classDef = dictionaryService.getClass(nodeTypeQname);
        if(classDef == null)
        {
            return null;
        }
        // build Mapping
        HashMap<QName, FieldDefinition> result = new HashMap<QName, FieldDefinition>();
        addPropertiesToMapping(classDef.getProperties(), result);
        for(QName aspectName : aspectsQNames)
        {
            if(!dataModelMappingConfiguration.isAspectMapped(aspectName))
            {
                continue;
            }
            AspectDefinition aspectDef = dictionaryService.getAspect(aspectName);
            if(aspectDef == null)
            {
                continue;
            }
            addPropertiesToMapping(aspectDef.getProperties(), result);
        }
        return Collections.unmodifiableMap(result);        
    }

    protected void addPropertiesToMapping(Map<QName,PropertyDefinition> propDefs, HashMap<QName, FieldDefinition> result)
    {
        for(PropertyDefinition propDef : propDefs.values())
        {
            if(!dataModelMappingConfiguration.isPropertyMapped(propDef.getName()))
            {
                continue;
            }
            FieldDefinition fieldDef = this.convertToFieldDefinition(propDef);
            if(fieldDef != null)
            {
                result.put(propDef.getName(), fieldDef);
            }
        }
    }
    
    @Override
    public Collection<FieldValue> getFieldValues(NodeRef nodeRef)
    {
        Map<QName, FieldDefinition> propertyMapping = getPropertyMapping(nodeRef);
        if(propertyMapping == null)
        {
            return null;
        }
        Map<QName, Serializable> alfrescoProperties = nodeService.getProperties(nodeRef);
        ArrayList<FieldValue> result = new ArrayList<FieldValue>(propertyMapping.size());
        for(Entry<QName, FieldDefinition> mappingEntry : propertyMapping.entrySet())
        {
            QName alfrescoName = mappingEntry.getKey();
            FieldDefinition fieldDef = mappingEntry.getValue();
            result.add(new FieldValue(fieldDef, alfrescoProperties.get(alfrescoName)));
        }
        return result;
    }

    public NodeService getNodeService()
    {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public NamespaceService getNamespaceService()
    {
        return namespaceService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public DataModelMappingConfiguration getDataModelMappingConfiguration()
    {
        return dataModelMappingConfiguration;
    }

    public void setDataModelMappingConfiguration(DataModelMappingConfiguration dataModelMappingConfiguration)
    {
        this.dataModelMappingConfiguration = dataModelMappingConfiguration;
    }

}
