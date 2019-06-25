package com.example.aps.model;


import com.example.aps.model.APSInstanceVariable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class APSInstanceSearch {
	
	private String name;
	private String businessKey;
	private String processDefinitionId;
	private APSInstanceVariable[] variables;
	
	public APSInstanceSearch(String processDefinitionId, APSInstanceVariable[] variables) {
        this.processDefinitionId = processDefinitionId;
        this.variables = variables;
    }
	
	public APSInstanceSearch(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public APSInstanceVariable[] getVariables() {
		return variables;
	}
	public void setVariables(APSInstanceVariable[] variables) {
		this.variables = variables;
	}
	
	



}
