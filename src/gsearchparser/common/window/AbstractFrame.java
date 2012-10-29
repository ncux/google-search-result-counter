package gsearchparser.common.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Abstract frame class. Implements some nice dialog properties
 * 
 * @author SHaurushkin
 */
public abstract class AbstractFrame extends JFrame
{
	/**
	 * Russian locale
	 */
	private static final Locale LOCALE_RU = new Locale("ru");
	protected ResourceBundle loc_data = null;

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public AbstractFrame()
	{
		super();
		loc_data = ResourceBundle.getBundle("resources.loc_data", LOCALE_RU);
	}

	/**
	 * Initialize common view options - size, behavior and listeners
	 */
	protected void initDialogFrame()
	{
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				RestoreMe.storeOptions(AbstractFrame.this);
				System.exit(0);
			}
		});
		this.pack();
		RestoreMe.restoreOptions(this);
		addListeners();
	}

	/**
	 * Add listeners to the components
	 */
	abstract protected void addListeners();
}
