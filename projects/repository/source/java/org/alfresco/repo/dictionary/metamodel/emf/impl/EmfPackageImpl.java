/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import org.alfresco.repo.dictionary.metamodel.emf.EMFAspect;
import org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation;
import org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation;
import org.alfresco.repo.dictionary.metamodel.emf.EMFClass;
import org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix;
import org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI;
import org.alfresco.repo.dictionary.metamodel.emf.EMFProperty;
import org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType;
import org.alfresco.repo.dictionary.metamodel.emf.EMFType;
import org.alfresco.repo.dictionary.metamodel.emf.EmfFactory;
import org.alfresco.repo.dictionary.metamodel.emf.EmfPackage;

import org.alfresco.repo.ref.QName;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EmfPackageImpl extends EPackageImpl implements EmfPackage
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfClassEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfPropertyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfAspectEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfAssociationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfChildAssociationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfPropertyTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfNamespacePrefixEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass emfNamespaceURIEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType qNameEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private EmfPackageImpl()
    {
        super(eNS_URI, EmfFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static EmfPackage init()
    {
        if (isInited) return (EmfPackage)EPackage.Registry.INSTANCE.getEPackage(EmfPackage.eNS_URI);

        // Obtain or create and register package
        EmfPackageImpl theEmfPackage = (EmfPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof EmfPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new EmfPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theEmfPackage.createPackageContents();

        // Initialize created meta-data
        theEmfPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theEmfPackage.freeze();

        return theEmfPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFClass()
    {
        return emfClassEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFClass_EmfName()
    {
        return (EAttribute)emfClassEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFClass_EmfProperties()
    {
        return (EReference)emfClassEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFClass_EmfSuperClass()
    {
        return (EReference)emfClassEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFClass_EmfAssociations()
    {
        return (EReference)emfClassEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFProperty()
    {
        return emfPropertyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFProperty_EmfContainerClass()
    {
        return (EReference)emfPropertyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfName()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFProperty_EmfType()
    {
        return (EReference)emfPropertyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfProtected()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfMandatory()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfMultiple()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfIndexed()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfStoredInIndex()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfTokenisedInIndex()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFProperty_EmfIndexAtomically()
    {
        return (EAttribute)emfPropertyEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFAspect()
    {
        return emfAspectEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFAssociation()
    {
        return emfAssociationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFAssociation_EmfName()
    {
        return (EAttribute)emfAssociationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFAssociation_EmfContainerClass()
    {
        return (EReference)emfAssociationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFAssociation_EmfProtected()
    {
        return (EAttribute)emfAssociationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFAssociation_EmfMandatory()
    {
        return (EAttribute)emfAssociationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFAssociation_EmfMultiple()
    {
        return (EAttribute)emfAssociationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFAssociation_EmfRequiredToClasses()
    {
        return (EReference)emfAssociationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFChildAssociation()
    {
        return emfChildAssociationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFChildAssociation_EmfDefaultType()
    {
        return (EReference)emfChildAssociationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFPropertyType()
    {
        return emfPropertyTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFPropertyType_QName()
    {
        return (EAttribute)emfPropertyTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFPropertyType_EmfAnalyserClassName()
    {
        return (EAttribute)emfPropertyTypeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFType()
    {
        return emfTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFType_EmfOrderedChildren()
    {
        return (EAttribute)emfTypeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFType_EmfDefaultAspects()
    {
        return (EReference)emfTypeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFType_EmfStrict()
    {
        return (EAttribute)emfTypeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFNamespacePrefix()
    {
        return emfNamespacePrefixEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFNamespacePrefix_EmfPrefix()
    {
        return (EAttribute)emfNamespacePrefixEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFNamespacePrefix_EmfURI()
    {
        return (EReference)emfNamespacePrefixEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEMFNamespaceURI()
    {
        return emfNamespaceURIEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEMFNamespaceURI_EmfURI()
    {
        return (EAttribute)emfNamespaceURIEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getEMFNamespaceURI_EmfPrefixes()
    {
        return (EReference)emfNamespaceURIEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getQName()
    {
        return qNameEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EmfFactory getEmfFactory()
    {
        return (EmfFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents()
    {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        emfClassEClass = createEClass(EMF_CLASS);
        createEAttribute(emfClassEClass, EMF_CLASS__EMF_NAME);
        createEReference(emfClassEClass, EMF_CLASS__EMF_PROPERTIES);
        createEReference(emfClassEClass, EMF_CLASS__EMF_SUPER_CLASS);
        createEReference(emfClassEClass, EMF_CLASS__EMF_ASSOCIATIONS);

        emfPropertyEClass = createEClass(EMF_PROPERTY);
        createEReference(emfPropertyEClass, EMF_PROPERTY__EMF_CONTAINER_CLASS);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_NAME);
        createEReference(emfPropertyEClass, EMF_PROPERTY__EMF_TYPE);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_PROTECTED);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_MANDATORY);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_MULTIPLE);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_INDEXED);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_STORED_IN_INDEX);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_TOKENISED_IN_INDEX);
        createEAttribute(emfPropertyEClass, EMF_PROPERTY__EMF_INDEX_ATOMICALLY);

        emfAspectEClass = createEClass(EMF_ASPECT);

        emfAssociationEClass = createEClass(EMF_ASSOCIATION);
        createEAttribute(emfAssociationEClass, EMF_ASSOCIATION__EMF_NAME);
        createEReference(emfAssociationEClass, EMF_ASSOCIATION__EMF_CONTAINER_CLASS);
        createEAttribute(emfAssociationEClass, EMF_ASSOCIATION__EMF_PROTECTED);
        createEAttribute(emfAssociationEClass, EMF_ASSOCIATION__EMF_MANDATORY);
        createEAttribute(emfAssociationEClass, EMF_ASSOCIATION__EMF_MULTIPLE);
        createEReference(emfAssociationEClass, EMF_ASSOCIATION__EMF_REQUIRED_TO_CLASSES);

        emfChildAssociationEClass = createEClass(EMF_CHILD_ASSOCIATION);
        createEReference(emfChildAssociationEClass, EMF_CHILD_ASSOCIATION__EMF_DEFAULT_TYPE);

        emfPropertyTypeEClass = createEClass(EMF_PROPERTY_TYPE);
        createEAttribute(emfPropertyTypeEClass, EMF_PROPERTY_TYPE__QNAME);
        createEAttribute(emfPropertyTypeEClass, EMF_PROPERTY_TYPE__EMF_ANALYSER_CLASS_NAME);

        emfTypeEClass = createEClass(EMF_TYPE);
        createEAttribute(emfTypeEClass, EMF_TYPE__EMF_ORDERED_CHILDREN);
        createEReference(emfTypeEClass, EMF_TYPE__EMF_DEFAULT_ASPECTS);
        createEAttribute(emfTypeEClass, EMF_TYPE__EMF_STRICT);

        emfNamespacePrefixEClass = createEClass(EMF_NAMESPACE_PREFIX);
        createEAttribute(emfNamespacePrefixEClass, EMF_NAMESPACE_PREFIX__EMF_PREFIX);
        createEReference(emfNamespacePrefixEClass, EMF_NAMESPACE_PREFIX__EMF_URI);

        emfNamespaceURIEClass = createEClass(EMF_NAMESPACE_URI);
        createEAttribute(emfNamespaceURIEClass, EMF_NAMESPACE_URI__EMF_URI);
        createEReference(emfNamespaceURIEClass, EMF_NAMESPACE_URI__EMF_PREFIXES);

        // Create data types
        qNameEDataType = createEDataType(QNAME);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents()
    {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Add supertypes to classes
        emfAspectEClass.getESuperTypes().add(this.getEMFClass());
        emfChildAssociationEClass.getESuperTypes().add(this.getEMFAssociation());
        emfTypeEClass.getESuperTypes().add(this.getEMFClass());

        // Initialize classes and features; add operations and parameters
        initEClass(emfClassEClass, EMFClass.class, "EMFClass", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEMFClass_EmfName(), this.getQName(), "emfName", null, 0, 1, EMFClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFClass_EmfProperties(), this.getEMFProperty(), this.getEMFProperty_EmfContainerClass(), "emfProperties", null, 0, -1, EMFClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFClass_EmfSuperClass(), this.getEMFClass(), null, "emfSuperClass", null, 0, 1, EMFClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFClass_EmfAssociations(), this.getEMFAssociation(), this.getEMFAssociation_EmfContainerClass(), "emfAssociations", null, 0, -1, EMFClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(emfPropertyEClass, EMFProperty.class, "EMFProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEMFProperty_EmfContainerClass(), this.getEMFClass(), this.getEMFClass_EmfProperties(), "emfContainerClass", null, 0, 1, EMFProperty.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfName(), ecorePackage.getEString(), "emfName", null, 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFProperty_EmfType(), this.getEMFPropertyType(), null, "emfType", null, 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfProtected(), ecorePackage.getEBooleanObject(), "emfProtected", "false", 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfMandatory(), ecorePackage.getEBooleanObject(), "emfMandatory", "false", 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfMultiple(), ecorePackage.getEBooleanObject(), "emfMultiple", "false", 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfIndexed(), ecorePackage.getEBooleanObject(), "emfIndexed", "true", 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfStoredInIndex(), ecorePackage.getEBooleanObject(), "emfStoredInIndex", "false", 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfTokenisedInIndex(), ecorePackage.getEBooleanObject(), "emfTokenisedInIndex", "false", 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFProperty_EmfIndexAtomically(), ecorePackage.getEBooleanObject(), "emfIndexAtomically", "true", 0, 1, EMFProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(emfAspectEClass, EMFAspect.class, "EMFAspect", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(emfAssociationEClass, EMFAssociation.class, "EMFAssociation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEMFAssociation_EmfName(), ecorePackage.getEString(), "emfName", null, 0, 1, EMFAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFAssociation_EmfContainerClass(), this.getEMFClass(), this.getEMFClass_EmfAssociations(), "emfContainerClass", null, 0, 1, EMFAssociation.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFAssociation_EmfProtected(), ecorePackage.getEBooleanObject(), "emfProtected", "false", 0, 1, EMFAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFAssociation_EmfMandatory(), ecorePackage.getEBooleanObject(), "emfMandatory", "false", 0, 1, EMFAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFAssociation_EmfMultiple(), ecorePackage.getEBooleanObject(), "emfMultiple", "true", 0, 1, EMFAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFAssociation_EmfRequiredToClasses(), this.getEMFClass(), null, "emfRequiredToClasses", null, 0, -1, EMFAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(emfChildAssociationEClass, EMFChildAssociation.class, "EMFChildAssociation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getEMFChildAssociation_EmfDefaultType(), this.getEMFType(), null, "emfDefaultType", null, 0, 1, EMFChildAssociation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(emfPropertyTypeEClass, EMFPropertyType.class, "EMFPropertyType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEMFPropertyType_QName(), this.getQName(), "qName", null, 0, 1, EMFPropertyType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFPropertyType_EmfAnalyserClassName(), ecorePackage.getEString(), "emfAnalyserClassName", null, 0, 1, EMFPropertyType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(emfTypeEClass, EMFType.class, "EMFType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEMFType_EmfOrderedChildren(), ecorePackage.getEBooleanObject(), "emfOrderedChildren", "false", 0, 1, EMFType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFType_EmfDefaultAspects(), this.getEMFAspect(), null, "emfDefaultAspects", null, 0, -1, EMFType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getEMFType_EmfStrict(), ecorePackage.getEBooleanObject(), "emfStrict", "true", 0, 1, EMFType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(emfNamespacePrefixEClass, EMFNamespacePrefix.class, "EMFNamespacePrefix", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEMFNamespacePrefix_EmfPrefix(), ecorePackage.getEString(), "emfPrefix", null, 0, 1, EMFNamespacePrefix.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFNamespacePrefix_EmfURI(), this.getEMFNamespaceURI(), this.getEMFNamespaceURI_EmfPrefixes(), "emfURI", null, 1, 1, EMFNamespacePrefix.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(emfNamespaceURIEClass, EMFNamespaceURI.class, "EMFNamespaceURI", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getEMFNamespaceURI_EmfURI(), ecorePackage.getEString(), "emfURI", null, 0, 1, EMFNamespaceURI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getEMFNamespaceURI_EmfPrefixes(), this.getEMFNamespacePrefix(), this.getEMFNamespacePrefix_EmfURI(), "emfPrefixes", null, 0, -1, EMFNamespaceURI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize data types
        initEDataType(qNameEDataType, QName.class, "QName", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //EmfPackageImpl
