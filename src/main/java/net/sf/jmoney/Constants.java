package net.sf.jmoney;

import javax.swing.*;

/**
 * An interface that holds the global constants.
 */
public interface Constants {

	/**
	 * The name of the author.
	 */
	String AUTHOR = "Johann Gyger";

	/**
	 * The email address of the author.
	 */
	String EMAIL = "jgyger@users.sf.net";

	/**
	 * Copyright string.
	 */
	String COPYRIGHT =
		"Copyright (C) 2002 " + AUTHOR + " <" + EMAIL + ">";

	/**
	 * Corresponding int for CANCEL.
	 */
	int CANCEL = 0;

	/**
	 * Corresponding int for OK.
	 */
	int OK = 1;

	/**
	 * Corresponding int for HELP.
	 */
	int HELP = 2;

	/**
	 * Corresponding int for NEW.
	 */
	int NEW = 3;

	ImageIcon JMONEY_IMAGE =
		new ImageIcon(Constants.class.getResource("img/jmoney_logo.png"));

	ImageIcon ACCOUNTS_ICON =
		new ImageIcon(Constants.class.getResource("img/Accounts.gif"));

	ImageIcon ACCOUNT_ICON =
		new ImageIcon(Constants.class.getResource("img/Account.gif"));

	ImageIcon CATEGORY_ICON =
		new ImageIcon(Constants.class.getResource("img/Category.gif"));

	ImageIcon ARROW_UP_ICON =
		new ImageIcon(Constants.class.getResource("img/ArrowUp.gif"));

	ImageIcon ARROW_DOWN_ICON =
		new ImageIcon(Constants.class.getResource("img/ArrowDown.gif"));

	ImageIcon NEW_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/New16.gif"));

	ImageIcon OPEN_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Open16.gif"));
	
	ImageIcon SAVE_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Save16.gif"));
	
	ImageIcon SAVE_AS_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/SaveAs16.gif"));

	ImageIcon PRINT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Print16.gif"));
		
	ImageIcon IMPORT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Import16.gif"));
		
	ImageIcon EXPORT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Export16.gif"));

	ImageIcon UNDO_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Undo16.gif"));

	ImageIcon REDO_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Redo16.gif"));

	ImageIcon CUT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Cut16.gif"));

	ImageIcon COPY_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Copy16.gif"));

	ImageIcon PASTE_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Paste16.gif"));

	ImageIcon FIND_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Find16.gif"));

	ImageIcon FIND_AGAIN_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/FindAgain16.gif"));
		
	ImageIcon PREFERENCES_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Preferences16.gif"));

	ImageIcon ABOUT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/About16.gif"));

	/**
	 * Filename extension
	 */
	String FILE_EXTENSION = ".jmx";

	/**
	 * File filter name
	 */
	String FILE_FILTER_NAME = "JMoney Files (*.jmx)";

}
