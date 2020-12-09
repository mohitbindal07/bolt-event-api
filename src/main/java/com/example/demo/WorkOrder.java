package com.example.demo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkOrder {

	private String workOrderId;
	private String workOrderStatus;
	private String workOrderAssigned;
	private List<WorkOrderFQN> fqnList;
	
	
	public WorkOrder() {
		super();
	}
	public WorkOrder(String id,String status,String assigned,List<WorkOrderFQN> fqnList) {
		this.workOrderId = id;
		this.workOrderStatus = status;
		this.workOrderAssigned = assigned;
		this.fqnList = fqnList;
	}
	public String getWorkOrderId() {
		return workOrderId;
	}
	public void setWorkOrderId(String workOrderId) {
		this.workOrderId = workOrderId;
	}
	public String getWorkOrderStatus() {
		return workOrderStatus;
	}
	public void setWorkOrderStatus(String workOrderStatus) {
		this.workOrderStatus = workOrderStatus;
	}
	public String getWorkOrderAssigned() {
		return workOrderAssigned;
	}
	public void setWorkOrderAssigned(String workOrderAssigned) {
		this.workOrderAssigned = workOrderAssigned;
	}
	public List<WorkOrderFQN> getFqn() {
		return fqnList;
	}
	public void setFqn(List<WorkOrderFQN> fqn) {
		this.fqnList = fqn;
	}
}
