package com.activiti.repo.dictionary;

import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;

public class DictionaryRef extends NodeRef
{
    public static final String DICTIONARY_PROTOCOL = "dictionary";
    public static final StoreRef DEFAULT_DICTIONARY = new StoreRef(DICTIONARY_PROTOCOL, "default");

    protected static final String NAME_SEPARATOR = "/";
    
    private static final long serialVersionUID = -2083305978298709327L;
    
    private QName metaQName;
    

    public DictionaryRef(QName metaQName)
    {
        super(DEFAULT_DICTIONARY, metaQName.toString());
        this.metaQName = metaQName;
    }

    
    public QName getQName()
    {
        return this.metaQName;
    }
    
}
