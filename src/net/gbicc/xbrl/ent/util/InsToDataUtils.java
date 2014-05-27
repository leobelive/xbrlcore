package net.gbicc.xbrl.ent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.gbicc.xbrl.ent.model.GeneImport;
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
	public static List<GeneImport> dealAllElements(List<ItemElement> ies,
			List<TupleElement> tes) {
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
		/**************** 调用createImpartSQL和createRowImpartSQL方法生成数据导入的基本因子的列表 *********************/
		List<GeneImport> ls_dealGene = new ArrayList<GeneImport>();
		for (String key1 : m_rm.keySet()) {
			String[] tnfragiles = key1.split("_");
			if (tnfragiles.length == 0) {
				System.out.println("The " + key1 + " is illegal!");
				continue;
			}
			for (String key2 : m_itemWithContextRef.keySet()) {
				if (tnfragiles[0].equalsIgnoreCase("RD")) {
					// 生成导入到关系型数据库表的SQL语句
					Map<String, String> cfv = createRelationImpartSQL(
							m_rm.get(key1), m_itemWithContextRef.get(key2));
					if (cfv != null && cfv.keySet().size() >= 3) {
						GeneImport gi = new GeneImport();
						// 设置导入因子的表名
						gi.setTableName(key1);
						// 设置导入因子的上下文值
						gi.setCongtextrefvl(key2);
						// 设置上下文对应的字段
						gi.setContextcolumn(cfv.get("contextColumn"));
						// 设置导入需要的字段字符串
						gi.setFieldString(cfv.get("fieldString"));
						// 设置需要导入值的字符串
						gi.setValueString(cfv.get("valueString"));
						// 生成删除原有数据的SQL语句，以上下文为条件,并且设置
						String deleteSQL = createRelationDeleteSQL(key1, key2,
								m_rm.get(key1));
						gi.setDeleteSqlstr(deleteSQL);
						// 加入到列表中
						ls_dealGene.add(gi);
					}
				} else if (tnfragiles[0].equalsIgnoreCase("LT")) {
					// 生成删除原有数据的SQL语句，以上下文为条件
					String deleteSQL = createRowDeleteSQL(key1, key2);
					// 生成导入到行式数据库表的SQL语句
					List<GeneImport> rowGeneList = createRowImpactSQL(key1,
							key2, deleteSQL, m_rm.get(key1),
							m_itemWithContextRef.get(key2));
					if (rowGeneList != null) {
						ls_dealGene.addAll(rowGeneList);
					}
				} else {
					System.out.println("The " + key1 + " is illegal, "
							+ "not the RD type or LT type");
					continue;
				}
			}
		}
		return ls_dealGene;
	}

	/***
	 * 生成导数的SQL语句，处理映射成行列式数据库表的元素
	 * 
	 * 由于行式数据库表的字段都是固定的,所以拼接SQL语句也是采用固定的方式
	 * 
	 * @param tn
	 *            导入数据的表名称
	 * 
	 * @param ctv
	 *            上下文的值
	 * 
	 * @param dstr
	 *            删除数据库表中数据的delete语句
	 * 
	 * @param rslst
	 *            一个表的映射关系
	 * 
	 * @param ielst
	 *            相同上下文的元素对象清单
	 * 
	 * @return
	 */
	public static List<GeneImport> createRowImpactSQL(String tn, String ctv,
			String dstr, List<RelationMapping> rslst, List<ItemElement> ielst) {
		List<GeneImport> results = new ArrayList<GeneImport>();
		for (RelationMapping rm : rslst) {
			for (ItemElement ie : ielst) {
				if (rm.getElementName().equals(
						ie.getPrefix() + ":" + ie.getName())) {
					// 生成ID的column和value
					String fieldNames = "ID" + ",";
					String valueString = "'" + random32() + "',";
					// 生成元素的column和value
					fieldNames = fieldNames + "ELEMENTNAME" + ",";
					valueString = valueString + "'" + rm.getElementName()
							+ "',";
					// 生成元素对应的值的column和value
					fieldNames = fieldNames + "ELEMENTVALUE" + ",";
					valueString = valueString + "'" + ie.getValue() + "',";
					// 生成元素对应的上下文的column和value
					// fieldNames = fieldNames + "CONTEXTREF" + ",";
					// valueString = valueString + "'" + ie.getContextRef() +
					// "',";
					// 生成元素对应的单位上下文的column和value
					fieldNames = fieldNames + "UNITREF" + ",";
					valueString = valueString + "'" + ie.getUnitRef() + "',";
					// 生成数据创建的时间戳
					fieldNames = fieldNames + "CREATETIME";
					valueString = valueString + "sysdate";
					/************ 拼接insertSQL语句 **************/
					GeneImport gi = new GeneImport();
					// 设置导入因子的表名
					gi.setTableName(tn);
					// 设置导入因子的上下文值
					gi.setCongtextrefvl(ctv);
					// 设置上下文对应的字段
					gi.setContextcolumn("CONTEXTREF");
					// 设置导入需要的字段字符串
					gi.setFieldString(fieldNames);
					// 设置需要导入值的字符串
					gi.setValueString(valueString);
					// 删除清空数据的SQL语句
					gi.setDeleteSqlstr(dstr);
					// 加入到列表中
					results.add(gi);
				}
			}
		}
		return results;
	}

	/**
	 * 
	 * 生成导数的SQL语句，处理映射成关系型数据库表的元素
	 * 
	 * @param resInTable
	 *            一个表的映射关系
	 * 
	 * @param iesSameContext
	 *            相同上下文的元素对象清单
	 * 
	 * @return 一个Map,这个map包含三个键值，分别是contextColumn，fieldString和valueString，
	 *         其value值则存放上下文的字段名称、拼接的字段连接字符串和拼接的值字符串
	 */
	public static Map<String, String> createRelationImpartSQL(
			List<RelationMapping> rsInTable, List<ItemElement> iesSameContext) {
		Map<String, String> results = new HashMap<String, String>();
		String fieldNames = "ID" + ",";
		String valueString = "'" + random32() + "',";
		int elementCount = 0;
		for (RelationMapping rs : rsInTable) {
			if (rs.getParten().equalsIgnoreCase("2")) {
				// fieldNames += rs.getFieldName() + ",";
				// valueString += "'" + contextRef + "',";
				results.put("contextColumn", rs.getFieldName());
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
			// 添加创建数据的时间戳
			fieldNames += "CREATETIME";
			valueString += "sysdate";
			/************
			 * 拼接insertSQL语句，暂时不用这个方法
			 * 
			 * 还是分成两个字符串，一个存放列表，一个存放数据值
			 * 
			 * **************/
			// String insertSql = "insert into " + tablename + " (" + fieldNames
			// + ") values (" + valueString + ")";
			results.put("fieldString", fieldNames);
			results.put("valueString", valueString);
			return results;
		} else {
			return null;
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
	public static String createRelationDeleteSQL(String tablename,
			String contextRef, List<RelationMapping> rsInTable) {
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

	/**
	 * 返回行式类型表的删除语句
	 * 
	 * @param tablename
	 *            表名
	 * @param contextRef
	 *            上下文值
	 * @return
	 */
	public static String createRowDeleteSQL(String tablename, String contextRef) {
		return "delete from " + tablename + " where CONTEXTREF = '"
				+ contextRef + "'";
	}
}
