package com.activiti.repo.dictionary;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;


/**
 * Base Data Dictionary Reference.
 * 
 * @author David Caruana
 */
public class DictionaryRef extends NodeRef
{
    /**
     * Data Dictionary Store Protocol
     */
    public static final String DICTIONARY_PROTOCOL = "dictionary";
    
    /**
     * Default Dictionary Store Reference
     */
    public static final StoreRef DEFAULT_DICTIONARY = new StoreRef(DICTIONARY_PROTOCOL, "default");

    /**
     * Qualified name seperator
     */
    protected static final String NAME_SEPARATOR = "-";
    
    /**
     * Qualified name of meta definition
     */
    private QName metaQName;

    private static final long serialVersionUID = -2083305978298709327L;
    

    /**
     * Construct Dictionary Reference for Default Dictionary Store
     * 
     * @param metaQName  the name of meta definition (e.g. class name, property name)
     */
    public DictionaryRef(QName metaQName)
    {
        super(DEFAULT_DICTIONARY, metaQName.toString());
        this.metaQName = metaQName;
    }


    /**
     * Gets the qualified name of the meta definition
     * 
     * @return  the qualified name (e.g. class name, property name)
     */
    public QName getQName()
    {
        return this.metaQName;
    }
    
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj == null || !(obj instanceof DictionaryRef))
        {
            return false;
        }
        DictionaryRef that = (DictionaryRef) obj;
        return this.getQName().equals(that.getQName());
    }
}
