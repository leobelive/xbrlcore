package net.gbicc.xbrl.comparason.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import net.gbicc.xbrl.ent.util.CompareUtils;

import org.springframework.stereotype.Service;

/**
 * 实例文档比较的服务和方法
 * 
 * @author joephoenix
 * 
 */
@Service("comparasonService")
public class ComparasonService {

	public Map<String, Integer> getComparasonResult(String path1, String path2)
			throws IOException {
		String rootPath = this.getClass().getResource("").getPath();
		rootPath = rootPath.substring(0, rootPath.indexOf("classes") + 8);
		// 第一份实例文档
		path1 = rootPath + "instances/"
				+ "80000222_0000_JNWT01Y_20160630_V02.xml";
		File instanceFile1 = new File(path1);
		// 第二份实例文档
		path2 = rootPath + "instances/"
				+ "80064225_0000_JNWT01Y_20130630_V03.xml";
		File instanceFile2 = new File(path2);

		byte[] instance1 = readInstance(instanceFile1);
		byte[] instance2 = readInstance(instanceFile2);
		// 比较实例文档
		return CompareUtils.compare(instance1, instance2);
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
