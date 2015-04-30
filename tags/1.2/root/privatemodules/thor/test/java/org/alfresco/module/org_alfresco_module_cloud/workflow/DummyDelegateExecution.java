package org.alfresco.module.org_alfresco_module_cloud.workflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.EngineServices;
import org.activiti.engine.delegate.DelegateExecution;

public class DummyDelegateExecution implements DelegateExecution {

	private Map<String, Object> variables = new HashMap<String, Object>();
	private Map<String, Object> localVariables = new HashMap<String, Object>();
	private String tennantId = "";
	
	@Override
	public Map<String, Object> getVariables() {
		return variables;
	}

	@Override
	public Map<String, Object> getVariablesLocal() {
		return localVariables;
	}

	@Override
	public Object getVariable(String variableName) {
		return variables.get(variableName);
	}
	
	@Override
	public <T> T getVariable(String variableName, Class<T> variableClass) {
	    return variableClass.cast(variables.get(variableName));
	}

	@Override
	public Set<String> getVariableNames() {
		return variables.keySet();
	}

	@Override
	public Set<String> getVariableNamesLocal() {
		return localVariables.keySet();
	}

	@Override
	public void setVariable(String variableName, Object value) {
		variables.put(variableName, value);
	}

	@Override
	public Object setVariableLocal(String variableName, Object value) {
		localVariables.put(variableName, value);
		return value;
	}

	@Override
	public void setVariables(Map<String, ? extends Object> variables) {
		this.variables.putAll(variables);
	}

	@Override
	public void setVariablesLocal(Map<String, ? extends Object> variables) {
		this.localVariables.putAll(variables);
	}

	@Override
	public boolean hasVariables() {
		return !variables.isEmpty();
	}

	@Override
	public boolean hasVariablesLocal() {
		return !localVariables.isEmpty();
	}

	@Override
	public boolean hasVariable(String variableName) {
		return variables.containsKey(variableName);
	}

	@Override
	public boolean hasVariableLocal(String variableName) {
		return localVariables.containsKey(variableName);
	}

	@Override
	public void createVariableLocal(String variableName, Object value) {
		setVariableLocal(variableName, value);
	}

	@Override
	public void removeVariable(String variableName) {
		variables.remove(variableName);
	}

	@Override
	public void removeVariableLocal(String variableName) {
		localVariables.remove(variableName);
	}

	@Override
	public void removeVariables(Collection<String> variableNames) {
		for(String varName : variableNames) {
			variables.remove(varName);
		}
	}

	@Override
	public void removeVariablesLocal(Collection<String> variableNames) {
		for(String varName : variableNames) {
			localVariables.remove(varName);
		}
	}

	@Override
	public void removeVariables() {
		variables.clear();
	}

	@Override
	public void removeVariablesLocal() {
		localVariables.clear();
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessInstanceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEventName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessDefinitionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentActivityId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentActivityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EngineServices getEngineServices() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public Object getVariableLocal(String arg0)
    {
        return localVariables.get(arg0);
    }
    
    @Override
    public <T> T getVariableLocal(String variableName, Class<T> variableClass)
    {
        return variableClass.cast(localVariables.get(variableName));
    }

    @Override
    public String getTenantId()
    {
        return tennantId;
    }
    
    public void setTenantId(String tennantId)
    {
        this.tennantId = tennantId;
    }

}
