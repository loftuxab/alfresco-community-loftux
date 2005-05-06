/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.alfresco.repo.dictionary.metamodel.emf.EmfFactory
 * @generated
 */
public interface EmfPackage extends EPackage{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "emf";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.alfresco.com/repo/datadictionary";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "datadictionary";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EmfPackage eINSTANCE = org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl.init();

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFClassImpl <em>EMF Class</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFClassImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFClass()
     * @generated
     */
    int EMF_CLASS = 0;

    /**
     * The feature id for the '<em><b>Emf Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CLASS__EMF_NAME = 0;

    /**
     * The feature id for the '<em><b>Emf Properties</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CLASS__EMF_PROPERTIES = 1;

    /**
     * The feature id for the '<em><b>Emf Super Class</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CLASS__EMF_SUPER_CLASS = 2;

    /**
     * The feature id for the '<em><b>Emf Associations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CLASS__EMF_ASSOCIATIONS = 3;

    /**
     * The number of structural features of the the '<em>EMF Class</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CLASS_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl <em>EMF Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFPropertyImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFProperty()
     * @generated
     */
    int EMF_PROPERTY = 1;

    /**
     * The feature id for the '<em><b>Emf Container Class</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_CONTAINER_CLASS = 0;

    /**
     * The feature id for the '<em><b>Emf Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_NAME = 1;

    /**
     * The feature id for the '<em><b>Emf Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_TYPE = 2;

    /**
     * The feature id for the '<em><b>Emf Protected</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_PROTECTED = 3;

    /**
     * The feature id for the '<em><b>Emf Mandatory</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_MANDATORY = 4;

    /**
     * The feature id for the '<em><b>Emf Multiple</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_MULTIPLE = 5;

    /**
     * The feature id for the '<em><b>Emf Indexed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_INDEXED = 6;

    /**
     * The feature id for the '<em><b>Emf Stored In Index</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_STORED_IN_INDEX = 7;

    /**
     * The feature id for the '<em><b>Emf Tokenised In Index</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_TOKENISED_IN_INDEX = 8;

    /**
     * The feature id for the '<em><b>Emf Index Atomically</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY__EMF_INDEX_ATOMICALLY = 9;

    /**
     * The number of structural features of the the '<em>EMF Property</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY_FEATURE_COUNT = 10;

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAspectImpl <em>EMF Aspect</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAspectImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFAspect()
     * @generated
     */
    int EMF_ASPECT = 2;

    /**
     * The feature id for the '<em><b>Emf Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASPECT__EMF_NAME = EMF_CLASS__EMF_NAME;

    /**
     * The feature id for the '<em><b>Emf Properties</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASPECT__EMF_PROPERTIES = EMF_CLASS__EMF_PROPERTIES;

    /**
     * The feature id for the '<em><b>Emf Super Class</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASPECT__EMF_SUPER_CLASS = EMF_CLASS__EMF_SUPER_CLASS;

    /**
     * The feature id for the '<em><b>Emf Associations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASPECT__EMF_ASSOCIATIONS = EMF_CLASS__EMF_ASSOCIATIONS;

    /**
     * The number of structural features of the the '<em>EMF Aspect</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASPECT_FEATURE_COUNT = EMF_CLASS_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl <em>EMF Association</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFAssociationImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFAssociation()
     * @generated
     */
    int EMF_ASSOCIATION = 3;

    /**
     * The feature id for the '<em><b>Emf Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASSOCIATION__EMF_NAME = 0;

    /**
     * The feature id for the '<em><b>Emf Container Class</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASSOCIATION__EMF_CONTAINER_CLASS = 1;

    /**
     * The feature id for the '<em><b>Emf Protected</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASSOCIATION__EMF_PROTECTED = 2;

    /**
     * The feature id for the '<em><b>Emf Mandatory</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASSOCIATION__EMF_MANDATORY = 3;

    /**
     * The feature id for the '<em><b>Emf Multiple</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASSOCIATION__EMF_MULTIPLE = 4;

    /**
     * The feature id for the '<em><b>Emf Required To Classes</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES = 5;

    /**
     * The number of structural features of the the '<em>EMF Association</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_ASSOCIATION_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFChildAssociationImpl <em>EMF Child Association</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFChildAssociationImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFChildAssociation()
     * @generated
     */
    int EMF_CHILD_ASSOCIATION = 4;

