package net.gbicc.xbrl.ent.model;

public class ItemElement extends BaseElement {
	/*** 语境（上下文） */
	private String contextRef;
	/** 元素值 */
	private String value;
	/** 数字型的单位 **/
	private String unitRef;
	/** 小数点位数 **/
	private String decimals;
	/** 数字精度 **/
	private String precision;
	/** 数值是否为空 **/
	private boolean isNull;

	public String getContextRef() {
		return contextRef;
	}

	public void setContextRef(String contextRef) {
		this.contextRef = contextRef;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnitRef() {
		return unitRef;
	}

	public void setUnitRef(String unitRef) {
		this.unitRef = unitRef;
	}

	public String getDecimals() {
		return decimals;
	}

	public void setDecimals(String decimals) {
		this.decimals = decimals;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}
}
