package com.example.aps.model;

public class APSProcessDefinition {
	
	private String id;
	private String name;
	private String description;
	private String key;
	private String category;
	private Integer version;
	private String deploymentId;
	private String tenantId;
	private boolean hasStartForm;
	private String[] metaDataValues;
	
	
    public APSProcessDefinition() {}    

	
	
	public APSProcessDefinition(String id, String name, String description, String key, String category,
			Integer version, String deploymentId, String tenantId, boolean hasStartForm, String[] metaDataValues) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.key = key;
		this.category = category;
		this.version = version;
		this.deploymentId = deploymentId;
		this.tenantId = tenantId;
		this.hasStartForm = hasStartForm;
		this.metaDataValues = metaDataValues;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public boolean isHasStartForm() {
		return hasStartForm;
	}
	public void setHasStartForm(boolean hasStartForm) {
		this.hasStartForm = hasStartForm;
	}
	public String[] getMetaDataValues() {
		return metaDataValues;
	}
	public void setMetaDataValues(String[] metaDataValues) {
		this.metaDataValues = metaDataValues;
	}

}
