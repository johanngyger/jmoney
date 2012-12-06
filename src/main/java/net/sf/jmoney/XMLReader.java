package net.sf.jmoney;

import net.sf.jmoney.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.XMLDecoder;
import java.io.*;
import java.util.zip.GZIPInputStream;

public class XMLReader {

    private static final Logger log = LoggerFactory.getLogger(XMLReader.class);

	public static Session readSessionFromInputStream(InputStream in) {
		XMLDecoder dec = null;
		try {
			GZIPInputStream gin = new GZIPInputStream(in);
			BufferedInputStream bin = new BufferedInputStream(gin);
			dec = new XMLDecoder(bin);
			Session session = (Session) dec.readObject();
			return session;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
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
            log.error(e.getMessage(), e);
			return null;
		}
	}

}
