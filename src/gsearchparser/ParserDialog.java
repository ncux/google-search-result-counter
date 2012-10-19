package gsearchparser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 * S
 * 
 * @author SHaurushkin
 */
public class ParserDialog extends JFrame
{

	/**
	 * Russian locale
	 */
	public static final Locale LOCALE_RU = new Locale("ru");

	private static final long serialVersionUID = 6959318357186149652L;
	private String pathLabel = null;

	private ParserModel parserModel = null;

	private JPanel panel = null;
	private JLabel sourceFilePathLabel = null;
	private JTable table = null;
	private JButton startButton = null;
	private JButton stopButton = null;
	private JButton chooseFileButton = null;
	private JButton exportButton = null;
	private JButton clearButton = null;

	/**
	 * Constructor
	 * @param model
	 */
	public ParserDialog(ParserModel model)
	{
		super();
		parserModel = model;
		initialize();
		model.setView(this);
	}

	/**
	 * Inititalize view
	 */
	private void initialize()
	{
		ResourceBundle loc_data = ResourceBundle.getBundle(
				"resources.loc_data", LOCALE_RU);
		setTitle(loc_data.getString("title"));
		int row = 0;
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		chooseFileButton = new JButton(loc_data.getString("open_button"));
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = row++;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets.left = gbc.insets.top = gbc.insets.bottom = gbc.insets.right = 10;
		gbl.setConstraints(chooseFileButton, gbc);

		pathLabel = loc_data.getString("path");
		sourceFilePathLabel = new JLabel(pathLabel);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = row++;
		gbc.weighty = 1;
		gbl.setConstraints(sourceFilePathLabel, gbc);

		startButton = new JButton(loc_data.getString("start_button"));
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.gridy = row++;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(startButton, gbc);

		stopButton = new JButton(loc_data.getString("stop_button"));
		stopButton.setEnabled(false);
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridy = row++;
		gbl.setConstraints(stopButton, gbc);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(loc_data.getString("column_keyword"));
		model.addColumn(loc_data.getString("column_count"));
		table.setModel(model);

		gbc.gridy = row;
		JScrollPane sp = new JScrollPane(table);
		sp.setMinimumSize(sp.getPreferredSize());
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbl.setConstraints(sp, gbc);

		exportButton = new JButton(loc_data.getString("export_button"));
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 2;
		gbc.gridy = row++;
		gbl.setConstraints(exportButton, gbc);

		clearButton = new JButton(loc_data.getString("clear_button"));
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = row;
		gbl.setConstraints(clearButton, gbc);

		panel = new JPanel();
		panel.setLayout(gbl);
		panel.add(chooseFileButton);
		panel.add(exportButton);
		panel.add(sp);
		panel.add(startButton);
		panel.add(stopButton);
		panel.add(sourceFilePathLabel);
		panel.add(clearButton);

		this.setContentPane(panel);
		this.setLocation(new Point(350, 300));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.pack();
		addListeners();
	}

	/**
	 * Add listeners to view
	 */
	private void addListeners()
	{
		chooseFileButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				parserModel.openChooseTxtFileDialog();
			}
		});

		startButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				parserModel.startParsing();
			}
		});

		stopButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				parserModel.stopParsing();
			}
		});

		exportButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				parserModel.exportToXls();
			}
		});

		clearButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				parserModel.clearList();
			}
		});

	}

	/**
	 * Set source file path
	 * @param path
	 */
	public void setSourceFilePath(String path)
	{
		sourceFilePathLabel.setText(pathLabel + path);
	}

	/**
	 * Set keywords list to table
	 * @param keywordsList
	 */
	public void setKeywordsToTable(Set<String> keywordsList)
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		Vector<Vector<Object>> dataVector = model.getDataVector();
		for (String keyword : keywordsList)
		{
			boolean isFound = false;
			for (Vector rowVector : dataVector)
			{
				String firstColumn = (String) rowVector.get(0);
				if (firstColumn.equals(keyword))
				{
					isFound = true;
				}
			}
			if (!isFound)
			{
				model.addRow(new String[]
				{ keyword });
			}
		}
	}

	/**
	 * Set result value for table
	 * @param keyword
	 * @param number
	 */
	public void setTableResult(String keyword, Long number)
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int row = 0;
		Vector<Vector<Object>> dataVector = model.getDataVector();
		for (Vector rowVector : dataVector)
		{
			String firstColumn = (String) rowVector.get(0);
			if (firstColumn.equals(keyword))
			{
				break;
			}
			row++;
		}
		model.setValueAt(number, row, 1);
	}

	/**
	 * Set view when performing parsing
	 * @param isPerformingParsing
	 */
	public void setIsPerforming(boolean isPerformingParsing)
	{
		startButton.setEnabled(!isPerformingParsing);
		stopButton.setEnabled(isPerformingParsing);
		clearButton.setEnabled(!isPerformingParsing);
	}

	/**
	 * Clear table content
	 */
	public void clearTable()
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		Vector dataVector = model.getDataVector();
		dataVector.clear();
		model.fireTableDataChanged();
	}

}