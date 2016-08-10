package com.example.user.myapplication;

public class ArduinoEntity {
	public String id;
	public String insertime;
	public String accessStatus;
	public ArduinoEntity(String id, String insertime, String accessStatus) {
		super();
		this.id = id;
		this.insertime = insertime;
		this.accessStatus = accessStatus;
	}
	public ArduinoEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInsertime() {
		return insertime;
	}
	public void setInsertime(String insertime) {
		this.insertime = insertime;
	}
	public String getAccessStatus() {
		return accessStatus;
	}
	public void setAccessStatus(String accessStatus) {
		this.accessStatus = accessStatus;
	}
	
	
	
	
}
