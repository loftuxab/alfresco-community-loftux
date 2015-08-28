package com.icegreen.greenmail.imap.commands;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Builder for search terms.
 *
 * @author mm
 */
public abstract class SearchTermBuilder {
    private SearchKey key;
    private List<Object> parameters = Collections.<Object>emptyList();

    public static SearchTermBuilder create(final String pTerm) {
        SearchKey key = SearchKey.valueOf(pTerm);
        SearchTermBuilder builder;
        switch (key) {
            // Non flags first
            case HEADER:
                builder = createHeaderTermBuilder();
                break;
            // Flags
            case ALL:
                builder = createSearchTermBuilder(new AllSearchTerm());
                break;
            case ANSWERED:
                builder = createFlagSearchTermBuilder("ANSWERED", true);
                break;
            case BCC:
                builder = createRecipientSearchTermBuilder(Message.RecipientType.BCC);
                break;
            case CC:
                builder = createRecipientSearchTermBuilder(Message.RecipientType.CC);
                break;
            case DELETED:
                builder = createFlagSearchTermBuilder("DELETED", true);
                break;
            case DRAFT:
                builder = createFlagSearchTermBuilder("DRAFT", true);
                break;
            case FLAGGED:
                builder = createFlagSearchTermBuilder("FLAGGED", true);
                break;
            case FROM:
                builder = createFromSearchTermBuilder();
                break;
            case NEW:
                builder = createSearchTermBuilder(
                        new AndTerm(createFlagSearchTerm("RECENT", true), createFlagSearchTerm("SEEN", false))
                );
                break;
            case OLD:
                builder = createSearchTermBuilder(createFlagSearchTerm("RECENT", false));
                break;
            case RECENT:
                builder = createSearchTermBuilder(createFlagSearchTerm("RECENT", true));
                break;
            case SEEN:
                builder = createSearchTermBuilder(createFlagSearchTerm("SEEN", true));
                break;
            case TO:
                builder = createRecipientSearchTermBuilder(Message.RecipientType.TO);
                break;
            case UNANSWERED:
                builder = createSearchTermBuilder(createFlagSearchTerm("ANSWERED", false));
                break;
            case UNDELETED:
                builder = createSearchTermBuilder(createFlagSearchTerm("DELETED", false));
                break;
            case UNDRAFT:
                builder = createSearchTermBuilder(createFlagSearchTerm("DRAFT", false));
                break;
            case UNFLAGGED:
                builder = createSearchTermBuilder(createFlagSearchTerm("FLAGGED", false));
                break;
            case UNSEEN:
                builder = createSearchTermBuilder(createFlagSearchTerm("SEEN", false));
                break;
            case KEYWORD:
            case UNKEYWORD:
                builder = createKeywordSearchTermBuilder(key);
                break;
            case BEFORE:
                builder = createDateSearchTermBuilder(ComparisonTerm.LT, true);
                break;
            case BODY:
                builder = createBodyTerm();
                break;
            case LARGER:
                builder = createSizeSearchTermBuilder(ComparisonTerm.GT);
                break;
            case ON:
                builder = createDateSearchTermBuilder(ComparisonTerm.EQ, true);
                break;
            case TEXT:
                builder =createTextTerm();
                break;
            case SENTBEFORE:
                builder = createDateSearchTermBuilder(ComparisonTerm.LT, false);
                break;
            case SENTON:
                builder = createDateSearchTermBuilder(ComparisonTerm.EQ, false);
                break;
            case SENTSINCE:
                builder = createDateSearchTermBuilder(ComparisonTerm.GT, false);
                break;
            case SINCE:
                builder = createDateSearchTermBuilder(ComparisonTerm.GT, true);
                break;
            case SMALLER:
                builder = createSizeSearchTermBuilder(ComparisonTerm.LT);
                break;
            case SUBJECT:
                builder = createSubjectTerm();
                break;
            case OR:
                builder = createOrTerm();
                break;
            case NOT:
                builder = createNotTerm();
                break;
            case UID:
                //TODO
            default:
                throw new IllegalStateException("Unsupported search term '" + pTerm + '\'');
        }
        builder.setSearchKey(key);
        return builder;
    }

    private void setSearchKey(final SearchKey pKey) {
        key = pKey;
    }

