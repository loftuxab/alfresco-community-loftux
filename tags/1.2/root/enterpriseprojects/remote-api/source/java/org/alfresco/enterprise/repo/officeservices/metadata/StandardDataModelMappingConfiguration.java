package org.alfresco.enterprise.repo.officeservices.metadata;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class StandardDataModelMappingConfiguration implements DataModelMappingConfiguration, InitializingBean
{
    
    protected DictionaryService dictionaryService;
    
    protected String documentRootType;
    
    protected String folderRootType;
    
    protected QName rootDocumentType;
    
    protected QName rootFolderType;
    
    protected List<String> includedTypesPatterns;
    
    protected List<String> excludedTypesPatterns;
    
    protected List<String> includedAspectsPatterns;
    
    protected List<String> excludedAspectsPatterns;
    
    protected List<String> includedPropertiesPatterns;
    
    protected List<String> excludedPropertiesPatterns;
    
    protected List<String> includedInstantiableTypesPatterns;
    
    protected List<String> excludedInstantiableTypesPatterns;
    
    protected List<Pattern> includedTypes;
    
    protected List<Pattern> excludedTypes;
    
    protected List<Pattern> includedAspects;
    
    protected List<Pattern> excludedAspects;
    
    protected List<Pattern> includedProperties;
    
    protected List<Pattern> excludedProperties;
    
    protected List<Pattern> includedInstantiableTypes;
    
    protected List<Pattern> excludedInstantiableTypes;
    
    protected Logger logger = Logger.getLogger(this.getClass());

    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "dictionaryService", this.dictionaryService);
        PropertyCheck.mandatory(this, "documentRootType", this.documentRootType);
        PropertyCheck.mandatory(this, "folderRootType", this.folderRootType);
        rootDocumentType = QName.createQName(documentRootType);
        rootFolderType = QName.createQName(folderRootType);
        includedTypes = compilePatterns(includedTypesPatterns, "includedTypesPatterns");
        excludedTypes = compilePatterns(excludedTypesPatterns, "excludedTypesPatterns");
        includedAspects = compilePatterns(includedAspectsPatterns, "includedAspectsPatterns");
        excludedAspects = compilePatterns(excludedAspectsPatterns, "excludedAspectsPatterns");
        includedProperties = compilePatterns(includedPropertiesPatterns, "includedPropertiesPatterns");
        excludedProperties = compilePatterns(excludedPropertiesPatterns, "excludedPropertiesPatterns");
        includedInstantiableTypes = compilePatterns(includedInstantiableTypesPatterns, "includedInstantiableTypesPatterns");
        excludedInstantiableTypes = compilePatterns(excludedInstantiableTypesPatterns, "excludedInstantiableTypesPatterns");
    }
    
    private List<Pattern> compilePatterns(List<String> patternStrings, String propertyName)
    {
        if(patternStrings == null)
        {
            return null;
        }
        List<Pattern> result = new LinkedList<Pattern>();
        for(String patternString : patternStrings)
        {
            try
            {
                result.add(Pattern.compile(patternString));
            }
            catch(PatternSyntaxException pse)
            {
                logger.error("Syntax error in " + propertyName + " '" + patternString + "'. Ignoring pattern.",pse);
            }
        }
        return result;
    }
    
    protected static boolean isNameAcceptedBy(QName qname, List<Pattern> patterns)
    {
        if( (qname != null) && (patterns != null) )
        {
            for(Pattern pattern : patterns)
            {
                if(pattern.matcher(qname.toString()).matches())
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected static boolean isMapped(QName qname, List<Pattern> includedPatterns, List<Pattern> excludedPatterns)
    {
        return isNameAcceptedBy(qname, includedPatterns) && !isNameAcceptedBy(qname, excludedPatterns);
    }
    
    @Override
    public boolean isTypeMapped(QName qname)
    {
        return dictionaryService.isSubClass(qname, ContentModel.TYPE_CONTENT) && isMapped(qname, includedTypes, excludedTypes);
    }
    
    @Override
    public boolean isAspectMapped(QName qname)
    {
        return isMapped(qname, includedAspects, excludedAspects);
    }
    
    @Override
    public boolean isPropertyMapped(QName qname)
    {
        return isMapped(qname, includedProperties, excludedProperties);
    }

    @Override
    public QName getRootDocumentType()
    {
        return rootDocumentType;
    }

    @Override
    public QName getRootFolderType()
    {
        return rootFolderType;
    }

    @Override
    public boolean isInstantiable(QName qname)
    {
        return isTypeMapped(qname) && isMapped(qname, includedInstantiableTypes, excludedInstantiableTypes);
    }

    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public String getDocumentRootType()
    {
        return documentRootType;
    }

    public void setDocumentRootType(String documentRootType)
    {
        this.documentRootType = documentRootType;
    }

    public String getFolderRootType()
    {
        return folderRootType;
    }

    public void setFolderRootType(String folderRootType)
    {
        this.folderRootType = folderRootType;
    }

    public List<String> getIncludedTypesPatterns()
    {
        return includedTypesPatterns;
    }

    public void setIncludedTypesPatterns(List<String> includedTypesPatterns)
    {
        this.includedTypesPatterns = includedTypesPatterns;
    }

    public List<String> getExcludedTypesPatterns()
    {
        return excludedTypesPatterns;
    }

    public void setExcludedTypesPatterns(List<String> excludedTypesPatterns)
    {
        this.excludedTypesPatterns = excludedTypesPatterns;
    }

    public List<String> getIncludedAspectsPatterns()
    {
        return includedAspectsPatterns;
    }

    public void setIncludedAspectsPatterns(List<String> includedAspectsPatterns)
    {
        this.includedAspectsPatterns = includedAspectsPatterns;
    }

    public List<String> getExcludedAspectsPatterns()
    {
        return excludedAspectsPatterns;
    }

    public void setExcludedAspectsPatterns(List<String> ignoredAspectsPatterns)
    {
        this.excludedAspectsPatterns = ignoredAspectsPatterns;
    }

    public List<String> getIncludedPropertiesPatterns()
    {
        return includedPropertiesPatterns;
    }

    public void setIncludedPropertiesPatterns(List<String> includedPropertiesPatterns)
    {
        this.includedPropertiesPatterns = includedPropertiesPatterns;
    }

    public List<String> getExcludedPropertiesPatterns()
    {
        return excludedPropertiesPatterns;
    }

    public void setExcludedPropertiesPatterns(List<String> ignoredPropertiesPatterns)
    {
        this.excludedPropertiesPatterns = ignoredPropertiesPatterns;
    }

    public List<String> getIncludedInstantiableTypesPatterns()
    {
        return includedInstantiableTypesPatterns;
    }

    public void setIncludedInstantiableTypesPatterns(List<String> includedInstantiableTypesPatterns)
    {
        this.includedInstantiableTypesPatterns = includedInstantiableTypesPatterns;
    }

    public List<String> getExcludedInstantiableTypesPatterns()
    {
        return excludedInstantiableTypesPatterns;
    }

    public void setExcludedInstantiableTypesPatterns(List<String> excludedInstantiableTypesPatterns)
    {
        this.excludedInstantiableTypesPatterns = excludedInstantiableTypesPatterns;
    }

}
