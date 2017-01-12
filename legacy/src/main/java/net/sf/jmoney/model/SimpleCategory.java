package net.sf.jmoney.model;

/**
 * An implementation of the Category interface
 */
public class SimpleCategory extends AbstractCategory {

	private String categoryName;

	private String fullCategoryName = null;

	public SimpleCategory() {
	}

	public SimpleCategory(String aCategoryName) {
		setCategoryName(aCategoryName);
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getFullCategoryName() {
		if (fullCategoryName == null) {
			Object[] path = getCategoryNode().getUserObjectPath();
			if (path.length > 1) {
				fullCategoryName = path[1].toString();
				for (int i = 2; i < path.length; i++)
					fullCategoryName += ":" + path[i];
			} else
				fullCategoryName = categoryName;
		}
		return fullCategoryName;
	}

	public void setCategoryName(String aCategoryName) {
		categoryName = aCategoryName;
		fullCategoryName = null;
	}

}
