package net.sf.jmoney.model;

import java.io.Serializable;

/**
 * Interface for those classes that can be a category of an entry.
 */
public interface Category extends Serializable, Comparable {

	/**
	 * @return the name of the category.
	 */
	public String getCategoryName();

	/**
	 * @return the full qualified name of the category.
	 */
	public String getFullCategoryName();

	/**
	 * @return the node that will be used to insert the category into the tree.
	 */
	public CategoryNode getCategoryNode();

}
