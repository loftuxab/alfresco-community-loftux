// $ANTLR 3.5.2 W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g 2015-06-06 12:06:50

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
		"CARAT", "COLON", "COMMA", "CONJUNCTION", "DATETIME", "DECIMAL_INTEGER_LITERAL", 
		"DECIMAL_NUMERAL", "DEFAULT", "DIGIT", "DISJUNCTION", "DOLLAR", "DOT", 
		"DOTDOT", "E", "EQUALS", "EXACT_PHRASE", "EXACT_TERM", "EXCLAMATION", 
		"EXCLUDE", "EXCLUSIVE", "EXPONENT", "FG_EXACT_PHRASE", "FG_EXACT_TERM", 
		"FG_PHRASE", "FG_PROXIMITY", "FG_RANGE", "FG_SYNONYM", "FG_TERM", "FIELD_CONJUNCTION", 
		"FIELD_DEFAULT", "FIELD_DISJUNCTION", "FIELD_EXCLUDE", "FIELD_GROUP", 
		"FIELD_MANDATORY", "FIELD_NEGATION", "FIELD_OPTIONAL", "FIELD_REF", "FLOATING_POINT_LITERAL", 
		"FTS", "FTSPHRASE", "FTSPRE", "FTSWILD", "FTSWORD", "FUZZY", "F_ESC", 
		"F_HEX", "F_URI_ALPHA", "F_URI_DIGIT", "F_URI_ESC", "F_URI_OTHER", "GT", 
		"ID", "INCLUSIVE", "IN_WORD", "LCURL", "LPAREN", "LSQUARE", "LT", "MANDATORY", 
		"MINUS", "NAME_SPACE", "NEGATION", "NON_ZERO_DIGIT", "NOT", "OPTIONAL", 
		"OR", "PERCENT", "PHRASE", "PLUS", "PREFIX", "PROXIMITY", "QUALIFIER", 
		"QUESTION_MARK", "RANGE", "RCURL", "RPAREN", "RSQUARE", "SIGNED_INTEGER", 
		"STAR", "START_WORD", "SYNONYM", "TEMPLATE", "TERM", "TILDA", "TO", "URI", 
		"WS", "ZERO_DIGIT"
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
	public static final int DECIMAL_INTEGER_LITERAL=14;
	public static final int DECIMAL_NUMERAL=15;
	public static final int DEFAULT=16;
	public static final int DIGIT=17;
	public static final int DISJUNCTION=18;
	public static final int DOLLAR=19;
	public static final int DOT=20;
	public static final int DOTDOT=21;
	public static final int E=22;
	public static final int EQUALS=23;
	public static final int EXACT_PHRASE=24;
	public static final int EXACT_TERM=25;
	public static final int EXCLAMATION=26;
	public static final int EXCLUDE=27;
	public static final int EXCLUSIVE=28;
	public static final int EXPONENT=29;
	public static final int FG_EXACT_PHRASE=30;
	public static final int FG_EXACT_TERM=31;
	public static final int FG_PHRASE=32;
	public static final int FG_PROXIMITY=33;
	public static final int FG_RANGE=34;
	public static final int FG_SYNONYM=35;
	public static final int FG_TERM=36;
	public static final int FIELD_CONJUNCTION=37;
	public static final int FIELD_DEFAULT=38;
	public static final int FIELD_DISJUNCTION=39;
	public static final int FIELD_EXCLUDE=40;
	public static final int FIELD_GROUP=41;
	public static final int FIELD_MANDATORY=42;
	public static final int FIELD_NEGATION=43;
	public static final int FIELD_OPTIONAL=44;
	public static final int FIELD_REF=45;
	public static final int FLOATING_POINT_LITERAL=46;
	public static final int FTS=47;
	public static final int FTSPHRASE=48;
	public static final int FTSPRE=49;
	public static final int FTSWILD=50;
	public static final int FTSWORD=51;
	public static final int FUZZY=52;
	public static final int F_ESC=53;
	public static final int F_HEX=54;
	public static final int F_URI_ALPHA=55;
	public static final int F_URI_DIGIT=56;
	public static final int F_URI_ESC=57;
	public static final int F_URI_OTHER=58;
	public static final int GT=59;
	public static final int ID=60;
	public static final int INCLUSIVE=61;
	public static final int IN_WORD=62;
	public static final int LCURL=63;
	public static final int LPAREN=64;
	public static final int LSQUARE=65;
	public static final int LT=66;
	public static final int MANDATORY=67;
	public static final int MINUS=68;
	public static final int NAME_SPACE=69;
	public static final int NEGATION=70;
	public static final int NON_ZERO_DIGIT=71;
	public static final int NOT=72;
	public static final int OPTIONAL=73;
	public static final int OR=74;
	public static final int PERCENT=75;
	public static final int PHRASE=76;
	public static final int PLUS=77;
	public static final int PREFIX=78;
	public static final int PROXIMITY=79;
	public static final int QUALIFIER=80;
	public static final int QUESTION_MARK=81;
	public static final int RANGE=82;
	public static final int RCURL=83;
	public static final int RPAREN=84;
	public static final int RSQUARE=85;
	public static final int SIGNED_INTEGER=86;
	public static final int STAR=87;
	public static final int START_WORD=88;
	public static final int SYNONYM=89;
	public static final int TEMPLATE=90;
	public static final int TERM=91;
	public static final int TILDA=92;
	public static final int TO=93;
	public static final int URI=94;
	public static final int WS=95;
	public static final int ZERO_DIGIT=96;

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
			case COMMA:
			case DOT:
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
			case NOT:
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
			case FTSPHRASE:
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
			case MINUS:
				{
				int LA1_4 = input.LA(2);
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
							new NoViableAltException("", 1, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case AND:
				{
				int LA1_5 = input.LA(2);
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
							new NoViableAltException("", 1, 5, input);
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
				int LA1_7 = input.LA(2);
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
							new NoViableAltException("", 1, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EXCLAMATION:
				{
				int LA1_8 = input.LA(2);
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
							new NoViableAltException("", 1, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case QUESTION_MARK:
			case STAR:
				{
				int LA1_9 = input.LA(2);
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
							new NoViableAltException("", 1, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case AT:
				{
				int LA1_10 = input.LA(2);
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
							new NoViableAltException("", 1, 10, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TO:
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
			case DATETIME:
			case DECIMAL_INTEGER_LITERAL:
			case FLOATING_POINT_LITERAL:
			case FTSPRE:
			case FTSWILD:
			case FTSWORD:
				{
				int LA1_12 = input.LA(2);
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
							new NoViableAltException("", 1, 12, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case OR:
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
			case URI:
				{
				int LA1_14 = input.LA(2);
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
							new NoViableAltException("", 1, 14, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EQUALS:
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
			case TILDA:
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
			case LSQUARE:
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
			case LT:
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
				if ( ((LA5_0 >= AND && LA5_0 <= BAR)||LA5_0==COMMA||(LA5_0 >= DATETIME && LA5_0 <= DECIMAL_INTEGER_LITERAL)||LA5_0==DOT||LA5_0==EQUALS||LA5_0==EXCLAMATION||LA5_0==FLOATING_POINT_LITERAL||(LA5_0 >= FTSPHRASE && LA5_0 <= FTSWORD)||LA5_0==ID||(LA5_0 >= LPAREN && LA5_0 <= LT)||LA5_0==MINUS||LA5_0==NOT||(LA5_0 >= OR && LA5_0 <= PERCENT)||LA5_0==PLUS||LA5_0==QUESTION_MARK||LA5_0==STAR||(LA5_0 >= TILDA && LA5_0 <= URI)) ) {
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
						if ( ((LA4_1 >= AND && LA4_1 <= BAR)||LA4_1==COMMA||(LA4_1 >= DATETIME && LA4_1 <= DECIMAL_INTEGER_LITERAL)||LA4_1==DOT||LA4_1==EQUALS||LA4_1==EXCLAMATION||LA4_1==FLOATING_POINT_LITERAL||(LA4_1 >= FTSPHRASE && LA4_1 <= FTSWORD)||LA4_1==ID||(LA4_1 >= LPAREN && LA4_1 <= LT)||LA4_1==MINUS||LA4_1==NOT||(LA4_1 >= OR && LA4_1 <= PERCENT)||LA4_1==PLUS||LA4_1==QUESTION_MARK||LA4_1==STAR||(LA4_1 >= TILDA && LA4_1 <= URI)) ) {
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
					if ( ((LA6_2 >= AND && LA6_2 <= BAR)||LA6_2==COMMA||(LA6_2 >= DATETIME && LA6_2 <= DECIMAL_INTEGER_LITERAL)||LA6_2==DOT||LA6_2==EQUALS||LA6_2==EXCLAMATION||LA6_2==FLOATING_POINT_LITERAL||(LA6_2 >= FTSPHRASE && LA6_2 <= FTSWORD)||LA6_2==ID||(LA6_2 >= LPAREN && LA6_2 <= LT)||LA6_2==MINUS||LA6_2==NOT||(LA6_2 >= OR && LA6_2 <= PERCENT)||LA6_2==PLUS||LA6_2==QUESTION_MARK||LA6_2==STAR||(LA6_2 >= TILDA && LA6_2 <= URI)) ) {
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
					if ( ((LA8_2 >= AND && LA8_2 <= AT)||LA8_2==COMMA||(LA8_2 >= DATETIME && LA8_2 <= DECIMAL_INTEGER_LITERAL)||LA8_2==DOT||LA8_2==EQUALS||LA8_2==FLOATING_POINT_LITERAL||(LA8_2 >= FTSPHRASE && LA8_2 <= FTSWORD)||LA8_2==ID||(LA8_2 >= LPAREN && LA8_2 <= LT)||LA8_2==NOT||(LA8_2 >= OR && LA8_2 <= PERCENT)||LA8_2==QUESTION_MARK||LA8_2==STAR||(LA8_2 >= TILDA && LA8_2 <= URI)) ) {
						alt8=1;
					}

					}
					break;
				case AMP:
				case AND:
				case AT:
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
						if ( ((LA7_1 >= AND && LA7_1 <= BAR)||LA7_1==COMMA||(LA7_1 >= DATETIME && LA7_1 <= DECIMAL_INTEGER_LITERAL)||LA7_1==DOT||LA7_1==EQUALS||LA7_1==EXCLAMATION||LA7_1==FLOATING_POINT_LITERAL||(LA7_1 >= FTSPHRASE && LA7_1 <= FTSWORD)||LA7_1==ID||(LA7_1 >= LPAREN && LA7_1 <= LT)||LA7_1==MINUS||LA7_1==NOT||(LA7_1 >= OR && LA7_1 <= PERCENT)||LA7_1==PLUS||LA7_1==QUESTION_MARK||LA7_1==STAR||(LA7_1 >= TILDA && LA7_1 <= URI)) ) {
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
				if ( (LA9_0==COMMA||(LA9_0 >= DATETIME && LA9_0 <= DECIMAL_INTEGER_LITERAL)||LA9_0==DOT||LA9_0==FLOATING_POINT_LITERAL||(LA9_0 >= FTSPHRASE && LA9_0 <= FTSWORD)||LA9_0==ID||LA9_0==MINUS||LA9_0==NOT||LA9_0==QUESTION_MARK||LA9_0==STAR||LA9_0==TO) ) {
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
			else if ( ((LA15_0 >= AND && LA15_0 <= AT)||LA15_0==COMMA||(LA15_0 >= DATETIME && LA15_0 <= DECIMAL_INTEGER_LITERAL)||LA15_0==DOT||LA15_0==EQUALS||LA15_0==FLOATING_POINT_LITERAL||(LA15_0 >= FTSPHRASE && LA15_0 <= FTSWORD)||LA15_0==ID||(LA15_0 >= LPAREN && LA15_0 <= LT)||(LA15_0 >= OR && LA15_0 <= PERCENT)||LA15_0==QUESTION_MARK||LA15_0==STAR||(LA15_0 >= TILDA && LA15_0 <= URI)) ) {
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
			if ( (LA16_0==COMMA||(LA16_0 >= DATETIME && LA16_0 <= DECIMAL_INTEGER_LITERAL)||LA16_0==DOT||LA16_0==FLOATING_POINT_LITERAL||(LA16_0 >= FTSPHRASE && LA16_0 <= FTSWORD)||LA16_0==ID||LA16_0==NOT||LA16_0==QUESTION_MARK||LA16_0==STAR||LA16_0==TO) ) {
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:445:1: ftsTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ftsTermOrPhrase | ftsExactTermOrPhrase | ftsTokenisedTermOrPhrase | ftsRange -> ^( RANGE ftsRange ) | ftsFieldGroup -> ftsFieldGroup | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template );
	public final FTSParser.ftsTest_return ftsTest() throws RecognitionException {
		FTSParser.ftsTest_return retval = new FTSParser.ftsTest_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token LPAREN43=null;
		Token RPAREN45=null;
		ParserRuleReturnScope ftsFieldGroupProximity37 =null;
		ParserRuleReturnScope ftsTermOrPhrase38 =null;
		ParserRuleReturnScope ftsExactTermOrPhrase39 =null;
		ParserRuleReturnScope ftsTokenisedTermOrPhrase40 =null;
		ParserRuleReturnScope ftsRange41 =null;
		ParserRuleReturnScope ftsFieldGroup42 =null;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:446:9: ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( PROXIMITY ftsFieldGroupProximity ) | ftsTermOrPhrase | ftsExactTermOrPhrase | ftsTokenisedTermOrPhrase | ftsRange -> ^( RANGE ftsRange ) | ftsFieldGroup -> ftsFieldGroup | LPAREN ftsDisjunction RPAREN -> ftsDisjunction | template -> template )
			int alt17=8;
			switch ( input.LA(1) ) {
			case ID:
				{
				switch ( input.LA(2) ) {
				case DOT:
					{
					int LA17_16 = input.LA(3);
					if ( (LA17_16==EOF||(LA17_16 >= AMP && LA17_16 <= BAR)||LA17_16==CARAT||LA17_16==COMMA||(LA17_16 >= DATETIME && LA17_16 <= DECIMAL_INTEGER_LITERAL)||LA17_16==DOT||LA17_16==EQUALS||LA17_16==EXCLAMATION||LA17_16==FLOATING_POINT_LITERAL||(LA17_16 >= FTSPHRASE && LA17_16 <= FTSWORD)||LA17_16==ID||(LA17_16 >= LPAREN && LA17_16 <= LT)||LA17_16==MINUS||LA17_16==NOT||(LA17_16 >= OR && LA17_16 <= PERCENT)||LA17_16==PLUS||LA17_16==QUESTION_MARK||LA17_16==RPAREN||LA17_16==STAR||(LA17_16 >= TILDA && LA17_16 <= URI)) ) {
						alt17=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 16, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case STAR:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						int LA17_28 = input.LA(4);
						if ( (LA17_28==DECIMAL_INTEGER_LITERAL) ) {
							int LA17_42 = input.LA(5);
							if ( (LA17_42==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA17_49 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case EOF:
								case AMP:
								case AND:
								case AT:
								case BAR:
								case CARAT:
								case COMMA:
								case DOT:
								case EQUALS:
								case EXCLAMATION:
								case FTSPHRASE:
								case LPAREN:
								case LSQUARE:
								case LT:
								case MINUS:
								case OR:
								case PERCENT:
								case PLUS:
								case QUESTION_MARK:
								case RPAREN:
								case STAR:
								case TILDA:
								case URI:
									{
									alt17=2;
									}
									break;
								case ID:
									{
									int LA17_50 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case TO:
									{
									int LA17_51 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
									int LA17_52 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
											new NoViableAltException("", 17, 47, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA17_42 >= AMP && LA17_42 <= BAR)||LA17_42==CARAT||LA17_42==COMMA||(LA17_42 >= DATETIME && LA17_42 <= DECIMAL_INTEGER_LITERAL)||(LA17_42 >= DOT && LA17_42 <= DOTDOT)||LA17_42==EQUALS||LA17_42==EXCLAMATION||LA17_42==FLOATING_POINT_LITERAL||(LA17_42 >= FTSPHRASE && LA17_42 <= FTSWORD)||LA17_42==ID||(LA17_42 >= LPAREN && LA17_42 <= LT)||LA17_42==MINUS||LA17_42==NOT||(LA17_42 >= OR && LA17_42 <= PERCENT)||LA17_42==PLUS||LA17_42==QUESTION_MARK||LA17_42==STAR||(LA17_42 >= TILDA && LA17_42 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 42, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA17_28==RPAREN) && (synpred2_FTS())) {
							alt17=1;
						}
						else if ( ((LA17_28 >= AMP && LA17_28 <= BAR)||LA17_28==COMMA||LA17_28==DATETIME||LA17_28==DOT||LA17_28==EQUALS||LA17_28==EXCLAMATION||LA17_28==FLOATING_POINT_LITERAL||(LA17_28 >= FTSPHRASE && LA17_28 <= FTSWORD)||LA17_28==ID||(LA17_28 >= LPAREN && LA17_28 <= LT)||LA17_28==MINUS||LA17_28==NOT||(LA17_28 >= OR && LA17_28 <= PERCENT)||LA17_28==PLUS||LA17_28==QUESTION_MARK||LA17_28==STAR||(LA17_28 >= TILDA && LA17_28 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_29 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case EOF:
					case AMP:
					case AND:
					case AT:
					case BAR:
					case CARAT:
					case COMMA:
					case DOT:
					case EQUALS:
					case EXCLAMATION:
					case FTSPHRASE:
					case LSQUARE:
					case LT:
					case MINUS:
					case OR:
					case PERCENT:
					case PLUS:
					case QUESTION_MARK:
					case RPAREN:
					case STAR:
					case TILDA:
					case URI:
						{
						alt17=2;
						}
						break;
					case ID:
						{
						int LA17_30 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case TO:
						{
						int LA17_31 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
						int LA17_32 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
								new NoViableAltException("", 17, 17, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case COLON:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						alt17=6;
						}
						break;
					case ID:
						{
						switch ( input.LA(4) ) {
						case DOT:
							{
							int LA17_44 = input.LA(5);
							if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 44, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COLON:
							{
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case EOF:
						case AMP:
						case AND:
						case AT:
						case BAR:
						case CARAT:
						case COMMA:
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
						case OR:
						case PERCENT:
						case PLUS:
						case QUESTION_MARK:
						case RPAREN:
						case STAR:
						case TILDA:
						case TO:
						case URI:
							{
							alt17=2;
							}
							break;
						case DOTDOT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
						}
						break;
					case TO:
						{
						int LA17_35 = input.LA(4);
						if ( (LA17_35==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 35, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case OR:
						{
						int LA17_24 = input.LA(4);
						if ( (LA17_24==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case AND:
						{
						int LA17_25 = input.LA(4);
						if ( (LA17_25==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_36 = input.LA(4);
						if ( (LA17_36==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 36, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case FTSPHRASE:
						{
						int LA17_37 = input.LA(4);
						if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_37==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 37, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case COMMA:
					case DOT:
					case QUESTION_MARK:
					case STAR:
						{
						alt17=2;
						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
						{
						int LA17_38 = input.LA(4);
						if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_38==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 38, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case LSQUARE:
					case LT:
						{
						alt17=5;
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
								new NoViableAltException("", 17, 18, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case EOF:
				case AMP:
				case AND:
				case AT:
				case BAR:
				case CARAT:
				case COMMA:
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
				case OR:
				case PERCENT:
				case PLUS:
				case QUESTION_MARK:
				case RPAREN:
				case TILDA:
				case TO:
				case URI:
					{
					alt17=2;
					}
					break;
				case DOTDOT:
					{
					alt17=5;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case AT:
				{
				switch ( input.LA(2) ) {
				case ID:
					{
					int LA17_19 = input.LA(3);
					if ( (LA17_19==DOT) ) {
						int LA17_39 = input.LA(4);
						if ( (LA17_39==ID) ) {
							int LA17_45 = input.LA(5);
							if ( (LA17_45==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case ID:
									{
									switch ( input.LA(7) ) {
									case DOT:
										{
										int LA17_44 = input.LA(8);
										if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
											alt17=2;
										}

										else {
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 44, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}

										}
										break;
									case COLON:
										{
										switch ( input.LA(8) ) {
										case LPAREN:
											{
											alt17=6;
											}
											break;
										case FTSPHRASE:
											{
											int LA17_37 = input.LA(9);
											if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_37==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 37, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case COMMA:
										case DOT:
										case NOT:
										case QUESTION_MARK:
										case STAR:
										case TO:
											{
											alt17=2;
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
											int LA17_38 = input.LA(9);
											if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_38==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 38, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case LSQUARE:
										case LT:
											{
											alt17=5;
											}
											break;
										default:
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 41, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}
										}
										break;
									case EOF:
									case AMP:
									case AND:
									case AT:
									case BAR:
									case CARAT:
									case COMMA:
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
									case OR:
									case PERCENT:
									case PLUS:
									case QUESTION_MARK:
									case RPAREN:
									case STAR:
									case TILDA:
									case TO:
									case URI:
										{
										alt17=2;
										}
										break;
									case DOTDOT:
										{
										alt17=5;
										}
										break;
									default:
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 34, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}
									}
									break;
								case TO:
									{
									int LA17_35 = input.LA(7);
									if ( (LA17_35==COLON) ) {
										switch ( input.LA(8) ) {
										case LPAREN:
											{
											alt17=6;
											}
											break;
										case FTSPHRASE:
											{
											int LA17_37 = input.LA(9);
											if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_37==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 37, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case COMMA:
										case DOT:
										case NOT:
										case QUESTION_MARK:
										case STAR:
										case TO:
											{
											alt17=2;
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
											int LA17_38 = input.LA(9);
											if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_38==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 38, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case LSQUARE:
										case LT:
											{
											alt17=5;
											}
											break;
										default:
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 41, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}
									}
									else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
										alt17=2;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 35, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case OR:
									{
									int LA17_24 = input.LA(7);
									if ( (LA17_24==COLON) ) {
										switch ( input.LA(8) ) {
										case LPAREN:
											{
											alt17=6;
											}
											break;
										case FTSPHRASE:
											{
											int LA17_37 = input.LA(9);
											if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_37==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 37, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case COMMA:
										case DOT:
										case NOT:
										case QUESTION_MARK:
										case STAR:
										case TO:
											{
											alt17=2;
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
											int LA17_38 = input.LA(9);
											if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_38==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 38, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case LSQUARE:
										case LT:
											{
											alt17=5;
											}
											break;
										default:
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 41, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 24, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case AND:
									{
									int LA17_25 = input.LA(7);
									if ( (LA17_25==COLON) ) {
										switch ( input.LA(8) ) {
										case LPAREN:
											{
											alt17=6;
											}
											break;
										case FTSPHRASE:
											{
											int LA17_37 = input.LA(9);
											if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_37==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 37, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case COMMA:
										case DOT:
										case NOT:
										case QUESTION_MARK:
										case STAR:
										case TO:
											{
											alt17=2;
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
											int LA17_38 = input.LA(9);
											if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_38==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 38, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case LSQUARE:
										case LT:
											{
											alt17=5;
											}
											break;
										default:
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 41, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 25, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case NOT:
									{
									int LA17_36 = input.LA(7);
									if ( (LA17_36==COLON) ) {
										switch ( input.LA(8) ) {
										case LPAREN:
											{
											alt17=6;
											}
											break;
										case FTSPHRASE:
											{
											int LA17_37 = input.LA(9);
											if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_37==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 37, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case COMMA:
										case DOT:
										case NOT:
										case QUESTION_MARK:
										case STAR:
										case TO:
											{
											alt17=2;
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
											int LA17_38 = input.LA(9);
											if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
												alt17=2;
											}
											else if ( (LA17_38==DOTDOT) ) {
												alt17=5;
											}

											else {
												if (state.backtracking>0) {state.failed=true; return retval;}
												int nvaeMark = input.mark();
												try {
													for (int nvaeConsume = 0; nvaeConsume < 9 - 1; nvaeConsume++) {
														input.consume();
													}
													NoViableAltException nvae =
														new NoViableAltException("", 17, 38, input);
													throw nvae;
												} finally {
													input.rewind(nvaeMark);
												}
											}

											}
											break;
										case LSQUARE:
										case LT:
											{
											alt17=5;
											}
											break;
										default:
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 41, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}
									}
									else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
										alt17=2;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 36, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case QUESTION_MARK:
								case STAR:
									{
									alt17=2;
									}
									break;
								case DATETIME:
								case DECIMAL_INTEGER_LITERAL:
								case FLOATING_POINT_LITERAL:
								case FTSPRE:
								case FTSWILD:
								case FTSWORD:
									{
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 18, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 45, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 39, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( (LA17_19==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case ID:
							{
							switch ( input.LA(5) ) {
							case DOT:
								{
								int LA17_44 = input.LA(6);
								if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
									alt17=2;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 44, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COLON:
								{
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
								}
								break;
							case EOF:
							case AMP:
							case AND:
							case AT:
							case BAR:
							case CARAT:
							case COMMA:
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
							case OR:
							case PERCENT:
							case PLUS:
							case QUESTION_MARK:
							case RPAREN:
							case STAR:
							case TILDA:
							case TO:
							case URI:
								{
								alt17=2;
								}
								break;
							case DOTDOT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 34, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case TO:
							{
							int LA17_35 = input.LA(5);
							if ( (LA17_35==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 35, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case OR:
							{
							int LA17_24 = input.LA(5);
							if ( (LA17_24==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 24, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case AND:
							{
							int LA17_25 = input.LA(5);
							if ( (LA17_25==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 25, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case NOT:
							{
							int LA17_36 = input.LA(5);
							if ( (LA17_36==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 36, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case QUESTION_MARK:
						case STAR:
							{
							alt17=2;
							}
							break;
						case DATETIME:
						case DECIMAL_INTEGER_LITERAL:
						case FLOATING_POINT_LITERAL:
						case FTSPRE:
						case FTSWILD:
						case FTSWORD:
							{
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 19, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case TO:
					{
					int LA17_20 = input.LA(3);
					if ( (LA17_20==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case ID:
							{
							switch ( input.LA(5) ) {
							case DOT:
								{
								int LA17_44 = input.LA(6);
								if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
									alt17=2;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 44, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COLON:
								{
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
								}
								break;
							case EOF:
							case AMP:
							case AND:
							case AT:
							case BAR:
							case CARAT:
							case COMMA:
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
							case OR:
							case PERCENT:
							case PLUS:
							case QUESTION_MARK:
							case RPAREN:
							case STAR:
							case TILDA:
							case TO:
							case URI:
								{
								alt17=2;
								}
								break;
							case DOTDOT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 34, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case TO:
							{
							int LA17_35 = input.LA(5);
							if ( (LA17_35==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 35, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case OR:
							{
							int LA17_24 = input.LA(5);
							if ( (LA17_24==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 24, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case AND:
							{
							int LA17_25 = input.LA(5);
							if ( (LA17_25==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 25, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case NOT:
							{
							int LA17_36 = input.LA(5);
							if ( (LA17_36==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 36, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case QUESTION_MARK:
						case STAR:
							{
							alt17=2;
							}
							break;
						case DATETIME:
						case DECIMAL_INTEGER_LITERAL:
						case FLOATING_POINT_LITERAL:
						case FTSPRE:
						case FTSWILD:
						case FTSWORD:
							{
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 20, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case OR:
					{
					int LA17_5 = input.LA(3);
					if ( (LA17_5==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case ID:
							{
							switch ( input.LA(5) ) {
							case DOT:
								{
								int LA17_44 = input.LA(6);
								if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
									alt17=2;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 44, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COLON:
								{
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
								}
								break;
							case EOF:
							case AMP:
							case AND:
							case AT:
							case BAR:
							case CARAT:
							case COMMA:
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
							case OR:
							case PERCENT:
							case PLUS:
							case QUESTION_MARK:
							case RPAREN:
							case STAR:
							case TILDA:
							case TO:
							case URI:
								{
								alt17=2;
								}
								break;
							case DOTDOT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 34, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case TO:
							{
							int LA17_35 = input.LA(5);
							if ( (LA17_35==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 35, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case OR:
							{
							int LA17_24 = input.LA(5);
							if ( (LA17_24==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 24, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case AND:
							{
							int LA17_25 = input.LA(5);
							if ( (LA17_25==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 25, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case NOT:
							{
							int LA17_36 = input.LA(5);
							if ( (LA17_36==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 36, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case QUESTION_MARK:
						case STAR:
							{
							alt17=2;
							}
							break;
						case DATETIME:
						case DECIMAL_INTEGER_LITERAL:
						case FLOATING_POINT_LITERAL:
						case FTSPRE:
						case FTSWILD:
						case FTSWORD:
							{
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 5, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case AND:
					{
					int LA17_6 = input.LA(3);
					if ( (LA17_6==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case ID:
							{
							switch ( input.LA(5) ) {
							case DOT:
								{
								int LA17_44 = input.LA(6);
								if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
									alt17=2;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 44, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COLON:
								{
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
								}
								break;
							case EOF:
							case AMP:
							case AND:
							case AT:
							case BAR:
							case CARAT:
							case COMMA:
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
							case OR:
							case PERCENT:
							case PLUS:
							case QUESTION_MARK:
							case RPAREN:
							case STAR:
							case TILDA:
							case TO:
							case URI:
								{
								alt17=2;
								}
								break;
							case DOTDOT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 34, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case TO:
							{
							int LA17_35 = input.LA(5);
							if ( (LA17_35==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 35, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case OR:
							{
							int LA17_24 = input.LA(5);
							if ( (LA17_24==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 24, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case AND:
							{
							int LA17_25 = input.LA(5);
							if ( (LA17_25==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 25, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case NOT:
							{
							int LA17_36 = input.LA(5);
							if ( (LA17_36==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 36, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case QUESTION_MARK:
						case STAR:
							{
							alt17=2;
							}
							break;
						case DATETIME:
						case DECIMAL_INTEGER_LITERAL:
						case FLOATING_POINT_LITERAL:
						case FTSPRE:
						case FTSWILD:
						case FTSWORD:
							{
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 6, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case NOT:
					{
					int LA17_21 = input.LA(3);
					if ( (LA17_21==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case ID:
							{
							switch ( input.LA(5) ) {
							case DOT:
								{
								int LA17_44 = input.LA(6);
								if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
									alt17=2;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 44, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COLON:
								{
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
								}
								break;
							case EOF:
							case AMP:
							case AND:
							case AT:
							case BAR:
							case CARAT:
							case COMMA:
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
							case OR:
							case PERCENT:
							case PLUS:
							case QUESTION_MARK:
							case RPAREN:
							case STAR:
							case TILDA:
							case TO:
							case URI:
								{
								alt17=2;
								}
								break;
							case DOTDOT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 34, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case TO:
							{
							int LA17_35 = input.LA(5);
							if ( (LA17_35==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 35, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case OR:
							{
							int LA17_24 = input.LA(5);
							if ( (LA17_24==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 24, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case AND:
							{
							int LA17_25 = input.LA(5);
							if ( (LA17_25==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 25, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case NOT:
							{
							int LA17_36 = input.LA(5);
							if ( (LA17_36==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 36, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case QUESTION_MARK:
						case STAR:
							{
							alt17=2;
							}
							break;
						case DATETIME:
						case DECIMAL_INTEGER_LITERAL:
						case FLOATING_POINT_LITERAL:
						case FTSPRE:
						case FTSWILD:
						case FTSWORD:
							{
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 18, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 21, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case URI:
					{
					switch ( input.LA(3) ) {
					case ID:
						{
						int LA17_22 = input.LA(4);
						if ( (LA17_22==DOT) ) {
							int LA17_40 = input.LA(5);
							if ( (LA17_40==ID) ) {
								int LA17_46 = input.LA(6);
								if ( (LA17_46==COLON) ) {
									switch ( input.LA(7) ) {
									case LPAREN:
										{
										alt17=6;
										}
										break;
									case FTSPHRASE:
										{
										int LA17_37 = input.LA(8);
										if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
											alt17=2;
										}
										else if ( (LA17_37==DOTDOT) ) {
											alt17=5;
										}

										else {
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 37, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}

										}
										break;
									case COMMA:
									case DOT:
									case NOT:
									case QUESTION_MARK:
									case STAR:
									case TO:
										{
										alt17=2;
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
										int LA17_38 = input.LA(8);
										if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
											alt17=2;
										}
										else if ( (LA17_38==DOTDOT) ) {
											alt17=5;
										}

										else {
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 17, 38, input);
												throw nvae;
											} finally {
												input.rewind(nvaeMark);
											}
										}

										}
										break;
									case LSQUARE:
									case LT:
										{
										alt17=5;
										}
										break;
									default:
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 41, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 46, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 40, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA17_22==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 22, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case TO:
						{
						int LA17_23 = input.LA(4);
						if ( (LA17_23==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 23, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case OR:
						{
						int LA17_24 = input.LA(4);
						if ( (LA17_24==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case AND:
						{
						int LA17_25 = input.LA(4);
						if ( (LA17_25==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_26 = input.LA(4);
						if ( (LA17_26==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 26, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
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
								new NoViableAltException("", 17, 8, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case TO:
				{
				switch ( input.LA(2) ) {
				case STAR:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						int LA17_28 = input.LA(4);
						if ( (LA17_28==DECIMAL_INTEGER_LITERAL) ) {
							int LA17_42 = input.LA(5);
							if ( (LA17_42==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA17_49 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case EOF:
								case AMP:
								case AND:
								case AT:
								case BAR:
								case CARAT:
								case COMMA:
								case DOT:
								case EQUALS:
								case EXCLAMATION:
								case FTSPHRASE:
								case LPAREN:
								case LSQUARE:
								case LT:
								case MINUS:
								case OR:
								case PERCENT:
								case PLUS:
								case QUESTION_MARK:
								case RPAREN:
								case STAR:
								case TILDA:
								case URI:
									{
									alt17=2;
									}
									break;
								case ID:
									{
									int LA17_50 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case TO:
									{
									int LA17_51 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
									int LA17_52 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
											new NoViableAltException("", 17, 47, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA17_42 >= AMP && LA17_42 <= BAR)||LA17_42==CARAT||LA17_42==COMMA||(LA17_42 >= DATETIME && LA17_42 <= DECIMAL_INTEGER_LITERAL)||(LA17_42 >= DOT && LA17_42 <= DOTDOT)||LA17_42==EQUALS||LA17_42==EXCLAMATION||LA17_42==FLOATING_POINT_LITERAL||(LA17_42 >= FTSPHRASE && LA17_42 <= FTSWORD)||LA17_42==ID||(LA17_42 >= LPAREN && LA17_42 <= LT)||LA17_42==MINUS||LA17_42==NOT||(LA17_42 >= OR && LA17_42 <= PERCENT)||LA17_42==PLUS||LA17_42==QUESTION_MARK||LA17_42==STAR||(LA17_42 >= TILDA && LA17_42 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 42, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA17_28==RPAREN) && (synpred2_FTS())) {
							alt17=1;
						}
						else if ( ((LA17_28 >= AMP && LA17_28 <= BAR)||LA17_28==COMMA||LA17_28==DATETIME||LA17_28==DOT||LA17_28==EQUALS||LA17_28==EXCLAMATION||LA17_28==FLOATING_POINT_LITERAL||(LA17_28 >= FTSPHRASE && LA17_28 <= FTSWORD)||LA17_28==ID||(LA17_28 >= LPAREN && LA17_28 <= LT)||LA17_28==MINUS||LA17_28==NOT||(LA17_28 >= OR && LA17_28 <= PERCENT)||LA17_28==PLUS||LA17_28==QUESTION_MARK||LA17_28==STAR||(LA17_28 >= TILDA && LA17_28 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_29 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case EOF:
					case AMP:
					case AND:
					case AT:
					case BAR:
					case CARAT:
					case COMMA:
					case DOT:
					case EQUALS:
					case EXCLAMATION:
					case FTSPHRASE:
					case LSQUARE:
					case LT:
					case MINUS:
					case OR:
					case PERCENT:
					case PLUS:
					case QUESTION_MARK:
					case RPAREN:
					case STAR:
					case TILDA:
					case URI:
						{
						alt17=2;
						}
						break;
					case ID:
						{
						int LA17_30 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case TO:
						{
						int LA17_31 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
						int LA17_32 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
								new NoViableAltException("", 17, 17, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case COLON:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						alt17=6;
						}
						break;
					case ID:
						{
						switch ( input.LA(4) ) {
						case DOT:
							{
							int LA17_44 = input.LA(5);
							if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 44, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COLON:
							{
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case EOF:
						case AMP:
						case AND:
						case AT:
						case BAR:
						case CARAT:
						case COMMA:
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
						case OR:
						case PERCENT:
						case PLUS:
						case QUESTION_MARK:
						case RPAREN:
						case STAR:
						case TILDA:
						case TO:
						case URI:
							{
							alt17=2;
							}
							break;
						case DOTDOT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
						}
						break;
					case TO:
						{
						int LA17_35 = input.LA(4);
						if ( (LA17_35==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 35, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case OR:
						{
						int LA17_24 = input.LA(4);
						if ( (LA17_24==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case AND:
						{
						int LA17_25 = input.LA(4);
						if ( (LA17_25==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_36 = input.LA(4);
						if ( (LA17_36==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 36, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case FTSPHRASE:
						{
						int LA17_37 = input.LA(4);
						if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_37==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 37, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case COMMA:
					case DOT:
					case QUESTION_MARK:
					case STAR:
						{
						alt17=2;
						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
						{
						int LA17_38 = input.LA(4);
						if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_38==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 38, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case LSQUARE:
					case LT:
						{
						alt17=5;
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
								new NoViableAltException("", 17, 18, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case EOF:
				case AMP:
				case AND:
				case AT:
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
				case PERCENT:
				case PLUS:
				case QUESTION_MARK:
				case RPAREN:
				case TILDA:
				case TO:
				case URI:
					{
					alt17=2;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case NOT:
				{
				switch ( input.LA(2) ) {
				case STAR:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						int LA17_28 = input.LA(4);
						if ( (LA17_28==DECIMAL_INTEGER_LITERAL) ) {
							int LA17_42 = input.LA(5);
							if ( (LA17_42==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA17_49 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case EOF:
								case AMP:
								case AND:
								case AT:
								case BAR:
								case CARAT:
								case COMMA:
								case DOT:
								case EQUALS:
								case EXCLAMATION:
								case FTSPHRASE:
								case LPAREN:
								case LSQUARE:
								case LT:
								case MINUS:
								case OR:
								case PERCENT:
								case PLUS:
								case QUESTION_MARK:
								case RPAREN:
								case STAR:
								case TILDA:
								case URI:
									{
									alt17=2;
									}
									break;
								case ID:
									{
									int LA17_50 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case TO:
									{
									int LA17_51 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
									int LA17_52 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
											new NoViableAltException("", 17, 47, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA17_42 >= AMP && LA17_42 <= BAR)||LA17_42==CARAT||LA17_42==COMMA||(LA17_42 >= DATETIME && LA17_42 <= DECIMAL_INTEGER_LITERAL)||(LA17_42 >= DOT && LA17_42 <= DOTDOT)||LA17_42==EQUALS||LA17_42==EXCLAMATION||LA17_42==FLOATING_POINT_LITERAL||(LA17_42 >= FTSPHRASE && LA17_42 <= FTSWORD)||LA17_42==ID||(LA17_42 >= LPAREN && LA17_42 <= LT)||LA17_42==MINUS||LA17_42==NOT||(LA17_42 >= OR && LA17_42 <= PERCENT)||LA17_42==PLUS||LA17_42==QUESTION_MARK||LA17_42==STAR||(LA17_42 >= TILDA && LA17_42 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 42, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA17_28==RPAREN) && (synpred2_FTS())) {
							alt17=1;
						}
						else if ( ((LA17_28 >= AMP && LA17_28 <= BAR)||LA17_28==COMMA||LA17_28==DATETIME||LA17_28==DOT||LA17_28==EQUALS||LA17_28==EXCLAMATION||LA17_28==FLOATING_POINT_LITERAL||(LA17_28 >= FTSPHRASE && LA17_28 <= FTSWORD)||LA17_28==ID||(LA17_28 >= LPAREN && LA17_28 <= LT)||LA17_28==MINUS||LA17_28==NOT||(LA17_28 >= OR && LA17_28 <= PERCENT)||LA17_28==PLUS||LA17_28==QUESTION_MARK||LA17_28==STAR||(LA17_28 >= TILDA && LA17_28 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_29 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case EOF:
					case AMP:
					case AND:
					case AT:
					case BAR:
					case CARAT:
					case COMMA:
					case DOT:
					case EQUALS:
					case EXCLAMATION:
					case FTSPHRASE:
					case LSQUARE:
					case LT:
					case MINUS:
					case OR:
					case PERCENT:
					case PLUS:
					case QUESTION_MARK:
					case RPAREN:
					case STAR:
					case TILDA:
					case URI:
						{
						alt17=2;
						}
						break;
					case ID:
						{
						int LA17_30 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case TO:
						{
						int LA17_31 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
						int LA17_32 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
								new NoViableAltException("", 17, 17, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case COLON:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						alt17=6;
						}
						break;
					case ID:
						{
						switch ( input.LA(4) ) {
						case DOT:
							{
							int LA17_44 = input.LA(5);
							if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 44, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COLON:
							{
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case EOF:
						case AMP:
						case AND:
						case AT:
						case BAR:
						case CARAT:
						case COMMA:
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
						case OR:
						case PERCENT:
						case PLUS:
						case QUESTION_MARK:
						case RPAREN:
						case STAR:
						case TILDA:
						case TO:
						case URI:
							{
							alt17=2;
							}
							break;
						case DOTDOT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
						}
						break;
					case TO:
						{
						int LA17_35 = input.LA(4);
						if ( (LA17_35==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 35, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case OR:
						{
						int LA17_24 = input.LA(4);
						if ( (LA17_24==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case AND:
						{
						int LA17_25 = input.LA(4);
						if ( (LA17_25==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_36 = input.LA(4);
						if ( (LA17_36==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 36, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case FTSPHRASE:
						{
						int LA17_37 = input.LA(4);
						if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_37==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 37, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case COMMA:
					case DOT:
					case QUESTION_MARK:
					case STAR:
						{
						alt17=2;
						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
						{
						int LA17_38 = input.LA(4);
						if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_38==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 38, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case LSQUARE:
					case LT:
						{
						alt17=5;
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
								new NoViableAltException("", 17, 18, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case EOF:
				case AMP:
				case AND:
				case AT:
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
				case PERCENT:
				case PLUS:
				case QUESTION_MARK:
				case RPAREN:
				case TILDA:
				case TO:
				case URI:
					{
					alt17=2;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case OR:
				{
				int LA17_5 = input.LA(2);
				if ( (LA17_5==COLON) ) {
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						alt17=6;
						}
						break;
					case ID:
						{
						switch ( input.LA(4) ) {
						case DOT:
							{
							int LA17_44 = input.LA(5);
							if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 44, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COLON:
							{
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case EOF:
						case AMP:
						case AND:
						case AT:
						case BAR:
						case CARAT:
						case COMMA:
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
						case OR:
						case PERCENT:
						case PLUS:
						case QUESTION_MARK:
						case RPAREN:
						case STAR:
						case TILDA:
						case TO:
						case URI:
							{
							alt17=2;
							}
							break;
						case DOTDOT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
						}
						break;
					case TO:
						{
						int LA17_35 = input.LA(4);
						if ( (LA17_35==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 35, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case OR:
						{
						int LA17_24 = input.LA(4);
						if ( (LA17_24==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case AND:
						{
						int LA17_25 = input.LA(4);
						if ( (LA17_25==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_36 = input.LA(4);
						if ( (LA17_36==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 36, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case FTSPHRASE:
						{
						int LA17_37 = input.LA(4);
						if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_37==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 37, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case COMMA:
					case DOT:
					case QUESTION_MARK:
					case STAR:
						{
						alt17=2;
						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
						{
						int LA17_38 = input.LA(4);
						if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_38==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 38, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case LSQUARE:
					case LT:
						{
						alt17=5;
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
								new NoViableAltException("", 17, 18, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case AND:
				{
				int LA17_6 = input.LA(2);
				if ( (LA17_6==COLON) ) {
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						alt17=6;
						}
						break;
					case ID:
						{
						switch ( input.LA(4) ) {
						case DOT:
							{
							int LA17_44 = input.LA(5);
							if ( (LA17_44==EOF||(LA17_44 >= AMP && LA17_44 <= BAR)||LA17_44==CARAT||LA17_44==COMMA||(LA17_44 >= DATETIME && LA17_44 <= DECIMAL_INTEGER_LITERAL)||LA17_44==DOT||LA17_44==EQUALS||LA17_44==EXCLAMATION||LA17_44==FLOATING_POINT_LITERAL||(LA17_44 >= FTSPHRASE && LA17_44 <= FTSWORD)||LA17_44==ID||(LA17_44 >= LPAREN && LA17_44 <= LT)||LA17_44==MINUS||LA17_44==NOT||(LA17_44 >= OR && LA17_44 <= PERCENT)||LA17_44==PLUS||LA17_44==QUESTION_MARK||LA17_44==RPAREN||LA17_44==STAR||(LA17_44 >= TILDA && LA17_44 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 44, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COLON:
							{
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
							}
							break;
						case EOF:
						case AMP:
						case AND:
						case AT:
						case BAR:
						case CARAT:
						case COMMA:
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
						case OR:
						case PERCENT:
						case PLUS:
						case QUESTION_MARK:
						case RPAREN:
						case STAR:
						case TILDA:
						case TO:
						case URI:
							{
							alt17=2;
							}
							break;
						case DOTDOT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 34, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
						}
						break;
					case TO:
						{
						int LA17_35 = input.LA(4);
						if ( (LA17_35==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_35==EOF||(LA17_35 >= AMP && LA17_35 <= BAR)||LA17_35==CARAT||LA17_35==COMMA||(LA17_35 >= DATETIME && LA17_35 <= DECIMAL_INTEGER_LITERAL)||LA17_35==DOT||LA17_35==EQUALS||LA17_35==EXCLAMATION||LA17_35==FLOATING_POINT_LITERAL||(LA17_35 >= FTSPHRASE && LA17_35 <= FTSWORD)||LA17_35==ID||(LA17_35 >= LPAREN && LA17_35 <= LT)||LA17_35==MINUS||LA17_35==NOT||(LA17_35 >= OR && LA17_35 <= PERCENT)||LA17_35==PLUS||LA17_35==QUESTION_MARK||LA17_35==RPAREN||LA17_35==STAR||(LA17_35 >= TILDA && LA17_35 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 35, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case OR:
						{
						int LA17_24 = input.LA(4);
						if ( (LA17_24==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 24, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case AND:
						{
						int LA17_25 = input.LA(4);
						if ( (LA17_25==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 25, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_36 = input.LA(4);
						if ( (LA17_36==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
								{
								alt17=6;
								}
								break;
							case FTSPHRASE:
								{
								int LA17_37 = input.LA(6);
								if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_37==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 37, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case COMMA:
							case DOT:
							case NOT:
							case QUESTION_MARK:
							case STAR:
							case TO:
								{
								alt17=2;
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
								int LA17_38 = input.LA(6);
								if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
									alt17=2;
								}
								else if ( (LA17_38==DOTDOT) ) {
									alt17=5;
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 17, 38, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case LSQUARE:
							case LT:
								{
								alt17=5;
								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 41, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
						}
						else if ( (LA17_36==EOF||(LA17_36 >= AMP && LA17_36 <= BAR)||LA17_36==CARAT||LA17_36==COMMA||(LA17_36 >= DATETIME && LA17_36 <= DECIMAL_INTEGER_LITERAL)||LA17_36==DOT||LA17_36==EQUALS||LA17_36==EXCLAMATION||LA17_36==FLOATING_POINT_LITERAL||(LA17_36 >= FTSPHRASE && LA17_36 <= FTSWORD)||LA17_36==ID||(LA17_36 >= LPAREN && LA17_36 <= LT)||LA17_36==MINUS||LA17_36==NOT||(LA17_36 >= OR && LA17_36 <= PERCENT)||LA17_36==PLUS||LA17_36==QUESTION_MARK||LA17_36==RPAREN||LA17_36==STAR||(LA17_36 >= TILDA && LA17_36 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 36, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case FTSPHRASE:
						{
						int LA17_37 = input.LA(4);
						if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_37==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 37, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case COMMA:
					case DOT:
					case QUESTION_MARK:
					case STAR:
						{
						alt17=2;
						}
						break;
					case DATETIME:
					case DECIMAL_INTEGER_LITERAL:
					case FLOATING_POINT_LITERAL:
					case FTSPRE:
					case FTSWILD:
					case FTSWORD:
						{
						int LA17_38 = input.LA(4);
						if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
							alt17=2;
						}
						else if ( (LA17_38==DOTDOT) ) {
							alt17=5;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 38, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case LSQUARE:
					case LT:
						{
						alt17=5;
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
								new NoViableAltException("", 17, 18, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 6, input);
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
				switch ( input.LA(2) ) {
				case STAR:
					{
					switch ( input.LA(3) ) {
					case LPAREN:
						{
						int LA17_28 = input.LA(4);
						if ( (LA17_28==DECIMAL_INTEGER_LITERAL) ) {
							int LA17_42 = input.LA(5);
							if ( (LA17_42==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA17_49 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case EOF:
								case AMP:
								case AND:
								case AT:
								case BAR:
								case CARAT:
								case COMMA:
								case DOT:
								case EQUALS:
								case EXCLAMATION:
								case FTSPHRASE:
								case LPAREN:
								case LSQUARE:
								case LT:
								case MINUS:
								case OR:
								case PERCENT:
								case PLUS:
								case QUESTION_MARK:
								case RPAREN:
								case STAR:
								case TILDA:
								case URI:
									{
									alt17=2;
									}
									break;
								case ID:
									{
									int LA17_50 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
									}

									}
									break;
								case TO:
									{
									int LA17_51 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
									int LA17_52 = input.LA(7);
									if ( (synpred2_FTS()) ) {
										alt17=1;
									}
									else if ( (true) ) {
										alt17=2;
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
											new NoViableAltException("", 17, 47, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA17_42 >= AMP && LA17_42 <= BAR)||LA17_42==CARAT||LA17_42==COMMA||(LA17_42 >= DATETIME && LA17_42 <= DECIMAL_INTEGER_LITERAL)||(LA17_42 >= DOT && LA17_42 <= DOTDOT)||LA17_42==EQUALS||LA17_42==EXCLAMATION||LA17_42==FLOATING_POINT_LITERAL||(LA17_42 >= FTSPHRASE && LA17_42 <= FTSWORD)||LA17_42==ID||(LA17_42 >= LPAREN && LA17_42 <= LT)||LA17_42==MINUS||LA17_42==NOT||(LA17_42 >= OR && LA17_42 <= PERCENT)||LA17_42==PLUS||LA17_42==QUESTION_MARK||LA17_42==STAR||(LA17_42 >= TILDA && LA17_42 <= URI)) ) {
								alt17=2;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 42, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA17_28==RPAREN) && (synpred2_FTS())) {
							alt17=1;
						}
						else if ( ((LA17_28 >= AMP && LA17_28 <= BAR)||LA17_28==COMMA||LA17_28==DATETIME||LA17_28==DOT||LA17_28==EQUALS||LA17_28==EXCLAMATION||LA17_28==FLOATING_POINT_LITERAL||(LA17_28 >= FTSPHRASE && LA17_28 <= FTSWORD)||LA17_28==ID||(LA17_28 >= LPAREN && LA17_28 <= LT)||LA17_28==MINUS||LA17_28==NOT||(LA17_28 >= OR && LA17_28 <= PERCENT)||LA17_28==PLUS||LA17_28==QUESTION_MARK||LA17_28==STAR||(LA17_28 >= TILDA && LA17_28 <= URI)) ) {
							alt17=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 28, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA17_29 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case EOF:
					case AMP:
					case AND:
					case AT:
					case BAR:
					case CARAT:
					case COMMA:
					case DOT:
					case EQUALS:
					case EXCLAMATION:
					case FTSPHRASE:
					case LSQUARE:
					case LT:
					case MINUS:
					case OR:
					case PERCENT:
					case PLUS:
					case QUESTION_MARK:
					case RPAREN:
					case STAR:
					case TILDA:
					case URI:
						{
						alt17=2;
						}
						break;
					case ID:
						{
						int LA17_30 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
						}

						}
						break;
					case TO:
						{
						int LA17_31 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
						int LA17_32 = input.LA(4);
						if ( (synpred2_FTS()) ) {
							alt17=1;
						}
						else if ( (true) ) {
							alt17=2;
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
								new NoViableAltException("", 17, 17, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
					}
					break;
				case EOF:
				case AMP:
				case AND:
				case AT:
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
				case PERCENT:
				case PLUS:
				case QUESTION_MARK:
				case RPAREN:
				case TILDA:
				case TO:
				case URI:
					{
					alt17=2;
					}
					break;
				case DOTDOT:
					{
					alt17=5;
					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 7, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case URI:
				{
				switch ( input.LA(2) ) {
				case ID:
					{
					int LA17_22 = input.LA(3);
					if ( (LA17_22==DOT) ) {
						int LA17_40 = input.LA(4);
						if ( (LA17_40==ID) ) {
							int LA17_46 = input.LA(5);
							if ( (LA17_46==COLON) ) {
								switch ( input.LA(6) ) {
								case LPAREN:
									{
									alt17=6;
									}
									break;
								case FTSPHRASE:
									{
									int LA17_37 = input.LA(7);
									if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_37==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 37, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case COMMA:
								case DOT:
								case NOT:
								case QUESTION_MARK:
								case STAR:
								case TO:
									{
									alt17=2;
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
									int LA17_38 = input.LA(7);
									if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
										alt17=2;
									}
									else if ( (LA17_38==DOTDOT) ) {
										alt17=5;
									}

									else {
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 17, 38, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}

									}
									break;
								case LSQUARE:
								case LT:
									{
									alt17=5;
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
											new NoViableAltException("", 17, 41, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 46, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}

						else {
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 40, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( (LA17_22==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case NOT:
						case QUESTION_MARK:
						case STAR:
						case TO:
							{
							alt17=2;
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
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 41, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 22, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case TO:
					{
					int LA17_23 = input.LA(3);
					if ( (LA17_23==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case NOT:
						case QUESTION_MARK:
						case STAR:
						case TO:
							{
							alt17=2;
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
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 41, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 23, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case OR:
					{
					int LA17_24 = input.LA(3);
					if ( (LA17_24==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case NOT:
						case QUESTION_MARK:
						case STAR:
						case TO:
							{
							alt17=2;
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
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 41, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 24, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case AND:
					{
					int LA17_25 = input.LA(3);
					if ( (LA17_25==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case NOT:
						case QUESTION_MARK:
						case STAR:
						case TO:
							{
							alt17=2;
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
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 41, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 25, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case NOT:
					{
					int LA17_26 = input.LA(3);
					if ( (LA17_26==COLON) ) {
						switch ( input.LA(4) ) {
						case LPAREN:
							{
							alt17=6;
							}
							break;
						case FTSPHRASE:
							{
							int LA17_37 = input.LA(5);
							if ( (LA17_37==EOF||(LA17_37 >= AMP && LA17_37 <= BAR)||LA17_37==CARAT||LA17_37==COMMA||(LA17_37 >= DATETIME && LA17_37 <= DECIMAL_INTEGER_LITERAL)||LA17_37==DOT||LA17_37==EQUALS||LA17_37==EXCLAMATION||LA17_37==FLOATING_POINT_LITERAL||(LA17_37 >= FTSPHRASE && LA17_37 <= FTSWORD)||LA17_37==ID||(LA17_37 >= LPAREN && LA17_37 <= LT)||LA17_37==MINUS||LA17_37==NOT||(LA17_37 >= OR && LA17_37 <= PERCENT)||LA17_37==PLUS||LA17_37==QUESTION_MARK||LA17_37==RPAREN||LA17_37==STAR||(LA17_37 >= TILDA && LA17_37 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_37==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 37, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case COMMA:
						case DOT:
						case NOT:
						case QUESTION_MARK:
						case STAR:
						case TO:
							{
							alt17=2;
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
							int LA17_38 = input.LA(5);
							if ( (LA17_38==EOF||(LA17_38 >= AMP && LA17_38 <= BAR)||LA17_38==CARAT||LA17_38==COMMA||(LA17_38 >= DATETIME && LA17_38 <= DECIMAL_INTEGER_LITERAL)||LA17_38==DOT||LA17_38==EQUALS||LA17_38==EXCLAMATION||LA17_38==FLOATING_POINT_LITERAL||(LA17_38 >= FTSPHRASE && LA17_38 <= FTSWORD)||LA17_38==ID||(LA17_38 >= LPAREN && LA17_38 <= LT)||LA17_38==MINUS||LA17_38==NOT||(LA17_38 >= OR && LA17_38 <= PERCENT)||LA17_38==PLUS||LA17_38==QUESTION_MARK||LA17_38==RPAREN||LA17_38==STAR||(LA17_38 >= TILDA && LA17_38 <= URI)) ) {
								alt17=2;
							}
							else if ( (LA17_38==DOTDOT) ) {
								alt17=5;
							}

							else {
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 17, 38, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

							}
							break;
						case LSQUARE:
						case LT:
							{
							alt17=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 17, 41, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 17, 26, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				default:
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 8, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
				}
				break;
			case FTSPHRASE:
				{
				int LA17_9 = input.LA(2);
				if ( (LA17_9==EOF||(LA17_9 >= AMP && LA17_9 <= BAR)||LA17_9==CARAT||LA17_9==COMMA||(LA17_9 >= DATETIME && LA17_9 <= DECIMAL_INTEGER_LITERAL)||LA17_9==DOT||LA17_9==EQUALS||LA17_9==EXCLAMATION||LA17_9==FLOATING_POINT_LITERAL||(LA17_9 >= FTSPHRASE && LA17_9 <= FTSWORD)||LA17_9==ID||(LA17_9 >= LPAREN && LA17_9 <= LT)||LA17_9==MINUS||LA17_9==NOT||(LA17_9 >= OR && LA17_9 <= PERCENT)||LA17_9==PLUS||LA17_9==QUESTION_MARK||LA17_9==RPAREN||LA17_9==STAR||(LA17_9 >= TILDA && LA17_9 <= URI)) ) {
					alt17=2;
				}
				else if ( (LA17_9==DOTDOT) ) {
					alt17=5;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 17, 9, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case COMMA:
			case DOT:
			case QUESTION_MARK:
			case STAR:
				{
				alt17=2;
				}
				break;
			case EQUALS:
				{
				alt17=3;
				}
				break;
			case TILDA:
				{
				alt17=4;
				}
				break;
			case LSQUARE:
			case LT:
				{
				alt17=5;
				}
				break;
			case LPAREN:
				{
				alt17=7;
				}
				break;
			case PERCENT:
				{
				alt17=8;
				}
				break;
			default:
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 17, 0, input);
				throw nvae;
			}
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:451:12: ftsTermOrPhrase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsTermOrPhrase_in_ftsTest1822);
					ftsTermOrPhrase38=ftsTermOrPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsTermOrPhrase38.getTree());

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:453:12: ftsExactTermOrPhrase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsExactTermOrPhrase_in_ftsTest1845);
					ftsExactTermOrPhrase39=ftsExactTermOrPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsExactTermOrPhrase39.getTree());

					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:455:12: ftsTokenisedTermOrPhrase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsTokenisedTermOrPhrase_in_ftsTest1869);
					ftsTokenisedTermOrPhrase40=ftsTokenisedTermOrPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsTokenisedTermOrPhrase40.getTree());

					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:457:12: ftsRange
					{
					pushFollow(FOLLOW_ftsRange_in_ftsTest1892);
					ftsRange41=ftsRange();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRange.add(ftsRange41.getTree());
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
					// 458:17: -> ^( RANGE ftsRange )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:459:25: ^( RANGE ftsRange )
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
				case 6 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:461:12: ftsFieldGroup
					{
					pushFollow(FOLLOW_ftsFieldGroup_in_ftsTest1965);
					ftsFieldGroup42=ftsFieldGroup();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroup.add(ftsFieldGroup42.getTree());
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
					// 462:17: -> ftsFieldGroup
					{
						adaptor.addChild(root_0, stream_ftsFieldGroup.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 7 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:463:12: LPAREN ftsDisjunction RPAREN
					{
					LPAREN43=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsTest1998); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN43);

					pushFollow(FOLLOW_ftsDisjunction_in_ftsTest2000);
					ftsDisjunction44=ftsDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsDisjunction.add(ftsDisjunction44.getTree());
					RPAREN45=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsTest2002); if (state.failed) return retval; 
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
					// 464:17: -> ftsDisjunction
					{
						adaptor.addChild(root_0, stream_ftsDisjunction.nextTree());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 8 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:465:12: template
					{
					pushFollow(FOLLOW_template_in_ftsTest2035);
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
					// 466:17: -> template
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:469:1: cmisTest : ( cmisTerm -> ^( TERM cmisTerm ) | cmisPhrase -> ^( PHRASE cmisPhrase ) );
	public final FTSParser.cmisTest_return cmisTest() throws RecognitionException {
		FTSParser.cmisTest_return retval = new FTSParser.cmisTest_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope cmisTerm47 =null;
		ParserRuleReturnScope cmisPhrase48 =null;

		RewriteRuleSubtreeStream stream_cmisPhrase=new RewriteRuleSubtreeStream(adaptor,"rule cmisPhrase");
		RewriteRuleSubtreeStream stream_cmisTerm=new RewriteRuleSubtreeStream(adaptor,"rule cmisTerm");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:470:9: ( cmisTerm -> ^( TERM cmisTerm ) | cmisPhrase -> ^( PHRASE cmisPhrase ) )
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==COMMA||(LA18_0 >= DATETIME && LA18_0 <= DECIMAL_INTEGER_LITERAL)||LA18_0==DOT||LA18_0==FLOATING_POINT_LITERAL||(LA18_0 >= FTSPRE && LA18_0 <= FTSWORD)||LA18_0==ID||LA18_0==NOT||LA18_0==QUESTION_MARK||LA18_0==STAR||LA18_0==TO) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:471:9: cmisTerm
					{
					pushFollow(FOLLOW_cmisTerm_in_cmisTest2088);
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
					// 472:17: -> ^( TERM cmisTerm )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:473:25: ^( TERM cmisTerm )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:474:11: cmisPhrase
					{
					pushFollow(FOLLOW_cmisPhrase_in_cmisTest2148);
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
					// 475:17: -> ^( PHRASE cmisPhrase )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:476:25: ^( PHRASE cmisPhrase )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:479:1: template : ( PERCENT tempReference -> ^( TEMPLATE tempReference ) | PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN -> ^( TEMPLATE ( tempReference )+ ) );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:480:9: ( PERCENT tempReference -> ^( TEMPLATE tempReference ) | PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN -> ^( TEMPLATE ( tempReference )+ ) )
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==PERCENT) ) {
				int LA21_1 = input.LA(2);
				if ( (LA21_1==LPAREN) ) {
					alt21=2;
				}
				else if ( ((LA21_1 >= AND && LA21_1 <= AT)||LA21_1==ID||LA21_1==NOT||LA21_1==OR||(LA21_1 >= TO && LA21_1 <= URI)) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:481:9: PERCENT tempReference
					{
					PERCENT49=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_template2229); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PERCENT.add(PERCENT49);

					pushFollow(FOLLOW_tempReference_in_template2231);
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
					// 482:17: -> ^( TEMPLATE tempReference )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:483:25: ^( TEMPLATE tempReference )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:484:11: PERCENT LPAREN ( tempReference ( COMMA )? )+ RPAREN
					{
					PERCENT51=(Token)match(input,PERCENT,FOLLOW_PERCENT_in_template2291); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PERCENT.add(PERCENT51);

					LPAREN52=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_template2293); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN52);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:484:26: ( tempReference ( COMMA )? )+
					int cnt20=0;
					loop20:
					while (true) {
						int alt20=2;
						int LA20_0 = input.LA(1);
						if ( ((LA20_0 >= AND && LA20_0 <= AT)||LA20_0==ID||LA20_0==NOT||LA20_0==OR||(LA20_0 >= TO && LA20_0 <= URI)) ) {
							alt20=1;
						}

						switch (alt20) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:484:27: tempReference ( COMMA )?
							{
							pushFollow(FOLLOW_tempReference_in_template2296);
							tempReference53=tempReference();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_tempReference.add(tempReference53.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:484:41: ( COMMA )?
							int alt19=2;
							int LA19_0 = input.LA(1);
							if ( (LA19_0==COMMA) ) {
								alt19=1;
							}
							switch (alt19) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:484:41: COMMA
									{
									COMMA54=(Token)match(input,COMMA,FOLLOW_COMMA_in_template2298); if (state.failed) return retval; 
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

					RPAREN55=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_template2303); if (state.failed) return retval; 
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
					// 485:17: -> ^( TEMPLATE ( tempReference )+ )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:486:25: ^( TEMPLATE ( tempReference )+ )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:489:1: fuzzy : TILDA number -> ^( FUZZY number ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:490:9: ( TILDA number -> ^( FUZZY number ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:491:9: TILDA number
			{
			TILDA56=(Token)match(input,TILDA,FOLLOW_TILDA_in_fuzzy2385); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA56);

			pushFollow(FOLLOW_number_in_fuzzy2387);
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
			// 492:17: -> ^( FUZZY number )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:493:25: ^( FUZZY number )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:496:1: slop : TILDA DECIMAL_INTEGER_LITERAL -> ^( FUZZY DECIMAL_INTEGER_LITERAL ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:497:9: ( TILDA DECIMAL_INTEGER_LITERAL -> ^( FUZZY DECIMAL_INTEGER_LITERAL ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:498:9: TILDA DECIMAL_INTEGER_LITERAL
			{
			TILDA58=(Token)match(input,TILDA,FOLLOW_TILDA_in_slop2468); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA58);

			DECIMAL_INTEGER_LITERAL59=(Token)match(input,DECIMAL_INTEGER_LITERAL,FOLLOW_DECIMAL_INTEGER_LITERAL_in_slop2470); if (state.failed) return retval; 
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
			// 499:17: -> ^( FUZZY DECIMAL_INTEGER_LITERAL )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:500:25: ^( FUZZY DECIMAL_INTEGER_LITERAL )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:503:1: boost : CARAT number -> ^( BOOST number ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:504:9: ( CARAT number -> ^( BOOST number ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:505:9: CARAT number
			{
			CARAT60=(Token)match(input,CARAT,FOLLOW_CARAT_in_boost2551); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_CARAT.add(CARAT60);

			pushFollow(FOLLOW_number_in_boost2553);
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
			// 506:17: -> ^( BOOST number )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:507:25: ^( BOOST number )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:510:1: ftsTermOrPhrase : ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:511:9: ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) )
			int alt27=3;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==AT) && (synpred3_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==ID) ) {
				int LA27_2 = input.LA(2);
				if ( (LA27_2==DOT) ) {
					int LA27_10 = input.LA(3);
					if ( (LA27_10==ID) ) {
						int LA27_12 = input.LA(4);
						if ( (synpred3_FTS()) ) {
							alt27=1;
						}
						else if ( (true) ) {
							alt27=3;
						}

					}
					else if ( (LA27_10==EOF||(LA27_10 >= AMP && LA27_10 <= BAR)||LA27_10==CARAT||LA27_10==COMMA||(LA27_10 >= DATETIME && LA27_10 <= DECIMAL_INTEGER_LITERAL)||LA27_10==DOT||LA27_10==EQUALS||LA27_10==EXCLAMATION||LA27_10==FLOATING_POINT_LITERAL||(LA27_10 >= FTSPHRASE && LA27_10 <= FTSWORD)||(LA27_10 >= LPAREN && LA27_10 <= LT)||LA27_10==MINUS||LA27_10==NOT||(LA27_10 >= OR && LA27_10 <= PERCENT)||LA27_10==PLUS||LA27_10==QUESTION_MARK||LA27_10==RPAREN||LA27_10==STAR||(LA27_10 >= TILDA && LA27_10 <= URI)) ) {
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
				else if ( (LA27_2==COLON) && (synpred3_FTS())) {
					alt27=1;
				}
				else if ( (LA27_2==EOF||(LA27_2 >= AMP && LA27_2 <= BAR)||LA27_2==CARAT||LA27_2==COMMA||(LA27_2 >= DATETIME && LA27_2 <= DECIMAL_INTEGER_LITERAL)||LA27_2==EQUALS||LA27_2==EXCLAMATION||LA27_2==FLOATING_POINT_LITERAL||(LA27_2 >= FTSPHRASE && LA27_2 <= FTSWORD)||LA27_2==ID||(LA27_2 >= LPAREN && LA27_2 <= LT)||LA27_2==MINUS||LA27_2==NOT||(LA27_2 >= OR && LA27_2 <= PERCENT)||LA27_2==PLUS||LA27_2==QUESTION_MARK||LA27_2==RPAREN||LA27_2==STAR||(LA27_2 >= TILDA && LA27_2 <= URI)) ) {
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
				if ( (LA27_3==COLON) && (synpred3_FTS())) {
					alt27=1;
				}
				else if ( (LA27_3==EOF||(LA27_3 >= AMP && LA27_3 <= BAR)||LA27_3==CARAT||LA27_3==COMMA||(LA27_3 >= DATETIME && LA27_3 <= DECIMAL_INTEGER_LITERAL)||LA27_3==DOT||LA27_3==EQUALS||LA27_3==EXCLAMATION||LA27_3==FLOATING_POINT_LITERAL||(LA27_3 >= FTSPHRASE && LA27_3 <= FTSWORD)||LA27_3==ID||(LA27_3 >= LPAREN && LA27_3 <= LT)||LA27_3==MINUS||LA27_3==NOT||(LA27_3 >= OR && LA27_3 <= PERCENT)||LA27_3==PLUS||LA27_3==QUESTION_MARK||LA27_3==RPAREN||LA27_3==STAR||(LA27_3 >= TILDA && LA27_3 <= URI)) ) {
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
			else if ( (LA27_0==OR) && (synpred3_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==AND) && (synpred3_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==NOT) ) {
				int LA27_6 = input.LA(2);
				if ( (LA27_6==COLON) && (synpred3_FTS())) {
					alt27=1;
				}
				else if ( (LA27_6==EOF||(LA27_6 >= AMP && LA27_6 <= BAR)||LA27_6==CARAT||LA27_6==COMMA||(LA27_6 >= DATETIME && LA27_6 <= DECIMAL_INTEGER_LITERAL)||LA27_6==DOT||LA27_6==EQUALS||LA27_6==EXCLAMATION||LA27_6==FLOATING_POINT_LITERAL||(LA27_6 >= FTSPHRASE && LA27_6 <= FTSWORD)||LA27_6==ID||(LA27_6 >= LPAREN && LA27_6 <= LT)||LA27_6==MINUS||LA27_6==NOT||(LA27_6 >= OR && LA27_6 <= PERCENT)||LA27_6==PLUS||LA27_6==QUESTION_MARK||LA27_6==RPAREN||LA27_6==STAR||(LA27_6 >= TILDA && LA27_6 <= URI)) ) {
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
			else if ( (LA27_0==URI) && (synpred3_FTS())) {
				alt27=1;
			}
			else if ( (LA27_0==FTSPHRASE) ) {
				alt27=2;
			}
			else if ( (LA27_0==COMMA||(LA27_0 >= DATETIME && LA27_0 <= DECIMAL_INTEGER_LITERAL)||LA27_0==DOT||LA27_0==FLOATING_POINT_LITERAL||(LA27_0 >= FTSPRE && LA27_0 <= FTSWORD)||LA27_0==QUESTION_MARK||LA27_0==STAR) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:512:9: ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					{
					pushFollow(FOLLOW_fieldReference_in_ftsTermOrPhrase2642);
					fieldReference62=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference62.getTree());
					COLON63=(Token)match(input,COLON,FOLLOW_COLON_in_ftsTermOrPhrase2644); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON63);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:513:9: ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					int alt24=2;
					int LA24_0 = input.LA(1);
					if ( (LA24_0==FTSPHRASE) ) {
						alt24=1;
					}
					else if ( (LA24_0==COMMA||(LA24_0 >= DATETIME && LA24_0 <= DECIMAL_INTEGER_LITERAL)||LA24_0==DOT||LA24_0==FLOATING_POINT_LITERAL||(LA24_0 >= FTSPRE && LA24_0 <= FTSWORD)||LA24_0==ID||LA24_0==NOT||LA24_0==QUESTION_MARK||LA24_0==STAR||LA24_0==TO) ) {
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
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:17: FTSPHRASE ( ( slop )=> slop )?
							{
							FTSPHRASE64=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2672); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE64);

							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:27: ( ( slop )=> slop )?
							int alt22=2;
							int LA22_0 = input.LA(1);
							if ( (LA22_0==TILDA) ) {
								int LA22_1 = input.LA(2);
								if ( (LA22_1==DECIMAL_INTEGER_LITERAL) ) {
									int LA22_3 = input.LA(3);
									if ( (synpred4_FTS()) ) {
										alt22=1;
									}
								}
							}
							switch (alt22) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:28: ( slop )=> slop
									{
									pushFollow(FOLLOW_slop_in_ftsTermOrPhrase2680);
									slop65=slop();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_slop.add(slop65.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: fieldReference, slop, FTSPHRASE
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 515:17: -> ^( PHRASE FTSPHRASE fieldReference ( slop )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:515:20: ^( PHRASE FTSPHRASE fieldReference ( slop )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
								adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:515:54: ( slop )?
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
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:17: ftsWord ( ( fuzzy )=> fuzzy )?
							{
							pushFollow(FOLLOW_ftsWord_in_ftsTermOrPhrase2747);
							ftsWord66=ftsWord();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord66.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:25: ( ( fuzzy )=> fuzzy )?
							int alt23=2;
							int LA23_0 = input.LA(1);
							if ( (LA23_0==TILDA) ) {
								int LA23_1 = input.LA(2);
								if ( (LA23_1==DECIMAL_INTEGER_LITERAL||LA23_1==FLOATING_POINT_LITERAL) ) {
									int LA23_3 = input.LA(3);
									if ( (synpred5_FTS()) ) {
										alt23=1;
									}
								}
							}
							switch (alt23) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:26: ( fuzzy )=> fuzzy
									{
									pushFollow(FOLLOW_fuzzy_in_ftsTermOrPhrase2756);
									fuzzy67=fuzzy();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy67.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: fieldReference, ftsWord, fuzzy
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 518:17: -> ^( TERM ftsWord fieldReference ( fuzzy )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:518:20: ^( TERM ftsWord fieldReference ( fuzzy )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
								adaptor.addChild(root_1, stream_ftsWord.nextTree());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:518:50: ( fuzzy )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:521:9: FTSPHRASE ( ( slop )=> slop )?
					{
					FTSPHRASE68=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2817); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE68);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:521:19: ( ( slop )=> slop )?
					int alt25=2;
					int LA25_0 = input.LA(1);
					if ( (LA25_0==TILDA) ) {
						int LA25_1 = input.LA(2);
						if ( (LA25_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA25_3 = input.LA(3);
							if ( (synpred6_FTS()) ) {
								alt25=1;
							}
						}
					}
					switch (alt25) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:521:20: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsTermOrPhrase2825);
							slop69=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop69.getTree());
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
					// 522:17: -> ^( PHRASE FTSPHRASE ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:522:20: ^( PHRASE FTSPHRASE ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
						adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:522:39: ( slop )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:9: ftsWord ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsWord_in_ftsTermOrPhrase2875);
					ftsWord70=ftsWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord70.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:17: ( ( fuzzy )=> fuzzy )?
					int alt26=2;
					int LA26_0 = input.LA(1);
					if ( (LA26_0==TILDA) ) {
						int LA26_1 = input.LA(2);
						if ( (LA26_1==DECIMAL_INTEGER_LITERAL||LA26_1==FLOATING_POINT_LITERAL) ) {
							int LA26_3 = input.LA(3);
							if ( (synpred7_FTS()) ) {
								alt26=1;
							}
						}
					}
					switch (alt26) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:18: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsTermOrPhrase2884);
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
					// 525:17: -> ^( TERM ftsWord ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:525:20: ^( TERM ftsWord ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsWord.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:525:35: ( fuzzy )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:529:1: ftsExactTermOrPhrase : EQUALS ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:530:9: ( EQUALS ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:531:9: EQUALS ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) )
			{
			EQUALS72=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsExactTermOrPhrase2963); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS72);

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:532:9: ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord ( fuzzy )? ) )
			int alt33=3;
			int LA33_0 = input.LA(1);
			if ( (LA33_0==AT) && (synpred8_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==ID) ) {
				int LA33_2 = input.LA(2);
				if ( (LA33_2==DOT) ) {
					int LA33_10 = input.LA(3);
					if ( (LA33_10==ID) ) {
						int LA33_12 = input.LA(4);
						if ( (LA33_12==EOF||(LA33_12 >= AMP && LA33_12 <= BAR)||LA33_12==CARAT||LA33_12==COMMA||(LA33_12 >= DATETIME && LA33_12 <= DECIMAL_INTEGER_LITERAL)||(LA33_12 >= DOT && LA33_12 <= DOTDOT)||LA33_12==EQUALS||LA33_12==EXCLAMATION||LA33_12==FLOATING_POINT_LITERAL||(LA33_12 >= FTSPHRASE && LA33_12 <= FTSWORD)||LA33_12==ID||(LA33_12 >= LPAREN && LA33_12 <= LT)||LA33_12==MINUS||LA33_12==NOT||(LA33_12 >= OR && LA33_12 <= PERCENT)||LA33_12==PLUS||LA33_12==QUESTION_MARK||LA33_12==RPAREN||LA33_12==STAR||(LA33_12 >= TILDA && LA33_12 <= URI)) ) {
							alt33=3;
						}
						else if ( (LA33_12==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
							case LSQUARE:
							case LT:
								{
								alt33=3;
								}
								break;
							case ID:
								{
								int LA33_14 = input.LA(6);
								if ( (synpred8_FTS()) ) {
									alt33=1;
								}
								else if ( (true) ) {
									alt33=3;
								}

								}
								break;
							case TO:
								{
								int LA33_15 = input.LA(6);
								if ( (synpred8_FTS()) ) {
									alt33=1;
								}
								else if ( (true) ) {
									alt33=3;
								}

								}
								break;
							case OR:
								{
								int LA33_16 = input.LA(6);
								if ( (LA33_16==COLON) ) {
									switch ( input.LA(7) ) {
									case LPAREN:
									case LSQUARE:
									case LT:
										{
										alt33=3;
										}
										break;
									case FTSPHRASE:
										{
										int LA33_25 = input.LA(8);
										if ( (synpred8_FTS()) ) {
											alt33=1;
										}
										else if ( (true) ) {
											alt33=3;
										}

										}
										break;
									case COMMA:
									case DOT:
										{
										int LA33_20 = input.LA(8);
										if ( ((LA33_20 >= DATETIME && LA33_20 <= DECIMAL_INTEGER_LITERAL)||LA33_20==FLOATING_POINT_LITERAL||(LA33_20 >= FTSPRE && LA33_20 <= FTSWORD)||LA33_20==ID||LA33_20==NOT||LA33_20==QUESTION_MARK||LA33_20==STAR||LA33_20==TO) ) {
											int LA33_24 = input.LA(9);
											if ( (synpred8_FTS()) ) {
												alt33=1;
											}
											else if ( (true) ) {
												alt33=3;
											}

										}

										else {
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 33, 20, input);
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
										int LA33_26 = input.LA(8);
										if ( (synpred8_FTS()) ) {
											alt33=1;
										}
										else if ( (true) ) {
											alt33=3;
										}

										}
										break;
									case NOT:
									case QUESTION_MARK:
									case STAR:
									case TO:
										{
										int LA33_27 = input.LA(8);
										if ( (synpred8_FTS()) ) {
											alt33=1;
										}
										else if ( (true) ) {
											alt33=3;
										}

										}
										break;
									default:
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 33, 23, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 33, 16, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case AND:
								{
								int LA33_17 = input.LA(6);
								if ( (LA33_17==COLON) ) {
									switch ( input.LA(7) ) {
									case LPAREN:
									case LSQUARE:
									case LT:
										{
										alt33=3;
										}
										break;
									case FTSPHRASE:
										{
										int LA33_25 = input.LA(8);
										if ( (synpred8_FTS()) ) {
											alt33=1;
										}
										else if ( (true) ) {
											alt33=3;
										}

										}
										break;
									case COMMA:
									case DOT:
										{
										int LA33_20 = input.LA(8);
										if ( ((LA33_20 >= DATETIME && LA33_20 <= DECIMAL_INTEGER_LITERAL)||LA33_20==FLOATING_POINT_LITERAL||(LA33_20 >= FTSPRE && LA33_20 <= FTSWORD)||LA33_20==ID||LA33_20==NOT||LA33_20==QUESTION_MARK||LA33_20==STAR||LA33_20==TO) ) {
											int LA33_24 = input.LA(9);
											if ( (synpred8_FTS()) ) {
												alt33=1;
											}
											else if ( (true) ) {
												alt33=3;
											}

										}

										else {
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 33, 20, input);
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
										int LA33_26 = input.LA(8);
										if ( (synpred8_FTS()) ) {
											alt33=1;
										}
										else if ( (true) ) {
											alt33=3;
										}

										}
										break;
									case NOT:
									case QUESTION_MARK:
									case STAR:
									case TO:
										{
										int LA33_27 = input.LA(8);
										if ( (synpred8_FTS()) ) {
											alt33=1;
										}
										else if ( (true) ) {
											alt33=3;
										}

										}
										break;
									default:
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 33, 23, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 33, 17, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case NOT:
								{
								int LA33_18 = input.LA(6);
								if ( (synpred8_FTS()) ) {
									alt33=1;
								}
								else if ( (true) ) {
									alt33=3;
								}

								}
								break;
							case FTSPHRASE:
								{
								int LA33_19 = input.LA(6);
								if ( (synpred8_FTS()) ) {
									alt33=1;
								}
								else if ( (true) ) {
									alt33=3;
								}

								}
								break;
							case COMMA:
							case DOT:
								{
								int LA33_20 = input.LA(6);
								if ( ((LA33_20 >= DATETIME && LA33_20 <= DECIMAL_INTEGER_LITERAL)||LA33_20==FLOATING_POINT_LITERAL||(LA33_20 >= FTSPRE && LA33_20 <= FTSWORD)||LA33_20==ID||LA33_20==NOT||LA33_20==QUESTION_MARK||LA33_20==STAR||LA33_20==TO) ) {
									int LA33_24 = input.LA(7);
									if ( (synpred8_FTS()) ) {
										alt33=1;
									}
									else if ( (true) ) {
										alt33=3;
									}

								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 33, 20, input);
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
								int LA33_21 = input.LA(6);
								if ( (synpred8_FTS()) ) {
									alt33=1;
								}
								else if ( (true) ) {
									alt33=3;
								}

								}
								break;
							case QUESTION_MARK:
							case STAR:
								{
								int LA33_22 = input.LA(6);
								if ( (synpred8_FTS()) ) {
									alt33=1;
								}
								else if ( (true) ) {
									alt33=3;
								}

								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 33, 13, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
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
					else if ( (LA33_10==EOF||(LA33_10 >= AMP && LA33_10 <= BAR)||LA33_10==CARAT||LA33_10==COMMA||(LA33_10 >= DATETIME && LA33_10 <= DECIMAL_INTEGER_LITERAL)||LA33_10==DOT||LA33_10==EQUALS||LA33_10==EXCLAMATION||LA33_10==FLOATING_POINT_LITERAL||(LA33_10 >= FTSPHRASE && LA33_10 <= FTSWORD)||(LA33_10 >= LPAREN && LA33_10 <= LT)||LA33_10==MINUS||LA33_10==NOT||(LA33_10 >= OR && LA33_10 <= PERCENT)||LA33_10==PLUS||LA33_10==QUESTION_MARK||LA33_10==RPAREN||LA33_10==STAR||(LA33_10 >= TILDA && LA33_10 <= URI)) ) {
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
				else if ( (LA33_2==COLON) && (synpred8_FTS())) {
					alt33=1;
				}
				else if ( (LA33_2==EOF||(LA33_2 >= AMP && LA33_2 <= BAR)||LA33_2==CARAT||LA33_2==COMMA||(LA33_2 >= DATETIME && LA33_2 <= DECIMAL_INTEGER_LITERAL)||LA33_2==EQUALS||LA33_2==EXCLAMATION||LA33_2==FLOATING_POINT_LITERAL||(LA33_2 >= FTSPHRASE && LA33_2 <= FTSWORD)||LA33_2==ID||(LA33_2 >= LPAREN && LA33_2 <= LT)||LA33_2==MINUS||LA33_2==NOT||(LA33_2 >= OR && LA33_2 <= PERCENT)||LA33_2==PLUS||LA33_2==QUESTION_MARK||LA33_2==RPAREN||LA33_2==STAR||(LA33_2 >= TILDA && LA33_2 <= URI)) ) {
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
				if ( (LA33_3==COLON) && (synpred8_FTS())) {
					alt33=1;
				}
				else if ( (LA33_3==EOF||(LA33_3 >= AMP && LA33_3 <= BAR)||LA33_3==CARAT||LA33_3==COMMA||(LA33_3 >= DATETIME && LA33_3 <= DECIMAL_INTEGER_LITERAL)||LA33_3==DOT||LA33_3==EQUALS||LA33_3==EXCLAMATION||LA33_3==FLOATING_POINT_LITERAL||(LA33_3 >= FTSPHRASE && LA33_3 <= FTSWORD)||LA33_3==ID||(LA33_3 >= LPAREN && LA33_3 <= LT)||LA33_3==MINUS||LA33_3==NOT||(LA33_3 >= OR && LA33_3 <= PERCENT)||LA33_3==PLUS||LA33_3==QUESTION_MARK||LA33_3==RPAREN||LA33_3==STAR||(LA33_3 >= TILDA && LA33_3 <= URI)) ) {
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
			else if ( (LA33_0==OR) && (synpred8_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==AND) && (synpred8_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==NOT) ) {
				int LA33_6 = input.LA(2);
				if ( (LA33_6==COLON) && (synpred8_FTS())) {
					alt33=1;
				}
				else if ( (LA33_6==EOF||(LA33_6 >= AMP && LA33_6 <= BAR)||LA33_6==CARAT||LA33_6==COMMA||(LA33_6 >= DATETIME && LA33_6 <= DECIMAL_INTEGER_LITERAL)||LA33_6==DOT||LA33_6==EQUALS||LA33_6==EXCLAMATION||LA33_6==FLOATING_POINT_LITERAL||(LA33_6 >= FTSPHRASE && LA33_6 <= FTSWORD)||LA33_6==ID||(LA33_6 >= LPAREN && LA33_6 <= LT)||LA33_6==MINUS||LA33_6==NOT||(LA33_6 >= OR && LA33_6 <= PERCENT)||LA33_6==PLUS||LA33_6==QUESTION_MARK||LA33_6==RPAREN||LA33_6==STAR||(LA33_6 >= TILDA && LA33_6 <= URI)) ) {
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
			else if ( (LA33_0==URI) && (synpred8_FTS())) {
				alt33=1;
			}
			else if ( (LA33_0==FTSPHRASE) ) {
				alt33=2;
			}
			else if ( (LA33_0==COMMA||(LA33_0 >= DATETIME && LA33_0 <= DECIMAL_INTEGER_LITERAL)||LA33_0==DOT||LA33_0==FLOATING_POINT_LITERAL||(LA33_0 >= FTSPRE && LA33_0 <= FTSWORD)||LA33_0==QUESTION_MARK||LA33_0==STAR) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:533:9: ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) )
					{
					pushFollow(FOLLOW_fieldReference_in_ftsExactTermOrPhrase2991);
					fieldReference73=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference73.getTree());
					COLON74=(Token)match(input,COLON,FOLLOW_COLON_in_ftsExactTermOrPhrase2993); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON74);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:534:9: ( FTSPHRASE ( ( slop )=> slop )? -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? ) )
					int alt30=2;
					int LA30_0 = input.LA(1);
					if ( (LA30_0==FTSPHRASE) ) {
						alt30=1;
					}
					else if ( (LA30_0==COMMA||(LA30_0 >= DATETIME && LA30_0 <= DECIMAL_INTEGER_LITERAL)||LA30_0==DOT||LA30_0==FLOATING_POINT_LITERAL||(LA30_0 >= FTSPRE && LA30_0 <= FTSWORD)||LA30_0==ID||LA30_0==NOT||LA30_0==QUESTION_MARK||LA30_0==STAR||LA30_0==TO) ) {
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
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:17: FTSPHRASE ( ( slop )=> slop )?
							{
							FTSPHRASE75=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3021); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE75);

							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:27: ( ( slop )=> slop )?
							int alt28=2;
							int LA28_0 = input.LA(1);
							if ( (LA28_0==TILDA) ) {
								int LA28_1 = input.LA(2);
								if ( (LA28_1==DECIMAL_INTEGER_LITERAL) ) {
									int LA28_3 = input.LA(3);
									if ( (synpred9_FTS()) ) {
										alt28=1;
									}
								}
							}
							switch (alt28) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:28: ( slop )=> slop
									{
									pushFollow(FOLLOW_slop_in_ftsExactTermOrPhrase3029);
									slop76=slop();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_slop.add(slop76.getTree());
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
							// 536:17: -> ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:536:20: ^( EXACT_PHRASE FTSPHRASE fieldReference ( slop )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_PHRASE, "EXACT_PHRASE"), root_1);
								adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:536:60: ( slop )?
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
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:17: ftsWord ( ( fuzzy )=> fuzzy )?
							{
							pushFollow(FOLLOW_ftsWord_in_ftsExactTermOrPhrase3096);
							ftsWord77=ftsWord();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord77.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:25: ( ( fuzzy )=> fuzzy )?
							int alt29=2;
							int LA29_0 = input.LA(1);
							if ( (LA29_0==TILDA) ) {
								int LA29_1 = input.LA(2);
								if ( (LA29_1==DECIMAL_INTEGER_LITERAL||LA29_1==FLOATING_POINT_LITERAL) ) {
									int LA29_3 = input.LA(3);
									if ( (synpred10_FTS()) ) {
										alt29=1;
									}
								}
							}
							switch (alt29) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:26: ( fuzzy )=> fuzzy
									{
									pushFollow(FOLLOW_fuzzy_in_ftsExactTermOrPhrase3105);
									fuzzy78=fuzzy();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy78.getTree());
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
							// 539:17: -> ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:539:20: ^( EXACT_TERM ftsWord fieldReference ( fuzzy )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_TERM, "EXACT_TERM"), root_1);
								adaptor.addChild(root_1, stream_ftsWord.nextTree());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:539:56: ( fuzzy )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:542:9: FTSPHRASE ( ( slop )=> slop )?
					{
					FTSPHRASE79=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3166); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE79);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:542:19: ( ( slop )=> slop )?
					int alt31=2;
					int LA31_0 = input.LA(1);
					if ( (LA31_0==TILDA) ) {
						int LA31_1 = input.LA(2);
						if ( (LA31_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA31_3 = input.LA(3);
							if ( (synpred11_FTS()) ) {
								alt31=1;
							}
						}
					}
					switch (alt31) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:542:20: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsExactTermOrPhrase3174);
							slop80=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop80.getTree());
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
					// 543:17: -> ^( EXACT_PHRASE FTSPHRASE ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:543:20: ^( EXACT_PHRASE FTSPHRASE ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_PHRASE, "EXACT_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:543:45: ( slop )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:545:9: ftsWord ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsWord_in_ftsExactTermOrPhrase3224);
					ftsWord81=ftsWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord81.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:545:17: ( ( fuzzy )=> fuzzy )?
					int alt32=2;
					int LA32_0 = input.LA(1);
					if ( (LA32_0==TILDA) ) {
						int LA32_1 = input.LA(2);
						if ( (LA32_1==DECIMAL_INTEGER_LITERAL||LA32_1==FLOATING_POINT_LITERAL) ) {
							int LA32_3 = input.LA(3);
							if ( (synpred12_FTS()) ) {
								alt32=1;
							}
						}
					}
					switch (alt32) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:545:18: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsExactTermOrPhrase3233);
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
					// 546:17: -> ^( EXACT_TERM ftsWord ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:546:20: ^( EXACT_TERM ftsWord ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(EXACT_TERM, "EXACT_TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsWord.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:546:41: ( fuzzy )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:551:1: ftsTokenisedTermOrPhrase : TILDA ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:552:9: ( TILDA ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:553:9: TILDA ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) )
			{
			TILDA83=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsTokenisedTermOrPhrase3314); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA83);

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:554:9: ( ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) ) | FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord ( fuzzy )? ) )
			int alt39=3;
			int LA39_0 = input.LA(1);
			if ( (LA39_0==AT) && (synpred13_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==ID) ) {
				int LA39_2 = input.LA(2);
				if ( (LA39_2==DOT) ) {
					int LA39_10 = input.LA(3);
					if ( (LA39_10==ID) ) {
						int LA39_12 = input.LA(4);
						if ( (LA39_12==EOF||(LA39_12 >= AMP && LA39_12 <= BAR)||LA39_12==CARAT||LA39_12==COMMA||(LA39_12 >= DATETIME && LA39_12 <= DECIMAL_INTEGER_LITERAL)||(LA39_12 >= DOT && LA39_12 <= DOTDOT)||LA39_12==EQUALS||LA39_12==EXCLAMATION||LA39_12==FLOATING_POINT_LITERAL||(LA39_12 >= FTSPHRASE && LA39_12 <= FTSWORD)||LA39_12==ID||(LA39_12 >= LPAREN && LA39_12 <= LT)||LA39_12==MINUS||LA39_12==NOT||(LA39_12 >= OR && LA39_12 <= PERCENT)||LA39_12==PLUS||LA39_12==QUESTION_MARK||LA39_12==RPAREN||LA39_12==STAR||(LA39_12 >= TILDA && LA39_12 <= URI)) ) {
							alt39=3;
						}
						else if ( (LA39_12==COLON) ) {
							switch ( input.LA(5) ) {
							case LPAREN:
							case LSQUARE:
							case LT:
								{
								alt39=3;
								}
								break;
							case ID:
								{
								int LA39_14 = input.LA(6);
								if ( (synpred13_FTS()) ) {
									alt39=1;
								}
								else if ( (true) ) {
									alt39=3;
								}

								}
								break;
							case TO:
								{
								int LA39_15 = input.LA(6);
								if ( (synpred13_FTS()) ) {
									alt39=1;
								}
								else if ( (true) ) {
									alt39=3;
								}

								}
								break;
							case OR:
								{
								int LA39_16 = input.LA(6);
								if ( (LA39_16==COLON) ) {
									switch ( input.LA(7) ) {
									case LPAREN:
									case LSQUARE:
									case LT:
										{
										alt39=3;
										}
										break;
									case FTSPHRASE:
										{
										int LA39_25 = input.LA(8);
										if ( (synpred13_FTS()) ) {
											alt39=1;
										}
										else if ( (true) ) {
											alt39=3;
										}

										}
										break;
									case COMMA:
									case DOT:
										{
										int LA39_20 = input.LA(8);
										if ( ((LA39_20 >= DATETIME && LA39_20 <= DECIMAL_INTEGER_LITERAL)||LA39_20==FLOATING_POINT_LITERAL||(LA39_20 >= FTSPRE && LA39_20 <= FTSWORD)||LA39_20==ID||LA39_20==NOT||LA39_20==QUESTION_MARK||LA39_20==STAR||LA39_20==TO) ) {
											int LA39_24 = input.LA(9);
											if ( (synpred13_FTS()) ) {
												alt39=1;
											}
											else if ( (true) ) {
												alt39=3;
											}

										}

										else {
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 39, 20, input);
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
										int LA39_26 = input.LA(8);
										if ( (synpred13_FTS()) ) {
											alt39=1;
										}
										else if ( (true) ) {
											alt39=3;
										}

										}
										break;
									case NOT:
									case QUESTION_MARK:
									case STAR:
									case TO:
										{
										int LA39_27 = input.LA(8);
										if ( (synpred13_FTS()) ) {
											alt39=1;
										}
										else if ( (true) ) {
											alt39=3;
										}

										}
										break;
									default:
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 39, 23, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 39, 16, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case AND:
								{
								int LA39_17 = input.LA(6);
								if ( (LA39_17==COLON) ) {
									switch ( input.LA(7) ) {
									case LPAREN:
									case LSQUARE:
									case LT:
										{
										alt39=3;
										}
										break;
									case FTSPHRASE:
										{
										int LA39_25 = input.LA(8);
										if ( (synpred13_FTS()) ) {
											alt39=1;
										}
										else if ( (true) ) {
											alt39=3;
										}

										}
										break;
									case COMMA:
									case DOT:
										{
										int LA39_20 = input.LA(8);
										if ( ((LA39_20 >= DATETIME && LA39_20 <= DECIMAL_INTEGER_LITERAL)||LA39_20==FLOATING_POINT_LITERAL||(LA39_20 >= FTSPRE && LA39_20 <= FTSWORD)||LA39_20==ID||LA39_20==NOT||LA39_20==QUESTION_MARK||LA39_20==STAR||LA39_20==TO) ) {
											int LA39_24 = input.LA(9);
											if ( (synpred13_FTS()) ) {
												alt39=1;
											}
											else if ( (true) ) {
												alt39=3;
											}

										}

										else {
											if (state.backtracking>0) {state.failed=true; return retval;}
											int nvaeMark = input.mark();
											try {
												for (int nvaeConsume = 0; nvaeConsume < 8 - 1; nvaeConsume++) {
													input.consume();
												}
												NoViableAltException nvae =
													new NoViableAltException("", 39, 20, input);
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
										int LA39_26 = input.LA(8);
										if ( (synpred13_FTS()) ) {
											alt39=1;
										}
										else if ( (true) ) {
											alt39=3;
										}

										}
										break;
									case NOT:
									case QUESTION_MARK:
									case STAR:
									case TO:
										{
										int LA39_27 = input.LA(8);
										if ( (synpred13_FTS()) ) {
											alt39=1;
										}
										else if ( (true) ) {
											alt39=3;
										}

										}
										break;
									default:
										if (state.backtracking>0) {state.failed=true; return retval;}
										int nvaeMark = input.mark();
										try {
											for (int nvaeConsume = 0; nvaeConsume < 7 - 1; nvaeConsume++) {
												input.consume();
											}
											NoViableAltException nvae =
												new NoViableAltException("", 39, 23, input);
											throw nvae;
										} finally {
											input.rewind(nvaeMark);
										}
									}
								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 39, 17, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}

								}
								break;
							case NOT:
								{
								int LA39_18 = input.LA(6);
								if ( (synpred13_FTS()) ) {
									alt39=1;
								}
								else if ( (true) ) {
									alt39=3;
								}

								}
								break;
							case FTSPHRASE:
								{
								int LA39_19 = input.LA(6);
								if ( (synpred13_FTS()) ) {
									alt39=1;
								}
								else if ( (true) ) {
									alt39=3;
								}

								}
								break;
							case COMMA:
							case DOT:
								{
								int LA39_20 = input.LA(6);
								if ( ((LA39_20 >= DATETIME && LA39_20 <= DECIMAL_INTEGER_LITERAL)||LA39_20==FLOATING_POINT_LITERAL||(LA39_20 >= FTSPRE && LA39_20 <= FTSWORD)||LA39_20==ID||LA39_20==NOT||LA39_20==QUESTION_MARK||LA39_20==STAR||LA39_20==TO) ) {
									int LA39_24 = input.LA(7);
									if ( (synpred13_FTS()) ) {
										alt39=1;
									}
									else if ( (true) ) {
										alt39=3;
									}

								}

								else {
									if (state.backtracking>0) {state.failed=true; return retval;}
									int nvaeMark = input.mark();
									try {
										for (int nvaeConsume = 0; nvaeConsume < 6 - 1; nvaeConsume++) {
											input.consume();
										}
										NoViableAltException nvae =
											new NoViableAltException("", 39, 20, input);
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
								int LA39_21 = input.LA(6);
								if ( (synpred13_FTS()) ) {
									alt39=1;
								}
								else if ( (true) ) {
									alt39=3;
								}

								}
								break;
							case QUESTION_MARK:
							case STAR:
								{
								int LA39_22 = input.LA(6);
								if ( (synpred13_FTS()) ) {
									alt39=1;
								}
								else if ( (true) ) {
									alt39=3;
								}

								}
								break;
							default:
								if (state.backtracking>0) {state.failed=true; return retval;}
								int nvaeMark = input.mark();
								try {
									for (int nvaeConsume = 0; nvaeConsume < 5 - 1; nvaeConsume++) {
										input.consume();
									}
									NoViableAltException nvae =
										new NoViableAltException("", 39, 13, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}
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
					else if ( (LA39_10==EOF||(LA39_10 >= AMP && LA39_10 <= BAR)||LA39_10==CARAT||LA39_10==COMMA||(LA39_10 >= DATETIME && LA39_10 <= DECIMAL_INTEGER_LITERAL)||LA39_10==DOT||LA39_10==EQUALS||LA39_10==EXCLAMATION||LA39_10==FLOATING_POINT_LITERAL||(LA39_10 >= FTSPHRASE && LA39_10 <= FTSWORD)||(LA39_10 >= LPAREN && LA39_10 <= LT)||LA39_10==MINUS||LA39_10==NOT||(LA39_10 >= OR && LA39_10 <= PERCENT)||LA39_10==PLUS||LA39_10==QUESTION_MARK||LA39_10==RPAREN||LA39_10==STAR||(LA39_10 >= TILDA && LA39_10 <= URI)) ) {
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
				else if ( (LA39_2==COLON) && (synpred13_FTS())) {
					alt39=1;
				}
				else if ( (LA39_2==EOF||(LA39_2 >= AMP && LA39_2 <= BAR)||LA39_2==CARAT||LA39_2==COMMA||(LA39_2 >= DATETIME && LA39_2 <= DECIMAL_INTEGER_LITERAL)||LA39_2==EQUALS||LA39_2==EXCLAMATION||LA39_2==FLOATING_POINT_LITERAL||(LA39_2 >= FTSPHRASE && LA39_2 <= FTSWORD)||LA39_2==ID||(LA39_2 >= LPAREN && LA39_2 <= LT)||LA39_2==MINUS||LA39_2==NOT||(LA39_2 >= OR && LA39_2 <= PERCENT)||LA39_2==PLUS||LA39_2==QUESTION_MARK||LA39_2==RPAREN||LA39_2==STAR||(LA39_2 >= TILDA && LA39_2 <= URI)) ) {
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
				if ( (LA39_3==COLON) && (synpred13_FTS())) {
					alt39=1;
				}
				else if ( (LA39_3==EOF||(LA39_3 >= AMP && LA39_3 <= BAR)||LA39_3==CARAT||LA39_3==COMMA||(LA39_3 >= DATETIME && LA39_3 <= DECIMAL_INTEGER_LITERAL)||LA39_3==DOT||LA39_3==EQUALS||LA39_3==EXCLAMATION||LA39_3==FLOATING_POINT_LITERAL||(LA39_3 >= FTSPHRASE && LA39_3 <= FTSWORD)||LA39_3==ID||(LA39_3 >= LPAREN && LA39_3 <= LT)||LA39_3==MINUS||LA39_3==NOT||(LA39_3 >= OR && LA39_3 <= PERCENT)||LA39_3==PLUS||LA39_3==QUESTION_MARK||LA39_3==RPAREN||LA39_3==STAR||(LA39_3 >= TILDA && LA39_3 <= URI)) ) {
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
			else if ( (LA39_0==OR) && (synpred13_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==AND) && (synpred13_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==NOT) ) {
				int LA39_6 = input.LA(2);
				if ( (LA39_6==COLON) && (synpred13_FTS())) {
					alt39=1;
				}
				else if ( (LA39_6==EOF||(LA39_6 >= AMP && LA39_6 <= BAR)||LA39_6==CARAT||LA39_6==COMMA||(LA39_6 >= DATETIME && LA39_6 <= DECIMAL_INTEGER_LITERAL)||LA39_6==DOT||LA39_6==EQUALS||LA39_6==EXCLAMATION||LA39_6==FLOATING_POINT_LITERAL||(LA39_6 >= FTSPHRASE && LA39_6 <= FTSWORD)||LA39_6==ID||(LA39_6 >= LPAREN && LA39_6 <= LT)||LA39_6==MINUS||LA39_6==NOT||(LA39_6 >= OR && LA39_6 <= PERCENT)||LA39_6==PLUS||LA39_6==QUESTION_MARK||LA39_6==RPAREN||LA39_6==STAR||(LA39_6 >= TILDA && LA39_6 <= URI)) ) {
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
			else if ( (LA39_0==URI) && (synpred13_FTS())) {
				alt39=1;
			}
			else if ( (LA39_0==FTSPHRASE) ) {
				alt39=2;
			}
			else if ( (LA39_0==COMMA||(LA39_0 >= DATETIME && LA39_0 <= DECIMAL_INTEGER_LITERAL)||LA39_0==DOT||LA39_0==FLOATING_POINT_LITERAL||(LA39_0 >= FTSPRE && LA39_0 <= FTSWORD)||LA39_0==QUESTION_MARK||LA39_0==STAR) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:555:9: ( fieldReference COLON )=> fieldReference COLON ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					{
					pushFollow(FOLLOW_fieldReference_in_ftsTokenisedTermOrPhrase3342);
					fieldReference84=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference84.getTree());
					COLON85=(Token)match(input,COLON,FOLLOW_COLON_in_ftsTokenisedTermOrPhrase3344); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON85);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:556:9: ( FTSPHRASE ( ( slop )=> slop )? -> ^( PHRASE FTSPHRASE fieldReference ( slop )? ) | ftsWord ( ( fuzzy )=> fuzzy )? -> ^( TERM ftsWord fieldReference ( fuzzy )? ) )
					int alt36=2;
					int LA36_0 = input.LA(1);
					if ( (LA36_0==FTSPHRASE) ) {
						alt36=1;
					}
					else if ( (LA36_0==COMMA||(LA36_0 >= DATETIME && LA36_0 <= DECIMAL_INTEGER_LITERAL)||LA36_0==DOT||LA36_0==FLOATING_POINT_LITERAL||(LA36_0 >= FTSPRE && LA36_0 <= FTSWORD)||LA36_0==ID||LA36_0==NOT||LA36_0==QUESTION_MARK||LA36_0==STAR||LA36_0==TO) ) {
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
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:17: FTSPHRASE ( ( slop )=> slop )?
							{
							FTSPHRASE86=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3372); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE86);

							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:27: ( ( slop )=> slop )?
							int alt34=2;
							int LA34_0 = input.LA(1);
							if ( (LA34_0==TILDA) ) {
								int LA34_1 = input.LA(2);
								if ( (LA34_1==DECIMAL_INTEGER_LITERAL) ) {
									int LA34_3 = input.LA(3);
									if ( (synpred14_FTS()) ) {
										alt34=1;
									}
								}
							}
							switch (alt34) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:28: ( slop )=> slop
									{
									pushFollow(FOLLOW_slop_in_ftsTokenisedTermOrPhrase3380);
									slop87=slop();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_slop.add(slop87.getTree());
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
							// 558:17: -> ^( PHRASE FTSPHRASE fieldReference ( slop )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:558:20: ^( PHRASE FTSPHRASE fieldReference ( slop )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
								adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:558:54: ( slop )?
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
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:17: ftsWord ( ( fuzzy )=> fuzzy )?
							{
							pushFollow(FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3447);
							ftsWord88=ftsWord();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord88.getTree());
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:25: ( ( fuzzy )=> fuzzy )?
							int alt35=2;
							int LA35_0 = input.LA(1);
							if ( (LA35_0==TILDA) ) {
								int LA35_1 = input.LA(2);
								if ( (LA35_1==DECIMAL_INTEGER_LITERAL||LA35_1==FLOATING_POINT_LITERAL) ) {
									int LA35_3 = input.LA(3);
									if ( (synpred15_FTS()) ) {
										alt35=1;
									}
								}
							}
							switch (alt35) {
								case 1 :
									// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:26: ( fuzzy )=> fuzzy
									{
									pushFollow(FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3456);
									fuzzy89=fuzzy();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy89.getTree());
									}
									break;

							}

							// AST REWRITE
							// elements: fieldReference, fuzzy, ftsWord
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							if ( state.backtracking==0 ) {
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (Object)adaptor.nil();
							// 561:17: -> ^( TERM ftsWord fieldReference ( fuzzy )? )
							{
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:561:20: ^( TERM ftsWord fieldReference ( fuzzy )? )
								{
								Object root_1 = (Object)adaptor.nil();
								root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
								adaptor.addChild(root_1, stream_ftsWord.nextTree());
								adaptor.addChild(root_1, stream_fieldReference.nextTree());
								// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:561:50: ( fuzzy )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:564:9: FTSPHRASE ( ( slop )=> slop )?
					{
					FTSPHRASE90=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3517); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_FTSPHRASE.add(FTSPHRASE90);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:564:19: ( ( slop )=> slop )?
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( (LA37_0==TILDA) ) {
						int LA37_1 = input.LA(2);
						if ( (LA37_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA37_3 = input.LA(3);
							if ( (synpred16_FTS()) ) {
								alt37=1;
							}
						}
					}
					switch (alt37) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:564:20: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsTokenisedTermOrPhrase3525);
							slop91=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop91.getTree());
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
					// 565:17: -> ^( PHRASE FTSPHRASE ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:565:20: ^( PHRASE FTSPHRASE ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PHRASE, "PHRASE"), root_1);
						adaptor.addChild(root_1, stream_FTSPHRASE.nextNode());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:565:39: ( slop )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:9: ftsWord ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3575);
					ftsWord92=ftsWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsWord.add(ftsWord92.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:17: ( ( fuzzy )=> fuzzy )?
					int alt38=2;
					int LA38_0 = input.LA(1);
					if ( (LA38_0==TILDA) ) {
						int LA38_1 = input.LA(2);
						if ( (LA38_1==DECIMAL_INTEGER_LITERAL||LA38_1==FLOATING_POINT_LITERAL) ) {
							int LA38_3 = input.LA(3);
							if ( (synpred17_FTS()) ) {
								alt38=1;
							}
						}
					}
					switch (alt38) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:18: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3584);
							fuzzy93=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy93.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsWord, fuzzy
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 568:17: -> ^( TERM ftsWord ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:568:20: ^( TERM ftsWord ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(TERM, "TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsWord.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:568:35: ( fuzzy )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:573:1: cmisTerm : ftsWord -> ftsWord ;
	public final FTSParser.cmisTerm_return cmisTerm() throws RecognitionException {
		FTSParser.cmisTerm_return retval = new FTSParser.cmisTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsWord94 =null;

		RewriteRuleSubtreeStream stream_ftsWord=new RewriteRuleSubtreeStream(adaptor,"rule ftsWord");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:574:9: ( ftsWord -> ftsWord )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:575:9: ftsWord
			{
			pushFollow(FOLLOW_ftsWord_in_cmisTerm3657);
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
			// 576:17: -> ftsWord
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:580:1: cmisPhrase : FTSPHRASE -> FTSPHRASE ;
	public final FTSParser.cmisPhrase_return cmisPhrase() throws RecognitionException {
		FTSParser.cmisPhrase_return retval = new FTSParser.cmisPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token FTSPHRASE95=null;

		Object FTSPHRASE95_tree=null;
		RewriteRuleTokenStream stream_FTSPHRASE=new RewriteRuleTokenStream(adaptor,"token FTSPHRASE");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:581:9: ( FTSPHRASE -> FTSPHRASE )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:582:9: FTSPHRASE
			{
			FTSPHRASE95=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_cmisPhrase3711); if (state.failed) return retval; 
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
			// 583:17: -> FTSPHRASE
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:587:1: ftsRange : ( fieldReference COLON )? ftsFieldGroupRange -> ftsFieldGroupRange ( fieldReference )? ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:588:9: ( ( fieldReference COLON )? ftsFieldGroupRange -> ftsFieldGroupRange ( fieldReference )? )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:589:9: ( fieldReference COLON )? ftsFieldGroupRange
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:589:9: ( fieldReference COLON )?
			int alt40=2;
			int LA40_0 = input.LA(1);
			if ( ((LA40_0 >= AND && LA40_0 <= AT)||LA40_0==NOT||LA40_0==OR||(LA40_0 >= TO && LA40_0 <= URI)) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:589:10: fieldReference COLON
					{
					pushFollow(FOLLOW_fieldReference_in_ftsRange3766);
					fieldReference96=fieldReference();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference96.getTree());
					COLON97=(Token)match(input,COLON,FOLLOW_COLON_in_ftsRange3768); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_COLON.add(COLON97);

					}
					break;

			}

			pushFollow(FOLLOW_ftsFieldGroupRange_in_ftsRange3772);
			ftsFieldGroupRange98=ftsFieldGroupRange();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupRange.add(ftsFieldGroupRange98.getTree());
			// AST REWRITE
			// elements: ftsFieldGroupRange, fieldReference
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 590:17: -> ftsFieldGroupRange ( fieldReference )?
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupRange.nextTree());
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:590:39: ( fieldReference )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:593:1: ftsFieldGroup : fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:594:9: ( fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:595:9: fieldReference COLON LPAREN ftsFieldGroupDisjunction RPAREN
			{
			pushFollow(FOLLOW_fieldReference_in_ftsFieldGroup3828);
			fieldReference99=fieldReference();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_fieldReference.add(fieldReference99.getTree());
			COLON100=(Token)match(input,COLON,FOLLOW_COLON_in_ftsFieldGroup3830); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_COLON.add(COLON100);

			LPAREN101=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsFieldGroup3832); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN101);

			pushFollow(FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroup3834);
			ftsFieldGroupDisjunction102=ftsFieldGroupDisjunction();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupDisjunction.add(ftsFieldGroupDisjunction102.getTree());
			RPAREN103=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsFieldGroup3836); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN103);

			// AST REWRITE
			// elements: ftsFieldGroupDisjunction, fieldReference
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 596:17: -> ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:597:25: ^( FIELD_GROUP fieldReference ftsFieldGroupDisjunction )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:600:1: ftsFieldGroupDisjunction : ({...}? ftsFieldGroupExplicitDisjunction |{...}? ftsFieldGroupImplicitDisjunction );
	public final FTSParser.ftsFieldGroupDisjunction_return ftsFieldGroupDisjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupDisjunction_return retval = new FTSParser.ftsFieldGroupDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsFieldGroupExplicitDisjunction104 =null;
		ParserRuleReturnScope ftsFieldGroupImplicitDisjunction105 =null;


		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:601:9: ({...}? ftsFieldGroupExplicitDisjunction |{...}? ftsFieldGroupImplicitDisjunction )
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
			case COMMA:
			case DOT:
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
			case QUESTION_MARK:
			case STAR:
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
			case EQUALS:
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
			case FTSPHRASE:
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
			case TILDA:
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
			case TO:
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:602:9: {...}? ftsFieldGroupExplicitDisjunction
					{
					root_0 = (Object)adaptor.nil();


					if ( !((defaultFieldConjunction() == true)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "ftsFieldGroupDisjunction", "defaultFieldConjunction() == true");
					}
					pushFollow(FOLLOW_ftsFieldGroupExplicitDisjunction_in_ftsFieldGroupDisjunction3921);
					ftsFieldGroupExplicitDisjunction104=ftsFieldGroupExplicitDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsFieldGroupExplicitDisjunction104.getTree());

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:603:11: {...}? ftsFieldGroupImplicitDisjunction
					{
					root_0 = (Object)adaptor.nil();


					if ( !((defaultFieldConjunction() == false)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "ftsFieldGroupDisjunction", "defaultFieldConjunction() == false");
					}
					pushFollow(FOLLOW_ftsFieldGroupImplicitDisjunction_in_ftsFieldGroupDisjunction3935);
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:606:1: ftsFieldGroupExplicitDisjunction : ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )* -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:607:9: ( ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )* -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:608:9: ftsFieldGroupImplicitConjunction ( or ftsFieldGroupImplicitConjunction )*
			{
			pushFollow(FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3968);
			ftsFieldGroupImplicitConjunction106=ftsFieldGroupImplicitConjunction();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupImplicitConjunction.add(ftsFieldGroupImplicitConjunction106.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:608:42: ( or ftsFieldGroupImplicitConjunction )*
			loop42:
			while (true) {
				int alt42=2;
				int LA42_0 = input.LA(1);
				if ( (LA42_0==BAR||LA42_0==OR) ) {
					alt42=1;
				}

				switch (alt42) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:608:43: or ftsFieldGroupImplicitConjunction
					{
					pushFollow(FOLLOW_or_in_ftsFieldGroupExplicitDisjunction3971);
					or107=or();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_or.add(or107.getTree());
					pushFollow(FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3973);
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
			// 609:17: -> ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:610:25: ^( FIELD_DISJUNCTION ( ftsFieldGroupImplicitConjunction )+ )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:613:1: ftsFieldGroupImplicitDisjunction : ( ( or )? ftsFieldGroupExplicitConjunction )+ -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ ) ;
	public final FTSParser.ftsFieldGroupImplicitDisjunction_return ftsFieldGroupImplicitDisjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupImplicitDisjunction_return retval = new FTSParser.ftsFieldGroupImplicitDisjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope or109 =null;
		ParserRuleReturnScope ftsFieldGroupExplicitConjunction110 =null;

		RewriteRuleSubtreeStream stream_or=new RewriteRuleSubtreeStream(adaptor,"rule or");
		RewriteRuleSubtreeStream stream_ftsFieldGroupExplicitConjunction=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupExplicitConjunction");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:614:9: ( ( ( or )? ftsFieldGroupExplicitConjunction )+ -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:615:9: ( ( or )? ftsFieldGroupExplicitConjunction )+
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:615:9: ( ( or )? ftsFieldGroupExplicitConjunction )+
			int cnt44=0;
			loop44:
			while (true) {
				int alt44=2;
				int LA44_0 = input.LA(1);
				if ( (LA44_0==BAR||LA44_0==COMMA||(LA44_0 >= DATETIME && LA44_0 <= DECIMAL_INTEGER_LITERAL)||LA44_0==DOT||LA44_0==EQUALS||LA44_0==EXCLAMATION||LA44_0==FLOATING_POINT_LITERAL||(LA44_0 >= FTSPHRASE && LA44_0 <= FTSWORD)||LA44_0==ID||(LA44_0 >= LPAREN && LA44_0 <= LT)||LA44_0==MINUS||LA44_0==NOT||LA44_0==OR||LA44_0==PLUS||LA44_0==QUESTION_MARK||LA44_0==STAR||(LA44_0 >= TILDA && LA44_0 <= TO)) ) {
					alt44=1;
				}

				switch (alt44) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:615:10: ( or )? ftsFieldGroupExplicitConjunction
					{
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:615:10: ( or )?
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
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:615:10: or
							{
							pushFollow(FOLLOW_or_in_ftsFieldGroupImplicitDisjunction4058);
							or109=or();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_or.add(or109.getTree());
							}
							break;

					}

					pushFollow(FOLLOW_ftsFieldGroupExplicitConjunction_in_ftsFieldGroupImplicitDisjunction4061);
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
			// 616:17: -> ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:617:25: ^( FIELD_DISJUNCTION ( ftsFieldGroupExplicitConjunction )+ )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:624:1: ftsFieldGroupExplicitConjunction : ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )* -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:625:9: ( ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )* -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:626:9: ftsFieldGroupPrefixed ( and ftsFieldGroupPrefixed )*
			{
			pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4148);
			ftsFieldGroupPrefixed111=ftsFieldGroupPrefixed();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupPrefixed.add(ftsFieldGroupPrefixed111.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:626:31: ( and ftsFieldGroupPrefixed )*
			loop45:
			while (true) {
				int alt45=2;
				int LA45_0 = input.LA(1);
				if ( ((LA45_0 >= AMP && LA45_0 <= AND)) ) {
					alt45=1;
				}

				switch (alt45) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:626:32: and ftsFieldGroupPrefixed
					{
					pushFollow(FOLLOW_and_in_ftsFieldGroupExplicitConjunction4151);
					and112=and();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_and.add(and112.getTree());
					pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4153);
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
			// 627:17: -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:628:25: ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:631:1: ftsFieldGroupImplicitConjunction : ( ( and )? ftsFieldGroupPrefixed )+ -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) ;
	public final FTSParser.ftsFieldGroupImplicitConjunction_return ftsFieldGroupImplicitConjunction() throws RecognitionException {
		FTSParser.ftsFieldGroupImplicitConjunction_return retval = new FTSParser.ftsFieldGroupImplicitConjunction_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope and114 =null;
		ParserRuleReturnScope ftsFieldGroupPrefixed115 =null;

		RewriteRuleSubtreeStream stream_ftsFieldGroupPrefixed=new RewriteRuleSubtreeStream(adaptor,"rule ftsFieldGroupPrefixed");
		RewriteRuleSubtreeStream stream_and=new RewriteRuleSubtreeStream(adaptor,"rule and");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:632:9: ( ( ( and )? ftsFieldGroupPrefixed )+ -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:633:9: ( ( and )? ftsFieldGroupPrefixed )+
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:633:9: ( ( and )? ftsFieldGroupPrefixed )+
			int cnt47=0;
			loop47:
			while (true) {
				int alt47=2;
				int LA47_0 = input.LA(1);
				if ( (LA47_0==BAR) ) {
					int LA47_2 = input.LA(2);
					if ( (LA47_2==COMMA||(LA47_2 >= DATETIME && LA47_2 <= DECIMAL_INTEGER_LITERAL)||LA47_2==DOT||LA47_2==EQUALS||LA47_2==FLOATING_POINT_LITERAL||(LA47_2 >= FTSPHRASE && LA47_2 <= FTSWORD)||LA47_2==ID||(LA47_2 >= LPAREN && LA47_2 <= LT)||LA47_2==NOT||LA47_2==QUESTION_MARK||LA47_2==STAR||(LA47_2 >= TILDA && LA47_2 <= TO)) ) {
						alt47=1;
					}

				}
				else if ( ((LA47_0 >= AMP && LA47_0 <= AND)||LA47_0==COMMA||(LA47_0 >= DATETIME && LA47_0 <= DECIMAL_INTEGER_LITERAL)||LA47_0==DOT||LA47_0==EQUALS||LA47_0==EXCLAMATION||LA47_0==FLOATING_POINT_LITERAL||(LA47_0 >= FTSPHRASE && LA47_0 <= FTSWORD)||LA47_0==ID||(LA47_0 >= LPAREN && LA47_0 <= LT)||LA47_0==MINUS||LA47_0==NOT||LA47_0==PLUS||LA47_0==QUESTION_MARK||LA47_0==STAR||(LA47_0 >= TILDA && LA47_0 <= TO)) ) {
					alt47=1;
				}

				switch (alt47) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:633:10: ( and )? ftsFieldGroupPrefixed
					{
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:633:10: ( and )?
					int alt46=2;
					int LA46_0 = input.LA(1);
					if ( ((LA46_0 >= AMP && LA46_0 <= AND)) ) {
						alt46=1;
					}
					switch (alt46) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:633:10: and
							{
							pushFollow(FOLLOW_and_in_ftsFieldGroupImplicitConjunction4238);
							and114=and();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_and.add(and114.getTree());
							}
							break;

					}

					pushFollow(FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupImplicitConjunction4241);
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
			// 634:17: -> ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:635:25: ^( FIELD_CONJUNCTION ( ftsFieldGroupPrefixed )+ )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:638:1: ftsFieldGroupPrefixed : ( ( not )=> not ftsFieldGroupTest ( boost )? -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? ) | ftsFieldGroupTest ( boost )? -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? ) | PLUS ftsFieldGroupTest ( boost )? -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? ) | BAR ftsFieldGroupTest ( boost )? -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? ) | MINUS ftsFieldGroupTest ( boost )? -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? ) );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:639:9: ( ( not )=> not ftsFieldGroupTest ( boost )? -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? ) | ftsFieldGroupTest ( boost )? -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? ) | PLUS ftsFieldGroupTest ( boost )? -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? ) | BAR ftsFieldGroupTest ( boost )? -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? ) | MINUS ftsFieldGroupTest ( boost )? -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? ) )
			int alt53=5;
			int LA53_0 = input.LA(1);
			if ( (LA53_0==NOT) ) {
				int LA53_1 = input.LA(2);
				if ( (synpred18_FTS()) ) {
					alt53=1;
				}
				else if ( (true) ) {
					alt53=2;
				}

			}
			else if ( (LA53_0==EXCLAMATION) && (synpred18_FTS())) {
				alt53=1;
			}
			else if ( (LA53_0==COMMA||(LA53_0 >= DATETIME && LA53_0 <= DECIMAL_INTEGER_LITERAL)||LA53_0==DOT||LA53_0==EQUALS||LA53_0==FLOATING_POINT_LITERAL||(LA53_0 >= FTSPHRASE && LA53_0 <= FTSWORD)||LA53_0==ID||(LA53_0 >= LPAREN && LA53_0 <= LT)||LA53_0==QUESTION_MARK||LA53_0==STAR||(LA53_0 >= TILDA && LA53_0 <= TO)) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:9: ( not )=> not ftsFieldGroupTest ( boost )?
					{
					pushFollow(FOLLOW_not_in_ftsFieldGroupPrefixed4331);
					not116=not();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_not.add(not116.getTree());
					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4333);
					ftsFieldGroupTest117=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest117.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:40: ( boost )?
					int alt48=2;
					int LA48_0 = input.LA(1);
					if ( (LA48_0==CARAT) ) {
						alt48=1;
					}
					switch (alt48) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:40: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4335);
							boost118=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost118.getTree());
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
					// 641:17: -> ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:642:25: ^( FIELD_NEGATION ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_NEGATION, "FIELD_NEGATION"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:642:60: ( boost )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:643:11: ftsFieldGroupTest ( boost )?
					{
					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4399);
					ftsFieldGroupTest119=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest119.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:643:29: ( boost )?
					int alt49=2;
					int LA49_0 = input.LA(1);
					if ( (LA49_0==CARAT) ) {
						alt49=1;
					}
					switch (alt49) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:643:29: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4401);
							boost120=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost120.getTree());
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
					// 644:17: -> ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:645:25: ^( FIELD_DEFAULT ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_DEFAULT, "FIELD_DEFAULT"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:645:59: ( boost )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:646:11: PLUS ftsFieldGroupTest ( boost )?
					{
					PLUS121=(Token)match(input,PLUS,FOLLOW_PLUS_in_ftsFieldGroupPrefixed4465); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_PLUS.add(PLUS121);

					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4467);
					ftsFieldGroupTest122=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest122.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:646:34: ( boost )?
					int alt50=2;
					int LA50_0 = input.LA(1);
					if ( (LA50_0==CARAT) ) {
						alt50=1;
					}
					switch (alt50) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:646:34: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4469);
							boost123=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost123.getTree());
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
					// 647:17: -> ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:648:25: ^( FIELD_MANDATORY ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_MANDATORY, "FIELD_MANDATORY"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:648:61: ( boost )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:649:11: BAR ftsFieldGroupTest ( boost )?
					{
					BAR124=(Token)match(input,BAR,FOLLOW_BAR_in_ftsFieldGroupPrefixed4533); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_BAR.add(BAR124);

					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4535);
					ftsFieldGroupTest125=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest125.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:649:33: ( boost )?
					int alt51=2;
					int LA51_0 = input.LA(1);
					if ( (LA51_0==CARAT) ) {
						alt51=1;
					}
					switch (alt51) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:649:33: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4537);
							boost126=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost126.getTree());
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
					// 650:17: -> ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:25: ^( FIELD_OPTIONAL ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_OPTIONAL, "FIELD_OPTIONAL"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:651:60: ( boost )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:652:11: MINUS ftsFieldGroupTest ( boost )?
					{
					MINUS127=(Token)match(input,MINUS,FOLLOW_MINUS_in_ftsFieldGroupPrefixed4601); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_MINUS.add(MINUS127);

					pushFollow(FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4603);
					ftsFieldGroupTest128=ftsFieldGroupTest();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTest.add(ftsFieldGroupTest128.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:652:35: ( boost )?
					int alt52=2;
					int LA52_0 = input.LA(1);
					if ( (LA52_0==CARAT) ) {
						alt52=1;
					}
					switch (alt52) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:652:35: boost
							{
							pushFollow(FOLLOW_boost_in_ftsFieldGroupPrefixed4605);
							boost129=boost();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_boost.add(boost129.getTree());
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
					// 653:17: -> ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:25: ^( FIELD_EXCLUDE ftsFieldGroupTest ( boost )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_EXCLUDE, "FIELD_EXCLUDE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTest.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:654:59: ( boost )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:657:1: ftsFieldGroupTest : ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( FG_PROXIMITY ftsFieldGroupProximity ) | ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? ) | ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? ) | ftsFieldGroupPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? ) | ftsFieldGroupExactPhrase ( ( slop )=> slop )? -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? ) | ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? ) | ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )? -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? ) | ftsFieldGroupRange -> ^( FG_RANGE ftsFieldGroupRange ) | LPAREN ftsFieldGroupDisjunction RPAREN -> ftsFieldGroupDisjunction );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:658:9: ( ( ftsFieldGroupProximity )=> ftsFieldGroupProximity -> ^( FG_PROXIMITY ftsFieldGroupProximity ) | ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? ) | ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )? -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? ) | ftsFieldGroupPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? ) | ftsFieldGroupExactPhrase ( ( slop )=> slop )? -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? ) | ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )? -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? ) | ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )? -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? ) | ftsFieldGroupRange -> ^( FG_RANGE ftsFieldGroupRange ) | LPAREN ftsFieldGroupDisjunction RPAREN -> ftsFieldGroupDisjunction )
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
						int LA60_15 = input.LA(4);
						if ( (LA60_15==DECIMAL_INTEGER_LITERAL) ) {
							int LA60_19 = input.LA(5);
							if ( (LA60_19==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA60_22 = input.LA(7);
									if ( (synpred19_FTS()) ) {
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
									int LA60_23 = input.LA(7);
									if ( (synpred19_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								case TO:
									{
									int LA60_24 = input.LA(7);
									if ( (synpred19_FTS()) ) {
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
											new NoViableAltException("", 60, 21, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA60_19 >= AMP && LA60_19 <= AND)||LA60_19==BAR||LA60_19==CARAT||LA60_19==COMMA||(LA60_19 >= DATETIME && LA60_19 <= DECIMAL_INTEGER_LITERAL)||(LA60_19 >= DOT && LA60_19 <= DOTDOT)||LA60_19==EQUALS||LA60_19==EXCLAMATION||LA60_19==FLOATING_POINT_LITERAL||(LA60_19 >= FTSPHRASE && LA60_19 <= FTSWORD)||LA60_19==ID||(LA60_19 >= LPAREN && LA60_19 <= LT)||LA60_19==MINUS||LA60_19==NOT||LA60_19==OR||LA60_19==PLUS||LA60_19==QUESTION_MARK||LA60_19==STAR||(LA60_19 >= TILDA && LA60_19 <= TO)) ) {
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
										new NoViableAltException("", 60, 19, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA60_15==RPAREN) && (synpred19_FTS())) {
							alt60=1;
						}
						else if ( ((LA60_15 >= AMP && LA60_15 <= AND)||LA60_15==BAR||LA60_15==COMMA||LA60_15==DATETIME||LA60_15==DOT||LA60_15==EQUALS||LA60_15==EXCLAMATION||LA60_15==FLOATING_POINT_LITERAL||(LA60_15 >= FTSPHRASE && LA60_15 <= FTSWORD)||LA60_15==ID||(LA60_15 >= LPAREN && LA60_15 <= LT)||LA60_15==MINUS||LA60_15==NOT||LA60_15==OR||LA60_15==PLUS||LA60_15==QUESTION_MARK||LA60_15==STAR||(LA60_15 >= TILDA && LA60_15 <= TO)) ) {
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
									new NoViableAltException("", 60, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA60_16 = input.LA(4);
						if ( (synpred19_FTS()) ) {
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
						int LA60_17 = input.LA(4);
						if ( (synpred19_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					case TO:
						{
						int LA60_18 = input.LA(4);
						if ( (synpred19_FTS()) ) {
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
								new NoViableAltException("", 60, 9, input);
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
			case COMMA:
			case DOT:
			case QUESTION_MARK:
			case STAR:
				{
				alt60=2;
				}
				break;
			case EQUALS:
				{
				int LA60_3 = input.LA(2);
				if ( (LA60_3==COMMA||(LA60_3 >= DATETIME && LA60_3 <= DECIMAL_INTEGER_LITERAL)||LA60_3==DOT||LA60_3==FLOATING_POINT_LITERAL||(LA60_3 >= FTSPRE && LA60_3 <= FTSWORD)||LA60_3==ID||LA60_3==NOT||LA60_3==QUESTION_MARK||LA60_3==STAR||LA60_3==TO) ) {
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
				if ( ((LA60_4 >= AMP && LA60_4 <= AND)||LA60_4==BAR||LA60_4==CARAT||LA60_4==COMMA||(LA60_4 >= DATETIME && LA60_4 <= DECIMAL_INTEGER_LITERAL)||LA60_4==DOT||LA60_4==EQUALS||LA60_4==EXCLAMATION||LA60_4==FLOATING_POINT_LITERAL||(LA60_4 >= FTSPHRASE && LA60_4 <= FTSWORD)||LA60_4==ID||(LA60_4 >= LPAREN && LA60_4 <= LT)||LA60_4==MINUS||LA60_4==NOT||LA60_4==OR||LA60_4==PLUS||LA60_4==QUESTION_MARK||LA60_4==RPAREN||LA60_4==STAR||(LA60_4 >= TILDA && LA60_4 <= TO)) ) {
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
				else if ( (LA60_5==COMMA||(LA60_5 >= DATETIME && LA60_5 <= DECIMAL_INTEGER_LITERAL)||LA60_5==DOT||LA60_5==FLOATING_POINT_LITERAL||(LA60_5 >= FTSPRE && LA60_5 <= FTSWORD)||LA60_5==ID||LA60_5==NOT||LA60_5==QUESTION_MARK||LA60_5==STAR||LA60_5==TO) ) {
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
						int LA60_15 = input.LA(4);
						if ( (LA60_15==DECIMAL_INTEGER_LITERAL) ) {
							int LA60_19 = input.LA(5);
							if ( (LA60_19==RPAREN) ) {
								switch ( input.LA(6) ) {
								case NOT:
									{
									int LA60_22 = input.LA(7);
									if ( (synpred19_FTS()) ) {
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
									int LA60_23 = input.LA(7);
									if ( (synpred19_FTS()) ) {
										alt60=1;
									}
									else if ( (true) ) {
										alt60=2;
									}

									}
									break;
								case TO:
									{
									int LA60_24 = input.LA(7);
									if ( (synpred19_FTS()) ) {
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
											new NoViableAltException("", 60, 21, input);
										throw nvae;
									} finally {
										input.rewind(nvaeMark);
									}
								}
							}
							else if ( ((LA60_19 >= AMP && LA60_19 <= AND)||LA60_19==BAR||LA60_19==CARAT||LA60_19==COMMA||(LA60_19 >= DATETIME && LA60_19 <= DECIMAL_INTEGER_LITERAL)||(LA60_19 >= DOT && LA60_19 <= DOTDOT)||LA60_19==EQUALS||LA60_19==EXCLAMATION||LA60_19==FLOATING_POINT_LITERAL||(LA60_19 >= FTSPHRASE && LA60_19 <= FTSWORD)||LA60_19==ID||(LA60_19 >= LPAREN && LA60_19 <= LT)||LA60_19==MINUS||LA60_19==NOT||LA60_19==OR||LA60_19==PLUS||LA60_19==QUESTION_MARK||LA60_19==STAR||(LA60_19 >= TILDA && LA60_19 <= TO)) ) {
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
										new NoViableAltException("", 60, 19, input);
									throw nvae;
								} finally {
									input.rewind(nvaeMark);
								}
							}

						}
						else if ( (LA60_15==RPAREN) && (synpred19_FTS())) {
							alt60=1;
						}
						else if ( ((LA60_15 >= AMP && LA60_15 <= AND)||LA60_15==BAR||LA60_15==COMMA||LA60_15==DATETIME||LA60_15==DOT||LA60_15==EQUALS||LA60_15==EXCLAMATION||LA60_15==FLOATING_POINT_LITERAL||(LA60_15 >= FTSPHRASE && LA60_15 <= FTSWORD)||LA60_15==ID||(LA60_15 >= LPAREN && LA60_15 <= LT)||LA60_15==MINUS||LA60_15==NOT||LA60_15==OR||LA60_15==PLUS||LA60_15==QUESTION_MARK||LA60_15==STAR||(LA60_15 >= TILDA && LA60_15 <= TO)) ) {
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
									new NoViableAltException("", 60, 15, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case NOT:
						{
						int LA60_16 = input.LA(4);
						if ( (synpred19_FTS()) ) {
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
						int LA60_17 = input.LA(4);
						if ( (synpred19_FTS()) ) {
							alt60=1;
						}
						else if ( (true) ) {
							alt60=2;
						}

						}
						break;
					case TO:
						{
						int LA60_18 = input.LA(4);
						if ( (synpred19_FTS()) ) {
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
								new NoViableAltException("", 60, 9, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( ((LA60_6 >= AMP && LA60_6 <= AND)||LA60_6==BAR||LA60_6==CARAT||LA60_6==COMMA||(LA60_6 >= DATETIME && LA60_6 <= DECIMAL_INTEGER_LITERAL)||LA60_6==DOT||LA60_6==EQUALS||LA60_6==EXCLAMATION||LA60_6==FLOATING_POINT_LITERAL||(LA60_6 >= FTSPHRASE && LA60_6 <= FTSWORD)||LA60_6==ID||(LA60_6 >= LPAREN && LA60_6 <= LT)||LA60_6==MINUS||LA60_6==NOT||LA60_6==OR||LA60_6==PLUS||LA60_6==QUESTION_MARK||LA60_6==RPAREN||(LA60_6 >= TILDA && LA60_6 <= TO)) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:659:9: ( ftsFieldGroupProximity )=> ftsFieldGroupProximity
					{
					pushFollow(FOLLOW_ftsFieldGroupProximity_in_ftsFieldGroupTest4696);
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
					// 660:17: -> ^( FG_PROXIMITY ftsFieldGroupProximity )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:661:25: ^( FG_PROXIMITY ftsFieldGroupProximity )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:662:11: ftsFieldGroupTerm ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupTest4756);
					ftsFieldGroupTerm131=ftsFieldGroupTerm();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTerm.add(ftsFieldGroupTerm131.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:662:29: ( ( fuzzy )=> fuzzy )?
					int alt54=2;
					int LA54_0 = input.LA(1);
					if ( (LA54_0==TILDA) ) {
						int LA54_1 = input.LA(2);
						if ( (LA54_1==DECIMAL_INTEGER_LITERAL||LA54_1==FLOATING_POINT_LITERAL) ) {
							int LA54_3 = input.LA(3);
							if ( (synpred20_FTS()) ) {
								alt54=1;
							}
						}
					}
					switch (alt54) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:662:31: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest4766);
							fuzzy132=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy132.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: fuzzy, ftsFieldGroupTerm
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 663:17: -> ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:664:25: ^( FG_TERM ftsFieldGroupTerm ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_TERM, "FG_TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTerm.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:664:53: ( fuzzy )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:665:11: ftsFieldGroupExactTerm ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsFieldGroupExactTerm_in_ftsFieldGroupTest4831);
					ftsFieldGroupExactTerm133=ftsFieldGroupExactTerm();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupExactTerm.add(ftsFieldGroupExactTerm133.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:665:34: ( ( fuzzy )=> fuzzy )?
					int alt55=2;
					int LA55_0 = input.LA(1);
					if ( (LA55_0==TILDA) ) {
						int LA55_1 = input.LA(2);
						if ( (LA55_1==DECIMAL_INTEGER_LITERAL||LA55_1==FLOATING_POINT_LITERAL) ) {
							int LA55_3 = input.LA(3);
							if ( (synpred21_FTS()) ) {
								alt55=1;
							}
						}
					}
					switch (alt55) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:665:36: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest4841);
							fuzzy134=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy134.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: fuzzy, ftsFieldGroupExactTerm
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 666:17: -> ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:667:25: ^( FG_EXACT_TERM ftsFieldGroupExactTerm ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_EXACT_TERM, "FG_EXACT_TERM"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupExactTerm.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:667:64: ( fuzzy )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:668:11: ftsFieldGroupPhrase ( ( slop )=> slop )?
					{
					pushFollow(FOLLOW_ftsFieldGroupPhrase_in_ftsFieldGroupTest4906);
					ftsFieldGroupPhrase135=ftsFieldGroupPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupPhrase.add(ftsFieldGroupPhrase135.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:668:31: ( ( slop )=> slop )?
					int alt56=2;
					int LA56_0 = input.LA(1);
					if ( (LA56_0==TILDA) ) {
						int LA56_1 = input.LA(2);
						if ( (LA56_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA56_3 = input.LA(3);
							if ( (synpred22_FTS()) ) {
								alt56=1;
							}
						}
					}
					switch (alt56) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:668:33: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsFieldGroupTest4916);
							slop136=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop136.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupPhrase, slop
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 669:17: -> ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:670:25: ^( FG_PHRASE ftsFieldGroupPhrase ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PHRASE, "FG_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupPhrase.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:670:57: ( slop )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:671:11: ftsFieldGroupExactPhrase ( ( slop )=> slop )?
					{
					pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTest4981);
					ftsFieldGroupExactPhrase137=ftsFieldGroupExactPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupExactPhrase.add(ftsFieldGroupExactPhrase137.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:671:36: ( ( slop )=> slop )?
					int alt57=2;
					int LA57_0 = input.LA(1);
					if ( (LA57_0==TILDA) ) {
						int LA57_1 = input.LA(2);
						if ( (LA57_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA57_3 = input.LA(3);
							if ( (synpred23_FTS()) ) {
								alt57=1;
							}
						}
					}
					switch (alt57) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:671:38: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsFieldGroupTest4991);
							slop138=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop138.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: slop, ftsFieldGroupExactPhrase
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 672:17: -> ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:673:25: ^( FG_EXACT_PHRASE ftsFieldGroupExactPhrase ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_EXACT_PHRASE, "FG_EXACT_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupExactPhrase.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:673:68: ( slop )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:674:11: ftsFieldGroupTokenisedPhrase ( ( slop )=> slop )?
					{
					pushFollow(FOLLOW_ftsFieldGroupTokenisedPhrase_in_ftsFieldGroupTest5056);
					ftsFieldGroupTokenisedPhrase139=ftsFieldGroupTokenisedPhrase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupTokenisedPhrase.add(ftsFieldGroupTokenisedPhrase139.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:674:40: ( ( slop )=> slop )?
					int alt58=2;
					int LA58_0 = input.LA(1);
					if ( (LA58_0==TILDA) ) {
						int LA58_1 = input.LA(2);
						if ( (LA58_1==DECIMAL_INTEGER_LITERAL) ) {
							int LA58_3 = input.LA(3);
							if ( (synpred24_FTS()) ) {
								alt58=1;
							}
						}
					}
					switch (alt58) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:674:42: ( slop )=> slop
							{
							pushFollow(FOLLOW_slop_in_ftsFieldGroupTest5066);
							slop140=slop();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_slop.add(slop140.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: slop, ftsFieldGroupTokenisedPhrase
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 675:17: -> ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:676:25: ^( FG_PHRASE ftsFieldGroupTokenisedPhrase ( slop )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_PHRASE, "FG_PHRASE"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupTokenisedPhrase.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:676:66: ( slop )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:677:11: ftsFieldGroupSynonym ( ( fuzzy )=> fuzzy )?
					{
					pushFollow(FOLLOW_ftsFieldGroupSynonym_in_ftsFieldGroupTest5131);
					ftsFieldGroupSynonym141=ftsFieldGroupSynonym();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupSynonym.add(ftsFieldGroupSynonym141.getTree());
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:677:32: ( ( fuzzy )=> fuzzy )?
					int alt59=2;
					int LA59_0 = input.LA(1);
					if ( (LA59_0==TILDA) ) {
						int LA59_1 = input.LA(2);
						if ( (LA59_1==DECIMAL_INTEGER_LITERAL||LA59_1==FLOATING_POINT_LITERAL) ) {
							int LA59_3 = input.LA(3);
							if ( (synpred25_FTS()) ) {
								alt59=1;
							}
						}
					}
					switch (alt59) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:677:34: ( fuzzy )=> fuzzy
							{
							pushFollow(FOLLOW_fuzzy_in_ftsFieldGroupTest5141);
							fuzzy142=fuzzy();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==0 ) stream_fuzzy.add(fuzzy142.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: ftsFieldGroupSynonym, fuzzy
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==0 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (Object)adaptor.nil();
					// 678:17: -> ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:25: ^( FG_SYNONYM ftsFieldGroupSynonym ( fuzzy )? )
						{
						Object root_1 = (Object)adaptor.nil();
						root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FG_SYNONYM, "FG_SYNONYM"), root_1);
						adaptor.addChild(root_1, stream_ftsFieldGroupSynonym.nextTree());
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:679:59: ( fuzzy )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:680:11: ftsFieldGroupRange
					{
					pushFollow(FOLLOW_ftsFieldGroupRange_in_ftsFieldGroupTest5206);
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
					// 681:17: -> ^( FG_RANGE ftsFieldGroupRange )
					{
						// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:682:25: ^( FG_RANGE ftsFieldGroupRange )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:683:11: LPAREN ftsFieldGroupDisjunction RPAREN
					{
					LPAREN144=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_ftsFieldGroupTest5266); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN144);

					pushFollow(FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroupTest5268);
					ftsFieldGroupDisjunction145=ftsFieldGroupDisjunction();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsFieldGroupDisjunction.add(ftsFieldGroupDisjunction145.getTree());
					RPAREN146=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_ftsFieldGroupTest5270); if (state.failed) return retval; 
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
					// 684:17: -> ftsFieldGroupDisjunction
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:687:1: ftsFieldGroupTerm : ftsWord ;
	public final FTSParser.ftsFieldGroupTerm_return ftsFieldGroupTerm() throws RecognitionException {
		FTSParser.ftsFieldGroupTerm_return retval = new FTSParser.ftsFieldGroupTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		ParserRuleReturnScope ftsWord147 =null;


		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:688:9: ( ftsWord )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:689:9: ftsWord
			{
			root_0 = (Object)adaptor.nil();


			pushFollow(FOLLOW_ftsWord_in_ftsFieldGroupTerm5323);
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:692:1: ftsFieldGroupExactTerm : EQUALS ftsFieldGroupTerm -> ftsFieldGroupTerm ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:693:9: ( EQUALS ftsFieldGroupTerm -> ftsFieldGroupTerm )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:694:9: EQUALS ftsFieldGroupTerm
			{
			EQUALS148=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsFieldGroupExactTerm5356); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS148);

			pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupExactTerm5358);
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
			// 695:17: -> ftsFieldGroupTerm
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:698:1: ftsFieldGroupPhrase : FTSPHRASE ;
	public final FTSParser.ftsFieldGroupPhrase_return ftsFieldGroupPhrase() throws RecognitionException {
		FTSParser.ftsFieldGroupPhrase_return retval = new FTSParser.ftsFieldGroupPhrase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token FTSPHRASE150=null;

		Object FTSPHRASE150_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:699:9: ( FTSPHRASE )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:700:9: FTSPHRASE
			{
			root_0 = (Object)adaptor.nil();


			FTSPHRASE150=(Token)match(input,FTSPHRASE,FOLLOW_FTSPHRASE_in_ftsFieldGroupPhrase5411); if (state.failed) return retval;
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:703:1: ftsFieldGroupExactPhrase : EQUALS ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:704:9: ( EQUALS ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:705:9: EQUALS ftsFieldGroupExactPhrase
			{
			EQUALS151=(Token)match(input,EQUALS,FOLLOW_EQUALS_in_ftsFieldGroupExactPhrase5452); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_EQUALS.add(EQUALS151);

			pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupExactPhrase5454);
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
			// 706:17: -> ftsFieldGroupExactPhrase
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:709:1: ftsFieldGroupTokenisedPhrase : TILDA ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:710:9: ( TILDA ftsFieldGroupExactPhrase -> ftsFieldGroupExactPhrase )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:711:9: TILDA ftsFieldGroupExactPhrase
			{
			TILDA153=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsFieldGroupTokenisedPhrase5515); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA153);

			pushFollow(FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTokenisedPhrase5517);
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
			// 712:17: -> ftsFieldGroupExactPhrase
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:715:1: ftsFieldGroupSynonym : TILDA ftsFieldGroupTerm -> ftsFieldGroupTerm ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:716:9: ( TILDA ftsFieldGroupTerm -> ftsFieldGroupTerm )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:717:9: TILDA ftsFieldGroupTerm
			{
			TILDA155=(Token)match(input,TILDA,FOLLOW_TILDA_in_ftsFieldGroupSynonym5570); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_TILDA.add(TILDA155);

			pushFollow(FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupSynonym5572);
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
			// 718:17: -> ftsFieldGroupTerm
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:721:1: ftsFieldGroupProximity : ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+ -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+ ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:722:9: ( ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+ -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+ )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:723:9: ftsFieldGroupProximityTerm ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+
			{
			pushFollow(FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5625);
			ftsFieldGroupProximityTerm157=ftsFieldGroupProximityTerm();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_ftsFieldGroupProximityTerm.add(ftsFieldGroupProximityTerm157.getTree());
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:723:36: ( ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm )+
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
						if ( (synpred26_FTS()) ) {
							alt61=1;
						}

						}
						break;
					case ID:
						{
						int LA61_4 = input.LA(3);
						if ( (synpred26_FTS()) ) {
							alt61=1;
						}

						}
						break;
					case TO:
						{
						int LA61_5 = input.LA(3);
						if ( (synpred26_FTS()) ) {
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
						if ( (synpred26_FTS()) ) {
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
									if ( (synpred26_FTS()) ) {
										alt61=1;
									}

									}
									break;
								case ID:
									{
									int LA61_13 = input.LA(6);
									if ( (synpred26_FTS()) ) {
										alt61=1;
									}

									}
									break;
								case TO:
									{
									int LA61_14 = input.LA(6);
									if ( (synpred26_FTS()) ) {
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
									if ( (synpred26_FTS()) ) {
										alt61=1;
									}

									}
									break;
								}
							}

						}
						else if ( (LA61_7==RPAREN) && (synpred26_FTS())) {
							alt61=1;
						}

						}
						break;
					}
				}

				switch (alt61) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:723:38: ( proximityGroup )=> proximityGroup ftsFieldGroupProximityTerm
					{
					pushFollow(FOLLOW_proximityGroup_in_ftsFieldGroupProximity5635);
					proximityGroup158=proximityGroup();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_proximityGroup.add(proximityGroup158.getTree());
					pushFollow(FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5637);
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
			// elements: ftsFieldGroupProximityTerm, ftsFieldGroupProximityTerm, proximityGroup
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 724:17: -> ftsFieldGroupProximityTerm ( proximityGroup ftsFieldGroupProximityTerm )+
			{
				adaptor.addChild(root_0, stream_ftsFieldGroupProximityTerm.nextTree());
				if ( !(stream_ftsFieldGroupProximityTerm.hasNext()||stream_proximityGroup.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_ftsFieldGroupProximityTerm.hasNext()||stream_proximityGroup.hasNext() ) {
					adaptor.addChild(root_0, stream_proximityGroup.nextTree());
					adaptor.addChild(root_0, stream_ftsFieldGroupProximityTerm.nextTree());
				}
				stream_ftsFieldGroupProximityTerm.reset();
				stream_proximityGroup.reset();

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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:727:1: ftsFieldGroupProximityTerm : ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME );
	public final FTSParser.ftsFieldGroupProximityTerm_return ftsFieldGroupProximityTerm() throws RecognitionException {
		FTSParser.ftsFieldGroupProximityTerm_return retval = new FTSParser.ftsFieldGroupProximityTerm_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set160=null;

		Object set160_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:728:9: ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set160=input.LT(1);
			if ( (input.LA(1) >= DATETIME && input.LA(1) <= DECIMAL_INTEGER_LITERAL)||input.LA(1)==FLOATING_POINT_LITERAL||(input.LA(1) >= FTSPRE && input.LA(1) <= FTSWORD)||input.LA(1)==ID||input.LA(1)==NOT||input.LA(1)==TO ) {
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:740:1: proximityGroup : STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )? -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:741:9: ( STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )? -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:742:9: STAR ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )?
			{
			STAR161=(Token)match(input,STAR,FOLLOW_STAR_in_proximityGroup5830); if (state.failed) return retval; 
			if ( state.backtracking==0 ) stream_STAR.add(STAR161);

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:742:14: ( LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN )?
			int alt63=2;
			int LA63_0 = input.LA(1);
			if ( (LA63_0==LPAREN) ) {
				alt63=1;
			}
			switch (alt63) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:742:15: LPAREN ( DECIMAL_INTEGER_LITERAL )? RPAREN
					{
					LPAREN162=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_proximityGroup5833); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN162);

					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:742:22: ( DECIMAL_INTEGER_LITERAL )?
					int alt62=2;
					int LA62_0 = input.LA(1);
					if ( (LA62_0==DECIMAL_INTEGER_LITERAL) ) {
						alt62=1;
					}
					switch (alt62) {
						case 1 :
							// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:742:22: DECIMAL_INTEGER_LITERAL
							{
							DECIMAL_INTEGER_LITERAL163=(Token)match(input,DECIMAL_INTEGER_LITERAL,FOLLOW_DECIMAL_INTEGER_LITERAL_in_proximityGroup5835); if (state.failed) return retval; 
							if ( state.backtracking==0 ) stream_DECIMAL_INTEGER_LITERAL.add(DECIMAL_INTEGER_LITERAL163);

							}
							break;

					}

					RPAREN164=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_proximityGroup5838); if (state.failed) return retval; 
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
			// 743:17: -> ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:744:25: ^( PROXIMITY ( DECIMAL_INTEGER_LITERAL )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PROXIMITY, "PROXIMITY"), root_1);
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:744:37: ( DECIMAL_INTEGER_LITERAL )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:747:1: ftsFieldGroupRange : ( ftsRangeWord DOTDOT ftsRangeWord -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE | range_left ftsRangeWord TO ftsRangeWord range_right -> range_left ftsRangeWord ftsRangeWord range_right );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:748:9: ( ftsRangeWord DOTDOT ftsRangeWord -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE | range_left ftsRangeWord TO ftsRangeWord range_right -> range_left ftsRangeWord ftsRangeWord range_right )
			int alt64=2;
			int LA64_0 = input.LA(1);
			if ( ((LA64_0 >= DATETIME && LA64_0 <= DECIMAL_INTEGER_LITERAL)||LA64_0==FLOATING_POINT_LITERAL||(LA64_0 >= FTSPHRASE && LA64_0 <= FTSWORD)||LA64_0==ID) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:749:9: ftsRangeWord DOTDOT ftsRangeWord
					{
					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5922);
					ftsRangeWord165=ftsRangeWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord165.getTree());
					DOTDOT166=(Token)match(input,DOTDOT,FOLLOW_DOTDOT_in_ftsFieldGroupRange5924); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOTDOT.add(DOTDOT166);

					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5926);
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
					// 750:17: -> INCLUSIVE ftsRangeWord ftsRangeWord INCLUSIVE
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:751:11: range_left ftsRangeWord TO ftsRangeWord range_right
					{
					pushFollow(FOLLOW_range_left_in_ftsFieldGroupRange5964);
					range_left168=range_left();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_range_left.add(range_left168.getTree());
					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5966);
					ftsRangeWord169=ftsRangeWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord169.getTree());
					TO170=(Token)match(input,TO,FOLLOW_TO_in_ftsFieldGroupRange5968); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_TO.add(TO170);

					pushFollow(FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5970);
					ftsRangeWord171=ftsRangeWord();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_ftsRangeWord.add(ftsRangeWord171.getTree());
					pushFollow(FOLLOW_range_right_in_ftsFieldGroupRange5972);
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
					// 752:17: -> range_left ftsRangeWord ftsRangeWord range_right
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:755:1: range_left : ( LSQUARE -> INCLUSIVE | LT -> EXCLUSIVE );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:756:9: ( LSQUARE -> INCLUSIVE | LT -> EXCLUSIVE )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:757:9: LSQUARE
					{
					LSQUARE173=(Token)match(input,LSQUARE,FOLLOW_LSQUARE_in_range_left6031); if (state.failed) return retval; 
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
					// 758:17: -> INCLUSIVE
					{
						adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:759:11: LT
					{
					LT174=(Token)match(input,LT,FOLLOW_LT_in_range_left6063); if (state.failed) return retval; 
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
					// 760:17: -> EXCLUSIVE
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:763:1: range_right : ( RSQUARE -> INCLUSIVE | GT -> EXCLUSIVE );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:764:9: ( RSQUARE -> INCLUSIVE | GT -> EXCLUSIVE )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:765:9: RSQUARE
					{
					RSQUARE175=(Token)match(input,RSQUARE,FOLLOW_RSQUARE_in_range_right6116); if (state.failed) return retval; 
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
					// 766:17: -> INCLUSIVE
					{
						adaptor.addChild(root_0, (Object)adaptor.create(INCLUSIVE, "INCLUSIVE"));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:767:11: GT
					{
					GT176=(Token)match(input,GT,FOLLOW_GT_in_range_right6148); if (state.failed) return retval; 
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
					// 768:17: -> EXCLUSIVE
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:773:1: fieldReference : ( AT )? ( ( prefix )=> prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:774:9: ( ( AT )? ( ( prefix )=> prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:775:9: ( AT )? ( ( prefix )=> prefix | uri )? identifier
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:775:9: ( AT )?
			int alt67=2;
			int LA67_0 = input.LA(1);
			if ( (LA67_0==AT) ) {
				alt67=1;
			}
			switch (alt67) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:775:9: AT
					{
					AT177=(Token)match(input,AT,FOLLOW_AT_in_fieldReference6204); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_AT.add(AT177);

					}
					break;

			}

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:776:9: ( ( prefix )=> prefix | uri )?
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
											if ( (synpred27_FTS()) ) {
												alt68=1;
											}
										}
									}
									else if ( (LA68_11==COLON) && (synpred27_FTS())) {
										alt68=1;
									}
								}
								else if ( (LA68_8==TO) ) {
									int LA68_12 = input.LA(6);
									if ( (LA68_12==COLON) && (synpred27_FTS())) {
										alt68=1;
									}
								}
								else if ( (LA68_8==OR) && (synpred27_FTS())) {
									alt68=1;
								}
								else if ( (LA68_8==AND) && (synpred27_FTS())) {
									alt68=1;
								}
								else if ( (LA68_8==NOT) ) {
									int LA68_15 = input.LA(6);
									if ( (LA68_15==COLON) && (synpred27_FTS())) {
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
									if ( (synpred27_FTS()) ) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred27_FTS())) {
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
									if ( (synpred27_FTS()) ) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred27_FTS())) {
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
									if ( (synpred27_FTS()) ) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred27_FTS())) {
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
									if ( (synpred27_FTS()) ) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred27_FTS())) {
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
									if ( (synpred27_FTS()) ) {
										alt68=1;
									}
								}
							}
							else if ( (LA68_11==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==TO) ) {
							int LA68_12 = input.LA(4);
							if ( (LA68_12==COLON) && (synpred27_FTS())) {
								alt68=1;
							}
						}
						else if ( (LA68_8==OR) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==AND) && (synpred27_FTS())) {
							alt68=1;
						}
						else if ( (LA68_8==NOT) ) {
							int LA68_15 = input.LA(4);
							if ( (LA68_15==COLON) && (synpred27_FTS())) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:777:19: ( prefix )=> prefix
					{
					pushFollow(FOLLOW_prefix_in_fieldReference6241);
					prefix178=prefix();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_prefix.add(prefix178.getTree());
					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:778:19: uri
					{
					pushFollow(FOLLOW_uri_in_fieldReference6261);
					uri179=uri();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_uri.add(uri179.getTree());
					}
					break;

			}

			pushFollow(FOLLOW_identifier_in_fieldReference6282);
			identifier180=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier180.getTree());
			// AST REWRITE
			// elements: uri, prefix, identifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 781:17: -> ^( FIELD_REF identifier ( prefix )? ( uri )? )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:782:25: ^( FIELD_REF identifier ( prefix )? ( uri )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_REF, "FIELD_REF"), root_1);
				adaptor.addChild(root_1, stream_identifier.nextTree());
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:782:48: ( prefix )?
				if ( stream_prefix.hasNext() ) {
					adaptor.addChild(root_1, stream_prefix.nextTree());
				}
				stream_prefix.reset();

				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:782:56: ( uri )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:785:1: tempReference : ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:786:9: ( ( AT )? ( prefix | uri )? identifier -> ^( FIELD_REF identifier ( prefix )? ( uri )? ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:787:9: ( AT )? ( prefix | uri )? identifier
			{
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:787:9: ( AT )?
			int alt69=2;
			int LA69_0 = input.LA(1);
			if ( (LA69_0==AT) ) {
				alt69=1;
			}
			switch (alt69) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:787:9: AT
					{
					AT181=(Token)match(input,AT,FOLLOW_AT_in_tempReference6369); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_AT.add(AT181);

					}
					break;

			}

			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:788:9: ( prefix | uri )?
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:789:17: prefix
					{
					pushFollow(FOLLOW_prefix_in_tempReference6398);
					prefix182=prefix();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_prefix.add(prefix182.getTree());
					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:790:19: uri
					{
					pushFollow(FOLLOW_uri_in_tempReference6418);
					uri183=uri();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) stream_uri.add(uri183.getTree());
					}
					break;

			}

			pushFollow(FOLLOW_identifier_in_tempReference6439);
			identifier184=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier184.getTree());
			// AST REWRITE
			// elements: uri, prefix, identifier
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==0 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (Object)adaptor.nil();
			// 793:17: -> ^( FIELD_REF identifier ( prefix )? ( uri )? )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:794:25: ^( FIELD_REF identifier ( prefix )? ( uri )? )
				{
				Object root_1 = (Object)adaptor.nil();
				root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(FIELD_REF, "FIELD_REF"), root_1);
				adaptor.addChild(root_1, stream_identifier.nextTree());
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:794:48: ( prefix )?
				if ( stream_prefix.hasNext() ) {
					adaptor.addChild(root_1, stream_prefix.nextTree());
				}
				stream_prefix.reset();

				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:794:56: ( uri )?
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:797:1: prefix : identifier COLON -> ^( PREFIX identifier ) ;
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:798:9: ( identifier COLON -> ^( PREFIX identifier ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:799:9: identifier COLON
			{
			pushFollow(FOLLOW_identifier_in_prefix6526);
			identifier185=identifier();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==0 ) stream_identifier.add(identifier185.getTree());
			COLON186=(Token)match(input,COLON,FOLLOW_COLON_in_prefix6528); if (state.failed) return retval; 
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
			// 800:17: -> ^( PREFIX identifier )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:801:25: ^( PREFIX identifier )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:804:1: uri : URI -> ^( NAME_SPACE URI ) ;
	public final FTSParser.uri_return uri() throws RecognitionException {
		FTSParser.uri_return retval = new FTSParser.uri_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token URI187=null;

		Object URI187_tree=null;
		RewriteRuleTokenStream stream_URI=new RewriteRuleTokenStream(adaptor,"token URI");

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:805:9: ( URI -> ^( NAME_SPACE URI ) )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:806:9: URI
			{
			URI187=(Token)match(input,URI,FOLLOW_URI_in_uri6609); if (state.failed) return retval; 
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
			// 807:17: -> ^( NAME_SPACE URI )
			{
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:808:25: ^( NAME_SPACE URI )
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:811:1: identifier : ( ( ID DOT ID )=>id1= ID DOT id2= ID ->| ID -> ID | TO -> TO | OR -> OR | AND -> AND | NOT -> NOT );
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
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:812:9: ( ( ID DOT ID )=>id1= ID DOT id2= ID ->| ID -> ID | TO -> TO | OR -> OR | AND -> AND | NOT -> NOT )
			int alt71=6;
			switch ( input.LA(1) ) {
			case ID:
				{
				int LA71_1 = input.LA(2);
				if ( (LA71_1==DOT) ) {
					int LA71_6 = input.LA(3);
					if ( (LA71_6==ID) ) {
						int LA71_8 = input.LA(4);
						if ( (synpred28_FTS()) ) {
							alt71=1;
						}
						else if ( (true) ) {
							alt71=2;
						}

					}
					else if ( ((LA71_6 >= DATETIME && LA71_6 <= DECIMAL_INTEGER_LITERAL)||LA71_6==FLOATING_POINT_LITERAL||(LA71_6 >= FTSPRE && LA71_6 <= FTSWORD)||LA71_6==NOT||LA71_6==QUESTION_MARK||LA71_6==STAR||LA71_6==TO) ) {
						alt71=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 71, 6, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA71_1==EOF||(LA71_1 >= AMP && LA71_1 <= BAR)||(LA71_1 >= CARAT && LA71_1 <= COMMA)||(LA71_1 >= DATETIME && LA71_1 <= DECIMAL_INTEGER_LITERAL)||LA71_1==EQUALS||LA71_1==EXCLAMATION||LA71_1==FLOATING_POINT_LITERAL||(LA71_1 >= FTSPHRASE && LA71_1 <= FTSWORD)||LA71_1==ID||(LA71_1 >= LPAREN && LA71_1 <= LT)||LA71_1==MINUS||LA71_1==NOT||(LA71_1 >= OR && LA71_1 <= PERCENT)||LA71_1==PLUS||LA71_1==QUESTION_MARK||LA71_1==RPAREN||LA71_1==STAR||(LA71_1 >= TILDA && LA71_1 <= URI)) ) {
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:813:9: ( ID DOT ID )=>id1= ID DOT id2= ID
					{
					id1=(Token)match(input,ID,FOLLOW_ID_in_identifier6711); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_ID.add(id1);

					DOT188=(Token)match(input,DOT,FOLLOW_DOT_in_identifier6713); if (state.failed) return retval; 
					if ( state.backtracking==0 ) stream_DOT.add(DOT188);

					id2=(Token)match(input,ID,FOLLOW_ID_in_identifier6717); if (state.failed) return retval; 
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
					// 815:17: ->
					{
						adaptor.addChild(root_0, new CommonTree(new CommonToken(FTSLexer.ID, (id1!=null?id1.getText():null)+(DOT188!=null?DOT188.getText():null)+(id2!=null?id2.getText():null))));
					}


					retval.tree = root_0;
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:817:12: ID
					{
					ID189=(Token)match(input,ID,FOLLOW_ID_in_identifier6766); if (state.failed) return retval; 
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
					// 818:17: -> ID
					{
						adaptor.addChild(root_0, stream_ID.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:821:12: TO
					{
					TO190=(Token)match(input,TO,FOLLOW_TO_in_identifier6833); if (state.failed) return retval; 
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
					// 822:17: -> TO
					{
						adaptor.addChild(root_0, stream_TO.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:823:12: OR
					{
					OR191=(Token)match(input,OR,FOLLOW_OR_in_identifier6871); if (state.failed) return retval; 
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
					// 824:17: -> OR
					{
						adaptor.addChild(root_0, stream_OR.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:825:12: AND
					{
					AND192=(Token)match(input,AND,FOLLOW_AND_in_identifier6909); if (state.failed) return retval; 
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
					// 826:17: -> AND
					{
						adaptor.addChild(root_0, stream_AND.nextNode());
					}


					retval.tree = root_0;
					}

					}
					break;
				case 6 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:827:12: NOT
					{
					NOT193=(Token)match(input,NOT,FOLLOW_NOT_in_identifier6948); if (state.failed) return retval; 
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
					// 828:17: -> NOT
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:831:1: ftsWord : ( ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase DOT | COMMA ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) | ( DOT | COMMA ) ftsWordBase | ftsWordBase );
	public final FTSParser.ftsWord_return ftsWord() throws RecognitionException {
		FTSParser.ftsWord_return retval = new FTSParser.ftsWord_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set194=null;
		Token set196=null;
		Token set198=null;
		Token set200=null;
		Token set202=null;
		Token set205=null;
		Token set207=null;
		Token set209=null;
		Token set211=null;
		Token set213=null;
		Token set215=null;
		Token set217=null;
		Token set219=null;
		Token set221=null;
		Token set223=null;
		Token set225=null;
		Token set227=null;
		Token set229=null;
		Token set230=null;
		Token set232=null;
		Token set234=null;
		Token set236=null;
		Token set239=null;
		Token set241=null;
		Token set243=null;
		Token set245=null;
		Token set247=null;
		Token set249=null;
		Token set251=null;
		Token set253=null;
		Token set255=null;
		Token set257=null;
		Token set258=null;
		Token set260=null;
		Token set262=null;
		Token set265=null;
		Token set267=null;
		Token set269=null;
		Token set271=null;
		Token set273=null;
		Token set275=null;
		Token set277=null;
		Token set278=null;
		Token set280=null;
		Token set283=null;
		Token set285=null;
		Token set287=null;
		Token set289=null;
		Token set290=null;
		ParserRuleReturnScope ftsWordBase195 =null;
		ParserRuleReturnScope ftsWordBase197 =null;
		ParserRuleReturnScope ftsWordBase199 =null;
		ParserRuleReturnScope ftsWordBase201 =null;
		ParserRuleReturnScope ftsWordBase203 =null;
		ParserRuleReturnScope ftsWordBase204 =null;
		ParserRuleReturnScope ftsWordBase206 =null;
		ParserRuleReturnScope ftsWordBase208 =null;
		ParserRuleReturnScope ftsWordBase210 =null;
		ParserRuleReturnScope ftsWordBase212 =null;
		ParserRuleReturnScope ftsWordBase214 =null;
		ParserRuleReturnScope ftsWordBase216 =null;
		ParserRuleReturnScope ftsWordBase218 =null;
		ParserRuleReturnScope ftsWordBase220 =null;
		ParserRuleReturnScope ftsWordBase222 =null;
		ParserRuleReturnScope ftsWordBase224 =null;
		ParserRuleReturnScope ftsWordBase226 =null;
		ParserRuleReturnScope ftsWordBase228 =null;
		ParserRuleReturnScope ftsWordBase231 =null;
		ParserRuleReturnScope ftsWordBase233 =null;
		ParserRuleReturnScope ftsWordBase235 =null;
		ParserRuleReturnScope ftsWordBase237 =null;
		ParserRuleReturnScope ftsWordBase238 =null;
		ParserRuleReturnScope ftsWordBase240 =null;
		ParserRuleReturnScope ftsWordBase242 =null;
		ParserRuleReturnScope ftsWordBase244 =null;
		ParserRuleReturnScope ftsWordBase246 =null;
		ParserRuleReturnScope ftsWordBase248 =null;
		ParserRuleReturnScope ftsWordBase250 =null;
		ParserRuleReturnScope ftsWordBase252 =null;
		ParserRuleReturnScope ftsWordBase254 =null;
		ParserRuleReturnScope ftsWordBase256 =null;
		ParserRuleReturnScope ftsWordBase259 =null;
		ParserRuleReturnScope ftsWordBase261 =null;
		ParserRuleReturnScope ftsWordBase263 =null;
		ParserRuleReturnScope ftsWordBase264 =null;
		ParserRuleReturnScope ftsWordBase266 =null;
		ParserRuleReturnScope ftsWordBase268 =null;
		ParserRuleReturnScope ftsWordBase270 =null;
		ParserRuleReturnScope ftsWordBase272 =null;
		ParserRuleReturnScope ftsWordBase274 =null;
		ParserRuleReturnScope ftsWordBase276 =null;
		ParserRuleReturnScope ftsWordBase279 =null;
		ParserRuleReturnScope ftsWordBase281 =null;
		ParserRuleReturnScope ftsWordBase282 =null;
		ParserRuleReturnScope ftsWordBase284 =null;
		ParserRuleReturnScope ftsWordBase286 =null;
		ParserRuleReturnScope ftsWordBase288 =null;
		ParserRuleReturnScope ftsWordBase291 =null;
		ParserRuleReturnScope ftsWordBase292 =null;

		Object set194_tree=null;
		Object set196_tree=null;
		Object set198_tree=null;
		Object set200_tree=null;
		Object set202_tree=null;
		Object set205_tree=null;
		Object set207_tree=null;
		Object set209_tree=null;
		Object set211_tree=null;
		Object set213_tree=null;
		Object set215_tree=null;
		Object set217_tree=null;
		Object set219_tree=null;
		Object set221_tree=null;
		Object set223_tree=null;
		Object set225_tree=null;
		Object set227_tree=null;
		Object set229_tree=null;
		Object set230_tree=null;
		Object set232_tree=null;
		Object set234_tree=null;
		Object set236_tree=null;
		Object set239_tree=null;
		Object set241_tree=null;
		Object set243_tree=null;
		Object set245_tree=null;
		Object set247_tree=null;
		Object set249_tree=null;
		Object set251_tree=null;
		Object set253_tree=null;
		Object set255_tree=null;
		Object set257_tree=null;
		Object set258_tree=null;
		Object set260_tree=null;
		Object set262_tree=null;
		Object set265_tree=null;
		Object set267_tree=null;
		Object set269_tree=null;
		Object set271_tree=null;
		Object set273_tree=null;
		Object set275_tree=null;
		Object set277_tree=null;
		Object set278_tree=null;
		Object set280_tree=null;
		Object set283_tree=null;
		Object set285_tree=null;
		Object set287_tree=null;
		Object set289_tree=null;
		Object set290_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:832:9: ( ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase DOT | COMMA ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) | ( DOT | COMMA ) ftsWordBase | ftsWordBase )
			int alt72=18;
			alt72 = dfa72.predict(input);
			switch (alt72) {
				case 1 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:833:12: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					set194=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set194));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7072);
					ftsWordBase195=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase195.getTree());

					set196=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set196));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7080);
					ftsWordBase197=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase197.getTree());

					set198=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
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
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7088);
					ftsWordBase199=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase199.getTree());

					set200=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set200));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7096);
					ftsWordBase201=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase201.getTree());

					set202=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set202));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7104);
					ftsWordBase203=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase203.getTree());

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:835:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase DOT | COMMA ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7164);
					ftsWordBase204=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase204.getTree());

					set205=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set205));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7172);
					ftsWordBase206=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase206.getTree());

					set207=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set207));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7180);
					ftsWordBase208=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase208.getTree());

					set209=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set209));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7188);
					ftsWordBase210=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase210.getTree());

					set211=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set211));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7196);
					ftsWordBase212=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase212.getTree());

					}
					break;
				case 3 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:837:12: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					set213=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set213));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7269);
					ftsWordBase214=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase214.getTree());

					set215=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set215));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7277);
					ftsWordBase216=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase216.getTree());

					set217=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set217));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7285);
					ftsWordBase218=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase218.getTree());

					set219=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set219));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7293);
					ftsWordBase220=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase220.getTree());

					set221=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set221));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 4 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:839:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7359);
					ftsWordBase222=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase222.getTree());

					set223=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set223));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7367);
					ftsWordBase224=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase224.getTree());

					set225=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set225));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7375);
					ftsWordBase226=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase226.getTree());

					set227=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set227));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7383);
					ftsWordBase228=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase228.getTree());

					set229=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set229));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 5 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:841:12: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					set230=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set230));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7456);
					ftsWordBase231=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase231.getTree());

					set232=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set232));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7464);
					ftsWordBase233=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase233.getTree());

					set234=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set234));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7472);
					ftsWordBase235=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase235.getTree());

					set236=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set236));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7480);
					ftsWordBase237=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase237.getTree());

					}
					break;
				case 6 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:843:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7534);
					ftsWordBase238=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase238.getTree());

					set239=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set239));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7542);
					ftsWordBase240=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase240.getTree());

					set241=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set241));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7550);
					ftsWordBase242=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase242.getTree());

					set243=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set243));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7558);
					ftsWordBase244=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase244.getTree());

					}
					break;
				case 7 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:845:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					set245=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set245));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7622);
					ftsWordBase246=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase246.getTree());

					set247=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set247));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7630);
					ftsWordBase248=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase248.getTree());

					set249=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set249));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7638);
					ftsWordBase250=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase250.getTree());

					set251=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set251));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 8 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:847:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7696);
					ftsWordBase252=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase252.getTree());

					set253=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set253));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7704);
					ftsWordBase254=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase254.getTree());

					set255=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set255));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7712);
					ftsWordBase256=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase256.getTree());

					set257=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set257));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 9 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:849:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					set258=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set258));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7776);
					ftsWordBase259=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase259.getTree());

					set260=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set260));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7784);
					ftsWordBase261=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase261.getTree());

					set262=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set262));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7792);
					ftsWordBase263=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase263.getTree());

					}
					break;
				case 10 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:851:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7838);
					ftsWordBase264=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase264.getTree());

					set265=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set265));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7846);
					ftsWordBase266=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase266.getTree());

					set267=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set267));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7854);
					ftsWordBase268=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase268.getTree());

					}
					break;
				case 11 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:853:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					set269=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set269));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7911);
					ftsWordBase270=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase270.getTree());

					set271=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set271));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7919);
					ftsWordBase272=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase272.getTree());

					set273=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set273));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 12 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:855:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7970);
					ftsWordBase274=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase274.getTree());

					set275=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set275));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord7978);
					ftsWordBase276=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase276.getTree());

					set277=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set277));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 13 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:857:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					set278=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set278));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8035);
					ftsWordBase279=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase279.getTree());

					set280=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set280));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8043);
					ftsWordBase281=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase281.getTree());

					}
					break;
				case 14 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:859:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8081);
					ftsWordBase282=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase282.getTree());

					set283=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set283));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8089);
					ftsWordBase284=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase284.getTree());

					}
					break;
				case 15 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:861:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					set285=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set285));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8137);
					ftsWordBase286=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase286.getTree());

					set287=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set287));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 16 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:863:11: ( ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA )
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8179);
					ftsWordBase288=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase288.getTree());

					set289=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set289));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					}
					break;
				case 17 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:865:11: ( DOT | COMMA ) ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					set290=input.LT(1);
					if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
						input.consume();
						if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set290));
						state.errorRecovery=false;
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						throw mse;
					}
					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8203);
					ftsWordBase291=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase291.getTree());

					}
					break;
				case 18 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:866:11: ftsWordBase
					{
					root_0 = (Object)adaptor.nil();


					pushFollow(FOLLOW_ftsWordBase_in_ftsWord8216);
					ftsWordBase292=ftsWordBase();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==0 ) adaptor.addChild(root_0, ftsWordBase292.getTree());

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
	// $ANTLR end "ftsWord"


	public static class ftsWordBase_return extends ParserRuleReturnScope {
		Object tree;
		@Override
		public Object getTree() { return tree; }
	};


	// $ANTLR start "ftsWordBase"
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:869:1: ftsWordBase : ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | STAR | QUESTION_MARK | DATETIME );
	public final FTSParser.ftsWordBase_return ftsWordBase() throws RecognitionException {
		FTSParser.ftsWordBase_return retval = new FTSParser.ftsWordBase_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set293=null;

		Object set293_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:870:9: ( ID | FTSWORD | FTSPRE | FTSWILD | NOT | TO | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | STAR | QUESTION_MARK | DATETIME )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set293=input.LT(1);
			if ( (input.LA(1) >= DATETIME && input.LA(1) <= DECIMAL_INTEGER_LITERAL)||input.LA(1)==FLOATING_POINT_LITERAL||(input.LA(1) >= FTSPRE && input.LA(1) <= FTSWORD)||input.LA(1)==ID||input.LA(1)==NOT||input.LA(1)==QUESTION_MARK||input.LA(1)==STAR||input.LA(1)==TO ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set293));
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:884:1: number : ( DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL );
	public final FTSParser.number_return number() throws RecognitionException {
		FTSParser.number_return retval = new FTSParser.number_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set294=null;

		Object set294_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:885:9: ( DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set294=input.LT(1);
			if ( input.LA(1)==DECIMAL_INTEGER_LITERAL||input.LA(1)==FLOATING_POINT_LITERAL ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set294));
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:890:1: ftsRangeWord : ( ID | FTSWORD | FTSPRE | FTSWILD | FTSPHRASE | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME );
	public final FTSParser.ftsRangeWord_return ftsRangeWord() throws RecognitionException {
		FTSParser.ftsRangeWord_return retval = new FTSParser.ftsRangeWord_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set295=null;

		Object set295_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:891:9: ( ID | FTSWORD | FTSPRE | FTSWILD | FTSPHRASE | DECIMAL_INTEGER_LITERAL | FLOATING_POINT_LITERAL | DATETIME )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set295=input.LT(1);
			if ( (input.LA(1) >= DATETIME && input.LA(1) <= DECIMAL_INTEGER_LITERAL)||input.LA(1)==FLOATING_POINT_LITERAL||(input.LA(1) >= FTSPHRASE && input.LA(1) <= FTSWORD)||input.LA(1)==ID ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set295));
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:904:1: or : ( OR | BAR BAR );
	public final FTSParser.or_return or() throws RecognitionException {
		FTSParser.or_return retval = new FTSParser.or_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token OR296=null;
		Token BAR297=null;
		Token BAR298=null;

		Object OR296_tree=null;
		Object BAR297_tree=null;
		Object BAR298_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:905:9: ( OR | BAR BAR )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:906:9: OR
					{
					root_0 = (Object)adaptor.nil();


					OR296=(Token)match(input,OR,FOLLOW_OR_in_or8581); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					OR296_tree = (Object)adaptor.create(OR296);
					adaptor.addChild(root_0, OR296_tree);
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:907:11: BAR BAR
					{
					root_0 = (Object)adaptor.nil();


					BAR297=(Token)match(input,BAR,FOLLOW_BAR_in_or8593); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					BAR297_tree = (Object)adaptor.create(BAR297);
					adaptor.addChild(root_0, BAR297_tree);
					}

					BAR298=(Token)match(input,BAR,FOLLOW_BAR_in_or8595); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					BAR298_tree = (Object)adaptor.create(BAR298);
					adaptor.addChild(root_0, BAR298_tree);
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:910:1: and : ( AND | AMP AMP );
	public final FTSParser.and_return and() throws RecognitionException {
		FTSParser.and_return retval = new FTSParser.and_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token AND299=null;
		Token AMP300=null;
		Token AMP301=null;

		Object AND299_tree=null;
		Object AMP300_tree=null;
		Object AMP301_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:911:9: ( AND | AMP AMP )
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
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:912:9: AND
					{
					root_0 = (Object)adaptor.nil();


					AND299=(Token)match(input,AND,FOLLOW_AND_in_and8628); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AND299_tree = (Object)adaptor.create(AND299);
					adaptor.addChild(root_0, AND299_tree);
					}

					}
					break;
				case 2 :
					// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:913:11: AMP AMP
					{
					root_0 = (Object)adaptor.nil();


					AMP300=(Token)match(input,AMP,FOLLOW_AMP_in_and8640); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AMP300_tree = (Object)adaptor.create(AMP300);
					adaptor.addChild(root_0, AMP300_tree);
					}

					AMP301=(Token)match(input,AMP,FOLLOW_AMP_in_and8642); if (state.failed) return retval;
					if ( state.backtracking==0 ) {
					AMP301_tree = (Object)adaptor.create(AMP301);
					adaptor.addChild(root_0, AMP301_tree);
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
	// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:916:1: not : ( NOT | EXCLAMATION );
	public final FTSParser.not_return not() throws RecognitionException {
		FTSParser.not_return retval = new FTSParser.not_return();
		retval.start = input.LT(1);

		Object root_0 = null;

		Token set302=null;

		Object set302_tree=null;

		try {
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:917:9: ( NOT | EXCLAMATION )
			// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:
			{
			root_0 = (Object)adaptor.nil();


			set302=input.LT(1);
			if ( input.LA(1)==EXCLAMATION||input.LA(1)==NOT ) {
				input.consume();
				if ( state.backtracking==0 ) adaptor.addChild(root_0, (Object)adaptor.create(set302));
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
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:512:9: ( fieldReference COLON )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:512:10: fieldReference COLON
		{
		pushFollow(FOLLOW_fieldReference_in_synpred3_FTS2635);
		fieldReference();
		state._fsp--;
		if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred3_FTS2637); if (state.failed) return;

		}

	}
	// $ANTLR end synpred3_FTS

	// $ANTLR start synpred4_FTS
	public final void synpred4_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:28: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:514:29: slop
		{
		pushFollow(FOLLOW_slop_in_synpred4_FTS2676);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred4_FTS

	// $ANTLR start synpred5_FTS
	public final void synpred5_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:26: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:517:27: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred5_FTS2751);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred5_FTS

	// $ANTLR start synpred6_FTS
	public final void synpred6_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:521:20: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:521:21: slop
		{
		pushFollow(FOLLOW_slop_in_synpred6_FTS2821);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred6_FTS

	// $ANTLR start synpred7_FTS
	public final void synpred7_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:18: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:524:19: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred7_FTS2879);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred7_FTS

	// $ANTLR start synpred8_FTS
	public final void synpred8_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:533:9: ( fieldReference COLON )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:533:10: fieldReference COLON
		{
		pushFollow(FOLLOW_fieldReference_in_synpred8_FTS2984);
		fieldReference();
		state._fsp--;
		if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred8_FTS2986); if (state.failed) return;

		}

	}
	// $ANTLR end synpred8_FTS

	// $ANTLR start synpred9_FTS
	public final void synpred9_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:28: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:535:29: slop
		{
		pushFollow(FOLLOW_slop_in_synpred9_FTS3025);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred9_FTS

	// $ANTLR start synpred10_FTS
	public final void synpred10_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:26: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:538:27: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred10_FTS3100);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred10_FTS

	// $ANTLR start synpred11_FTS
	public final void synpred11_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:542:20: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:542:21: slop
		{
		pushFollow(FOLLOW_slop_in_synpred11_FTS3170);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred11_FTS

	// $ANTLR start synpred12_FTS
	public final void synpred12_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:545:18: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:545:19: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred12_FTS3228);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred12_FTS

	// $ANTLR start synpred13_FTS
	public final void synpred13_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:555:9: ( fieldReference COLON )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:555:10: fieldReference COLON
		{
		pushFollow(FOLLOW_fieldReference_in_synpred13_FTS3335);
		fieldReference();
		state._fsp--;
		if (state.failed) return;

		match(input,COLON,FOLLOW_COLON_in_synpred13_FTS3337); if (state.failed) return;

		}

	}
	// $ANTLR end synpred13_FTS

	// $ANTLR start synpred14_FTS
	public final void synpred14_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:28: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:557:29: slop
		{
		pushFollow(FOLLOW_slop_in_synpred14_FTS3376);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred14_FTS

	// $ANTLR start synpred15_FTS
	public final void synpred15_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:26: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:560:27: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred15_FTS3451);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred15_FTS

	// $ANTLR start synpred16_FTS
	public final void synpred16_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:564:20: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:564:21: slop
		{
		pushFollow(FOLLOW_slop_in_synpred16_FTS3521);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred16_FTS

	// $ANTLR start synpred17_FTS
	public final void synpred17_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:18: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:567:19: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred17_FTS3579);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred17_FTS

	// $ANTLR start synpred18_FTS
	public final void synpred18_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:9: ( not )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:640:10: not
		{
		pushFollow(FOLLOW_not_in_synpred18_FTS4326);
		not();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred18_FTS

	// $ANTLR start synpred19_FTS
	public final void synpred19_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:659:9: ( ftsFieldGroupProximity )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:659:10: ftsFieldGroupProximity
		{
		pushFollow(FOLLOW_ftsFieldGroupProximity_in_synpred19_FTS4691);
		ftsFieldGroupProximity();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred19_FTS

	// $ANTLR start synpred20_FTS
	public final void synpred20_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:662:31: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:662:32: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred20_FTS4761);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred20_FTS

	// $ANTLR start synpred21_FTS
	public final void synpred21_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:665:36: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:665:37: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred21_FTS4836);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred21_FTS

	// $ANTLR start synpred22_FTS
	public final void synpred22_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:668:33: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:668:34: slop
		{
		pushFollow(FOLLOW_slop_in_synpred22_FTS4911);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred22_FTS

	// $ANTLR start synpred23_FTS
	public final void synpred23_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:671:38: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:671:39: slop
		{
		pushFollow(FOLLOW_slop_in_synpred23_FTS4986);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred23_FTS

	// $ANTLR start synpred24_FTS
	public final void synpred24_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:674:42: ( slop )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:674:43: slop
		{
		pushFollow(FOLLOW_slop_in_synpred24_FTS5061);
		slop();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred24_FTS

	// $ANTLR start synpred25_FTS
	public final void synpred25_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:677:34: ( fuzzy )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:677:35: fuzzy
		{
		pushFollow(FOLLOW_fuzzy_in_synpred25_FTS5136);
		fuzzy();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred25_FTS

	// $ANTLR start synpred26_FTS
	public final void synpred26_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:723:38: ( proximityGroup )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:723:39: proximityGroup
		{
		pushFollow(FOLLOW_proximityGroup_in_synpred26_FTS5630);
		proximityGroup();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred26_FTS

	// $ANTLR start synpred27_FTS
	public final void synpred27_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:777:19: ( prefix )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:777:20: prefix
		{
		pushFollow(FOLLOW_prefix_in_synpred27_FTS6236);
		prefix();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred27_FTS

	// $ANTLR start synpred28_FTS
	public final void synpred28_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:813:9: ( ID DOT ID )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:813:10: ID DOT ID
		{
		match(input,ID,FOLLOW_ID_in_synpred28_FTS6691); if (state.failed) return;

		match(input,DOT,FOLLOW_DOT_in_synpred28_FTS6693); if (state.failed) return;

		match(input,ID,FOLLOW_ID_in_synpred28_FTS6695); if (state.failed) return;

		}

	}
	// $ANTLR end synpred28_FTS

	// $ANTLR start synpred29_FTS
	public final void synpred29_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:833:12: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:833:13: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred29_FTS7017);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred29_FTS7025);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred29_FTS7033);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred29_FTS7041);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred29_FTS7049);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred29_FTS

	// $ANTLR start synpred30_FTS
	public final void synpred30_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:835:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase DOT | COMMA ftsWordBase )
		int alt75=2;
		int LA75_0 = input.LA(1);
		if ( ((LA75_0 >= DATETIME && LA75_0 <= DECIMAL_INTEGER_LITERAL)||LA75_0==FLOATING_POINT_LITERAL||(LA75_0 >= FTSPRE && LA75_0 <= FTSWORD)||LA75_0==ID||LA75_0==NOT||LA75_0==QUESTION_MARK||LA75_0==STAR||LA75_0==TO) ) {
			alt75=1;
		}
		else if ( (LA75_0==COMMA) ) {
			alt75=2;
		}

		else {
			if (state.backtracking>0) {state.failed=true; return;}
			NoViableAltException nvae =
				new NoViableAltException("", 75, 0, input);
			throw nvae;
		}

		switch (alt75) {
			case 1 :
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:835:12: ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase DOT
				{
				pushFollow(FOLLOW_ftsWordBase_in_synpred30_FTS7117);
				ftsWordBase();
				state._fsp--;
				if (state.failed) return;

				if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
					input.consume();
					state.errorRecovery=false;
					state.failed=false;
				}
				else {
					if (state.backtracking>0) {state.failed=true; return;}
					MismatchedSetException mse = new MismatchedSetException(null,input);
					throw mse;
				}
				pushFollow(FOLLOW_ftsWordBase_in_synpred30_FTS7125);
				ftsWordBase();
				state._fsp--;
				if (state.failed) return;

				if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
					input.consume();
					state.errorRecovery=false;
					state.failed=false;
				}
				else {
					if (state.backtracking>0) {state.failed=true; return;}
					MismatchedSetException mse = new MismatchedSetException(null,input);
					throw mse;
				}
				pushFollow(FOLLOW_ftsWordBase_in_synpred30_FTS7133);
				ftsWordBase();
				state._fsp--;
				if (state.failed) return;

				if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
					input.consume();
					state.errorRecovery=false;
					state.failed=false;
				}
				else {
					if (state.backtracking>0) {state.failed=true; return;}
					MismatchedSetException mse = new MismatchedSetException(null,input);
					throw mse;
				}
				pushFollow(FOLLOW_ftsWordBase_in_synpred30_FTS7141);
				ftsWordBase();
				state._fsp--;
				if (state.failed) return;

				match(input,DOT,FOLLOW_DOT_in_synpred30_FTS7143); if (state.failed) return;

				}
				break;
			case 2 :
				// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:835:100: COMMA ftsWordBase
				{
				match(input,COMMA,FOLLOW_COMMA_in_synpred30_FTS7145); if (state.failed) return;

				pushFollow(FOLLOW_ftsWordBase_in_synpred30_FTS7147);
				ftsWordBase();
				state._fsp--;
				if (state.failed) return;

				}
				break;

		}
	}
	// $ANTLR end synpred30_FTS

	// $ANTLR start synpred31_FTS
	public final void synpred31_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:837:12: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:837:13: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred31_FTS7216);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred31_FTS7224);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred31_FTS7232);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred31_FTS7240);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred31_FTS

	// $ANTLR start synpred32_FTS
	public final void synpred32_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:839:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:839:12: ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
		{
		pushFollow(FOLLOW_ftsWordBase_in_synpred32_FTS7312);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred32_FTS7320);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred32_FTS7328);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred32_FTS7336);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred32_FTS

	// $ANTLR start synpred33_FTS
	public final void synpred33_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:841:12: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:841:13: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred33_FTS7409);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred33_FTS7417);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred33_FTS7425);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred33_FTS7433);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred33_FTS

	// $ANTLR start synpred34_FTS
	public final void synpred34_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:843:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:843:12: ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
		{
		pushFollow(FOLLOW_ftsWordBase_in_synpred34_FTS7493);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred34_FTS7501);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred34_FTS7509);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred34_FTS7517);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred34_FTS

	// $ANTLR start synpred35_FTS
	public final void synpred35_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:845:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:845:12: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred35_FTS7577);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred35_FTS7585);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred35_FTS7593);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred35_FTS

	// $ANTLR start synpred36_FTS
	public final void synpred36_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:847:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:847:12: ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
		{
		pushFollow(FOLLOW_ftsWordBase_in_synpred36_FTS7657);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred36_FTS7665);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred36_FTS7673);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred36_FTS

	// $ANTLR start synpred37_FTS
	public final void synpred37_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:849:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:849:12: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred37_FTS7737);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred37_FTS7745);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred37_FTS7753);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred37_FTS

	// $ANTLR start synpred38_FTS
	public final void synpred38_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:851:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:851:12: ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
		{
		pushFollow(FOLLOW_ftsWordBase_in_synpred38_FTS7805);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred38_FTS7813);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred38_FTS7821);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred38_FTS

	// $ANTLR start synpred39_FTS
	public final void synpred39_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:853:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:853:12: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred39_FTS7874);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred39_FTS7882);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred39_FTS

	// $ANTLR start synpred40_FTS
	public final void synpred40_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:855:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:855:12: ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
		{
		pushFollow(FOLLOW_ftsWordBase_in_synpred40_FTS7938);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred40_FTS7946);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred40_FTS

	// $ANTLR start synpred41_FTS
	public final void synpred41_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:857:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:857:12: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred41_FTS8004);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred41_FTS8012);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred41_FTS

	// $ANTLR start synpred42_FTS
	public final void synpred42_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:859:11: ( ftsWordBase ( DOT | COMMA ) ftsWordBase )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:859:12: ftsWordBase ( DOT | COMMA ) ftsWordBase
		{
		pushFollow(FOLLOW_ftsWordBase_in_synpred42_FTS8056);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred42_FTS8064);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		}

	}
	// $ANTLR end synpred42_FTS

	// $ANTLR start synpred43_FTS
	public final void synpred43_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:861:11: ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:861:12: ( DOT | COMMA ) ftsWordBase ( DOT | COMMA )
		{
		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		pushFollow(FOLLOW_ftsWordBase_in_synpred43_FTS8108);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred43_FTS

	// $ANTLR start synpred44_FTS
	public final void synpred44_FTS_fragment() throws RecognitionException {
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:863:11: ( ftsWordBase ( DOT | COMMA ) )
		// W:\\alfresco\\HEAD-BUG-FIX\\root\\projects\\data-model\\source\\java\\org\\alfresco\\repo\\search\\impl\\parsers\\FTS.g:863:12: ftsWordBase ( DOT | COMMA )
		{
		pushFollow(FOLLOW_ftsWordBase_in_synpred44_FTS8156);
		ftsWordBase();
		state._fsp--;
		if (state.failed) return;

		if ( input.LA(1)==COMMA||input.LA(1)==DOT ) {
			input.consume();
			state.errorRecovery=false;
			state.failed=false;
		}
		else {
			if (state.backtracking>0) {state.failed=true; return;}
			MismatchedSetException mse = new MismatchedSetException(null,input);
			throw mse;
		}
		}

	}
	// $ANTLR end synpred44_FTS

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
	public final boolean synpred34_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred34_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred43_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred43_FTS_fragment(); // can never throw exception
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
	public final boolean synpred44_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred44_FTS_fragment(); // can never throw exception
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
	public final boolean synpred41_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred41_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred38_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred38_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred42_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred42_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred35_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred35_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred40_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred40_FTS_fragment(); // can never throw exception
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
	public final boolean synpred36_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred36_FTS_fragment(); // can never throw exception
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
	public final boolean synpred39_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred39_FTS_fragment(); // can never throw exception
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
	public final boolean synpred37_FTS() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred37_FTS_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}


	protected DFA72 dfa72 = new DFA72(this);
	static final String DFA72_eotS =
		"\u00de\uffff";
	static final String DFA72_eofS =
		"\2\uffff\1\5\1\7\1\15\1\uffff\1\46\63\uffff\1\103\1\uffff\1\134\63\uffff"+
		"\1\171\1\uffff\1\u0092\63\uffff\1\u00af\1\uffff\1\u00c8\65\uffff";
	static final String DFA72_minS =
		"\1\13\1\15\3\4\1\uffff\1\4\1\uffff\1\13\10\uffff\1\13\1\uffff\1\13\1\uffff"+
		"\2\13\12\uffff\1\13\10\uffff\1\13\1\uffff\1\13\1\uffff\2\13\12\uffff\1"+
		"\4\1\uffff\1\4\1\uffff\1\13\10\uffff\1\13\1\uffff\1\13\1\uffff\2\13\12"+
		"\uffff\1\13\10\uffff\1\13\1\uffff\1\13\1\uffff\2\13\12\uffff\1\4\1\uffff"+
		"\1\4\1\uffff\1\13\10\uffff\1\13\1\uffff\1\13\1\uffff\2\13\12\uffff\1\13"+
		"\10\uffff\1\13\1\uffff\1\13\1\uffff\2\13\12\uffff\1\4\1\uffff\1\4\1\uffff"+
		"\1\0\10\uffff\1\0\1\uffff\1\0\1\uffff\2\0\12\uffff\1\0\10\uffff\1\0\1"+
		"\uffff\1\0\1\uffff\2\0\14\uffff";
	static final String DFA72_maxS =
		"\2\135\3\136\1\uffff\1\136\1\uffff\1\24\10\uffff\1\24\1\uffff\1\24\1\uffff"+
		"\2\24\12\uffff\1\24\10\uffff\1\24\1\uffff\1\24\1\uffff\2\24\12\uffff\1"+
		"\136\1\uffff\1\136\1\uffff\1\24\10\uffff\1\24\1\uffff\1\24\1\uffff\2\24"+
		"\12\uffff\1\24\10\uffff\1\24\1\uffff\1\24\1\uffff\2\24\12\uffff\1\136"+
		"\1\uffff\1\136\1\uffff\1\24\10\uffff\1\24\1\uffff\1\24\1\uffff\2\24\12"+
		"\uffff\1\24\10\uffff\1\24\1\uffff\1\24\1\uffff\2\24\12\uffff\1\136\1\uffff"+
		"\1\136\1\uffff\1\0\10\uffff\1\0\1\uffff\1\0\1\uffff\2\0\12\uffff\1\0\10"+
		"\uffff\1\0\1\uffff\1\0\1\uffff\2\0\14\uffff";
	static final String DFA72_acceptS =
		"\5\uffff\1\22\1\uffff\1\21\1\uffff\10\20\1\uffff\1\20\1\uffff\1\20\2\uffff"+
		"\12\20\1\uffff\10\17\1\uffff\1\17\1\uffff\1\17\2\uffff\12\17\1\uffff\1"+
		"\16\1\uffff\1\15\1\uffff\10\14\1\uffff\1\14\1\uffff\1\14\2\uffff\12\14"+
		"\1\uffff\10\13\1\uffff\1\13\1\uffff\1\13\2\uffff\12\13\1\uffff\1\12\1"+
		"\uffff\1\11\1\uffff\10\10\1\uffff\1\10\1\uffff\1\10\2\uffff\12\10\1\uffff"+
		"\10\7\1\uffff\1\7\1\uffff\1\7\2\uffff\12\7\1\uffff\1\6\1\uffff\1\5\1\uffff"+
		"\10\4\1\uffff\1\4\1\uffff\1\4\2\uffff\12\4\1\uffff\10\3\1\uffff\1\3\1"+
		"\uffff\1\3\2\uffff\12\3\1\2\1\1";
	static final String DFA72_specialS =
		"\4\uffff\1\55\1\uffff\1\2\1\uffff\1\24\10\uffff\1\57\1\uffff\1\35\1\uffff"+
		"\1\45\1\31\12\uffff\1\47\10\uffff\1\13\1\uffff\1\43\1\uffff\1\20\1\25"+
		"\12\uffff\1\27\1\uffff\1\36\1\uffff\1\21\10\uffff\1\23\1\uffff\1\22\1"+
		"\uffff\1\0\1\11\12\uffff\1\16\10\uffff\1\41\1\uffff\1\56\1\uffff\1\37"+
		"\1\50\12\uffff\1\33\1\uffff\1\26\1\uffff\1\54\10\uffff\1\40\1\uffff\1"+
		"\32\1\uffff\1\17\1\46\12\uffff\1\53\10\uffff\1\12\1\uffff\1\14\1\uffff"+
		"\1\52\1\10\12\uffff\1\1\1\uffff\1\5\1\uffff\1\6\10\uffff\1\7\1\uffff\1"+
		"\51\1\uffff\1\15\1\34\12\uffff\1\44\10\uffff\1\3\1\uffff\1\4\1\uffff\1"+
		"\30\1\42\14\uffff}>";
	static final String[] DFA72_transitionS = {
			"\1\1\1\uffff\2\2\5\uffff\1\1\31\uffff\1\2\2\uffff\3\2\10\uffff\1\2\13"+
			"\uffff\1\2\10\uffff\1\2\5\uffff\1\2\5\uffff\1\2",
			"\2\3\37\uffff\1\3\2\uffff\3\3\10\uffff\1\3\13\uffff\1\3\10\uffff\1\3"+
			"\5\uffff\1\3\5\uffff\1\3",
			"\4\5\1\uffff\1\5\1\uffff\1\4\1\uffff\2\5\5\uffff\1\4\2\uffff\1\5\2\uffff"+
			"\1\5\23\uffff\1\5\1\uffff\4\5\10\uffff\1\5\3\uffff\3\5\1\uffff\1\5\3"+
			"\uffff\1\5\1\uffff\2\5\1\uffff\1\5\3\uffff\1\5\2\uffff\1\5\2\uffff\1"+
			"\5\4\uffff\3\5",
			"\4\7\1\uffff\1\7\1\uffff\1\6\1\uffff\2\7\5\uffff\1\6\2\uffff\1\7\2\uffff"+
			"\1\7\23\uffff\1\7\1\uffff\4\7\10\uffff\1\7\3\uffff\3\7\1\uffff\1\7\3"+
			"\uffff\1\7\1\uffff\2\7\1\uffff\1\7\3\uffff\1\7\2\uffff\1\7\2\uffff\1"+
			"\7\4\uffff\3\7",
			"\1\14\1\13\1\24\1\20\1\uffff\1\12\1\uffff\1\31\1\uffff\2\26\5\uffff"+
			"\1\31\2\uffff\1\32\2\uffff\1\22\23\uffff\1\26\1\uffff\1\30\3\26\10\uffff"+
			"\1\21\3\uffff\1\35\1\33\1\34\1\uffff\1\40\3\uffff\1\10\1\uffff\1\17\1"+
			"\36\1\uffff\1\37\3\uffff\1\23\2\uffff\1\16\2\uffff\1\23\4\uffff\1\11"+
			"\1\25\1\27",
			"",
			"\1\45\1\44\1\55\1\51\1\uffff\1\43\1\uffff\1\62\1\uffff\2\57\5\uffff"+
			"\1\62\2\uffff\1\63\2\uffff\1\53\23\uffff\1\57\1\uffff\1\61\3\57\10\uffff"+
			"\1\52\3\uffff\1\66\1\64\1\65\1\uffff\1\71\3\uffff\1\41\1\uffff\1\50\1"+
			"\67\1\uffff\1\70\3\uffff\1\54\2\uffff\1\47\2\uffff\1\54\4\uffff\1\42"+
			"\1\56\1\60",
			"",
			"\1\72\10\uffff\1\72",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\72\10\uffff\1\72",
			"",
			"\1\72\10\uffff\1\72",
			"",
			"\1\72\10\uffff\1\72",
			"\1\72\10\uffff\1\72",
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
			"\1\74\10\uffff\1\74",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\74\10\uffff\1\74",
			"",
			"\1\74\10\uffff\1\74",
			"",
			"\1\74\10\uffff\1\74",
			"\1\74\10\uffff\1\74",
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
			"\1\102\1\101\1\112\1\106\1\uffff\1\100\1\uffff\1\117\1\uffff\2\114\5"+
			"\uffff\1\117\2\uffff\1\120\2\uffff\1\110\23\uffff\1\114\1\uffff\1\116"+
			"\3\114\10\uffff\1\107\3\uffff\1\123\1\121\1\122\1\uffff\1\126\3\uffff"+
			"\1\76\1\uffff\1\105\1\124\1\uffff\1\125\3\uffff\1\111\2\uffff\1\104\2"+
			"\uffff\1\111\4\uffff\1\77\1\113\1\115",
			"",
			"\1\133\1\132\1\143\1\137\1\uffff\1\131\1\uffff\1\150\1\uffff\2\145\5"+
			"\uffff\1\150\2\uffff\1\151\2\uffff\1\141\23\uffff\1\145\1\uffff\1\147"+
			"\3\145\10\uffff\1\140\3\uffff\1\154\1\152\1\153\1\uffff\1\157\3\uffff"+
			"\1\127\1\uffff\1\136\1\155\1\uffff\1\156\3\uffff\1\142\2\uffff\1\135"+
			"\2\uffff\1\142\4\uffff\1\130\1\144\1\146",
			"",
			"\1\160\10\uffff\1\160",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\160\10\uffff\1\160",
			"",
			"\1\160\10\uffff\1\160",
			"",
			"\1\160\10\uffff\1\160",
			"\1\160\10\uffff\1\160",
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
			"\1\162\10\uffff\1\162",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\162\10\uffff\1\162",
			"",
			"\1\162\10\uffff\1\162",
			"",
			"\1\162\10\uffff\1\162",
			"\1\162\10\uffff\1\162",
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
			"\1\170\1\167\1\u0080\1\174\1\uffff\1\166\1\uffff\1\u0085\1\uffff\2\u0082"+
			"\5\uffff\1\u0085\2\uffff\1\u0086\2\uffff\1\176\23\uffff\1\u0082\1\uffff"+
			"\1\u0084\3\u0082\10\uffff\1\175\3\uffff\1\u0089\1\u0087\1\u0088\1\uffff"+
			"\1\u008c\3\uffff\1\164\1\uffff\1\173\1\u008a\1\uffff\1\u008b\3\uffff"+
			"\1\177\2\uffff\1\172\2\uffff\1\177\4\uffff\1\165\1\u0081\1\u0083",
			"",
			"\1\u0091\1\u0090\1\u0099\1\u0095\1\uffff\1\u008f\1\uffff\1\u009e\1\uffff"+
			"\2\u009b\5\uffff\1\u009e\2\uffff\1\u009f\2\uffff\1\u0097\23\uffff\1\u009b"+
			"\1\uffff\1\u009d\3\u009b\10\uffff\1\u0096\3\uffff\1\u00a2\1\u00a0\1\u00a1"+
			"\1\uffff\1\u00a5\3\uffff\1\u008d\1\uffff\1\u0094\1\u00a3\1\uffff\1\u00a4"+
			"\3\uffff\1\u0098\2\uffff\1\u0093\2\uffff\1\u0098\4\uffff\1\u008e\1\u009a"+
			"\1\u009c",
			"",
			"\1\u00a6\10\uffff\1\u00a6",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\u00a6\10\uffff\1\u00a6",
			"",
			"\1\u00a6\10\uffff\1\u00a6",
			"",
			"\1\u00a6\10\uffff\1\u00a6",
			"\1\u00a6\10\uffff\1\u00a6",
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
			"\1\u00a8\10\uffff\1\u00a8",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\u00a8\10\uffff\1\u00a8",
			"",
			"\1\u00a8\10\uffff\1\u00a8",
			"",
			"\1\u00a8\10\uffff\1\u00a8",
			"\1\u00a8\10\uffff\1\u00a8",
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
			"\1\u00ae\1\u00ad\1\u00b6\1\u00b2\1\uffff\1\u00ac\1\uffff\1\u00bb\1\uffff"+
			"\2\u00b8\5\uffff\1\u00bb\2\uffff\1\u00bc\2\uffff\1\u00b4\23\uffff\1\u00b8"+
			"\1\uffff\1\u00ba\3\u00b8\10\uffff\1\u00b3\3\uffff\1\u00bf\1\u00bd\1\u00be"+
			"\1\uffff\1\u00c2\3\uffff\1\u00aa\1\uffff\1\u00b1\1\u00c0\1\uffff\1\u00c1"+
			"\3\uffff\1\u00b5\2\uffff\1\u00b0\2\uffff\1\u00b5\4\uffff\1\u00ab\1\u00b7"+
			"\1\u00b9",
			"",
			"\1\u00c7\1\u00c6\1\u00cf\1\u00cb\1\uffff\1\u00c5\1\uffff\1\u00d4\1\uffff"+
			"\2\u00d1\5\uffff\1\u00d4\2\uffff\1\u00d5\2\uffff\1\u00cd\23\uffff\1\u00d1"+
			"\1\uffff\1\u00d3\3\u00d1\10\uffff\1\u00cc\3\uffff\1\u00d8\1\u00d6\1\u00d7"+
			"\1\uffff\1\u00db\3\uffff\1\u00c3\1\uffff\1\u00ca\1\u00d9\1\uffff\1\u00da"+
			"\3\uffff\1\u00ce\2\uffff\1\u00c9\2\uffff\1\u00ce\4\uffff\1\u00c4\1\u00d0"+
			"\1\u00d2",
			"",
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
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
			"\1\uffff",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			"",
			"\1\uffff",
			"\1\uffff",
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
			""
	};

	static final short[] DFA72_eot = DFA.unpackEncodedString(DFA72_eotS);
	static final short[] DFA72_eof = DFA.unpackEncodedString(DFA72_eofS);
	static final char[] DFA72_min = DFA.unpackEncodedStringToUnsignedChars(DFA72_minS);
	static final char[] DFA72_max = DFA.unpackEncodedStringToUnsignedChars(DFA72_maxS);
	static final short[] DFA72_accept = DFA.unpackEncodedString(DFA72_acceptS);
	static final short[] DFA72_special = DFA.unpackEncodedString(DFA72_specialS);
	static final short[][] DFA72_transition;

	static {
		int numStates = DFA72_transitionS.length;
		DFA72_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA72_transition[i] = DFA.unpackEncodedString(DFA72_transitionS[i]);
		}
	}

	protected class DFA72 extends DFA {

		public DFA72(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 72;
			this.eot = DFA72_eot;
			this.eof = DFA72_eof;
			this.min = DFA72_min;
			this.max = DFA72_max;
			this.accept = DFA72_accept;
			this.special = DFA72_special;
			this.transition = DFA72_transition;
		}
		@Override
		public String getDescription() {
			return "831:1: ftsWord : ( ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase DOT | COMMA ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ftsWordBase ( DOT | COMMA ) ftsWordBase )=> ftsWordBase ( DOT | COMMA ) ftsWordBase | ( ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) )=> ( DOT | COMMA ) ftsWordBase ( DOT | COMMA ) | ( ftsWordBase ( DOT | COMMA ) )=> ftsWordBase ( DOT | COMMA ) | ( DOT | COMMA ) ftsWordBase | ftsWordBase );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TokenStream input = (TokenStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA72_75 = input.LA(1);
						 
						int index72_75 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_75==COMMA||LA72_75==DOT) ) {s = 112;}
						else if ( (synpred38_FTS()) ) {s = 113;}
						else if ( (synpred40_FTS()) ) {s = 86;}
						 
						input.seek(index72_75);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA72_166 = input.LA(1);
						 
						int index72_166 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_166==NOT) ) {s = 170;}
						else if ( (LA72_166==TILDA) && (synpred32_FTS())) {s = 171;}
						else if ( (LA72_166==CARAT) && (synpred32_FTS())) {s = 172;}
						else if ( (LA72_166==AND) && (synpred32_FTS())) {s = 173;}
						else if ( (LA72_166==AMP) && (synpred32_FTS())) {s = 174;}
						else if ( (LA72_166==EOF) && (synpred32_FTS())) {s = 175;}
						else if ( (LA72_166==RPAREN) && (synpred32_FTS())) {s = 176;}
						else if ( (LA72_166==OR) && (synpred32_FTS())) {s = 177;}
						else if ( (LA72_166==BAR) && (synpred32_FTS())) {s = 178;}
						else if ( (LA72_166==ID) ) {s = 179;}
						else if ( (LA72_166==EXCLAMATION) && (synpred32_FTS())) {s = 180;}
						else if ( (LA72_166==QUESTION_MARK||LA72_166==STAR) ) {s = 181;}
						else if ( (LA72_166==AT) && (synpred32_FTS())) {s = 182;}
						else if ( (LA72_166==TO) ) {s = 183;}
						else if ( ((LA72_166 >= DATETIME && LA72_166 <= DECIMAL_INTEGER_LITERAL)||LA72_166==FLOATING_POINT_LITERAL||(LA72_166 >= FTSPRE && LA72_166 <= FTSWORD)) ) {s = 184;}
						else if ( (LA72_166==URI) && (synpred32_FTS())) {s = 185;}
						else if ( (LA72_166==FTSPHRASE) && (synpred32_FTS())) {s = 186;}
						else if ( (LA72_166==COMMA||LA72_166==DOT) && (synpred32_FTS())) {s = 187;}
						else if ( (LA72_166==EQUALS) && (synpred32_FTS())) {s = 188;}
						else if ( (LA72_166==LSQUARE) && (synpred32_FTS())) {s = 189;}
						else if ( (LA72_166==LT) && (synpred32_FTS())) {s = 190;}
						else if ( (LA72_166==LPAREN) && (synpred32_FTS())) {s = 191;}
						else if ( (LA72_166==PERCENT) && (synpred32_FTS())) {s = 192;}
						else if ( (LA72_166==PLUS) && (synpred32_FTS())) {s = 193;}
						else if ( (LA72_166==MINUS) && (synpred32_FTS())) {s = 194;}
						 
						input.seek(index72_166);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA72_6 = input.LA(1);
						 
						int index72_6 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_6==NOT) ) {s = 33;}
						else if ( (LA72_6==TILDA) && (synpred43_FTS())) {s = 34;}
						else if ( (LA72_6==CARAT) && (synpred43_FTS())) {s = 35;}
						else if ( (LA72_6==AND) && (synpred43_FTS())) {s = 36;}
						else if ( (LA72_6==AMP) && (synpred43_FTS())) {s = 37;}
						else if ( (LA72_6==EOF) && (synpred43_FTS())) {s = 38;}
						else if ( (LA72_6==RPAREN) && (synpred43_FTS())) {s = 39;}
						else if ( (LA72_6==OR) && (synpred43_FTS())) {s = 40;}
						else if ( (LA72_6==BAR) && (synpred43_FTS())) {s = 41;}
						else if ( (LA72_6==ID) ) {s = 42;}
						else if ( (LA72_6==EXCLAMATION) && (synpred43_FTS())) {s = 43;}
						else if ( (LA72_6==QUESTION_MARK||LA72_6==STAR) ) {s = 44;}
						else if ( (LA72_6==AT) && (synpred43_FTS())) {s = 45;}
						else if ( (LA72_6==TO) ) {s = 46;}
						else if ( ((LA72_6 >= DATETIME && LA72_6 <= DECIMAL_INTEGER_LITERAL)||LA72_6==FLOATING_POINT_LITERAL||(LA72_6 >= FTSPRE && LA72_6 <= FTSWORD)) ) {s = 47;}
						else if ( (LA72_6==URI) && (synpred43_FTS())) {s = 48;}
						else if ( (LA72_6==FTSPHRASE) && (synpred43_FTS())) {s = 49;}
						else if ( (LA72_6==COMMA||LA72_6==DOT) && (synpred43_FTS())) {s = 50;}
						else if ( (LA72_6==EQUALS) && (synpred43_FTS())) {s = 51;}
						else if ( (LA72_6==LSQUARE) && (synpred43_FTS())) {s = 52;}
						else if ( (LA72_6==LT) && (synpred43_FTS())) {s = 53;}
						else if ( (LA72_6==LPAREN) && (synpred43_FTS())) {s = 54;}
						else if ( (LA72_6==PERCENT) && (synpred43_FTS())) {s = 55;}
						else if ( (LA72_6==PLUS) && (synpred43_FTS())) {s = 56;}
						else if ( (LA72_6==MINUS) && (synpred43_FTS())) {s = 57;}
						 
						input.seek(index72_6);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA72_204 = input.LA(1);
						 
						int index72_204 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred29_FTS()) ) {s = 221;}
						else if ( (synpred31_FTS()) ) {s = 219;}
						 
						input.seek(index72_204);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA72_206 = input.LA(1);
						 
						int index72_206 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred29_FTS()) ) {s = 221;}
						else if ( (synpred31_FTS()) ) {s = 219;}
						 
						input.seek(index72_206);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA72_168 = input.LA(1);
						 
						int index72_168 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_168==NOT) ) {s = 195;}
						else if ( (LA72_168==TILDA) && (synpred31_FTS())) {s = 196;}
						else if ( (LA72_168==CARAT) && (synpred31_FTS())) {s = 197;}
						else if ( (LA72_168==AND) && (synpred31_FTS())) {s = 198;}
						else if ( (LA72_168==AMP) && (synpred31_FTS())) {s = 199;}
						else if ( (LA72_168==EOF) && (synpred31_FTS())) {s = 200;}
						else if ( (LA72_168==RPAREN) && (synpred31_FTS())) {s = 201;}
						else if ( (LA72_168==OR) && (synpred31_FTS())) {s = 202;}
						else if ( (LA72_168==BAR) && (synpred31_FTS())) {s = 203;}
						else if ( (LA72_168==ID) ) {s = 204;}
						else if ( (LA72_168==EXCLAMATION) && (synpred31_FTS())) {s = 205;}
						else if ( (LA72_168==QUESTION_MARK||LA72_168==STAR) ) {s = 206;}
						else if ( (LA72_168==AT) && (synpred31_FTS())) {s = 207;}
						else if ( (LA72_168==TO) ) {s = 208;}
						else if ( ((LA72_168 >= DATETIME && LA72_168 <= DECIMAL_INTEGER_LITERAL)||LA72_168==FLOATING_POINT_LITERAL||(LA72_168 >= FTSPRE && LA72_168 <= FTSWORD)) ) {s = 209;}
						else if ( (LA72_168==URI) && (synpred31_FTS())) {s = 210;}
						else if ( (LA72_168==FTSPHRASE) && (synpred31_FTS())) {s = 211;}
						else if ( (LA72_168==COMMA||LA72_168==DOT) && (synpred31_FTS())) {s = 212;}
						else if ( (LA72_168==EQUALS) && (synpred31_FTS())) {s = 213;}
						else if ( (LA72_168==LSQUARE) && (synpred31_FTS())) {s = 214;}
						else if ( (LA72_168==LT) && (synpred31_FTS())) {s = 215;}
						else if ( (LA72_168==LPAREN) && (synpred31_FTS())) {s = 216;}
						else if ( (LA72_168==PERCENT) && (synpred31_FTS())) {s = 217;}
						else if ( (LA72_168==PLUS) && (synpred31_FTS())) {s = 218;}
						else if ( (LA72_168==MINUS) && (synpred31_FTS())) {s = 219;}
						 
						input.seek(index72_168);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA72_170 = input.LA(1);
						 
						int index72_170 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred30_FTS()) ) {s = 220;}
						else if ( (synpred32_FTS()) ) {s = 194;}
						 
						input.seek(index72_170);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA72_179 = input.LA(1);
						 
						int index72_179 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred30_FTS()) ) {s = 220;}
						else if ( (synpred32_FTS()) ) {s = 194;}
						 
						input.seek(index72_179);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA72_155 = input.LA(1);
						 
						int index72_155 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_155==COMMA||LA72_155==DOT) ) {s = 168;}
						else if ( (synpred33_FTS()) ) {s = 169;}
						else if ( (synpred35_FTS()) ) {s = 165;}
						 
						input.seek(index72_155);
						if ( s>=0 ) return s;
						break;

					case 9 : 
						int LA72_76 = input.LA(1);
						 
						int index72_76 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_76==COMMA||LA72_76==DOT) ) {s = 112;}
						else if ( (synpred38_FTS()) ) {s = 113;}
						else if ( (synpred40_FTS()) ) {s = 86;}
						 
						input.seek(index72_76);
						if ( s>=0 ) return s;
						break;

					case 10 : 
						int LA72_150 = input.LA(1);
						 
						int index72_150 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_150==COMMA||LA72_150==DOT) ) {s = 168;}
						else if ( (synpred33_FTS()) ) {s = 169;}
						else if ( (synpred35_FTS()) ) {s = 165;}
						 
						input.seek(index72_150);
						if ( s>=0 ) return s;
						break;

					case 11 : 
						int LA72_42 = input.LA(1);
						 
						int index72_42 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_42==COMMA||LA72_42==DOT) ) {s = 60;}
						else if ( (synpred41_FTS()) ) {s = 61;}
						else if ( (synpred43_FTS()) ) {s = 57;}
						else if ( (true) ) {s = 7;}
						 
						input.seek(index72_42);
						if ( s>=0 ) return s;
						break;

					case 12 : 
						int LA72_152 = input.LA(1);
						 
						int index72_152 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_152==COMMA||LA72_152==DOT) ) {s = 168;}
						else if ( (synpred33_FTS()) ) {s = 169;}
						else if ( (synpred35_FTS()) ) {s = 165;}
						 
						input.seek(index72_152);
						if ( s>=0 ) return s;
						break;

					case 13 : 
						int LA72_183 = input.LA(1);
						 
						int index72_183 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred30_FTS()) ) {s = 220;}
						else if ( (synpred32_FTS()) ) {s = 194;}
						 
						input.seek(index72_183);
						if ( s>=0 ) return s;
						break;

					case 14 : 
						int LA72_87 = input.LA(1);
						 
						int index72_87 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_87==COMMA||LA72_87==DOT) ) {s = 114;}
						else if ( (synpred37_FTS()) ) {s = 115;}
						else if ( (synpred39_FTS()) ) {s = 111;}
						 
						input.seek(index72_87);
						if ( s>=0 ) return s;
						break;

					case 15 : 
						int LA72_129 = input.LA(1);
						 
						int index72_129 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_129==COMMA||LA72_129==DOT) ) {s = 166;}
						else if ( (synpred34_FTS()) ) {s = 167;}
						else if ( (synpred36_FTS()) ) {s = 140;}
						 
						input.seek(index72_129);
						if ( s>=0 ) return s;
						break;

					case 16 : 
						int LA72_46 = input.LA(1);
						 
						int index72_46 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_46==COMMA||LA72_46==DOT) ) {s = 60;}
						else if ( (synpred41_FTS()) ) {s = 61;}
						else if ( (synpred43_FTS()) ) {s = 57;}
						else if ( (true) ) {s = 7;}
						 
						input.seek(index72_46);
						if ( s>=0 ) return s;
						break;

					case 17 : 
						int LA72_62 = input.LA(1);
						 
						int index72_62 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_62==COMMA||LA72_62==DOT) ) {s = 112;}
						else if ( (synpred38_FTS()) ) {s = 113;}
						else if ( (synpred40_FTS()) ) {s = 86;}
						 
						input.seek(index72_62);
						if ( s>=0 ) return s;
						break;

					case 18 : 
						int LA72_73 = input.LA(1);
						 
						int index72_73 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_73==COMMA||LA72_73==DOT) ) {s = 112;}
						else if ( (synpred38_FTS()) ) {s = 113;}
						else if ( (synpred40_FTS()) ) {s = 86;}
						 
						input.seek(index72_73);
						if ( s>=0 ) return s;
						break;

					case 19 : 
						int LA72_71 = input.LA(1);
						 
						int index72_71 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_71==COMMA||LA72_71==DOT) ) {s = 112;}
						else if ( (synpred38_FTS()) ) {s = 113;}
						else if ( (synpred40_FTS()) ) {s = 86;}
						 
						input.seek(index72_71);
						if ( s>=0 ) return s;
						break;

					case 20 : 
						int LA72_8 = input.LA(1);
						 
						int index72_8 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_8==COMMA||LA72_8==DOT) ) {s = 58;}
						else if ( (synpred42_FTS()) ) {s = 59;}
						else if ( (synpred44_FTS()) ) {s = 32;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index72_8);
						if ( s>=0 ) return s;
						break;

					case 21 : 
						int LA72_47 = input.LA(1);
						 
						int index72_47 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_47==COMMA||LA72_47==DOT) ) {s = 60;}
						else if ( (synpred41_FTS()) ) {s = 61;}
						else if ( (synpred43_FTS()) ) {s = 57;}
						else if ( (true) ) {s = 7;}
						 
						input.seek(index72_47);
						if ( s>=0 ) return s;
						break;

					case 22 : 
						int LA72_114 = input.LA(1);
						 
						int index72_114 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_114==NOT) ) {s = 141;}
						else if ( (LA72_114==TILDA) && (synpred35_FTS())) {s = 142;}
						else if ( (LA72_114==CARAT) && (synpred35_FTS())) {s = 143;}
						else if ( (LA72_114==AND) && (synpred35_FTS())) {s = 144;}
						else if ( (LA72_114==AMP) && (synpred35_FTS())) {s = 145;}
						else if ( (LA72_114==EOF) && (synpred35_FTS())) {s = 146;}
						else if ( (LA72_114==RPAREN) && (synpred35_FTS())) {s = 147;}
						else if ( (LA72_114==OR) && (synpred35_FTS())) {s = 148;}
						else if ( (LA72_114==BAR) && (synpred35_FTS())) {s = 149;}
						else if ( (LA72_114==ID) ) {s = 150;}
						else if ( (LA72_114==EXCLAMATION) && (synpred35_FTS())) {s = 151;}
						else if ( (LA72_114==QUESTION_MARK||LA72_114==STAR) ) {s = 152;}
						else if ( (LA72_114==AT) && (synpred35_FTS())) {s = 153;}
						else if ( (LA72_114==TO) ) {s = 154;}
						else if ( ((LA72_114 >= DATETIME && LA72_114 <= DECIMAL_INTEGER_LITERAL)||LA72_114==FLOATING_POINT_LITERAL||(LA72_114 >= FTSPRE && LA72_114 <= FTSWORD)) ) {s = 155;}
						else if ( (LA72_114==URI) && (synpred35_FTS())) {s = 156;}
						else if ( (LA72_114==FTSPHRASE) && (synpred35_FTS())) {s = 157;}
						else if ( (LA72_114==COMMA||LA72_114==DOT) && (synpred35_FTS())) {s = 158;}
						else if ( (LA72_114==EQUALS) && (synpred35_FTS())) {s = 159;}
						else if ( (LA72_114==LSQUARE) && (synpred35_FTS())) {s = 160;}
						else if ( (LA72_114==LT) && (synpred35_FTS())) {s = 161;}
						else if ( (LA72_114==LPAREN) && (synpred35_FTS())) {s = 162;}
						else if ( (LA72_114==PERCENT) && (synpred35_FTS())) {s = 163;}
						else if ( (LA72_114==PLUS) && (synpred35_FTS())) {s = 164;}
						else if ( (LA72_114==MINUS) && (synpred35_FTS())) {s = 165;}
						 
						input.seek(index72_114);
						if ( s>=0 ) return s;
						break;

					case 23 : 
						int LA72_58 = input.LA(1);
						 
						int index72_58 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_58==NOT) ) {s = 62;}
						else if ( (LA72_58==TILDA) && (synpred40_FTS())) {s = 63;}
						else if ( (LA72_58==CARAT) && (synpred40_FTS())) {s = 64;}
						else if ( (LA72_58==AND) && (synpred40_FTS())) {s = 65;}
						else if ( (LA72_58==AMP) && (synpred40_FTS())) {s = 66;}
						else if ( (LA72_58==EOF) && (synpred40_FTS())) {s = 67;}
						else if ( (LA72_58==RPAREN) && (synpred40_FTS())) {s = 68;}
						else if ( (LA72_58==OR) && (synpred40_FTS())) {s = 69;}
						else if ( (LA72_58==BAR) && (synpred40_FTS())) {s = 70;}
						else if ( (LA72_58==ID) ) {s = 71;}
						else if ( (LA72_58==EXCLAMATION) && (synpred40_FTS())) {s = 72;}
						else if ( (LA72_58==QUESTION_MARK||LA72_58==STAR) ) {s = 73;}
						else if ( (LA72_58==AT) && (synpred40_FTS())) {s = 74;}
						else if ( (LA72_58==TO) ) {s = 75;}
						else if ( ((LA72_58 >= DATETIME && LA72_58 <= DECIMAL_INTEGER_LITERAL)||LA72_58==FLOATING_POINT_LITERAL||(LA72_58 >= FTSPRE && LA72_58 <= FTSWORD)) ) {s = 76;}
						else if ( (LA72_58==URI) && (synpred40_FTS())) {s = 77;}
						else if ( (LA72_58==FTSPHRASE) && (synpred40_FTS())) {s = 78;}
						else if ( (LA72_58==COMMA||LA72_58==DOT) && (synpred40_FTS())) {s = 79;}
						else if ( (LA72_58==EQUALS) && (synpred40_FTS())) {s = 80;}
						else if ( (LA72_58==LSQUARE) && (synpred40_FTS())) {s = 81;}
						else if ( (LA72_58==LT) && (synpred40_FTS())) {s = 82;}
						else if ( (LA72_58==LPAREN) && (synpred40_FTS())) {s = 83;}
						else if ( (LA72_58==PERCENT) && (synpred40_FTS())) {s = 84;}
						else if ( (LA72_58==PLUS) && (synpred40_FTS())) {s = 85;}
						else if ( (LA72_58==MINUS) && (synpred40_FTS())) {s = 86;}
						 
						input.seek(index72_58);
						if ( s>=0 ) return s;
						break;

					case 24 : 
						int LA72_208 = input.LA(1);
						 
						int index72_208 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred29_FTS()) ) {s = 221;}
						else if ( (synpred31_FTS()) ) {s = 219;}
						 
						input.seek(index72_208);
						if ( s>=0 ) return s;
						break;

					case 25 : 
						int LA72_22 = input.LA(1);
						 
						int index72_22 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_22==COMMA||LA72_22==DOT) ) {s = 58;}
						else if ( (synpred42_FTS()) ) {s = 59;}
						else if ( (synpred44_FTS()) ) {s = 32;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index72_22);
						if ( s>=0 ) return s;
						break;

					case 26 : 
						int LA72_127 = input.LA(1);
						 
						int index72_127 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_127==COMMA||LA72_127==DOT) ) {s = 166;}
						else if ( (synpred34_FTS()) ) {s = 167;}
						else if ( (synpred36_FTS()) ) {s = 140;}
						 
						input.seek(index72_127);
						if ( s>=0 ) return s;
						break;

					case 27 : 
						int LA72_112 = input.LA(1);
						 
						int index72_112 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_112==NOT) ) {s = 116;}
						else if ( (LA72_112==TILDA) && (synpred36_FTS())) {s = 117;}
						else if ( (LA72_112==CARAT) && (synpred36_FTS())) {s = 118;}
						else if ( (LA72_112==AND) && (synpred36_FTS())) {s = 119;}
						else if ( (LA72_112==AMP) && (synpred36_FTS())) {s = 120;}
						else if ( (LA72_112==EOF) && (synpred36_FTS())) {s = 121;}
						else if ( (LA72_112==RPAREN) && (synpred36_FTS())) {s = 122;}
						else if ( (LA72_112==OR) && (synpred36_FTS())) {s = 123;}
						else if ( (LA72_112==BAR) && (synpred36_FTS())) {s = 124;}
						else if ( (LA72_112==ID) ) {s = 125;}
						else if ( (LA72_112==EXCLAMATION) && (synpred36_FTS())) {s = 126;}
						else if ( (LA72_112==QUESTION_MARK||LA72_112==STAR) ) {s = 127;}
						else if ( (LA72_112==AT) && (synpred36_FTS())) {s = 128;}
						else if ( (LA72_112==TO) ) {s = 129;}
						else if ( ((LA72_112 >= DATETIME && LA72_112 <= DECIMAL_INTEGER_LITERAL)||LA72_112==FLOATING_POINT_LITERAL||(LA72_112 >= FTSPRE && LA72_112 <= FTSWORD)) ) {s = 130;}
						else if ( (LA72_112==URI) && (synpred36_FTS())) {s = 131;}
						else if ( (LA72_112==FTSPHRASE) && (synpred36_FTS())) {s = 132;}
						else if ( (LA72_112==COMMA||LA72_112==DOT) && (synpred36_FTS())) {s = 133;}
						else if ( (LA72_112==EQUALS) && (synpred36_FTS())) {s = 134;}
						else if ( (LA72_112==LSQUARE) && (synpred36_FTS())) {s = 135;}
						else if ( (LA72_112==LT) && (synpred36_FTS())) {s = 136;}
						else if ( (LA72_112==LPAREN) && (synpred36_FTS())) {s = 137;}
						else if ( (LA72_112==PERCENT) && (synpred36_FTS())) {s = 138;}
						else if ( (LA72_112==PLUS) && (synpred36_FTS())) {s = 139;}
						else if ( (LA72_112==MINUS) && (synpred36_FTS())) {s = 140;}
						 
						input.seek(index72_112);
						if ( s>=0 ) return s;
						break;

					case 28 : 
						int LA72_184 = input.LA(1);
						 
						int index72_184 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred30_FTS()) ) {s = 220;}
						else if ( (synpred32_FTS()) ) {s = 194;}
						 
						input.seek(index72_184);
						if ( s>=0 ) return s;
						break;

					case 29 : 
						int LA72_19 = input.LA(1);
						 
						int index72_19 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_19==COMMA||LA72_19==DOT) ) {s = 58;}
						else if ( (synpred42_FTS()) ) {s = 59;}
						else if ( (synpred44_FTS()) ) {s = 32;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index72_19);
						if ( s>=0 ) return s;
						break;

					case 30 : 
						int LA72_60 = input.LA(1);
						 
						int index72_60 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_60==NOT) ) {s = 87;}
						else if ( (LA72_60==TILDA) && (synpred39_FTS())) {s = 88;}
						else if ( (LA72_60==CARAT) && (synpred39_FTS())) {s = 89;}
						else if ( (LA72_60==AND) && (synpred39_FTS())) {s = 90;}
						else if ( (LA72_60==AMP) && (synpred39_FTS())) {s = 91;}
						else if ( (LA72_60==EOF) && (synpred39_FTS())) {s = 92;}
						else if ( (LA72_60==RPAREN) && (synpred39_FTS())) {s = 93;}
						else if ( (LA72_60==OR) && (synpred39_FTS())) {s = 94;}
						else if ( (LA72_60==BAR) && (synpred39_FTS())) {s = 95;}
						else if ( (LA72_60==ID) ) {s = 96;}
						else if ( (LA72_60==EXCLAMATION) && (synpred39_FTS())) {s = 97;}
						else if ( (LA72_60==QUESTION_MARK||LA72_60==STAR) ) {s = 98;}
						else if ( (LA72_60==AT) && (synpred39_FTS())) {s = 99;}
						else if ( (LA72_60==TO) ) {s = 100;}
						else if ( ((LA72_60 >= DATETIME && LA72_60 <= DECIMAL_INTEGER_LITERAL)||LA72_60==FLOATING_POINT_LITERAL||(LA72_60 >= FTSPRE && LA72_60 <= FTSWORD)) ) {s = 101;}
						else if ( (LA72_60==URI) && (synpred39_FTS())) {s = 102;}
						else if ( (LA72_60==FTSPHRASE) && (synpred39_FTS())) {s = 103;}
						else if ( (LA72_60==COMMA||LA72_60==DOT) && (synpred39_FTS())) {s = 104;}
						else if ( (LA72_60==EQUALS) && (synpred39_FTS())) {s = 105;}
						else if ( (LA72_60==LSQUARE) && (synpred39_FTS())) {s = 106;}
						else if ( (LA72_60==LT) && (synpred39_FTS())) {s = 107;}
						else if ( (LA72_60==LPAREN) && (synpred39_FTS())) {s = 108;}
						else if ( (LA72_60==PERCENT) && (synpred39_FTS())) {s = 109;}
						else if ( (LA72_60==PLUS) && (synpred39_FTS())) {s = 110;}
						else if ( (LA72_60==MINUS) && (synpred39_FTS())) {s = 111;}
						 
						input.seek(index72_60);
						if ( s>=0 ) return s;
						break;

					case 31 : 
						int LA72_100 = input.LA(1);
						 
						int index72_100 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_100==COMMA||LA72_100==DOT) ) {s = 114;}
						else if ( (synpred37_FTS()) ) {s = 115;}
						else if ( (synpred39_FTS()) ) {s = 111;}
						 
						input.seek(index72_100);
						if ( s>=0 ) return s;
						break;

					case 32 : 
						int LA72_125 = input.LA(1);
						 
						int index72_125 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_125==COMMA||LA72_125==DOT) ) {s = 166;}
						else if ( (synpred34_FTS()) ) {s = 167;}
						else if ( (synpred36_FTS()) ) {s = 140;}
						 
						input.seek(index72_125);
						if ( s>=0 ) return s;
						break;

					case 33 : 
						int LA72_96 = input.LA(1);
						 
						int index72_96 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_96==COMMA||LA72_96==DOT) ) {s = 114;}
						else if ( (synpred37_FTS()) ) {s = 115;}
						else if ( (synpred39_FTS()) ) {s = 111;}
						 
						input.seek(index72_96);
						if ( s>=0 ) return s;
						break;

					case 34 : 
						int LA72_209 = input.LA(1);
						 
						int index72_209 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred29_FTS()) ) {s = 221;}
						else if ( (synpred31_FTS()) ) {s = 219;}
						 
						input.seek(index72_209);
						if ( s>=0 ) return s;
						break;

					case 35 : 
						int LA72_44 = input.LA(1);
						 
						int index72_44 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_44==COMMA||LA72_44==DOT) ) {s = 60;}
						else if ( (synpred41_FTS()) ) {s = 61;}
						else if ( (synpred43_FTS()) ) {s = 57;}
						else if ( (true) ) {s = 7;}
						 
						input.seek(index72_44);
						if ( s>=0 ) return s;
						break;

					case 36 : 
						int LA72_195 = input.LA(1);
						 
						int index72_195 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred29_FTS()) ) {s = 221;}
						else if ( (synpred31_FTS()) ) {s = 219;}
						 
						input.seek(index72_195);
						if ( s>=0 ) return s;
						break;

					case 37 : 
						int LA72_21 = input.LA(1);
						 
						int index72_21 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_21==COMMA||LA72_21==DOT) ) {s = 58;}
						else if ( (synpred42_FTS()) ) {s = 59;}
						else if ( (synpred44_FTS()) ) {s = 32;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index72_21);
						if ( s>=0 ) return s;
						break;

					case 38 : 
						int LA72_130 = input.LA(1);
						 
						int index72_130 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_130==COMMA||LA72_130==DOT) ) {s = 166;}
						else if ( (synpred34_FTS()) ) {s = 167;}
						else if ( (synpred36_FTS()) ) {s = 140;}
						 
						input.seek(index72_130);
						if ( s>=0 ) return s;
						break;

					case 39 : 
						int LA72_33 = input.LA(1);
						 
						int index72_33 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_33==COMMA||LA72_33==DOT) ) {s = 60;}
						else if ( (synpred41_FTS()) ) {s = 61;}
						else if ( (synpred43_FTS()) ) {s = 57;}
						else if ( (true) ) {s = 7;}
						 
						input.seek(index72_33);
						if ( s>=0 ) return s;
						break;

					case 40 : 
						int LA72_101 = input.LA(1);
						 
						int index72_101 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_101==COMMA||LA72_101==DOT) ) {s = 114;}
						else if ( (synpred37_FTS()) ) {s = 115;}
						else if ( (synpred39_FTS()) ) {s = 111;}
						 
						input.seek(index72_101);
						if ( s>=0 ) return s;
						break;

					case 41 : 
						int LA72_181 = input.LA(1);
						 
						int index72_181 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred30_FTS()) ) {s = 220;}
						else if ( (synpred32_FTS()) ) {s = 194;}
						 
						input.seek(index72_181);
						if ( s>=0 ) return s;
						break;

					case 42 : 
						int LA72_154 = input.LA(1);
						 
						int index72_154 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_154==COMMA||LA72_154==DOT) ) {s = 168;}
						else if ( (synpred33_FTS()) ) {s = 169;}
						else if ( (synpred35_FTS()) ) {s = 165;}
						 
						input.seek(index72_154);
						if ( s>=0 ) return s;
						break;

					case 43 : 
						int LA72_141 = input.LA(1);
						 
						int index72_141 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_141==COMMA||LA72_141==DOT) ) {s = 168;}
						else if ( (synpred33_FTS()) ) {s = 169;}
						else if ( (synpred35_FTS()) ) {s = 165;}
						 
						input.seek(index72_141);
						if ( s>=0 ) return s;
						break;

					case 44 : 
						int LA72_116 = input.LA(1);
						 
						int index72_116 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_116==COMMA||LA72_116==DOT) ) {s = 166;}
						else if ( (synpred34_FTS()) ) {s = 167;}
						else if ( (synpred36_FTS()) ) {s = 140;}
						 
						input.seek(index72_116);
						if ( s>=0 ) return s;
						break;

					case 45 : 
						int LA72_4 = input.LA(1);
						 
						int index72_4 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_4==NOT) ) {s = 8;}
						else if ( (LA72_4==TILDA) && (synpred44_FTS())) {s = 9;}
						else if ( (LA72_4==CARAT) && (synpred44_FTS())) {s = 10;}
						else if ( (LA72_4==AND) && (synpred44_FTS())) {s = 11;}
						else if ( (LA72_4==AMP) && (synpred44_FTS())) {s = 12;}
						else if ( (LA72_4==EOF) && (synpred44_FTS())) {s = 13;}
						else if ( (LA72_4==RPAREN) && (synpred44_FTS())) {s = 14;}
						else if ( (LA72_4==OR) && (synpred44_FTS())) {s = 15;}
						else if ( (LA72_4==BAR) && (synpred44_FTS())) {s = 16;}
						else if ( (LA72_4==ID) ) {s = 17;}
						else if ( (LA72_4==EXCLAMATION) && (synpred44_FTS())) {s = 18;}
						else if ( (LA72_4==QUESTION_MARK||LA72_4==STAR) ) {s = 19;}
						else if ( (LA72_4==AT) && (synpred44_FTS())) {s = 20;}
						else if ( (LA72_4==TO) ) {s = 21;}
						else if ( ((LA72_4 >= DATETIME && LA72_4 <= DECIMAL_INTEGER_LITERAL)||LA72_4==FLOATING_POINT_LITERAL||(LA72_4 >= FTSPRE && LA72_4 <= FTSWORD)) ) {s = 22;}
						else if ( (LA72_4==URI) && (synpred44_FTS())) {s = 23;}
						else if ( (LA72_4==FTSPHRASE) && (synpred44_FTS())) {s = 24;}
						else if ( (LA72_4==COMMA||LA72_4==DOT) && (synpred44_FTS())) {s = 25;}
						else if ( (LA72_4==EQUALS) && (synpred44_FTS())) {s = 26;}
						else if ( (LA72_4==LSQUARE) && (synpred44_FTS())) {s = 27;}
						else if ( (LA72_4==LT) && (synpred44_FTS())) {s = 28;}
						else if ( (LA72_4==LPAREN) && (synpred44_FTS())) {s = 29;}
						else if ( (LA72_4==PERCENT) && (synpred44_FTS())) {s = 30;}
						else if ( (LA72_4==PLUS) && (synpred44_FTS())) {s = 31;}
						else if ( (LA72_4==MINUS) && (synpred44_FTS())) {s = 32;}
						 
						input.seek(index72_4);
						if ( s>=0 ) return s;
						break;

					case 46 : 
						int LA72_98 = input.LA(1);
						 
						int index72_98 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_98==COMMA||LA72_98==DOT) ) {s = 114;}
						else if ( (synpred37_FTS()) ) {s = 115;}
						else if ( (synpred39_FTS()) ) {s = 111;}
						 
						input.seek(index72_98);
						if ( s>=0 ) return s;
						break;

					case 47 : 
						int LA72_17 = input.LA(1);
						 
						int index72_17 = input.index();
						input.rewind();
						s = -1;
						if ( (LA72_17==COMMA||LA72_17==DOT) ) {s = 58;}
						else if ( (synpred42_FTS()) ) {s = 59;}
						else if ( (synpred44_FTS()) ) {s = 32;}
						else if ( (true) ) {s = 5;}
						 
						input.seek(index72_17);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 72, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	public static final BitSet FOLLOW_ftsDisjunction_in_ftsQuery577 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_ftsQuery579 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisExplicitDisjunction_in_ftsDisjunction639 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsExplicitDisjunction_in_ftsDisjunction653 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsImplicitDisjunction_in_ftsDisjunction667 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction700 = new BitSet(new long[]{0x0000000000000082L,0x0000000000000400L});
	public static final BitSet FOLLOW_or_in_ftsExplicitDisjunction703 = new BitSet(new long[]{0x100F4000049068F0L,0x0000000070822D17L});
	public static final BitSet FOLLOW_ftsImplicitConjunction_in_ftsExplicitDisjunction705 = new BitSet(new long[]{0x0000000000000082L,0x0000000000000400L});
	public static final BitSet FOLLOW_cmisConjunction_in_cmisExplicitDisjunction789 = new BitSet(new long[]{0x0000000000000082L,0x0000000000000400L});
	public static final BitSet FOLLOW_or_in_cmisExplicitDisjunction792 = new BitSet(new long[]{0x100F400000106800L,0x0000000020820110L});
	public static final BitSet FOLLOW_cmisConjunction_in_cmisExplicitDisjunction794 = new BitSet(new long[]{0x0000000000000082L,0x0000000000000400L});
	public static final BitSet FOLLOW_or_in_ftsImplicitDisjunction879 = new BitSet(new long[]{0x100F4000049068E0L,0x0000000070822D17L});
	public static final BitSet FOLLOW_ftsExplicitConjunction_in_ftsImplicitDisjunction882 = new BitSet(new long[]{0x100F4000049068E2L,0x0000000070822D17L});
	public static final BitSet FOLLOW_ftsPrefixed_in_ftsExplicitConjunction969 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsExplicitConjunction972 = new BitSet(new long[]{0x100F4000049068E0L,0x0000000070822D17L});
	public static final BitSet FOLLOW_ftsPrefixed_in_ftsExplicitConjunction974 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsImplicitConjunction1059 = new BitSet(new long[]{0x100F4000049068E0L,0x0000000070822D17L});
	public static final BitSet FOLLOW_ftsPrefixed_in_ftsImplicitConjunction1062 = new BitSet(new long[]{0x100F4000049068F2L,0x0000000070822D17L});
	public static final BitSet FOLLOW_cmisPrefixed_in_cmisConjunction1146 = new BitSet(new long[]{0x100F400000106802L,0x0000000020820110L});
	public static final BitSet FOLLOW_not_in_ftsPrefixed1238 = new BitSet(new long[]{0x100F400000906860L,0x0000000070820D07L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1240 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1242 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1306 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1308 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_in_ftsPrefixed1372 = new BitSet(new long[]{0x100F400000906860L,0x0000000070820D07L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1374 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1376 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BAR_in_ftsPrefixed1440 = new BitSet(new long[]{0x100F400000906860L,0x0000000070820D07L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1442 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1444 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_ftsPrefixed1508 = new BitSet(new long[]{0x100F400000906860L,0x0000000070820D07L});
	public static final BitSet FOLLOW_ftsTest_in_ftsPrefixed1510 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsPrefixed1512 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisTest_in_cmisPrefixed1597 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_cmisPrefixed1657 = new BitSet(new long[]{0x100F400000106800L,0x0000000020820100L});
	public static final BitSet FOLLOW_cmisTest_in_cmisPrefixed1659 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_ftsTest1751 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTermOrPhrase_in_ftsTest1822 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsExactTermOrPhrase_in_ftsTest1845 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsTokenisedTermOrPhrase_in_ftsTest1869 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsRange_in_ftsTest1892 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroup_in_ftsTest1965 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_ftsTest1998 = new BitSet(new long[]{0x100F4000049068F0L,0x0000000070822D17L});
	public static final BitSet FOLLOW_ftsDisjunction_in_ftsTest2000 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
	public static final BitSet FOLLOW_RPAREN_in_ftsTest2002 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_template_in_ftsTest2035 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisTerm_in_cmisTest2088 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_cmisPhrase_in_cmisTest2148 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PERCENT_in_template2229 = new BitSet(new long[]{0x1000000000000060L,0x0000000060000500L});
	public static final BitSet FOLLOW_tempReference_in_template2231 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PERCENT_in_template2291 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_LPAREN_in_template2293 = new BitSet(new long[]{0x1000000000000060L,0x0000000060000500L});
	public static final BitSet FOLLOW_tempReference_in_template2296 = new BitSet(new long[]{0x1000000000000860L,0x0000000060100500L});
	public static final BitSet FOLLOW_COMMA_in_template2298 = new BitSet(new long[]{0x1000000000000060L,0x0000000060100500L});
	public static final BitSet FOLLOW_RPAREN_in_template2303 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_fuzzy2385 = new BitSet(new long[]{0x0000400000004000L});
	public static final BitSet FOLLOW_number_in_fuzzy2387 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_slop2468 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_DECIMAL_INTEGER_LITERAL_in_slop2470 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CARAT_in_boost2551 = new BitSet(new long[]{0x0000400000004000L});
	public static final BitSet FOLLOW_number_in_boost2553 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_ftsTermOrPhrase2642 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsTermOrPhrase2644 = new BitSet(new long[]{0x100F400000106800L,0x0000000020820100L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2672 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsTermOrPhrase2680 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTermOrPhrase2747 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTermOrPhrase2756 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTermOrPhrase2817 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsTermOrPhrase2825 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTermOrPhrase2875 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTermOrPhrase2884 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_ftsExactTermOrPhrase2963 = new BitSet(new long[]{0x100F400000106860L,0x0000000060820500L});
	public static final BitSet FOLLOW_fieldReference_in_ftsExactTermOrPhrase2991 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsExactTermOrPhrase2993 = new BitSet(new long[]{0x100F400000106800L,0x0000000020820100L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3021 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsExactTermOrPhrase3029 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsExactTermOrPhrase3096 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsExactTermOrPhrase3105 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsExactTermOrPhrase3166 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsExactTermOrPhrase3174 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsExactTermOrPhrase3224 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsExactTermOrPhrase3233 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_ftsTokenisedTermOrPhrase3314 = new BitSet(new long[]{0x100F400000106860L,0x0000000060820500L});
	public static final BitSet FOLLOW_fieldReference_in_ftsTokenisedTermOrPhrase3342 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsTokenisedTermOrPhrase3344 = new BitSet(new long[]{0x100F400000106800L,0x0000000020820100L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3372 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsTokenisedTermOrPhrase3380 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3447 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3456 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsTokenisedTermOrPhrase3517 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsTokenisedTermOrPhrase3525 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsTokenisedTermOrPhrase3575 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsTokenisedTermOrPhrase3584 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_cmisTerm3657 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_cmisPhrase3711 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_ftsRange3766 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsRange3768 = new BitSet(new long[]{0x100F400000006000L,0x0000000000000006L});
	public static final BitSet FOLLOW_ftsFieldGroupRange_in_ftsRange3772 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_ftsFieldGroup3828 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_ftsFieldGroup3830 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
	public static final BitSet FOLLOW_LPAREN_in_ftsFieldGroup3832 = new BitSet(new long[]{0x100F4000049068B0L,0x0000000030822517L});
	public static final BitSet FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroup3834 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
	public static final BitSet FOLLOW_RPAREN_in_ftsFieldGroup3836 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupExplicitDisjunction_in_ftsFieldGroupDisjunction3921 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupImplicitDisjunction_in_ftsFieldGroupDisjunction3935 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3968 = new BitSet(new long[]{0x0000000000000082L,0x0000000000000400L});
	public static final BitSet FOLLOW_or_in_ftsFieldGroupExplicitDisjunction3971 = new BitSet(new long[]{0x100F4000049068B0L,0x0000000030822117L});
	public static final BitSet FOLLOW_ftsFieldGroupImplicitConjunction_in_ftsFieldGroupExplicitDisjunction3973 = new BitSet(new long[]{0x0000000000000082L,0x0000000000000400L});
	public static final BitSet FOLLOW_or_in_ftsFieldGroupImplicitDisjunction4058 = new BitSet(new long[]{0x100F400004906880L,0x0000000030822117L});
	public static final BitSet FOLLOW_ftsFieldGroupExplicitConjunction_in_ftsFieldGroupImplicitDisjunction4061 = new BitSet(new long[]{0x100F400004906882L,0x0000000030822517L});
	public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4148 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsFieldGroupExplicitConjunction4151 = new BitSet(new long[]{0x100F400004906880L,0x0000000030822117L});
	public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupExplicitConjunction4153 = new BitSet(new long[]{0x0000000000000032L});
	public static final BitSet FOLLOW_and_in_ftsFieldGroupImplicitConjunction4238 = new BitSet(new long[]{0x100F400004906880L,0x0000000030822117L});
	public static final BitSet FOLLOW_ftsFieldGroupPrefixed_in_ftsFieldGroupImplicitConjunction4241 = new BitSet(new long[]{0x100F4000049068B2L,0x0000000030822117L});
	public static final BitSet FOLLOW_not_in_ftsFieldGroupPrefixed4331 = new BitSet(new long[]{0x100F400000906800L,0x0000000030820107L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4333 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4335 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4399 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4401 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_in_ftsFieldGroupPrefixed4465 = new BitSet(new long[]{0x100F400000906800L,0x0000000030820107L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4467 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4469 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BAR_in_ftsFieldGroupPrefixed4533 = new BitSet(new long[]{0x100F400000906800L,0x0000000030820107L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4535 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4537 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MINUS_in_ftsFieldGroupPrefixed4601 = new BitSet(new long[]{0x100F400000906800L,0x0000000030820107L});
	public static final BitSet FOLLOW_ftsFieldGroupTest_in_ftsFieldGroupPrefixed4603 = new BitSet(new long[]{0x0000000000000202L});
	public static final BitSet FOLLOW_boost_in_ftsFieldGroupPrefixed4605 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_ftsFieldGroupTest4696 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupTest4756 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest4766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupExactTerm_in_ftsFieldGroupTest4831 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest4841 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupPhrase_in_ftsFieldGroupTest4906 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest4916 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTest4981 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest4991 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupTokenisedPhrase_in_ftsFieldGroupTest5056 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_slop_in_ftsFieldGroupTest5066 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupSynonym_in_ftsFieldGroupTest5131 = new BitSet(new long[]{0x0000000000000002L,0x0000000010000000L});
	public static final BitSet FOLLOW_fuzzy_in_ftsFieldGroupTest5141 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupRange_in_ftsFieldGroupTest5206 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_ftsFieldGroupTest5266 = new BitSet(new long[]{0x100F4000049068B0L,0x0000000030822517L});
	public static final BitSet FOLLOW_ftsFieldGroupDisjunction_in_ftsFieldGroupTest5268 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
	public static final BitSet FOLLOW_RPAREN_in_ftsFieldGroupTest5270 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWord_in_ftsFieldGroupTerm5323 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_ftsFieldGroupExactTerm5356 = new BitSet(new long[]{0x100E400000106800L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupExactTerm5358 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FTSPHRASE_in_ftsFieldGroupPhrase5411 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_EQUALS_in_ftsFieldGroupExactPhrase5452 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupExactPhrase5454 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_ftsFieldGroupTokenisedPhrase5515 = new BitSet(new long[]{0x0000000000800000L});
	public static final BitSet FOLLOW_ftsFieldGroupExactPhrase_in_ftsFieldGroupTokenisedPhrase5517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TILDA_in_ftsFieldGroupSynonym5570 = new BitSet(new long[]{0x100E400000106800L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsFieldGroupTerm_in_ftsFieldGroupSynonym5572 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5625 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
	public static final BitSet FOLLOW_proximityGroup_in_ftsFieldGroupProximity5635 = new BitSet(new long[]{0x100E400000006000L,0x0000000020000100L});
	public static final BitSet FOLLOW_ftsFieldGroupProximityTerm_in_ftsFieldGroupProximity5637 = new BitSet(new long[]{0x0000000000000002L,0x0000000000800000L});
	public static final BitSet FOLLOW_STAR_in_proximityGroup5830 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000001L});
	public static final BitSet FOLLOW_LPAREN_in_proximityGroup5833 = new BitSet(new long[]{0x0000000000004000L,0x0000000000100000L});
	public static final BitSet FOLLOW_DECIMAL_INTEGER_LITERAL_in_proximityGroup5835 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
	public static final BitSet FOLLOW_RPAREN_in_proximityGroup5838 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5922 = new BitSet(new long[]{0x0000000000200000L});
	public static final BitSet FOLLOW_DOTDOT_in_ftsFieldGroupRange5924 = new BitSet(new long[]{0x100F400000006000L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5926 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_range_left_in_ftsFieldGroupRange5964 = new BitSet(new long[]{0x100F400000006000L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5966 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
	public static final BitSet FOLLOW_TO_in_ftsFieldGroupRange5968 = new BitSet(new long[]{0x100F400000006000L});
	public static final BitSet FOLLOW_ftsRangeWord_in_ftsFieldGroupRange5970 = new BitSet(new long[]{0x0800000000000000L,0x0000000000200000L});
	public static final BitSet FOLLOW_range_right_in_ftsFieldGroupRange5972 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LSQUARE_in_range_left6031 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_range_left6063 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RSQUARE_in_range_right6116 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GT_in_range_right6148 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_fieldReference6204 = new BitSet(new long[]{0x1000000000000020L,0x0000000060000500L});
	public static final BitSet FOLLOW_prefix_in_fieldReference6241 = new BitSet(new long[]{0x1000000000000020L,0x0000000020000500L});
	public static final BitSet FOLLOW_uri_in_fieldReference6261 = new BitSet(new long[]{0x1000000000000020L,0x0000000020000500L});
	public static final BitSet FOLLOW_identifier_in_fieldReference6282 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_tempReference6369 = new BitSet(new long[]{0x1000000000000020L,0x0000000060000500L});
	public static final BitSet FOLLOW_prefix_in_tempReference6398 = new BitSet(new long[]{0x1000000000000020L,0x0000000020000500L});
	public static final BitSet FOLLOW_uri_in_tempReference6418 = new BitSet(new long[]{0x1000000000000020L,0x0000000020000500L});
	public static final BitSet FOLLOW_identifier_in_tempReference6439 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_identifier_in_prefix6526 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_prefix6528 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_URI_in_uri6609 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_identifier6711 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_DOT_in_identifier6713 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_ID_in_identifier6717 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_identifier6766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TO_in_identifier6833 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_in_identifier6871 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_in_identifier6909 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_identifier6948 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord7066 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7072 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7074 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7080 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7082 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7088 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7090 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7096 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7098 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7104 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7164 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7166 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7172 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7174 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7180 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7182 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7188 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7190 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7196 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord7263 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7269 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7271 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7277 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7279 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7285 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7287 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7293 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7295 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7359 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7361 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7367 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7369 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7375 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7377 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7383 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7385 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord7450 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7456 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7458 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7464 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7466 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7472 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7474 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7480 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7534 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7536 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7542 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7544 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7550 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7552 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7558 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord7616 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7622 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7624 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7630 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7632 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7638 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7640 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7696 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7698 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7704 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7706 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7712 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7714 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord7770 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7776 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7778 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7784 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7786 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7792 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7838 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7840 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7846 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7848 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7854 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord7905 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7911 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7913 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7919 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7921 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7970 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7972 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord7978 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord7980 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord8029 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8035 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord8037 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8043 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8081 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord8083 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8089 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord8131 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8137 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord8139 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8179 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_ftsWord8181 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_ftsWord8197 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8203 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_ftsWord8216 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OR_in_or8581 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BAR_in_or8593 = new BitSet(new long[]{0x0000000000000080L});
	public static final BitSet FOLLOW_BAR_in_or8595 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AND_in_and8628 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AMP_in_and8640 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_AMP_in_and8642 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_in_synpred1_FTS1233 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_synpred2_FTS1746 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_synpred3_FTS2635 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_synpred3_FTS2637 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred4_FTS2676 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred5_FTS2751 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred6_FTS2821 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred7_FTS2879 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_synpred8_FTS2984 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_synpred8_FTS2986 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred9_FTS3025 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred10_FTS3100 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred11_FTS3170 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred12_FTS3228 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fieldReference_in_synpred13_FTS3335 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_COLON_in_synpred13_FTS3337 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred14_FTS3376 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred15_FTS3451 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred16_FTS3521 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred17_FTS3579 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_not_in_synpred18_FTS4326 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsFieldGroupProximity_in_synpred19_FTS4691 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred20_FTS4761 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred21_FTS4836 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred22_FTS4911 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred23_FTS4986 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_slop_in_synpred24_FTS5061 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_fuzzy_in_synpred25_FTS5136 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_proximityGroup_in_synpred26_FTS5630 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_prefix_in_synpred27_FTS6236 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ID_in_synpred28_FTS6691 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_DOT_in_synpred28_FTS6693 = new BitSet(new long[]{0x1000000000000000L});
	public static final BitSet FOLLOW_ID_in_synpred28_FTS6695 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred29_FTS7011 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred29_FTS7017 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred29_FTS7019 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred29_FTS7025 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred29_FTS7027 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred29_FTS7033 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred29_FTS7035 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred29_FTS7041 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred29_FTS7043 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred29_FTS7049 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred30_FTS7117 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred30_FTS7119 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred30_FTS7125 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred30_FTS7127 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred30_FTS7133 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred30_FTS7135 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred30_FTS7141 = new BitSet(new long[]{0x0000000000100000L});
	public static final BitSet FOLLOW_DOT_in_synpred30_FTS7143 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_COMMA_in_synpred30_FTS7145 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred30_FTS7147 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred31_FTS7210 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred31_FTS7216 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred31_FTS7218 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred31_FTS7224 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred31_FTS7226 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred31_FTS7232 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred31_FTS7234 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred31_FTS7240 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred31_FTS7242 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred32_FTS7312 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred32_FTS7314 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred32_FTS7320 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred32_FTS7322 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred32_FTS7328 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred32_FTS7330 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred32_FTS7336 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred32_FTS7338 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred33_FTS7403 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred33_FTS7409 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred33_FTS7411 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred33_FTS7417 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred33_FTS7419 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred33_FTS7425 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred33_FTS7427 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred33_FTS7433 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred34_FTS7493 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred34_FTS7495 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred34_FTS7501 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred34_FTS7503 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred34_FTS7509 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred34_FTS7511 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred34_FTS7517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred35_FTS7571 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred35_FTS7577 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred35_FTS7579 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred35_FTS7585 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred35_FTS7587 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred35_FTS7593 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred35_FTS7595 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred36_FTS7657 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred36_FTS7659 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred36_FTS7665 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred36_FTS7667 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred36_FTS7673 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred36_FTS7675 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred37_FTS7731 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred37_FTS7737 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred37_FTS7739 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred37_FTS7745 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred37_FTS7747 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred37_FTS7753 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred38_FTS7805 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred38_FTS7807 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred38_FTS7813 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred38_FTS7815 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred38_FTS7821 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred39_FTS7868 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred39_FTS7874 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred39_FTS7876 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred39_FTS7882 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred39_FTS7884 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred40_FTS7938 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred40_FTS7940 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred40_FTS7946 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred40_FTS7948 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred41_FTS7998 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred41_FTS8004 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred41_FTS8006 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred41_FTS8012 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred42_FTS8056 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred42_FTS8058 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred42_FTS8064 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_synpred43_FTS8102 = new BitSet(new long[]{0x100E400000006000L,0x0000000020820100L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred43_FTS8108 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred43_FTS8110 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ftsWordBase_in_synpred44_FTS8156 = new BitSet(new long[]{0x0000000000100800L});
	public static final BitSet FOLLOW_set_in_synpred44_FTS8158 = new BitSet(new long[]{0x0000000000000002L});
}
