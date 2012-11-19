package gsearchparser.mvc;

import gsearchparser.ExcelExportCreator;
import gsearchparser.GoogleResultCounterParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mozilla.universalchardet.UniversalDetector;

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
	public void exportToXls(File file) {
		if (file == null) {
			throw new NullPointerException("Xls file can't be null");
		}

		ExcelExportCreator excelCreator = new ExcelExportCreator(file,
				resultMap);
		excelCreator.create();
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
			throw new NullPointerException("Пустой параметр");
		if (subscribers.contains(subscriber))
			throw new IllegalArgumentException("Повторная подписка: "
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
		return resultMap;
	}

	public Long getResultMapValue(String keyword) {
		return resultMap.get(keyword);
	}

	public Collection<String> getKeywordsList() {
		return resultMap.keySet();
	}

	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	public boolean isPerforming() {
		return isPerforming;
	}

}
