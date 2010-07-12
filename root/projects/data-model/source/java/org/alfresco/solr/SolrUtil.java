/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.solr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.alfresco.repo.dictionary.IndexTokenisationMode;
import org.alfresco.repo.search.impl.lucene.analysis.DateTimeAnalyser;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.QName;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Andy
 */
public abstract class SolrUtil
{
    public static void generateSchema(File file, DictionaryService service) throws IOException, SAXException
    {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setNewLineAfterDeclaration(false);
        format.setIndentSize(3);
        format.setEncoding("UTF-8");
        XMLWriter xmlWriter = new XMLWriter(new BufferedWriter(new FileWriter(file)), format);
        xmlWriter.startDocument();

        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "name", "name", null, "alfresco");
        attrs.addAttribute("", "version", "version", null, "1.0");
        xmlWriter.startElement("", "schema", "schema", attrs);

        xmlWriter.startElement("", "types", "types", new AttributesImpl());
        writeFieldType(xmlWriter, "alfrescoDataType", "org.alfresco.solr.AlfrescoDataType");
        xmlWriter.endElement("", "types", "types");

        xmlWriter.startElement("", "fields", "fields", new AttributesImpl());

        writeField(xmlWriter, "ID", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "TX", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "PARENT", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "LINKASPECT", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "PATH", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "ANCESTOR", "alfresco", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "ISCONTAINER", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "ISCATEGORY", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "QNAME", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "ISROOT", "alfresco", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "PRIMARYASSOCTYPEQNAME", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "ISNODE", "alfresco", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "ASSOCTYPEQNAME", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "PRIMARYPARENT", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "TYPE", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);
        writeField(xmlWriter, "ASPECT", "alfresco", Store.YES, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, true);
        writeField(xmlWriter, "FTSSTATUS", "alfresco", Store.NO, Index.NOT_ANALYZED_NO_NORMS, TermVector.NO, false);

        for (QName modelName : service.getAllModels())
        {
            for (QName propertyName : service.getProperties(modelName))
            {
                PropertyDefinition propertyDefinition = service.getProperty(propertyName);
                writeField(xmlWriter, propertyDefinition);
            }
        }
        xmlWriter.endElement("", "fields", "fields");

        xmlWriter.endElement("", "schema", "schema");

        xmlWriter.endDocument();
        xmlWriter.close();
    }

    private static void writeFieldType(XMLWriter xmlWriter, String name, String clazz) throws SAXException
    {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "name", "name", null, name);
        attrs.addAttribute("", "class", "class", null, clazz);
        xmlWriter.startElement("", "fieldType", "fieldType", attrs);
        xmlWriter.endElement("", "fieldType", "fieldType");
    }

    private static void writeField(XMLWriter xmlWriter, PropertyDefinition propertyDefinition) throws SAXException
    {
        String name = "@" + propertyDefinition.getName().toString();

        Store store = propertyDefinition.isStoredInIndex() ? Store.YES : Store.NO;
        boolean multiValued = propertyDefinition.isMultiValued();
        if (propertyDefinition.isIndexed())
        {
            switch (propertyDefinition.getIndexTokenisationMode())
            {
            case BOTH:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
                if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.CONTENT))
                {

                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.MLTEXT))
                {

                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.TEXT))
                {
                }
                else if (propertyDefinition.getDataType().getName().equals(DataTypeDefinition.DATETIME))
                {
                    DataTypeDefinition dataType = propertyDefinition.getDataType();
                    String analyserClassName = dataType.getAnalyserClassName();
                    if (analyserClassName.equals(DateTimeAnalyser.class.getCanonicalName()))
                    {
                        writeField(xmlWriter, name + ".sort", "alfrescoDataType", store, Index.NOT_ANALYZED, TermVector.NO, multiValued);
                    }
                    else
                    {
                        // nothing
                    }
                }
                else
                {
                    // nothing
                }
                break;
            case FALSE:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.NOT_ANALYZED, TermVector.NO, multiValued);
                break;
            case TRUE:
                writeField(xmlWriter, name, "alfrescoDataType", store, Index.ANALYZED, TermVector.NO, multiValued);
                break;
            default:
            }

        }
        else
        {
            writeField(xmlWriter, name, "alfrescoDataType", store, Index.NO, TermVector.NO, multiValued);
        }
    }

    private static void writeField(XMLWriter xmlWriter, String name, String fieldType, Store store, Index index, TermVector termVector, boolean multiValued) throws SAXException
    {
        AttributesImpl attrs = new AttributesImpl();
        attrs.addAttribute("", "name", "name", null, name);
        attrs.addAttribute("", "type", "type", null, fieldType);

        attrs.addAttribute("", "indexed", "indexed", null, Boolean.valueOf(index != Index.NO).toString());
        if ((index == Index.NOT_ANALYZED_NO_NORMS) || (index == Index.ANALYZED_NO_NORMS))
        {
            attrs.addAttribute("", "omitNorms", "omitNorms", null, "true");
        }

        attrs.addAttribute("", "stored", "stored", null, Boolean.valueOf(store != Store.NO).toString());

        attrs.addAttribute("", "multiValued", "multiValued", null, Boolean.valueOf(multiValued).toString());

        xmlWriter.startElement("", "field", "field", attrs);
        xmlWriter.endElement("", "field", "field");
    }
}
