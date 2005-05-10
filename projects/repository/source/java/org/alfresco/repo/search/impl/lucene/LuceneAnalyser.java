/*
 * Created on 04-Apr-2005
 */
package org.alfresco.repo.search.impl.lucene;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.dictionary.DictionaryRef;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.impl.lucene.analysis.PathAnalyser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * Analyse properties according to the property definition.
 * 
 * The default is to use the standard tokeniser. The tokeniser should not have
 * been called when indexeing properties that require no tokenisation. (tokenise
 * should be set to false when adding the field to the document)
 * 
 * @author andyh
 * 
 */

public class LuceneAnalyser extends Analyzer
{

    private DictionaryService dictionaryService;

    private Analyzer defaultAnalyser;

    private Map<String, Analyzer> analysers = new HashMap<String, Analyzer>();

    /**
     * Constructs with a default standard analyser
     * 
     * @param defaultAnalyzer
     *            Any fields not specifically defined to use a different
     *            analyzer will use the one provided here.
     */
    public LuceneAnalyser(DictionaryService dictionaryService)
    {
        this(new StandardAnalyzer());
        this.dictionaryService = dictionaryService;
    }

    /**
     * Constructs with default analyzer.
     * 
     * @param defaultAnalyzer
     *            Any fields not specifically defined to use a different
     *            analyzer will use the one provided here.
     */
    public LuceneAnalyser(Analyzer defaultAnalyser)
    {
        this.defaultAnalyser = defaultAnalyser;
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        Analyzer analyser = (Analyzer) analysers.get(fieldName);
        if (analyser == null)
        {
            analyser = findAnalyser(fieldName);
        }
        return analyser.tokenStream(fieldName, reader);
    }

    private Analyzer findAnalyser(String fieldName)
    {
        Analyzer analyser;
        if (fieldName.equals("PATH"))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals("QNAME"))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals("TYPE"))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals("ASPECT"))
        {
            analyser = new PathAnalyser();
        }
        else if (fieldName.equals("ANCESTOR"))
        {
            analyser = new WhitespaceAnalyzer();
        }
        else if (fieldName.startsWith("@"))
        {
            QName propertyQName = QName.createQName(fieldName.substring(1));
            PropertyDefinition propertyDefinition = dictionaryService.getProperty(propertyQName);
            PropertyTypeDefinition propertyType = dictionaryService.getPropertyType(new DictionaryRef(PropertyTypeDefinition.TEXT));
            if(propertyDefinition != null)
            {
               propertyType = propertyDefinition.getPropertyType();
            }
            String analyserClassName = propertyType.getAnalyserClassName();
            try
            {
                Class<?> clazz = Class.forName(analyserClassName);
                analyser = (Analyzer)clazz.newInstance();
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Unable to load analyser for type "+propertyDefinition.getQName() + " of type "+propertyType.getName()+ " using "+analyserClassName);
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException("Unable to load analyser for type "+propertyDefinition.getQName() + " of type "+propertyType.getName()+ " using "+analyserClassName);   
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException("Unable to load analyser for type "+propertyDefinition.getQName() + " of type "+propertyType.getName()+ " using "+analyserClassName);
            }
        }
        else
        {
            analyser = defaultAnalyser;
        }
        analysers.put(fieldName, analyser);
        return analyser;
    }
}
