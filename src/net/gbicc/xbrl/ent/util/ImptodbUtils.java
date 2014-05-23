package net.gbicc.xbrl.ent.util;

import java.io.FileWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	public String putInstanceTOdata(byte[] instance) {
		// 声明返回变量
		Map<String, List<String>> m_dealSql = new HashMap<String, List<String>>();
		InstanceDocument instanceDocument = new InstanceDocument();
		instanceDocument = InstanceUtils.readInstance(instance);
		// 读取普通元素列表
		List<ItemElement> itemList = instanceDocument.getItemList();
		// 读取TUPLE类型元素列表
		List<TupleElement> tupleList = instanceDocument.getTupleList();
		/**
		 * 执行方法，生成插入数据的SQL语句，
		 * 
		 * 对tuple类型的表格遍历所有item处理，
		 * 
		 * 对item则直接遍历
		 **/
		m_dealSql = InsToDataUtils.dealAllElements(itemList, tupleList);
		// 保存SQL语句到本地,方便检查
		Calendar calendar = Calendar.getInstance();
		writeSQLTOfile(m_dealSql.get("INSERT"),
				"sql" + calendar.getTimeInMillis());
		/** 批量执行SQl入库 **/
		try {
			SqlUtils.executeSQLs(m_dealSql.get("DELETE"));
			SqlUtils.executeSQLs(m_dealSql.get("INSERT"));
			return String.valueOf("OK::" + m_dealSql.get("INSERT").size()
					+ "条数据导入成功");
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
		// String ROOT_PATH = this.getClass().getResource("").getPath();
		// String rootPath = ROOT_PATH.substring(0,ROOT_PATH.indexOf("classes")
		// + 8);
		String rootPath = "C:\\ProjectFolder\\NEEQCode\\";
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
