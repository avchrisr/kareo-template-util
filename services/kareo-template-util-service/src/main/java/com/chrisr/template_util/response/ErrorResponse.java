package com.chrisr.template_util.response;

public class ErrorResponse {

	private int status;
	private String message;
	private String datetime;

	public ErrorResponse() {}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