    private static SearchTermBuilder createHeaderTermBuilder() {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return new HeaderTerm(getStringParameter(0), getStringParameter(1));
            }
        };
    }

    SearchTermBuilder addParameter(final Object pParameter) {
        if (Collections.<Object>emptyList() == parameters) {
            parameters = new ArrayList<Object>();
        }
        parameters.add(pParameter);
        return this;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public Object getParameter(final int pIdx) {
        return getParameters().get(pIdx);
    }

    public String getStringParameter(final int pIdx)
    {
        Object obj = getParameters().get(pIdx);
        if (obj instanceof String)
        {
            return (String) obj;
        }
        return null;
    }

    public SearchTerm getSearchTermParameter(final int pIdx)
    {
        Object obj = getParameters().get(pIdx);
        if (obj instanceof SearchTerm)
        {
            return (SearchTerm) obj;
        }
        return null;
    }

    public boolean expectsParameter() {
        return parameters.size() < key.getNumberOfParameters();
    }

    public boolean isExpressionParameter()
    {
        return key.isParameterExpression();
    }

    public abstract SearchTerm build();

    private static SearchTermBuilder createSearchTermBuilder(final SearchTerm pSearchTerm) {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return pSearchTerm;
            }
        };
    }

    private static SearchTermBuilder createRecipientSearchTermBuilder(final Message.RecipientType type) {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                try {
                    return new RecipientTerm(type, new InternetAddress(getStringParameter(0)));
                } catch (AddressException e) {
                    throw new IllegalArgumentException("Address is not correct", e);
                }
            }
        };
    }

    private static SearchTermBuilder createFromSearchTermBuilder() {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                try {
                    return new FromTerm(new InternetAddress(getStringParameter(0)));
                } catch (AddressException e) {
                    throw new IllegalArgumentException("Address is not correct", e);
                }
            }
        };
    }

    private static SearchTermBuilder createFlagSearchTermBuilder(final String pFlagName, final boolean pValue) {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return createFlagSearchTerm(pFlagName, pValue);
            }
        };
    }
    private static SearchTermBuilder createKeywordSearchTermBuilder(final SearchKey pKey) {
       return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return createFlagSearchTerm(getStringParameter(0), pKey == SearchKey.KEYWORD);
            }
        };
    }

    private static SearchTerm createFlagSearchTerm(String pFlagName, boolean pValue) {
        Flags.Flag flag = toFlag(pFlagName);
        Flags flags = new javax.mail.Flags();
        if(null==flag) { // user flags
            flags.add(pFlagName);
        }
        else {
            flags.add(flag);
        }
        return new FlagTerm(flags, pValue);
    }

    /**
     * @param comparison the comparison type. See {@link ComparisonTerm}
     * @param isInternalDate true for internal (received) date
     * @return SearchTermBuilder
     */
    private static SearchTermBuilder createDateSearchTermBuilder(final int comparison, final boolean isInternalDate)
    {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                String dateStr = getStringParameter(0);
                try {
                    Date date = new SimpleDateFormat("dd-MMM-yyyy").parse(dateStr);
                    return isInternalDate ? new ReceivedDateTerm(comparison, date) : new SentDateTerm(comparison, date);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Date cannot be parsed", e);
                }
            }
        };
    }

    /**
     *
     * @param comparison the comparison type. See {@link ComparisonTerm}
     * @return SearchTermBuilder
     */
    private static SearchTermBuilder createSizeSearchTermBuilder(final int comparison)
    {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                String sizeStr = getStringParameter(0);
                try
                {
                    int size = Integer.parseInt(sizeStr);
                    return new SizeTerm(comparison, size);
                } catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("Size cannot be parsed", e);
                }
            }
        };
    }

    private static SearchTermBuilder createSubjectTerm()
    {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return new SubjectTerm(getStringParameter(0));
            }
        };
    }

    private static SearchTermBuilder createBodyTerm()
    {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return new BodyTerm(getStringParameter(0));
            }
        };
    }

    private static SearchTermBuilder createTextTerm()
    {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                String data = getStringParameter(0);
                return new OrTerm(new BodyTerm(data), new HeaderTerm(data, data));
            }
        };
    }

    private static SearchTermBuilder createOrTerm()
    {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return new OrTerm(getSearchTermParameter(0), getSearchTermParameter(1));
            }
        };
    }

    private static SearchTermBuilder createNotTerm()
    {
        return new SearchTermBuilder() {
            @Override
            public SearchTerm build() {
                return new NotTerm(getSearchTermParameter(0));
            }
        };
    }

    private static javax.mail.Flags.Flag toFlag(String pFlag) {
        if (pFlag == null || pFlag.trim().length() < 1) {
            throw new IllegalArgumentException("Can not convert empty string to mail flag");
        }
        pFlag = pFlag.trim().toUpperCase();
        if (pFlag.equals("ANSWERED")) {
            return javax.mail.Flags.Flag.ANSWERED;
        }
        if (pFlag.equals("DELETED")) {
            return javax.mail.Flags.Flag.DELETED;
        }
        if (pFlag.equals("DRAFT")) {
            return javax.mail.Flags.Flag.DRAFT;
        }
        if (pFlag.equals("FLAGGED")) {
            return javax.mail.Flags.Flag.FLAGGED;
        }
        if (pFlag.equals("RECENT")) {
            return javax.mail.Flags.Flag.RECENT;
        }
        if (pFlag.equals("SEEN")) {
            return javax.mail.Flags.Flag.SEEN;
        }
        return null;
    }

    @Override
    public String toString() {
        return "SearchTermBuilder{" +
                "key=" + key +
                ", parameters=" + parameters +
                '}';
    }

    /**
     * Search term that matches all messages
     */
    private static class AllSearchTerm extends SearchTerm {
        @Override
        public boolean match(Message msg) {
            return true;
        }
    }
}
