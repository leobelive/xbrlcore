package net.gbicc.xbrl.ent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.gbicc.xbrl.ent.model.ItemElement;
import net.gbicc.xbrl.ent.model.RelationMapping;
import net.gbicc.xbrl.ent.model.TupleElement;

public class InsToDataUtils {

	/**
	 * 随机生成32位ID
	 * 
	 * @return
	 */
	private static String random32() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}

	/**
	 * 处理所有的Item类型的数据
	 * 
	 * @param ies
	 * @return
	 */
	public static Map<String, List<String>> dealAllElements(
			List<ItemElement> ies, List<TupleElement> tes) {
		// 读取配置信息中存放item类型数据的表
		List<String> tables = SqlUtils.getTableNames();
		// 按照表名称遍历配置信息，读取每个table的详细Mapping信息，以tableName为主键
		Map<String, List<RelationMapping>> m_rm = new HashMap<String, List<RelationMapping>>();
		for (String tn : tables) {
			m_rm.put(tn, SqlUtils.getMappingRelation(tn));
		}
		/************************ 对实例文档内容的处理 **************************************/
		// 读取item类型元素存放数据的上下文列表
		List<String> contextRefs = new ArrayList<String>();
		for (ItemElement ie : ies) {
			if (!contextRefs.contains(ie.getContextRef())) {
				contextRefs.add(ie.getContextRef());
			}
		}
		// 读取tuple类型元素的存放的上下文列表，如果item列表中存在重复的，则不导入
		for (TupleElement te : tes) {
			for (int i = 0; i < te.getChildren().size(); i++) {
				ItemElement ie = (ItemElement) te.getChildren().get(i);
				if (!contextRefs.contains(ie.getContextRef())) {
					contextRefs.add(ie.getContextRef());
				}
			}
		}
		// 按照上下文读取itemElements信息，使用context做主键形成Map
		Map<String, List<ItemElement>> m_itemWithContextRef = new HashMap<String, List<ItemElement>>();
		for (String ref : contextRefs) {
			List<ItemElement> itemWithContext = new ArrayList<ItemElement>();
			// 遍历item元素对象的列表
			for (ItemElement ie : ies) {
				if (ie.getContextRef().equals(ref)) {
					itemWithContext.add(ie);
				}
			}
			// 遍历tuple元素对象的列表，主要对tuple对象中的子元素（item）进行处理
			for (TupleElement te : tes) {
				for (int i = 0; i < te.getChildren().size(); i++) {
					ItemElement ie = (ItemElement) te.getChildren().get(i);
					if (ie.getContextRef().equals(ref)) {
						itemWithContext.add(ie);
					}
				}
			}
			m_itemWithContextRef.put(ref, itemWithContext);
		}
		//
		/*********************************** 生成SQL的处理 *************************************/
		// 调用createImpactSQL生成insertSQL的列表
		Map<String, List<String>> m_dealSql = new HashMap<String, List<String>>();
		List<String> insertSQLs = new ArrayList<String>();
		List<String> deleteSQLs = new ArrayList<String>();
		for (String key1 : m_rm.keySet()) {
			for (String key2 : m_itemWithContextRef.keySet()) {
				String deleteSQL = createDeleteSQL(key1, key2, m_rm.get(key1));
				String insertSQL = createImpactSQL(m_rm.get(key1),
						m_itemWithContextRef.get(key2), key1, key2);
				deleteSQLs.add(deleteSQL);
				if (!insertSQL.equalsIgnoreCase("NOSQL")) {
					insertSQLs.add(insertSQL);
				}
			}
		}
		m_dealSql.put("INSERT", insertSQLs);
		m_dealSql.put("DELETE", deleteSQLs);
		// 返回生成的数据插入SQL语句
		return m_dealSql;
	}

	/**
	 * 
	 * @param resInTable
	 *            一个表的映射关系
	 * @param iesSameContext
	 *            相同上下文的元素对象清单
	 * @param tablename
	 *            插入的目标表
	 * @param contextRef
	 *            上下文的内容
	 * @return
	 */
	public static String createImpactSQL(List<RelationMapping> rsInTable,
			List<ItemElement> iesSameContext, String tablename,
			String contextRef) {
		String fieldNames = "ID" + ",";
		String valueString = "'" + random32() + "',";
		int elementCount = 0;
		for (RelationMapping rs : rsInTable) {
			if (rs.getParten().equalsIgnoreCase("2")) {
				fieldNames += rs.getFieldName() + ",";
				valueString += "'" + contextRef + "',";
				continue;
			}
			for (ItemElement ie : iesSameContext) {
				if (rs.getElementName().equals(
						ie.getPrefix() + ":" + ie.getName())) {
					fieldNames += rs.getFieldName() + ",";
					valueString += "'" + ie.getValue() + "',";
					elementCount++;
					break;
				}
			}
		}
		if (elementCount > 0) {
			String insertSql = "insert into " + tablename + " ("
					+ fieldNames.substring(0, fieldNames.length() - 1)
					+ ") values ("
					+ valueString.substring(0, valueString.length() - 1) + ")";
			return insertSql;
		} else {
			return "NOSQL";
		}
	}

	/**
	 * 按照表名和上下文字段删除数据
	 * 
	 * 需要通过映射关系表，确认哪个是上下文字段
	 * 
	 * @param tablename
	 *            表名
	 * @param contextRef
	 *            上下文
	 * @param rsInTable
	 *            相关的映射内容
	 * @return
	 */
	public static String createDeleteSQL(String tablename, String contextRef,
			List<RelationMapping> rsInTable) {
		String contextColumn = "";
		for (RelationMapping rs : rsInTable) {
			if (rs.getParten().equalsIgnoreCase("2")) {
				contextColumn = rs.getFieldName();
				break;
			}
		}
		return "delete from " + tablename + " where " + contextColumn + " = '"
				+ contextRef + "'";
	}
}
