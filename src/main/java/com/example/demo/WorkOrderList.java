package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkOrderList {

	private List<WorkOrder> workorders;

	public WorkOrderList() {
		workorders = new ArrayList<>();
	}

	public List<WorkOrder> getWorkorders() {
		return workorders;
	}

	public void setWorkorders(List<WorkOrder> workorders) {
		this.workorders = workorders;
	}

}
