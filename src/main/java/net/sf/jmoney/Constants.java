package net.sf.jmoney;

import javax.swing.*;

/**
 * An interface that holds the global constants.
 */
public interface Constants {

	/**
	 * The name of the author.
	 */
	public static final String AUTHOR = "Johann Gyger";

	/**
	 * The email address of the author.
	 */
	public static final String EMAIL = "jgyger@users.sf.net";

	/**
	 * Copyright string.
	 */
	public static final String COPYRIGHT =
		"Copyright (C) 2002 " + AUTHOR + " <" + EMAIL + ">";

	/**
	 * Corresponding int for CANCEL.
	 */
	public static final int CANCEL = 0;

	/**
	 * Corresponding int for OK.
	 */
	public static final int OK = 1;

	/**
	 * Corresponding int for HELP.
	 */
	public static final int HELP = 2;

	/**
	 * Corresponding int for NEW.
	 */
	public static final int NEW = 3;

	public static final ImageIcon JMONEY_IMAGE =
		new ImageIcon(Constants.class.getResource("img/jmoney_logo.png"));

	public static final ImageIcon ACCOUNTS_ICON =
		new ImageIcon(Constants.class.getResource("img/Accounts.gif"));

	public static final ImageIcon ACCOUNT_ICON =
		new ImageIcon(Constants.class.getResource("img/Account.gif"));

	public static final ImageIcon CATEGORY_ICON =
		new ImageIcon(Constants.class.getResource("img/Category.gif"));

	public static final ImageIcon ARROW_UP_ICON =
		new ImageIcon(Constants.class.getResource("img/ArrowUp.gif"));

	public static final ImageIcon ARROW_DOWN_ICON =
		new ImageIcon(Constants.class.getResource("img/ArrowDown.gif"));

	public static final ImageIcon NEW_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/New16.gif"));

	public static final ImageIcon OPEN_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Open16.gif"));
	
	public static final ImageIcon SAVE_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Save16.gif"));
	
	public static final ImageIcon SAVE_AS_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/SaveAs16.gif"));

	public static final ImageIcon PRINT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Print16.gif"));
		
	public static final ImageIcon IMPORT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Import16.gif"));
		
	public static final ImageIcon EXPORT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Export16.gif"));

	public static final ImageIcon UNDO_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Undo16.gif"));

	public static final ImageIcon REDO_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Redo16.gif"));

	public static final ImageIcon CUT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Cut16.gif"));

	public static final ImageIcon COPY_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Copy16.gif"));

	public static final ImageIcon PASTE_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Paste16.gif"));

	public static final ImageIcon FIND_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Find16.gif"));

	public static final ImageIcon FIND_AGAIN_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/FindAgain16.gif"));
		
	public static final ImageIcon PREFERENCES_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/Preferences16.gif"));

	public static final ImageIcon ABOUT_ICON =
		new ImageIcon(Constants.class.getResource("img/jlfgr/About16.gif"));

	/**
	 * Filename extension
	 */
	public static final String FILE_EXTENSION = ".jmx";

	/**
	 * File filter name
	 */
	public static final String FILE_FILTER_NAME = "JMoney Files (*.jmx)";

}
