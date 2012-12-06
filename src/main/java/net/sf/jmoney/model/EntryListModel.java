package net.sf.jmoney.model;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class EntryListModel extends DefaultListModel {

	/**
	 * Used by XMLDecoder
	 */
	public void add(Object obj) {
		addElement(obj);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		EventListenerList tmp = listenerList;
		listenerList = new EventListenerList();
		out.defaultWriteObject();
		listenerList = tmp;
	}

}
