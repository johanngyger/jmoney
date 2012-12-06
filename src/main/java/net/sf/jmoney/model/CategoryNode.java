package net.sf.jmoney.model;

import net.sf.jmoney.SortedTreeNode;

public class CategoryNode extends SortedTreeNode {

    /**
     * Used by XMLDecoder.
     */
    public CategoryNode() { }

    /**
     * Creates a new category node
     * @param cat a category
     */
    public CategoryNode(Category cat) { super(cat); }

    /**
     * @return the category.
     */
    public Category getCategory() { return (Category) getUserObject(); }

}
