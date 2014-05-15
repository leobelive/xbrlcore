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
				+ "80000222_0000_JNWT01Y_20160630_V02.xml";
		File instanceFile = new File(instancePath);
		String fileName = instanceFile.getName();
		byte[] instance = readInstance(instanceFile);
		TaxonomyInfo ti = getTaxonomyConfig();

		// 开始校验
		List<ValidateObject> messageList = validate(instance,
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
		ti.setImportLocation("http://www.ssf.gov.cn/jnwt/jnser/ssf_jnwt_ser_2014-01-10.xsd");
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

	/**
	 * 对实例文档进行校验
	 * 
	 * 按照分类标准的规则
	 * 
	 * 
	 * @param content
	 *            实例文档的二进制代码
	 * @param taxonomyBase
	 *            分类标准的根地址
	 * @param location
	 *            分类标准的引用路径
	 * @param instanceId
	 *            文件名称，用以区分实例文档实力的唯一性
	 * @return
	 */
	public List<ValidateObject> validate(byte[] content, String taxonomyBase,
			String location, String instanceId) {
		if (content != null && content.length > 0) {
			TaxonomyReader taxonomyReader = TaxonomyReader.getInstance();
			taxonomyReader.setCacheBase(taxonomyBase);
			TaxonomySet taxonomySet = taxonomyReader.getTaxonomySet(location);
			InstanceResolver instanceResolver = new InstanceResolver(
					taxonomySet, content, instanceId);
			XbrlInstance xbrlInstance = instanceResolver.getXbrlInstance();
			List<XbrlMessage> messages = InstanceResolver.validateInstance(
					xbrlInstance, true);

			// 对校验的结果进行处理，组成易于阅读的列表
			List<ValidateObject> validateMessages = new ArrayList<ValidateObject>();
			for (XbrlMessage xm : messages) {
				Object tag = xm.getTag();
				if (tag != null && tag instanceof CalcDetail) {
					CalcDetail calcDetail = (CalcDetail) tag;
					StringBuilder sbDes = new StringBuilder();
					if ("129".equals(xm.getId())) {
						String title = "等式不成立";
						String detail = ValidateUtils.getCalculationDesc(
								calcDetail, xbrlInstance.getOwnerDTS());
						sbDes.append(title);
						sbDes.append("--");
						sbDes.append(detail);
					}
					String funRight = ValidateUtils.getFunctionDescription(
							calcDetail, xbrlInstance.getOwnerDTS());
					sbDes.append(funRight);
					ValidateObject vo = new ValidateObject();
					vo.setMessage(sbDes.toString());
					validateMessages.add(vo);
					continue;
				}
				List<String> elementsDefinition = ValidateUtils
						.initElementscodes();
				if (elementsDefinition.contains(xm.getId())) {
					String detail = xm.getMessage().replace("数据项 ", "")
							.replace("缺失schema元素定义", "");
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的元素定义--" + detail.replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> contents = ValidateUtils.initContentCodes();
				if (contents.contains(xm.getId())) {
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的上下文定义--"
							+ xm.getMessage().replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> props = ValidateUtils.initPropertyCodes();
				if (props.contains(xm.getId())) {
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的属性定义--"
							+ xm.getMessage().replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> tples = ValidateUtils.initTupleCodes();
				if (tples.contains(xm.getId())) {
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的数据表格定义-"
							+ xm.getMessage().replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> others = ValidateUtils.initTupleCodes();
				if (others.contains(xm.getId())) {
					String msg = xm.getMessage();
					if (msg.indexOf(":") > 0) {
						msg = msg.substring(0, msg.indexOf(":") - 1);
					}
					ValidateObject vo = new ValidateObject();
					vo.setMessage("元素的内容存在错误--" + msg.replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
			}
			return validateMessages;
		} else {
			return null;
		}
	}
}
