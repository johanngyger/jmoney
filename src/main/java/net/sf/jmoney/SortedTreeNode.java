package net.sf.jmoney;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Collections;

public class SortedTreeNode
	extends DefaultMutableTreeNode
	implements Comparable {

	public SortedTreeNode() {
	}

	public SortedTreeNode(Object usrObj) {
		super(usrObj, true);
	}

	public int compareTo(Object o) {
		return toString().compareTo(o.toString());
	}

	public void insert(MutableTreeNode child, int index) {
		super.insert(child, index);
		sortChildren();
	}

	public void sortChildren() {
		if (children == null)
			return;
		Collections.sort(children);
	}

}
