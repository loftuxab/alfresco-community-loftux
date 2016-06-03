package org.alfresco.share.site.document;

import java.util.Set;

import org.alfresco.po.share.site.document.DocumentAspect;

/**
 * The Property holds information to be compared during the add & remove aspects.
 * @author Shan Nagarajan
 * @since  1.1
 */
public class AspectTestProptery
{

    private String testName;
    
    private DocumentAspect aspect;
    
    private int sizeBeforeAspectAdded;
    
    private int sizeAfterAspectAdded;
    
    private Set<String> expectedProprtyKey;

    public String getTestName()
    {
        return testName;
    }

    public void setTestName(String testName)
    {
        this.testName = testName;
    }

    public DocumentAspect getAspect()
    {
        return aspect;
    }

    public void setAspect(DocumentAspect aspect)
    {
        this.aspect = aspect;
    }

    public int getSizeBeforeAspectAdded()
    {
        return sizeBeforeAspectAdded;
    }

    public void setSizeBeforeAspectAdded(int sizeBeforeAspectAdded)
    {
        this.sizeBeforeAspectAdded = sizeBeforeAspectAdded;
    }

    public int getSizeAfterAspectAdded()
    {
        return sizeAfterAspectAdded;
    }

    public void setSizeAfterAspectAdded(int sizeAfterAspectAdded)
    {
        this.sizeAfterAspectAdded = sizeAfterAspectAdded;
    }

    public Set<String> getExpectedProprtyKey()
    {
        return expectedProprtyKey;
    }

    public void setExpectedProprtyKey(Set<String> expectedProprtyKey)
    {
        this.expectedProprtyKey = expectedProprtyKey;
    }
    
}
