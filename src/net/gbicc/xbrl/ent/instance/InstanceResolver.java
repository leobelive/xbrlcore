package net.gbicc.xbrl.ent.instance;

import java.util.ArrayList;
import java.util.List;
import system.qizx.xdm.XdmNode;
import net.gbicc.xbrl.core.HandlerContext;
import net.gbicc.xbrl.core.IXbrlDocument;
import net.gbicc.xbrl.core.TaxonomySet;
import net.gbicc.xbrl.core.XbrlContentType;
import net.gbicc.xbrl.core.XbrlDocument;
import net.gbicc.xbrl.core.XbrlInstance;
import net.gbicc.xbrl.core.XbrlLoader;
import net.gbicc.xbrl.core.XbrlMessage;
import net.gbicc.xbrl.ent.CoreUrlResolver;

public class InstanceResolver {

	private XbrlInstance xbrlInstance = null;

	public InstanceResolver(TaxonomySet dts, byte[] bytes, String entityId) {
		XbrlLoader loader = XbrlLoader.create(dts);
		CoreUrlResolver cUrlResolver = new CoreUrlResolver(entityId, bytes);
		String instanceUri = CoreUrlResolver.buildUri(entityId);
		// 创建Handler
		WebHandlerContext handler = new WebHandlerContext();
		handler.setXmlResolver(cUrlResolver);
		loader.setHandlerContext(handler);
		handler.setDefaultLang("zh");
		loader.load(instanceUri);
		IXbrlDocument instDoc = loader.getDocument(instanceUri);
		if (instDoc != null
				&& instDoc.getContentType().contains(XbrlContentType.Instance)
				&& instDoc instanceof XbrlDocument) {
			XbrlDocument xbrlDoc = (XbrlDocument) instDoc;
			for (XdmNode child = xbrlDoc.firstChild(); child != null; child = child
					.getNextSibling()) {
				if (child instanceof XbrlInstance) {
					xbrlInstance = (XbrlInstance) child;
					break;
				}
			}
		}
	}

	/**
	 * 校验实例文档
	 * 
	 * @param xbrlInstance
	 * @return
	 */
	public static List<XbrlMessage> validateInstance(XbrlInstance xbrlInstance,
			boolean traceAllCalculations) {
		if (xbrlInstance == null)
			return null;
		TaxonomySet dts = xbrlInstance.getOwnerDTS();
		if (dts == null)
			return null;
		// 创建Handler
		WebHandlerContext handler = new WebHandlerContext();
		handler.setDefaultLang("zh");
		if (traceAllCalculations) {
			handler.getOptions().setTraceAllCalculations(true);
		}
		dts.setHandlerContext(handler);
		xbrlInstance.validateXbrl21();
		return handler._messages;
	}

	public XbrlInstance getXbrlInstance() {
		return xbrlInstance;

	}
}

class WebHandlerContext extends HandlerContext {
	List<XbrlMessage> _messages = new ArrayList<XbrlMessage>();

	public void sendMessage(XbrlMessage xbrlMessage) {
		if (xbrlMessage == null) {
			return;
		}
		if (xbrlMessage.getId().equals("0")) {

		} else if (xbrlMessage.getId().equals("1")) {
		} else {
			_messages.add(xbrlMessage);
		}
	}
}
