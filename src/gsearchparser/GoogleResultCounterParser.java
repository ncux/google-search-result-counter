package gsearchparser;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 
 * 
 * @author SHaurushkin
 */
public class GoogleResultCounterParser extends Thread
{

	private List<String> keywordsList = null;
	private ParserModel parserModel = null;
	private Map<String, Long> resultMap = null;
	private boolean isStop = false;

	@Override
	public void run()
	{
		Iterator<String> it = keywordsList.iterator();
		while (it.hasNext())
		{
			if (isStop)
			{
				parserModel.setFinished(true);
				return;
			}

			String keyword = it.next();
			if (resultMap.get(keyword) != null)
			{
				continue;
			}

			String keywordForWeb = StringEscapeUtils.escapeHtml4(keyword);

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
					Long number = parseResultString(value);
					parserModel.addToResultMap(keyword, number);
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		parserModel.setFinished(true);
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

	public void setStop(boolean isStop)
	{
		this.isStop = isStop;
	}

	public void setResultMap(Map<String, Long> resultMap)
	{
		this.resultMap = resultMap;
	}
}
