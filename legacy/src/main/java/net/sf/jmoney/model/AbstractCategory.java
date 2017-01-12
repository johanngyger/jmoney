package net.sf.jmoney.model;

/**
 * An implementation of the Category interface
 */
public abstract class AbstractCategory implements Category {

	protected CategoryNode categoryNode = new CategoryNode(this);

	public String getFullCategoryName() {
		return getCategoryName();
	}

	/**
	 * @return the category tree node
	 */
	public CategoryNode getCategoryNode() {
		return categoryNode;
	}

	public void setCategoryNode(CategoryNode aCategoryNode) {
		categoryNode = aCategoryNode;
	}

	public int compareTo(Object o) {
		Category c = (Category) o;
		return getCategoryName().compareTo(c.getCategoryName());
	}

}
