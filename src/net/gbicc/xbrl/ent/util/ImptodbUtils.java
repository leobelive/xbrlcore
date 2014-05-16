package net.gbicc.xbrl.ent.util;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import net.gbicc.xbrl.ent.model.InstanceDocument;
import net.gbicc.xbrl.ent.model.ItemElement;
import net.gbicc.xbrl.ent.model.TupleElement;

public class ImptodbUtils {

	/**
	 * 把实例文档的数据导入到相应的数据库表的方法
	 * 
	 * 此方法还不完善，一个参数少了，还需要加上“元素”和“表字段”对应的配置信息文件或者表
	 * 
	 * 尽量读取一个表的所有字段，灵活配置这些字段相对应的元素，
	 * 
	 * 如果是时间戳、操作人等公用字段，也采用灵活方式实现
	 * 
	 * @param instance
	 *            实例文档的二进制代码
	 */
	public String importToDB(byte[] instance) {
		List insertList = new LinkedList();// 插入语句
		List delSQlList = new LinkedList();// 删除语句
		// 生成固定字段
		String fixedField = XmlToDBUtils.createFixedField();
		// 输入操作人的id号
		String fixedValue = XmlToDBUtils.createFixedValue("ddddd");

		// 读实例文档，获得tupleList,itemList、contexts
		InstanceDocument instanceDocument = new InstanceDocument();
		instanceDocument = InstanceUtils.readInstance(instance);
		// 读取普通元素列表
		List<ItemElement> itemList = instanceDocument.getItemList();
		// 读取TUPLE类型元素列表
		List<TupleElement> tupleList = instanceDocument.getTupleList();

		// 生成相关表类型的SQL语句
		XmlToDBUtils.dealTupleList(insertList, delSQlList, fixedField,
				fixedValue, instanceDocument);
		XmlToDBUtils.dealItemList(insertList, delSQlList, fixedField,
				fixedValue, instanceDocument);

		// 保存SQL语句到本地,方便检查
		writeSQLTOfile(insertList, "ddddd");
		/**
		 * 批量执行SQl入库
		 **/
		try {
			SqlUtils.executeSQLs(delSQlList);
			SqlUtils.executeSQLs(insertList);
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * 保存文件到本地
	 * 
	 * @param insertList
	 */
	public void writeSQLTOfile(List insertList, String fileName) {
		// 生成文件的完整路径
		String ROOT_PATH = this.getClass().getResource("").getPath();
		String rootPath = ROOT_PATH.substring(0,
				ROOT_PATH.indexOf("classes") + 8);
		String instancePath = rootPath + "resource/" + fileName + ".txt";
		// 写入信息到文件中
		FileWriter writer;
		try {
			writer = new FileWriter(instancePath);
			for (int i = 0; i < insertList.size(); i++) {
				writer.write((String) insertList.get(i) + ";");
				writer.write("\r\n");
			}

			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
