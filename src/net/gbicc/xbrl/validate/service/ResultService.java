package net.gbicc.xbrl.validate.service;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;

import java.util.List;

import net.gbicc.xbrl.core.TaxonomySet;
import net.gbicc.xbrl.core.XbrlInstance;

import net.gbicc.xbrl.core.XbrlMessage;

import net.gbicc.xbrl.ent.instance.InstanceResolver;
import net.gbicc.xbrl.ent.taxonomy.TaxonomyReader;

import org.springframework.stereotype.Service;

/**
 * 校验结果展示实现类
 * 
 * @author joephoenix
 */
@Service("resultService")
public class ResultService {

	public List<XbrlMessage> returnErrorList() throws IOException {
		// String path = this.getClass().getResource("").getPath();
		String rootPath = "C://XBRLCACHE//xbrlcore";
		// path = path.replace("classes", "");
		String instancePath = rootPath + "/instances/"
				+ "80000222_0000_JNWT01Y_20160630_V02.xml";
		String taxonomyBase = rootPath + "/taxonomies/";
		File instanceFile = new File(instancePath);
		String fileName = instanceFile.getName();
		FileInputStream fisInstance = new FileInputStream(instanceFile);
		byte[] buffer = new byte[fisInstance.available()];
		fisInstance.read(buffer);
		fisInstance.close();
		String location = "http://www.ssf.gov.cn/jnwt/jnser/ssf_jnwt_ser_2014-01-10.xsd";
		List<XbrlMessage> messageList = validate(buffer, taxonomyBase,
				location, fileName);
		return messageList;
	}

	public List<XbrlMessage> validate(byte[] content, String taxonomyBase,
			String location, String fileName) {
		if (content != null && content.length > 0) {
			TaxonomyReader taxonomyReader = TaxonomyReader.getInstance();
			taxonomyReader.setCacheBase(taxonomyBase);
			TaxonomySet taxonomySet = taxonomyReader.getTaxonomySet(location);
			InstanceResolver instanceResolver = new InstanceResolver(
					taxonomySet, content, fileName);
			XbrlInstance xbrlInstance = instanceResolver.getXbrlInstance();
			List<XbrlMessage> messages = InstanceResolver.validateInstance(
					xbrlInstance, true);
			return messages;
		} else {
			return null;
		}
	}
}
