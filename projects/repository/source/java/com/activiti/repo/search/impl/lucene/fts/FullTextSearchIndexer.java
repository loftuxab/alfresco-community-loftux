/*
 * Created on 18-Apr-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl.lucene.fts;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class FullTextSearchIndexer
{

    public FullTextSearchIndexer()
    {
        super();
    }

    public void index()
    {
        System.out.println("Indexer called");
    }
    
    
    public static void main(String[] args) throws InterruptedException
    {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    }
}
