package gsearchparser.mvc;

import gsearchparser.ExcelExportCreator;
import gsearchparser.GoogleResultCounterParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FilenameUtils;
import org.mozilla.universalchardet.UniversalDetector;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Model for managing view
 * 
 * @author SHaurushkin
 */
public class ParserModel {
	private Map<Thread, GoogleResultCounterParser> googleParserMap = null;

	private Map<String, Long> resultMap = new LinkedHashMap<String, Long>();

	private final Collection<ModelSubscriber> subscribers = new CopyOnWriteArrayList<ModelSubscriber>();

	public void parseTxtFile(File txtFile) {
		if (txtFile == null) {
			throw new NullPointerException("Text file can't be null!");
		}

		try {
			resultMap.clear();
			FileInputStream fstream = new FileInputStream(txtFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					getEncoding(txtFile)));

			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.isEmpty()) {
					continue;
				}
				resultMap.put(strLine, null);
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private String getEncoding(File txtFile) throws IOException {
		FileInputStream fstream = new FileInputStream(txtFile);
		UniversalDetector universalDetector = new UniversalDetector(null);
		int nread;
		byte[] buf = new byte[4096];
		while ((nread = fstream.read(buf)) > 0 && !universalDetector.isDone()) {
			universalDetector.handleData(buf, 0, nread);
		}
		fstream.close();
		universalDetector.dataEnd();
		return universalDetector.getDetectedCharset();
	}

	private int threadNumber = 0;

	private int countOfFinished = 0;

	private boolean isPerforming = false;

	public void startParsing() {
		if (resultMap.isEmpty()) {
			return;
		}

		setIsPerforming(true);
		countOfFinished = 0;
		int keywordsNumberForEach = resultMap.size() / threadNumber;
		if (keywordsNumberForEach == 0) {
			keywordsNumberForEach = 1;
			threadNumber = resultMap.size();
		}

		googleParserMap = new HashMap<Thread, GoogleResultCounterParser>(
				threadNumber);
		for (int i = 0; i < threadNumber; i++) {
			List<String> keywordListForEach = new ArrayList<String>();

			String[] keywords = resultMap.keySet().toArray(
					new String[resultMap.keySet().size()]);

			for (int j = i; j < keywords.length; j += threadNumber) {
				keywordListForEach.add(keywords[j]);
			}

			GoogleResultCounterParser googleParser = new GoogleResultCounterParser(
					this, keywordListForEach);
			Thread thread = new Thread(googleParser);
			thread.start();
			googleParserMap.put(thread, googleParser);
		}
	}

	/**
	 * Export data from Map resultMap to *.xls document
	 */
	public void export(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}

		String extension = FilenameUtils.getExtension(file.getName());
		if (extension.equalsIgnoreCase("xls")) {
			exportToXls(file);
		} else if (extension.equalsIgnoreCase("csv")) {
			exportToCsv(file);
		}
	}

	/**
	 * Export data from Map resultMap to *.csv document
	 */
	private void exportToXls(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}

		ExcelExportCreator excelCreator = new ExcelExportCreator(file,
				resultMap);
		excelCreator.create();
	}

	/**
	 * Export data from Map resultMap to *.csv document
	 */
	private void exportToCsv(File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}

		try {
			FileOutputStream fos = new FileOutputStream(file);
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(fos,
					"UTF-8"), ',', CSVWriter.DEFAULT_QUOTE_CHARACTER,
					CSVWriter.NO_ESCAPE_CHARACTER, "\n");
			for (Entry<String, Long> entry : resultMap.entrySet()) {
				String theValue = "";
				if (entry.getValue() != null) {
					theValue = String.valueOf(entry.getValue());
				}
				String[] entries = { entry.getKey().toString(), theValue };
				writer.writeNext(entries);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add to result map and update table data
	 * 
	 * @param keyword
	 * @param number
	 */
	public synchronized void addToResultMap(final String keyword,
			final Long number) {
		resultMap.put(keyword, number);
		notifySubscribers();
	}

	/**
	 * Notify subscribers
	 */
	protected void notifySubscribers() {
		for (ModelSubscriber subscriber : subscribers)
			subscriber.modelChanged();
	}

	public void subscribe(ModelSubscriber subscriber) {
		if (subscriber == null)
			throw new NullPointerException("������ ��������");
		if (subscribers.contains(subscriber))
			throw new IllegalArgumentException("��������� ��������: "
					+ subscriber);

		subscribers.add(subscriber);
		notifySubscribers();
	}

	/**
	 * stop parsing
	 */
	public void stopParsing() {
		for (Thread thread : googleParserMap.keySet()) {
			if (thread != null && thread.isAlive()) {
				googleParserMap.get(thread).setExit(true);
			}
		}
	}

	public void setIsPerforming(final boolean isPerforming) {
		this.isPerforming = isPerforming;
		notifySubscribers();
	}

	public synchronized void setThreadFinished() {
		countOfFinished++;
		if (countOfFinished == threadNumber) {
			setIsPerforming(false);
		}
	}

	public void clearList() {
		resultMap.clear();
	}

	public Map<String, Long> getResultMap() {
		return new HashMap<String, Long>(resultMap);
	}

	public Long getResultMapValue(String keyword) {
		return resultMap.get(keyword);
	}

	public Collection<String> getKeywordsList() {
		return new HashSet<String>(resultMap.keySet());
	}

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	public boolean isPerforming() {
		return isPerforming;
	}

	public void setResultMap(Map<String, Long> map) {
		resultMap = map;
	}
}
