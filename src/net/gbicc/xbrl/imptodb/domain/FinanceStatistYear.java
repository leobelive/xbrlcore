package net.gbicc.xbrl.imptodb.domain;

import java.io.Serializable;

public class FinanceStatistYear implements Serializable {

	private String id;
	private String securityCode;
	private String securityName;
	private String accountingYear;

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

	public String getAccountingYear() {
		return accountingYear;
	}

	public void setAccountingYear(String accountingYear) {
		this.accountingYear = accountingYear;
	}
}
