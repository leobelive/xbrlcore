package net.gbicc.xbrl.ent.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.gbicc.xbrl.ent.model.BasicInfo;
import net.gbicc.xbrl.ent.model.Context;
import net.gbicc.xbrl.ent.model.InstanceDocument;
import net.gbicc.xbrl.ent.model.ItemElement;
import net.gbicc.xbrl.ent.model.PublicAccount;
import net.gbicc.xbrl.ent.model.TupleElement;

public class XmlToDBUtils {
	// 固定字段
	public static final String[] Fileds = { "is_valid", "Time_mark",
			"create_prsn", "create_time", "Mdfy_prsn", "Mdfy_time" };

	/**
	 * 创建固定字段
	 * 
	 * @return
	 */
	public static String createFixedField() {
		String fixedField = "";
		for (int i = 0; i < Fileds.length; i++) {
			fixedField += Fileds[i] + ",";
		}
		fixedField = fixedField.substring(0, fixedField.length() - 1) + ")";
		return fixedField;
	}

	/**
	 * 创建固定字段的值
	 * 
	 * @return
	 */
	public static String createFixedValue(String operater) {
		// TODO Auto-generated method stub
		String fixedValue = "";
		String currentTime = new Timestamp(new Date().getTime()).toString();
		currentTime = "to_timestamp('" + currentTime
				+ "','yyyy-mm-dd hh24:mi:ss.ff')";
		fixedValue = "1," + currentTime + ",'" + operater + "'," + currentTime
				+ ",'" + operater + "'," + currentTime + ")";
		return fixedValue;
	}

	/**
	 * 处理TUPLE类型元素，生成插入语句、删除语句
	 * 
	 * @param delSQlList
	 * @param tupleList
	 *            ,实例文档中TUPLE类型列表
	 * @param insertList
	 *            ，创建的插入语句
	 * @param fixedField
	 * @param fixedValue
	 * @param message
	 * @param instanceDocument
	 */
	public static void dealTupleList(List insertList, List delSQlList,
			String fixedField, String fixedValue,
			InstanceDocument instanceDocument) {
		List<TupleElement> tupleList = instanceDocument.getTupleList();// TUPLE类型元素列表
		List<TupleElement> delTupleList = new LinkedList();// 未创建单独表的TUPLE
		for (TupleElement tupleElement : tupleList) {
			int tupleSize = SqlUtils.getTuples(tupleElement.getName());
			if (tupleSize == 0) {// tuple类型未创建单独的表
				delTupleList.add(tupleElement);
			}
			List<BasicInfo> list = new LinkedList();
			if (tupleSize > 1) {// 多个TUPLE存入一个表，科目字段存储对应的科目以区分属于哪个TUPLE
				list = SqlUtils.getBasicInfoByAccount(tupleElement.getName(),
						"tuple");
				if (list.size() > 0) {
					String tableName = list.get(0).getTableName();
					// String delSQL = createDelSQl(tableName, message);
					String sql = "insert into " + tableName;
					String bianDongField = createVariableField(list,
							tupleElement.getChildren());
					String bianDongValue = createVariableValue(list,
							tupleElement.getChildren(), null, list.get(0)
									.getParten(), instanceDocument);
					sql += bianDongField + fixedField + bianDongValue
							+ fixedValue;
					// delSQlList.add(delSQL);
					insertList.add(sql);
				}

			} else {// 一个TUPLE存入一个表
				list = SqlUtils.getBasicInfos(tupleElement.getName(), "tuple");
				if (list.size() > 0) {
					String tableName = list.get(0).getTableName();
					// String delSQL = createDelSQl(tableName, message);
					String sql = "insert into " + tableName;
					String bianDongField = createVariableField(list,
							tupleElement.getChildren());
					String bianDongValue = createVariableValue(list,
							tupleElement.getChildren(), null, null,
							instanceDocument);
					sql += bianDongField + fixedField + bianDongValue
							+ fixedValue;
					// delSQlList.add(delSQL);
					insertList.add(sql);
				}
			}

		}
		// 未创建单独表的TUPLE存入XBRL
		for (TupleElement tupleElement : delTupleList) {
			List<ItemElement> childList = tupleElement.getChildren();
			for (ItemElement itemElement : childList) {
				String uuid = random32();
				String sql = "insert into TD_ELEMENTS_VALUE_ONE ";
				sql += "(Id,ELEMENT_NAME,ELEMENT_VALUE,CONTENTS," + fixedField
						+ " values('";
				sql += uuid + "','" + itemElement.getName() + "','"
						+ itemElement.getValue() + "','"
						+ itemElement.getContextRef() + "',";
				sql += fixedValue;
				// String delSQL = createDelSQl("TD_ELEMENTS_VALUE_ONE",
				// message);
				// delSQlList.add(delSQL);
				insertList.add(sql);
			}
		}
	}

