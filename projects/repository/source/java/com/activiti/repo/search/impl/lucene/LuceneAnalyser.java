/*
 * Created on 04-Apr-2005
 */
package com.activiti.repo.search.impl.lucene;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.activiti.repo.search.impl.lucene.analysis.PathAnalyser;

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

   private Analyzer defaultAnalyser;

   private Map<String, Analyzer> analysers = new HashMap<String, Analyzer>();

   /**
    * Constructs with a default standard analyser
    * 
    * @param defaultAnalyzer
    *           Any fields not specifically defined to use a different analyzer
    *           will use the one provided here.
    */
   public LuceneAnalyser()
   {
      this(new StandardAnalyzer());
   }

   /**
    * Constructs with default analyzer.
    * 
    * @param defaultAnalyzer
    *           Any fields not specifically defined to use a different analyzer
    *           will use the one provided here.
    */
   public LuceneAnalyser(Analyzer defaultAnalyser)
   {
      this.defaultAnalyser = defaultAnalyser;
   }
   
   public TokenStream tokenStream(String fieldName, Reader reader)
   {
      Analyzer analyser = (Analyzer) analysers.get(fieldName);
      if(analyser == null)
      {
        analyser = findAnalyser(fieldName);
      }
      return analyser.tokenStream(fieldName, reader);
   }

   private Analyzer findAnalyser(String fieldName)
   {
      Analyzer analyser;
      if(fieldName.equals("PATH"))
      {
         analyser = new PathAnalyser();
      }
      else
      {
         analyser = defaultAnalyser;
      }
      analysers.put(fieldName, analyser);
      return analyser;
   }
   
}
