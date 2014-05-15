package net.gbicc.xbrl.ent.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.gbicc.xbrl.core.Fact;
import net.gbicc.xbrl.core.Label;
import net.gbicc.xbrl.core.TaxonomySet;
import net.gbicc.xbrl.core.XbrlInstance;
import net.gbicc.xbrl.core.XbrlMessage;
import net.gbicc.xbrl.core.messages.CalcDetail;
import net.gbicc.xbrl.ent.instance.InstanceResolver;
import net.gbicc.xbrl.ent.model.ValidateObject;
import net.gbicc.xbrl.ent.taxonomy.TaxonomyReader;

public class ValidateUtils {

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
	public static List<ValidateObject> validate(byte[] content,
			String taxonomyBase, String location, String instanceId) {
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
						String detail = getCalculationDesc(calcDetail,
								xbrlInstance.getOwnerDTS());
						sbDes.append(title);
						sbDes.append("--");
						sbDes.append(detail);
					}
					String funRight = getFunctionDescription(calcDetail,
							xbrlInstance.getOwnerDTS());
					sbDes.append(funRight);
					ValidateObject vo = new ValidateObject();
					vo.setMessage(sbDes.toString());
					validateMessages.add(vo);
					continue;
				}
				List<String> elementsDefinition = initElementscodes();
				if (elementsDefinition.contains(xm.getId())) {
					String detail = xm.getMessage().replace("数据项 ", "")
							.replace("缺失schema元素定义", "");
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的元素定义--" + detail.replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> contents = initContentCodes();
				if (contents.contains(xm.getId())) {
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的上下文定义--"
							+ xm.getMessage().replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> props = initPropertyCodes();
				if (props.contains(xm.getId())) {
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的属性定义--"
							+ xm.getMessage().replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> tples = initTupleCodes();
				if (tples.contains(xm.getId())) {
					ValidateObject vo = new ValidateObject();
					vo.setMessage("错误的数据表格定义-"
							+ xm.getMessage().replace(":", "_"));
					validateMessages.add(vo);
					continue;
				}
				List<String> others = initTupleCodes();
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

	/**
	 * 返回元素校验相关的错误ID
	 * 
	 * 其他类型的错误,主要是数据方面的
	 * 
	 * @return
	 */
	public static List<String> initOthersCodes() {
		List<String> ioc = new ArrayList<String>();
		ioc.add("8006");
		ioc.add("8008");
		return ioc;
	}

	/**
	 * 返回元素校验相关的错误ID
	 * 
	 * tuple类型存在错误
	 * 
	 * @return
	 */
	public static List<String> initTupleCodes() {
		List<String> itc = new ArrayList<String>();
		itc.add("50");
		itc.add("77");
		itc.add("78");
		itc.add("79");
		itc.add("80");
		itc.add("81");
		return itc;
	}

	/**
	 * 返回元素校验相关的错误ID
	 * 
	 * 数值类型必须引用单位/非数值型科目严禁设置unitRef属性/除分数、明确空值外，
	 * 
	 * 其他数值型科目必须设置precision或decimals属性
	 * 
	 * @return
	 */
	public static List<String> initPropertyCodes() {
		List<String> ipt = new ArrayList<String>();
		ipt.add("65");
		ipt.add("66");
		ipt.add("67");
		ipt.add("68");
		ipt.add("69");
		ipt.add("79");
		ipt.add("71");
		ipt.add("72");
		ipt.add("73");
		return ipt;
	}

	/**
	 * 返回元素校验相关的错误ID
	 * 
	 * 上下文
	 * 
	 * @return
	 */
	public static List<String> initContentCodes() {
		List<String> CCS = new ArrayList<String>();
		CCS.add("56");
		CCS.add("59");
		CCS.add("60");
		CCS.add("61");
		CCS.add("62");
		CCS.add("63");
		CCS.add("64");
		CCS.add("64.1");
		return CCS;
	}

	/**
	 * 返回元素校验相关的错误ID
	 * 
	 * 元素定义
	 * 
	 * @return
	 */
	public static List<String> initElementscodes() {
		List<String> ecs = new ArrayList<String>();
		ecs.add("45");
		ecs.add("46");
		ecs.add("47");
		ecs.add("48");
		ecs.add("49");
		ecs.add("51");
		ecs.add("52");
		ecs.add("53");
		ecs.add("54");
		ecs.add("55");
		ecs.add("56");
		ecs.add("57");
		ecs.add("58");
		ecs.add("202");
		return ecs;
	}

	/**
	 * 读取公式的描述
	 * 
	 * @param function
	 * @return
	 */
	public static String getFunctionDescription(CalcDetail calcDetail,
			TaxonomySet taxonomySet) {
		if (calcDetail == null || taxonomySet == null)
			return null;
		StringBuilder desc = new StringBuilder("[");
		Fact fact = calcDetail.getTarget();
		List<Label> labels = fact.getConcept().getLabels(taxonomySet);
		String label = getStandardLabel(labels);
		if (StringUtils.isBlank(label)) {
			label = fact.getConcept().getId();
		}
		desc.append(label).append("=");
		for (CalcDetail.Contribution child : calcDetail.getChildFacts()) {
			String childLabel = getStandardLabel(child.getConcept().getLabels(
					taxonomySet));
			if (StringUtils.isBlank(childLabel))
				childLabel = child.getConcept().getId();
			if (child.getWeight().compareTo(new BigDecimal("0")) < 0) {
				desc.append("(").append(child.getWeight()).append(")x");
			} else {
				desc.append(child.getWeight()).append("x");
			}
			desc.append(childLabel);
			desc.append("+");
		}
		desc.deleteCharAt(desc.length() - 1);
		desc.append("]");
		return desc.toString();
	}

	/**
	 * 读取实际的不等式
	 * 
	 * @param calcDetail
	 * @param taxonomySet
	 * @return
	 */
	public static String getCalculationDesc(CalcDetail calcDetail,
			TaxonomySet taxonomySet) {
		if (calcDetail == null || taxonomySet == null)
			return null;
		StringBuilder desc = new StringBuilder();
		Fact fact = calcDetail.getTarget();
		List<Label> labels = fact.getConcept().getLabels(taxonomySet);
		String label = getStandardLabel(labels);
		if (StringUtils.isBlank(label)) {
			desc.append(fact.getConcept().getId());
		} else {
			desc.append(label);
		}
		desc.append("(");
		desc.append(fact.getInnerText()).append(") ≠ ");
		for (CalcDetail.Contribution child : calcDetail.getChildFacts()) {
			Fact childFact = child.getFact();
			String childLabel = getStandardLabel(child.getConcept().getLabels(
					taxonomySet));
			if (child.getWeight().compareTo(new BigDecimal("0")) < 0) {
				desc.append("(").append(child.getWeight()).append(")x");
			} else {
				desc.append(child.getWeight()).append("x");
			}
			desc.append(childLabel);
			desc.append("(");
			if (childFact == null || childFact.getInnerText() == null) {
				desc.append("null");
			} else {
				desc.append(childFact.getInnerText());
			}
			desc.append(")+");
		}
		desc.deleteCharAt(desc.length() - 1);
		desc.append("=").append(calcDetail.getCalcValue());
		return desc.toString();
	}

	/**
	 * 读取标准的label，作为展示只用
	 * 
	 * @param labels
	 * @return
	 */
	private static String getStandardLabel(List<Label> labels) {
		if (labels == null || labels.size() == 0)
			return null;
		for (Label label : labels) {
			if (label.isStandard())
				return label.getInnerText();
		}
		return null;
	}
}
