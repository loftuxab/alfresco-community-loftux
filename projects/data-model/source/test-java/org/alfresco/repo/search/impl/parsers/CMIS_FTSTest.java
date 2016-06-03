package org.alfresco.repo.search.impl.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.gunit.GrammarInfo;
import org.antlr.gunit.gUnitLexer;
import org.antlr.gunit.gUnitParser;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

public class CMIS_FTSTest extends TestCase
{
    public CMIS_FTSTest()
    {
        // TODO Auto-generated constructor stub
    }

    public CMIS_FTSTest(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

    }
    
    public void testLexer() throws IOException, RecognitionException
    {
        ClassLoader cl = CMIS_FTSTest.class.getClassLoader();
        InputStream modelStream = cl.getResourceAsStream("org/alfresco/repo/search/impl/parsers/cmis_fts_test.gunit");

        CharStream input = new ANTLRInputStream(modelStream);
        
        
        gUnitExecutor executer = new gUnitExecutor(parse(input), "FTS");
        
        System.out.print(executer.execTest());  // unit test result
        
        assertEquals("Failures ", 0, executer.failures.size()); 
        assertEquals("Invalids ", 0, executer.invalids.size()); 
    }

    public void testLexerOutput() throws IOException
    {
        String str = "~woof^2";
        CharStream input = new ANTLRInputStream(new ByteArrayInputStream(str.getBytes("UTF-8")));
        FTSLexer lexer = new FTSLexer(input);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        for(CommonToken token : (List<CommonToken>)tokenStream.getTokens())
        {
            System.out.println(token.toString());
        }
        
    }
    
    private GrammarInfo parse(CharStream input) throws RecognitionException
    {
        gUnitLexer lexer = new gUnitLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        GrammarInfo grammarInfo = new GrammarInfo();
        gUnitParser parser = new gUnitParser(tokens, grammarInfo);
        parser.gUnitDef(); // parse gunit script and save elements to grammarInfo
        return grammarInfo;
    }

    
}
