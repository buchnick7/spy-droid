/* Nethanel Gelernter (C) */

package il.ac.colman.androidtrojan.Channels.PasteBin.authenc;
import javax.crypto.SecretKey;

public interface AuthEncKey {
	SecretKey getEncKey();
	SecretKey getMacKey();
	byte[] getKey();
}
