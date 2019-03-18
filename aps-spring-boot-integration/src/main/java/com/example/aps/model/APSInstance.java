package com.example.aps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class APSInstance {
	
	private String id;
	private String name;
	private String businessKey;
	private String processDefinitionId;
	private String tenantId;
	private String started;
	private String ended;
	private String processDefinitionName;
	private String processDefinitionDescription;
	private String processDefinitionKey;
	private String processDefinitionCategory;
	private String processDefinitionVersion;
	private String processDefinitionDeploymentId;
	private boolean graphicalNotationDefined;
	private boolean startFormDefined;
	private boolean suspended;
	private User startedBy;
	private APSInstanceVariable[] variables;
	
    public APSInstance() {}    
	
	public APSInstance(String name, String businessKey, String processDefinitionId, APSInstanceVariable[] variables) {
        this.setName(name);
        this.setBusinessKey(businessKey);
		this.setProcessDefinitionId(processDefinitionId);
		this.setVariables(variables);
    }
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getStarted() {
		return started;
	}
	public void setStarted(String started) {
		this.started = started;
	}
	public String getEnded() {
		return ended;
	}
	public void setEnded(String ended) {
		this.ended = ended;
	}
	public String getProcessDefinitionName() {
		return processDefinitionName;
	}
	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}
	public String getProcessDefinitionDescription() {
		return processDefinitionDescription;
	}
	public void setProcessDefinitionDescription(String processDefinitionDescription) {
		this.processDefinitionDescription = processDefinitionDescription;
	}
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	public String getProcessDefinitionCategory() {
		return processDefinitionCategory;
	}
	public void setProcessDefinitionCategory(String processDefinitionCategory) {
		this.processDefinitionCategory = processDefinitionCategory;
	}
	public String getProcessDefinitionVersion() {
		return processDefinitionVersion;
	}
	public void setProcessDefinitionVersion(String processDefinitionVersion) {
		this.processDefinitionVersion = processDefinitionVersion;
	}
	public String getProcessDefinitionDeploymentId() {
		return processDefinitionDeploymentId;
	}
	public void setProcessDefinitionDeploymentId(String processDefinitionDeploymentId) {
		this.processDefinitionDeploymentId = processDefinitionDeploymentId;
	}
	public boolean isGraphicalNotationDefined() {
		return graphicalNotationDefined;
	}
	public void setGraphicalNotationDefined(boolean graphicalNotationDefined) {
		this.graphicalNotationDefined = graphicalNotationDefined;
	}
	public boolean isStartFormDefined() {
		return startFormDefined;
	}
	public void setStartFormDefined(boolean startFormDefined) {
		this.startFormDefined = startFormDefined;
	}
	public boolean isSuspended() {
		return suspended;
	}
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	public User getStartedBy() {
		return startedBy;
	}
	public void setStartedBy(User startedBy) {
		this.startedBy = startedBy;
	}
	public APSInstanceVariable[] getVariables() {
		return variables;
	}
	public void setVariables(APSInstanceVariable[] variables) {
		this.variables = variables;
	}
	
	



}
