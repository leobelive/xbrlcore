package net.gbicc.xbrl.ent.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InstanceDocument {
	/****** 存放item类型的元素 ******/
	private List itemList = new LinkedList();
	/****** 存放tuple类型的元素 ******/
	private List tupleList = new LinkedList();
	/** 存放时点型的上下文* */
	private HashMap instantContextMap = new HashMap();
	/** 存放区间型的上下文 */
	private HashMap durationContextMap = new HashMap();
	/**
	 * *以namespace+name 为key, tuple List 为 value，
	 * 
	 * list里面为TupleElement对象******
	 */
	private Map tupleListMapByFullName = new HashMap();

	public List getItemList() {
		return itemList;
	}

	public void setItemList(List itemList) {
		this.itemList = itemList;
	}

	public List getTupleList() {
		return tupleList;
	}

	public void setTupleList(List tupleList) {
		this.tupleList = tupleList;
	}

	public HashMap getInstantContextMap() {
		return instantContextMap;
	}

	public void setInstantContextMap(HashMap instantContextMap) {
		this.instantContextMap = instantContextMap;
	}

	public HashMap getDurationContextMap() {
		return durationContextMap;
	}

	public void setDurationContextMap(HashMap durationContextMap) {
		this.durationContextMap = durationContextMap;
	}

	public Map getTupleListMapByFullName() {
		return tupleListMapByFullName;
	}

	public void setTupleListMapByFullName(Map tupleListMapByFullName) {
		this.tupleListMapByFullName = tupleListMapByFullName;
	}
}
