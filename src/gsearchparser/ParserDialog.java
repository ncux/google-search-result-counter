package gsearchparser;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class ParserDialog extends JFrame {
	private static final String MLS_PATH = "Path: ";
	private ParserModel model = null;
	private JPanel panel = null;
	private JLabel sourceFilePathLabel = null;

	public ParserDialog(ParserModel model) {
		super("Google number parser");
		this.model = model;
		initialize();
		model.setView(this);
	}

	private void initialize() {
		panel = new JPanel();
		int row = 0;
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JButton chooseFileButton = new JButton("Choose source");
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbl.setConstraints(chooseFileButton, gbc);
		row++;

		sourceFilePathLabel = new JLabel(MLS_PATH);
		gbc.gridy = row;
		gbl.setConstraints(sourceFilePathLabel, gbc);
		row++;

		JButton startButton = new JButton("Start");
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(startButton, gbc);

		JButton stopFileButton = new JButton("Stop");
		gbc.gridy = row;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(stopFileButton, gbc);
		row++;

		JButton exportButton = new JButton("Export");
		gbc.gridy = row;
		gbl.setConstraints(exportButton, gbc);

		panel.setLayout(gbl);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		chooseFileButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				model.openChooseTxtFileDialog();
			}
		});

		exportButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				model.exportToXls();
			}
		});

		panel.add(chooseFileButton);
		panel.add(exportButton);
		panel.add(startButton);
		panel.add(stopFileButton);
		panel.add(sourceFilePathLabel);
		this.setContentPane(panel);
		setMinimumSize(new Dimension(200, 100));
		setPreferredSize(new Dimension(200, 100));
	}

	public void setSourceFilePath(String path) {
		sourceFilePathLabel.setText(MLS_PATH + path);
	}
}