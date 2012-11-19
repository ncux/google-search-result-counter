package gsearchparser;

import gsearchparser.mvc.ParserModel;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Parse google search page for each keyword from <code>keywordsList</code>.
 * 
 * @author SHaurushkin
 */
public class GoogleResultCounterParser implements Runnable
{

	private List<String> keywordsList = null;
	private ParserModel parserModel = null;
	private boolean isExit = false;

	/**
	 * @param parserModel
	 * @param keywordList
	 */
	public GoogleResultCounterParser(ParserModel parserModel,
			List<String> keywordList)
	{
		this.keywordsList = keywordList;
		this.parserModel = parserModel;
	}

	@Override
	public void run()
	{
		for (int i = 0; i < keywordsList.size(); i++)
		{
			// if we need to exit
			if (isExit)
			{
				parserModel.setThreadFinished();
				return;
			}

			String keyword = keywordsList.get(i);

			// check if result already exists
			if (parserModel.getResultMapValue(keyword) != null)
			{
				continue;
			}

			try
			{
				String keywordForWeb = URLEncoder.encode(keyword, "UTF-8");
				Document doc = Jsoup
						.connect(
								"http://www.google.ru/search?q="
										+ keywordForWeb
										+ "&hl=en&ie=UTF-8&oe=UTF8")
						.userAgent(
								"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
						.get();
				Element el = doc.getElementById("resultStats");
				if (el != null)
				{
					Long number = parseResultString(el.ownText());
					parserModel.addToResultMap(keyword, number);
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		parserModel.setThreadFinished();
	}

	/**
	 * Parse string with amount of results
	 * @param value
	 * @return amount of search results
	 */
	private Long parseResultString(String value)
	{
		if (value.isEmpty())
		{
			return Long.valueOf("0");
		}

		String strWithoutEscapes = StringEscapeUtils.unescapeHtml(value);
		strWithoutEscapes = strWithoutEscapes.replaceAll("\\D+", "");

		Long resultNumber = null;
		try
		{
			resultNumber = Long.valueOf(strWithoutEscapes);
		} catch (NumberFormatException exc)
		{
			exc.printStackTrace();
		}

		return resultNumber;
	}

	/**
	 * Set thread to stop
	 * @param isExit
	 */
	synchronized public void setExit(boolean isExit)
	{
		this.isExit = isExit;
	}
}
