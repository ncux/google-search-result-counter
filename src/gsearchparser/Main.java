package gsearchparser;

import gsearchparser.mvc.ParserController;
import gsearchparser.mvc.ParserDialog;
import gsearchparser.mvc.ParserModel;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
	public static void main(String[] argv) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ParserModel model = new ParserModel();
				ParserDialog dialog = new ParserDialog(model);
				ParserController controller = new ParserController(model,
						dialog);
				model.subscribe(controller);
				dialog.setVisible(true);
			}
		});
	}
}
