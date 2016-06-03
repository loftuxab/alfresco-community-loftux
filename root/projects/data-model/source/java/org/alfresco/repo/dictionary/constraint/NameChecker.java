package org.alfresco.repo.dictionary.constraint;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.PropertyCheck;
import org.springframework.beans.factory.InitializingBean;

/**
 * Loads cm:filename constraint from dictionary to use it during batch jobs
 * 
 * @see <a href=https://issues.alfresco.com/jira/browse/MNT-9414>MNT-9414</a>
 * 
 * @author Viachaslau Tsikhanovich *
 */
public class NameChecker implements InitializingBean
{
    private DictionaryService dictionaryService;

    private Constraint nameConstraint;
    
    // namespaceURI of the constraint
    private String namespaceURI = NamespaceService.CONTENT_MODEL_1_0_URI;
    
    //constraint local name
    private String constraintLocalName ="filename";  
    
    public void setNamespaceURI(String namespaceURI)
    {
        this.namespaceURI = namespaceURI;
    }
    
    public void setConstraintLocalName(String constraintLocalName)
    {
        this.constraintLocalName = constraintLocalName;
    }
    
    public DictionaryService getDictionaryService()
    {
        return dictionaryService;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Loads filename constraint from dictionary
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "dictionaryService", dictionaryService);

        QName qNameConstraint = QName.createQName(namespaceURI, constraintLocalName);
        ConstraintDefinition constraintDef = dictionaryService.getConstraint(qNameConstraint);
        if (constraintDef == null)
        {
            throw new AlfrescoRuntimeException("Constraint definition does not exist: " + qNameConstraint);
        }
        nameConstraint = constraintDef.getConstraint();
        if (nameConstraint == null)
        {
            throw new AlfrescoRuntimeException("Constraint does not exist: " + qNameConstraint);
        }
    }

    public void evaluate(Object value)
    {
        nameConstraint.evaluate(value);
    }

}
