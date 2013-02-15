package gsearchparser.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ParserController implements ModelSubscriber {

	/** Model */
	private ParserModel model = null;

	/**
	 * View
	 */
	private ParserDialog view = null;

	public ParserController(ParserModel model, ParserDialog dialog) {
		this.model = model;
		this.view = dialog;

		// Add listeners
		view.addChooseTxtFileListener(new ChooseTxtFileListener());
		view.addStartParsingListener(new StartParsingListener());
		view.addStopParsingListener(new StopParsingListener());
		view.addExportListener(new ExportListener());
		view.addClearTableListener(new ClearTableListener());
		view.addAddRowButtonListener(new AddRowButtonListener());
		view.addRemoveRowButtonListener(new RemoveRowButtonListener());
	}

	class ChooseTxtFileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			File openedFile = view.openChooseTxtFile();

			if (openedFile != null) {
				view.setSourceFilePath(openedFile.getAbsolutePath());
				model.parseTxtFile(openedFile);
				view.setTableData(model.getKeywordsList());
			}
		}
	}

	class StartParsingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int threadNumber = view.getThreadNumber();
			if (threadNumber == 0) {
				return;
			}

			model.setThreadNumber(threadNumber);
			model.setResultMap(view.getKeywordsMap());
			model.startParsing();
		}
	}

	class StopParsingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.stopParsing();
		}
	}

	class ExportListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (model.getResultMap().isEmpty()) {
				view.showWarningEmptyData();
				return;
			}

			File openedFile = view.chooseFileToSave();
			if (openedFile != null) {
				model.setResultMap(view.getKeywordsMap());
				model.export(openedFile);
			}
		}
	}
	
	class ClearTableListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.clearList();
			view.clearTable();
			view.setSourceFilePath(null);
		}
	}

	class AddRowButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			view.addRow();
		}
	}

	class RemoveRowButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			view.removeSelectedRow();
		}
	}

	@Override
	public void modelChanged() {
		view.setTableResult(model.getResultMap());
		view.setIsPerforming(model.isPerforming());
	}
}
