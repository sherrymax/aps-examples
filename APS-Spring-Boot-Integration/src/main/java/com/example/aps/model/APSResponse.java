package com.example.aps.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class APSResponse {
	private Integer size;
	private Integer total;
	private Integer start;
	private APSProcessDefinition[] data;
	
    public APSResponse() {}    

	
	public APSResponse(Integer size, Integer total, Integer start, APSProcessDefinition[] data) {
		this.size = size;
		this.total = total;
		this.start = start;
		this.data = data;
	}
	
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public APSProcessDefinition[] getData() {
		return data;
	}
	public void setData(APSProcessDefinition[] data) {
		this.data = data;
	}
}
