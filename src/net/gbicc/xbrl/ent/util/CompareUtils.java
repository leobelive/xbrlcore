package net.gbicc.xbrl.ent.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gbicc.xbrl.ent.model.InstanceDocument;
import net.gbicc.xbrl.ent.model.ItemElement;
import net.gbicc.xbrl.ent.model.TupleElement;

public class CompareUtils {

	@SuppressWarnings("unchecked")
	public static Map<String, Integer> compare(byte[] content1, byte[] content2) {
		// 结果的map
		Map<String, Integer> cr = new HashMap<String, Integer>();

		/******** 读取第一份实例文档的内容 ********/
		// 读实例文档，获得tupleList,itemList、contexts
		InstanceDocument instanceDocument1 = new InstanceDocument();
		instanceDocument1 = InstanceUtils.readInstance(content1);
		// 普通元素列表
		List<ItemElement> itemList1 = instanceDocument1.getItemList();
		// TUPLE类型元素列表
		List<TupleElement> tupleList1 = instanceDocument1.getTupleList();

		/********** 读取第二份份实例文档的内容 **************/
		InstanceDocument instanceDocument2 = new InstanceDocument();
		instanceDocument2 = InstanceUtils.readInstance(content2);
		// 普通元素列表
		List<ItemElement> itemList2 = instanceDocument2.getItemList();
		// TUPLE类型元素列表
		List<TupleElement> tupleList2 = instanceDocument2.getTupleList();
		/*******
		 * 开始比较,使用3个方法来比较，以第一份为准，
		 * 
		 * 第一份实例文档中不存在的元素，记为增加
		 * 
		 * 第一份实例文档中存在，而第二份实例文档不存在，记为减少
		 * 
		 * 第一份实例文档和第二份文档中元素的值不一样的，记为更改
		 *******/
		cr.put("apparedElements",
				getNewApparedElements(itemList1, tupleList1, itemList2,
						tupleList2).size());
		cr.put("noUsedElements",
				getNotUsedElements(itemList1, tupleList1, itemList2, tupleList2)
						.size());
		cr.put("changedElements", getChangedElements(itemList1, itemList2)
				.size());

		return cr;
	}

	/**
	 * 判断第二份实例文档用到的元素是否在第一份实例文档中存在
	 * 
	 * 不存在的话，则视为新使用的元素
	 * 
	 * @param listItem1
	 *            第一份实例文档的item的清单
	 * @param listTuple1
	 *            第一份实例文档的tuple的清单
	 * @param listItem2
	 *            第二份实例文档的item的清单
	 * @param listTuple2
	 *            第二份实例文档的tuple的清单
	 * @return 元素名称列表
	 */
	private static List<String> getNewApparedElements(
			List<ItemElement> listItem1, List<TupleElement> listTuple1,
			List<ItemElement> listItem2, List<TupleElement> listTuple2) {
		List<String> appearElements = new ArrayList<String>();
		/**** listItem2中的元素在ListItem1中没有，则是新增的元素 ****/
		for (ItemElement ie2 : listItem2) {
			boolean isExist = false;
			for (ItemElement ie1 : listItem1) {
				if (ie1.getName().equalsIgnoreCase(ie2.getName())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				appearElements.add(ie2.getName());
			}
		}
		/**** listTuple2中的元素在listTuple1中没有，则是新增的tuple元素 ****/
		for (TupleElement te2 : listTuple2) {
			boolean isExist = false;
			for (TupleElement te1 : listTuple1) {
				if (te1.getName().equalsIgnoreCase(te2.getName())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				appearElements.add(te2.getName());
			}
		}
		return appearElements;
	}

	/**
	 * 判断第一份实例文档用到的元素是否在第二份实例文档中存在
	 * 
	 * 不存在的话，则视为这个元素不使用了
	 * 
	 * @param listItem1
	 *            第一份实例文档的item的清单
	 * @param listTuple1
	 *            第一份实例文档的tuple的清单
	 * @param listItem2
	 *            第二份实例文档的item的清单
	 * @param listTuple2
	 *            第二份实例文档的tuple的清单
	 * @return 元素名称列表
	 */
	private static List<String> getNotUsedElements(List<ItemElement> listItem1,
			List<TupleElement> listTuple1, List<ItemElement> listItem2,
			List<TupleElement> listTuple2) {
		List<String> notUsedElements = new ArrayList<String>();
		/**** listItem1中的元素在ListItem2中没有，则是不使用的item元素 ****/
		for (ItemElement ie1 : listItem1) {
			boolean isExist = false;
			for (ItemElement ie2 : listItem2) {
				if (ie2.getName().equalsIgnoreCase(ie1.getName())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				notUsedElements.add(ie1.getName());
			}
		}
		/**** listTuple1中的元素在listTuple2中没有，则是不使用的tuple元素 ****/
		for (TupleElement te1 : listTuple1) {
			boolean isExist = false;
			for (TupleElement te2 : listTuple2) {
				if (te2.getName().equalsIgnoreCase(te1.getName())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				notUsedElements.add(te1.getName());
			}
		}
		return notUsedElements;
	}

	/**
	 * 判断第一份文档中的元素和值 与 第二份实例文档的元素和值 是否相等
	 * 
	 * 如果不相等，则视为是改变的元素，计入列表
	 * 
	 * @param listItem1
	 *            第一份实例文档的item的清单
	 * @param listItem2
	 *            第二份实例文档的item的清单
	 * @return 元素名称列表
	 */
	private static List<String> getChangedElements(List<ItemElement> listItem1,
			List<ItemElement> listItem2) {
		List<String> changedElements = new ArrayList<String>();
		for (ItemElement ie1 : listItem1) {
			boolean isExist = false;
			for (ItemElement ie2 : listItem2) {
				if (ie2.getName().equalsIgnoreCase(ie1.getName())
						&& ie2.getContextRef().equalsIgnoreCase(
								ie1.getContextRef())
						&& ie2.getValue().equalsIgnoreCase(ie1.getValue())) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				changedElements.add(ie1.getName());
			}
		}
		return changedElements;
	}
}
