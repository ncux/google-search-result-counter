package gsearchparser;


public class Main
{
	public static void main(String[] argv)
	{
		ParserModel model = new ParserModel();
		ParserDialog dialog = new ParserDialog(model);
		dialog.pack();
		dialog.setVisible(true);
	}
}
