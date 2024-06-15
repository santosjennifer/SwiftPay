package com.github.dto;

public class AuthorizerDto {

    private String status;
    private DataDto data;
    
    public String getStatus() {
		return status;
	}
    
	public void setStatus(String status) {
		this.status = status;
	}

	public DataDto getData() {
		return data;
	}
	
	public void setData(DataDto data) {
		this.data = data;
	}

	public static class DataDto {
        private boolean authorization;

		public boolean isAuthorization() {
			return authorization;
		}

		public void setAuthorization(boolean authorization) {
			this.authorization = authorization;
		}
		
    }
	
}
