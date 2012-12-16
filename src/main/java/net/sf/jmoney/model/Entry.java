package net.sf.jmoney.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * The data model for an entry.
 */
public class Entry implements Serializable {

	/**
	 * Entry is uncleared.
	 */
	public static final int UNCLEARED = 0;

	/**
	 * Entry is reconciling.
	 */
	public static final int RECONCILING = 1;

	/**
	 * Entry is cleared.
	 */
	public static final int CLEARED = 2;

	/**
	 * This entry is a prototype if the check field points to this string.
	 */
	public static final Entry PROTOTYPE = new Entry();

	protected long creation = Calendar.getInstance().getTime().getTime();

	protected String check = null;

	protected Date date = null;

	protected Date valuta = null;

	protected String description = null;

	protected Category category = null;

	protected long amount = 0;

	protected int status = 0;

	protected String memo = null;

	protected transient PropertyChangeSupport changeSupport =
		new PropertyChangeSupport(this);

	/**
	 * Creates a new entry.
	 */
	public Entry() {
	}

	public Entry(Entry entry) {
		copyValues(entry);
	}

	public Entry toEntry() {
		return new Entry(this);
	}

	public DoubleEntry toDoubleEntry() {
		return new DoubleEntry(this);
	}

	public SplittedEntry toSplittedEntry() {
		return new SplittedEntry(this);
	}

	protected void copyValues(Entry newEntry) {
		amount = newEntry.getAmount();
		category = newEntry.getCategory();
		check = newEntry.getCheck();
		creation = newEntry.getCreation();
		date = newEntry.getDate();
		description = newEntry.getDescription();
		memo = newEntry.getMemo();
		status = newEntry.getStatus();
		valuta = newEntry.getValuta();
	}

	/**
	 * Returns the creation.
	 */
	public long getCreation() {
		return creation;
	}

	/**
	 * Returns the check.
	 */
	public String getCheck() {
		return check;
	}

	/**
	 * Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the valuta.
	 */
	public Date getValuta() {
		return valuta;
	}

	/**
	 * Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the category.
	 */
	public Category getCategory() {
		return category;
	}

	public String getFullCategoryName() {
		return category == null ? null : category.getFullCategoryName();
	}

	/**
	 * Returns the amount.
	 */
	public long getAmount() {
		return amount;
	}

	/**
	 * Returns the status.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Returns the memo.
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * Sets the creation.
	 */
	public void setCreation(long aCreation) {
		creation = aCreation;
	}

	/**
	 * Sets the check.
	 */
	public void setCheck(String aCheck) {
		if (check != null && check.equals(aCheck))
			return;
		check = aCheck.length() == 0 ? null : aCheck;
		changeSupport.firePropertyChange("check", null, check);
	}

	/**
	 * Sets the date.
	 */
	public void setDate(Date aDate) {
		if (date != null && date.equals(aDate))
			return;
		date = aDate;
		changeSupport.firePropertyChange("date", null, date);
	}

	/**
	 * Sets the valuta.
	 */
	public void setValuta(Date aValuta) {
		if (valuta != null && valuta.equals(aValuta))
			return;
		valuta = aValuta;
		changeSupport.firePropertyChange("valuta", null, valuta);
	}

	/**
	 * Sets the description.
	 */
	public void setDescription(String aDescription) {
		if (description != null && description.equals(aDescription))
			return;
		description = aDescription.length() == 0 ? null : aDescription;
		changeSupport.firePropertyChange("description", null, description);
	}

	/**
	 * Sets the category.
	 */
	public void setCategory(Category aCategory) {
		if (category != null && category.equals(aCategory))
			return;
		category = aCategory;
		changeSupport.firePropertyChange("category", null, category);
	}

	/**
	 * Sets the amount.
	 */
	public void setAmount(long anAmount) {
		if (amount == anAmount)
			return;
		amount = anAmount;
		changeSupport.firePropertyChange("amount", null, (double) amount);
	}

	/**
	 * Sets the check. Either UNCLEARED, RECONCILING or CLEARED.
	 */
	public void setStatus(int aStatus) {
		if (status == aStatus)
			return;
		status = aStatus;
		changeSupport.firePropertyChange("status", 0, status);
	}

	/**
	 * Sets the memo.
	 */
	public void setMemo(String aMemo) {
		if (memo != null && memo.equals(aMemo))
			return;
		memo = aMemo.length() == 0 ? null : aMemo;
		changeSupport.firePropertyChange("memo", null, memo);
	}

	/**
	 * Adds a PropertyChangeListener.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		changeSupport.addPropertyChangeListener(pcl);
	}

	/**
	 * Removes a PropertyChangeListener.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		changeSupport.removePropertyChangeListener(pcl);
	}

	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		changeSupport = new PropertyChangeSupport(this);
	}

}