    /**
     * The feature id for the '<em><b>Emf Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION__EMF_NAME = EMF_ASSOCIATION__EMF_NAME;

    /**
     * The feature id for the '<em><b>Emf Container Class</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION__EMF_CONTAINER_CLASS = EMF_ASSOCIATION__EMF_CONTAINER_CLASS;

    /**
     * The feature id for the '<em><b>Emf Protected</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION__EMF_PROTECTED = EMF_ASSOCIATION__EMF_PROTECTED;

    /**
     * The feature id for the '<em><b>Emf Mandatory</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION__EMF_MANDATORY = EMF_ASSOCIATION__EMF_MANDATORY;

    /**
     * The feature id for the '<em><b>Emf Multiple</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION__EMF_MULTIPLE = EMF_ASSOCIATION__EMF_MULTIPLE;

    /**
     * The feature id for the '<em><b>Emf Required To Classes</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION__EMF_REQUIRED_TO_CLASSES = EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES;

    /**
     * The feature id for the '<em><b>Emf Default Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE = EMF_ASSOCIATION_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>EMF Child Association</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_CHILD_ASSOCIATION_FEATURE_COUNT = EMF_ASSOCIATION_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFPropertyTypeImpl <em>EMF Property Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFPropertyTypeImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFPropertyType()
     * @generated
     */
    int EMF_PROPERTY_TYPE = 5;

    /**
     * The feature id for the '<em><b>QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY_TYPE__QNAME = 0;

    /**
     * The feature id for the '<em><b>Emf Analyser Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY_TYPE__EMF_ANALYSER_CLASS_NAME = 1;

    /**
     * The number of structural features of the the '<em>EMF Property Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_PROPERTY_TYPE_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFTypeImpl <em>EMF Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFTypeImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFType()
     * @generated
     */
    int EMF_TYPE = 6;

    /**
     * The feature id for the '<em><b>Emf Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE__EMF_NAME = EMF_CLASS__EMF_NAME;

    /**
     * The feature id for the '<em><b>Emf Properties</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE__EMF_PROPERTIES = EMF_CLASS__EMF_PROPERTIES;

    /**
     * The feature id for the '<em><b>Emf Super Class</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE__EMF_SUPER_CLASS = EMF_CLASS__EMF_SUPER_CLASS;

    /**
     * The feature id for the '<em><b>Emf Associations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE__EMF_ASSOCIATIONS = EMF_CLASS__EMF_ASSOCIATIONS;

    /**
     * The feature id for the '<em><b>Emf Ordered Children</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE__EMF_ORDERED_CHILDREN = EMF_CLASS_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Emf Default Aspects</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE__EMF_DEFAULT_ASPECTS = EMF_CLASS_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Emf Strict</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE__EMF_STRICT = EMF_CLASS_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>EMF Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_TYPE_FEATURE_COUNT = EMF_CLASS_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespacePrefixImpl <em>EMF Namespace Prefix</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespacePrefixImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFNamespacePrefix()
     * @generated
     */
    int EMF_NAMESPACE_PREFIX = 7;

    /**
     * The feature id for the '<em><b>Emf Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_NAMESPACE_PREFIX__EMF_PREFIX = 0;

    /**
     * The feature id for the '<em><b>Emf URI</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_NAMESPACE_PREFIX__EMF_URI = 1;

    /**
     * The number of structural features of the the '<em>EMF Namespace Prefix</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_NAMESPACE_PREFIX_FEATURE_COUNT = 2;

 
    /**
     * The meta object id for the '{@link org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespaceURIImpl <em>EMF Namespace URI</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EMFNamespaceURIImpl
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getEMFNamespaceURI()
     * @generated
     */
    int EMF_NAMESPACE_URI = 8;

