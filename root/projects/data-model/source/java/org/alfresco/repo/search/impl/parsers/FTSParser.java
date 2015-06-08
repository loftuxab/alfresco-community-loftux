// $ANTLR 3.5.2 W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g 2015-06-06 12:26:21

package org.alfresco.repo.search.impl.parsers;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;


@SuppressWarnings("all")
public class FTSParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "AMP", "AND", "AT", "BAR", "BOOST", 
		"CARAT", "COLON", "COMMA", "CONJUNCTION", "DATETIME", "DAY", "DECIMAL_INTEGER_LITERAL", 
		"DECIMAL_NUMERAL", "DEFAULT", "DIGIT", "DISJUNCTION", "DOLLAR", "DOT", 
		"DOTDOT", "E", "EQUALS", "EXACT_PHRASE", "EXACT_TERM", "EXCLAMATION", 
		"EXCLUDE", "EXCLUSIVE", "EXPONENT", "FG_EXACT_PHRASE", "FG_EXACT_TERM", 
		"FG_PHRASE", "FG_PROXIMITY", "FG_RANGE", "FG_SYNONYM", "FG_TERM", "FIELD_CONJUNCTION", 
		"FIELD_DEFAULT", "FIELD_DISJUNCTION", "FIELD_EXCLUDE", "FIELD_GROUP", 
		"FIELD_MANDATORY", "FIELD_NEGATION", "FIELD_OPTIONAL", "FIELD_REF", "FLOATING_POINT_LITERAL", 
		"FS", "FTS", "FTSPHRASE", "FTSPRE", "FTSWILD", "FTSWORD", "FUZZY", "F_ESC", 
		"F_HEX", "F_URI_ALPHA", "F_URI_DIGIT", "F_URI_ESC", "F_URI_OTHER", "GT", 
		"HOUR", "ID", "INCLUSIVE", "IN_WORD", "LCURL", "LPAREN", "LSQUARE", "LT", 
		"MANDATORY", "MILLIS", "MINUS", "MINUTE", "MONTH", "NAME_SPACE", "NEGATION", 
		"NON_ZERO_DIGIT", "NOT", "NOW", "OPTIONAL", "OR", "PERCENT", "PHRASE", 
		"PLUS", "PREFIX", "PROXIMITY", "QUALIFIER", "QUESTION_MARK", "RANGE", 
		"RCURL", "RPAREN", "RSQUARE", "SECOND", "SIGNED_INTEGER", "SPECIFICDATETIME", 
		"STAR", "START_WORD", "SYNONYM", "TEMPLATE", "TERM", "TILDA", "TO", "UNIT", 
		"URI", "WS", "YEAR", "ZERO_DIGIT"
	};
	public static final int EOF=-1;
	public static final int AMP=4;
	public static final int AND=5;
	public static final int AT=6;
	public static final int BAR=7;
	public static final int BOOST=8;
	public static final int CARAT=9;
	public static final int COLON=10;
	public static final int COMMA=11;
	public static final int CONJUNCTION=12;
	public static final int DATETIME=13;
	public static final int DAY=14;
	public static final int DECIMAL_INTEGER_LITERAL=15;
	public static final int DECIMAL_NUMERAL=16;
	public static final int DEFAULT=17;
	public static final int DIGIT=18;
	public static final int DISJUNCTION=19;
	public static final int DOLLAR=20;
	public static final int DOT=21;
	public static final int DOTDOT=22;
	public static final int E=23;
	public static final int EQUALS=24;
	public static final int EXACT_PHRASE=25;
	public static final int EXACT_TERM=26;
	public static final int EXCLAMATION=27;
	public static final int EXCLUDE=28;
	public static final int EXCLUSIVE=29;
	public static final int EXPONENT=30;
	public static final int FG_EXACT_PHRASE=31;
	public static final int FG_EXACT_TERM=32;
	public static final int FG_PHRASE=33;
	public static final int FG_PROXIMITY=34;
	public static final int FG_RANGE=35;
	public static final int FG_SYNONYM=36;
	public static final int FG_TERM=37;
	public static final int FIELD_CONJUNCTION=38;
	public static final int FIELD_DEFAULT=39;
	public static final int FIELD_DISJUNCTION=40;
	public static final int FIELD_EXCLUDE=41;
	public static final int FIELD_GROUP=42;
	public static final int FIELD_MANDATORY=43;
	public static final int FIELD_NEGATION=44;
	public static final int FIELD_OPTIONAL=45;
	public static final int FIELD_REF=46;
	public static final int FLOATING_POINT_LITERAL=47;
	public static final int FS=48;
	public static final int FTS=49;
	public static final int FTSPHRASE=50;
	public static final int FTSPRE=51;
	public static final int FTSWILD=52;
	public static final int FTSWORD=53;
	public static final int FUZZY=54;
	public static final int F_ESC=55;
	public static final int F_HEX=56;
	public static final int F_URI_ALPHA=57;
	public static final int F_URI_DIGIT=58;
	public static final int F_URI_ESC=59;
	public static final int F_URI_OTHER=60;
	public static final int GT=61;
	public static final int HOUR=62;
	public static final int ID=63;
	public static final int INCLUSIVE=64;
	public static final int IN_WORD=65;
	public static final int LCURL=66;
	public static final int LPAREN=67;
	public static final int LSQUARE=68;
	public static final int LT=69;
	public static final int MANDATORY=70;
	public static final int MILLIS=71;
	public static final int MINUS=72;
	public static final int MINUTE=73;
	public static final int MONTH=74;
	public static final int NAME_SPACE=75;
	public static final int NEGATION=76;
	public static final int NON_ZERO_DIGIT=77;
	public static final int NOT=78;
	public static final int NOW=79;
	public static final int OPTIONAL=80;
	public static final int OR=81;
	public static final int PERCENT=82;
	public static final int PHRASE=83;
	public static final int PLUS=84;
	public static final int PREFIX=85;
	public static final int PROXIMITY=86;
	public static final int QUALIFIER=87;
	public static final int QUESTION_MARK=88;
	public static final int RANGE=89;
	public static final int RCURL=90;
	public static final int RPAREN=91;
	public static final int RSQUARE=92;
	public static final int SECOND=93;
	public static final int SIGNED_INTEGER=94;
	public static final int SPECIFICDATETIME=95;
	public static final int STAR=96;
	public static final int START_WORD=97;
	public static final int SYNONYM=98;
	public static final int TEMPLATE=99;
	public static final int TERM=100;
	public static final int TILDA=101;
	public static final int TO=102;
	public static final int UNIT=103;
	public static final int URI=104;
	public static final int WS=105;
	public static final int YEAR=106;
	public static final int ZERO_DIGIT=107;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators


	public FTSParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}
	public FTSParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return FTSParser.tokenNames; }
	@Override public String getGrammarFileName() { return "W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g"; }


	    public enum Mode
	    {
	        CMIS, DEFAULT_CONJUNCTION, DEFAULT_DISJUNCTION
	    }
	    
	    private Stack<String> paraphrases = new Stack<String>();
	    
	    private boolean defaultFieldConjunction = true;
	    
	    private Mode mode = Mode.DEFAULT_CONJUNCTION;
	    
	    public Mode getMode()
	    {
	       return mode;
	    }
	    
	    public void setMode(Mode mode)
	    {
	       this.mode = mode;
	    }
	    
	    public boolean defaultFieldConjunction()
	    {
	       return defaultFieldConjunction;
	    }
	    
	    public void setDefaultFieldConjunction(boolean defaultFieldConjunction)
	    {
	       this.defaultFieldConjunction = defaultFieldConjunction;
	    }
	    
	    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException
	    {
	        throw new MismatchedTokenException(ttype, input);
	    }
	        
	    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow) throws RecognitionException
	    {
	        throw e;
	    }
	    
	   public String getErrorMessage(RecognitionException e, String[] tokenNames) 
	    {
	        List stack = getRuleInvocationStack(e, this.getClass().getName());
	        String msg = e.getMessage();
	        if ( e instanceof UnwantedTokenException ) 
	            {
	            UnwantedTokenException ute = (UnwantedTokenException)e;
	            String tokenName="<unknown>";
	            if ( ute.expecting== Token.EOF ) 
	            {
	                tokenName = "EOF";
	            }
	            else 
	            {
	                tokenName = tokenNames[ute.expecting];
	            }
	            msg = "extraneous input " + getTokenErrorDisplay(ute.getUnexpectedToken())
	                + " expecting "+tokenName;
	        }
	        else if ( e instanceof MissingTokenException ) 
	        {
	            MissingTokenException mte = (MissingTokenException)e;
	            String tokenName="<unknown>";
	            if ( mte.expecting== Token.EOF ) 
	            {
	                tokenName = "EOF";
	            }
	            else 
	            {
	                tokenName = tokenNames[mte.expecting];
	            }
	            msg = "missing " + tokenName+" at " + getTokenErrorDisplay(e.token)
	                + "  (" + getLongTokenErrorDisplay(e.token) +")";
	        }
	        else if ( e instanceof MismatchedTokenException ) 
	        {
	            MismatchedTokenException mte = (MismatchedTokenException)e;
	            String tokenName="<unknown>";
	            if ( mte.expecting== Token.EOF ) 
	            {
	                tokenName = "EOF";
	            }
	            else
	            {
	                tokenName = tokenNames[mte.expecting];
	            }
	            msg = "mismatched input " + getTokenErrorDisplay(e.token)
	                + " expecting " + tokenName +"  (" + getLongTokenErrorDisplay(e.token) + ")";
	        }
	        else if ( e instanceof MismatchedTreeNodeException ) 
	        {
	            MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
	            String tokenName="<unknown>";
	            if ( mtne.expecting==Token.EOF )  
	            {
	                tokenName = "EOF";
	            }
	            else 
	            {
	                tokenName = tokenNames[mtne.expecting];
	            }
	            msg = "mismatched tree node: " + mtne.node + " expecting " + tokenName;
	        }
	        else if ( e instanceof NoViableAltException ) 
	        {
	            NoViableAltException nvae = (NoViableAltException)e;
	            msg = "no viable alternative at input " + getTokenErrorDisplay(e.token)
	                + "\n\t (decision=" + nvae.decisionNumber
	                + " state " + nvae.stateNumber + ")" 
	                + " decision=<<" + nvae.grammarDecisionDescription + ">>";
	        }
	        else if ( e instanceof EarlyExitException ) 
	        {
	            //EarlyExitException eee = (EarlyExitException)e;
	            // for development, can add "(decision="+eee.decisionNumber+")"
	            msg = "required (...)+ loop did not match anything at input " + getTokenErrorDisplay(e.token);
	        }
	            else if ( e instanceof MismatchedSetException ) 
	            {
	                MismatchedSetException mse = (MismatchedSetException)e;
	                msg = "mismatched input " + getTokenErrorDisplay(e.token)
	                + " expecting set " + mse.expecting;
	        }
	        else if ( e instanceof MismatchedNotSetException ) 
	        {
	            MismatchedNotSetException mse = (MismatchedNotSetException)e;
	            msg = "mismatched input " + getTokenErrorDisplay(e.token)
	                + " expecting set " + mse.expecting;
	        }
	        else if ( e instanceof FailedPredicateException ) 
	        {
	            FailedPredicateException fpe = (FailedPredicateException)e;
	            msg = "rule " + fpe.ruleName + " failed predicate: {" + fpe.predicateText + "}?";
	        }
	                
	        if(paraphrases.size() > 0)
	        {
	            String paraphrase = (String)paraphrases.peek();
	            msg = msg+" "+paraphrase;
	        }
	        return msg +"\n\t"+stack;
	    }
	        
	    public String getLongTokenErrorDisplay(Token t)
	    {
	        return t.toString();
	    }
	    

	    public String getErrorString(RecognitionException e)
	    {
	        String hdr = getErrorHeader(e);
	        String msg = getErrorMessage(e, this.getTokenNames());
	        return hdr+" "+msg;
	    } 


	public static class ftsQuery_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsQuery"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:342:1: ftsQuery : ftsDisjunction EOF -> ftsDisjunction ;
	public final FTSParser.ftsQuery_return ftsQuery() throws RecognitionException {
		FTSParser.ftsQuery_return retval = new FTSParser.ftsQuery_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EOF2=null;
		ParserRuleReturnScope ftsDisjunction1 =null;

		Object EOF2_tree=null;
		RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
		RewriteRuleSubtreeStream stream_ftsDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsDisjunction");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:343:9: ( ftsDisjunction EOF -> ftsDisjunction )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:344:9: ftsDisjunction EOF
			{
			pushFollow(FOLLOW_ftsDisjunction_in_ftsQuery577);
			ftsDisjunction1=ftsDisjunction();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsDisjunction.add(ftsDisjunction1.getTree());
			EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_ftsQuery579); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EOF.add(EOF2);

			// AST REWRITE
			// elements: ftsDisjunction
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 345:17: -> ftsDisjunction
			{
				adaptor.addChild(root_0, stream_ftsDisjunction.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsQuery"


	public static class ftsDisjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsDisjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:353:1: ftsDisjunction : ({...}? cmisExplicitDisjunction |{...}? ftsExplicitDisjunction |{...}? ftsImplicitDisjunction );
	public final FTSParser.ftsDisjunction_return ftsDisjunction() throws RecognitionException {
		FTSParser.ftsDisjunction_return retval = new FTSParser.ftsDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope cmisExplicitDisjunction3 =null;
		ParserRuleReturnScope ftsExplicitDisjunction4 =null;
		ParserRuleReturnScope ftsImplicitDisjunction5 =null;


		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:354:9: ({...}? cmisExplicitDisjunction |{...}? ftsExplicitDisjunction |{...}? ftsImplicitDisjunction )
			int alt1=3;
			switch ( input.LA(1) ) {
			case NOT:
				{
				int LA1_1 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FTSPHRASE:
				{
				int LA1_2 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case MINUS:
				{
				int LA1_3 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case AND:
				{
				int LA1_4 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case AMP:
				{
				alt1=2;
				}
				break;
			case ID:
				{
				int LA1_6 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EXCLAMATION:
				{
				int LA1_7 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STAR:
				{
				int LA1_8 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case AT:
				{
				int LA1_9 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TO:
				{
				int LA1_10 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case DATETIME:
			case DECIMAL_INTEGER_LITERAL:
			case FLOATING_POINT_LITERAL:
			case FTSPRE:
			case FTSWILD:
			case FTSWORD:
				{
				int LA1_11 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case OR:
				{
				int LA1_12 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case URI:
				{
				int LA1_13 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case QUESTION_MARK:
				{
				int LA1_14 = input.LA(2);
				if ( ((getMode() == Mode.CMIS)) ) {
					alt1=1;
				}
				else if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LSQUARE:
				{
				int LA1_15 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 15, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LT:
				{
				int LA1_16 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 16, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EQUALS:
				{
				int LA1_17 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 17, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TILDA:
				{
				int LA1_18 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 18, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LPAREN:
				{
				int LA1_19 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 19, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PERCENT:
				{
				int LA1_20 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 20, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PLUS:
				{
				int LA1_21 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 21, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BAR:
				{
				int LA1_22 = input.LA(2);
				if ( ((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
					alt1=2;
				}
				else if ( ((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
					alt1=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 1, 22, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 1, 0, input);
				throw nvae;
			}
			switch (alt1) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:355:11: {...}? cmisExplicitDisjunction
					{
					root_0 = (Object)adaptor.nil();


					if ( !((getMode() == Mode.CMIS)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "ftsDisjunction", "getMode() == Mode.CMIS");
					}
					pushFollow(FOLLOW_cmisExplicitDisjunction_in_ftsDisjunction639);
					cmisExplicitDisjunction3=cmisExplicitDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, cmisExplicitDisjunction3.getTree());

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:356:11: {...}? ftsExplicitDisjunction
					{
					root_0 = (Object)adaptor.nil();


					if ( !((getMode() == Mode.DEFAULT_CONJUNCTION)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "ftsDisjunction", "getMode() == Mode.DEFAULT_CONJUNCTION");
					}
					pushFollow(FOLLOW_ftsExplicitDisjunction_in_ftsDisjunction653);
					ftsExplicitDisjunction4=ftsExplicitDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsExplicitDisjunction4.getTree());

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:357:11: {...}? ftsImplicitDisjunction
					{
					root_0 = (Object)adaptor.nil();


					if ( !((getMode() == Mode.DEFAULT_DISJUNCTION)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "ftsDisjunction", "getMode() == Mode.DEFAULT_DISJUNCTION");
					}
					pushFollow(FOLLOW_ftsImplicitDisjunction_in_ftsDisjunction667);
					ftsImplicitDisjunction5=ftsImplicitDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsImplicitDisjunction5.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsDisjunction"


	public static class ftsExplicitDisjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsExplicitDisjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:360:1: ftsExplicitDisjunction : ftsImplicitConjunction ( or ftsImplicitConjunction )* -> ^( DISJUNCTION ( ftsImplicitConjunction )+ ) ;
	public final FTSParser.ftsExplicitDisjunction_return ftsExplicitDisjunction() throws RecognitionException {
		FTSParser.ftsExplicitDisjunction_return retval = new FTSParser.ftsExplicitDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsImplicitConjunction6 =null;
		ParserRuleReturnScope or7 =null;
		ParserRuleReturnScope ftsImplicitConjunction8 =null;

		RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
		RewriteRuleSubtreeStream stream_ftsImplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsImplicitConjunction");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:361:9: ( ftsImplicitConjunction ( or ftsImplicitConjunction )* -> ^( DISJUNCTION ( ftsImplicitConjunction )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:362:9: ftsImplicitConjunction ( or ftsImplicitConjunction )*
			{
			pushFollow(FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction700);
			ftsImplicitConjunction6=ftsImplicitConjunction();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsImplicitConjunction.add(ftsImplicitConjunction6.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:362:32: ( or ftsImplicitConjunction )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==BAR||LA2_0==OR) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:362:33: or ftsImplicitConjunction
					{
					pushFollow(FOLLOW_or_in_ftsExplicitDisjunction703);
					or7=or();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_or.add(or7.getTree());
					pushFollow(FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction705);
					ftsImplicitConjunction8=ftsImplicitConjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsImplicitConjunction.add(ftsImplicitConjunction8.getTree());
					}
					break;

				default :
					break loop2;
				}
			}

			// AST REWRITE
			// elements: ftsImplicitConjunction
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 363:17: -> ^( DISJUNCTION ( ftsImplicitConjunction )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:364:25: ^( DISJUNCTION ( ftsImplicitConjunction )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);
				if ( !(stream_ftsImplicitConjunction.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsImplicitConjunction.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsImplicitConjunction.nextTree());
				}
				stream_ftsImplicitConjunction.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsExplicitDisjunction"


	public static class cmisExplicitDisjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cmisExplicitDisjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:367:1: cmisExplicitDisjunction : cmisConjunction ( or cmisConjunction )* -> ^( DISJUNCTION ( cmisConjunction )+ ) ;
	public final FTSParser.cmisExplicitDisjunction_return cmisExplicitDisjunction() throws RecognitionException {
		FTSParser.cmisExplicitDisjunction_return retval = new FTSParser.cmisExplicitDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope cmisConjunction9 =null;
		ParserRuleReturnScope or10 =null;
		ParserRuleReturnScope cmisConjunction11 =null;

		RewriteRuleSubtreeStream stream_cmisConjunction=new RewriteRuleSubtreeStream(adaptor,"rule cmisConjunction");
		RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:368:9: ( cmisConjunction ( or cmisConjunction )* -> ^( DISJUNCTION ( cmisConjunction )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:369:9: cmisConjunction ( or cmisConjunction )*
			{
			pushFollow(FOLLOW_cmisConjunction_in_cmisExplicitDisjunction789);
			cmisConjunction9=cmisConjunction();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_cmisConjunction.add(cmisConjunction9.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:369:25: ( or cmisConjunction )*
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==BAR||LA3_0==OR) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:369:26: or cmisConjunction
					{
					pushFollow(FOLLOW_or_in_cmisExplicitDisjunction792);
					or10=or();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_or.add(or10.getTree());
					pushFollow(FOLLOW_cmisConjunction_in_cmisExplicitDisjunction794);
					cmisConjunction11=cmisConjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_cmisConjunction.add(cmisConjunction11.getTree());
					}
					break;

				default :
					break loop3;
				}
			}

			// AST REWRITE
			// elements: cmisConjunction
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 370:17: -> ^( DISJUNCTION ( cmisConjunction )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:371:25: ^( DISJUNCTION ( cmisConjunction )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);
				if ( !(stream_cmisConjunction.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_cmisConjunction.hasNext() ) {
					adaptor.addChild(root_1, stream_cmisConjunction.nextTree());
				}
				stream_cmisConjunction.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cmisExplicitDisjunction"


	public static class ftsImplicitDisjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsImplicitDisjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:374:1: ftsImplicitDisjunction : ( ( or )? ftsExplicitConjunction )+ -> ^( DISJUNCTION ( ftsExplicitConjunction )+ ) ;
	public final FTSParser.ftsImplicitDisjunction_return ftsImplicitDisjunction() throws RecognitionException {
		FTSParser.ftsImplicitDisjunction_return retval = new FTSParser.ftsImplicitDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope or12 =null;
		ParserRuleReturnScope ftsExplicitConjunction13 =null;

		RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
		RewriteRuleSubtreeStream stream_ftsExplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsExplicitConjunction");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:375:9: ( ( ( or )? ftsExplicitConjunction )+ -> ^( DISJUNCTION ( ftsExplicitConjunction )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:376:9: ( ( or )? ftsExplicitConjunction )+
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:376:9: ( ( or )? ftsExplicitConjunction )+
			int cnt5=0;
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( ((LA5_0 >= AND && LA5_0 <= BAR)||LA5_0==DATETIME||LA5_0==DECIMAL_INTEGER_LITERAL||LA5_0==EQUALS||LA5_0==EXCLAMATION||LA5_0==FLOATING_POINT_LITERAL||(LA5_0 >= FTSPHRASE && LA5_0 <= FTSWORD)||LA5_0==ID||(LA5_0 >= LPAREN && LA5_0 <= LT)||LA5_0==MINUS||LA5_0==NOT||(LA5_0 >= OR && LA5_0 <= PERCENT)||LA5_0==PLUS||LA5_0==QUESTION_MARK||LA5_0==STAR||(LA5_0 >= TILDA && LA5_0 <= TO)||LA5_0==URI) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:376:10: ( or )? ftsExplicitConjunction
					{
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:376:10: ( or )?
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==OR) ) {
						int LA4_1 = input.LA(2);
						if ( ((LA4_1 >= AND && LA4_1 <= BAR)||LA4_1==DATETIME||LA4_1==DECIMAL_INTEGER_LITERAL||LA4_1==EQUALS||LA4_1==EXCLAMATION||LA4_1==FLOATING_POINT_LITERAL||(LA4_1 >= FTSPHRASE && LA4_1 <= FTSWORD)||LA4_1==ID||(LA4_1 >= LPAREN && LA4_1 <= LT)||LA4_1==MINUS||LA4_1==NOT||(LA4_1 >= OR && LA4_1 <= PERCENT)||LA4_1==PLUS||LA4_1==QUESTION_MARK||LA4_1==STAR||(LA4_1 >= TILDA && LA4_1 <= TO)||LA4_1==URI) ) {
							alt4=1;
						}
					}
					else if ( (LA4_0==BAR) ) {
						int LA4_2 = input.LA(2);
						if ( (LA4_2==BAR) ) {
							alt4=1;
						}
					}
					switch (alt4) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:376:10: or
							{
							pushFollow(FOLLOW_or_in_ftsImplicitDisjunction879);
							or12=or();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_or.add(or12.getTree());
							}
							break;

					}

					pushFollow(FOLLOW_ftsExplicitConjunction_in_ftsImplicitDisjunction882);
					ftsExplicitConjunction13=ftsExplicitConjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsExplicitConjunction.add(ftsExplicitConjunction13.getTree());
					}
					break;

				default :
					if ( cnt5 >= 1 ) break loop5;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(5, input);
					throw eee;
				}
				cnt5++;
			}

			// AST REWRITE
			// elements: ftsExplicitConjunction
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 377:17: -> ^( DISJUNCTION ( ftsExplicitConjunction )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:378:25: ^( DISJUNCTION ( ftsExplicitConjunction )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DISJUNCTION, "DISJUNCTION"), root_1);
				if ( !(stream_ftsExplicitConjunction.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsExplicitConjunction.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsExplicitConjunction.nextTree());
				}
				stream_ftsExplicitConjunction.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsImplicitDisjunction"


	public static class ftsExplicitConjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsExplicitConjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:385:1: ftsExplicitConjunction : ftsPrefixed ( and ftsPrefixed )* -> ^( CONJUNCTION ( ftsPrefixed )+ ) ;
	public final FTSParser.ftsExplicitConjunction_return ftsExplicitConjunction() throws RecognitionException {
		FTSParser.ftsExplicitConjunction_return retval = new FTSParser.ftsExplicitConjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsPrefixed14 =null;
		ParserRuleReturnScope and15 =null;
		ParserRuleReturnScope ftsPrefixed16 =null;

		RewriteRuleSubtreeStream stream_ftsPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsPrefixed");
		RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:386:9: ( ftsPrefixed ( and ftsPrefixed )* -> ^( CONJUNCTION ( ftsPrefixed )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:387:9: ftsPrefixed ( and ftsPrefixed )*
			{
			pushFollow(FOLLOW_ftsPrefixed_in_ftsExplicitConjunction969);
			ftsPrefixed14=ftsPrefixed();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsPrefixed.add(ftsPrefixed14.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:387:21: ( and ftsPrefixed )*
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==AND) ) {
					int LA6_2 = input.LA(2);
					if ( ((LA6_2 >= AND && LA6_2 <= BAR)||LA6_2==DATETIME||LA6_2==DECIMAL_INTEGER_LITERAL||LA6_2==EQUALS||LA6_2==EXCLAMATION||LA6_2==FLOATING_POINT_LITERAL||(LA6_2 >= FTSPHRASE && LA6_2 <= FTSWORD)||LA6_2==ID||(LA6_2 >= LPAREN && LA6_2 <= LT)||LA6_2==MINUS||LA6_2==NOT||(LA6_2 >= OR && LA6_2 <= PERCENT)||LA6_2==PLUS||LA6_2==QUESTION_MARK||LA6_2==STAR||(LA6_2 >= TILDA && LA6_2 <= TO)||LA6_2==URI) ) {
						alt6=1;
					}

				}
				else if ( (LA6_0==AMP) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:387:22: and ftsPrefixed
					{
					pushFollow(FOLLOW_and_in_ftsExplicitConjunction972);
					and15=and();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_and.add(and15.getTree());
					pushFollow(FOLLOW_ftsPrefixed_in_ftsExplicitConjunction974);
					ftsPrefixed16=ftsPrefixed();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsPrefixed.add(ftsPrefixed16.getTree());
					}
					break;

				default :
					break loop6;
				}
			}

			// AST REWRITE
			// elements: ftsPrefixed
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 388:17: -> ^( CONJUNCTION ( ftsPrefixed )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:389:25: ^( CONJUNCTION ( ftsPrefixed )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);
				if ( !(stream_ftsPrefixed.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsPrefixed.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsPrefixed.nextTree());
				}
				stream_ftsPrefixed.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsExplicitConjunction"


	public static class ftsImplicitConjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsImplicitConjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:392:1: ftsImplicitConjunction : ( ( and )? ftsPrefixed )+ -> ^( CONJUNCTION ( ftsPrefixed )+ ) ;
	public final FTSParser.ftsImplicitConjunction_return ftsImplicitConjunction() throws RecognitionException {
		FTSParser.ftsImplicitConjunction_return retval = new FTSParser.ftsImplicitConjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope and17 =null;
		ParserRuleReturnScope ftsPrefixed18 =null;

		RewriteRuleSubtreeStream stream_ftsPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsPrefixed");
		RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:393:9: ( ( ( and )? ftsPrefixed )+ -> ^( CONJUNCTION ( ftsPrefixed )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:394:9: ( ( and )? ftsPrefixed )+
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:394:9: ( ( and )? ftsPrefixed )+
			int cnt8=0;
			loop8:
			while (true) {
				int alt8=2;
				switch ( input.LA(1) ) {
				case OR:
					{
					int LA8_1 = input.LA(2);
					if ( (LA8_1==COLON) ) {
						alt8=1;
					}

					}
					break;
				case BAR:
					{
					int LA8_2 = input.LA(2);
					if ( ((LA8_2 >= AND && LA8_2 <= AT)||LA8_2==DATETIME||LA8_2==DECIMAL_INTEGER_LITERAL||LA8_2==EQUALS||LA8_2==FLOATING_POINT_LITERAL||(LA8_2 >= FTSPHRASE && LA8_2 <= FTSWORD)||LA8_2==ID||(LA8_2 >= LPAREN && LA8_2 <= LT)||LA8_2==NOT||(LA8_2 >= OR && LA8_2 <= PERCENT)||LA8_2==QUESTION_MARK||LA8_2==STAR||(LA8_2 >= TILDA && LA8_2 <= TO)||LA8_2==URI) ) {
						alt8=1;
					}

					}
					break;
				case AMP:
				case AND:
				case AT:
				case DATETIME:
				case DECIMAL_INTEGER_LITERAL:
				case EQUALS:
				case EXCLAMATION:
				case FLOATING_POINT_LITERAL:
				case FTSPHRASE:
				case FTSPRE:
				case FTSWILD:
				case FTSWORD:
				case ID:
				case LPAREN:
				case LSQUARE:
				case LT:
				case MINUS:
				case NOT:
				case PERCENT:
				case PLUS:
				case QUESTION_MARK:
				case STAR:
				case TILDA:
				case TO:
				case URI:
					{
					alt8=1;
					}
					break;
				}
				switch (alt8) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:394:10: ( and )? ftsPrefixed
					{
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:394:10: ( and )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==AND) ) {
						int LA7_1 = input.LA(2);
						if ( ((LA7_1 >= AND && LA7_1 <= BAR)||LA7_1==DATETIME||LA7_1==DECIMAL_INTEGER_LITERAL||LA7_1==EQUALS||LA7_1==EXCLAMATION||LA7_1==FLOATING_POINT_LITERAL||(LA7_1 >= FTSPHRASE && LA7_1 <= FTSWORD)||LA7_1==ID||(LA7_1 >= LPAREN && LA7_1 <= LT)||LA7_1==MINUS||LA7_1==NOT||(LA7_1 >= OR && LA7_1 <= PERCENT)||LA7_1==PLUS||LA7_1==QUESTION_MARK||LA7_1==STAR||(LA7_1 >= TILDA && LA7_1 <= TO)||LA7_1==URI) ) {
							alt7=1;
						}
					}
					else if ( (LA7_0==AMP) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:394:10: and
							{
							pushFollow(FOLLOW_and_in_ftsImplicitConjunction1059);
							and17=and();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_and.add(and17.getTree());
							}
							break;

					}

					pushFollow(FOLLOW_ftsPrefixed_in_ftsImplicitConjunction1062);
					ftsPrefixed18=ftsPrefixed();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsPrefixed.add(ftsPrefixed18.getTree());
					}
					break;

				default :
					if ( cnt8 >= 1 ) break loop8;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(8, input);
					throw eee;
				}
				cnt8++;
			}

			// AST REWRITE
			// elements: ftsPrefixed
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 395:17: -> ^( CONJUNCTION ( ftsPrefixed )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:396:25: ^( CONJUNCTION ( ftsPrefixed )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);
				if ( !(stream_ftsPrefixed.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsPrefixed.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsPrefixed.nextTree());
				}
				stream_ftsPrefixed.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsImplicitConjunction"


	public static class cmisConjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cmisConjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:399:1: cmisConjunction : ( cmisPrefixed )+ -> ^( CONJUNCTION ( cmisPrefixed )+ ) ;
	public final FTSParser.cmisConjunction_return cmisConjunction() throws RecognitionException {
		FTSParser.cmisConjunction_return retval = new FTSParser.cmisConjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope cmisPrefixed19 =null;

		RewriteRuleSubtreeStream stream_cmisPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule cmisPrefixed");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:400:9: ( ( cmisPrefixed )+ -> ^( CONJUNCTION ( cmisPrefixed )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:401:9: ( cmisPrefixed )+
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:401:9: ( cmisPrefixed )+
			int cnt9=0;
			loop9:
			while (true) {
				int alt9=2;
				int LA9_0 = input.LA(1);
				if ( (LA9_0==DATETIME||LA9_0==DECIMAL_INTEGER_LITERAL||LA9_0==FLOATING_POINT_LITERAL||(LA9_0 >= FTSPHRASE && LA9_0 <= FTSWORD)||LA9_0==ID||LA9_0==MINUS||LA9_0==NOT||LA9_0==QUESTION_MARK||LA9_0==STAR||LA9_0==TO) ) {
					alt9=1;
				}

				switch (alt9) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:401:9: cmisPrefixed
					{
					pushFollow(FOLLOW_cmisPrefixed_in_cmisConjunction1146);
					cmisPrefixed19=cmisPrefixed();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_cmisPrefixed.add(cmisPrefixed19.getTree());
					}
					break;

				default :
					if ( cnt9 >= 1 ) break loop9;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(9, input);
					throw eee;
				}
				cnt9++;
			}

			// AST REWRITE
			// elements: cmisPrefixed
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 402:17: -> ^( CONJUNCTION ( cmisPrefixed )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:403:25: ^( CONJUNCTION ( cmisPrefixed )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(CONJUNCTION, "CONJUNCTION"), root_1);
				if ( !(stream_cmisPrefixed.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_cmisPrefixed.hasNext() ) {
					adaptor.addChild(root_1, stream_cmisPrefixed.nextTree());
				}
				stream_cmisPrefixed.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cmisConjunction"


	public static class ftsPrefixed_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsPrefixed"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:413:1: ftsPrefixed : ( ( not )=> not ftsTest ( boost )? -> ^( NEGATION ftsTest ( boost )? ) | ftsTest ( boost )? -> ^( DEFAULT ftsTest ( boost )? ) | PLUS ftsTest ( boost )? -> ^( MANDATORY ftsTest ( boost )? ) | BAR ftsTest ( boost )? -> ^( OPTIONAL ftsTest ( boost )? ) | MINUS ftsTest ( boost )? -> ^( EXCLUDE ftsTest ( boost )? ) );
	public final FTSParser.ftsPrefixed_return ftsPrefixed() throws RecognitionException {
		FTSParser.ftsPrefixed_return retval = new FTSParser.ftsPrefixed_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token PLUS25=null;
		Token BAR28=null;
		Token MINUS31=null;
		ParserRuleReturnScope not20 =null;
		ParserRuleReturnScope ftsTest21 =null;
		ParserRuleReturnScope boost22 =null;
		ParserRuleReturnScope ftsTest23 =null;
		ParserRuleReturnScope boost24 =null;
		ParserRuleReturnScope ftsTest26 =null;
		ParserRuleReturnScope boost27 =null;
		ParserRuleReturnScope ftsTest29 =null;
		ParserRuleReturnScope boost30 =null;
		ParserRuleReturnScope ftsTest32 =null;
		ParserRuleReturnScope boost33 =null;

		Object PLUS25_tree=null;
		Object BAR28_tree=null;
		Object MINUS31_tree=null;
		RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
		RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
		RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
		RewriteRuleSubtreeStream stream_not=new RewriteRuleSubtreeStream(adaptor,"rule not");
		RewriteRuleSubtreeStream stream_ftsTest=new RewriteRuleSubtreeStream(adaptor,"rule ftsTest");
		RewriteRuleSubtreeStream stream_boost=new RewriteRuleSubtreeStream(adaptor,"rule boost");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:414:9: ( ( not )=> not ftsTest ( boost )? -> ^( NEGATION ftsTest ( boost )? ) | ftsTest ( boost )? -> ^( DEFAULT ftsTest ( boost )? ) | PLUS ftsTest ( boost )? -> ^( MANDATORY ftsTest ( boost )? ) | BAR ftsTest ( boost )? -> ^( OPTIONAL ftsTest ( boost )? ) | MINUS ftsTest ( boost )? -> ^( EXCLUDE ftsTest ( boost )? ) )
			int alt15=5;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==NOT) ) {
				int LA15_1 = input.LA(2);
				if ( (synpred1_FTS()) ) {
					alt15=1;
				}
				else if ( (true) ) {
					alt15=2;
				}

			}
			else if ( (LA15_0==EXCLAMATION) && (synpred1_FTS())) {
				alt15=1;
			}
			else if ( ((LA15_0 >= AND && LA15_0 <= AT)||LA15_0==DATETIME||LA15_0==DECIMAL_INTEGER_LITERAL||LA15_0==EQUALS||LA15_0==FLOATING_POINT_LITERAL||(LA15_0 >= FTSPHRASE && LA15_0 <= FTSWORD)||LA15_0==ID||(LA15_0 >= LPAREN && LA15_0 <= LT)||(LA15_0 >= OR && LA15_0 <= PERCENT)||LA15_0==QUESTION_MARK||LA15_0==STAR||(LA15_0 >= TILDA && LA15_0 <= TO)||LA15_0==URI) ) {
				alt15=2;
			}
			else if ( (LA15_0==PLUS) ) {
				alt15=3;
			}
			else if ( (LA15_0==BAR) ) {
				alt15=4;
			}
			else if ( (LA15_0==MINUS) ) {
				alt15=5;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:415:9: ( not )=> not ftsTest ( boost )?
					{
					pushFollow(FOLLOW_not_in_ftsPrefixed1238);
					not20=not();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_not.add(not20.getTree());
					pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1240);
					ftsTest21=ftsTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest21.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:415:30: ( boost )?
					int alt10=2;
					int LA10_0 = input.LA(1);
					if ( (LA10_0==CARAT) ) {
						alt10=1;
					}
					switch (alt10) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:415:30: boost
							{
							pushFollow(FOLLOW_boost_in_ftsPrefixed1242);
							boost22=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost22.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsTest, boost
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 416:17: -> ^( NEGATION ftsTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:417:25: ^( NEGATION ftsTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NEGATION, "NEGATION"), root_1);
						adaptor.addChild(root_1, stream_ftsTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:417:44: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:418:11: ftsTest ( boost )?
					{
					pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1306);
					ftsTest23=ftsTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest23.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:418:19: ( boost )?
					int alt11=2;
					int LA11_0 = input.LA(1);
					if ( (LA11_0==CARAT) ) {
						alt11=1;
					}
					switch (alt11) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:418:19: boost
							{
							pushFollow(FOLLOW_boost_in_ftsPrefixed1308);
							boost24=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost24.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsTest, boost
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 419:17: -> ^( DEFAULT ftsTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:420:25: ^( DEFAULT ftsTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DEFAULT, "DEFAULT"), root_1);
						adaptor.addChild(root_1, stream_ftsTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:420:43: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:421:11: PLUS ftsTest ( boost )?
					{
					PLUS25=(Token)match(input,PLUS,FOLLOW_PLUS_in_ftsPrefixed1372); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PLUS.add(PLUS25);

					pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1374);
					ftsTest26=ftsTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest26.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:421:24: ( boost )?
					int alt12=2;
					int LA12_0 = input.LA(1);
					if ( (LA12_0==CARAT) ) {
						alt12=1;
					}
					switch (alt12) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:421:24: boost
							{
							pushFollow(FOLLOW_boost_in_ftsPrefixed1376);
							boost27=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost27.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: boost, ftsTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 422:17: -> ^( MANDATORY ftsTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:423:25: ^( MANDATORY ftsTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(MANDATORY, "MANDATORY"), root_1);
						adaptor.addChild(root_1, stream_ftsTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:423:45: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:11: BAR ftsTest ( boost )?
					{
					BAR28=(Token)match(input,BAR,FOLLOW_BAR_in_ftsPrefixed1440); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BAR.add(BAR28);

					pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1442);
					ftsTest29=ftsTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest29.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:23: ( boost )?
					int alt13=2;
					int LA13_0 = input.LA(1);
					if ( (LA13_0==CARAT) ) {
						alt13=1;
					}
					switch (alt13) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:424:23: boost
							{
							pushFollow(FOLLOW_boost_in_ftsPrefixed1444);
							boost30=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost30.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: boost, ftsTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 425:17: -> ^( OPTIONAL ftsTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:426:25: ^( OPTIONAL ftsTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(OPTIONAL, "OPTIONAL"), root_1);
						adaptor.addChild(root_1, stream_ftsTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:426:44: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:427:11: MINUS ftsTest ( boost )?
					{
					MINUS31=(Token)match(input,MINUS,FOLLOW_MINUS_in_ftsPrefixed1508); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_MINUS.add(MINUS31);

					pushFollow(FOLLOW_ftsTest_in_ftsPrefixed1510);
					ftsTest32=ftsTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsTest.add(ftsTest32.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:427:25: ( boost )?
					int alt14=2;
					int LA14_0 = input.LA(1);
					if ( (LA14_0==CARAT) ) {
						alt14=1;
					}
					switch (alt14) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:427:25: boost
							{
							pushFollow(FOLLOW_boost_in_ftsPrefixed1512);
							boost33=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost33.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: boost, ftsTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 428:17: -> ^( EXCLUDE ftsTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:429:25: ^( EXCLUDE ftsTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXCLUDE, "EXCLUDE"), root_1);
						adaptor.addChild(root_1, stream_ftsTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:429:43: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsPrefixed"


	public static class cmisPrefixed_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cmisPrefixed"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:432:1: cmisPrefixed : ( cmisTest -> ^( DEFAULT cmisTest ) | MINUS cmisTest -> ^( EXCLUDE cmisTest ) );
	public final FTSParser.cmisPrefixed_return cmisPrefixed() throws RecognitionException {
		FTSParser.cmisPrefixed_return retval = new FTSParser.cmisPrefixed_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token MINUS35=null;
		ParserRuleReturnScope cmisTest34 =null;
		ParserRuleReturnScope cmisTest36 =null;

		Object MINUS35_tree=null;
		RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
		RewriteRuleSubtreeStream stream_cmisTest=new RewriteRuleSubtreeStream(adaptor,"rule cmisTest");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:433:9: ( cmisTest -> ^( DEFAULT cmisTest ) | MINUS cmisTest -> ^( EXCLUDE cmisTest ) )
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==DATETIME||LA16_0==DECIMAL_INTEGER_LITERAL||LA16_0==FLOATING_POINT_LITERAL||(LA16_0 >= FTSPHRASE && LA16_0 <= FTSWORD)||LA16_0==ID||LA16_0==NOT||LA16_0==QUESTION_MARK||LA16_0==STAR||LA16_0==TO) ) {
				alt16=1;
			}
			else if ( (LA16_0==MINUS) ) {
				alt16=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}

			switch (alt16) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:434:9: cmisTest
					{
					pushFollow(FOLLOW_cmisTest_in_cmisPrefixed1597);
					cmisTest34=cmisTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_cmisTest.add(cmisTest34.getTree());
					// AST REWRITE
					// elements: cmisTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 435:17: -> ^( DEFAULT cmisTest )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:436:25: ^( DEFAULT cmisTest )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(DEFAULT, "DEFAULT"), root_1);
						adaptor.addChild(root_1, stream_cmisTest.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:437:11: MINUS cmisTest
					{
					MINUS35=(Token)match(input,MINUS,FOLLOW_MINUS_in_cmisPrefixed1657); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_MINUS.add(MINUS35);

					pushFollow(FOLLOW_cmisTest_in_cmisPrefixed1659);
					cmisTest36=cmisTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_cmisTest.add(cmisTest36.getTree());
					// AST REWRITE
					// elements: cmisTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 438:17: -> ^( EXCLUDE cmisTest )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:439:25: ^( EXCLUDE cmisTest )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXCLUDE, "EXCLUDE"), root_1);
						adaptor.addChild(root_1, stream_cmisTest.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cmisPrefixed"


	public static class ftsTest_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsTest"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:445:1: ftsTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ( ftsRange )=> ftsRange -> ^( RANGE ftsRange ) | ( ftsFieldGroup )=> ftsFieldGroup -> ftsFieldGroup | ( ftsTermOrPhrase )=> ftsTermOrPhrase | ( ftsExactTermOrPhrase )=> ftsExactTermOrPhrase | ( ftsTokenisedTermOrPhrase )=> ftsTokenisedTermOrPhrase | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template );
	public final FTSParser.ftsTest_return ftsTest() throws RecognitionException {
		FTSParser.ftsTest_return retval = new FTSParser.ftsTest_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LPAREN43=null;
		Token RPAREN45=null;
		ParserRuleReturnScope ftsFieldGroupProximity37 =null;
		ParserRuleReturnScope ftsRange38 =null;
		ParserRuleReturnScope ftsFieldGroup39 =null;
		ParserRuleReturnScope ftsTermOrPhrase40 =null;
		ParserRuleReturnScope ftsExactTermOrPhrase41 =null;
		ParserRuleReturnScope ftsTokenisedTermOrPhrase42 =null;
		ParserRuleReturnScope ftsDisjunction44 =null;
		ParserRuleReturnScope template46 =null;

		Object LPAREN43_tree=null;
		Object RPAREN45_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_template=new RewriteRuleSubtreeStream(adaptor,"rule template");
		RewriteRuleSubtreeStream stream_ftsFieldGroup=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroup");
		RewriteRuleSubtreeStream stream_ftsDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsDisjunction");
		RewriteRuleSubtreeStream stream_ftsRange=new RewriteRuleSubtreeStream(adaptor,"rule ftsRange");
		RewriteRuleSubtreeStream stream_ftsFieldGroupProximity=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupProximity");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:446:9: ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ( ftsRange )=> ftsRange -> ^( RANGE ftsRange ) | ( ftsFieldGroup )=> ftsFieldGroup -> ftsFieldGroup | ( ftsTermOrPhrase )=> ftsTermOrPhrase | ( ftsExactTermOrPhrase )=> ftsExactTermOrPhrase | ( ftsTokenisedTermOrPhrase )=> ftsTokenisedTermOrPhrase | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template )
			int alt17=8;
			alt17 = dfa17.predict(input);
			switch (alt17) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:447:12: ( ftsFieldGroupProximity )=> ftsFieldGroupProximity
					{
					pushFollow(FOLLOW_ftsFieldGroupProximity_in_ftsTest1751);
					ftsFieldGroupProximity37=ftsFieldGroupProximity();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupProximity.add(ftsFieldGroupProximity37.getTree());
					// AST REWRITE
					// elements: ftsFieldGroupProximity
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 448:17: -> ^( PROXIMITY ftsFieldGroupProximity )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:449:25: ^( PROXIMITY ftsFieldGroupProximity )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROXIMITY, "PROXIMITY"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupProximity.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:451:12: ( ftsRange )=> ftsRange
					{
					pushFollow(FOLLOW_ftsRange_in_ftsTest1828);
					ftsRange38=ftsRange();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRange.add(ftsRange38.getTree());
					// AST REWRITE
					// elements: ftsRange
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 452:17: -> ^( RANGE ftsRange )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:453:25: ^( RANGE ftsRange )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_1);
						adaptor.addChild(root_1, stream_ftsRange.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:455:12: ( ftsFieldGroup )=> ftsFieldGroup
					{
					pushFollow(FOLLOW_ftsFieldGroup_in_ftsTest1907);
					ftsFieldGroup39=ftsFieldGroup();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroup.add(ftsFieldGroup39.getTree());
					// AST REWRITE
					// elements: ftsFieldGroup
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 456:17: -> ftsFieldGroup
					{
						adaptor.addChild(root_0, stream_ftsFieldGroup.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:458:12: ( ftsTermOrPhrase )=> ftsTermOrPhrase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsTermOrPhrase_in_ftsTest1956);
					ftsTermOrPhrase40=ftsTermOrPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsTermOrPhrase40.getTree());

					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:12: ( ftsExactTermOrPhrase )=> ftsExactTermOrPhrase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsExactTermOrPhrase_in_ftsTest1985);
					ftsExactTermOrPhrase41=ftsExactTermOrPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsExactTermOrPhrase41.getTree());

					}
					break;
				case 6 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:462:12: ( ftsTokenisedTermOrPhrase )=> ftsTokenisedTermOrPhrase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsTokenisedTermOrPhrase_in_ftsTest2015);
					ftsTokenisedTermOrPhrase42=ftsTokenisedTermOrPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsTokenisedTermOrPhrase42.getTree());

					}
					break;
				case 7 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:465:12: LPAREN ftsDisjunction RPAREN
					{
					LPAREN43=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsTest2046); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN43);

					pushFollow(FOLLOW_ftsDisjunction_in_ftsTest2048);
					ftsDisjunction44=ftsDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsDisjunction.add(ftsDisjunction44.getTree());
					RPAREN45=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsTest2050); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN45);

					// AST REWRITE
					// elements: ftsDisjunction
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 466:17: -> ftsDisjunction
					{
						adaptor.addChild(root_0, stream_ftsDisjunction.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 8 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:467:12: template
					{
					pushFollow(FOLLOW_template_in_ftsTest2083);
					template46=template();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_template.add(template46.getTree());
					// AST REWRITE
					// elements: template
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 468:17: -> template
					{
						adaptor.addChild(root_0, stream_template.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsTest"


	public static class cmisTest_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cmisTest"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:471:1: cmisTest : ( cmisTerm -> ^( TERM cmisTerm ) | cmisPhrase -> ^( PHRASE cmisPhrase ) );
	public final FTSParser.cmisTest_return cmisTest() throws RecognitionException {
		FTSParser.cmisTest_return retval = new FTSParser.cmisTest_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope cmisTerm47 =null;
		ParserRuleReturnScope cmisPhrase48 =null;

		RewriteRuleSubtreeStream stream_cmisPhrase=new RewriteRuleSubtreeStream(adaptor,"rule cmisPhrase");
		RewriteRuleSubtreeStream stream_cmisTerm=new RewriteRuleSubtreeStream(adaptor,"rule cmisTerm");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:472:9: ( cmisTerm -> ^( TERM cmisTerm ) | cmisPhrase -> ^( PHRASE cmisPhrase ) )
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==DATETIME||LA18_0==DECIMAL_INTEGER_LITERAL||LA18_0==FLOATING_POINT_LITERAL||(LA18_0 >= FTSPRE && LA18_0 <= FTSWORD)||LA18_0==ID||LA18_0==NOT||LA18_0==QUESTION_MARK||LA18_0==STAR||LA18_0==TO) ) {
				alt18=1;
			}
			else if ( (LA18_0==FTSPHRASE) ) {
				alt18=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}

			switch (alt18) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:473:9: cmisTerm
					{
					pushFollow(FOLLOW_cmisTerm_in_cmisTest2136);
					cmisTerm47=cmisTerm();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_cmisTerm.add(cmisTerm47.getTree());
					// AST REWRITE
					// elements: cmisTerm
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 474:17: -> ^( TERM cmisTerm )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:475:25: ^( TERM cmisTerm )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
						adaptor.addChild(root_1, stream_cmisTerm.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:476:11: cmisPhrase
					{
					pushFollow(FOLLOW_cmisPhrase_in_cmisTest2196);
					cmisPhrase48=cmisPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_cmisPhrase.add(cmisPhrase48.getTree());
					// AST REWRITE
					// elements: cmisPhrase
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 477:17: -> ^( PHRASE cmisPhrase )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:478:25: ^( PHRASE cmisPhrase )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
						adaptor.addChild(root_1, stream_cmisPhrase.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cmisTest"


	public static class template_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "template"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:481:1: template : ( PERCENT tempReference -> ^( TEMPLATE tempReference ) | PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN -> ^( TEMPLATE ( tempReference )+ ) );
	public final FTSParser.template_return template() throws RecognitionException {
		FTSParser.template_return retval = new FTSParser.template_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token PERCENT49=null;
		Token PERCENT51=null;
		Token LPAREN52=null;
		Token COMMA54=null;
		Token RPAREN55=null;
		ParserRuleReturnScope tempReference50 =null;
		ParserRuleReturnScope tempReference53 =null;

		Object PERCENT49_tree=null;
		Object PERCENT51_tree=null;
		Object LPAREN52_tree=null;
		Object COMMA54_tree=null;
		Object RPAREN55_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_PERCENT=new RewriteRuleTokenStream(adaptor,"token PERCENT");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_tempReference=new RewriteRuleSubtreeStream(adaptor,"rule tempReference");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:482:9: ( PERCENT tempReference -> ^( TEMPLATE tempReference ) | PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN -> ^( TEMPLATE ( tempReference )+ ) )
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==PERCENT) ) {
				int LA21_1 = input.LA(2);
				if ( (LA21_1==LPAREN) ) {
					alt21=2;
				}
				else if ( ((LA21_1 >= AND && LA21_1 <= AT)||LA21_1==ID||LA21_1==NOT||LA21_1==OR||LA21_1==TO||LA21_1==URI) ) {
					alt21=1;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 21, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 21, 0, input);
				throw nvae;
			}

			switch (alt21) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:483:9: PERCENT tempReference
					{
					PERCENT49=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_template2277); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PERCENT.add(PERCENT49);

					pushFollow(FOLLOW_tempReference_in_template2279);
					tempReference50=tempReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_tempReference.add(tempReference50.getTree());
					// AST REWRITE
					// elements: tempReference
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 484:17: -> ^( TEMPLATE tempReference )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:485:25: ^( TEMPLATE tempReference )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TEMPLATE, "TEMPLATE"), root_1);
						adaptor.addChild(root_1, stream_tempReference.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:486:11: PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN
					{
					PERCENT51=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_template2339); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PERCENT.add(PERCENT51);

					LPAREN52=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_template2341); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN52);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:486:26: ( tempReference ( COMMA )? )+
					int cnt20=0;
					loop20:
					while (true) {
						int alt20=2;
						int LA20_0 = input.LA(1);
						if ( ((LA20_0 >= AND && LA20_0 <= AT)||LA20_0==ID||LA20_0==NOT||LA20_0==OR||LA20_0==TO||LA20_0==URI) ) {
							alt20=1;
						}

						switch (alt20) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:486:27: tempReference ( COMMA )?
							{
							pushFollow(FOLLOW_tempReference_in_template2344);
							tempReference53=tempReference();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_tempReference.add(tempReference53.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:486:41: ( COMMA )?
							int alt19=2;
							int LA19_0 = input.LA(1);
							if ( (LA19_0==COMMA) ) {
								alt19=1;
							}
							switch (alt19) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:486:41: COMMA
									{
									COMMA54=(Token)match(input,COMMA,FOLLOW_COMMA_in_template2346); if (state.failed) return retval; 
									if ( state.backtracking==0 ) stream_COMMA.add(COMMA54);

									}
									break;

							}

							}
							break;

						default :
							if ( cnt20 >= 1 ) break loop20;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(20, input);
							throw eee;
						}
						cnt20++;
					}

					RPAREN55=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_template2351); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN55);

					// AST REWRITE
					// elements: tempReference
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 487:17: -> ^( TEMPLATE ( tempReference )+ )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:488:25: ^( TEMPLATE ( tempReference )+ )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TEMPLATE, "TEMPLATE"), root_1);
						if ( !(stream_tempReference.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_tempReference.hasNext() ) {
							adaptor.addChild(root_1, stream_tempReference.nextTree());
						}
						stream_tempReference.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "template"


	public static class fuzzy_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "fuzzy"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:491:1: fuzzy : TILDA number -> ^( FUZZY number ) ;
	public final FTSParser.fuzzy_return fuzzy() throws RecognitionException {
		FTSParser.fuzzy_return retval = new FTSParser.fuzzy_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TILDA56=null;
		ParserRuleReturnScope number57 =null;

		Object TILDA56_tree=null;
		RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
		RewriteRuleSubtreeStream stream_number=new RewriteRuleSubtreeStream(adaptor,"rule number");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:492:9: ( TILDA number -> ^( FUZZY number ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:493:9: TILDA number
			{
			TILDA56=(Token)match(input,TILDA,FOLLOW_TILDA_in_fuzzy2433); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA56);

			pushFollow(FOLLOW_number_in_fuzzy2435);
			number57=number();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_number.add(number57.getTree());
			// AST REWRITE
			// elements: number
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 494:17: -> ^( FUZZY number )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:495:25: ^( FUZZY number )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUZZY, "FUZZY"), root_1);
				adaptor.addChild(root_1, stream_number.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "fuzzy"


	public static class slop_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "slop"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:498:1: slop : TILDA DECIMAL_INTEGER_LITERAL -> ^( FUZZY DECIMAL_INTEGER_LITERAL ) ;
	public final FTSParser.slop_return slop() throws RecognitionException {
		FTSParser.slop_return retval = new FTSParser.slop_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TILDA58=null;
		Token DECIMAL_INTEGER_LITERAL59=null;

		Object TILDA58_tree=null;
		Object DECIMAL_INTEGER_LITERAL59_tree=null;
		RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
		RewriteRuleTokenStream stream_DECIMAL_INTEGER_LITERAL=new RewriteRuleTokenStream(adaptor,"token DECIMAL_INTEGER_LITERAL");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:499:9: ( TILDA DECIMAL_INTEGER_LITERAL -> ^( FUZZY DECIMAL_INTEGER_LITERAL ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:500:9: TILDA DECIMAL_INTEGER_LITERAL
			{
			TILDA58=(Token)match(input,TILDA,FOLLOW_TILDA_in_slop2516); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA58);

			DECIMAL_INTEGER_LITERAL59=(Token)match(input,DECIMAL_INTEGER_LITERAL,FOLLOW_DECIMAL_INTEGER_LITERAL_in_slop2518); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_DECIMAL_INTEGER_LITERAL.add(DECIMAL_INTEGER_LITERAL59);

			// AST REWRITE
			// elements: DECIMAL_INTEGER_LITERAL
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 501:17: -> ^( FUZZY DECIMAL_INTEGER_LITERAL )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:502:25: ^( FUZZY DECIMAL_INTEGER_LITERAL )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FUZZY, "FUZZY"), root_1);
				adaptor.addChild(root_1, stream_DECIMAL_INTEGER_LITERAL.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "slop"


	public static class boost_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "boost"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:505:1: boost : CARAT number -> ^( BOOST number ) ;
	public final FTSParser.boost_return boost() throws RecognitionException {
		FTSParser.boost_return retval = new FTSParser.boost_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token CARAT60=null;
		ParserRuleReturnScope number61 =null;

		Object CARAT60_tree=null;
		RewriteRuleTokenStream stream_CARAT=new RewriteRuleTokenStream(adaptor,"token CARAT");
		RewriteRuleSubtreeStream stream_number=new RewriteRuleSubtreeStream(adaptor,"rule number");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:506:9: ( CARAT number -> ^( BOOST number ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:507:9: CARAT number
			{
			CARAT60=(Token)match(input,CARAT,FOLLOW_CARAT_in_boost2599); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CARAT.add(CARAT60);

			pushFollow(FOLLOW_number_in_boost2601);
			number61=number();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_number.add(number61.getTree());
			// AST REWRITE
			// elements: number
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 508:17: -> ^( BOOST number )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:509:25: ^( BOOST number )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BOOST, "BOOST"), root_1);
				adaptor.addChild(root_1, stream_number.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "boost"


	public static class ftsTermOrPhrase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsTermOrPhrase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:512:1: ftsTermOrPhrase : ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) );
	public final FTSParser.ftsTermOrPhrase_return ftsTermOrPhrase() throws RecognitionException {
		FTSParser.ftsTermOrPhrase_return retval = new FTSParser.ftsTermOrPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COLON63=null;
		Token FTSPHRASE64=null;
		Token FTSPHRASE68=null;
		ParserRuleReturnScope fieldReference62 =null;
		ParserRuleReturnScope slop65 =null;
		ParserRuleReturnScope ftsWord66 =null;
		ParserRuleReturnScope fuzzy67 =null;
		ParserRuleReturnScope slop69 =null;
		ParserRuleReturnScope ftsWord70 =null;
		ParserRuleReturnScope fuzzy71 =null;

		Object COLON63_tree=null;
		Object FTSPHRASE64_tree=null;
		Object FTSPHRASE68_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_FTSPHRASE=new RewriteRuleTokenStream(adaptor,"token FTSPHRASE");
		RewriteRuleSubtreeStream stream_fuzzy=new RewriteRuleSubtreeStream(adaptor,"rule fuzzy");
		RewriteRuleSubtreeStream stream_slop=new RewriteRuleSubtreeStream(adaptor,"rule slop");
		RewriteRuleSubtreeStream stream_ftsWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsWord");
		RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:513:9: ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) )
			int alt27=3;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==AT) && (synpred8_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==ID) ) {
				int LA27_2 = input.LA(2);
				if ( (LA27_2==DOT) ) {
					int LA27_10 = input.LA(3);
					if ( (LA27_10==ID) ) {
						int LA27_12 = input.LA(4);
						if ( (LA27_12==COLON) && (synpred8_FTS())) {
							alt27=1;
						}
						else if ( (LA27_12==EOF||(LA27_12 >= AMP && LA27_12 <= BAR)||LA27_12==CARAT||LA27_12==COMMA||LA27_12==DATETIME||LA27_12==DECIMAL_INTEGER_LITERAL||LA27_12==DOT||LA27_12==EQUALS||LA27_12==EXCLAMATION||LA27_12==FLOATING_POINT_LITERAL||(LA27_12 >= FTSPHRASE && LA27_12 <= FTSWORD)||LA27_12==ID||(LA27_12 >= LPAREN && LA27_12 <= LT)||LA27_12==MINUS||LA27_12==NOT||(LA27_12 >= OR && LA27_12 <= PERCENT)||LA27_12==PLUS||LA27_12==QUESTION_MARK||LA27_12==RPAREN||LA27_12==STAR||(LA27_12 >= TILDA && LA27_12 <= TO)||LA27_12==URI) ) {
							alt27=3;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 27, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( (LA27_10==DATETIME||LA27_10==DECIMAL_INTEGER_LITERAL||LA27_10==FLOATING_POINT_LITERAL||(LA27_10 >= FTSPRE && LA27_10 <= FTSWORD)||LA27_10==NOT||LA27_10==QUESTION_MARK||LA27_10==STAR||LA27_10==TO) ) {
						alt27=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 27, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA27_2==COLON) && (synpred8_FTS())) {
					alt27=1;
				}
				else if ( (LA27_2==EOF||(LA27_2 >= AMP && LA27_2 <= BAR)||LA27_2==CARAT||LA27_2==COMMA||LA27_2==DATETIME||LA27_2==DECIMAL_INTEGER_LITERAL||LA27_2==EQUALS||LA27_2==EXCLAMATION||LA27_2==FLOATING_POINT_LITERAL||(LA27_2 >= FTSPHRASE && LA27_2 <= FTSWORD)||LA27_2==ID||(LA27_2 >= LPAREN && LA27_2 <= LT)||LA27_2==MINUS||LA27_2==NOT||(LA27_2 >= OR && LA27_2 <= PERCENT)||LA27_2==PLUS||LA27_2==QUESTION_MARK||LA27_2==RPAREN||LA27_2==STAR||(LA27_2 >= TILDA && LA27_2 <= TO)||LA27_2==URI) ) {
					alt27=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 27, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA27_0==TO) ) {
				int LA27_3 = input.LA(2);
				if ( (LA27_3==COLON) && (synpred8_FTS())) {
					alt27=1;
				}
				else if ( (LA27_3==EOF||(LA27_3 >= AMP && LA27_3 <= BAR)||LA27_3==CARAT||LA27_3==COMMA||LA27_3==DATETIME||LA27_3==DECIMAL_INTEGER_LITERAL||LA27_3==DOT||LA27_3==EQUALS||LA27_3==EXCLAMATION||LA27_3==FLOATING_POINT_LITERAL||(LA27_3 >= FTSPHRASE && LA27_3 <= FTSWORD)||LA27_3==ID||(LA27_3 >= LPAREN && LA27_3 <= LT)||LA27_3==MINUS||LA27_3==NOT||(LA27_3 >= OR && LA27_3 <= PERCENT)||LA27_3==PLUS||LA27_3==QUESTION_MARK||LA27_3==RPAREN||LA27_3==STAR||(LA27_3 >= TILDA && LA27_3 <= TO)||LA27_3==URI) ) {
					alt27=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 27, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA27_0==OR) && (synpred8_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==AND) && (synpred8_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==NOT) ) {
				int LA27_6 = input.LA(2);
				if ( (LA27_6==COLON) && (synpred8_FTS())) {
					alt27=1;
				}
				else if ( (LA27_6==EOF||(LA27_6 >= AMP && LA27_6 <= BAR)||LA27_6==CARAT||LA27_6==COMMA||LA27_6==DATETIME||LA27_6==DECIMAL_INTEGER_LITERAL||LA27_6==DOT||LA27_6==EQUALS||LA27_6==EXCLAMATION||LA27_6==FLOATING_POINT_LITERAL||(LA27_6 >= FTSPHRASE && LA27_6 <= FTSWORD)||LA27_6==ID||(LA27_6 >= LPAREN && LA27_6 <= LT)||LA27_6==MINUS||LA27_6==NOT||(LA27_6 >= OR && LA27_6 <= PERCENT)||LA27_6==PLUS||LA27_6==QUESTION_MARK||LA27_6==RPAREN||LA27_6==STAR||(LA27_6 >= TILDA && LA27_6 <= TO)||LA27_6==URI) ) {
					alt27=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 27, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA27_0==URI) && (synpred8_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==FTSPHRASE) ) {
				alt27=2;
			}
			else if ( (LA27_0==DATETIME||LA27_0==DECIMAL_INTEGER_LITERAL||LA27_0==FLOATING_POINT_LITERAL||(LA27_0 >= FTSPRE && LA27_0 <= FTSWORD)||LA27_0==QUESTION_MARK||LA27_0==STAR) ) {
				alt27=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}

			switch (alt27) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:9: ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					{
					pushFollow(FOLLOW_fieldReference_in_ftsTermOrPhrase2690);
					fieldReference62=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference62.getTree());
					COLON63=(Token)match(input,COLON,FOLLOW_COLON_in_ftsTermOrPhrase2692); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON63);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:515:9: ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0==FTSPHRASE) ) {
						alt24=1;
					}
					else if ( (LA24_0==DATETIME||LA24_0==DECIMAL_INTEGER_LITERAL||LA24_0==FLOATING_POINT_LITERAL||(LA24_0 >= FTSPRE && LA24_0 <= FTSWORD)||LA24_0==ID||LA24_0==NOT||LA24_0==QUESTION_MARK||LA24_0==STAR||LA24_0==TO) ) {
						alt24=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 24, 0, input);
						throw nvae;
					}

					switch (alt24) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:516:17: FTSPHRASE ( ( slop )=> slop )?
							{
							FTSPHRASE64=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2720); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE64);

							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:516:27: ( ( slop )=> slop )?
							int alt22=2;
							int LA22_0 = input.LA(1);
							if ( (LA22_0==TILDA) ) {
								int LA22_1 = input.LA(2);
								if ( (LA22_1==DECIMAL_INTEGER_LITERAL) ) {
									int LA22_3 = input.LA(3);
									if ( (synpred9_FTS()) ) {
										alt22=1;
									}
								}
							}
							switch (alt22) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:516:28: ( slop )=> slop
									{
									pushFollow(FOLLOW_slop_in_ftsTermOrPhrase2728);
									slop65=slop();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_slop.add(slop65.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: FTSPHRASE, slop, fieldReference
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 517:17: -> ^( PHRASE FTSPHRASE fieldReference ( slop )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:20: ^( PHRASE FTSPHRASE fieldReference ( slop )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
								adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:54: ( slop )?
								if ( stream_slop.hasNext() ) {
									adaptor.addChild(root_1, stream_slop.nextTree());
								}
								stream_slop.reset();

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:519:17: ftsWord ( ( fuzzy )=> fuzzy )?
							{
							pushFollow(FOLLOW_ftsWord_in_ftsTermOrPhrase2795);
							ftsWord66=ftsWord();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord66.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:519:25: ( ( fuzzy )=> fuzzy )?
							int alt23=2;
							int LA23_0 = input.LA(1);
							if ( (LA23_0==TILDA) ) {
								int LA23_1 = input.LA(2);
								if ( (LA23_1==DECIMAL_INTEGER_LITERAL||LA23_1==FLOATING_POINT_LITERAL) ) {
									int LA23_3 = input.LA(3);
									if ( (synpred10_FTS()) ) {
										alt23=1;
									}
								}
							}
							switch (alt23) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:519:26: ( fuzzy )=> fuzzy
									{
									pushFollow(FOLLOW_fuzzy_in_ftsTermOrPhrase2804);
									fuzzy67=fuzzy();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy67.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: ftsWord, fuzzy, fieldReference
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 520:17: -> ^( TERM ftsWord fieldReference ( fuzzy )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:520:20: ^( TERM ftsWord fieldReference ( fuzzy )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
								adaptor.addChild(root_1, stream_ftsWord.nextTree());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:520:50: ( fuzzy )?
								if ( stream_fuzzy.hasNext() ) {
									adaptor.addChild(root_1, stream_fuzzy.nextTree());
								}
								stream_fuzzy.reset();

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:523:9: FTSPHRASE ( ( slop )=> slop )?
					{
					FTSPHRASE68=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2865); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE68);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:523:19: ( ( slop )=> slop )?
					int alt25=2;
					int LA25_0 = input.LA(1);
					if ( (LA25_0==TILDA) ) {
						int LA25_1 = input.LA(2);
						if ( (LA25_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA25_3 = input.LA(3);
							if ( (synpred11_FTS()) ) {
								alt25=1;
							}
						}
					}
					switch (alt25) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:523:20: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsTermOrPhrase2873);
							slop69=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop69.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: slop, FTSPHRASE
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 524:17: -> ^( PHRASE FTSPHRASE ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:20: ^( PHRASE FTSPHRASE ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
						adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:39: ( slop )?
						if ( stream_slop.hasNext() ) {
							adaptor.addChild(root_1, stream_slop.nextTree());
						}
						stream_slop.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:526:9: ftsWord ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsWord_in_ftsTermOrPhrase2923);
					ftsWord70=ftsWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord70.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:526:17: ( ( fuzzy )=> fuzzy )?
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0==TILDA) ) {
						int LA26_1 = input.LA(2);
						if ( (LA26_1==DECIMAL_INTEGER_LITERAL||LA26_1==FLOATING_POINT_LITERAL) ) {
							int LA26_3 = input.LA(3);
							if ( (synpred12_FTS()) ) {
								alt26=1;
							}
						}
					}
					switch (alt26) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:526:18: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsTermOrPhrase2932);
							fuzzy71=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy71.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: fuzzy, ftsWord
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 527:17: -> ^( TERM ftsWord ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:527:20: ^( TERM ftsWord ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsWord.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:527:35: ( fuzzy )?
						if ( stream_fuzzy.hasNext() ) {
							adaptor.addChild(root_1, stream_fuzzy.nextTree());
						}
						stream_fuzzy.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsTermOrPhrase"


	public static class ftsExactTermOrPhrase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsExactTermOrPhrase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:531:1: ftsExactTermOrPhrase : EQUALS ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) ) ;
	public final FTSParser.ftsExactTermOrPhrase_return ftsExactTermOrPhrase() throws RecognitionException {
		FTSParser.ftsExactTermOrPhrase_return retval = new FTSParser.ftsExactTermOrPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EQUALS72=null;
		Token COLON74=null;
		Token FTSPHRASE75=null;
		Token FTSPHRASE79=null;
		ParserRuleReturnScope fieldReference73 =null;
		ParserRuleReturnScope slop76 =null;
		ParserRuleReturnScope ftsWord77 =null;
		ParserRuleReturnScope fuzzy78 =null;
		ParserRuleReturnScope slop80 =null;
		ParserRuleReturnScope ftsWord81 =null;
		ParserRuleReturnScope fuzzy82 =null;

		Object EQUALS72_tree=null;
		Object COLON74_tree=null;
		Object FTSPHRASE75_tree=null;
		Object FTSPHRASE79_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
		RewriteRuleTokenStream stream_FTSPHRASE=new RewriteRuleTokenStream(adaptor,"token FTSPHRASE");
		RewriteRuleSubtreeStream stream_fuzzy=new RewriteRuleSubtreeStream(adaptor,"rule fuzzy");
		RewriteRuleSubtreeStream stream_slop=new RewriteRuleSubtreeStream(adaptor,"rule slop");
		RewriteRuleSubtreeStream stream_ftsWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsWord");
		RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:532:9: ( EQUALS ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:533:9: EQUALS ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) )
			{
			EQUALS72=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsExactTermOrPhrase3011); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS72);

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:534:9: ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) )
			int alt33=3;
			int LA33_0 = input.LA(1);
			if ( (LA33_0==AT) && (synpred13_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==ID) ) {
				int LA33_2 = input.LA(2);
				if ( (LA33_2==DOT) ) {
					int LA33_10 = input.LA(3);
					if ( (LA33_10==ID) ) {
						int LA33_12 = input.LA(4);
						if ( (LA33_12==COLON) && (synpred13_FTS())) {
							alt33=1;
						}
						else if ( (LA33_12==EOF||(LA33_12 >= AMP && LA33_12 <= BAR)||LA33_12==CARAT||LA33_12==COMMA||LA33_12==DATETIME||LA33_12==DECIMAL_INTEGER_LITERAL||LA33_12==DOT||LA33_12==EQUALS||LA33_12==EXCLAMATION||LA33_12==FLOATING_POINT_LITERAL||(LA33_12 >= FTSPHRASE && LA33_12 <= FTSWORD)||LA33_12==ID||(LA33_12 >= LPAREN && LA33_12 <= LT)||LA33_12==MINUS||LA33_12==NOT||(LA33_12 >= OR && LA33_12 <= PERCENT)||LA33_12==PLUS||LA33_12==QUESTION_MARK||LA33_12==RPAREN||LA33_12==STAR||(LA33_12 >= TILDA && LA33_12 <= TO)||LA33_12==URI) ) {
							alt33=3;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 33, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( (LA33_10==DATETIME||LA33_10==DECIMAL_INTEGER_LITERAL||LA33_10==FLOATING_POINT_LITERAL||(LA33_10 >= FTSPRE && LA33_10 <= FTSWORD)||LA33_10==NOT||LA33_10==QUESTION_MARK||LA33_10==STAR||LA33_10==TO) ) {
						alt33=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 33, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA33_2==COLON) && (synpred13_FTS())) {
					alt33=1;
				}
				else if ( (LA33_2==EOF||(LA33_2 >= AMP && LA33_2 <= BAR)||LA33_2==CARAT||LA33_2==COMMA||LA33_2==DATETIME||LA33_2==DECIMAL_INTEGER_LITERAL||LA33_2==EQUALS||LA33_2==EXCLAMATION||LA33_2==FLOATING_POINT_LITERAL||(LA33_2 >= FTSPHRASE && LA33_2 <= FTSWORD)||LA33_2==ID||(LA33_2 >= LPAREN && LA33_2 <= LT)||LA33_2==MINUS||LA33_2==NOT||(LA33_2 >= OR && LA33_2 <= PERCENT)||LA33_2==PLUS||LA33_2==QUESTION_MARK||LA33_2==RPAREN||LA33_2==STAR||(LA33_2 >= TILDA && LA33_2 <= TO)||LA33_2==URI) ) {
					alt33=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 33, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA33_0==TO) ) {
				int LA33_3 = input.LA(2);
				if ( (LA33_3==COLON) && (synpred13_FTS())) {
					alt33=1;
				}
				else if ( (LA33_3==EOF||(LA33_3 >= AMP && LA33_3 <= BAR)||LA33_3==CARAT||LA33_3==COMMA||LA33_3==DATETIME||LA33_3==DECIMAL_INTEGER_LITERAL||LA33_3==DOT||LA33_3==EQUALS||LA33_3==EXCLAMATION||LA33_3==FLOATING_POINT_LITERAL||(LA33_3 >= FTSPHRASE && LA33_3 <= FTSWORD)||LA33_3==ID||(LA33_3 >= LPAREN && LA33_3 <= LT)||LA33_3==MINUS||LA33_3==NOT||(LA33_3 >= OR && LA33_3 <= PERCENT)||LA33_3==PLUS||LA33_3==QUESTION_MARK||LA33_3==RPAREN||LA33_3==STAR||(LA33_3 >= TILDA && LA33_3 <= TO)||LA33_3==URI) ) {
					alt33=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 33, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA33_0==OR) && (synpred13_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==AND) && (synpred13_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==NOT) ) {
				int LA33_6 = input.LA(2);
				if ( (LA33_6==COLON) && (synpred13_FTS())) {
					alt33=1;
				}
				else if ( (LA33_6==EOF||(LA33_6 >= AMP && LA33_6 <= BAR)||LA33_6==CARAT||LA33_6==COMMA||LA33_6==DATETIME||LA33_6==DECIMAL_INTEGER_LITERAL||LA33_6==DOT||LA33_6==EQUALS||LA33_6==EXCLAMATION||LA33_6==FLOATING_POINT_LITERAL||(LA33_6 >= FTSPHRASE && LA33_6 <= FTSWORD)||LA33_6==ID||(LA33_6 >= LPAREN && LA33_6 <= LT)||LA33_6==MINUS||LA33_6==NOT||(LA33_6 >= OR && LA33_6 <= PERCENT)||LA33_6==PLUS||LA33_6==QUESTION_MARK||LA33_6==RPAREN||LA33_6==STAR||(LA33_6 >= TILDA && LA33_6 <= TO)||LA33_6==URI) ) {
					alt33=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 33, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA33_0==URI) && (synpred13_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==FTSPHRASE) ) {
				alt33=2;
			}
			else if ( (LA33_0==DATETIME||LA33_0==DECIMAL_INTEGER_LITERAL||LA33_0==FLOATING_POINT_LITERAL||(LA33_0 >= FTSPRE && LA33_0 <= FTSWORD)||LA33_0==QUESTION_MARK||LA33_0==STAR) ) {
				alt33=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 33, 0, input);
				throw nvae;
			}

			switch (alt33) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:9: ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) )
					{
					pushFollow(FOLLOW_fieldReference_in_ftsExactTermOrPhrase3039);
					fieldReference73=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference73.getTree());
					COLON74=(Token)match(input,COLON,FOLLOW_COLON_in_ftsExactTermOrPhrase3041); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON74);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:536:9: ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) )
					int alt30=2;
					int LA30_0 = input.LA(1);
					if ( (LA30_0==FTSPHRASE) ) {
						alt30=1;
					}
					else if ( (LA30_0==DATETIME||LA30_0==DECIMAL_INTEGER_LITERAL||LA30_0==FLOATING_POINT_LITERAL||(LA30_0 >= FTSPRE && LA30_0 <= FTSWORD)||LA30_0==ID||LA30_0==NOT||LA30_0==QUESTION_MARK||LA30_0==STAR||LA30_0==TO) ) {
						alt30=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 30, 0, input);
						throw nvae;
					}

					switch (alt30) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:537:17: FTSPHRASE ( ( slop )=> slop )?
							{
							FTSPHRASE75=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3069); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE75);

							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:537:27: ( ( slop )=> slop )?
							int alt28=2;
							int LA28_0 = input.LA(1);
							if ( (LA28_0==TILDA) ) {
								int LA28_1 = input.LA(2);
								if ( (LA28_1==DECIMAL_INTEGER_LITERAL) ) {
									int LA28_3 = input.LA(3);
									if ( (synpred14_FTS()) ) {
										alt28=1;
									}
								}
							}
							switch (alt28) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:537:28: ( slop )=> slop
									{
									pushFollow(FOLLOW_slop_in_ftsExactTermOrPhrase3077);
									slop76=slop();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_slop.add(slop76.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: fieldReference, FTSPHRASE, slop
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 538:17: -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:20: ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_PHRASE, "EXACT_PHRASE"), root_1);
								adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:60: ( slop )?
								if ( stream_slop.hasNext() ) {
									adaptor.addChild(root_1, stream_slop.nextTree());
								}
								stream_slop.reset();

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:540:17: ftsWord ( ( fuzzy )=> fuzzy )?
							{
							pushFollow(FOLLOW_ftsWord_in_ftsExactTermOrPhrase3144);
							ftsWord77=ftsWord();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord77.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:540:25: ( ( fuzzy )=> fuzzy )?
							int alt29=2;
							int LA29_0 = input.LA(1);
							if ( (LA29_0==TILDA) ) {
								int LA29_1 = input.LA(2);
								if ( (LA29_1==DECIMAL_INTEGER_LITERAL||LA29_1==FLOATING_POINT_LITERAL) ) {
									int LA29_3 = input.LA(3);
									if ( (synpred15_FTS()) ) {
										alt29=1;
									}
								}
							}
							switch (alt29) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:540:26: ( fuzzy )=> fuzzy
									{
									pushFollow(FOLLOW_fuzzy_in_ftsExactTermOrPhrase3153);
									fuzzy78=fuzzy();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy78.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: ftsWord, fuzzy, fieldReference
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 541:17: -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:541:20: ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_TERM, "EXACT_TERM"), root_1);
								adaptor.addChild(root_1, stream_ftsWord.nextTree());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:541:56: ( fuzzy )?
								if ( stream_fuzzy.hasNext() ) {
									adaptor.addChild(root_1, stream_fuzzy.nextTree());
								}
								stream_fuzzy.reset();

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:544:9: FTSPHRASE ( ( slop )=> slop )?
					{
					FTSPHRASE79=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3214); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE79);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:544:19: ( ( slop )=> slop )?
					int alt31=2;
					int LA31_0 = input.LA(1);
					if ( (LA31_0==TILDA) ) {
						int LA31_1 = input.LA(2);
						if ( (LA31_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA31_3 = input.LA(3);
							if ( (synpred16_FTS()) ) {
								alt31=1;
							}
						}
					}
					switch (alt31) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:544:20: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsExactTermOrPhrase3222);
							slop80=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop80.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: FTSPHRASE, slop
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 545:17: -> ^( EXACT_PHRASE FTSPHRASE ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:545:20: ^( EXACT_PHRASE FTSPHRASE ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_PHRASE, "EXACT_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:545:45: ( slop )?
						if ( stream_slop.hasNext() ) {
							adaptor.addChild(root_1, stream_slop.nextTree());
						}
						stream_slop.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:547:9: ftsWord ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsWord_in_ftsExactTermOrPhrase3272);
					ftsWord81=ftsWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord81.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:547:17: ( ( fuzzy )=> fuzzy )?
					int alt32=2;
					int LA32_0 = input.LA(1);
					if ( (LA32_0==TILDA) ) {
						int LA32_1 = input.LA(2);
						if ( (LA32_1==DECIMAL_INTEGER_LITERAL||LA32_1==FLOATING_POINT_LITERAL) ) {
							int LA32_3 = input.LA(3);
							if ( (synpred17_FTS()) ) {
								alt32=1;
							}
						}
					}
					switch (alt32) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:547:18: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsExactTermOrPhrase3281);
							fuzzy82=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy82.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: fuzzy, ftsWord
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 548:17: -> ^( EXACT_TERM ftsWord ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:548:20: ^( EXACT_TERM ftsWord ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_TERM, "EXACT_TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsWord.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:548:41: ( fuzzy )?
						if ( stream_fuzzy.hasNext() ) {
							adaptor.addChild(root_1, stream_fuzzy.nextTree());
						}
						stream_fuzzy.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsExactTermOrPhrase"


	public static class ftsTokenisedTermOrPhrase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsTokenisedTermOrPhrase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:553:1: ftsTokenisedTermOrPhrase : TILDA ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) ) ;
	public final FTSParser.ftsTokenisedTermOrPhrase_return ftsTokenisedTermOrPhrase() throws RecognitionException {
		FTSParser.ftsTokenisedTermOrPhrase_return retval = new FTSParser.ftsTokenisedTermOrPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TILDA83=null;
		Token COLON85=null;
		Token FTSPHRASE86=null;
		Token FTSPHRASE90=null;
		ParserRuleReturnScope fieldReference84 =null;
		ParserRuleReturnScope slop87 =null;
		ParserRuleReturnScope ftsWord88 =null;
		ParserRuleReturnScope fuzzy89 =null;
		ParserRuleReturnScope slop91 =null;
		ParserRuleReturnScope ftsWord92 =null;
		ParserRuleReturnScope fuzzy93 =null;

		Object TILDA83_tree=null;
		Object COLON85_tree=null;
		Object FTSPHRASE86_tree=null;
		Object FTSPHRASE90_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
		RewriteRuleTokenStream stream_FTSPHRASE=new RewriteRuleTokenStream(adaptor,"token FTSPHRASE");
		RewriteRuleSubtreeStream stream_fuzzy=new RewriteRuleSubtreeStream(adaptor,"rule fuzzy");
		RewriteRuleSubtreeStream stream_slop=new RewriteRuleSubtreeStream(adaptor,"rule slop");
		RewriteRuleSubtreeStream stream_ftsWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsWord");
		RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:554:9: ( TILDA ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:555:9: TILDA ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) )
			{
			TILDA83=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsTokenisedTermOrPhrase3362); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA83);

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:556:9: ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) )
			int alt39=3;
			int LA39_0 = input.LA(1);
			if ( (LA39_0==AT) && (synpred18_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==ID) ) {
				int LA39_2 = input.LA(2);
				if ( (LA39_2==DOT) ) {
					int LA39_10 = input.LA(3);
					if ( (LA39_10==ID) ) {
						int LA39_12 = input.LA(4);
						if ( (LA39_12==COLON) && (synpred18_FTS())) {
							alt39=1;
						}
						else if ( (LA39_12==EOF||(LA39_12 >= AMP && LA39_12 <= BAR)||LA39_12==CARAT||LA39_12==COMMA||LA39_12==DATETIME||LA39_12==DECIMAL_INTEGER_LITERAL||LA39_12==DOT||LA39_12==EQUALS||LA39_12==EXCLAMATION||LA39_12==FLOATING_POINT_LITERAL||(LA39_12 >= FTSPHRASE && LA39_12 <= FTSWORD)||LA39_12==ID||(LA39_12 >= LPAREN && LA39_12 <= LT)||LA39_12==MINUS||LA39_12==NOT||(LA39_12 >= OR && LA39_12 <= PERCENT)||LA39_12==PLUS||LA39_12==QUESTION_MARK||LA39_12==RPAREN||LA39_12==STAR||(LA39_12 >= TILDA && LA39_12 <= TO)||LA39_12==URI) ) {
							alt39=3;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 39, 12, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( (LA39_10==DATETIME||LA39_10==DECIMAL_INTEGER_LITERAL||LA39_10==FLOATING_POINT_LITERAL||(LA39_10 >= FTSPRE && LA39_10 <= FTSWORD)||LA39_10==NOT||LA39_10==QUESTION_MARK||LA39_10==STAR||LA39_10==TO) ) {
						alt39=3;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 39, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA39_2==COLON) && (synpred18_FTS())) {
					alt39=1;
				}
				else if ( (LA39_2==EOF||(LA39_2 >= AMP && LA39_2 <= BAR)||LA39_2==CARAT||LA39_2==COMMA||LA39_2==DATETIME||LA39_2==DECIMAL_INTEGER_LITERAL||LA39_2==EQUALS||LA39_2==EXCLAMATION||LA39_2==FLOATING_POINT_LITERAL||(LA39_2 >= FTSPHRASE && LA39_2 <= FTSWORD)||LA39_2==ID||(LA39_2 >= LPAREN && LA39_2 <= LT)||LA39_2==MINUS||LA39_2==NOT||(LA39_2 >= OR && LA39_2 <= PERCENT)||LA39_2==PLUS||LA39_2==QUESTION_MARK||LA39_2==RPAREN||LA39_2==STAR||(LA39_2 >= TILDA && LA39_2 <= TO)||LA39_2==URI) ) {
					alt39=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA39_0==TO) ) {
				int LA39_3 = input.LA(2);
				if ( (LA39_3==COLON) && (synpred18_FTS())) {
					alt39=1;
				}
				else if ( (LA39_3==EOF||(LA39_3 >= AMP && LA39_3 <= BAR)||LA39_3==CARAT||LA39_3==COMMA||LA39_3==DATETIME||LA39_3==DECIMAL_INTEGER_LITERAL||LA39_3==DOT||LA39_3==EQUALS||LA39_3==EXCLAMATION||LA39_3==FLOATING_POINT_LITERAL||(LA39_3 >= FTSPHRASE && LA39_3 <= FTSWORD)||LA39_3==ID||(LA39_3 >= LPAREN && LA39_3 <= LT)||LA39_3==MINUS||LA39_3==NOT||(LA39_3 >= OR && LA39_3 <= PERCENT)||LA39_3==PLUS||LA39_3==QUESTION_MARK||LA39_3==RPAREN||LA39_3==STAR||(LA39_3 >= TILDA && LA39_3 <= TO)||LA39_3==URI) ) {
					alt39=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA39_0==OR) && (synpred18_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==AND) && (synpred18_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==NOT) ) {
				int LA39_6 = input.LA(2);
				if ( (LA39_6==COLON) && (synpred18_FTS())) {
					alt39=1;
				}
				else if ( (LA39_6==EOF||(LA39_6 >= AMP && LA39_6 <= BAR)||LA39_6==CARAT||LA39_6==COMMA||LA39_6==DATETIME||LA39_6==DECIMAL_INTEGER_LITERAL||LA39_6==DOT||LA39_6==EQUALS||LA39_6==EXCLAMATION||LA39_6==FLOATING_POINT_LITERAL||(LA39_6 >= FTSPHRASE && LA39_6 <= FTSWORD)||LA39_6==ID||(LA39_6 >= LPAREN && LA39_6 <= LT)||LA39_6==MINUS||LA39_6==NOT||(LA39_6 >= OR && LA39_6 <= PERCENT)||LA39_6==PLUS||LA39_6==QUESTION_MARK||LA39_6==RPAREN||LA39_6==STAR||(LA39_6 >= TILDA && LA39_6 <= TO)||LA39_6==URI) ) {
					alt39=3;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 39, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA39_0==URI) && (synpred18_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==FTSPHRASE) ) {
				alt39=2;
			}
			else if ( (LA39_0==DATETIME||LA39_0==DECIMAL_INTEGER_LITERAL||LA39_0==FLOATING_POINT_LITERAL||(LA39_0 >= FTSPRE && LA39_0 <= FTSWORD)||LA39_0==QUESTION_MARK||LA39_0==STAR) ) {
				alt39=3;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 39, 0, input);
				throw nvae;
			}

			switch (alt39) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:9: ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					{
					pushFollow(FOLLOW_fieldReference_in_ftsTokenisedTermOrPhrase3390);
					fieldReference84=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference84.getTree());
					COLON85=(Token)match(input,COLON,FOLLOW_COLON_in_ftsTokenisedTermOrPhrase3392); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON85);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:558:9: ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					int alt36=2;
					int LA36_0 = input.LA(1);
					if ( (LA36_0==FTSPHRASE) ) {
						alt36=1;
					}
					else if ( (LA36_0==DATETIME||LA36_0==DECIMAL_INTEGER_LITERAL||LA36_0==FLOATING_POINT_LITERAL||(LA36_0 >= FTSPRE && LA36_0 <= FTSWORD)||LA36_0==ID||LA36_0==NOT||LA36_0==QUESTION_MARK||LA36_0==STAR||LA36_0==TO) ) {
						alt36=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 36, 0, input);
						throw nvae;
					}

					switch (alt36) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:559:17: FTSPHRASE ( ( slop )=> slop )?
							{
							FTSPHRASE86=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3420); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE86);

							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:559:27: ( ( slop )=> slop )?
							int alt34=2;
							int LA34_0 = input.LA(1);
							if ( (LA34_0==TILDA) ) {
								int LA34_1 = input.LA(2);
								if ( (LA34_1==DECIMAL_INTEGER_LITERAL) ) {
									int LA34_3 = input.LA(3);
									if ( (synpred19_FTS()) ) {
										alt34=1;
									}
								}
							}
							switch (alt34) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:559:28: ( slop )=> slop
									{
									pushFollow(FOLLOW_slop_in_ftsTokenisedTermOrPhrase3428);
									slop87=slop();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_slop.add(slop87.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: fieldReference, FTSPHRASE, slop
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 560:17: -> ^( PHRASE FTSPHRASE fieldReference ( slop )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:20: ^( PHRASE FTSPHRASE fieldReference ( slop )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
								adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:54: ( slop )?
								if ( stream_slop.hasNext() ) {
									adaptor.addChild(root_1, stream_slop.nextTree());
								}
								stream_slop.reset();

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;
						case 2 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:562:17: ftsWord ( ( fuzzy )=> fuzzy )?
							{
							pushFollow(FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3495);
							ftsWord88=ftsWord();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord88.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:562:25: ( ( fuzzy )=> fuzzy )?
							int alt35=2;
							int LA35_0 = input.LA(1);
							if ( (LA35_0==TILDA) ) {
								int LA35_1 = input.LA(2);
								if ( (LA35_1==DECIMAL_INTEGER_LITERAL||LA35_1==FLOATING_POINT_LITERAL) ) {
									int LA35_3 = input.LA(3);
									if ( (synpred20_FTS()) ) {
										alt35=1;
									}
								}
							}
							switch (alt35) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:562:26: ( fuzzy )=> fuzzy
									{
									pushFollow(FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3504);
									fuzzy89=fuzzy();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy89.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: fuzzy, fieldReference, ftsWord
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 563:17: -> ^( TERM ftsWord fieldReference ( fuzzy )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:563:20: ^( TERM ftsWord fieldReference ( fuzzy )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
								adaptor.addChild(root_1, stream_ftsWord.nextTree());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:563:50: ( fuzzy )?
								if ( stream_fuzzy.hasNext() ) {
									adaptor.addChild(root_1, stream_fuzzy.nextTree());
								}
								stream_fuzzy.reset();

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;
							}

							}
							break;

					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:566:9: FTSPHRASE ( ( slop )=> slop )?
					{
					FTSPHRASE90=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3565); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE90);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:566:19: ( ( slop )=> slop )?
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( (LA37_0==TILDA) ) {
						int LA37_1 = input.LA(2);
						if ( (LA37_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA37_3 = input.LA(3);
							if ( (synpred21_FTS()) ) {
								alt37=1;
							}
						}
					}
					switch (alt37) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:566:20: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsTokenisedTermOrPhrase3573);
							slop91=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop91.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: FTSPHRASE, slop
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 567:17: -> ^( PHRASE FTSPHRASE ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:20: ^( PHRASE FTSPHRASE ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
						adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:39: ( slop )?
						if ( stream_slop.hasNext() ) {
							adaptor.addChild(root_1, stream_slop.nextTree());
						}
						stream_slop.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:569:9: ftsWord ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3623);
					ftsWord92=ftsWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord92.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:569:17: ( ( fuzzy )=> fuzzy )?
					int alt38=2;
					int LA38_0 = input.LA(1);
					if ( (LA38_0==TILDA) ) {
						int LA38_1 = input.LA(2);
						if ( (LA38_1==DECIMAL_INTEGER_LITERAL||LA38_1==FLOATING_POINT_LITERAL) ) {
							int LA38_3 = input.LA(3);
							if ( (synpred22_FTS()) ) {
								alt38=1;
							}
						}
					}
					switch (alt38) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:569:18: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3632);
							fuzzy93=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy93.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: fuzzy, ftsWord
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 570:17: -> ^( TERM ftsWord ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:570:20: ^( TERM ftsWord ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsWord.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:570:35: ( fuzzy )?
						if ( stream_fuzzy.hasNext() ) {
							adaptor.addChild(root_1, stream_fuzzy.nextTree());
						}
						stream_fuzzy.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsTokenisedTermOrPhrase"


	public static class cmisTerm_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cmisTerm"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:575:1: cmisTerm : ftsWord -> ftsWord ;
	public final FTSParser.cmisTerm_return cmisTerm() throws RecognitionException {
		FTSParser.cmisTerm_return retval = new FTSParser.cmisTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsWord94 =null;

		RewriteRuleSubtreeStream stream_ftsWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsWord");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:576:9: ( ftsWord -> ftsWord )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:577:9: ftsWord
			{
			pushFollow(FOLLOW_ftsWord_in_cmisTerm3705);
			ftsWord94=ftsWord();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord94.getTree());
			// AST REWRITE
			// elements: ftsWord
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 578:17: -> ftsWord
			{
				adaptor.addChild(root_0, stream_ftsWord.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cmisTerm"


	public static class cmisPhrase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "cmisPhrase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:582:1: cmisPhrase : FTSPHRASE -> FTSPHRASE ;
	public final FTSParser.cmisPhrase_return cmisPhrase() throws RecognitionException {
		FTSParser.cmisPhrase_return retval = new FTSParser.cmisPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token FTSPHRASE95=null;

		Object FTSPHRASE95_tree=null;
		RewriteRuleTokenStream stream_FTSPHRASE=new RewriteRuleTokenStream(adaptor,"token FTSPHRASE");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:583:9: ( FTSPHRASE -> FTSPHRASE )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:584:9: FTSPHRASE
			{
			FTSPHRASE95=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_cmisPhrase3759); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE95);

			// AST REWRITE
			// elements: FTSPHRASE
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 585:17: -> FTSPHRASE
			{
				adaptor.addChild(root_0, stream_FTSPHRASE.nextNode());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "cmisPhrase"


	public static class ftsRange_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsRange"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:589:1: ftsRange : ( fieldReference COLON )? ftsFieldGroupRange -> ftsFieldGroupRange ( fieldReference )? ;
	public final FTSParser.ftsRange_return ftsRange() throws RecognitionException {
		FTSParser.ftsRange_return retval = new FTSParser.ftsRange_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COLON97=null;
		ParserRuleReturnScope fieldReference96 =null;
		ParserRuleReturnScope ftsFieldGroupRange98 =null;

		Object COLON97_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleSubtreeStream stream_ftsFieldGroupRange=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupRange");
		RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:590:9: ( ( fieldReference COLON )? ftsFieldGroupRange -> ftsFieldGroupRange ( fieldReference )? )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:591:9: ( fieldReference COLON )? ftsFieldGroupRange
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:591:9: ( fieldReference COLON )?
			int alt40=2;
			int LA40_0 = input.LA(1);
			if ( ((LA40_0 >= AND && LA40_0 <= AT)||LA40_0==NOT||LA40_0==OR||LA40_0==TO||LA40_0==URI) ) {
				alt40=1;
			}
			else if ( (LA40_0==ID) ) {
				int LA40_2 = input.LA(2);
				if ( (LA40_2==COLON||LA40_2==DOT) ) {
					alt40=1;
				}
			}
			switch (alt40) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:591:10: fieldReference COLON
					{
					pushFollow(FOLLOW_fieldReference_in_ftsRange3814);
					fieldReference96=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference96.getTree());
					COLON97=(Token)match(input,COLON,FOLLOW_COLON_in_ftsRange3816); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON97);

					}
					break;

			}

			pushFollow(FOLLOW_ftsFieldGroupRange_in_ftsRange3820);
			ftsFieldGroupRange98=ftsFieldGroupRange();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupRange.add(ftsFieldGroupRange98.getTree());
			// AST REWRITE
			// elements: fieldReference, ftsFieldGroupRange
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 592:17: -> ftsFieldGroupRange ( fieldReference )?
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupRange.nextTree());
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:592:39: ( fieldReference )?
				if ( stream_fieldReference.hasNext() ) {
					adaptor.addChild(root_0, stream_fieldReference.nextTree());
				}
				stream_fieldReference.reset();

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsRange"


	public static class ftsFieldGroup_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroup"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:595:1: ftsFieldGroup : fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction ) ;
	public final FTSParser.ftsFieldGroup_return ftsFieldGroup() throws RecognitionException {
		FTSParser.ftsFieldGroup_return retval = new FTSParser.ftsFieldGroup_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COLON100=null;
		Token LPAREN101=null;
		Token RPAREN103=null;
		ParserRuleReturnScope fieldReference99 =null;
		ParserRuleReturnScope ftsFieldGroupDisjunction102 =null;

		Object COLON100_tree=null;
		Object LPAREN101_tree=null;
		Object RPAREN103_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_fieldReference=new RewriteRuleSubtreeStream(adaptor,"rule fieldReference");
		RewriteRuleSubtreeStream stream_ftsFieldGroupDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupDisjunction");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:596:9: ( fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:597:9: fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN
			{
			pushFollow(FOLLOW_fieldReference_in_ftsFieldGroup3876);
			fieldReference99=fieldReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference99.getTree());
			COLON100=(Token)match(input,COLON,FOLLOW_COLON_in_ftsFieldGroup3878); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_COLON.add(COLON100);

			LPAREN101=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsFieldGroup3880); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN101);

			pushFollow(FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroup3882);
			ftsFieldGroupDisjunction102=ftsFieldGroupDisjunction();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupDisjunction.add(ftsFieldGroupDisjunction102.getTree());
			RPAREN103=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsFieldGroup3884); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN103);

			// AST REWRITE
			// elements: fieldReference, ftsFieldGroupDisjunction
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 598:17: -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:599:25: ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_GROUP, "FIELD_GROUP"), root_1);
				adaptor.addChild(root_1, stream_fieldReference.nextTree());
				adaptor.addChild(root_1, stream_ftsFieldGroupDisjunction.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroup"


	public static class ftsFieldGroupDisjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupDisjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:602:1: ftsFieldGroupDisjunction : ({...}? ftsFieldGroupExplicitDisjunction |{...}? ftsFieldGroupImplicitDisjunction );
	public final FTSParser.ftsFieldGroupDisjunction_return ftsFieldGroupDisjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupDisjunction_return retval = new FTSParser.ftsFieldGroupDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsFieldGroupExplicitDisjunction104 =null;
		ParserRuleReturnScope ftsFieldGroupImplicitDisjunction105 =null;


		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:603:9: ({...}? ftsFieldGroupExplicitDisjunction |{...}? ftsFieldGroupImplicitDisjunction )
			int alt41=2;
			switch ( input.LA(1) ) {
			case AMP:
			case AND:
				{
				alt41=1;
				}
				break;
			case NOT:
				{
				int LA41_3 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EXCLAMATION:
				{
				int LA41_4 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case DATETIME:
			case DECIMAL_INTEGER_LITERAL:
			case FLOATING_POINT_LITERAL:
			case FTSPRE:
			case FTSWILD:
			case FTSWORD:
			case ID:
				{
				int LA41_5 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STAR:
				{
				int LA41_6 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EQUALS:
				{
				int LA41_7 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FTSPHRASE:
				{
				int LA41_8 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TILDA:
				{
				int LA41_9 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TO:
				{
				int LA41_10 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case QUESTION_MARK:
				{
				int LA41_11 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 11, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LSQUARE:
				{
				int LA41_12 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LT:
				{
				int LA41_13 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 13, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LPAREN:
				{
				int LA41_14 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case PLUS:
				{
				int LA41_15 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 15, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case BAR:
				{
				int LA41_16 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 16, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case MINUS:
				{
				int LA41_17 = input.LA(2);
				if ( ((defaultFieldConjunction() == true)) ) {
					alt41=1;
				}
				else if ( ((defaultFieldConjunction() == false)) ) {
					alt41=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 41, 17, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case OR:
				{
				alt41=2;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 41, 0, input);
				throw nvae;
			}
			switch (alt41) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:604:9: {...}? ftsFieldGroupExplicitDisjunction
					{
					root_0 = (Object)adaptor.nil();


					if ( !((defaultFieldConjunction() == true)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "ftsFieldGroupDisjunction", "defaultFieldConjunction() == true");
					}
					pushFollow(FOLLOW_ftsFieldGroupExplicitDisjunction_in_ftsFieldGroupDisjunction3969);
					ftsFieldGroupExplicitDisjunction104=ftsFieldGroupExplicitDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsFieldGroupExplicitDisjunction104.getTree());

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:605:11: {...}? ftsFieldGroupImplicitDisjunction
					{
					root_0 = (Object)adaptor.nil();


					if ( !((defaultFieldConjunction() == false)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "ftsFieldGroupDisjunction", "defaultFieldConjunction() == false");
					}
					pushFollow(FOLLOW_ftsFieldGroupImplicitDisjunction_in_ftsFieldGroupDisjunction3983);
					ftsFieldGroupImplicitDisjunction105=ftsFieldGroupImplicitDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsFieldGroupImplicitDisjunction105.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupDisjunction"


	public static class ftsFieldGroupExplicitDisjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupExplicitDisjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:608:1: ftsFieldGroupExplicitDisjunction : ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )* -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ ) ;
	public final FTSParser.ftsFieldGroupExplicitDisjunction_return ftsFieldGroupExplicitDisjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupExplicitDisjunction_return retval = new FTSParser.ftsFieldGroupExplicitDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsFieldGroupImplicitConjunction106 =null;
		ParserRuleReturnScope or107 =null;
		ParserRuleReturnScope ftsFieldGroupImplicitConjunction108 =null;

		RewriteRuleSubtreeStream stream_ftsFieldGroupImplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupImplicitConjunction");
		RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:609:9: ( ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )* -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:610:9: ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )*
			{
			pushFollow(FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction4016);
			ftsFieldGroupImplicitConjunction106=ftsFieldGroupImplicitConjunction();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupImplicitConjunction.add(ftsFieldGroupImplicitConjunction106.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:610:42: ( or ftsFieldGroupImplicitConjunction )*
			loop42:
			while (true) {
				int alt42=2;
				int LA42_0 = input.LA(1);
				if ( (LA42_0==BAR||LA42_0==OR) ) {
					alt42=1;
				}

				switch (alt42) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:610:43: or ftsFieldGroupImplicitConjunction
					{
					pushFollow(FOLLOW_or_in_ftsFieldGroupExplicitDisjunction4019);
					or107=or();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_or.add(or107.getTree());
					pushFollow(FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction4021);
					ftsFieldGroupImplicitConjunction108=ftsFieldGroupImplicitConjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupImplicitConjunction.add(ftsFieldGroupImplicitConjunction108.getTree());
					}
					break;

				default :
					break loop42;
				}
			}

			// AST REWRITE
			// elements: ftsFieldGroupImplicitConjunction
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 611:17: -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:612:25: ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_DISJUNCTION, "FIELD_DISJUNCTION"), root_1);
				if ( !(stream_ftsFieldGroupImplicitConjunction.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsFieldGroupImplicitConjunction.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsFieldGroupImplicitConjunction.nextTree());
				}
				stream_ftsFieldGroupImplicitConjunction.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupExplicitDisjunction"


	public static class ftsFieldGroupImplicitDisjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupImplicitDisjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:615:1: ftsFieldGroupImplicitDisjunction : ( ( or )? ftsFieldGroupExplicitConjunction )+ -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ ) ;
	public final FTSParser.ftsFieldGroupImplicitDisjunction_return ftsFieldGroupImplicitDisjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupImplicitDisjunction_return retval = new FTSParser.ftsFieldGroupImplicitDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope or109 =null;
		ParserRuleReturnScope ftsFieldGroupExplicitConjunction110 =null;

		RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
		RewriteRuleSubtreeStream stream_ftsFieldGroupExplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExplicitConjunction");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:616:9: ( ( ( or )? ftsFieldGroupExplicitConjunction )+ -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:617:9: ( ( or )? ftsFieldGroupExplicitConjunction )+
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:617:9: ( ( or )? ftsFieldGroupExplicitConjunction )+
			int cnt44=0;
			loop44:
			while (true) {
				int alt44=2;
				int LA44_0 = input.LA(1);
				if ( (LA44_0==BAR||LA44_0==DATETIME||LA44_0==DECIMAL_INTEGER_LITERAL||LA44_0==EQUALS||LA44_0==EXCLAMATION||LA44_0==FLOATING_POINT_LITERAL||(LA44_0 >= FTSPHRASE && LA44_0 <= FTSWORD)||LA44_0==ID||(LA44_0 >= LPAREN && LA44_0 <= LT)||LA44_0==MINUS||LA44_0==NOT||LA44_0==OR||LA44_0==PLUS||LA44_0==QUESTION_MARK||LA44_0==STAR||(LA44_0 >= TILDA && LA44_0 <= TO)) ) {
					alt44=1;
				}

				switch (alt44) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:617:10: ( or )? ftsFieldGroupExplicitConjunction
					{
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:617:10: ( or )?
					int alt43=2;
					int LA43_0 = input.LA(1);
					if ( (LA43_0==OR) ) {
						alt43=1;
					}
					else if ( (LA43_0==BAR) ) {
						int LA43_2 = input.LA(2);
						if ( (LA43_2==BAR) ) {
							alt43=1;
						}
					}
					switch (alt43) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:617:10: or
							{
							pushFollow(FOLLOW_or_in_ftsFieldGroupImplicitDisjunction4106);
							or109=or();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_or.add(or109.getTree());
							}
							break;

					}

					pushFollow(FOLLOW_ftsFieldGroupExplicitConjunction_in_ftsFieldGroupImplicitDisjunction4109);
					ftsFieldGroupExplicitConjunction110=ftsFieldGroupExplicitConjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupExplicitConjunction.add(ftsFieldGroupExplicitConjunction110.getTree());
					}
					break;

				default :
					if ( cnt44 >= 1 ) break loop44;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(44, input);
					throw eee;
				}
				cnt44++;
			}

			// AST REWRITE
			// elements: ftsFieldGroupExplicitConjunction
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 618:17: -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:619:25: ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_DISJUNCTION, "FIELD_DISJUNCTION"), root_1);
				if ( !(stream_ftsFieldGroupExplicitConjunction.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsFieldGroupExplicitConjunction.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsFieldGroupExplicitConjunction.nextTree());
				}
				stream_ftsFieldGroupExplicitConjunction.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupImplicitDisjunction"


	public static class ftsFieldGroupExplicitConjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupExplicitConjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:626:1: ftsFieldGroupExplicitConjunction : ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )* -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) ;
	public final FTSParser.ftsFieldGroupExplicitConjunction_return ftsFieldGroupExplicitConjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupExplicitConjunction_return retval = new FTSParser.ftsFieldGroupExplicitConjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsFieldGroupPrefixed111 =null;
		ParserRuleReturnScope and112 =null;
		ParserRuleReturnScope ftsFieldGroupPrefixed113 =null;

		RewriteRuleSubtreeStream stream_ftsFieldGroupPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupPrefixed");
		RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:627:9: ( ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )* -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:628:9: ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )*
			{
			pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4196);
			ftsFieldGroupPrefixed111=ftsFieldGroupPrefixed();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupPrefixed.add(ftsFieldGroupPrefixed111.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:628:31: ( and ftsFieldGroupPrefixed )*
			loop45:
			while (true) {
				int alt45=2;
				int LA45_0 = input.LA(1);
				if ( ((LA45_0 >= AMP && LA45_0 <= AND)) ) {
					alt45=1;
				}

				switch (alt45) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:628:32: and ftsFieldGroupPrefixed
					{
					pushFollow(FOLLOW_and_in_ftsFieldGroupExplicitConjunction4199);
					and112=and();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_and.add(and112.getTree());
					pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4201);
					ftsFieldGroupPrefixed113=ftsFieldGroupPrefixed();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupPrefixed.add(ftsFieldGroupPrefixed113.getTree());
					}
					break;

				default :
					break loop45;
				}
			}

			// AST REWRITE
			// elements: ftsFieldGroupPrefixed
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 629:17: -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:630:25: ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_CONJUNCTION, "FIELD_CONJUNCTION"), root_1);
				if ( !(stream_ftsFieldGroupPrefixed.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsFieldGroupPrefixed.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsFieldGroupPrefixed.nextTree());
				}
				stream_ftsFieldGroupPrefixed.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupExplicitConjunction"


	public static class ftsFieldGroupImplicitConjunction_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupImplicitConjunction"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:633:1: ftsFieldGroupImplicitConjunction : ( ( and )? ftsFieldGroupPrefixed )+ -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) ;
	public final FTSParser.ftsFieldGroupImplicitConjunction_return ftsFieldGroupImplicitConjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupImplicitConjunction_return retval = new FTSParser.ftsFieldGroupImplicitConjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope and114 =null;
		ParserRuleReturnScope ftsFieldGroupPrefixed115 =null;

		RewriteRuleSubtreeStream stream_ftsFieldGroupPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupPrefixed");
		RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:634:9: ( ( ( and )? ftsFieldGroupPrefixed )+ -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:9: ( ( and )? ftsFieldGroupPrefixed )+
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:9: ( ( and )? ftsFieldGroupPrefixed )+
			int cnt47=0;
			loop47:
			while (true) {
				int alt47=2;
				int LA47_0 = input.LA(1);
				if ( (LA47_0==BAR) ) {
					int LA47_2 = input.LA(2);
					if ( (LA47_2==DATETIME||LA47_2==DECIMAL_INTEGER_LITERAL||LA47_2==EQUALS||LA47_2==FLOATING_POINT_LITERAL||(LA47_2 >= FTSPHRASE && LA47_2 <= FTSWORD)||LA47_2==ID||(LA47_2 >= LPAREN && LA47_2 <= LT)||LA47_2==NOT||LA47_2==QUESTION_MARK||LA47_2==STAR||(LA47_2 >= TILDA && LA47_2 <= TO)) ) {
						alt47=1;
					}

				}
				else if ( ((LA47_0 >= AMP && LA47_0 <= AND)||LA47_0==DATETIME||LA47_0==DECIMAL_INTEGER_LITERAL||LA47_0==EQUALS||LA47_0==EXCLAMATION||LA47_0==FLOATING_POINT_LITERAL||(LA47_0 >= FTSPHRASE && LA47_0 <= FTSWORD)||LA47_0==ID||(LA47_0 >= LPAREN && LA47_0 <= LT)||LA47_0==MINUS||LA47_0==NOT||LA47_0==PLUS||LA47_0==QUESTION_MARK||LA47_0==STAR||(LA47_0 >= TILDA && LA47_0 <= TO)) ) {
					alt47=1;
				}

				switch (alt47) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:10: ( and )? ftsFieldGroupPrefixed
					{
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:10: ( and )?
					int alt46=2;
					int LA46_0 = input.LA(1);
					if ( ((LA46_0 >= AMP && LA46_0 <= AND)) ) {
						alt46=1;
					}
					switch (alt46) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:10: and
							{
							pushFollow(FOLLOW_and_in_ftsFieldGroupImplicitConjunction4286);
							and114=and();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_and.add(and114.getTree());
							}
							break;

					}

					pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupImplicitConjunction4289);
					ftsFieldGroupPrefixed115=ftsFieldGroupPrefixed();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupPrefixed.add(ftsFieldGroupPrefixed115.getTree());
					}
					break;

				default :
					if ( cnt47 >= 1 ) break loop47;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(47, input);
					throw eee;
				}
				cnt47++;
			}

			// AST REWRITE
			// elements: ftsFieldGroupPrefixed
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 636:17: -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:637:25: ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_CONJUNCTION, "FIELD_CONJUNCTION"), root_1);
				if ( !(stream_ftsFieldGroupPrefixed.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsFieldGroupPrefixed.hasNext() ) {
					adaptor.addChild(root_1, stream_ftsFieldGroupPrefixed.nextTree());
				}
				stream_ftsFieldGroupPrefixed.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupImplicitConjunction"


	public static class ftsFieldGroupPrefixed_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupPrefixed"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:1: ftsFieldGroupPrefixed : ( ( not )=> not ftsFieldGroupTest ( boost )? -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? ) | ftsFieldGroupTest ( boost )? -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? ) | PLUS ftsFieldGroupTest ( boost )? -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? ) | BAR ftsFieldGroupTest ( boost )? -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? ) | MINUS ftsFieldGroupTest ( boost )? -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? ) );
	public final FTSParser.ftsFieldGroupPrefixed_return ftsFieldGroupPrefixed() throws RecognitionException {
		FTSParser.ftsFieldGroupPrefixed_return retval = new FTSParser.ftsFieldGroupPrefixed_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token PLUS121=null;
		Token BAR124=null;
		Token MINUS127=null;
		ParserRuleReturnScope not116 =null;
		ParserRuleReturnScope ftsFieldGroupTest117 =null;
		ParserRuleReturnScope boost118 =null;
		ParserRuleReturnScope ftsFieldGroupTest119 =null;
		ParserRuleReturnScope boost120 =null;
		ParserRuleReturnScope ftsFieldGroupTest122 =null;
		ParserRuleReturnScope boost123 =null;
		ParserRuleReturnScope ftsFieldGroupTest125 =null;
		ParserRuleReturnScope boost126 =null;
		ParserRuleReturnScope ftsFieldGroupTest128 =null;
		ParserRuleReturnScope boost129 =null;

		Object PLUS121_tree=null;
		Object BAR124_tree=null;
		Object MINUS127_tree=null;
		RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
		RewriteRuleTokenStream stream_MINUS=new RewriteRuleTokenStream(adaptor,"token MINUS");
		RewriteRuleTokenStream stream_BAR=new RewriteRuleTokenStream(adaptor,"token BAR");
		RewriteRuleSubtreeStream stream_not=new RewriteRuleSubtreeStream(adaptor,"rule not");
		RewriteRuleSubtreeStream stream_boost=new RewriteRuleSubtreeStream(adaptor,"rule boost");
		RewriteRuleSubtreeStream stream_ftsFieldGroupTest=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTest");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:641:9: ( ( not )=> not ftsFieldGroupTest ( boost )? -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? ) | ftsFieldGroupTest ( boost )? -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? ) | PLUS ftsFieldGroupTest ( boost )? -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? ) | BAR ftsFieldGroupTest ( boost )? -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? ) | MINUS ftsFieldGroupTest ( boost )? -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? ) )
			int alt53=5;
			int LA53_0 = input.LA(1);
			if ( (LA53_0==NOT) ) {
				int LA53_1 = input.LA(2);
				if ( (synpred23_FTS()) ) {
					alt53=1;
				}
				else if ( (true) ) {
					alt53=2;
				}

			}
			else if ( (LA53_0==EXCLAMATION) && (synpred23_FTS())) {
				alt53=1;
			}
			else if ( (LA53_0==DATETIME||LA53_0==DECIMAL_INTEGER_LITERAL||LA53_0==EQUALS||LA53_0==FLOATING_POINT_LITERAL||(LA53_0 >= FTSPHRASE && LA53_0 <= FTSWORD)||LA53_0==ID||(LA53_0 >= LPAREN && LA53_0 <= LT)||LA53_0==QUESTION_MARK||LA53_0==STAR||(LA53_0 >= TILDA && LA53_0 <= TO)) ) {
				alt53=2;
			}
			else if ( (LA53_0==PLUS) ) {
				alt53=3;
			}
			else if ( (LA53_0==BAR) ) {
				alt53=4;
			}
			else if ( (LA53_0==MINUS) ) {
				alt53=5;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 53, 0, input);
				throw nvae;
			}

			switch (alt53) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:642:9: ( not )=> not ftsFieldGroupTest ( boost )?
					{
					pushFollow(FOLLOW_not_in_ftsFieldGroupPrefixed4379);
					not116=not();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_not.add(not116.getTree());
					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4381);
					ftsFieldGroupTest117=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest117.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:642:40: ( boost )?
					int alt48=2;
					int LA48_0 = input.LA(1);
					if ( (LA48_0==CARAT) ) {
						alt48=1;
					}
					switch (alt48) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:642:40: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4383);
							boost118=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost118.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupTest, boost
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 643:17: -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:644:25: ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_NEGATION, "FIELD_NEGATION"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:644:60: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:645:11: ftsFieldGroupTest ( boost )?
					{
					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4447);
					ftsFieldGroupTest119=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest119.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:645:29: ( boost )?
					int alt49=2;
					int LA49_0 = input.LA(1);
					if ( (LA49_0==CARAT) ) {
						alt49=1;
					}
					switch (alt49) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:645:29: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4449);
							boost120=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost120.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupTest, boost
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 646:17: -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:647:25: ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_DEFAULT, "FIELD_DEFAULT"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:647:59: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:648:11: PLUS ftsFieldGroupTest ( boost )?
					{
					PLUS121=(Token)match(input,PLUS,FOLLOW_PLUS_in_ftsFieldGroupPrefixed4513); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PLUS.add(PLUS121);

					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4515);
					ftsFieldGroupTest122=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest122.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:648:34: ( boost )?
					int alt50=2;
					int LA50_0 = input.LA(1);
					if ( (LA50_0==CARAT) ) {
						alt50=1;
					}
					switch (alt50) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:648:34: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4517);
							boost123=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost123.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupTest, boost
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 649:17: -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:650:25: ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_MANDATORY, "FIELD_MANDATORY"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:650:61: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:11: BAR ftsFieldGroupTest ( boost )?
					{
					BAR124=(Token)match(input,BAR,FOLLOW_BAR_in_ftsFieldGroupPrefixed4581); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BAR.add(BAR124);

					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4583);
					ftsFieldGroupTest125=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest125.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:33: ( boost )?
					int alt51=2;
					int LA51_0 = input.LA(1);
					if ( (LA51_0==CARAT) ) {
						alt51=1;
					}
					switch (alt51) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:33: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4585);
							boost126=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost126.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: boost, ftsFieldGroupTest
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 652:17: -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:653:25: ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_OPTIONAL, "FIELD_OPTIONAL"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:653:60: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:11: MINUS ftsFieldGroupTest ( boost )?
					{
					MINUS127=(Token)match(input,MINUS,FOLLOW_MINUS_in_ftsFieldGroupPrefixed4649); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_MINUS.add(MINUS127);

					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4651);
					ftsFieldGroupTest128=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest128.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:35: ( boost )?
					int alt52=2;
					int LA52_0 = input.LA(1);
					if ( (LA52_0==CARAT) ) {
						alt52=1;
					}
					switch (alt52) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:35: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4653);
							boost129=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost129.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupTest, boost
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 655:17: -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:656:25: ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_EXCLUDE, "FIELD_EXCLUDE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:656:59: ( boost )?
						if ( stream_boost.hasNext() ) {
							adaptor.addChild(root_1, stream_boost.nextTree());
						}
						stream_boost.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupPrefixed"


	public static class ftsFieldGroupTest_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupTest"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:659:1: ftsFieldGroupTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( FG_PROXIMITY ftsFieldGroupProximity ) | ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? ) | ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? ) | ftsFieldGroupPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? ) | ftsFieldGroupExactPhrase ( ( slop )=> slop )? -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? ) | ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? ) | ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )? -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? ) | ftsFieldGroupRange -> ^( FG_RANGE ftsFieldGroupRange ) | LPAREN ftsFieldGroupDisjunction RPAREN -> ftsFieldGroupDisjunction );
	public final FTSParser.ftsFieldGroupTest_return ftsFieldGroupTest() throws RecognitionException {
		FTSParser.ftsFieldGroupTest_return retval = new FTSParser.ftsFieldGroupTest_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LPAREN144=null;
		Token RPAREN146=null;
		ParserRuleReturnScope ftsFieldGroupProximity130 =null;
		ParserRuleReturnScope ftsFieldGroupTerm131 =null;
		ParserRuleReturnScope fuzzy132 =null;
		ParserRuleReturnScope ftsFieldGroupExactTerm133 =null;
		ParserRuleReturnScope fuzzy134 =null;
		ParserRuleReturnScope ftsFieldGroupPhrase135 =null;
		ParserRuleReturnScope slop136 =null;
		ParserRuleReturnScope ftsFieldGroupExactPhrase137 =null;
		ParserRuleReturnScope slop138 =null;
		ParserRuleReturnScope ftsFieldGroupTokenisedPhrase139 =null;
		ParserRuleReturnScope slop140 =null;
		ParserRuleReturnScope ftsFieldGroupSynonym141 =null;
		ParserRuleReturnScope fuzzy142 =null;
		ParserRuleReturnScope ftsFieldGroupRange143 =null;
		ParserRuleReturnScope ftsFieldGroupDisjunction145 =null;

		Object LPAREN144_tree=null;
		Object RPAREN146_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_ftsFieldGroupRange=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupRange");
		RewriteRuleSubtreeStream stream_ftsFieldGroupPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupPhrase");
		RewriteRuleSubtreeStream stream_ftsFieldGroupExactPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactPhrase");
		RewriteRuleSubtreeStream stream_ftsFieldGroupTokenisedPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTokenisedPhrase");
		RewriteRuleSubtreeStream stream_fuzzy=new RewriteRuleSubtreeStream(adaptor,"rule fuzzy");
		RewriteRuleSubtreeStream stream_slop=new RewriteRuleSubtreeStream(adaptor,"rule slop");
		RewriteRuleSubtreeStream stream_ftsFieldGroupTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTerm");
		RewriteRuleSubtreeStream stream_ftsFieldGroupSynonym=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupSynonym");
		RewriteRuleSubtreeStream stream_ftsFieldGroupExactTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactTerm");
		RewriteRuleSubtreeStream stream_ftsFieldGroupDisjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupDisjunction");
		RewriteRuleSubtreeStream stream_ftsFieldGroupProximity=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupProximity");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:660:9: ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( FG_PROXIMITY ftsFieldGroupProximity ) | ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? ) | ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? ) | ftsFieldGroupPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? ) | ftsFieldGroupExactPhrase ( ( slop )=> slop )? -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? ) | ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? ) | ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )? -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? ) | ftsFieldGroupRange -> ^( FG_RANGE ftsFieldGroupRange ) | LPAREN ftsFieldGroupDisjunction RPAREN -> ftsFieldGroupDisjunction )
			int alt60=9;
			switch ( input.LA(1) ) {
			case DATETIME:
			case DECIMAL_INTEGER_LITERAL:
			case FLOATING_POINT_LITERAL:
			case FTSPRE:
			case FTSWILD:
			case FTSWORD:
			case ID:
				{
				switch ( input.LA(2) ) {
				case STAR:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						int LA60_16 = input.LA(4);
						if ( (LA60_16==DECIMAL_INTEGER_LITERAL) ) {
							int LA60_20 = input.LA(5);
							if ( (LA60_20==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA60_23 = input.LA(7);
									if ( (synpred24_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								case AMP:
								case AND:
								case BAR:
								case CARAT:
								case EQUALS:
								case EXCLAMATION:
								case FTSPHRASE:
								case LPAREN:
								case LSQUARE:
								case LT:
								case MINUS:
								case OR:
								case PLUS:
								case QUESTION_MARK:
								case RPAREN:
								case STAR:
								case TILDA:
									{
									alt60=2;
									}
									break;
								case DATETIME:
								case DECIMAL_INTEGER_LITERAL:
								case FLOATING_POINT_LITERAL:
								case FTSPRE:
								case FTSWILD:
								case FTSWORD:
								case ID:
									{
									int LA60_24 = input.LA(7);
									if ( (synpred24_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								case TO:
									{
									int LA60_25 = input.LA(7);
									if ( (synpred24_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								default:
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 60, 22, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA60_20 >= AMP && LA60_20 <= AND)||LA60_20==BAR||LA60_20==CARAT||LA60_20==COMMA||LA60_20==DATETIME||LA60_20==DECIMAL_INTEGER_LITERAL||(LA60_20 >= DOT && LA60_20 <= DOTDOT)||LA60_20==EQUALS||LA60_20==EXCLAMATION||LA60_20==FLOATING_POINT_LITERAL||(LA60_20 >= FTSPHRASE && LA60_20 <= FTSWORD)||LA60_20==ID||(LA60_20 >= LPAREN && LA60_20 <= LT)||LA60_20==MINUS||LA60_20==NOT||LA60_20==OR||LA60_20==PLUS||LA60_20==QUESTION_MARK||LA60_20==STAR||(LA60_20 >= TILDA && LA60_20 <= TO)) ) {
								alt60=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 60, 20, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA60_16==RPAREN) && (synpred24_FTS())) {
							alt60=1;
						}
						else if ( ((LA60_16 >= AMP && LA60_16 <= AND)||LA60_16==BAR||LA60_16==DATETIME||LA60_16==EQUALS||LA60_16==EXCLAMATION||LA60_16==FLOATING_POINT_LITERAL||(LA60_16 >= FTSPHRASE && LA60_16 <= FTSWORD)||LA60_16==ID||(LA60_16 >= LPAREN && LA60_16 <= LT)||LA60_16==MINUS||LA60_16==NOT||LA60_16==OR||LA60_16==PLUS||LA60_16==QUESTION_MARK||LA60_16==STAR||(LA60_16 >= TILDA && LA60_16 <= TO)) ) {
							alt60=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 60, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA60_17 = input.LA(4);
						if ( (synpred24_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					case AMP:
					case AND:
					case BAR:
					case CARAT:
					case COMMA:
					case DOT:
					case DOTDOT:
					case EQUALS:
					case EXCLAMATION:
					case FTSPHRASE:
					case LSQUARE:
					case LT:
					case MINUS:
					case OR:
					case PLUS:
					case QUESTION_MARK:
					case RPAREN:
					case STAR:
					case TILDA:
						{
						alt60=2;
						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
					case ID:
						{
						int LA60_18 = input.LA(4);
						if ( (synpred24_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					case TO:
						{
						int LA60_19 = input.LA(4);
						if ( (synpred24_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 60, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case AMP:
				case AND:
				case BAR:
				case CARAT:
				case COMMA:
				case DATETIME:
				case DECIMAL_INTEGER_LITERAL:
				case DOT:
				case EQUALS:
				case EXCLAMATION:
				case FLOATING_POINT_LITERAL:
				case FTSPHRASE:
				case FTSPRE:
				case FTSWILD:
				case FTSWORD:
				case ID:
				case LPAREN:
				case LSQUARE:
				case LT:
				case MINUS:
				case NOT:
				case OR:
				case PLUS:
				case QUESTION_MARK:
				case RPAREN:
				case TILDA:
				case TO:
					{
					alt60=2;
					}
					break;
				case DOTDOT:
					{
					alt60=8;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case STAR:
				{
				int LA60_2 = input.LA(2);
				if ( ((LA60_2 >= AMP && LA60_2 <= AND)||LA60_2==BAR||LA60_2==CARAT||LA60_2==COMMA||LA60_2==DATETIME||LA60_2==DECIMAL_INTEGER_LITERAL||LA60_2==DOT||LA60_2==EQUALS||LA60_2==EXCLAMATION||LA60_2==FLOATING_POINT_LITERAL||(LA60_2 >= FTSPHRASE && LA60_2 <= FTSWORD)||LA60_2==ID||(LA60_2 >= LPAREN && LA60_2 <= LT)||LA60_2==MINUS||LA60_2==NOT||LA60_2==OR||LA60_2==PLUS||LA60_2==QUESTION_MARK||LA60_2==RPAREN||LA60_2==STAR||(LA60_2 >= TILDA && LA60_2 <= TO)) ) {
					alt60=2;
				}
				else if ( (LA60_2==DOTDOT) ) {
					alt60=8;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EQUALS:
				{
				int LA60_3 = input.LA(2);
				if ( (LA60_3==DATETIME||LA60_3==DECIMAL_INTEGER_LITERAL||LA60_3==FLOATING_POINT_LITERAL||(LA60_3 >= FTSPRE && LA60_3 <= FTSWORD)||LA60_3==ID||LA60_3==NOT||LA60_3==QUESTION_MARK||LA60_3==STAR||LA60_3==TO) ) {
					alt60=3;
				}
				else if ( (LA60_3==EQUALS) ) {
					alt60=5;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case FTSPHRASE:
				{
				int LA60_4 = input.LA(2);
				if ( ((LA60_4 >= AMP && LA60_4 <= AND)||LA60_4==BAR||LA60_4==CARAT||LA60_4==DATETIME||LA60_4==DECIMAL_INTEGER_LITERAL||LA60_4==EQUALS||LA60_4==EXCLAMATION||LA60_4==FLOATING_POINT_LITERAL||(LA60_4 >= FTSPHRASE && LA60_4 <= FTSWORD)||LA60_4==ID||(LA60_4 >= LPAREN && LA60_4 <= LT)||LA60_4==MINUS||LA60_4==NOT||LA60_4==OR||LA60_4==PLUS||LA60_4==QUESTION_MARK||LA60_4==RPAREN||LA60_4==STAR||(LA60_4 >= TILDA && LA60_4 <= TO)) ) {
					alt60=4;
				}
				else if ( (LA60_4==DOTDOT) ) {
					alt60=8;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TILDA:
				{
				int LA60_5 = input.LA(2);
				if ( (LA60_5==EQUALS) ) {
					alt60=6;
				}
				else if ( (LA60_5==DATETIME||LA60_5==DECIMAL_INTEGER_LITERAL||LA60_5==FLOATING_POINT_LITERAL||(LA60_5 >= FTSPRE && LA60_5 <= FTSWORD)||LA60_5==ID||LA60_5==NOT||LA60_5==QUESTION_MARK||LA60_5==STAR||LA60_5==TO) ) {
					alt60=7;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NOT:
			case TO:
				{
				int LA60_6 = input.LA(2);
				if ( (LA60_6==STAR) ) {
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						int LA60_16 = input.LA(4);
						if ( (LA60_16==DECIMAL_INTEGER_LITERAL) ) {
							int LA60_20 = input.LA(5);
							if ( (LA60_20==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA60_23 = input.LA(7);
									if ( (synpred24_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								case AMP:
								case AND:
								case BAR:
								case CARAT:
								case EQUALS:
								case EXCLAMATION:
								case FTSPHRASE:
								case LPAREN:
								case LSQUARE:
								case LT:
								case MINUS:
								case OR:
								case PLUS:
								case QUESTION_MARK:
								case RPAREN:
								case STAR:
								case TILDA:
									{
									alt60=2;
									}
									break;
								case DATETIME:
								case DECIMAL_INTEGER_LITERAL:
								case FLOATING_POINT_LITERAL:
								case FTSPRE:
								case FTSWILD:
								case FTSWORD:
								case ID:
									{
									int LA60_24 = input.LA(7);
									if ( (synpred24_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								case TO:
									{
									int LA60_25 = input.LA(7);
									if ( (synpred24_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								default:
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 60, 22, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA60_20 >= AMP && LA60_20 <= AND)||LA60_20==BAR||LA60_20==CARAT||LA60_20==COMMA||LA60_20==DATETIME||LA60_20==DECIMAL_INTEGER_LITERAL||(LA60_20 >= DOT && LA60_20 <= DOTDOT)||LA60_20==EQUALS||LA60_20==EXCLAMATION||LA60_20==FLOATING_POINT_LITERAL||(LA60_20 >= FTSPHRASE && LA60_20 <= FTSWORD)||LA60_20==ID||(LA60_20 >= LPAREN && LA60_20 <= LT)||LA60_20==MINUS||LA60_20==NOT||LA60_20==OR||LA60_20==PLUS||LA60_20==QUESTION_MARK||LA60_20==STAR||(LA60_20 >= TILDA && LA60_20 <= TO)) ) {
								alt60=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 60, 20, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA60_16==RPAREN) && (synpred24_FTS())) {
							alt60=1;
						}
						else if ( ((LA60_16 >= AMP && LA60_16 <= AND)||LA60_16==BAR||LA60_16==DATETIME||LA60_16==EQUALS||LA60_16==EXCLAMATION||LA60_16==FLOATING_POINT_LITERAL||(LA60_16 >= FTSPHRASE && LA60_16 <= FTSWORD)||LA60_16==ID||(LA60_16 >= LPAREN && LA60_16 <= LT)||LA60_16==MINUS||LA60_16==NOT||LA60_16==OR||LA60_16==PLUS||LA60_16==QUESTION_MARK||LA60_16==STAR||(LA60_16 >= TILDA && LA60_16 <= TO)) ) {
							alt60=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 60, 16, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA60_17 = input.LA(4);
						if ( (synpred24_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					case AMP:
					case AND:
					case BAR:
					case CARAT:
					case COMMA:
					case DOT:
					case DOTDOT:
					case EQUALS:
					case EXCLAMATION:
					case FTSPHRASE:
					case LSQUARE:
					case LT:
					case MINUS:
					case OR:
					case PLUS:
					case QUESTION_MARK:
					case RPAREN:
					case STAR:
					case TILDA:
						{
						alt60=2;
						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
					case ID:
						{
						int LA60_18 = input.LA(4);
						if ( (synpred24_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					case TO:
						{
						int LA60_19 = input.LA(4);
						if ( (synpred24_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					default:
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 60, 10, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( ((LA60_6 >= AMP && LA60_6 <= AND)||LA60_6==BAR||LA60_6==CARAT||LA60_6==COMMA||LA60_6==DATETIME||LA60_6==DECIMAL_INTEGER_LITERAL||LA60_6==DOT||LA60_6==EQUALS||LA60_6==EXCLAMATION||LA60_6==FLOATING_POINT_LITERAL||(LA60_6 >= FTSPHRASE && LA60_6 <= FTSWORD)||LA60_6==ID||(LA60_6 >= LPAREN && LA60_6 <= LT)||LA60_6==MINUS||LA60_6==NOT||LA60_6==OR||LA60_6==PLUS||LA60_6==QUESTION_MARK||LA60_6==RPAREN||(LA60_6 >= TILDA && LA60_6 <= TO)) ) {
					alt60=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 60, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case QUESTION_MARK:
				{
				alt60=2;
				}
				break;
			case LSQUARE:
			case LT:
				{
				alt60=8;
				}
				break;
			case LPAREN:
				{
				alt60=9;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 60, 0, input);
				throw nvae;
			}
			switch (alt60) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:661:9: ( ftsFieldGroupProximity )=> ftsFieldGroupProximity
					{
					pushFollow(FOLLOW_ftsFieldGroupProximity_in_ftsFieldGroupTest4744);
					ftsFieldGroupProximity130=ftsFieldGroupProximity();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupProximity.add(ftsFieldGroupProximity130.getTree());
					// AST REWRITE
					// elements: ftsFieldGroupProximity
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 662:17: -> ^( FG_PROXIMITY ftsFieldGroupProximity )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:663:25: ^( FG_PROXIMITY ftsFieldGroupProximity )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PROXIMITY, "FG_PROXIMITY"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupProximity.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:664:11: ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupTest4804);
					ftsFieldGroupTerm131=ftsFieldGroupTerm();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTerm.add(ftsFieldGroupTerm131.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:664:29: ( ( fuzzy )=> fuzzy )?
					int alt54=2;
					int LA54_0 = input.LA(1);
					if ( (LA54_0==TILDA) ) {
						int LA54_1 = input.LA(2);
						if ( (LA54_1==DECIMAL_INTEGER_LITERAL||LA54_1==FLOATING_POINT_LITERAL) ) {
							int LA54_3 = input.LA(3);
							if ( (synpred25_FTS()) ) {
								alt54=1;
							}
						}
					}
					switch (alt54) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:664:31: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest4814);
							fuzzy132=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy132.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupTerm, fuzzy
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 665:17: -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:666:25: ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_TERM, "FG_TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTerm.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:666:53: ( fuzzy )?
						if ( stream_fuzzy.hasNext() ) {
							adaptor.addChild(root_1, stream_fuzzy.nextTree());
						}
						stream_fuzzy.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:667:11: ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsFieldGroupExactTerm_in_ftsFieldGroupTest4879);
					ftsFieldGroupExactTerm133=ftsFieldGroupExactTerm();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupExactTerm.add(ftsFieldGroupExactTerm133.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:667:34: ( ( fuzzy )=> fuzzy )?
					int alt55=2;
					int LA55_0 = input.LA(1);
					if ( (LA55_0==TILDA) ) {
						int LA55_1 = input.LA(2);
						if ( (LA55_1==DECIMAL_INTEGER_LITERAL||LA55_1==FLOATING_POINT_LITERAL) ) {
							int LA55_3 = input.LA(3);
							if ( (synpred26_FTS()) ) {
								alt55=1;
							}
						}
					}
					switch (alt55) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:667:36: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest4889);
							fuzzy134=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy134.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupExactTerm, fuzzy
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 668:17: -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:669:25: ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_EXACT_TERM, "FG_EXACT_TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupExactTerm.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:669:64: ( fuzzy )?
						if ( stream_fuzzy.hasNext() ) {
							adaptor.addChild(root_1, stream_fuzzy.nextTree());
						}
						stream_fuzzy.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:670:11: ftsFieldGroupPhrase ( ( slop )=> slop )?
					{
					pushFollow(FOLLOW_ftsFieldGroupPhrase_in_ftsFieldGroupTest4954);
					ftsFieldGroupPhrase135=ftsFieldGroupPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupPhrase.add(ftsFieldGroupPhrase135.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:670:31: ( ( slop )=> slop )?
					int alt56=2;
					int LA56_0 = input.LA(1);
					if ( (LA56_0==TILDA) ) {
						int LA56_1 = input.LA(2);
						if ( (LA56_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA56_3 = input.LA(3);
							if ( (synpred27_FTS()) ) {
								alt56=1;
							}
						}
					}
					switch (alt56) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:670:33: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsFieldGroupTest4964);
							slop136=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop136.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: slop, ftsFieldGroupPhrase
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 671:17: -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:672:25: ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PHRASE, "FG_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupPhrase.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:672:57: ( slop )?
						if ( stream_slop.hasNext() ) {
							adaptor.addChild(root_1, stream_slop.nextTree());
						}
						stream_slop.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:673:11: ftsFieldGroupExactPhrase ( ( slop )=> slop )?
					{
					pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTest5029);
					ftsFieldGroupExactPhrase137=ftsFieldGroupExactPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupExactPhrase.add(ftsFieldGroupExactPhrase137.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:673:36: ( ( slop )=> slop )?
					int alt57=2;
					int LA57_0 = input.LA(1);
					if ( (LA57_0==TILDA) ) {
						int LA57_1 = input.LA(2);
						if ( (LA57_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA57_3 = input.LA(3);
							if ( (synpred28_FTS()) ) {
								alt57=1;
							}
						}
					}
					switch (alt57) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:673:38: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsFieldGroupTest5039);
							slop138=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop138.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupExactPhrase, slop
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 674:17: -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:675:25: ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_EXACT_PHRASE, "FG_EXACT_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupExactPhrase.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:675:68: ( slop )?
						if ( stream_slop.hasNext() ) {
							adaptor.addChild(root_1, stream_slop.nextTree());
						}
						stream_slop.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 6 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:676:11: ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )?
					{
					pushFollow(FOLLOW_ftsFieldGroupTokenisedPhrase_in_ftsFieldGroupTest5104);
					ftsFieldGroupTokenisedPhrase139=ftsFieldGroupTokenisedPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTokenisedPhrase.add(ftsFieldGroupTokenisedPhrase139.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:676:40: ( ( slop )=> slop )?
					int alt58=2;
					int LA58_0 = input.LA(1);
					if ( (LA58_0==TILDA) ) {
						int LA58_1 = input.LA(2);
						if ( (LA58_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA58_3 = input.LA(3);
							if ( (synpred29_FTS()) ) {
								alt58=1;
							}
						}
					}
					switch (alt58) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:676:42: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsFieldGroupTest5114);
							slop140=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop140.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupTokenisedPhrase, slop
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 677:17: -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:678:25: ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PHRASE, "FG_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTokenisedPhrase.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:678:66: ( slop )?
						if ( stream_slop.hasNext() ) {
							adaptor.addChild(root_1, stream_slop.nextTree());
						}
						stream_slop.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 7 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:11: ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsFieldGroupSynonym_in_ftsFieldGroupTest5179);
					ftsFieldGroupSynonym141=ftsFieldGroupSynonym();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupSynonym.add(ftsFieldGroupSynonym141.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:32: ( ( fuzzy )=> fuzzy )?
					int alt59=2;
					int LA59_0 = input.LA(1);
					if ( (LA59_0==TILDA) ) {
						int LA59_1 = input.LA(2);
						if ( (LA59_1==DECIMAL_INTEGER_LITERAL||LA59_1==FLOATING_POINT_LITERAL) ) {
							int LA59_3 = input.LA(3);
							if ( (synpred30_FTS()) ) {
								alt59=1;
							}
						}
					}
					switch (alt59) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:34: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest5189);
							fuzzy142=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy142.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: fuzzy, ftsFieldGroupSynonym
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 680:17: -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:681:25: ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_SYNONYM, "FG_SYNONYM"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupSynonym.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:681:59: ( fuzzy )?
						if ( stream_fuzzy.hasNext() ) {
							adaptor.addChild(root_1, stream_fuzzy.nextTree());
						}
						stream_fuzzy.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 8 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:682:11: ftsFieldGroupRange
					{
					pushFollow(FOLLOW_ftsFieldGroupRange_in_ftsFieldGroupTest5254);
					ftsFieldGroupRange143=ftsFieldGroupRange();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupRange.add(ftsFieldGroupRange143.getTree());
					// AST REWRITE
					// elements: ftsFieldGroupRange
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 683:17: -> ^( FG_RANGE ftsFieldGroupRange )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:684:25: ^( FG_RANGE ftsFieldGroupRange )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_RANGE, "FG_RANGE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupRange.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;
					}

					}
					break;
				case 9 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:685:11: LPAREN ftsFieldGroupDisjunction RPAREN
					{
					LPAREN144=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsFieldGroupTest5314); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN144);

					pushFollow(FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroupTest5316);
					ftsFieldGroupDisjunction145=ftsFieldGroupDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupDisjunction.add(ftsFieldGroupDisjunction145.getTree());
					RPAREN146=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsFieldGroupTest5318); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN146);

					// AST REWRITE
					// elements: ftsFieldGroupDisjunction
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 686:17: -> ftsFieldGroupDisjunction
					{
						adaptor.addChild(root_0, stream_ftsFieldGroupDisjunction.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupTest"


	public static class ftsFieldGroupTerm_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupTerm"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:689:1: ftsFieldGroupTerm : ftsWord ;
	public final FTSParser.ftsFieldGroupTerm_return ftsFieldGroupTerm() throws RecognitionException {
		FTSParser.ftsFieldGroupTerm_return retval = new FTSParser.ftsFieldGroupTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsWord147 =null;


		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:690:9: ( ftsWord )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:691:9: ftsWord
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_ftsWord_in_ftsFieldGroupTerm5371);
			ftsWord147=ftsWord();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWord147.getTree());

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupTerm"


	public static class ftsFieldGroupExactTerm_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupExactTerm"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:694:1: ftsFieldGroupExactTerm : EQUALS ftsFieldGroupTerm -> ftsFieldGroupTerm ;
	public final FTSParser.ftsFieldGroupExactTerm_return ftsFieldGroupExactTerm() throws RecognitionException {
		FTSParser.ftsFieldGroupExactTerm_return retval = new FTSParser.ftsFieldGroupExactTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EQUALS148=null;
		ParserRuleReturnScope ftsFieldGroupTerm149 =null;

		Object EQUALS148_tree=null;
		RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
		RewriteRuleSubtreeStream stream_ftsFieldGroupTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTerm");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:695:9: ( EQUALS ftsFieldGroupTerm -> ftsFieldGroupTerm )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:696:9: EQUALS ftsFieldGroupTerm
			{
			EQUALS148=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsFieldGroupExactTerm5404); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS148);

			pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupExactTerm5406);
			ftsFieldGroupTerm149=ftsFieldGroupTerm();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupTerm.add(ftsFieldGroupTerm149.getTree());
			// AST REWRITE
			// elements: ftsFieldGroupTerm
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 697:17: -> ftsFieldGroupTerm
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupTerm.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupExactTerm"


	public static class ftsFieldGroupPhrase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupPhrase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:700:1: ftsFieldGroupPhrase : FTSPHRASE ;
	public final FTSParser.ftsFieldGroupPhrase_return ftsFieldGroupPhrase() throws RecognitionException {
		FTSParser.ftsFieldGroupPhrase_return retval = new FTSParser.ftsFieldGroupPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token FTSPHRASE150=null;

		Object FTSPHRASE150_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:701:9: ( FTSPHRASE )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:702:9: FTSPHRASE
			{
			root_0 = (Object)adaptor.nil();


			FTSPHRASE150=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsFieldGroupPhrase5459); if (state.failed) return retval;
			if ( state.backtracking==0 ) {
			FTSPHRASE150_tree = (Object)adaptor.create(FTSPHRASE150);
			adaptor.addChild(root_0, FTSPHRASE150_tree);
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupPhrase"


	public static class ftsFieldGroupExactPhrase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupExactPhrase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:705:1: ftsFieldGroupExactPhrase : EQUALS ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase ;
	public final FTSParser.ftsFieldGroupExactPhrase_return ftsFieldGroupExactPhrase() throws RecognitionException {
		FTSParser.ftsFieldGroupExactPhrase_return retval = new FTSParser.ftsFieldGroupExactPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token EQUALS151=null;
		ParserRuleReturnScope ftsFieldGroupExactPhrase152 =null;

		Object EQUALS151_tree=null;
		RewriteRuleTokenStream stream_EQUALS=new RewriteRuleTokenStream(adaptor,"token EQUALS");
		RewriteRuleSubtreeStream stream_ftsFieldGroupExactPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactPhrase");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:706:9: ( EQUALS ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:707:9: EQUALS ftsFieldGroupExactPhrase
			{
			EQUALS151=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsFieldGroupExactPhrase5500); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS151);

			pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupExactPhrase5502);
			ftsFieldGroupExactPhrase152=ftsFieldGroupExactPhrase();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupExactPhrase.add(ftsFieldGroupExactPhrase152.getTree());
			// AST REWRITE
			// elements: ftsFieldGroupExactPhrase
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 708:17: -> ftsFieldGroupExactPhrase
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupExactPhrase.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupExactPhrase"


	public static class ftsFieldGroupTokenisedPhrase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupTokenisedPhrase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:711:1: ftsFieldGroupTokenisedPhrase : TILDA ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase ;
	public final FTSParser.ftsFieldGroupTokenisedPhrase_return ftsFieldGroupTokenisedPhrase() throws RecognitionException {
		FTSParser.ftsFieldGroupTokenisedPhrase_return retval = new FTSParser.ftsFieldGroupTokenisedPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TILDA153=null;
		ParserRuleReturnScope ftsFieldGroupExactPhrase154 =null;

		Object TILDA153_tree=null;
		RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
		RewriteRuleSubtreeStream stream_ftsFieldGroupExactPhrase=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExactPhrase");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:712:9: ( TILDA ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:713:9: TILDA ftsFieldGroupExactPhrase
			{
			TILDA153=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsFieldGroupTokenisedPhrase5563); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA153);

			pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTokenisedPhrase5565);
			ftsFieldGroupExactPhrase154=ftsFieldGroupExactPhrase();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupExactPhrase.add(ftsFieldGroupExactPhrase154.getTree());
			// AST REWRITE
			// elements: ftsFieldGroupExactPhrase
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 714:17: -> ftsFieldGroupExactPhrase
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupExactPhrase.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupTokenisedPhrase"


	public static class ftsFieldGroupSynonym_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupSynonym"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:717:1: ftsFieldGroupSynonym : TILDA ftsFieldGroupTerm -> ftsFieldGroupTerm ;
	public final FTSParser.ftsFieldGroupSynonym_return ftsFieldGroupSynonym() throws RecognitionException {
		FTSParser.ftsFieldGroupSynonym_return retval = new FTSParser.ftsFieldGroupSynonym_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token TILDA155=null;
		ParserRuleReturnScope ftsFieldGroupTerm156 =null;

		Object TILDA155_tree=null;
		RewriteRuleTokenStream stream_TILDA=new RewriteRuleTokenStream(adaptor,"token TILDA");
		RewriteRuleSubtreeStream stream_ftsFieldGroupTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupTerm");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:718:9: ( TILDA ftsFieldGroupTerm -> ftsFieldGroupTerm )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:719:9: TILDA ftsFieldGroupTerm
			{
			TILDA155=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsFieldGroupSynonym5618); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA155);

			pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupSynonym5620);
			ftsFieldGroupTerm156=ftsFieldGroupTerm();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupTerm.add(ftsFieldGroupTerm156.getTree());
			// AST REWRITE
			// elements: ftsFieldGroupTerm
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 720:17: -> ftsFieldGroupTerm
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupTerm.nextTree());
			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupSynonym"


	public static class ftsFieldGroupProximity_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupProximity"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:723:1: ftsFieldGroupProximity : ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+ -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+ ;
	public final FTSParser.ftsFieldGroupProximity_return ftsFieldGroupProximity() throws RecognitionException {
		FTSParser.ftsFieldGroupProximity_return retval = new FTSParser.ftsFieldGroupProximity_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsFieldGroupProximityTerm157 =null;
		ParserRuleReturnScope proximityGroup158 =null;
		ParserRuleReturnScope ftsFieldGroupProximityTerm159 =null;

		RewriteRuleSubtreeStream stream_proximityGroup=new RewriteRuleSubtreeStream(adaptor,"rule proximityGroup");
		RewriteRuleSubtreeStream stream_ftsFieldGroupProximityTerm=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupProximityTerm");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:724:9: ( ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+ -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+ )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:725:9: ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+
			{
			pushFollow(FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5673);
			ftsFieldGroupProximityTerm157=ftsFieldGroupProximityTerm();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupProximityTerm.add(ftsFieldGroupProximityTerm157.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:725:36: ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+
			int cnt61=0;
			loop61:
			while (true) {
				int alt61=2;
				int LA61_0 = input.LA(1);
				if ( (LA61_0==STAR) ) {
					switch ( input.LA(2) ) {
					case NOT:
						{
						int LA61_3 = input.LA(3);
						if ( (synpred31_FTS()) ) {
							alt61=1;
						}

						}
						break;
					case ID:
						{
						int LA61_4 = input.LA(3);
						if ( (synpred31_FTS()) ) {
							alt61=1;
						}

						}
						break;
					case TO:
						{
						int LA61_5 = input.LA(3);
						if ( (synpred31_FTS()) ) {
							alt61=1;
						}

						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
						{
						int LA61_6 = input.LA(3);
						if ( (synpred31_FTS()) ) {
							alt61=1;
						}

						}
						break;
					case LPAREN:
						{
						int LA61_7 = input.LA(3);
						if ( (LA61_7==DECIMAL_INTEGER_LITERAL) ) {
							int LA61_9 = input.LA(4);
							if ( (LA61_9==RPAREN) ) {
								switch ( input.LA(5) ) {
								case NOT:
									{
									int LA61_12 = input.LA(6);
									if ( (synpred31_FTS()) ) {
										alt61=1;
									}

									}
									break;
								case ID:
									{
									int LA61_13 = input.LA(6);
									if ( (synpred31_FTS()) ) {
										alt61=1;
									}

									}
									break;
								case TO:
									{
									int LA61_14 = input.LA(6);
									if ( (synpred31_FTS()) ) {
										alt61=1;
									}

									}
									break;
								case DATETIME:
								case DECIMAL_INTEGER_LITERAL:
								case FLOATING_POINT_LITERAL:
								case FTSPRE:
								case FTSWILD:
								case FTSWORD:
									{
									int LA61_15 = input.LA(6);
									if ( (synpred31_FTS()) ) {
										alt61=1;
									}

									}
									break;
								}
							}

						}
						else if ( (LA61_7==RPAREN) && (synpred31_FTS())) {
							alt61=1;
						}

						}
						break;
					}
				}

				switch (alt61) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:725:38: ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm
					{
					pushFollow(FOLLOW_proximityGroup_in_ftsFieldGroupProximity5683);
					proximityGroup158=proximityGroup();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_proximityGroup.add(proximityGroup158.getTree());
					pushFollow(FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5685);
					ftsFieldGroupProximityTerm159=ftsFieldGroupProximityTerm();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupProximityTerm.add(ftsFieldGroupProximityTerm159.getTree());
					}
					break;

				default :
					if ( cnt61 >= 1 ) break loop61;
					if (state.backtracking>0) {state.failed=true; return retval;}
					EarlyExitException eee = new EarlyExitException(61, input);
					throw eee;
				}
				cnt61++;
			}

			// AST REWRITE
			// elements: ftsFieldGroupProximityTerm, proximityGroup, ftsFieldGroupProximityTerm
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 726:17: -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupProximityTerm.nextTree());
				if ( !(stream_proximityGroup.hasNext()||stream_ftsFieldGroupProximityTerm.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_proximityGroup.hasNext()||stream_ftsFieldGroupProximityTerm.hasNext() ) {
					adaptor.addChild(root_0, stream_proximityGroup.nextTree());
					adaptor.addChild(root_0, stream_ftsFieldGroupProximityTerm.nextTree());
				}
				stream_proximityGroup.reset();
				stream_ftsFieldGroupProximityTerm.reset();

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupProximity"


	public static class ftsFieldGroupProximityTerm_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupProximityTerm"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:729:1: ftsFieldGroupProximityTerm : ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME );
	public final FTSParser.ftsFieldGroupProximityTerm_return ftsFieldGroupProximityTerm() throws RecognitionException {
		FTSParser.ftsFieldGroupProximityTerm_return retval = new FTSParser.ftsFieldGroupProximityTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set160=null;

		Object set160_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:730:9: ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set160=input.LT(1);
			if ( input.LA(1)==DATETIME||input.LA(1)==DECIMAL_INTEGER_LITERAL||input.LA(1)==FLOATING_POINT_LITERAL||(input.LA(1) >= FTSPRE && input.LA(1) <= FTSWORD)||input.LA(1)==ID||input.LA(1)==NOT||input.LA(1)==TO ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set160));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupProximityTerm"


	public static class proximityGroup_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "proximityGroup"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:742:1: proximityGroup : STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )? -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? ) ;
	public final FTSParser.proximityGroup_return proximityGroup() throws RecognitionException {
		FTSParser.proximityGroup_return retval = new FTSParser.proximityGroup_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token STAR161=null;
		Token LPAREN162=null;
		Token DECIMAL_INTEGER_LITERAL163=null;
		Token RPAREN164=null;

		Object STAR161_tree=null;
		Object LPAREN162_tree=null;
		Object DECIMAL_INTEGER_LITERAL163_tree=null;
		Object RPAREN164_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
		RewriteRuleTokenStream stream_DECIMAL_INTEGER_LITERAL=new RewriteRuleTokenStream(adaptor,"token DECIMAL_INTEGER_LITERAL");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:743:9: ( STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )? -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:744:9: STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )?
			{
			STAR161=(Token)match(input,STAR,FOLLOW_STAR_in_proximityGroup5878); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_STAR.add(STAR161);

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:744:14: ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )?
			int alt63=2;
			int LA63_0 = input.LA(1);
			if ( (LA63_0==LPAREN) ) {
				alt63=1;
			}
			switch (alt63) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:744:15: LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN
					{
					LPAREN162=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_proximityGroup5881); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN162);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:744:22: ( DECIMAL_INTEGER_LITERAL )?
					int alt62=2;
					int LA62_0 = input.LA(1);
					if ( (LA62_0==DECIMAL_INTEGER_LITERAL) ) {
						alt62=1;
					}
					switch (alt62) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:744:22: DECIMAL_INTEGER_LITERAL
							{
							DECIMAL_INTEGER_LITERAL163=(Token)match(input,DECIMAL_INTEGER_LITERAL,FOLLOW_DECIMAL_INTEGER_LITERAL_in_proximityGroup5883); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DECIMAL_INTEGER_LITERAL.add(DECIMAL_INTEGER_LITERAL163);

							}
							break;

					}

					RPAREN164=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_proximityGroup5886); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN164);

					}
					break;

			}

			// AST REWRITE
			// elements: DECIMAL_INTEGER_LITERAL
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 745:17: -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:746:25: ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROXIMITY, "PROXIMITY"), root_1);
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:746:37: ( DECIMAL_INTEGER_LITERAL )?
				if ( stream_DECIMAL_INTEGER_LITERAL.hasNext() ) {
					adaptor.addChild(root_1, stream_DECIMAL_INTEGER_LITERAL.nextNode());
				}
				stream_DECIMAL_INTEGER_LITERAL.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "proximityGroup"


	public static class ftsFieldGroupRange_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsFieldGroupRange"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:749:1: ftsFieldGroupRange : ( ftsRangeWord DOTDOT ftsRangeWord -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE | range_left ftsRangeWord TO ftsRangeWord range_right -> range_left ftsRangeWord ftsRangeWord range_right );
	public final FTSParser.ftsFieldGroupRange_return ftsFieldGroupRange() throws RecognitionException {
		FTSParser.ftsFieldGroupRange_return retval = new FTSParser.ftsFieldGroupRange_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token DOTDOT166=null;
		Token TO170=null;
		ParserRuleReturnScope ftsRangeWord165 =null;
		ParserRuleReturnScope ftsRangeWord167 =null;
		ParserRuleReturnScope range_left168 =null;
		ParserRuleReturnScope ftsRangeWord169 =null;
		ParserRuleReturnScope ftsRangeWord171 =null;
		ParserRuleReturnScope range_right172 =null;

		Object DOTDOT166_tree=null;
		Object TO170_tree=null;
		RewriteRuleTokenStream stream_DOTDOT=new RewriteRuleTokenStream(adaptor,"token DOTDOT");
		RewriteRuleTokenStream stream_TO=new RewriteRuleTokenStream(adaptor,"token TO");
		RewriteRuleSubtreeStream stream_range_left=new RewriteRuleSubtreeStream(adaptor,"rule range_left");
		RewriteRuleSubtreeStream stream_range_right=new RewriteRuleSubtreeStream(adaptor,"rule range_right");
		RewriteRuleSubtreeStream stream_ftsRangeWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsRangeWord");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:750:9: ( ftsRangeWord DOTDOT ftsRangeWord -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE | range_left ftsRangeWord TO ftsRangeWord range_right -> range_left ftsRangeWord ftsRangeWord range_right )
			int alt64=2;
			int LA64_0 = input.LA(1);
			if ( (LA64_0==DATETIME||LA64_0==DECIMAL_INTEGER_LITERAL||LA64_0==FLOATING_POINT_LITERAL||(LA64_0 >= FTSPHRASE && LA64_0 <= FTSWORD)||LA64_0==ID||LA64_0==STAR) ) {
				alt64=1;
			}
			else if ( ((LA64_0 >= LSQUARE && LA64_0 <= LT)) ) {
				alt64=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 64, 0, input);
				throw nvae;
			}

			switch (alt64) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:751:9: ftsRangeWord DOTDOT ftsRangeWord
					{
					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5970);
					ftsRangeWord165=ftsRangeWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord165.getTree());
					DOTDOT166=(Token)match(input,DOTDOT,FOLLOW_DOTDOT_in_ftsFieldGroupRange5972); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOTDOT.add(DOTDOT166);

					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5974);
					ftsRangeWord167=ftsRangeWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord167.getTree());
					// AST REWRITE
					// elements: ftsRangeWord, ftsRangeWord
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 752:17: -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE
					{
						adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));
						adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
						adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
						adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:753:11: range_left ftsRangeWord TO ftsRangeWord range_right
					{
					pushFollow(FOLLOW_range_left_in_ftsFieldGroupRange6012);
					range_left168=range_left();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_range_left.add(range_left168.getTree());
					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange6014);
					ftsRangeWord169=ftsRangeWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord169.getTree());
					TO170=(Token)match(input,TO,FOLLOW_TO_in_ftsFieldGroupRange6016); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TO.add(TO170);

					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange6018);
					ftsRangeWord171=ftsRangeWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord171.getTree());
					pushFollow(FOLLOW_range_right_in_ftsFieldGroupRange6020);
					range_right172=range_right();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_range_right.add(range_right172.getTree());
					// AST REWRITE
					// elements: range_right, ftsRangeWord, ftsRangeWord, range_left
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 754:17: -> range_left ftsRangeWord ftsRangeWord range_right
					{
						adaptor.addChild(root_0, stream_range_left.nextTree());
						adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
						adaptor.addChild(root_0, stream_ftsRangeWord.nextTree());
						adaptor.addChild(root_0, stream_range_right.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsFieldGroupRange"


	public static class range_left_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "range_left"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:757:1: range_left : ( LSQUARE -> INCLUSIVE | LT -> EXCLUSIVE );
	public final FTSParser.range_left_return range_left() throws RecognitionException {
		FTSParser.range_left_return retval = new FTSParser.range_left_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LSQUARE173=null;
		Token LT174=null;

		Object LSQUARE173_tree=null;
		Object LT174_tree=null;
		RewriteRuleTokenStream stream_LT=new RewriteRuleTokenStream(adaptor,"token LT");
		RewriteRuleTokenStream stream_LSQUARE=new RewriteRuleTokenStream(adaptor,"token LSQUARE");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:758:9: ( LSQUARE -> INCLUSIVE | LT -> EXCLUSIVE )
			int alt65=2;
			int LA65_0 = input.LA(1);
			if ( (LA65_0==LSQUARE) ) {
				alt65=1;
			}
			else if ( (LA65_0==LT) ) {
				alt65=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 65, 0, input);
				throw nvae;
			}

			switch (alt65) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:759:9: LSQUARE
					{
					LSQUARE173=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_range_left6079); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LSQUARE.add(LSQUARE173);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 760:17: -> INCLUSIVE
					{
						adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:761:11: LT
					{
					LT174=(Token)match(input,LT,FOLLOW_LT_in_range_left6111); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LT.add(LT174);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 762:17: -> EXCLUSIVE
					{
						adaptor.addChild(root_0, (Object)adaptor.create(EXCLUSIVE, "EXCLUSIVE"));
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "range_left"


	public static class range_right_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "range_right"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:765:1: range_right : ( RSQUARE -> INCLUSIVE | GT -> EXCLUSIVE );
	public final FTSParser.range_right_return range_right() throws RecognitionException {
		FTSParser.range_right_return retval = new FTSParser.range_right_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token RSQUARE175=null;
		Token GT176=null;

		Object RSQUARE175_tree=null;
		Object GT176_tree=null;
		RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
		RewriteRuleTokenStream stream_RSQUARE=new RewriteRuleTokenStream(adaptor,"token RSQUARE");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:766:9: ( RSQUARE -> INCLUSIVE | GT -> EXCLUSIVE )
			int alt66=2;
			int LA66_0 = input.LA(1);
			if ( (LA66_0==RSQUARE) ) {
				alt66=1;
			}
			else if ( (LA66_0==GT) ) {
				alt66=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 66, 0, input);
				throw nvae;
			}

			switch (alt66) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:767:9: RSQUARE
					{
					RSQUARE175=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_range_right6164); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_RSQUARE.add(RSQUARE175);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 768:17: -> INCLUSIVE
					{
						adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:769:11: GT
					{
					GT176=(Token)match(input,GT,FOLLOW_GT_in_range_right6196); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_GT.add(GT176);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 770:17: -> EXCLUSIVE
					{
						adaptor.addChild(root_0, (Object)adaptor.create(EXCLUSIVE, "EXCLUSIVE"));
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "range_right"


	public static class fieldReference_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "fieldReference"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:775:1: fieldReference : ( AT )? ( ( prefix )=> prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) ;
	public final FTSParser.fieldReference_return fieldReference() throws RecognitionException {
		FTSParser.fieldReference_return retval = new FTSParser.fieldReference_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AT177=null;
		ParserRuleReturnScope prefix178 =null;
		ParserRuleReturnScope uri179 =null;
		ParserRuleReturnScope identifier180 =null;

		Object AT177_tree=null;
		RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
		RewriteRuleSubtreeStream stream_prefix=new RewriteRuleSubtreeStream(adaptor,"rule prefix");
		RewriteRuleSubtreeStream stream_uri=new RewriteRuleSubtreeStream(adaptor,"rule uri");
		RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:776:9: ( ( AT )? ( ( prefix )=> prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:777:9: ( AT )? ( ( prefix )=> prefix | uri )? identifier
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:777:9: ( AT )?
			int alt67=2;
			int LA67_0 = input.LA(1);
			if ( (LA67_0==AT) ) {
				alt67=1;
			}
			switch (alt67) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:777:9: AT
					{
					AT177=(Token)match(input,AT,FOLLOW_AT_in_fieldReference6252); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_AT.add(AT177);

					}
					break;

			}

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:778:9: ( ( prefix )=> prefix | uri )?
			int alt68=3;
			switch ( input.LA(1) ) {
				case ID:
					{
					int LA68_1 = input.LA(2);
					if ( (LA68_1==DOT) ) {
						int LA68_7 = input.LA(3);
						if ( (LA68_7==ID) ) {
							int LA68_9 = input.LA(4);
							if ( (LA68_9==COLON) ) {
								int LA68_8 = input.LA(5);
								if ( (LA68_8==ID) ) {
									int LA68_11 = input.LA(6);
									if ( (LA68_11==DOT) ) {
										int LA68_16 = input.LA(7);
										if ( (LA68_16==ID) ) {
											int LA68_18 = input.LA(8);
											if ( (LA68_18==COLON) && (synpred32_FTS())) {
												alt68=1;
											}
										}
									}
									else if ( (LA68_11==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
								else if ( (LA68_8==TO) ) {
									int LA68_12 = input.LA(6);
									if ( (LA68_12==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
								else if ( (LA68_8==OR) && (synpred32_FTS())) {
									alt68=1;
								}
								else if ( (LA68_8==AND) && (synpred32_FTS())) {
									alt68=1;
								}
								else if ( (LA68_8==NOT) ) {
									int LA68_15 = input.LA(6);
									if ( (LA68_15==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
							}
						}
					}
					else if ( (LA68_1==COLON) ) {
						int LA68_8 = input.LA(3);
						if ( (LA68_8==ID) ) {
							int LA68_11 = input.LA(4);
							if ( (LA68_11==DOT) ) {
								int LA68_16 = input.LA(5);
								if ( (LA68_16==ID) ) {
									int LA68_18 = input.LA(6);
									if ( (LA68_18==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
					}
					}
					break;
				case TO:
					{
					int LA68_2 = input.LA(2);
					if ( (LA68_2==COLON) ) {
						int LA68_8 = input.LA(3);
						if ( (LA68_8==ID) ) {
							int LA68_11 = input.LA(4);
							if ( (LA68_11==DOT) ) {
								int LA68_16 = input.LA(5);
								if ( (LA68_16==ID) ) {
									int LA68_18 = input.LA(6);
									if ( (LA68_18==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
					}
					}
					break;
				case OR:
					{
					int LA68_3 = input.LA(2);
					if ( (LA68_3==COLON) ) {
						int LA68_8 = input.LA(3);
						if ( (LA68_8==ID) ) {
							int LA68_11 = input.LA(4);
							if ( (LA68_11==DOT) ) {
								int LA68_16 = input.LA(5);
								if ( (LA68_16==ID) ) {
									int LA68_18 = input.LA(6);
									if ( (LA68_18==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
					}
					}
					break;
				case AND:
					{
					int LA68_4 = input.LA(2);
					if ( (LA68_4==COLON) ) {
						int LA68_8 = input.LA(3);
						if ( (LA68_8==ID) ) {
							int LA68_11 = input.LA(4);
							if ( (LA68_11==DOT) ) {
								int LA68_16 = input.LA(5);
								if ( (LA68_16==ID) ) {
									int LA68_18 = input.LA(6);
									if ( (LA68_18==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
					}
					}
					break;
				case NOT:
					{
					int LA68_5 = input.LA(2);
					if ( (LA68_5==COLON) ) {
						int LA68_8 = input.LA(3);
						if ( (LA68_8==ID) ) {
							int LA68_11 = input.LA(4);
							if ( (LA68_11==DOT) ) {
								int LA68_16 = input.LA(5);
								if ( (LA68_16==ID) ) {
									int LA68_18 = input.LA(6);
									if ( (LA68_18==COLON) && (synpred32_FTS())) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred32_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred32_FTS())) {
								alt68=1;
							}
						}
					}
					}
					break;
				case URI:
					{
					alt68=2;
					}
					break;
			}
			switch (alt68) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:779:19: ( prefix )=> prefix
					{
					pushFollow(FOLLOW_prefix_in_fieldReference6289);
					prefix178=prefix();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_prefix.add(prefix178.getTree());
					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:780:19: uri
					{
					pushFollow(FOLLOW_uri_in_fieldReference6309);
					uri179=uri();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_uri.add(uri179.getTree());
					}
					break;

			}

			pushFollow(FOLLOW_identifier_in_fieldReference6330);
			identifier180=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier180.getTree());
			// AST REWRITE
			// elements: uri, identifier, prefix
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 783:17: -> ^( FIELD_REF identifier ( prefix )? ( uri )? )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:784:25: ^( FIELD_REF identifier ( prefix )? ( uri )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_REF, "FIELD_REF"), root_1);
				adaptor.addChild(root_1, stream_identifier.nextTree());
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:784:48: ( prefix )?
				if ( stream_prefix.hasNext() ) {
					adaptor.addChild(root_1, stream_prefix.nextTree());
				}
				stream_prefix.reset();

				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:784:56: ( uri )?
				if ( stream_uri.hasNext() ) {
					adaptor.addChild(root_1, stream_uri.nextTree());
				}
				stream_uri.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "fieldReference"


	public static class tempReference_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "tempReference"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:787:1: tempReference : ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) ;
	public final FTSParser.tempReference_return tempReference() throws RecognitionException {
		FTSParser.tempReference_return retval = new FTSParser.tempReference_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AT181=null;
		ParserRuleReturnScope prefix182 =null;
		ParserRuleReturnScope uri183 =null;
		ParserRuleReturnScope identifier184 =null;

		Object AT181_tree=null;
		RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
		RewriteRuleSubtreeStream stream_prefix=new RewriteRuleSubtreeStream(adaptor,"rule prefix");
		RewriteRuleSubtreeStream stream_uri=new RewriteRuleSubtreeStream(adaptor,"rule uri");
		RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:788:9: ( ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:789:9: ( AT )? ( prefix | uri )? identifier
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:789:9: ( AT )?
			int alt69=2;
			int LA69_0 = input.LA(1);
			if ( (LA69_0==AT) ) {
				alt69=1;
			}
			switch (alt69) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:789:9: AT
					{
					AT181=(Token)match(input,AT,FOLLOW_AT_in_tempReference6417); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_AT.add(AT181);

					}
					break;

			}

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:790:9: ( prefix | uri )?
			int alt70=3;
			switch ( input.LA(1) ) {
				case ID:
					{
					int LA70_1 = input.LA(2);
					if ( (LA70_1==DOT) ) {
						int LA70_7 = input.LA(3);
						if ( (LA70_7==ID) ) {
							int LA70_10 = input.LA(4);
							if ( (LA70_10==COLON) ) {
								alt70=1;
							}
						}
					}
					else if ( (LA70_1==COLON) ) {
						alt70=1;
					}
					}
					break;
				case TO:
					{
					int LA70_2 = input.LA(2);
					if ( (LA70_2==COLON) ) {
						alt70=1;
					}
					}
					break;
				case OR:
					{
					int LA70_3 = input.LA(2);
					if ( (LA70_3==COLON) ) {
						alt70=1;
					}
					}
					break;
				case AND:
					{
					int LA70_4 = input.LA(2);
					if ( (LA70_4==COLON) ) {
						alt70=1;
					}
					}
					break;
				case NOT:
					{
					int LA70_5 = input.LA(2);
					if ( (LA70_5==COLON) ) {
						alt70=1;
					}
					}
					break;
				case URI:
					{
					alt70=2;
					}
					break;
			}
			switch (alt70) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:791:17: prefix
					{
					pushFollow(FOLLOW_prefix_in_tempReference6446);
					prefix182=prefix();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_prefix.add(prefix182.getTree());
					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:792:19: uri
					{
					pushFollow(FOLLOW_uri_in_tempReference6466);
					uri183=uri();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_uri.add(uri183.getTree());
					}
					break;

			}

			pushFollow(FOLLOW_identifier_in_tempReference6487);
			identifier184=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier184.getTree());
			// AST REWRITE
			// elements: uri, identifier, prefix
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 795:17: -> ^( FIELD_REF identifier ( prefix )? ( uri )? )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:796:25: ^( FIELD_REF identifier ( prefix )? ( uri )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_REF, "FIELD_REF"), root_1);
				adaptor.addChild(root_1, stream_identifier.nextTree());
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:796:48: ( prefix )?
				if ( stream_prefix.hasNext() ) {
					adaptor.addChild(root_1, stream_prefix.nextTree());
				}
				stream_prefix.reset();

				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:796:56: ( uri )?
				if ( stream_uri.hasNext() ) {
					adaptor.addChild(root_1, stream_uri.nextTree());
				}
				stream_uri.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "tempReference"


	public static class prefix_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "prefix"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:799:1: prefix : identifier COLON -> ^( PREFIX identifier ) ;
	public final FTSParser.prefix_return prefix() throws RecognitionException {
		FTSParser.prefix_return retval = new FTSParser.prefix_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token COLON186=null;
		ParserRuleReturnScope identifier185 =null;

		Object COLON186_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleSubtreeStream stream_identifier=new RewriteRuleSubtreeStream(adaptor,"rule identifier");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:800:9: ( identifier COLON -> ^( PREFIX identifier ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:801:9: identifier COLON
			{
			pushFollow(FOLLOW_identifier_in_prefix6574);
			identifier185=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier185.getTree());
			COLON186=(Token)match(input,COLON,FOLLOW_COLON_in_prefix6576); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_COLON.add(COLON186);

			// AST REWRITE
			// elements: identifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 802:17: -> ^( PREFIX identifier )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:803:25: ^( PREFIX identifier )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PREFIX, "PREFIX"), root_1);
				adaptor.addChild(root_1, stream_identifier.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "prefix"


	public static class uri_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "uri"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:806:1: uri : URI -> ^( NAME_SPACE URI ) ;
	public final FTSParser.uri_return uri() throws RecognitionException {
		FTSParser.uri_return retval = new FTSParser.uri_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token URI187=null;

		Object URI187_tree=null;
		RewriteRuleTokenStream stream_URI=new RewriteRuleTokenStream(adaptor,"token URI");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:807:9: ( URI -> ^( NAME_SPACE URI ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:808:9: URI
			{
			URI187=(Token)match(input,URI,FOLLOW_URI_in_uri6657); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_URI.add(URI187);

			// AST REWRITE
			// elements: URI
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 809:17: -> ^( NAME_SPACE URI )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:810:25: ^( NAME_SPACE URI )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NAME_SPACE, "NAME_SPACE"), root_1);
				adaptor.addChild(root_1, stream_URI.nextNode());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "uri"


	public static class identifier_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "identifier"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:813:1: identifier : ( ( ID DOT ID )=>id1= ID DOT id2= ID ->| ID -> ID | TO -> TO | OR -> OR | AND -> AND | NOT -> NOT );
	public final FTSParser.identifier_return identifier() throws RecognitionException {
		FTSParser.identifier_return retval = new FTSParser.identifier_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token id1=null;
		Token id2=null;
		Token DOT188=null;
		Token ID189=null;
		Token TO190=null;
		Token OR191=null;
		Token AND192=null;
		Token NOT193=null;

		Object id1_tree=null;
		Object id2_tree=null;
		Object DOT188_tree=null;
		Object ID189_tree=null;
		Object TO190_tree=null;
		Object OR191_tree=null;
		Object AND192_tree=null;
		Object NOT193_tree=null;
		RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
		RewriteRuleTokenStream stream_AND=new RewriteRuleTokenStream(adaptor,"token AND");
		RewriteRuleTokenStream stream_TO=new RewriteRuleTokenStream(adaptor,"token TO");
		RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:814:9: ( ( ID DOT ID )=>id1= ID DOT id2= ID ->| ID -> ID | TO -> TO | OR -> OR | AND -> AND | NOT -> NOT )
			int alt71=6;
			switch ( input.LA(1) ) {
			case ID:
				{
				int LA71_1 = input.LA(2);
				if ( (LA71_1==DOT) && (synpred33_FTS())) {
					alt71=1;
				}
				else if ( (LA71_1==EOF||(LA71_1 >= AMP && LA71_1 <= BAR)||(LA71_1 >= CARAT && LA71_1 <= COMMA)||LA71_1==DATETIME||LA71_1==DECIMAL_INTEGER_LITERAL||LA71_1==EQUALS||LA71_1==EXCLAMATION||LA71_1==FLOATING_POINT_LITERAL||(LA71_1 >= FTSPHRASE && LA71_1 <= FTSWORD)||LA71_1==ID||(LA71_1 >= LPAREN && LA71_1 <= LT)||LA71_1==MINUS||LA71_1==NOT||(LA71_1 >= OR && LA71_1 <= PERCENT)||LA71_1==PLUS||LA71_1==QUESTION_MARK||LA71_1==RPAREN||LA71_1==STAR||(LA71_1 >= TILDA && LA71_1 <= TO)||LA71_1==URI) ) {
					alt71=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 71, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TO:
				{
				alt71=3;
				}
				break;
			case OR:
				{
				alt71=4;
				}
				break;
			case AND:
				{
				alt71=5;
				}
				break;
			case NOT:
				{
				alt71=6;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 71, 0, input);
				throw nvae;
			}
			switch (alt71) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:815:9: ( ID DOT ID )=>id1= ID DOT id2= ID
					{
					id1=(Token)match(input,ID,FOLLOW_ID_in_identifier6759); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(id1);

					DOT188=(Token)match(input,DOT,FOLLOW_DOT_in_identifier6761); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOT.add(DOT188);

					id2=(Token)match(input,ID,FOLLOW_ID_in_identifier6765); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(id2);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 817:17: ->
					{
						adaptor.addChild(root_0, new CommonTree(new CommonToken(FTSLexer.ID, (id1!=null?id1.getText():null)+(DOT188!=null?DOT188.getText():null)+(id2!=null?id2.getText():null))));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:819:12: ID
					{
					ID189=(Token)match(input,ID,FOLLOW_ID_in_identifier6814); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(ID189);

					// AST REWRITE
					// elements: ID
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 820:17: -> ID
					{
						adaptor.addChild(root_0, stream_ID.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:823:12: TO
					{
					TO190=(Token)match(input,TO,FOLLOW_TO_in_identifier6881); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TO.add(TO190);

					// AST REWRITE
					// elements: TO
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 824:17: -> TO
					{
						adaptor.addChild(root_0, stream_TO.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:825:12: OR
					{
					OR191=(Token)match(input,OR,FOLLOW_OR_in_identifier6919); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_OR.add(OR191);

					// AST REWRITE
					// elements: OR
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 826:17: -> OR
					{
						adaptor.addChild(root_0, stream_OR.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:827:12: AND
					{
					AND192=(Token)match(input,AND,FOLLOW_AND_in_identifier6957); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_AND.add(AND192);

					// AST REWRITE
					// elements: AND
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 828:17: -> AND
					{
						adaptor.addChild(root_0, stream_AND.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 6 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:829:12: NOT
					{
					NOT193=(Token)match(input,NOT,FOLLOW_NOT_in_identifier6996); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_NOT.add(NOT193);

					// AST REWRITE
					// elements: NOT
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 830:17: -> NOT
					{
						adaptor.addChild(root_0, stream_NOT.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "identifier"


	public static class ftsWord_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsWord"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:833:1: ftsWord : ftsWordBase ( ( DOT | COMMA ) ftsWordBase )* ;
	public final FTSParser.ftsWord_return ftsWord() throws RecognitionException {
		FTSParser.ftsWord_return retval = new FTSParser.ftsWord_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set195=null;
		ParserRuleReturnScope ftsWordBase194 =null;
		ParserRuleReturnScope ftsWordBase196 =null;

		Object set195_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:834:9: ( ftsWordBase ( ( DOT | COMMA ) ftsWordBase )* )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:835:9: ftsWordBase ( ( DOT | COMMA ) ftsWordBase )*
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_ftsWordBase_in_ftsWord7055);
			ftsWordBase194=ftsWordBase();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase194.getTree());

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:835:21: ( ( DOT | COMMA ) ftsWordBase )*
			loop72:
			while (true) {
				int alt72=2;
				int LA72_0 = input.LA(1);
				if ( (LA72_0==COMMA||LA72_0==DOT) ) {
					alt72=1;
				}

				switch (alt72) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:835:22: ( DOT | COMMA ) ftsWordBase
					{
					set195=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set195));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7064);
					ftsWordBase196=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase196.getTree());

					}
					break;

				default :
					break loop72;
				}
			}

			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsWord"


	public static class ftsWordBase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsWordBase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:839:1: ftsWordBase : ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | STAR | QUESTION_MARK | DATETIME );
	public final FTSParser.ftsWordBase_return ftsWordBase() throws RecognitionException {
		FTSParser.ftsWordBase_return retval = new FTSParser.ftsWordBase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set197=null;

		Object set197_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:840:9: ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | STAR | QUESTION_MARK | DATETIME )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set197=input.LT(1);
			if ( input.LA(1)==DATETIME||input.LA(1)==DECIMAL_INTEGER_LITERAL||input.LA(1)==FLOATING_POINT_LITERAL||(input.LA(1) >= FTSPRE && input.LA(1) <= FTSWORD)||input.LA(1)==ID||input.LA(1)==NOT||input.LA(1)==QUESTION_MARK||input.LA(1)==STAR||input.LA(1)==TO ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set197));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsWordBase"


	public static class number_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "number"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:854:1: number : ( DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL );
	public final FTSParser.number_return number() throws RecognitionException {
		FTSParser.number_return retval = new FTSParser.number_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set198=null;

		Object set198_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:855:9: ( DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set198=input.LT(1);
			if ( input.LA(1)==DECIMAL_INTEGER_LITERAL||input.LA(1)==FLOATING_POINT_LITERAL ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set198));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "number"


	public static class ftsRangeWord_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsRangeWord"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:860:1: ftsRangeWord : ( ID | FTSWORD | FTSPRE | FTSWILD | FTSPHRASE | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME | STAR );
	public final FTSParser.ftsRangeWord_return ftsRangeWord() throws RecognitionException {
		FTSParser.ftsRangeWord_return retval = new FTSParser.ftsRangeWord_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set199=null;

		Object set199_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:861:9: ( ID | FTSWORD | FTSPRE | FTSWILD | FTSPHRASE | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME | STAR )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set199=input.LT(1);
			if ( input.LA(1)==DATETIME||input.LA(1)==DECIMAL_INTEGER_LITERAL||input.LA(1)==FLOATING_POINT_LITERAL||(input.LA(1) >= FTSPHRASE && input.LA(1) <= FTSWORD)||input.LA(1)==ID||input.LA(1)==STAR ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set199));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ftsRangeWord"


	public static class or_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "or"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:875:1: or : ( OR | BAR BAR );
	public final FTSParser.or_return or() throws RecognitionException {
		FTSParser.or_return retval = new FTSParser.or_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OR200=null;
		Token BAR201=null;
		Token BAR202=null;

		Object OR200_tree=null;
		Object BAR201_tree=null;
		Object BAR202_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:876:9: ( OR | BAR BAR )
			int alt73=2;
			int LA73_0 = input.LA(1);
			if ( (LA73_0==OR) ) {
				alt73=1;
			}
			else if ( (LA73_0==BAR) ) {
				alt73=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 73, 0, input);
				throw nvae;
			}

			switch (alt73) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:877:9: OR
					{
					root_0 = (Object)adaptor.nil();


					OR200=(Token)match(input,OR,FOLLOW_OR_in_or7443); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					OR200_tree = (Object)adaptor.create(OR200);
					adaptor.addChild(root_0, OR200_tree);
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:878:11: BAR BAR
					{
					root_0 = (Object)adaptor.nil();


					BAR201=(Token)match(input,BAR,FOLLOW_BAR_in_or7455); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					BAR201_tree = (Object)adaptor.create(BAR201);
					adaptor.addChild(root_0, BAR201_tree);
					}

					BAR202=(Token)match(input,BAR,FOLLOW_BAR_in_or7457); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					BAR202_tree = (Object)adaptor.create(BAR202);
					adaptor.addChild(root_0, BAR202_tree);
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "or"


	public static class and_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "and"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:881:1: and : ( AND | AMP AMP );
	public final FTSParser.and_return and() throws RecognitionException {
		FTSParser.and_return retval = new FTSParser.and_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AND203=null;
		Token AMP204=null;
		Token AMP205=null;

		Object AND203_tree=null;
		Object AMP204_tree=null;
		Object AMP205_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:882:9: ( AND | AMP AMP )
			int alt74=2;
			int LA74_0 = input.LA(1);
			if ( (LA74_0==AND) ) {
				alt74=1;
			}
			else if ( (LA74_0==AMP) ) {
				alt74=2;
			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 74, 0, input);
				throw nvae;
			}

			switch (alt74) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:883:9: AND
					{
					root_0 = (Object)adaptor.nil();


					AND203=(Token)match(input,AND,FOLLOW_AND_in_and7490); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AND203_tree = (Object)adaptor.create(AND203);
					adaptor.addChild(root_0, AND203_tree);
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:884:11: AMP AMP
					{
					root_0 = (Object)adaptor.nil();


					AMP204=(Token)match(input,AMP,FOLLOW_AMP_in_and7502); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AMP204_tree = (Object)adaptor.create(AMP204);
					adaptor.addChild(root_0, AMP204_tree);
					}

					AMP205=(Token)match(input,AMP,FOLLOW_AMP_in_and7504); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AMP205_tree = (Object)adaptor.create(AMP205);
					adaptor.addChild(root_0, AMP205_tree);
					}

					}
					break;

			}
			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "and"


	public static class not_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "not"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:887:1: not : ( NOT | EXCLAMATION );
	public final FTSParser.not_return not() throws RecognitionException {
		FTSParser.not_return retval = new FTSParser.not_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set206=null;

		Object set206_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:888:9: ( NOT | EXCLAMATION )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set206=input.LT(1);
			if ( input.LA(1)==EXCLAMATION||input.LA(1)==NOT ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set206));
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}

			retval.stop = input.LT(-1);

			if ( state.backtracking==0 ) {
			retval.tree = (Object)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
			}
		}

		catch(RecognitionException e)
		{
		   throw e;
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "not"

	// $ANTLR start synpred1_FTS
	public final void synpred1_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:415:9: ( not )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:415:10: not
		{
		pushFollow(FOLLOW_not_in_synpred1_FTS1233);
		not();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred1_FTS

	// $ANTLR start synpred2_FTS
	public final void synpred2_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:447:12: ( ftsFieldGroupProximity )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:447:13: ftsFieldGroupProximity
		{
		pushFollow(FOLLOW_ftsFieldGroupProximity_in_synpred2_FTS1746);
		ftsFieldGroupProximity();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred2_FTS

	// $ANTLR start synpred3_FTS
	public final void synpred3_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:451:12: ( ftsRange )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:451:13: ftsRange
		{
		pushFollow(FOLLOW_ftsRange_in_synpred3_FTS1823);
		ftsRange();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred3_FTS

	// $ANTLR start synpred4_FTS
	public final void synpred4_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:455:12: ( ftsFieldGroup )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:455:13: ftsFieldGroup
		{
		pushFollow(FOLLOW_ftsFieldGroup_in_synpred4_FTS1902);
		ftsFieldGroup();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred4_FTS

	// $ANTLR start synpred5_FTS
	public final void synpred5_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:458:12: ( ftsTermOrPhrase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:458:13: ftsTermOrPhrase
		{
		pushFollow(FOLLOW_ftsTermOrPhrase_in_synpred5_FTS1951);
		ftsTermOrPhrase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred5_FTS

	// $ANTLR start synpred6_FTS
	public final void synpred6_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:12: ( ftsExactTermOrPhrase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:460:13: ftsExactTermOrPhrase
		{
		pushFollow(FOLLOW_ftsExactTermOrPhrase_in_synpred6_FTS1980);
		ftsExactTermOrPhrase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred6_FTS

	// $ANTLR start synpred7_FTS
	public final void synpred7_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:462:12: ( ftsTokenisedTermOrPhrase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:462:13: ftsTokenisedTermOrPhrase
		{
		pushFollow(FOLLOW_ftsTokenisedTermOrPhrase_in_synpred7_FTS2010);
		ftsTokenisedTermOrPhrase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred7_FTS

	// $ANTLR start synpred8_FTS
	public final void synpred8_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:9: ( fieldReference COLON )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:10: fieldReference COLON
		{
		pushFollow(FOLLOW_fieldReference_in_synpred8_FTS2683);
		fieldReference();
		state._fsp--;
		if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred8_FTS2685); if (state.failed) return;

		}

	}
	// $ANTLR end synpred8_FTS

	// $ANTLR start synpred9_FTS
	public final void synpred9_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:516:28: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:516:29: slop
		{
		pushFollow(FOLLOW_slop_in_synpred9_FTS2724);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred9_FTS

	// $ANTLR start synpred10_FTS
	public final void synpred10_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:519:26: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:519:27: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred10_FTS2799);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred10_FTS

	// $ANTLR start synpred11_FTS
	public final void synpred11_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:523:20: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:523:21: slop
		{
		pushFollow(FOLLOW_slop_in_synpred11_FTS2869);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred11_FTS

	// $ANTLR start synpred12_FTS
	public final void synpred12_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:526:18: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:526:19: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred12_FTS2927);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred12_FTS

	// $ANTLR start synpred13_FTS
	public final void synpred13_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:9: ( fieldReference COLON )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:10: fieldReference COLON
		{
		pushFollow(FOLLOW_fieldReference_in_synpred13_FTS3032);
		fieldReference();
		state._fsp--;
		if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred13_FTS3034); if (state.failed) return;

		}

	}
	// $ANTLR end synpred13_FTS

	// $ANTLR start synpred14_FTS
	public final void synpred14_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:537:28: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:537:29: slop
		{
		pushFollow(FOLLOW_slop_in_synpred14_FTS3073);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred14_FTS

	// $ANTLR start synpred15_FTS
	public final void synpred15_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:540:26: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:540:27: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred15_FTS3148);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred15_FTS

	// $ANTLR start synpred16_FTS
	public final void synpred16_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:544:20: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:544:21: slop
		{
		pushFollow(FOLLOW_slop_in_synpred16_FTS3218);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred16_FTS

	// $ANTLR start synpred17_FTS
	public final void synpred17_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:547:18: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:547:19: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred17_FTS3276);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred17_FTS

	// $ANTLR start synpred18_FTS
	public final void synpred18_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:9: ( fieldReference COLON )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:10: fieldReference COLON
		{
		pushFollow(FOLLOW_fieldReference_in_synpred18_FTS3383);
		fieldReference();
		state._fsp--;
		if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred18_FTS3385); if (state.failed) return;

		}

	}
	// $ANTLR end synpred18_FTS

	// $ANTLR start synpred19_FTS
	public final void synpred19_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:559:28: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:559:29: slop
		{
		pushFollow(FOLLOW_slop_in_synpred19_FTS3424);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred19_FTS

	// $ANTLR start synpred20_FTS
	public final void synpred20_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:562:26: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:562:27: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred20_FTS3499);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred20_FTS

	// $ANTLR start synpred21_FTS
	public final void synpred21_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:566:20: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:566:21: slop
		{
		pushFollow(FOLLOW_slop_in_synpred21_FTS3569);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred21_FTS

	// $ANTLR start synpred22_FTS
	public final void synpred22_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:569:18: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:569:19: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred22_FTS3627);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred22_FTS

	// $ANTLR start synpred23_FTS
	public final void synpred23_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:642:9: ( not )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:642:10: not
		{
		pushFollow(FOLLOW_not_in_synpred23_FTS4374);
		not();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred23_FTS

	// $ANTLR start synpred24_FTS
	public final void synpred24_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:661:9: ( ftsFieldGroupProximity )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:661:10: ftsFieldGroupProximity
		{
		pushFollow(FOLLOW_ftsFieldGroupProximity_in_synpred24_FTS4739);
		ftsFieldGroupProximity();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred24_FTS

	// $ANTLR start synpred25_FTS
	public final void synpred25_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:664:31: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:664:32: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred25_FTS4809);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred25_FTS

	// $ANTLR start synpred26_FTS
	public final void synpred26_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:667:36: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:667:37: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred26_FTS4884);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred26_FTS

	// $ANTLR start synpred27_FTS
	public final void synpred27_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:670:33: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:670:34: slop
		{
		pushFollow(FOLLOW_slop_in_synpred27_FTS4959);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred27_FTS

	// $ANTLR start synpred28_FTS
	public final void synpred28_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:673:38: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:673:39: slop
		{
		pushFollow(FOLLOW_slop_in_synpred28_FTS5034);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred28_FTS

	// $ANTLR start synpred29_FTS
	public final void synpred29_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:676:42: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:676:43: slop
		{
		pushFollow(FOLLOW_slop_in_synpred29_FTS5109);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred29_FTS

	// $ANTLR start synpred30_FTS
	public final void synpred30_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:34: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:35: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred30_FTS5184);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred30_FTS

	// $ANTLR start synpred31_FTS
	public final void synpred31_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:725:38: ( proximityGroup )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:725:39: proximityGroup
		{
		pushFollow(FOLLOW_proximityGroup_in_synpred31_FTS5678);
		proximityGroup();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred31_FTS

	// $ANTLR start synpred32_FTS
	public final void synpred32_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:779:19: ( prefix )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:779:20: prefix
		{
		pushFollow(FOLLOW_prefix_in_synpred32_FTS6284);
		prefix();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred32_FTS

	// $ANTLR start synpred33_FTS
	public final void synpred33_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:815:9: ( ID DOT ID )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:815:10: ID DOT ID
		{
		match(input,ID,FOLLOW_ID_in_synpred33_FTS6739); if (state.failed) return;

		match(input,DOT,FOLLOW_DOT_in_synpred33_FTS6741); if (state.failed) return;

		match(input,ID,FOLLOW_ID_in_synpred33_FTS6743); if (state.failed) return;

		}

	}
	// $ANTLR end synpred33_FTS

	// Delegated rules

	public final boolean synpred17_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred17_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred22_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred22_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred1_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred27_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred27_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred14_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred14_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred12_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred12_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred7_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred7_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred31_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred31_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred8_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred8_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred4_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred4_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred19_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred19_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred32_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred32_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred9_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred9_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred16_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred16_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred29_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred29_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred24_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred24_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred11_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred11_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred20_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred20_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred3_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred3_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred10_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred10_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred18_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred18_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred25_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred25_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred2_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred26_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred26_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred13_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred13_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred6_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred6_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred30_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred30_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred21_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred21_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred5_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred5_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred28_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred28_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred23_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred23_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred15_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred15_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred33_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred33_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}


	protected DFA17 dfa17 = new DFA17(this);
	static final String DFA17_eotS =
		"\173\uffff";
	static final String DFA17_eofS =
		"\1\uffff\1\33\1\uffff\2\33\2\uffff\1\33\1\uffff\1\33\2\uffff\1\33\6\uffff"+
		"\1\33\45\uffff\1\33\15\uffff\5\33\42\uffff\1\33\11\uffff\1\33\2\uffff";
	static final String DFA17_minS =
		"\1\5\1\4\1\5\2\4\2\12\1\4\1\5\1\4\2\uffff\1\4\5\uffff\1\15\1\4\1\5\32"+
		"\uffff\10\12\2\uffff\1\4\1\uffff\1\4\1\0\6\uffff\3\0\1\uffff\5\4\1\uffff"+
		"\2\77\1\15\1\4\27\uffff\1\15\3\uffff\2\12\1\4\11\uffff\1\4\2\uffff";
	static final String DFA17_maxS =
		"\5\150\2\12\1\150\1\146\1\150\2\uffff\1\150\5\uffff\1\146\1\150\1\146"+
		"\32\uffff\1\25\2\12\1\25\4\12\2\uffff\1\150\1\uffff\1\150\1\0\6\uffff"+
		"\3\0\1\uffff\5\150\1\uffff\2\77\1\146\1\150\27\uffff\1\146\3\uffff\2\12"+
		"\1\150\11\uffff\1\150\2\uffff";
	static final String DFA17_acceptS =
		"\12\uffff\2\2\1\uffff\1\4\1\5\1\6\1\7\1\10\3\uffff\1\2\31\4\10\uffff\2"+
		"\4\1\uffff\1\4\2\uffff\6\4\3\uffff\1\3\5\uffff\1\4\4\uffff\1\1\26\4\1"+
		"\uffff\3\4\3\uffff\11\4\1\uffff\2\4";
	static final String DFA17_specialS =
		"\1\6\1\16\1\uffff\1\11\1\15\2\uffff\1\17\1\uffff\1\0\2\uffff\1\24\5\uffff"+
		"\1\4\1\27\1\21\44\uffff\1\5\1\uffff\1\13\1\1\6\uffff\1\20\1\23\1\10\1"+
		"\uffff\1\2\1\12\1\7\1\14\1\26\3\uffff\1\3\1\25\27\uffff\1\22\5\uffff\1"+
		"\31\11\uffff\1\30\2\uffff}>";
	static final String[] DFA17_transitionS = {
			"\1\6\1\2\6\uffff\1\7\1\uffff\1\7\10\uffff\1\16\26\uffff\1\7\2\uffff\1"+
			"\11\3\7\11\uffff\1\1\3\uffff\1\20\1\12\1\13\10\uffff\1\4\2\uffff\1\5"+
			"\1\21\5\uffff\1\15\7\uffff\1\14\4\uffff\1\17\1\3\1\uffff\1\10",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\24\1\26\1\uffff\1\44\1\uffff\1\44"+
			"\5\uffff\1\22\1\25\1\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1"+
			"\46\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1"+
			"\37\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff"+
			"\1\23\4\uffff\1\27\1\43\1\uffff\1\45",
			"\1\6\71\uffff\1\57\16\uffff\1\61\2\uffff\1\5\24\uffff\1\60\1\uffff\1"+
			"\10",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\24\1\26\1\uffff\1\44\1\uffff\1\44"+
			"\5\uffff\1\26\2\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46\3"+
			"\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37\2"+
			"\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\23"+
			"\4\uffff\1\27\1\43\1\uffff\1\45",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\24\1\26\1\uffff\1\44\1\uffff\1\44"+
			"\5\uffff\1\26\2\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46\3"+
			"\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37\2"+
			"\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\23"+
			"\4\uffff\1\27\1\43\1\uffff\1\45",
			"\1\24",
			"\1\24",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\uffff\1\26\1\uffff\1\44\1\uffff"+
			"\1\44\5\uffff\1\26\1\25\1\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff"+
			"\1\46\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff"+
			"\1\37\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff"+
			"\1\23\4\uffff\1\27\1\43\1\uffff\1\45",
			"\1\65\71\uffff\1\62\16\uffff\1\66\2\uffff\1\64\24\uffff\1\63",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\3\uffff\1\44\1\uffff\1\44\6\uffff"+
			"\1\25\1\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46\3\44\11\uffff"+
			"\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37\2\uffff\1\35\1"+
			"\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\70\4\uffff\1\67"+
			"\1\43\1\uffff\1\45",
			"",
			"",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\uffff\1\26\1\uffff\1\44\1\uffff"+
			"\1\44\5\uffff\1\26\1\25\1\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff"+
			"\1\46\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff"+
			"\1\37\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff"+
			"\1\70\4\uffff\1\27\1\43\1\uffff\1\45",
			"",
			"",
			"",
			"",
			"",
			"\1\72\1\uffff\1\72\37\uffff\1\72\3\uffff\3\72\11\uffff\1\71\16\uffff"+
			"\1\72\11\uffff\1\72\7\uffff\1\72\5\uffff\1\72",
			"\1\102\1\101\1\42\1\36\1\uffff\1\100\1\uffff\1\76\1\uffff\1\105\1\uffff"+
			"\1\105\5\uffff\1\76\1\75\1\uffff\1\52\2\uffff\1\40\23\uffff\1\105\2\uffff"+
			"\1\46\3\105\11\uffff\1\103\3\uffff\1\73\1\47\1\50\2\uffff\1\56\5\uffff"+
			"\1\74\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff"+
			"\1\70\4\uffff\1\77\1\104\1\uffff\1\45",
			"\1\65\7\uffff\1\113\1\uffff\1\113\37\uffff\1\113\2\uffff\1\112\3\113"+
			"\11\uffff\1\107\3\uffff\1\106\1\12\1\13\10\uffff\1\111\2\uffff\1\64\6"+
			"\uffff\1\114\7\uffff\1\113\5\uffff\1\110",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\24\12\uffff\1\115",
			"\1\24",
			"\1\24",
			"\1\117\12\uffff\1\116",
			"\1\117",
			"\1\117",
			"\1\117",
			"\1\117",
			"",
			"",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\24\1\26\1\uffff\1\44\1\uffff\1\44"+
			"\5\uffff\1\26\2\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46\3"+
			"\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37\2"+
			"\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\70"+
			"\4\uffff\1\27\1\43\1\uffff\1\45",
			"",
			"\1\126\1\125\1\132\1\147\5\uffff\1\134\1\uffff\1\120\10\uffff\1\142"+
			"\2\uffff\1\130\23\uffff\1\134\2\uffff\1\123\3\134\11\uffff\1\127\3\uffff"+
			"\1\144\1\140\1\141\2\uffff\1\124\5\uffff\1\122\2\uffff\1\135\1\145\1"+
			"\uffff\1\146\3\uffff\1\137\2\uffff\1\121\4\uffff\1\131\4\uffff\1\143"+
			"\1\133\1\uffff\1\136",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"\1\uffff",
			"\1\uffff",
			"",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\117\1\151\1\uffff\1\44\1\uffff\1"+
			"\44\5\uffff\1\150\1\25\1\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff"+
			"\1\46\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff"+
			"\1\37\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff"+
			"\1\70\4\uffff\1\152\1\43\1\uffff\1\45",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\117\1\151\1\uffff\1\44\1\uffff\1"+
			"\44\5\uffff\1\151\2\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46"+
			"\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37"+
			"\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\70"+
			"\4\uffff\1\152\1\43\1\uffff\1\45",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\117\1\151\1\uffff\1\44\1\uffff\1"+
			"\44\5\uffff\1\151\2\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46"+
			"\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37"+
			"\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\70"+
			"\4\uffff\1\152\1\43\1\uffff\1\45",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\3\uffff\1\44\1\uffff\1\44\6\uffff"+
			"\1\25\1\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46\3\44\11\uffff"+
			"\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37\2\uffff\1\35\1"+
			"\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\70\4\uffff\1\153"+
			"\1\43\1\uffff\1\45",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\uffff\1\151\1\uffff\1\44\1\uffff"+
			"\1\44\5\uffff\1\151\1\25\1\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff"+
			"\1\46\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff"+
			"\1\37\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff"+
			"\1\70\4\uffff\1\152\1\43\1\uffff\1\45",
			"",
			"\1\154",
			"\1\155",
			"\1\113\1\uffff\1\113\37\uffff\1\113\2\uffff\1\112\3\113\11\uffff\1\113"+
			"\3\uffff\1\106\1\12\1\13\10\uffff\1\114\11\uffff\1\114\7\uffff\1\113"+
			"\5\uffff\1\114",
			"\1\167\1\166\1\132\1\161\1\uffff\1\165\1\uffff\1\157\1\uffff\1\134\1"+
			"\uffff\1\134\5\uffff\1\157\1\163\1\uffff\1\142\2\uffff\1\130\23\uffff"+
			"\1\134\2\uffff\1\123\3\134\11\uffff\1\127\3\uffff\1\144\1\140\1\141\2"+
			"\uffff\1\124\5\uffff\1\122\2\uffff\1\160\1\145\1\uffff\1\146\3\uffff"+
			"\1\137\2\uffff\1\156\4\uffff\1\162\4\uffff\1\164\1\133\1\uffff\1\136",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\171\1\uffff\1\171\37\uffff\1\171\3\uffff\3\171\11\uffff\1\170\16"+
			"\uffff\1\171\11\uffff\1\171\7\uffff\1\171\5\uffff\1\171",
			"",
			"",
			"",
			"\1\24",
			"\1\117",
			"\1\102\1\101\1\42\1\36\1\uffff\1\100\3\uffff\1\105\1\uffff\1\105\10"+
			"\uffff\1\52\2\uffff\1\40\23\uffff\1\105\2\uffff\1\46\3\105\11\uffff\1"+
			"\103\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\74\2\uffff\1\35\1"+
			"\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\70\4\uffff\1\172"+
			"\1\104\1\uffff\1\45",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\32\1\31\1\42\1\36\1\uffff\1\30\1\117\1\151\1\uffff\1\44\1\uffff\1"+
			"\44\5\uffff\1\151\2\uffff\1\52\2\uffff\1\40\23\uffff\1\44\2\uffff\1\46"+
			"\3\44\11\uffff\1\41\3\uffff\1\53\1\47\1\50\2\uffff\1\56\5\uffff\1\37"+
			"\2\uffff\1\35\1\54\1\uffff\1\55\3\uffff\1\51\2\uffff\1\34\4\uffff\1\70"+
			"\4\uffff\1\152\1\43\1\uffff\1\45",
			"",
			""
	};

	static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
	static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
	static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
	static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
	static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
	static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
	static final short[][] DFA17_transition;

	static {
		int numStates = DFA17_transitionS.length;
		DFA17_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
		}
	}

	protected class DFA17 extends DFA {

		public DFA17(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 17;
			this.eot = DFA17_eot;
			this.eof = DFA17_eof;
			this.min = DFA17_min;
			this.max = DFA17_max;
			this.accept = DFA17_accept;
			this.special = DFA17_special;
			this.transition = DFA17_transition;
		}
		@Override
		public String getDescription() {
			return "445:1: ftsTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ( ftsRange )=> ftsRange -> ^( RANGE ftsRange ) | ( ftsFieldGroup )=> ftsFieldGroup -> ftsFieldGroup | ( ftsTermOrPhrase )=> ftsTermOrPhrase | ( ftsExactTermOrPhrase )=> ftsExactTermOrPhrase | ( ftsTokenisedTermOrPhrase )=> ftsTokenisedTermOrPhrase | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA17_9 = input.LA(1);
						 
						int index17_9 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_9==DOTDOT) && (synpred3_FTS())) {s = 21;}
						else if ( (LA17_9==TILDA) && (synpred5_FTS())) {s = 55;}
						else if ( (LA17_9==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_9==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_9==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_9==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_9==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_9==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_9==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_9==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_9==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_9==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_9==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_9==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_9==DATETIME||LA17_9==DECIMAL_INTEGER_LITERAL||LA17_9==FLOATING_POINT_LITERAL||(LA17_9 >= FTSPRE && LA17_9 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_9==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_9==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_9==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_9==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_9==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_9==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_9==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_9==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_9==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_9==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_9==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_9);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA17_60 = input.LA(1);
						 
						int index17_60 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred2_FTS()) ) {s = 81;}
						else if ( (synpred5_FTS()) ) {s = 103;}
						 
						input.seek(index17_60);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA17_71 = input.LA(1);
						 
						int index17_71 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_71==DOT) ) {s = 104;}
						else if ( (LA17_71==COLON) ) {s = 79;}
						else if ( (LA17_71==DOTDOT) && (synpred3_FTS())) {s = 21;}
						else if ( (LA17_71==COMMA) && (synpred5_FTS())) {s = 105;}
						else if ( (LA17_71==TILDA) && (synpred5_FTS())) {s = 106;}
						else if ( (LA17_71==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_71==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_71==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_71==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_71==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_71==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_71==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_71==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_71==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_71==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_71==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_71==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_71==DATETIME||LA17_71==DECIMAL_INTEGER_LITERAL||LA17_71==FLOATING_POINT_LITERAL||(LA17_71 >= FTSPRE && LA17_71 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_71==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_71==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_71==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_71==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_71==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_71==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_71==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_71==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_71==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_71==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_71==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_71);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA17_79 = input.LA(1);
						 
						int index17_79 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_79==LPAREN) && (synpred4_FTS())) {s = 70;}
						else if ( (LA17_79==FTSPHRASE) ) {s = 74;}
						else if ( (LA17_79==LSQUARE) && (synpred3_FTS())) {s = 10;}
						else if ( (LA17_79==LT) && (synpred3_FTS())) {s = 11;}
						else if ( (LA17_79==DATETIME||LA17_79==DECIMAL_INTEGER_LITERAL||LA17_79==FLOATING_POINT_LITERAL||(LA17_79 >= FTSPRE && LA17_79 <= FTSWORD)||LA17_79==ID||LA17_79==STAR) ) {s = 75;}
						else if ( (LA17_79==NOT||LA17_79==QUESTION_MARK||LA17_79==TO) && (synpred5_FTS())) {s = 76;}
						 
						input.seek(index17_79);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA17_18 = input.LA(1);
						 
						int index17_18 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_18==ID) ) {s = 57;}
						else if ( (LA17_18==DATETIME||LA17_18==DECIMAL_INTEGER_LITERAL||LA17_18==FLOATING_POINT_LITERAL||(LA17_18 >= FTSPRE && LA17_18 <= FTSWORD)||LA17_18==NOT||LA17_18==QUESTION_MARK||LA17_18==STAR||LA17_18==TO) && (synpred5_FTS())) {s = 58;}
						 
						input.seek(index17_18);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA17_57 = input.LA(1);
						 
						int index17_57 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_57==COLON) ) {s = 20;}
						else if ( (LA17_57==TILDA) && (synpred5_FTS())) {s = 23;}
						else if ( (LA17_57==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_57==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_57==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_57==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_57==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_57==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_57==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_57==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_57==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_57==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_57==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_57==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_57==DATETIME||LA17_57==DECIMAL_INTEGER_LITERAL||LA17_57==FLOATING_POINT_LITERAL||(LA17_57 >= FTSPRE && LA17_57 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_57==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_57==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_57==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_57==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_57==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_57==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_57==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_57==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_57==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_57==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_57==MINUS) && (synpred5_FTS())) {s = 46;}
						else if ( (LA17_57==COMMA||LA17_57==DOT) && (synpred5_FTS())) {s = 22;}
						 
						input.seek(index17_57);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA17_0 = input.LA(1);
						 
						int index17_0 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_0==ID) ) {s = 1;}
						else if ( (LA17_0==AT) ) {s = 2;}
						else if ( (LA17_0==TO) ) {s = 3;}
						else if ( (LA17_0==NOT) ) {s = 4;}
						else if ( (LA17_0==OR) ) {s = 5;}
						else if ( (LA17_0==AND) ) {s = 6;}
						else if ( (LA17_0==DATETIME||LA17_0==DECIMAL_INTEGER_LITERAL||LA17_0==FLOATING_POINT_LITERAL||(LA17_0 >= FTSPRE && LA17_0 <= FTSWORD)) ) {s = 7;}
						else if ( (LA17_0==URI) ) {s = 8;}
						else if ( (LA17_0==FTSPHRASE) ) {s = 9;}
						else if ( (LA17_0==LSQUARE) && (synpred3_FTS())) {s = 10;}
						else if ( (LA17_0==LT) && (synpred3_FTS())) {s = 11;}
						else if ( (LA17_0==STAR) ) {s = 12;}
						else if ( (LA17_0==QUESTION_MARK) && (synpred5_FTS())) {s = 13;}
						else if ( (LA17_0==EQUALS) && (synpred6_FTS())) {s = 14;}
						else if ( (LA17_0==TILDA) && (synpred7_FTS())) {s = 15;}
						else if ( (LA17_0==LPAREN) ) {s = 16;}
						else if ( (LA17_0==PERCENT) ) {s = 17;}
						 
						input.seek(index17_0);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA17_73 = input.LA(1);
						 
						int index17_73 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_73==COLON) ) {s = 79;}
						else if ( (LA17_73==COMMA||LA17_73==DOT) && (synpred5_FTS())) {s = 105;}
						else if ( (LA17_73==TILDA) && (synpred5_FTS())) {s = 106;}
						else if ( (LA17_73==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_73==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_73==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_73==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_73==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_73==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_73==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_73==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_73==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_73==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_73==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_73==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_73==DATETIME||LA17_73==DECIMAL_INTEGER_LITERAL||LA17_73==FLOATING_POINT_LITERAL||(LA17_73 >= FTSPRE && LA17_73 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_73==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_73==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_73==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_73==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_73==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_73==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_73==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_73==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_73==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_73==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_73==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_73);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA17_69 = input.LA(1);
						 
						int index17_69 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred2_FTS()) ) {s = 81;}
						else if ( (synpred5_FTS()) ) {s = 103;}
						 
						input.seek(index17_69);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA17_3 = input.LA(1);
						 
						int index17_3 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_3==STAR) ) {s = 19;}
						else if ( (LA17_3==COLON) ) {s = 20;}
						else if ( (LA17_3==COMMA||LA17_3==DOT) && (synpred5_FTS())) {s = 22;}
						else if ( (LA17_3==TILDA) && (synpred5_FTS())) {s = 23;}
						else if ( (LA17_3==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_3==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_3==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_3==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_3==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_3==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_3==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_3==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_3==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_3==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_3==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_3==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_3==DATETIME||LA17_3==DECIMAL_INTEGER_LITERAL||LA17_3==FLOATING_POINT_LITERAL||(LA17_3 >= FTSPRE && LA17_3 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_3==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_3==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_3==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_3==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_3==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_3==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_3==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_3==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_3==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_3==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_3);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA17_72 = input.LA(1);
						 
						int index17_72 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_72==COLON) ) {s = 79;}
						else if ( (LA17_72==COMMA||LA17_72==DOT) && (synpred5_FTS())) {s = 105;}
						else if ( (LA17_72==TILDA) && (synpred5_FTS())) {s = 106;}
						else if ( (LA17_72==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_72==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_72==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_72==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_72==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_72==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_72==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_72==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_72==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_72==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_72==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_72==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_72==DATETIME||LA17_72==DECIMAL_INTEGER_LITERAL||LA17_72==FLOATING_POINT_LITERAL||(LA17_72 >= FTSPRE && LA17_72 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_72==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_72==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_72==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_72==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_72==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_72==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_72==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_72==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_72==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_72==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_72==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_72);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA17_59 = input.LA(1);
						 
						int index17_59 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_59==DECIMAL_INTEGER_LITERAL) ) {s = 80;}
						else if ( (LA17_59==RPAREN) && (synpred2_FTS())) {s = 81;}
						else if ( (LA17_59==NOT) && (synpred5_FTS())) {s = 82;}
						else if ( (LA17_59==FTSPHRASE) && (synpred5_FTS())) {s = 83;}
						else if ( (LA17_59==MINUS) && (synpred5_FTS())) {s = 84;}
						else if ( (LA17_59==AND) && (synpred5_FTS())) {s = 85;}
						else if ( (LA17_59==AMP) && (synpred5_FTS())) {s = 86;}
						else if ( (LA17_59==ID) && (synpred5_FTS())) {s = 87;}
						else if ( (LA17_59==EXCLAMATION) && (synpred5_FTS())) {s = 88;}
						else if ( (LA17_59==STAR) && (synpred5_FTS())) {s = 89;}
						else if ( (LA17_59==AT) && (synpred5_FTS())) {s = 90;}
						else if ( (LA17_59==TO) && (synpred5_FTS())) {s = 91;}
						else if ( (LA17_59==DATETIME||LA17_59==FLOATING_POINT_LITERAL||(LA17_59 >= FTSPRE && LA17_59 <= FTSWORD)) && (synpred5_FTS())) {s = 92;}
						else if ( (LA17_59==OR) && (synpred5_FTS())) {s = 93;}
						else if ( (LA17_59==URI) && (synpred5_FTS())) {s = 94;}
						else if ( (LA17_59==QUESTION_MARK) && (synpred5_FTS())) {s = 95;}
						else if ( (LA17_59==LSQUARE) && (synpred5_FTS())) {s = 96;}
						else if ( (LA17_59==LT) && (synpred5_FTS())) {s = 97;}
						else if ( (LA17_59==EQUALS) && (synpred5_FTS())) {s = 98;}
						else if ( (LA17_59==TILDA) && (synpred5_FTS())) {s = 99;}
						else if ( (LA17_59==LPAREN) && (synpred5_FTS())) {s = 100;}
						else if ( (LA17_59==PERCENT) && (synpred5_FTS())) {s = 101;}
						else if ( (LA17_59==PLUS) && (synpred5_FTS())) {s = 102;}
						else if ( (LA17_59==BAR) && (synpred5_FTS())) {s = 103;}
						 
						input.seek(index17_59);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA17_74 = input.LA(1);
						 
						int index17_74 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_74==DOTDOT) && (synpred3_FTS())) {s = 21;}
						else if ( (LA17_74==TILDA) && (synpred5_FTS())) {s = 107;}
						else if ( (LA17_74==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_74==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_74==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_74==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_74==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_74==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_74==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_74==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_74==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_74==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_74==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_74==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_74==DATETIME||LA17_74==DECIMAL_INTEGER_LITERAL||LA17_74==FLOATING_POINT_LITERAL||(LA17_74 >= FTSPRE && LA17_74 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_74==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_74==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_74==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_74==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_74==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_74==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_74==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_74==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_74==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_74==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_74==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_74);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA17_4 = input.LA(1);
						 
						int index17_4 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_4==STAR) ) {s = 19;}
						else if ( (LA17_4==COLON) ) {s = 20;}
						else if ( (LA17_4==COMMA||LA17_4==DOT) && (synpred5_FTS())) {s = 22;}
						else if ( (LA17_4==TILDA) && (synpred5_FTS())) {s = 23;}
						else if ( (LA17_4==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_4==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_4==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_4==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_4==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_4==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_4==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_4==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_4==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_4==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_4==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_4==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_4==DATETIME||LA17_4==DECIMAL_INTEGER_LITERAL||LA17_4==FLOATING_POINT_LITERAL||(LA17_4 >= FTSPRE && LA17_4 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_4==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_4==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_4==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_4==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_4==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_4==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_4==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_4==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_4==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_4==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_4);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA17_1 = input.LA(1);
						 
						int index17_1 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_1==DOT) ) {s = 18;}
						else if ( (LA17_1==STAR) ) {s = 19;}
						else if ( (LA17_1==COLON) ) {s = 20;}
						else if ( (LA17_1==DOTDOT) && (synpred3_FTS())) {s = 21;}
						else if ( (LA17_1==COMMA) && (synpred5_FTS())) {s = 22;}
						else if ( (LA17_1==TILDA) && (synpred5_FTS())) {s = 23;}
						else if ( (LA17_1==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_1==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_1==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_1==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_1==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_1==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_1==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_1==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_1==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_1==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_1==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_1==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_1==DATETIME||LA17_1==DECIMAL_INTEGER_LITERAL||LA17_1==FLOATING_POINT_LITERAL||(LA17_1 >= FTSPRE && LA17_1 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_1==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_1==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_1==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_1==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_1==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_1==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_1==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_1==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_1==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_1==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_1);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA17_7 = input.LA(1);
						 
						int index17_7 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_7==STAR) ) {s = 19;}
						else if ( (LA17_7==DOTDOT) && (synpred3_FTS())) {s = 21;}
						else if ( (LA17_7==COMMA||LA17_7==DOT) && (synpred5_FTS())) {s = 22;}
						else if ( (LA17_7==TILDA) && (synpred5_FTS())) {s = 23;}
						else if ( (LA17_7==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_7==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_7==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_7==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_7==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_7==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_7==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_7==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_7==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_7==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_7==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_7==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_7==DATETIME||LA17_7==DECIMAL_INTEGER_LITERAL||LA17_7==FLOATING_POINT_LITERAL||(LA17_7 >= FTSPRE && LA17_7 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_7==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_7==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_7==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_7==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_7==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_7==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_7==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_7==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_7==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_7==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_7);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA17_67 = input.LA(1);
						 
						int index17_67 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred2_FTS()) ) {s = 81;}
						else if ( (synpred5_FTS()) ) {s = 103;}
						 
						input.seek(index17_67);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA17_20 = input.LA(1);
						 
						int index17_20 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_20==LPAREN) && (synpred4_FTS())) {s = 70;}
						else if ( (LA17_20==ID) ) {s = 71;}
						else if ( (LA17_20==TO) ) {s = 72;}
						else if ( (LA17_20==OR) ) {s = 52;}
						else if ( (LA17_20==AND) ) {s = 53;}
						else if ( (LA17_20==NOT) ) {s = 73;}
						else if ( (LA17_20==FTSPHRASE) ) {s = 74;}
						else if ( (LA17_20==LSQUARE) && (synpred3_FTS())) {s = 10;}
						else if ( (LA17_20==LT) && (synpred3_FTS())) {s = 11;}
						else if ( (LA17_20==DATETIME||LA17_20==DECIMAL_INTEGER_LITERAL||LA17_20==FLOATING_POINT_LITERAL||(LA17_20 >= FTSPRE && LA17_20 <= FTSWORD)||LA17_20==STAR) ) {s = 75;}
						else if ( (LA17_20==QUESTION_MARK) && (synpred5_FTS())) {s = 76;}
						 
						input.seek(index17_20);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA17_104 = input.LA(1);
						 
						int index17_104 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_104==ID) ) {s = 120;}
						else if ( (LA17_104==DATETIME||LA17_104==DECIMAL_INTEGER_LITERAL||LA17_104==FLOATING_POINT_LITERAL||(LA17_104 >= FTSPRE && LA17_104 <= FTSWORD)||LA17_104==NOT||LA17_104==QUESTION_MARK||LA17_104==STAR||LA17_104==TO) && (synpred5_FTS())) {s = 121;}
						 
						input.seek(index17_104);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA17_68 = input.LA(1);
						 
						int index17_68 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred2_FTS()) ) {s = 81;}
						else if ( (synpred5_FTS()) ) {s = 103;}
						 
						input.seek(index17_68);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA17_12 = input.LA(1);
						 
						int index17_12 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_12==DOTDOT) && (synpred3_FTS())) {s = 21;}
						else if ( (LA17_12==COMMA||LA17_12==DOT) && (synpred5_FTS())) {s = 22;}
						else if ( (LA17_12==TILDA) && (synpred5_FTS())) {s = 23;}
						else if ( (LA17_12==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_12==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_12==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_12==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_12==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_12==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_12==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_12==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_12==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_12==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_12==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_12==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_12==DATETIME||LA17_12==DECIMAL_INTEGER_LITERAL||LA17_12==FLOATING_POINT_LITERAL||(LA17_12 >= FTSPRE && LA17_12 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_12==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_12==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_12==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_12==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_12==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_12==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_12==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_12==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_12==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_12==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_12==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_12);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA17_80 = input.LA(1);
						 
						int index17_80 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_80==RPAREN) ) {s = 110;}
						else if ( (LA17_80==COMMA||LA17_80==DOT) && (synpred5_FTS())) {s = 111;}
						else if ( (LA17_80==OR) && (synpred5_FTS())) {s = 112;}
						else if ( (LA17_80==BAR) && (synpred5_FTS())) {s = 113;}
						else if ( (LA17_80==STAR) && (synpred5_FTS())) {s = 114;}
						else if ( (LA17_80==FTSPHRASE) && (synpred5_FTS())) {s = 83;}
						else if ( (LA17_80==MINUS) && (synpred5_FTS())) {s = 84;}
						else if ( (LA17_80==NOT) && (synpred5_FTS())) {s = 82;}
						else if ( (LA17_80==DOTDOT) && (synpred5_FTS())) {s = 115;}
						else if ( (LA17_80==TILDA) && (synpred5_FTS())) {s = 116;}
						else if ( (LA17_80==CARAT) && (synpred5_FTS())) {s = 117;}
						else if ( (LA17_80==AND) && (synpred5_FTS())) {s = 118;}
						else if ( (LA17_80==AMP) && (synpred5_FTS())) {s = 119;}
						else if ( (LA17_80==ID) && (synpred5_FTS())) {s = 87;}
						else if ( (LA17_80==EXCLAMATION) && (synpred5_FTS())) {s = 88;}
						else if ( (LA17_80==QUESTION_MARK) && (synpred5_FTS())) {s = 95;}
						else if ( (LA17_80==AT) && (synpred5_FTS())) {s = 90;}
						else if ( (LA17_80==TO) && (synpred5_FTS())) {s = 91;}
						else if ( (LA17_80==DATETIME||LA17_80==DECIMAL_INTEGER_LITERAL||LA17_80==FLOATING_POINT_LITERAL||(LA17_80 >= FTSPRE && LA17_80 <= FTSWORD)) && (synpred5_FTS())) {s = 92;}
						else if ( (LA17_80==URI) && (synpred5_FTS())) {s = 94;}
						else if ( (LA17_80==LSQUARE) && (synpred5_FTS())) {s = 96;}
						else if ( (LA17_80==LT) && (synpred5_FTS())) {s = 97;}
						else if ( (LA17_80==EQUALS) && (synpred5_FTS())) {s = 98;}
						else if ( (LA17_80==LPAREN) && (synpred5_FTS())) {s = 100;}
						else if ( (LA17_80==PERCENT) && (synpred5_FTS())) {s = 101;}
						else if ( (LA17_80==PLUS) && (synpred5_FTS())) {s = 102;}
						 
						input.seek(index17_80);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA17_75 = input.LA(1);
						 
						int index17_75 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_75==DOTDOT) && (synpred3_FTS())) {s = 21;}
						else if ( (LA17_75==COMMA||LA17_75==DOT) && (synpred5_FTS())) {s = 105;}
						else if ( (LA17_75==TILDA) && (synpred5_FTS())) {s = 106;}
						else if ( (LA17_75==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_75==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_75==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_75==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_75==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_75==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_75==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_75==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_75==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_75==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_75==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_75==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_75==DATETIME||LA17_75==DECIMAL_INTEGER_LITERAL||LA17_75==FLOATING_POINT_LITERAL||(LA17_75 >= FTSPRE && LA17_75 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_75==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_75==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_75==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_75==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_75==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_75==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_75==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_75==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_75==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_75==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_75==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_75);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA17_19 = input.LA(1);
						 
						int index17_19 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_19==LPAREN) ) {s = 59;}
						else if ( (LA17_19==NOT) ) {s = 60;}
						else if ( (LA17_19==DOTDOT) && (synpred5_FTS())) {s = 61;}
						else if ( (LA17_19==COMMA||LA17_19==DOT) && (synpred5_FTS())) {s = 62;}
						else if ( (LA17_19==TILDA) && (synpred5_FTS())) {s = 63;}
						else if ( (LA17_19==CARAT) && (synpred5_FTS())) {s = 64;}
						else if ( (LA17_19==AND) && (synpred5_FTS())) {s = 65;}
						else if ( (LA17_19==AMP) && (synpred5_FTS())) {s = 66;}
						else if ( (LA17_19==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_19==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_19==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_19==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_19==ID) ) {s = 67;}
						else if ( (LA17_19==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_19==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_19==TO) ) {s = 68;}
						else if ( (LA17_19==DATETIME||LA17_19==DECIMAL_INTEGER_LITERAL||LA17_19==FLOATING_POINT_LITERAL||(LA17_19 >= FTSPRE && LA17_19 <= FTSWORD)) ) {s = 69;}
						else if ( (LA17_19==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_19==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_19==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_19==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_19==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_19==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_19==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_19==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_19==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_19==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_19);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA17_120 = input.LA(1);
						 
						int index17_120 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_120==COLON) ) {s = 79;}
						else if ( (LA17_120==TILDA) && (synpred5_FTS())) {s = 106;}
						else if ( (LA17_120==CARAT) && (synpred5_FTS())) {s = 24;}
						else if ( (LA17_120==AND) && (synpred5_FTS())) {s = 25;}
						else if ( (LA17_120==AMP) && (synpred5_FTS())) {s = 26;}
						else if ( (LA17_120==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_120==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_120==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_120==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_120==NOT) && (synpred5_FTS())) {s = 31;}
						else if ( (LA17_120==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_120==ID) && (synpred5_FTS())) {s = 33;}
						else if ( (LA17_120==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_120==TO) && (synpred5_FTS())) {s = 35;}
						else if ( (LA17_120==DATETIME||LA17_120==DECIMAL_INTEGER_LITERAL||LA17_120==FLOATING_POINT_LITERAL||(LA17_120 >= FTSPRE && LA17_120 <= FTSWORD)) && (synpred5_FTS())) {s = 36;}
						else if ( (LA17_120==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_120==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_120==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_120==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_120==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_120==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_120==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_120==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_120==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_120==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_120==MINUS) && (synpred5_FTS())) {s = 46;}
						else if ( (LA17_120==COMMA||LA17_120==DOT) && (synpred5_FTS())) {s = 105;}
						 
						input.seek(index17_120);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA17_110 = input.LA(1);
						 
						int index17_110 = input.index();
						input.rewind();
						s = -1;
						if ( (LA17_110==NOT) ) {s = 60;}
						else if ( (LA17_110==CARAT) && (synpred5_FTS())) {s = 64;}
						else if ( (LA17_110==AND) && (synpred5_FTS())) {s = 65;}
						else if ( (LA17_110==AMP) && (synpred5_FTS())) {s = 66;}
						else if ( (LA17_110==EOF) && (synpred5_FTS())) {s = 27;}
						else if ( (LA17_110==RPAREN) && (synpred5_FTS())) {s = 28;}
						else if ( (LA17_110==OR) && (synpred5_FTS())) {s = 29;}
						else if ( (LA17_110==BAR) && (synpred5_FTS())) {s = 30;}
						else if ( (LA17_110==ID) ) {s = 67;}
						else if ( (LA17_110==EXCLAMATION) && (synpred5_FTS())) {s = 32;}
						else if ( (LA17_110==AT) && (synpred5_FTS())) {s = 34;}
						else if ( (LA17_110==TO) ) {s = 68;}
						else if ( (LA17_110==DATETIME||LA17_110==DECIMAL_INTEGER_LITERAL||LA17_110==FLOATING_POINT_LITERAL||(LA17_110 >= FTSPRE && LA17_110 <= FTSWORD)) ) {s = 69;}
						else if ( (LA17_110==URI) && (synpred5_FTS())) {s = 37;}
						else if ( (LA17_110==FTSPHRASE) && (synpred5_FTS())) {s = 38;}
						else if ( (LA17_110==LSQUARE) && (synpred5_FTS())) {s = 39;}
						else if ( (LA17_110==LT) && (synpred5_FTS())) {s = 40;}
						else if ( (LA17_110==STAR) && (synpred5_FTS())) {s = 56;}
						else if ( (LA17_110==QUESTION_MARK) && (synpred5_FTS())) {s = 41;}
						else if ( (LA17_110==EQUALS) && (synpred5_FTS())) {s = 42;}
						else if ( (LA17_110==TILDA) && (synpred5_FTS())) {s = 122;}
						else if ( (LA17_110==LPAREN) && (synpred5_FTS())) {s = 43;}
						else if ( (LA17_110==PERCENT) && (synpred5_FTS())) {s = 44;}
						else if ( (LA17_110==PLUS) && (synpred5_FTS())) {s = 45;}
						else if ( (LA17_110==MINUS) && (synpred5_FTS())) {s = 46;}
						 
						input.seek(index17_110);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 17, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	public static final BitSet FOLLOW_ftsDisjunction_in_ftsQuery577 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_ftsQuery579 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisExplicitDisjunction_in_ftsDisjunction639 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsExplicitDisjunction_in_ftsDisjunction653 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsImplicitDisjunction_in_ftsDisjunction667 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction700 = new BitSet(new long[]{0x0000000000000082L,0x0000000000020000L});
	public static final BitSet FOLLOW_or_in_ftsExplicitDisjunction703 = new BitSet(new long[]{0x803C80000900A0F0L,0x0000016101164138L});
	public static final BitSet FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction705 = new BitSet(new long[]{0x0000000000000082L,0x0000000000020000L});
	public static final BitSet FOLLOW_cmisConjunction_in_cmisExplicitDisjunction789 = new BitSet(new long[]{0x0000000000000082L,0x0000000000020000L});
	public static final BitSet FOLLOW_or_in_cmisExplicitDisjunction792 = new BitSet(new long[]{0x803C80000000A000L,0x0000004101004100L});
	public static final BitSet FOLLOW_cmisConjunction_in_cmisExplicitDisjunction794 = new BitSet(new long[]{0x0000000000000082L,0x0000000000020000L});
	public static final BitSet FOLLOW_or_in_ftsImplicitDisjunction879 = new BitSet(new long[]{0x803C80000900A0E0L,0x0000016101164138L});
	public static final BitSet FOLLOW_ftsExplicitConjunction_in_ftsImplicitDisjunction882 = new BitSet(new long[]{0x803C80000900A0E2L,0x0000016101164138L});
	public static final BitSet FOLLOW_ftsPrefixed_in_ftsExplicitConjunction969 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsExplicitConjunction972 = new BitSet(new long[]{0x803C80000900A0E0L,0x0000016101164138L});
	public static final BitSet FOLLOW_ftsPrefixed_in_ftsExplicitConjunction974 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsImplicitConjunction1059 = new BitSet(new long[]{0x803C80000900A0E0L,0x0000016101164138L});
	public static final BitSet FOLLOW_ftsPrefixed_in_ftsImplicitConjunction1062 = new BitSet(new long[]{0x803C80000900A0F2L,0x0000016101164138L});
	public static final BitSet FOLLOW_cmisPrefixed_in_cmisConjunction1146 = new BitSet(new long[]{0x803C80000000A002L,0x0000004101004100L});
	public static final BitSet FOLLOW_not_in_ftsPrefixed1238 = new BitSet(new long[]{0x803C80000100A060L,0x0000016101064038L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1240 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1242 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1306 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1308 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_in_ftsPrefixed1372 = new BitSet(new long[]{0x803C80000100A060L,0x0000016101064038L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1374 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1376 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BAR_in_ftsPrefixed1440 = new BitSet(new long[]{0x803C80000100A060L,0x0000016101064038L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1442 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1444 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_ftsPrefixed1508 = new BitSet(new long[]{0x803C80000100A060L,0x0000016101064038L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1510 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1512 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisTest_in_cmisPrefixed1597 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_cmisPrefixed1657 = new BitSet(new long[]{0x803C80000000A000L,0x0000004101004000L});
	public static final BitSet FOLLOW_cmisTest_in_cmisPrefixed1659 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_ftsTest1751 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsRange_in_ftsTest1828 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroup_in_ftsTest1907 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTermOrPhrase_in_ftsTest1956 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsExactTermOrPhrase_in_ftsTest1985 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTokenisedTermOrPhrase_in_ftsTest2015 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_ftsTest2046 = new BitSet(new long[]{0x803C80000900A0F0L,0x0000016101164138L});
	public static final BitSet FOLLOW_ftsDisjunction_in_ftsTest2048 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_ftsTest2050 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_template_in_ftsTest2083 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisTerm_in_cmisTest2136 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisPhrase_in_cmisTest2196 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PERCENT_in_template2277 = new BitSet(new long[]{0x8000000000000060L,0x0000014000024000L});
	public static final BitSet FOLLOW_tempReference_in_template2279 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PERCENT_in_template2339 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
	public static final BitSet FOLLOW_LPAREN_in_template2341 = new BitSet(new long[]{0x8000000000000060L,0x0000014000024000L});
	public static final BitSet FOLLOW_tempReference_in_template2344 = new BitSet(new long[]{0x8000000000000860L,0x0000014008024000L});
	public static final BitSet FOLLOW_COMMA_in_template2346 = new BitSet(new long[]{0x8000000000000060L,0x0000014008024000L});
	public static final BitSet FOLLOW_RPAREN_in_template2351 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_fuzzy2433 = new BitSet(new long[]{0x0000800000008000L});
	public static final BitSet FOLLOW_number_in_fuzzy2435 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_slop2516 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_DECIMAL_INTEGER_LITERAL_in_slop2518 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CARAT_in_boost2599 = new BitSet(new long[]{0x0000800000008000L});
	public static final BitSet FOLLOW_number_in_boost2601 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_ftsTermOrPhrase2690 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsTermOrPhrase2692 = new BitSet(new long[]{0x803C80000000A000L,0x0000004101004000L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2720 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsTermOrPhrase2728 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTermOrPhrase2795 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTermOrPhrase2804 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2865 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsTermOrPhrase2873 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTermOrPhrase2923 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTermOrPhrase2932 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_ftsExactTermOrPhrase3011 = new BitSet(new long[]{0x803C80000000A060L,0x0000014101024000L});
	public static final BitSet FOLLOW_fieldReference_in_ftsExactTermOrPhrase3039 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsExactTermOrPhrase3041 = new BitSet(new long[]{0x803C80000000A000L,0x0000004101004000L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3069 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsExactTermOrPhrase3077 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsExactTermOrPhrase3144 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsExactTermOrPhrase3153 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3214 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsExactTermOrPhrase3222 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsExactTermOrPhrase3272 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsExactTermOrPhrase3281 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_ftsTokenisedTermOrPhrase3362 = new BitSet(new long[]{0x803C80000000A060L,0x0000014101024000L});
	public static final BitSet FOLLOW_fieldReference_in_ftsTokenisedTermOrPhrase3390 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsTokenisedTermOrPhrase3392 = new BitSet(new long[]{0x803C80000000A000L,0x0000004101004000L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3420 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsTokenisedTermOrPhrase3428 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3495 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3504 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3565 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsTokenisedTermOrPhrase3573 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3623 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3632 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_cmisTerm3705 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_cmisPhrase3759 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_ftsRange3814 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsRange3816 = new BitSet(new long[]{0x803C80000000A000L,0x0000000100000030L});
	public static final BitSet FOLLOW_ftsFieldGroupRange_in_ftsRange3820 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_ftsFieldGroup3876 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsFieldGroup3878 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
	public static final BitSet FOLLOW_LPAREN_in_ftsFieldGroup3880 = new BitSet(new long[]{0x803C80000900A0B0L,0x0000006101124138L});
	public static final BitSet FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroup3882 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_ftsFieldGroup3884 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupExplicitDisjunction_in_ftsFieldGroupDisjunction3969 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupImplicitDisjunction_in_ftsFieldGroupDisjunction3983 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction4016 = new BitSet(new long[]{0x0000000000000082L,0x0000000000020000L});
	public static final BitSet FOLLOW_or_in_ftsFieldGroupExplicitDisjunction4019 = new BitSet(new long[]{0x803C80000900A0B0L,0x0000006101104138L});
	public static final BitSet FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction4021 = new BitSet(new long[]{0x0000000000000082L,0x0000000000020000L});
	public static final BitSet FOLLOW_or_in_ftsFieldGroupImplicitDisjunction4106 = new BitSet(new long[]{0x803C80000900A080L,0x0000006101104138L});
	public static final BitSet FOLLOW_ftsFieldGroupExplicitConjunction_in_ftsFieldGroupImplicitDisjunction4109 = new BitSet(new long[]{0x803C80000900A082L,0x0000006101124138L});
	public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4196 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsFieldGroupExplicitConjunction4199 = new BitSet(new long[]{0x803C80000900A080L,0x0000006101104138L});
	public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4201 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsFieldGroupImplicitConjunction4286 = new BitSet(new long[]{0x803C80000900A080L,0x0000006101104138L});
	public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupImplicitConjunction4289 = new BitSet(new long[]{0x803C80000900A0B2L,0x0000006101104138L});
	public static final BitSet FOLLOW_not_in_ftsFieldGroupPrefixed4379 = new BitSet(new long[]{0x803C80000100A000L,0x0000006101004038L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4381 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4383 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4447 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4449 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_in_ftsFieldGroupPrefixed4513 = new BitSet(new long[]{0x803C80000100A000L,0x0000006101004038L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4515 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BAR_in_ftsFieldGroupPrefixed4581 = new BitSet(new long[]{0x803C80000100A000L,0x0000006101004038L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4583 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4585 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_ftsFieldGroupPrefixed4649 = new BitSet(new long[]{0x803C80000100A000L,0x0000006101004038L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4651 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4653 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_ftsFieldGroupTest4744 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupTest4804 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest4814 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupExactTerm_in_ftsFieldGroupTest4879 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest4889 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupPhrase_in_ftsFieldGroupTest4954 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest4964 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTest5029 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest5039 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupTokenisedPhrase_in_ftsFieldGroupTest5104 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest5114 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupSynonym_in_ftsFieldGroupTest5179 = new BitSet(new long[]{0x0000000000000002L,0x0000002000000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest5189 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupRange_in_ftsFieldGroupTest5254 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_ftsFieldGroupTest5314 = new BitSet(new long[]{0x803C80000900A0B0L,0x0000006101124138L});
	public static final BitSet FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroupTest5316 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_ftsFieldGroupTest5318 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsFieldGroupTerm5371 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_ftsFieldGroupExactTerm5404 = new BitSet(new long[]{0x803880000000A000L,0x0000004101004000L});
	public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupExactTerm5406 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsFieldGroupPhrase5459 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_ftsFieldGroupExactPhrase5500 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupExactPhrase5502 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_ftsFieldGroupTokenisedPhrase5563 = new BitSet(new long[]{0x0000000001000000L});
	public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTokenisedPhrase5565 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_ftsFieldGroupSynonym5618 = new BitSet(new long[]{0x803880000000A000L,0x0000004101004000L});
	public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupSynonym5620 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5673 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
	public static final BitSet FOLLOW_proximityGroup_in_ftsFieldGroupProximity5683 = new BitSet(new long[]{0x803880000000A000L,0x0000004000004000L});
	public static final BitSet FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5685 = new BitSet(new long[]{0x0000000000000002L,0x0000000100000000L});
	public static final BitSet FOLLOW_STAR_in_proximityGroup5878 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
	public static final BitSet FOLLOW_LPAREN_in_proximityGroup5881 = new BitSet(new long[]{0x0000000000008000L,0x0000000008000000L});
	public static final BitSet FOLLOW_DECIMAL_INTEGER_LITERAL_in_proximityGroup5883 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
	public static final BitSet FOLLOW_RPAREN_in_proximityGroup5886 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5970 = new BitSet(new long[]{0x0000000000400000L});
	public static final BitSet FOLLOW_DOTDOT_in_ftsFieldGroupRange5972 = new BitSet(new long[]{0x803C80000000A000L,0x0000000100000000L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5974 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_range_left_in_ftsFieldGroupRange6012 = new BitSet(new long[]{0x803C80000000A000L,0x0000000100000000L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange6014 = new BitSet(new long[]{0x0000000000000000L,0x0000004000000000L});
	public static final BitSet FOLLOW_TO_in_ftsFieldGroupRange6016 = new BitSet(new long[]{0x803C80000000A000L,0x0000000100000000L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange6018 = new BitSet(new long[]{0x2000000000000000L,0x0000000010000000L});
	public static final BitSet FOLLOW_range_right_in_ftsFieldGroupRange6020 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LSQUARE_in_range_left6079 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_range_left6111 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RSQUARE_in_range_right6164 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_range_right6196 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_fieldReference6252 = new BitSet(new long[]{0x8000000000000020L,0x0000014000024000L});
	public static final BitSet FOLLOW_prefix_in_fieldReference6289 = new BitSet(new long[]{0x8000000000000020L,0x0000004000024000L});
	public static final BitSet FOLLOW_uri_in_fieldReference6309 = new BitSet(new long[]{0x8000000000000020L,0x0000004000024000L});
	public static final BitSet FOLLOW_identifier_in_fieldReference6330 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_tempReference6417 = new BitSet(new long[]{0x8000000000000020L,0x0000014000024000L});
	public static final BitSet FOLLOW_prefix_in_tempReference6446 = new BitSet(new long[]{0x8000000000000020L,0x0000004000024000L});
	public static final BitSet FOLLOW_uri_in_tempReference6466 = new BitSet(new long[]{0x8000000000000020L,0x0000004000024000L});
	public static final BitSet FOLLOW_identifier_in_tempReference6487 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifier_in_prefix6574 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_prefix6576 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_URI_in_uri6657 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_identifier6759 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_DOT_in_identifier6761 = new BitSet(new long[]{0x8000000000000000L});
	public static final BitSet FOLLOW_ID_in_identifier6765 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_identifier6814 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TO_in_identifier6881 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_in_identifier6919 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_in_identifier6957 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_identifier6996 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7055 = new BitSet(new long[]{0x0000000000200802L});
	public static final BitSet FOLLOW_set_in_ftsWord7058 = new BitSet(new long[]{0x803880000000A000L,0x0000004101004000L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7064 = new BitSet(new long[]{0x0000000000200802L});
	public static final BitSet FOLLOW_OR_in_or7443 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BAR_in_or7455 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_BAR_in_or7457 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_in_and7490 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AMP_in_and7502 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_AMP_in_and7504 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_in_synpred1_FTS1233 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_synpred2_FTS1746 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsRange_in_synpred3_FTS1823 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroup_in_synpred4_FTS1902 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTermOrPhrase_in_synpred5_FTS1951 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsExactTermOrPhrase_in_synpred6_FTS1980 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTokenisedTermOrPhrase_in_synpred7_FTS2010 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_synpred8_FTS2683 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_synpred8_FTS2685 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred9_FTS2724 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred10_FTS2799 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred11_FTS2869 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred12_FTS2927 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_synpred13_FTS3032 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_synpred13_FTS3034 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred14_FTS3073 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred15_FTS3148 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred16_FTS3218 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred17_FTS3276 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_synpred18_FTS3383 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_synpred18_FTS3385 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred19_FTS3424 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred20_FTS3499 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred21_FTS3569 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred22_FTS3627 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_in_synpred23_FTS4374 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_synpred24_FTS4739 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred25_FTS4809 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred26_FTS4884 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred27_FTS4959 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred28_FTS5034 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred29_FTS5109 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred30_FTS5184 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_proximityGroup_in_synpred31_FTS5678 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_prefix_in_synpred32_FTS6284 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_synpred33_FTS6739 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_DOT_in_synpred33_FTS6741 = new BitSet(new long[]{0x8000000000000000L});
	public static final BitSet FOLLOW_ID_in_synpred33_FTS6743 = new BitSet(new long[]{0x0000000000000002L});
}
