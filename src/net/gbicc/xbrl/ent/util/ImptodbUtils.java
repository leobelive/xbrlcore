package net.gbicc.xbrl.ent.util;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.gbicc.xbrl.ent.model.GeneImport;
import net.gbicc.xbrl.ent.model.InstanceDocument;
import net.gbicc.xbrl.ent.model.ItemElement;
import net.gbicc.xbrl.ent.model.TupleElement;

public class ImptodbUtils {

	/**
	 * 为新三板系统导入实例文档数据到相应的数据库开发的特定方法
	 * 
	 * 在得到通用方法执行得到入库因子列表的基础上，进行特定的处理，得到入库的insert语句
	 * 
	 * 这些insert语句中，会加入新三板报表要求的字段内容，如年度、报告类型等
	 * 
	 * @param instance
	 *            实例文档的二进制流
	 * @param reportScope
	 *            报告年度
	 * @param reportType
	 *            报告类型
	 * @return
	 */
	public String mainImpartFun(byte[] instance, String reportScope,
			String reportType) {
		// 声明需要的对象
		List<GeneImport> geneList = putInstanceToGenes(instance);
		List<String> insertSQLS = new ArrayList<String>();
		List<String> deleteSQLS = new ArrayList<String>();
		// 遍历导数因子的列表，对每个因子进行处理
		for (GeneImport gi : geneList) {
			/** 判断是否是本期报告的值，主要是上下文中是否包含本期报告时间 **/
			if (gi.getCongtextrefvl().contains(reportScope)) {
				// 从导入数据的因子中读取字段和值的拼接字符串
				String fieldString = gi.getFieldString() + ",";
				String valueString = gi.getValueString() + ",";
				// 加入并设置上下文的字段和值
				fieldString += gi.getContextcolumn() + ",";
				valueString += "'" + gi.getCongtextrefvl() + "',";
				// 加入并设置会计年度的字段和值
				fieldString += "ACCOUNTING_YEAR,";
				valueString += "'" + reportScope + "',";
				// 加入并设置报告类型的字段和值
				fieldString += "REPORT_TYPE";
				valueString += "'" + reportType + "'";

				// 生成insert数据的SQL语句
				String insertSql = "insert into " + gi.getTableName() + " ("
						+ fieldString + ") values (" + valueString + ")";
				insertSQLS.add(insertSql);

				// 把清除数据的SQL语句加入的列表中
				if (!deleteSQLS.contains(gi.getDeleteSqlstr())) {
					deleteSQLS.add(gi.getDeleteSqlstr());
				}
			}
		}
		// 保存SQL语句到本地,方便检查
		Calendar calendar = Calendar.getInstance();
		writeSQLTOfile(insertSQLS, "sql" + calendar.getTimeInMillis());

		// 执行SQL语句，插入数据,先执行清空语句，再执行数据插入语句
		try {
			SqlUtils.executeSQLs(deleteSQLS);
			/******************************/
			SqlUtils.executeSQLs(insertSQLS);
			return String.valueOf("OK::" + insertSQLS.size() + "条数据导入成功");
		} catch (Exception sqlex) {
			sqlex.printStackTrace();
			return sqlex.getMessage();
		}

	}

	/**
	 * 
	 * 读取实例文档解析入库的主方法
	 * 
	 * 在实例文档解析完成之后，返回入库因子对象的列表
	 * 
	 * 后继对列表的处理，可以得到导入到SQL语句的SQL语句
	 * 
	 * 这个是主方法！
	 * 
	 * @param instance
	 *            实例文档的二进制代码
	 */
	public List<GeneImport> putInstanceToGenes(byte[] instance) {
		// 声明返回变量
		List<GeneImport> ls_genes = new ArrayList<GeneImport>();
		// 读取实例文档
		InstanceDocument instanceDocument = new InstanceDocument();
		instanceDocument = InstanceUtils.readInstance(instance);
		// 读取普通元素列表
		List<ItemElement> itemList = instanceDocument.getItemList();
		// 读取tuple类型元素列表
		List<TupleElement> tupleList = instanceDocument.getTupleList();
		/**
		 * 执行方法，生成插入数据的SQL语句，
		 * 
		 * 对tuple类型的表格遍历所有item处理，
		 * 
		 * 对item则直接遍历
		 **/
		return InsToDataUtils.dealAllElements(itemList, tupleList);
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
