/*
MIT License

Copyright (c) 2016 Qcenzo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software. 

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.qcenzo.tools.localize; 

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.fanyi.api.BaiduTranslate;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class TranslatorManager
{
	private static final TranslatorManager _INSTANCE = new TranslatorManager(); 
	private static final String _BING = "bing";
	private static final String _BAIDU = "baidu";
	private Map<String, String[]> _dataProviders;
	private String[] _baiduFTAbbr;
	private String _translator;
	private int _from;
	private int _to;
	private Pattern _contentPattern;
	
	public String[][] labelAndValues; 
	public String defaultTranslator; 
	public int defaultFromTo;
	 
	private TranslatorManager()
	{
		labelAndValues = new String[][]{{"&Bing", _BING}, { "B&aidu", _BAIDU}};
		defaultTranslator = _BING; 
		defaultFromTo = (9 << 8) + 4; 
		
		_dataProviders = new HashMap<String, String[]>();
		_dataProviders.put(_BING, new String[] 
			{
				"AUTO_DETECT",
				"ARABIC",
				"BULGARIAN",
				"CATALAN",
				"CHINESE_SIMPLIFIED",
				"CHINESE_TRADITIONAL",
				"CZECH",
				"DANISH",
				"DUTCH",
				"ENGLISH",
				"ESTONIAN",
				"FINNISH",
				"FRENCH",
				"GERMAN",
				"GREEK",
				"HAITIAN_CREOLE",
				"HEBREW",
				"HINDI",
				"HMONG_DAW",
				"HUNGARIAN",
				"INDONESIAN",
				"ITALIAN",
				"JAPANESE",
				"KOREAN",
				"LATVIAN",
				"LITHUANIAN",
				"MALAY",
				"NORWEGIAN",
				"PERSIAN",
				"POLISH",
				"PORTUGUESE",
				"ROMANIAN",
				"RUSSIAN",
				"SLOVAK",
				"SLOVENIAN",
				"SPANISH",
				"SWEDISH",
				"THAI",
				"TURKISH",
				"UKRAINIAN",
				"URDU",
				"VIETNAMESE"
			});
		_dataProviders.put(_BAIDU, new String[] 
			{
				"AUTO_DETECT",
				"ARABIC",
				"BULGARIAN",
				"CHINESE_SIMPLIFIED",
				"CHINESE_TRADITIONAL",
				"CZECH",
				"DANISH",
				"DUTCH",
				"ENGLISH",
				"ESTONIAN",
				"FINNISH",
				"FRENCH",
				"GERMAN",
				"GREEK",
				"HUNGARIAN",
				"ITALIAN",
				"JAPANESE",
				"KOREAN",
				"POLISH",
				"PORTUGUESE",
				"ROMANIAN",
				"RUSSIAN",
				"SLOVENIAN",
				"SPANISH",
				"SWEDISH",
				"THAI"
			});
		
		Translate.setClientId("srcloc");
		Translate.setClientSecret("1vazdyH7NIfVz2vjWtsfzIzKceOgGjFaBBCMXYDa34I=");
		
		BaiduTranslate.appId = "20160905000028206";
		BaiduTranslate.token = "V5nYHItcgjAhIR1P2meE";
		_baiduFTAbbr = new String[]
			{
				"auto",
				"ara",
				"bul",
				"zh",
				"cht",
				"cs",
				"dan",
				"nl",
				"en",
				"est",
				"fin",
				"fra",
				"de",
				"el",
				"hu",
				"it",
				"jp",
				"kor",
				"pl",
				"pt",
				"rom",
				"ru",
				"slo",
				"spa",
				"swe",
				"th"
			};
		
		_contentPattern = Pattern.compile("\\w.*\\w", Pattern.DOTALL);  
	}
	
	public static TranslatorManager getInstance()
	{
		return _INSTANCE;
	}
	
	public String[] getItems(String translator)
	{
		return _dataProviders.get(translator);
	}
	
	public void init(String translator, int fromto)
	{
		_translator = translator;
		_from = fromto >> 8 & 0xFF;
		_to = fromto & 0xFF; 
		
		if (_to == 0)
		{
			String[] items = getItems(translator);
			while (_to < items.length)
			{
				if (items[_to] == "ENGLISH")
					break;
				_to++;
			}
		}
	}
	
	public String execute(String text) throws Exception
	{
		Matcher matcher = _contentPattern.matcher(text); 
		Boolean found = matcher.find();
		
		String query = found ? matcher.group() : text;
		String result = null;
		 
		if (_translator == _BING)
		{
			String[] items = _dataProviders.get(_translator);
			result = Translate.execute(query, Language.valueOf(items[_from]), Language.valueOf(items[_to]));
		}
		else if (_translator == _BAIDU)
		{
			result = BaiduTranslate.execute(query, _baiduFTAbbr[_from], _baiduFTAbbr[_to]);  
		}
 
		return found ? text.replace(query, result) : result; 
	}
}