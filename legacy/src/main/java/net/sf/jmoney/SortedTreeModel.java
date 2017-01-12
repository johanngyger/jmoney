package net.sf.jmoney;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class SortedTreeModel extends DefaultTreeModel {

	public SortedTreeModel(TreeNode root) {
		super(root, false);
	}

	public void insertNodeInto(
		MutableTreeNode newChild,
		MutableTreeNode parent,
		int index) {
		parent.insert(newChild, 0);
		nodeStructureChanged(parent);
	}

	public void sortChildren(SortedTreeNode parent) {
		parent.sortChildren();
		nodeStructureChanged(parent);
	}

}
