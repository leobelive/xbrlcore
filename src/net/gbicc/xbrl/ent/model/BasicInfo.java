package net.gbicc.xbrl.ent.model;

public class BasicInfo {
	private String idStr;

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

	private String elementName;

	private String fieldName;

	private String tableName;

	private String type;

	private String parten;

	public String getParten() {
		return parten;
	}

	public void setParten(String parten) {
		this.parten = parten;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
