package net.sf.jmoney.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 * Holds the fields that will be saved in a file.
 */
public class Session implements Serializable {

    protected Vector accounts = new Vector();

    protected CategoryTreeModel categories = new CategoryTreeModel();

    protected transient boolean modified = false;

    protected transient PropertyChangeSupport changeSupport =
        new PropertyChangeSupport(this);

    public Session() {
    }

    public Session(int dummy) {
        categories = new CategoryTreeModel(0);
    }

    public void setAccounts(Vector newAccounts) {
        accounts = newAccounts;
    }

    public void setCategories(CategoryTreeModel newCategories) {
        categories = newCategories;
    }

    public Vector getAccounts() {
        return accounts;
    }

    public CategoryTreeModel getCategories() {
        return categories;
    }

    public Account getAccountByNumber(String accountNumber) {
        Vector accounts = getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            Account account = (Account) accounts.get(i);
            if (account.getAccountNumber() != null
                && account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public Account getNewAccount(String name) {
        Account account = new Account(name);
        getAccounts().addElement(account);
        getCategories().insertNodeInto(
            account.getCategoryNode(),
            getCategories().getTransferNode(),
            0);
        modified();
        return account;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean m) {
        if (modified == m)
            return;
        modified = m;
        changeSupport.firePropertyChange("modified", !m, m);
    }

    public void modified() {
        setModified(true);
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
        modified = false;
        changeSupport = new PropertyChangeSupport(this);
    }

}
