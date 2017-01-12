package net.sf.jmoney.model;

import java.util.Date;

/**
 * The data model for a double entry.
 */
public class DoubleEntry extends Entry {

    /**
     * The entry in the other account.
     */
    protected DoubleEntry other;

    /**
     * Used by XMLEncoder.
     */
    public DoubleEntry() { }

    /**
     * Creates a new double entry.
     */
    public DoubleEntry(Entry entry) {
	super(entry);
	other = new DoubleEntry(entry, true);
	other.other = this;
    }

    private DoubleEntry(Entry entry, boolean dummy) {
	super(entry);
	amount = -amount;
    }

    public DoubleEntry toDoubleEntry() {
	DoubleEntry de = new DoubleEntry(this);
	de.getOther().setAmount(getOther().getAmount());
	de.getOther().setStatus(getOther().getStatus());
	return de;
    }

    /**
     * Get other double entry.
     */
    public DoubleEntry getOther() { return other; }

    public void setOther(DoubleEntry aDoubleEntry) {
	other = aDoubleEntry;
	//other.other = this;
    }

    /**
     * Overridden. Sets the date of both entries.
     */
    public void setDate(Date aDate) {
	if (date != null && date.equals(aDate)) return;
	date = aDate;
	changeSupport.firePropertyChange("date", null, date);
	if (other == null) return;
	other.date = aDate;
	other.changeSupport.firePropertyChange("date", null, date);
    }

    /**
     * Overridden. Sets the description of both entries.
     */
    public void setDescription(String aDescription) {
	if (description != null && description.equals(aDescription)) return;
	description = aDescription.length() == 0? null: aDescription;
	changeSupport.firePropertyChange("description", null, description);
	if (other == null) return;
	other.description = description;
	other.changeSupport.firePropertyChange("description", null, description);
    }

    /**
     * Sets the memo of both entries.
     */
    public void setMemo(String aMemo) {
	if (memo != null && memo.equals(aMemo)) return;
	memo = aMemo.length() == 0? null: aMemo;
	changeSupport.firePropertyChange("memo", null, memo);
	if (other == null) return;
	other.memo = memo;
	other.changeSupport.firePropertyChange("memo", null, memo);
    }

    public void remove() {
	Account to = (Account) getCategory();
	Account from = (Account) getOther().getCategory();
	to.getEntries().removeElement(getOther());
	from.getEntries().removeElement(this);
    }

    public void removeOther() {
	Account a = (Account) getCategory();
	a.getEntries().removeElement(getOther());
    }

    public void addOther() {
	Account a = (Account) getCategory();
	a.getEntries().addElement(getOther());
    }
}