	private static String createVariableValue(List<BasicInfo> list,
			List<ItemElement> children, String contextRef, String parten,
			InstanceDocument instanceDocument) {
		Map map = new LinkedHashMap();
		String product_code = null;
		String product_id = null;
		// if (message.getProduct() != null) {// 全组合类型
		// product_id = message.getProduct().getIdStr();
		// }

		// String manager_id = message.getManager().getIdStr();
		/** 生成字段 **/
		// 不固定字段
		for (BasicInfo basicInfo : list) {
			if (basicInfo.getFieldName() != null) {
				map.put(basicInfo.getElementName(), null);
			}
		}
		for (ItemElement itemElement : children) {
			contextRef = itemElement.getContextRef();
			map.put(itemElement.getName(), itemElement.getValue());
		}
		/** 生成对应值 **/
		String uuid = random32();
		String valueSql = " values ('" + uuid + "','";
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String value = (String) entry.getValue();
			valueSql += value + "','";
		}
		if (contextRef != null) {
			if (contextRef.contains("duration")) {
				Context context = (Context) instanceDocument
						.getDurationContextMap().get(contextRef);
				/*
				 * if (message.getProduct() == null) {//
				 * 为空，为全组合000，产品ID的存储为：如果标有场景，则存储组合ID，否则存储空，代表全组合 if
				 * (context.getSegment() != null) {// 上下文中有场景 product_code =
				 * context.getSegment().getValue();
				 * 
				 * } else { product_id = null; } }
				 */

			} else if (contextRef.contains("instant")) {
				Context context = (Context) instanceDocument
						.getInstantContextMap().get(contextRef);
				/*
				 * if (message.getProduct() == null) {//
				 * 为空，为全组合000，产品ID的存储为：如果标有场景，则存储组合ID，否则存储空，代表全组合 if
				 * (context.getSegment() != null) {// 上下文中有场景 product_code =
				 * context.getSegment().getValue();
				 * 
				 * } else { product_id = null; } }
				 */
			}
		}

		valueSql += contextRef + "','" + parten + "','"
		/*
		 * + message.getPeriod().getCode() + "','" + message.getReportYear() +
		 * "','" + manager_id + "','" + product_id
		 */+ "',";

