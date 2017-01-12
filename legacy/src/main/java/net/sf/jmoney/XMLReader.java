package net.sf.jmoney;

import net.sf.jmoney.model.Session;

import java.beans.XMLDecoder;
import java.io.*;
import java.util.zip.GZIPInputStream;

public class XMLReader {

	public static Session readSessionFromInputStream(InputStream in) {
		XMLDecoder dec = null;
		try {
			GZIPInputStream gin = new GZIPInputStream(in);
			BufferedInputStream bin = new BufferedInputStream(gin);
			dec = new XMLDecoder(bin);
			Session session = (Session) dec.readObject();
			return session;
		} catch (IOException e) {
			return null;
		} finally {
			if (dec != null) {
				dec.close();
			}
		}
	}

	public static Session readSessionFromFile(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			return readSessionFromInputStream(in);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

}
