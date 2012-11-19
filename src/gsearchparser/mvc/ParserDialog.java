package gsearchparser.mvc;

import gsearchparser.common.window.AbstractFrame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

import org.apache.commons.io.FilenameUtils;

/**
 * Main dialog view
 * 
 * @author SHaurushkin
 */
public class ParserDialog extends AbstractFrame {
	private static final long serialVersionUID = 6959318357186149652L;

	private static final int COLUMN_KEYWORD = 0;
	private static final int COLUMN_RESULT = 1;

	private static DecimalFormat resultFormat = new DecimalFormat("#,###");

	/** visual components */
	private JPanel mainPanel = null;
	private JLabel sourceFilePathLabel = null;
	private JTable table = null;
	private JButton startButton = null;
	private JButton stopButton = null;
	private JButton chooseFileButton = null;
	private JButton exportButton = null;
	private JButton clearButton = null;
	private JSpinner threadNumberSpinner = null;
	private JButton addRowButton = null;
	private JButton removeRowButton = null;

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public ParserDialog(ParserModel model) {
		super();
		this.setTitle(loc_data.getString("title"));
		this.setContentPane(getMainPanel());
		this.setMinimumSize(new Dimension(350, 600));
		initDialogFrame();
	}

	/**
	 * Get main panel
	 * 
	 * @return
	 */
	@SuppressWarnings("serial")
	private Container getMainPanel() {
		if (mainPanel == null) {
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

			sourceFilePathLabel = new JLabel(loc_data.getString("path"));
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			gbc.gridy = row++;
			gbc.weighty = 1;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbl.setConstraints(sourceFilePathLabel, gbc);

			JLabel threadNumberLabel = new JLabel(
					loc_data.getString("thread_number"));
			gbc.gridwidth = 1;
			gbc.gridx = 0;
			gbc.gridy = row;
			gbl.setConstraints(threadNumberLabel, gbc);

			threadNumberSpinner = new JSpinner();
			threadNumberSpinner.setPreferredSize(new Dimension(45, 20));
			threadNumberSpinner.setMinimumSize(threadNumberSpinner
					.getPreferredSize());

			SpinnerNumberModel threadSpinnerModel = new SpinnerNumberModel();
			threadSpinnerModel.setMinimum(1);
			threadSpinnerModel.setValue(1);
			threadNumberSpinner.setModel(threadSpinnerModel);

			JFormattedTextField txt = ((JSpinner.NumberEditor) threadNumberSpinner
					.getEditor()).getTextField();
			((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

			gbc.gridx = 1;
			gbc.gridy = row++;
			gbl.setConstraints(threadNumberSpinner, gbc);

			startButton = new JButton(loc_data.getString("start_button"));
			startButton.setMinimumSize(startButton.getPreferredSize());
			gbc.gridwidth = 2;
			gbc.weighty = 0;
			gbc.gridwidth = 1;
			gbc.gridx = 0;
			gbc.gridy = row++;
			gbc.anchor = GridBagConstraints.WEST;
			gbl.setConstraints(startButton, gbc);

			stopButton = new JButton(loc_data.getString("stop_button"));
			stopButton.setEnabled(false);
			gbc.gridx = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridy = row++;
			gbl.setConstraints(stopButton, gbc);

			table = new JTable() {
				public boolean isCellEditable(int rowIndex, int colIndex) {
					// reject result changes
					if (colIndex == COLUMN_RESULT) {
						return false;
					}
					return true;
				}
			};
			DefaultTableModel model = new DefaultTableModel();
			model.addColumn(loc_data.getString("column_keyword"));
			model.addColumn(loc_data.getString("column_count"));

			table.setModel(model);

			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(new Dimension(300, 300));
			sp.setMinimumSize(new Dimension(300, 300));

			JPanel tablePanel = new JPanel(new BorderLayout());
			tablePanel.add(sp, BorderLayout.NORTH);

			addRowButton = new JButton("+");
			addRowButton.setMargin(new Insets(0, 0, 0, 0));
			addRowButton.setPreferredSize(new Dimension(20, 20));

			tablePanel.add(addRowButton, BorderLayout.CENTER);

			removeRowButton = new JButton("-");
			removeRowButton.setMargin(new Insets(0, 0, 0, 0));
			removeRowButton.setPreferredSize(new Dimension(20, 20));

			tablePanel.add(removeRowButton, BorderLayout.SOUTH);

			gbc.anchor = GridBagConstraints.CENTER;
			gbc.weighty = 1;
			gbc.gridx = 0;
			gbc.gridy = row++;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbl.setConstraints(tablePanel, gbc);

			exportButton = new JButton(loc_data.getString("export_button"));
			gbc.anchor = GridBagConstraints.EAST;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.gridy = row;
			gbl.setConstraints(exportButton, gbc);

			clearButton = new JButton(loc_data.getString("clear_button"));
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 1;
			gbc.gridy = row;
			gbl.setConstraints(clearButton, gbc);

			mainPanel = new JPanel();
			mainPanel.setLayout(gbl);
			mainPanel.add(chooseFileButton);
			mainPanel.add(exportButton);
			mainPanel.add(tablePanel);
			mainPanel.add(startButton);
			mainPanel.add(stopButton);
			mainPanel.add(sourceFilePathLabel);
			mainPanel.add(clearButton);
			mainPanel.add(threadNumberLabel);
			mainPanel.add(threadNumberSpinner);
		}
		return mainPanel;
	}

	/**
	 * Set source file path
	 * 
	 * @param path
	 *            to source file
	 */
	public void setSourceFilePath(String path) {
		if (path == null) {
			path = "";
		}

		sourceFilePathLabel.setText(loc_data.getString("path") + path);
	}

	/**
	 * Set keywords list to table
	 * 
	 * @param keywordsList
	 */
	public void setTableData(Collection<String> keywordsList) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> dataVector = model.getDataVector();
		for (String keyword : keywordsList) {
			boolean isFound = false;
			for (Vector<Object> rowVector : dataVector) {
				String firstColumn = (String) rowVector.get(0);
				if (firstColumn != null && firstColumn.equals(keyword)) {
					isFound = true;
				}
			}
			if (!isFound) {
				model.addRow(new String[] { keyword });
			}
		}
	}

	public void setTableResult(Map<String, Long> map) {
		Iterator<Entry<String, Long>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Long> entry = iter.next();
			setTableResult(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Set result value for table
	 * 
	 * @param keyword
	 * @param number
	 */
	public void setTableResult(String keyword, Long number) {
		if (number == null) {
			return;
		}

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int row = 0;

		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> dataVector = model.getDataVector();
		for (Vector<Object> rowVector : dataVector) {
			String firstColumn = (String) rowVector.get(0);
			if (firstColumn != null && firstColumn.equals(keyword)) {
				break;
			}
			row++;
		}

		String numberFormatted = resultFormat.format(number);

		model.setValueAt(numberFormatted, row, 1);
	}

	/**
	 * Set view when performing parsing
	 * 
	 * @param isPerformingParsing
	 */
	public void setIsPerforming(boolean isPerformingParsing) {
		startButton.setEnabled(!isPerformingParsing);
		clearButton.setEnabled(!isPerformingParsing);
		exportButton.setEnabled(!isPerformingParsing);
		stopButton.setEnabled(isPerformingParsing);
	}

	/**
	 * Clear table content
	 */
	public void clearTable() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();

		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> dataVector = model.getDataVector();
		dataVector.clear();
		model.fireTableDataChanged();
	}

	/**
	 * Get number of threads
	 * 
	 * @return
	 */
	public Integer getThreadNumber() {
		return (Integer) threadNumberSpinner.getValue();
	}

	/**
	 * Open warning window with warning about empty data
	 */
	public void showWarningEmptyData() {
		JOptionPane.showMessageDialog(this, loc_data.getString("warn_no_data"),
				loc_data.getString("title"), JOptionPane.INFORMATION_MESSAGE);
	}

	public void addChooseTxtFileListener(ActionListener actionListener) {
		chooseFileButton.addActionListener(actionListener);
	}

	public void addStartParsingListener(ActionListener actionListener) {
		startButton.addActionListener(actionListener);
	}

	public void addStopParsingListener(ActionListener actionListener) {
		stopButton.addActionListener(actionListener);
	}

	public void addExportListener(ActionListener actionListener) {
		exportButton.addActionListener(actionListener);
	}

	public void addClearTableListener(ActionListener actionListener) {
		clearButton.addActionListener(actionListener);
	}

	public void addAddRowButtonListener(ActionListener actionListener) {
		addRowButton.addActionListener(actionListener);
	}

	public void addRemoveRowButtonListener(ActionListener actionListener) {
		removeRowButton.addActionListener(actionListener);
	}

	public File openChooseTxtFile() {
		File file = null;

		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		}

		return file;
	}

	/**
	 * Choose XLS file to save
	 * 
	 * @return
	 */
	public File chooseXlsToSave() {
		File file = null;

		final JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				String extension = FilenameUtils.getExtension(f.getName());
				if (extension != null && extension.equalsIgnoreCase("XLS")) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return "Excel 97-2003 Workbook (*.xls)";
			}
		});

		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			if (!file.getName().toLowerCase().endsWith(".xls")) {
				file = new File(file.getAbsolutePath() + ".xls");
			}
		}

		return file;
	}

	public void addRow() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new String[] {});
	}

	public Map<String, Long> getKeywordsForSearch() {
		Map<String, Long> resultMap = new HashMap<String, Long>();

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> dataVector = model.getDataVector();
		for (Vector<Object> rowVector : dataVector) {
			String keywordColumn = (String) rowVector.get(COLUMN_KEYWORD);
			if (keywordColumn == null || keywordColumn.isEmpty()) {
				continue;
			}

			String resultColumn = (String) rowVector.get(COLUMN_RESULT);
			Long amount = null;
			try {

				amount = (Long) resultFormat.parse(resultColumn);
			} catch (ParseException | NullPointerException e) {
				// ignore, put null
			}
			resultMap.put(keywordColumn, amount);
		}

		return resultMap;
	}

	/**
	 * Remove selected rows from the table
	 */
	public void removeSelectedRow() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		int[] selectedRows = table.getSelectedRows();

		if (selectedRows.length == 0) {
			if (model.getRowCount() > 0) {
				model.removeRow(model.getRowCount() - 1); // remove last row
			}
		}

		// remove starting from last row
		for (int i = selectedRows.length - 1; i >= 0; i--) {
			model.removeRow(selectedRows[i]);
		}
	}
}