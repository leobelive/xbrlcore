package net.gbicc.xbrl.ent.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.gbicc.xbrl.ent.model.ChildElement;
import net.gbicc.xbrl.ent.model.Context;
import net.gbicc.xbrl.ent.model.InstanceDocument;
import net.gbicc.xbrl.ent.model.ItemElement;
import net.gbicc.xbrl.ent.model.TupleElement;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

public class InstanceUtils {

	/***** XBRL文档的关键字 *******/
	public static final String[] KEYWORDS = { "schemaRef", "linkbaseRef",
			"roleRef", "arcroleRef", "context", "unit" };
	/***** 国际通用标准名称空间设置 *******/
	public static final Namespace NAME_SPACE_xsi = new Namespace("xsi",
			"http://www.w3.org/2001/XMLSchema-instance");

	private static final class Holder {
		private static final InstanceUtils dts = new InstanceUtils();
	}

	public static InstanceUtils getInstance() {
		return Holder.dts;
	}

	/**
	 * 读取一份实例文档
	 * 
	 * @param content
	 * @return
	 */
	public static InstanceDocument readInstance(byte[] content) {
		InstanceDocument instanceDocument = new InstanceDocument();
		Document document = copyByteArrayToDocument(content);
		readTupleAndItem(instanceDocument, document);
		readContext(instanceDocument, document);
		return instanceDocument;
	}

	/**
	 * 将byte数组转成Document
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Document copyByteArrayToDocument(byte[] byteArray) {
		if (byteArray == null) {
			return null;
		}
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read(new ByteArrayInputStream(byteArray));
		} catch (DocumentException ex) {
			// ex.printStackTrace();
			throw new IllegalArgumentException(ex.getMessage());
		}
		return document;
	}

	/**
	 * 读取所有元素内容，包括tuple类型和item类型，分别放到不同的list
	 * 
	 * @param instanceDocument
	 * @param document
	 */
	private static void readTupleAndItem(InstanceDocument instanceDocument,
			Document document) {
		Element rootElement = document.getRootElement();
		Iterator childIterator = rootElement.elementIterator();
		while (childIterator.hasNext()) {
			Element childElement = (Element) childIterator.next();
			if (isKeyWord(childElement.getName())) {
				// 是不是关键元素。如果是，continue
				continue;
			}
			Iterator iterator = childElement.elementIterator();
			if (!iterator.hasNext()) {
				// 没有下级
				List itemList = instanceDocument.getItemList();
				ItemElement itemElement = new ItemElement();
				itemElement.setContextRef(childElement
						.attributeValue("contextRef"));
				itemElement.setPrefix(childElement.getNamespacePrefix());
				itemElement.setNameSpace(childElement.getNamespaceURI());
				itemElement.setName(childElement.getName());
				itemElement.setValue(childElement.getText());
				itemElement.setUnitRef(childElement.attributeValue("unitRef"));
				itemElement
						.setDecimals(childElement.attributeValue("decimals"));
				itemElement.setPrecision(childElement
						.attributeValue("precision"));
				QName name = new QName("nil", InstanceUtils.NAME_SPACE_xsi);
				Attribute attribute = childElement.attribute(name);
				if (attribute != null) {
					if ("true".equals(attribute.getValue())) {
						itemElement.setNull(true);
					}
				}
				itemList.add(itemElement);
			} else {
				// 有下级
				recursionTupleElement(childElement, null, instanceDocument);
			}
		}
	}

