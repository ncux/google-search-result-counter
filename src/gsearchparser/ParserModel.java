package gsearchparser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFileChooser;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ParserModel {

	private GoogleResultCounterParser googleParser = null;
	private List<String> keywordsList = new Vector<String>();
	private Map<String, Long> resultMap = new LinkedHashMap<String, Long>();
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
				if (strLine.isEmpty()) {
					continue;
				}
				keywordsList.add(strLine);
			}
			view.setKeywordsToTable(keywordsList);

			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void startParsing() {
		googleParser = new GoogleResultCounterParser();
		googleParser.setKeywords(keywordsList);
		googleParser.setParserModel(this);
		googleParser.start();
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

	public void addToResultMap(String keyword, Long number) {
		resultMap.put(keyword, number);
		view.setTableResult(keyword, number);
	}

	public void stopParsing() {
		if (googleParser != null) {
			googleParser.setStop(true);
		}
	}

	public void setIsPerforming(boolean isPerforming) {
		view.setIsPerforming(isPerforming);
	}

}
