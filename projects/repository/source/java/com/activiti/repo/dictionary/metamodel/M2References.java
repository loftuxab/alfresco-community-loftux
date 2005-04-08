package com.activiti.repo.dictionary.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.ref.QName;


/**
 * Utilities for managing Data Dictionary References
 * 
 * @author David Caruana
 */
public class M2References
{

    /**
     * Construct an immutable list of Class References
     * 
     * @param m2Classes  list of Class to construct references from
     * @return  list of class references
     */
    public static List<ClassRef> createClassRefList(Collection<? extends M2Class> m2Classes)
    {
        List<ClassRef> references = new ArrayList<ClassRef>(m2Classes.size());
        for (M2Class m2Class : m2Classes)
        {
            references.add(m2Class.getReference());
        }
        return Collections.unmodifiableList(references);
    }


    /**
     * Construct an immutable list of Property References
     * 
     * @param m2Properties  list of properties to construct references from
     * @return  list of property references
     */
    public static List<PropertyRef> createPropertyRefList(Collection<M2Property> m2Properties)
    {
        List<PropertyRef> references = new ArrayList<PropertyRef>(m2Properties.size());
        for (M2Property m2Property : m2Properties)
        {
            references.add(m2Property.getReference());
        }
        return Collections.unmodifiableList(references);
    }
    

    /**
     * Construct an immutable list of Class References
     * 
     * @param qnames  list of QNames to construct references from
     * @return  list of class references
     */
    public static Collection<ClassRef> createQNameClassRefCollection(Collection<QName> qnames)
    {
        Collection<ClassRef> ddrefs = new ArrayList<ClassRef>(qnames.size());
        for (QName qname : qnames)
        {
            ClassRef classRef = new ClassRef(qname);
            ddrefs.add(classRef);
        }
        
        return Collections.unmodifiableCollection(ddrefs);
    }
    
}
