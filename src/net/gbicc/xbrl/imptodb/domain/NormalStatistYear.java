package net.gbicc.xbrl.imptodb.domain;

import java.io.Serializable;

public class NormalStatistYear implements Serializable {
	private String id;
	private String securityCode;
	private String securityName;
	private String area;
	private String securities;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSecurities() {
		return securities;
	}

	public void setSecurities(String securities) {
		this.securities = securities;
	}
}
