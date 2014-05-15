package net.gbicc.xbrl.ent.model;

import java.util.ArrayList;
import java.util.List;

public class TupleElement extends BaseElement {
	private List children = new ArrayList();

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}
}