		return valueSql;
	}

	/**
	 * 随机生成32位ID
	 * 
	 * @return
	 */
	private static String random32() {
		// TODO Auto-generated method stub
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}

	private static String createVariableField(List<BasicInfo> list,
			List children) {
		// TODO Auto-generated method stub
		/** 生成字段 **/
		String VariableField = " (Id,";
		String contextRef = null;
		// 不固定字段
		for (BasicInfo basicInfo : list) {
			if (basicInfo.getFieldName() != null) {
				VariableField += basicInfo.getFieldName() + ",";
			}
		}
		VariableField += "CONTENTS,ACCT_CODE,report_type, year, Manager_id, product_id,";
		return VariableField;
	}

	public static void dealItemList(List insertList, List delSQlList,
			String fixedField, String fixedValue,
			InstanceDocument instanceDocument) {
		List<ItemElement> itemList = instanceDocument.getItemList();// 普通元素列表
		List itemDelList = new ArrayList();// 要剔除的对象
		// 1.找到类型为item的表名
		List listTableName = SqlUtils.getBasicInfoByType("item");
		for (int i = 0; i < listTableName.size(); i++) {
			String tableName = (String) listTableName.get(i);
			// 2.根据表名找到，表名下对应的字段和元素
			List<PublicAccount> partenList = SqlUtils.getPartenS(tableName);
			for (int j = 0; j < partenList.size(); j++) {
				PublicAccount publicAccount = partenList.get(j);
				List<BasicInfo> list = SqlUtils.getBasicInfosItem(tableName,
						publicAccount.getCode());
				List<ItemElement> newItemList = new LinkedList();
				// 3.根据元素名，找到itemList里相同元素，放入新的newList,同时从itemList中剔除
				for (BasicInfo basicInfo : list) {
					String elementName = basicInfo.getElementName();
					for (ItemElement itemElement : itemList) {
						if (elementName.equals(itemElement.getName())) {
							newItemList.add(itemElement);
							itemDelList.add(itemElement);
						}
					}
				}
				// 4.新的newList,按上下文进行分组（新的newList存放的是表名下，所有元素的对象）
				if (tableName.equals("TD_Scale_Rank_Info")
						|| (tableName.equals("TD_Scale_Rank"))) {
					Map map = listGroupByContextRef(newItemList);
					// 5.
					Iterator iter = map.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						String contextRef = (String) entry.getKey();
						List<ItemElement> listItem = (List) entry.getValue();
						// String delSQL = createDelSQl(tableName, "");
						String sql = "insert into " + tableName;
						String bianDongField = createVariableField(list,
								listItem);
						String bianDongValue = createVariableValue(list,
								listItem, null, publicAccount.getcName(),
								instanceDocument);
						sql += bianDongField + fixedField + bianDongValue
								+ fixedValue;
						// delSQlList.add(delSQL);
						insertList.add(sql);
					}
				} else {
					String tableNameItem = list.get(0).getTableName();
					// String delSQL = createDelSQl(tableName, message);
					String sql = "insert into " + tableNameItem;
					String bianDongField = createVariableField(list,
							newItemList);
					String bianDongValue = createVariableValue(list,
							newItemList, null, publicAccount.getcName(),
							instanceDocument);
					sql += bianDongField + fixedField + bianDongValue
							+ fixedValue;
					// delSQlList.add(delSQL);
					insertList.add(sql);
				}

			}

		}
		// 剔除非XBRL形式数据
		itemList.removeAll(itemDelList);

		// 处理XBRL形式，剩余的itemList存储为XBRL形式
		for (ItemElement itemElement : itemList) {
			String uuid = random32();
			String sql = "insert into TD_ELEMENTS_VALUE_ONE ";
			sql += "(Id,ELEMENT_NAME,ELEMENT_VALUE,CONTENTS," + fixedField
					+ " values('";
			sql += uuid + "','" + itemElement.getName() + "','"
					+ itemElement.getValue() + "','"
					+ itemElement.getContextRef() + "',";
			sql += fixedValue;
			// String delSQL = createDelSQl("TD_ELEMENTS_VALUE_ONE");
			// delSQlList.add(delSQL);
			insertList.add(sql);

		}
	}

	/**
	 * 按上下文将list分组
	 * 
	 * @param newItemList
	 * @return
	 */
	private static Map listGroupByContextRef(List<ItemElement> newItemList) {
		Map<String, List> map = new LinkedHashMap();
		for (ItemElement itemElement : newItemList) {
			String contextRef = itemElement.getContextRef();
			if (map.containsKey(contextRef)) {
				List list = map.get(contextRef);
				list.add(itemElement);
			} else {
				List list = new ArrayList();
				list.add(itemElement);
				map.put(itemElement.getContextRef(), list);
			}
		}
		return map;
	}
}