	/**
	 * 递归遍历Tuple元素
	 * 
	 * @param element
	 * @param list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void recursionTupleElement(Element element,
			TupleElement parent, InstanceDocument instanceDocument) {
		List list = instanceDocument.getTupleList();
		TupleElement tupleElement = new TupleElement();
		tupleElement.setPrefix(element.getNamespacePrefix());
		tupleElement.setNameSpace(element.getNamespaceURI());
		tupleElement.setName(element.getName());
		list.add(tupleElement);
		if (parent != null) {
			parent.getChildren().add(tupleElement);
		}
		List childList = tupleElement.getChildren();
		Iterator iterator = element.elementIterator();
		while (iterator.hasNext()) {
			Element childElement = (Element) iterator.next();
			Iterator childIterator = childElement.elementIterator();
			if (!childIterator.hasNext()) {
				// 没有下级，ItemElement元素
				ItemElement itemElement = new ItemElement();
				itemElement.setContextRef(childElement
						.attributeValue("contextRef"));
				itemElement.setPrefix(childElement.getNamespacePrefix());
				itemElement.setNameSpace(childElement.getNamespaceURI());
				itemElement.setName(childElement.getName());
				itemElement.setValue(childElement.getText());
				itemElement.setUnitRef(childElement.attributeValue("unitRef"));
				itemElement
						.setDecimals(childElement.attributeValue("decimals"));
				itemElement.setPrecision(childElement
						.attributeValue("precision"));
				QName name = new QName("nil", InstanceUtils.NAME_SPACE_xsi);
				Attribute attribute = childElement.attribute(name);
				if (attribute != null) {
					if ("true".equals(attribute.getValue())) {
						itemElement.setNull(true);
					}
				}
				childList.add(itemElement);
			} else {
				// 有下级
				recursionTupleElement(childElement, tupleElement,
						instanceDocument);
			}
		}
		List tupleList = (List) instanceDocument.getTupleListMapByFullName()
				.get(tupleElement.getNameSpace() + tupleElement.getName());
		if (tupleList != null) {
			tupleList.add(tupleElement);
		} else {
			List result = new LinkedList();
			result.add(tupleElement);
			instanceDocument.getTupleListMapByFullName().put(
					tupleElement.getNameSpace() + tupleElement.getName(),
					result);
		}
	}

	/**
	 * 读取上下文信息，包括instant和duration两种类型上下文
	 * 
	 * @param instanceDocument
	 * @param document
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void readContext(InstanceDocument instanceDocument,
			Document document) {
		List contexts = new ArrayList();
		Iterator iter = contexts.iterator();
		while (iter.hasNext()) {
			Element contextElement = (Element) iter.next();
			Context context = new Context();
			QName id = new QName("id");
			Attribute attributeId = contextElement.attribute(id);
			context.setId(attributeId.getValue());
			Iterator iterator = contextElement.elementIterator("entity");
			if (iterator.hasNext()) {
				Element entityElement = (Element) iterator.next();
				Iterator identifierIterator = entityElement
						.elementIterator("identifier");
				if (identifierIterator.hasNext()) {
					Element identifierElement = (Element) identifierIterator
							.next();
					context.setIdentifier(identifierElement.getTextTrim());
					QName qname = new QName("scheme");
					Attribute attribute = identifierElement.attribute(qname);
					context.setScheme(attribute.getValue());
				}
				Iterator segmentIterator = entityElement
						.elementIterator("segment");
				if (segmentIterator.hasNext()) {
					Element identifierElement = (Element) segmentIterator
							.next();
					Iterator iteratorChildrenIdentifierElement = identifierElement
							.elementIterator();
					if (iteratorChildrenIdentifierElement.hasNext()) {
						Element segment = (Element) iteratorChildrenIdentifierElement
								.next();
						ChildElement childElement = new ChildElement();
						childElement.setName(segment.getName());
						childElement.setValue(segment.getTextTrim());
						childElement.setNameSpace(segment.getNamespaceURI());
						childElement.setPrefix(segment.getNamespacePrefix());
						context.setSegment(childElement);
					}

				}
			}

			iterator = contextElement.elementIterator("period");
			if (iterator.hasNext()) {
				Element entityElement = (Element) iterator.next();
				Iterator dateIterator = entityElement
						.elementIterator("instant");
				if (dateIterator.hasNext()) {
					Element dateElement = (Element) dateIterator.next();
					String date = dateElement.getTextTrim();
					context.setEndDate(date);
					context.setType("instant");
					instanceDocument.getInstantContextMap().put(
							context.getId(), context);
					continue;
				}

				dateIterator = entityElement.elementIterator("startDate");
				if (dateIterator.hasNext()) {
					Element dateElement = (Element) dateIterator.next();
					context.setStartDate(dateElement.getTextTrim());
					context.setType("duration");
				}

				dateIterator = entityElement.elementIterator("endDate");
				if (dateIterator.hasNext()) {
					Element dateElement = (Element) dateIterator.next();
					context.setEndDate(dateElement.getTextTrim());
					instanceDocument.getDurationContextMap().put(
							context.getId(), context);
				}
			}
		}
	}

	/**
	 * 判断是不是关键字
	 * 
	 * @param name
	 * @return false 非关键字 true 关键字
	 */
	private static boolean isKeyWord(String name) {
		if (StringUtils.isBlank(name)) {
			return false;
		}

		for (int i = 0; i < KEYWORDS.length; i++) {
			if (KEYWORDS[i].equals(name)) {
				return true;
			}
		}
		return false;
	}
}
