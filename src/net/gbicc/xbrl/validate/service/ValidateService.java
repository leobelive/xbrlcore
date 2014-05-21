package net.gbicc.xbrl.validate.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.gbicc.xbrl.core.TaxonomySet;
import net.gbicc.xbrl.core.XbrlInstance;
import net.gbicc.xbrl.core.XbrlMessage;
import net.gbicc.xbrl.core.messages.CalcDetail;
import net.gbicc.xbrl.ent.instance.InstanceResolver;
import net.gbicc.xbrl.ent.model.TaxonomyInfo;
import net.gbicc.xbrl.ent.model.ValidateObject;
import net.gbicc.xbrl.ent.taxonomy.TaxonomyReader;
import net.gbicc.xbrl.ent.util.ValidateUtils;

import org.springframework.stereotype.Service;

/**
 * 校验结果展示实现类
 * 
 * @author joephoenix
 */
@Service("validateService")
public class ValidateService {

	/**
	 * 读取服务器上保存的实例文档然后得到校验结果的方法
	 * 
	 * @return 校验结果
	 * @throws IOException
	 */
	public List<ValidateObject> returnErrorList() throws IOException {
		// 读取实例文档
		String rootPath = this.getClass().getResource("").getPath();
		rootPath = rootPath.substring(0, rootPath.indexOf("classes") + 8);
		String instancePath = rootPath + "instances/"
				+ "430002_GB0301_20080630_V01.xml";
		File instanceFile = new File(instancePath);
		String fileName = instanceFile.getName();
		byte[] instance = readInstance(instanceFile);
		TaxonomyInfo ti = getTaxonomyConfig();

		// 开始校验
		List<ValidateObject> messageList = ValidateUtils.validate(instance,
				ti.getTaxonomyBase(), ti.getImportLocation(), fileName);
		return messageList;
	}

	/**
	 * 设置分类标准的信息
	 * 
	 * @return
	 */
	public TaxonomyInfo getTaxonomyConfig() {
		String path = this.getClass().getResource("").getPath();
		path = path.substring(0, path.indexOf("classes") + 8);
		path = path + "taxonomies/";
		TaxonomyInfo ti = new TaxonomyInfo();
		ti.setTaxonomyBase(path);
		// ti.setImportLocation("http://www.ssf.gov.cn/jnwt/jnser/ssf_jnwt_ser_2014-01-10.xsd");
		// 使用新三板的分类标准
		ti.setImportLocation("http://www.neeq.com.cn/neeq/entry/dis/all/2014-01-01");
		return ti;
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
