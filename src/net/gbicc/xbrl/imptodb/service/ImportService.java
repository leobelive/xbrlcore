package net.gbicc.xbrl.imptodb.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.gbicc.xbrl.ent.util.ImptodbUtils;

import org.springframework.stereotype.Service;

/**
 * 实例文档导入数据库指定表的服务和方法
 * 
 * @author joephoenix
 * 
 */
@Service("importService")
public class ImportService {

	/**
	 * 导入实例文档的数据到相应数据库对应表
	 * 
	 * @param instancePath
	 * @throws IOException
	 */
	public String importInstance(String instancePath) throws IOException {
		String rootPath = this.getClass().getResource("").getPath();
		rootPath = rootPath.substring(0, rootPath.indexOf("classes") + 8);
		// 读取实例文档
		instancePath = rootPath + "instances/"
				+ "430002_GB0301_20080630_V01.xml";
		File instanceFile = new File(instancePath);
		byte[] instance = readInstance(instanceFile);
		// 把实例文档的数据导入到相应的数据库表
		ImptodbUtils ib = new ImptodbUtils();
		String rltInfo = ib.putInstanceToData(instance);
		int quotePosition = rltInfo.indexOf("::");
		if (quotePosition > -1) {
			return rltInfo.substring(quotePosition + 2, rltInfo.length());
		} else {
			List<String> errorlist = new LinkedList<String>();
			errorlist.add(rltInfo);
			ib.writeSQLTOfile(errorlist, "errorinfo");
			return "FALSE";
		}
	}

	/**
	 * 读取实例文档，形成二进制代码并返回
	 * 
	 * @param instanceFile
	 *            实例文档的文件对象
	 * @return 二进制代码
	 * @throws IOException
	 */
	public byte[] readInstance(File instanceFile) throws IOException {
		FileInputStream fisInstance = new FileInputStream(instanceFile);
		byte[] buffer = new byte[fisInstance.available()];
		fisInstance.read(buffer);
		fisInstance.close();
		return buffer;
	}
}
