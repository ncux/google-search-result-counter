package gsearchparser;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ParserModel {
	private List<String> keywordsList = new ArrayList<String>();
	private Map<String, Integer> resultMap = new LinkedHashMap<String, Integer>();
	private ParserDialog view = null;

	public ParserModel() {

	}

	public void setView(ParserDialog parserDialog) {
		view = parserDialog;
	}

	public void openChooseTxtFileDialog() {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			view.setSourceFilePath(file.getAbsolutePath());
			parseTxtFile(file);
		}
	}

	private void parseTxtFile(File txtFile) {
		try {
			keywordsList.clear();
			FileInputStream fstream = new FileInputStream(txtFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"windows-1251"));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				if (strLine.equals("")) {
					continue;
				}

				keywordsList.add(strLine);
			}
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void startParsing() {
		Iterator<String> it = keywordsList.iterator();

		while (it.hasNext()) {
			String keyword = it.next();
			String keywordForWeb = StringEscapeUtils.escapeHtml4(keyword);

			try {
				keywordForWeb = URLEncoder.encode(keywordForWeb, "UTF-8");
				Document doc = Jsoup
						.connect(
								"http://www.google.ru/search?q="
										+ keywordForWeb
										+ "&hl=en&ie=UTF-8&oe=UTF8")
						.userAgent(
								"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
						.get();
				Element el = doc.getElementById("resultStats");
				if (el != null) {
					String value = el.ownText();
					Integer number = parseResultString(value);
					resultMap.put(keyword, number);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void exportToXls() {
		try {
			// create xls
			WritableWorkbook workbook;
			workbook = Workbook.createWorkbook(new File(
					"googleSearchStatistics.xls"));
			WritableSheet sheet = workbook.createSheet("First Sheet", 0);

			// for each keyword request google
			Iterator<String> iterator = resultMap.keySet().iterator();
			while (iterator.hasNext()) {
				String keyword = iterator.next();
				int row = 0;
				Label label = new Label(0, row, keyword);
				sheet.addCell(label);
				Number number = new Number(1, row, resultMap.get(keyword));
				sheet.addCell(number);
			}

			// save xls
			workbook.write();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public Integer parseResultString(String value) {
		String strWithoutEscapes = StringEscapeUtils.unescapeHtml4(value);
		strWithoutEscapes = strWithoutEscapes.replaceAll("\\D+", "");

		Integer resultNumber = Integer.valueOf(strWithoutEscapes);
		return resultNumber;
	}
}
