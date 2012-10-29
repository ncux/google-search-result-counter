package gsearchparser.common.window;

import java.awt.Frame;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Store and restore window size from properties file
 * 
 * @author SHaurushkin
 */
public class RestoreMe
{
	/**
	 * This will end up in the current directory A more sensible location is a
	 * sub-directory of user.home. (left as an exercise for the reader)
	 */
	public static final String fileName = "options.prop";

	/** Store location & size of UI */
	public static void storeOptions(Frame f)
	{
		File file = new File(fileName);
		Properties p = new Properties();
		// restore the frame from 'full screen' first!
		f.setExtendedState(Frame.NORMAL);
		Rectangle r = f.getBounds();
		int x = (int) r.getX();
		int y = (int) r.getY();
		int w = (int) r.getWidth();
		int h = (int) r.getHeight();

		p.setProperty("x", "" + x);
		p.setProperty("y", "" + y);
		p.setProperty("w", "" + w);
		p.setProperty("h", "" + h);

		try
		{
			BufferedWriter br = new BufferedWriter(new FileWriter(file));
			p.store(br, "Properties of the user frame");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/** Restore location & size of UI */
	public static void restoreOptions(Frame f)
	{
		File file = new File(fileName);
		if (!file.exists())
		{
			f.setLocationByPlatform(true);
			return;
		}

		try
		{
			Properties p = new Properties();
			BufferedReader br = new BufferedReader(new FileReader(file));
			p.load(br);

			int x = Integer.parseInt(p.getProperty("x"));
			int y = Integer.parseInt(p.getProperty("y"));
			int w = Integer.parseInt(p.getProperty("w"));
			int h = Integer.parseInt(p.getProperty("h"));

			Rectangle r = new Rectangle(x, y, w, h);

			f.setBounds(r);
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (NumberFormatException e1)
		{
			e1.printStackTrace();
		}
	}
}