package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraTicket {
	
	@JsonIgnore	
	private String jiraId;
	
	@JsonProperty("module")	
	private String module;
	
	@JsonProperty("title")	
	private String title;
	
	@JsonProperty("description")	
	private String description;
	
	@JsonProperty("severity")	
	private String severity;
	
	/*
	 * @JsonIgnore
	 * 
	 * @JsonProperty("effortEstimation") private String effortEstimation;
	 * 
	 * @JsonIgnore
	 * 
	 * @JsonProperty("assigned") private String assigned;
	 * 
	 * @JsonIgnore
	 * 
	 * @JsonProperty("comments") private String comments;
	 * 
	 * @JsonIgnore
	 * 
	 * @JsonProperty("status") private String status;
	 */
	public String getJiraId() {
		return jiraId;
	}
	public void setJiraId(String jiraId) {
		this.jiraId = jiraId;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	/*
	 * public String getEffortEstimation() { return effortEstimation; } public void
	 * setEffortEstimation(String effortEstimation) { this.effortEstimation =
	 * effortEstimation; } public String getAssigned() { return assigned; } public
	 * void setAssigned(String assigned) { this.assigned = assigned; } public String
	 * getComments() { return comments; } public void setComments(String comments) {
	 * this.comments = comments; } public String getStatus() { return status; }
	 * public void setStatus(String status) { this.status = status; }
	 */
	
}
