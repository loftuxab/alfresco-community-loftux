package com.activiti.repo.dictionary;

import com.activiti.repo.ref.QName;

public interface PropertyTypeDefinition
{

    // TODO: Default Namespaces
    // TODO: Fill out primitive types
    public QName STRING = QName.createQName("activiti", "string");
    public QName DATE = QName.createQName("activiti", "date");
    public QName QNAME = QName.createQName("activiti", "name");
    public QName GUID = QName.createQName("activiti", "guid");

    // TODO: Fill out rest of getters
    
}
