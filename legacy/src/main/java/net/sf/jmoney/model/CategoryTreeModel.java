package net.sf.jmoney.model;

import net.sf.jmoney.SortedTreeModel;

import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * The Category model.
 */
public class CategoryTreeModel extends SortedTreeModel {

	protected CategoryNode rootNode = (new RootCategory()).getCategoryNode();

	protected CategoryNode transferNode =
		(new TransferCategory()).getCategoryNode();

	protected CategoryNode splitNode = (new SplitCategory()).getCategoryNode();

	/**
	 * Used by XMLDecoder.
	 */
	public CategoryTreeModel() {
		super(new DefaultMutableTreeNode());
		setRoot(rootNode);
	}

	/**
	 * Creates a new CategoryTreeModel.
	 */
	public CategoryTreeModel(int dummy) {
		this();

		// This cannot be done in the parameterless constructor above,
		// XMLDecoder would add those nodes twice:
		rootNode.add(transferNode);
		rootNode.add(splitNode);
	}

	/**
	 * @return the root node.
	 */
	public CategoryNode getRootNode() {
		return rootNode;
	}

	/**
	 * @return the transfer node.
	 */
	public CategoryNode getTransferNode() {
		return transferNode;
	}

	/**
	 * @return the split node.
	 */
	public CategoryNode getSplitNode() {
		return splitNode;
	}

	public void setRootNode(CategoryNode aRootNode) {
		rootNode = aRootNode;
		setRoot(aRootNode);
	}

	public void setSplitNode(CategoryNode aSplitNode) {
		splitNode = aSplitNode;
	}

	public void setTransferNode(CategoryNode aTransferNode) {
		transferNode = aTransferNode;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		EventListenerList tmp = listenerList;
		listenerList = new EventListenerList();
		out.defaultWriteObject();
		listenerList = tmp;
	}

}
