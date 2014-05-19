package net.gbicc.xbrl.ent.model;

public class RelationMapping {

	/**
	 * 关系的ID，唯一标识符
	 */
	private String id;
	/**
	 * 元素名称
	 */
	private String elementName;
	/**
	 * 元素对应的字段名称
	 */
	private String fieldName;
	/**
	 * 元素对应的字段所在的表名
	 */
	private String tableName;
	/**
	 * 元素来源的类别，如item和tuple
	 */
	private String dataType;
	/**
	 * ？？？？？？？？？？询问秋然
	 */
	private String parten;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getParten() {
		return parten;
	}

	public void setParten(String parten) {
		this.parten = parten;
	}
}