    /**
     * The feature id for the '<em><b>Emf URI</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_NAMESPACE_URI__EMF_URI = 0;

    /**
     * The feature id for the '<em><b>Emf Prefixes</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_NAMESPACE_URI__EMF_PREFIXES = 1;

    /**
     * The number of structural features of the the '<em>EMF Namespace URI</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EMF_NAMESPACE_URI_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '<em>QName</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.alfresco.repo.ref.QName
     * @see org.alfresco.repo.dictionary.metamodel.emf.impl.EmfPackageImpl#getQName()
     * @generated
     */
    int QNAME = 9;


    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass <em>EMF Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Class</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFClass
     * @generated
     */
    EClass getEMFClass();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfName <em>Emf Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Name</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfName()
     * @see #getEMFClass()
     * @generated
     */
    EAttribute getEMFClass_EmfName();

    /**
     * Returns the meta object for the containment reference list '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfProperties <em>Emf Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Emf Properties</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfProperties()
     * @see #getEMFClass()
     * @generated
     */
    EReference getEMFClass_EmfProperties();

    /**
     * Returns the meta object for the reference '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfSuperClass <em>Emf Super Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Emf Super Class</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfSuperClass()
     * @see #getEMFClass()
     * @generated
     */
    EReference getEMFClass_EmfSuperClass();

    /**
     * Returns the meta object for the containment reference list '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfAssociations <em>Emf Associations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Emf Associations</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfAssociations()
     * @see #getEMFClass()
     * @generated
     */
    EReference getEMFClass_EmfAssociations();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty <em>EMF Property</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Property</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty
     * @generated
     */
    EClass getEMFProperty();

    /**
     * Returns the meta object for the container reference '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfContainerClass <em>Emf Container Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Emf Container Class</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfContainerClass()
     * @see #getEMFProperty()
     * @generated
     */
    EReference getEMFProperty_EmfContainerClass();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfName <em>Emf Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Name</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfName()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfName();

    /**
     * Returns the meta object for the reference '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfType <em>Emf Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Emf Type</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfType()
     * @see #getEMFProperty()
     * @generated
     */
    EReference getEMFProperty_EmfType();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfProtected <em>Emf Protected</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Protected</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfProtected()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfProtected();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfMandatory <em>Emf Mandatory</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Mandatory</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfMandatory()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfMandatory();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfMultiple <em>Emf Multiple</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Multiple</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfMultiple()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfMultiple();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfIndexed <em>Emf Indexed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Indexed</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfIndexed()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfIndexed();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfStoredInIndex <em>Emf Stored In Index</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Stored In Index</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfStoredInIndex()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfStoredInIndex();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfTokenisedInIndex <em>Emf Tokenised In Index</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Tokenised In Index</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfTokenisedInIndex()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfTokenisedInIndex();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfIndexAtomically <em>Emf Index Atomically</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Index Atomically</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfIndexAtomically()
     * @see #getEMFProperty()
     * @generated
     */
    EAttribute getEMFProperty_EmfIndexAtomically();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAspect <em>EMF Aspect</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Aspect</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAspect
     * @generated
     */
    EClass getEMFAspect();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation <em>EMF Association</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Association</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation
     * @generated
     */
    EClass getEMFAssociation();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfName <em>Emf Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Name</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfName()
     * @see #getEMFAssociation()
     * @generated
     */
    EAttribute getEMFAssociation_EmfName();

    /**
     * Returns the meta object for the container reference '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfContainerClass <em>Emf Container Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Emf Container Class</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfContainerClass()
     * @see #getEMFAssociation()
     * @generated
     */
    EReference getEMFAssociation_EmfContainerClass();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfProtected <em>Emf Protected</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Protected</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfProtected()
     * @see #getEMFAssociation()
     * @generated
     */
    EAttribute getEMFAssociation_EmfProtected();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfMandatory <em>Emf Mandatory</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Mandatory</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfMandatory()
     * @see #getEMFAssociation()
     * @generated
     */
    EAttribute getEMFAssociation_EmfMandatory();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfMultiple <em>Emf Multiple</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Multiple</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfMultiple()
     * @see #getEMFAssociation()
     * @generated
     */
    EAttribute getEMFAssociation_EmfMultiple();

