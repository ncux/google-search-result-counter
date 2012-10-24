package gsearchparser;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
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
			// create xls
			WritableWorkbook workbook;
			workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("First Sheet", 0);

			// for each keyword request google
			Iterator<String> iterator = resultMap.keySet().iterator();
			int row = 0;
			while (iterator.hasNext())
			{
				String keyword = iterator.next();
				Label label = new Label(0, row, keyword);
				sheet.addCell(label);
				Long value = resultMap.get(keyword);
				if (value != null)
				{
					Label number = new Label(1, row, value.toString());
					sheet.addCell(number);
				}
				row++;
			}

			// save xls
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
