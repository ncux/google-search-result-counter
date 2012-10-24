package gsearchparser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 
 * 
 * @author SHaurushkin
 */
public class GoogleResultCounterParser implements Runnable
{

	private List<String> keywordsList = null;
	private ParserModel parserModel = null;
	private boolean isExit = false;

	@Override
	public void run()
	{
		for (int i = 0; i < keywordsList.size(); i++)
		{
			if (isExit)
			{
				parserModel.setThreadFinished();
				return;
			}

			String keyword = keywordsList.get(i);
			if (parserModel.getResultMapValue(keyword) != null)
			{
				continue;
			}

			String keywordForWeb = keyword;
			try
			{
				keywordForWeb = URLEncoder.encode(keywordForWeb, "UTF-8");
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
					String value = el.ownText();
					Long number = null;
					if (!value.isEmpty())
					{
						number = parseResultString(value);
					} else
					{
						number = Long.valueOf("0");
					}
					parserModel.addToResultMap(keyword, number);
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		parserModel.setThreadFinished();
	}

	private Long parseResultString(String value)
	{
		String strWithoutEscapes = StringEscapeUtils.unescapeHtml4(value);
		strWithoutEscapes = strWithoutEscapes.replaceAll("\\D+", "");

		Long resultNumber = null;
		try
		{
			resultNumber = Long.valueOf(strWithoutEscapes);
		} catch (NumberFormatException exc)
		{

		}

		return resultNumber;
	}

	public void setKeywords(List<String> keywordsList)
	{
		this.keywordsList = keywordsList;
	}

	public void setParserModel(ParserModel parserModel)
	{
		this.parserModel = parserModel;
	}

	synchronized public void setStop(boolean isExit)
	{
		this.isExit = isExit;
	}
}
