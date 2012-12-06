package net.sf.jmoney.model;

import java.util.Enumeration;
import java.util.Vector;

public class SplittedEntry extends Entry {

    protected Vector entries = new Vector();

    public SplittedEntry() { }

    public SplittedEntry(Entry entry) {
	super(entry);
    }

    public Vector getEntries() { return entries; }

    public void setEntries(Vector newEntries) {
	entries = newEntries;
	changeSupport.firePropertyChange("entries", null, entries);
    }

    public void addEntry(Entry e) {
	e.setDate(getDate());
	entries.addElement(e);
    }

    public void removeEntryAt(int index) {
	Entry e = (Entry) entries.elementAt(index);
	if (e instanceof DoubleEntry) ((DoubleEntry) e).removeOther();
	entries.removeElementAt(index);
    }

    public void removeAllEntries() {
	for (Enumeration e = entries.elements(); e.hasMoreElements(); ) {
	    Entry entry = (Entry) e.nextElement();
	    if (entry instanceof DoubleEntry) ((DoubleEntry) entry).removeOther();
	}
	entries.removeAllElements();
    }

    public void setEntryAt(Entry newEntry, int index) {
	Entry oldEntry = (Entry) entries.elementAt(index);
	if (oldEntry instanceof DoubleEntry) ((DoubleEntry) oldEntry).removeOther();
	entries.setElementAt(newEntry, index);
	if (newEntry instanceof DoubleEntry) ((DoubleEntry) newEntry).addOther();
    }

    public SplittedEntry toSplittedEntry() {
	SplittedEntry se = new SplittedEntry(this);
	se.setEntries(getEntries());
	return se;
    }

}
