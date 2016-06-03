package org.alfresco.repo.search.impl.querymodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 */
public interface QueryModelFactory
{
    public Query createQuery(List<Column> columns, Source source, Constraint constraint, List<Ordering> orderings);

    public Selector createSelector(QName classQName, String alias);

    public Join createJoin(Source left, Source right, JoinType joinType, Constraint joinCondition);

    public Constraint createConjunction(List<Constraint> constraints);

    public Constraint createDisjunction(List<Constraint> constraints);

    public Constraint createFunctionalConstraint(Function function, Map<String, Argument> functionArguments);

    public Column createColumn(Function function, Map<String, Argument> functionArguments, String alias);

    public LiteralArgument createLiteralArgument(String name, QName type, Serializable value);

    public Ordering createOrdering(Column column, Order order);

    public ParameterArgument createParameterArgument(String name, String parameterName);

    public PropertyArgument createPropertyArgument(String name, boolean queryable, boolean orderable, String selectorAlias, String propertyName);
    
    public SelectorArgument createSelectorArgument(String name, String selectorAlias);

    public Function getFunction(String functionName);

    public ListArgument createListArgument(String name, ArrayList<Argument> arguments);
    
    public FunctionArgument createFunctionArgument(String name, Function function, Map<String, Argument> functionArguments);
}
