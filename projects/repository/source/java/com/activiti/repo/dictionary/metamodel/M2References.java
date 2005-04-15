package com.activiti.repo.dictionary.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.ref.QName;


/**
 * Utilities for managing Data Dictionary References
 * 
 * @author David Caruana
 */
public class M2References
{

    /**
     * Construct an immutable list of Class Definitions
     * 
     * @param m2Classes  list of Class to construct definitions from
     * @return  list of class definitions
     */
    public static List<ClassDefinition> createClassRefList(Collection<? extends M2Class> m2Classes)
    {
        List<ClassDefinition> defs = new ArrayList<ClassDefinition>(m2Classes.size());
        for (M2Class m2Class : m2Classes)
        {
            defs.add(m2Class.getClassDefinition());
        }
        return defs;
    }


    /**
     * Construct an immutable list of Property definitions
     * 
     * @param m2Properties  list of properties to construct definitions from
     * @return  list of property defintions
     */
    public static List<PropertyDefinition> createPropertyRefList(Collection<M2Property> m2Properties)
    {
        List<PropertyDefinition> defs = new ArrayList<PropertyDefinition>(m2Properties.size());
        for (M2Property m2Property : m2Properties)
        {
            defs.add(m2Property.getPropertyDefinition());
        }
        return defs;
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
        return ddrefs;
    }
}