    /**
     * Returns the meta object for the reference list '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfRequiredToClasses <em>Emf Required To Classes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Emf Required To Classes</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfRequiredToClasses()
     * @see #getEMFAssociation()
     * @generated
     */
    EReference getEMFAssociation_EmfRequiredToClasses();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation <em>EMF Child Association</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Child Association</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation
     * @generated
     */
    EClass getEMFChildAssociation();

    /**
     * Returns the meta object for the reference '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation#getEmfDefaultType <em>Emf Default Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Emf Default Type</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation#getEmfDefaultType()
     * @see #getEMFChildAssociation()
     * @generated
     */
    EReference getEMFChildAssociation_EmfDefaultType();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType <em>EMF Property Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Property Type</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType
     * @generated
     */
    EClass getEMFPropertyType();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType#getQName <em>QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>QName</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType#getQName()
     * @see #getEMFPropertyType()
     * @generated
     */
    EAttribute getEMFPropertyType_QName();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType#getEmfAnalyserClassName <em>Emf Analyser Class Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Analyser Class Name</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType#getEmfAnalyserClassName()
     * @see #getEMFPropertyType()
     * @generated
     */
    EAttribute getEMFPropertyType_EmfAnalyserClassName();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFType <em>EMF Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Type</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFType
     * @generated
     */
    EClass getEMFType();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfOrderedChildren <em>Emf Ordered Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Ordered Children</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfOrderedChildren()
     * @see #getEMFType()
     * @generated
     */
    EAttribute getEMFType_EmfOrderedChildren();

    /**
     * Returns the meta object for the reference list '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfDefaultAspects <em>Emf Default Aspects</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Emf Default Aspects</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfDefaultAspects()
     * @see #getEMFType()
     * @generated
     */
    EReference getEMFType_EmfDefaultAspects();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfStrict <em>Emf Strict</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Strict</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfStrict()
     * @see #getEMFType()
     * @generated
     */
    EAttribute getEMFType_EmfStrict();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix <em>EMF Namespace Prefix</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Namespace Prefix</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix
     * @generated
     */
    EClass getEMFNamespacePrefix();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix#getEmfPrefix <em>Emf Prefix</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf Prefix</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix#getEmfPrefix()
     * @see #getEMFNamespacePrefix()
     * @generated
     */
    EAttribute getEMFNamespacePrefix_EmfPrefix();

    /**
     * Returns the meta object for the reference '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix#getEmfURI <em>Emf URI</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Emf URI</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix#getEmfURI()
     * @see #getEMFNamespacePrefix()
     * @generated
     */
    EReference getEMFNamespacePrefix_EmfURI();

    /**
     * Returns the meta object for class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI <em>EMF Namespace URI</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>EMF Namespace URI</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI
     * @generated
     */
    EClass getEMFNamespaceURI();

    /**
     * Returns the meta object for the attribute '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI#getEmfURI <em>Emf URI</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Emf URI</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI#getEmfURI()
     * @see #getEMFNamespaceURI()
     * @generated
     */
    EAttribute getEMFNamespaceURI_EmfURI();

    /**
     * Returns the meta object for the reference list '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI#getEmfPrefixes <em>Emf Prefixes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Emf Prefixes</em>'.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI#getEmfPrefixes()
     * @see #getEMFNamespaceURI()
     * @generated
     */
    EReference getEMFNamespaceURI_EmfPrefixes();

    /**
     * Returns the meta object for data type '{@link org.alfresco.repo.ref.QName <em>QName</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>QName</em>'.
     * @see org.alfresco.repo.ref.QName
     * @model instanceClass="org.alfresco.repo.ref.QName"
     * @generated
     */
    EDataType getQName();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    EmfFactory getEmfFactory();

} //EmfPackage
