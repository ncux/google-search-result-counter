package gsearchparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Create and fill Excel document with result map
 * 
 * @author SHaurushkin
 */
public class ExcelExportCreator
{
	private static final int EXCEL_COLUMN_KEYWORD = 0;
	private static final int EXCEL_COLUMN_AMOUNT = 1;

	private File file = null;
	private Map<String, Long> resultMap = null;

	/**
	 * Constructor
	 * @param file file to save
	 * @param resultMap result map
	 */
	public ExcelExportCreator(File file, Map<String, Long> resultMap)
	{
		this.file = file;
		this.resultMap = resultMap;
	}

	/**
	 * Create document
	 */
	public void create()
	{
		try
		{
			// create document
			WritableWorkbook workbook = null;
			try
			{
				workbook = Workbook.createWorkbook(file);
			} catch (FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
						"Something is wrong", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			WritableSheet sheet = workbook.createSheet("First Sheet", 0);

			// fill document
			Iterator<String> iterator = resultMap.keySet().iterator();
			int row = 0;
			while (iterator.hasNext())
			{
				String keyword = iterator.next();

				Label label = new Label(EXCEL_COLUMN_KEYWORD, row, keyword);
				sheet.addCell(label);

				Long amount = resultMap.get(keyword);
				if (amount != null)
				{
					Label number = new Label(EXCEL_COLUMN_AMOUNT, row,
							String.valueOf(amount));
					sheet.addCell(number);
				}

				row++;
			}

			// save document
			workbook.write();
			workbook.close();
		} catch (IOException e)
		{
			e.printStackTrace();

		} catch (WriteException e)
		{
			e.printStackTrace();
		}
	}
}
