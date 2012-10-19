package gsearchparser;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
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
	private static final long serialVersionUID = 6959318357186149652L;
	private static final String MLS_PATH = "Path: ";

	private ParserModel parserModel = null;

	private JPanel panel = null;
	private JLabel sourceFilePathLabel = null;
	private JTable table;
	private JButton startButton;
	private JButton stopButton;
	private JButton chooseFileButton;
	private JButton exportButton;

	public ParserDialog(ParserModel model)
	{
		super("Google count result parser");
		parserModel = model;
		initialize();
		model.setView(this);
	}

	private void initialize()
	{
		int row = 0;
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		chooseFileButton = new JButton("Choose source");
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = row++;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets.left = gbc.insets.top = gbc.insets.bottom = gbc.insets.right = 5;
		gbl.setConstraints(chooseFileButton, gbc);

		sourceFilePathLabel = new JLabel(MLS_PATH);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridy = row++;
		gbc.weighty = 0;
		gbl.setConstraints(sourceFilePathLabel, gbc);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		startButton = new JButton("Start");
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridy = row++;
		gbc.anchor = GridBagConstraints.WEST;
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		gbl.setConstraints(buttonPanel, gbc);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("KeyWords");
		model.addColumn("Count");
		table.setModel(model);

		gbc.gridy = row;
		JScrollPane sp = new JScrollPane(table);
		sp.setMinimumSize(sp.getPreferredSize());
		gbl.setConstraints(sp, gbc);

		exportButton = new JButton("Export");
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 1;
		gbc.gridy = row;
		gbl.setConstraints(exportButton, gbc);

		panel = new JPanel();
		panel.setLayout(gbl);
		panel.add(chooseFileButton);
		panel.add(exportButton);
		panel.add(sp);
		panel.add(buttonPanel);
		panel.add(sourceFilePathLabel);

		this.setContentPane(panel);
		this.setLocation(new Point(350, 300));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		addListeners();
	}

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
	}

	public void setSourceFilePath(String path)
	{
		sourceFilePathLabel.setText(MLS_PATH + path);
	}

	public void setKeywordsToTable(List<String> keywordsList)
	{
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.getDataVector().clear();

		for (String keyword : keywordsList)
		{
			model.addRow(new String[]
			{ keyword });
		}
	}

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

	public void setIsPerforming(boolean isPerformingParsing)
	{
		startButton.setEnabled(!isPerformingParsing);
		stopButton.setEnabled(isPerformingParsing);

	}

}