package net.gbicc.xbrl.ent.model;

public class GeneImport {

	/**
	 * 需要插入数据表的名称
	 */
	private String tableName;
	/**
	 * 需要插入数据表的字段，字符串形式
	 */
	private String fieldString;
	/**
	 * 需要插入数据表的值，字符串形式
	 */
	private String valueString;
	/**
	 * 需要插入数据表的上下文字段
	 */
	private String contextcolumn;
	/**
	 * 需要插入数据表的上下文值
	 */
	private String congtextrefvl;
	/**
	 * 需要插入数据表的数据清除语句
	 */
	private String deleteSqlstr;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldString() {
		return fieldString;
	}

	public void setFieldString(String fieldString) {
		this.fieldString = fieldString;
	}

	public String getValueString() {
		return valueString;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	public String getContextcolumn() {
		return contextcolumn;
	}

	public void setContextcolumn(String contextcolumn) {
		this.contextcolumn = contextcolumn;
	}

	public String getCongtextrefvl() {
		return congtextrefvl;
	}

	public void setCongtextrefvl(String congtextrefvl) {
		this.congtextrefvl = congtextrefvl;
	}

	public String getDeleteSqlstr() {
		return deleteSqlstr;
	}

	public void setDeleteSqlstr(String deleteSqlstr) {
		this.deleteSqlstr = deleteSqlstr;
	}
}
