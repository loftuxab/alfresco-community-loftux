package com.activiti.repo.dictionary;

public class DictionaryException extends RuntimeException
{

    private static final long serialVersionUID = 3257008761007847733L;

    public DictionaryException(String msg)
    {
       super(msg);
    }
    
    public DictionaryException(String msg, Throwable cause)
    {
       super(msg, cause);
    }

}
