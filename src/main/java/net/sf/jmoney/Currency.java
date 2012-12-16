package net.sf.jmoney;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * This class was created because the currency support wich comes with the Java
 * SDK is to complicated. Therefore we provide a simpler model which is
 * not based upon locales but upon the ISO 4217 currencies.
 */
public class Currency implements Comparable {

	public static final ResourceBundle NAME =
		ResourceBundle.getBundle("net.sf.jmoney.resources.Currency");
	public static final int MAX_DECIMALS = 4;
	private static final short[] SCALE_FACTOR = { 1, 10, 100, 1000, 10000 };
	private static Hashtable currencies = null;
	private static Object[] sortedCurrencies = null;
	private static NumberFormat[] numberFormat = null;

	private String code; // ISO 4217 Code
	private byte decimals;

	/**
	 * @return the available currencies.
	 */
	public static Object[] getAvailableCurrencies() {
		if (currencies == null)
			initSystemCurrencies();
		if (sortedCurrencies == null) {
			sortedCurrencies = currencies.values().toArray();
			Arrays.sort(sortedCurrencies);
		}
		return sortedCurrencies;
	}

	/**
	 * @param code the currency code.
	 * @return the corresponding currency.
	 */
	public static Currency getCurrencyForCode(String code) {
		if (currencies == null)
			initSystemCurrencies();
		return (Currency) currencies.get(code);
	}

	private static void initSystemCurrencies() {
		InputStream in = Constants.class.getResourceAsStream("resources/Currencies.txt");
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
		currencies = new Hashtable();
		try {
			String line = buffer.readLine();
			String c;
			byte d;
			while (line != null) {
				c = line.substring(0, 3);
				d = 2;
				try {
					d = Byte.parseByte(line.substring(4, 5));
				} catch (Exception ex) {
                    ex.printStackTrace();
				}
				currencies.put(c, new Currency(c, d));
				line = buffer.readLine();
			}
		} catch (IOException ioex) {
            ioex.printStackTrace();
		}
	}

	private static void initNumberFormat() {
		numberFormat = new NumberFormat[MAX_DECIMALS + 1];
		for (int i = 0; i < numberFormat.length; i++) {
			numberFormat[i] = NumberFormat.getNumberInstance();
			numberFormat[i].setMaximumFractionDigits(i);
			numberFormat[i].setMinimumFractionDigits(i);
		}
	}

	protected Currency(String c, byte d) {
		if (d > MAX_DECIMALS)
			throw new IllegalArgumentException("Number of decimals not supported");
		code = c;
		decimals = d;
	}

	/**
	 * @return the currency code.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the name of the currency.
	 */
	public String getCurrencyName() {
		return NAME.getString(getCode());
	}

	public String toString() {
		return getCurrencyName() + " (" + getCode() + ")";
	}

	/**
	 * @return a number format instance for this currency.
	 */
	public NumberFormat getNumberFormat() {
		if (numberFormat == null)
			initNumberFormat();
		return numberFormat[getDecimals()];
	}

	public String format(long amount) {
		double a = ((double) amount) / getScaleFactor();
		return getNumberFormat().format(a);
	}

	public String format(Long amount) {
		return amount == null ? "" : format(amount.longValue());
	}

	/**
	 * @return the number of decimals that this currency has.
	 */
	public byte getDecimals() {
		return decimals;
	}

	/**
	 * @return the scale factor for this currency (10 to the number of decimals)
	 */
	public short getScaleFactor() {
		return SCALE_FACTOR[decimals];
	}

	public int compareTo(Object obj) {
		return getCurrencyName().compareTo(((Currency) obj).getCurrencyName());
	}
}
