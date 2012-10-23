package gsearchparser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Model for managing view
 * 
 * @author SHaurushkin
 */
public class ParserModel
{
	private Map<Thread, GoogleResultCounterParser> googleParserMap = null;
	private List<String> keywordsList = new ArrayList<String>();
	private Map<String, Long> resultMap = new LinkedHashMap<String, Long>();
	private ParserDialog view = null;
	private int threadNumber = 0;
	private int countOfFinished = 0;

	public void setView(ParserDialog parserDialog)
	{
		view = parserDialog;
	}

	public void openChooseTxtFileDialog()
	{
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			view.setSourceFilePath(file.getAbsolutePath());
			parseTxtFile(file);
		}
	}

	private void parseTxtFile(File txtFile)
	{
		try
		{
			keywordsList.clear();
			FileInputStream fstream = new FileInputStream(txtFile);
			DataInputStream in = new DataInputStream(fstream);
			// TODO check for other encodings
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)
			{
				if (strLine.isEmpty())
				{
					continue;
				}
				keywordsList.add(strLine);
				resultMap.put(strLine, null);
			}
			view.setKeywordsToTable(keywordsList);
			in.close();
		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void startParsing()
	{
		threadNumber = view.getThreadNumber();
		if (threadNumber == 0 || keywordsList.size() == 0)
		{
			return;
		}

		setIsPerforming(true);
		countOfFinished = 0;
		int keywordsNumberForEach = keywordsList.size() / threadNumber;
		if (keywordsNumberForEach == 0)
		{
			keywordsNumberForEach = 1;
			threadNumber = keywordsList.size();
		}

		googleParserMap = new HashMap<Thread, GoogleResultCounterParser>(
				threadNumber);
		for (int i = 0; i < threadNumber; i++)
		{
			List<String> keywordListForParser = new ArrayList<String>();
			for (int j = i; j < keywordsList.size(); j += threadNumber)
			{
				keywordListForParser.add(keywordsList.get(j));
			}

			GoogleResultCounterParser googleParser = new GoogleResultCounterParser();
			Thread thread = new Thread(googleParser);
			googleParser.setKeywords(keywordListForParser);
			googleParser.setParserModel(this);
			googleParser.setResultMap(resultMap);
			thread.start();
			googleParserMap.put(thread, googleParser);
		}
	}

	public void exportToXls()
	{
		if (resultMap.size() == 0)
		{
			showWarningEmptyData();
			return;
		}
		File file = chooseFileToSave();
		if (file != null)
		{
			// ExcelExportCreator excelCreator = new ExcelExportCreator(file,
			// resultMap);
			// excelCreator.create();
		}
	}

	/**
	 * Choose file to save
	 * 
	 * @return
	 */
	private File chooseFileToSave()
	{
		final JFileChooser fc = new JFileChooser();
		// fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(new FileFilter()
		{
			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory())
				{
					return true;
				}

				String extension = FilenameUtils.getExtension(f.getName());
				if (extension != null && extension.equalsIgnoreCase("XLS"))
				{
					return true;
				} else
				{
					return false;
				}
			}

			@Override
			public String getDescription()
			{
				return "Excel 97-2003 Workbook (*.xls)";
			}
		});

		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".xls"))
			{
				file = new File(file.getAbsolutePath() + ".xls");
			}
			return file;
		}

		return null;
	}

	/**
	 * Open warning window
	 */
	private void showWarningEmptyData()
	{
		ResourceBundle loc_data = ResourceBundle.getBundle(
				"resources.loc_data", ParserDialog.LOCALE_RU);

		JOptionPane.showMessageDialog(view, loc_data.getString("warn_no_data"),
				loc_data.getString("title"), JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Add to result map and update table data
	 * 
	 * @param keyword
	 * @param number
	 */
	public synchronized void addToResultMap(final String keyword,
			final Long number)
	{
		resultMap.put(keyword, number);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				view.setTableResult(keyword, number);
			}
		});
	}

	/**
	 * stop parsing
	 */
	public void stopParsing()
	{
		for (Thread thread : googleParserMap.keySet())
		{
			if (thread != null && thread.isAlive())
			{
				googleParserMap.get(thread).setStop(true);
			}
		}
	}

	public void setIsPerforming(final boolean isPerforming)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				view.setIsPerforming(isPerforming);
			}
		});
	}

	public synchronized void setThreadFinished()
	{
		countOfFinished++;
		if (countOfFinished == threadNumber)
		{
			setIsPerforming(false);
		}
	}

	public void clearList()
	{
		keywordsList.clear();
		resultMap.clear();
		view.clearTable();
	}
}
