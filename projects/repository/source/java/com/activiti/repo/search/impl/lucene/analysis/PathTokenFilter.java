/*
 * Created on Mar 16, 2005
 * 
 * TODO Comment this class
 * 
 *  
 */
package com.activiti.repo.search.impl.lucene.analysis;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * @author andyh
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class PathTokenFilter extends TokenFilter
{
   public final static String INTEGER_FORMAT = "0000000000";
   
   public final static String PATH_SEPARATOR = ";";
   
   public final static String NAMESPACE_START_DELIMITER = ":";
   
   public final static String NAMESPACE_END_DELIMITER = ":";

   public final static String SEPARATOR_TOKEN_TEXT = " ";

   public final static String NO_NS_TOKEN_TEXT = "<No Namespace>";

   public final static String TOKEN_TYPE_PATH_SEP = "PATH_SEPARATOR";

   public final static String TOKEN_TYPE_PATH_LENGTH = "PATH_LENGTH";

   public final static String TOKEN_TYPE_PATH_ELEMENT_NAME = "PATH_ELEMENT_NAME";

   public final static String TOKEN_TYPE_PATH_ELEMENT_NAMESPACE = "PATH_ELEMENT_NAMESPACE";

   String pathSeparator;

   String separatorTokenText;

   String noNsTokenText;

   String nsStartDelimiter;
   
   int nsStartDelimiterLength;
   
   String nsEndDelimiter;
   
   int nsEndDelimiterLength;

   LinkedList<Token> tokens = new LinkedList<Token>();

   Iterator<Token> it = null;

 
   private boolean includeNamespace;

   public PathTokenFilter(TokenStream in, String pathSeparator, String separatorTokenText, String noNsTokenText,
         String nsStartDelimiter, String nsEndDelimiter, boolean includeNameSpace)
   {
      super(in);
      this.pathSeparator = pathSeparator;
      this.separatorTokenText = separatorTokenText;
      this.noNsTokenText = noNsTokenText;
      this.nsStartDelimiter = nsStartDelimiter;
      this.nsEndDelimiter = nsEndDelimiter;
      this.includeNamespace = includeNameSpace;
      
      this.nsStartDelimiterLength = nsStartDelimiter.length();
      this.nsEndDelimiterLength = nsEndDelimiter.length();
      
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.apache.lucene.analysis.TokenStream#next()
    */

   public Token next() throws IOException
   {
      Token nextToken;
      if (it == null)
      {
         buildTokenListAndIterator();
      }
      if (it.hasNext())
      {
         nextToken = it.next();
      }
      else
      {
         nextToken = null;
      }
      return nextToken;
   }

   private void buildTokenListAndIterator() throws IOException
   {
      NumberFormat nf = new DecimalFormat(INTEGER_FORMAT);

      // Could optimise to read each path ata time - not just all paths
      int insertCountAt = 0;
      int lengthCounter = 0;
      Token t;
      Token pathSplitToken = null;
      Token nameToken = null;
      Token countToken = null;
      Token namespaceToken = null;
      while ((t = input.next()) != null)
      {
         String text = t.termText();
         if (text.endsWith(pathSeparator))
         {
            text = text.substring(0, text.length() - 1);
            pathSplitToken = new Token(separatorTokenText, t.startOffset(), t.endOffset(), TOKEN_TYPE_PATH_SEP);
            pathSplitToken.setPositionIncrement(1);

         }

         int split = -1;
         
         if(text.startsWith(nsStartDelimiter))
         {
            split = text.indexOf(nsEndDelimiter);
         }
         if (split == -1)
         {
            namespaceToken = new Token(noNsTokenText, t.startOffset(), t.startOffset(),
                  TOKEN_TYPE_PATH_ELEMENT_NAMESPACE);
            nameToken = new Token(text, t.startOffset(), t.endOffset(), TOKEN_TYPE_PATH_ELEMENT_NAME);

         }
         else
         {
            namespaceToken = new Token(text.substring(nsStartDelimiterLength, (split + nsEndDelimiterLength - 1)), t.startOffset(), t.startOffset() + split,
                  TOKEN_TYPE_PATH_ELEMENT_NAMESPACE);
            nameToken = new Token(text.substring(split + nsEndDelimiterLength), t.startOffset() + split + nsEndDelimiterLength, t.endOffset(),
                  TOKEN_TYPE_PATH_ELEMENT_NAME);
         }

         namespaceToken.setPositionIncrement(1);
         nameToken.setPositionIncrement(1);

         
         if(includeNamespace)
         {
            tokens.add(namespaceToken);
         }
         tokens.add(nameToken);

         lengthCounter++;

         if (pathSplitToken != null)
         {

            String countString = nf.format(lengthCounter);
            countToken = new Token(countString, t.startOffset(), t.endOffset(), TOKEN_TYPE_PATH_SEP);
            countToken.setPositionIncrement(1);

            tokens.add(insertCountAt, countToken);
            tokens.add(pathSplitToken);

            lengthCounter = 0;
            insertCountAt = tokens.size();

            pathSplitToken = null;
         }

      }

      String countString = nf.format(lengthCounter);
      countToken = new Token(countString, 0,0, TOKEN_TYPE_PATH_SEP);
      countToken.setPositionIncrement(1);

      tokens.add(insertCountAt, countToken);
      
      it = tokens.iterator();
   }
}