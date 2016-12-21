/* Nethanel Gelernter (C) */

package il.ac.colman.androidtrojan.Channels.PasteBin.hybenc;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import il.ac.colman.androidtrojan.Channels.PasteBin.authenc.AesMacAuthEnc;
import il.ac.colman.androidtrojan.Channels.PasteBin.authenc.AesMacKey;

/*
 * Uses RSA to encrypt a generated AES symmetric key, and encrypt the message with the symmetric key.
 */
public class Encrypter {

	public static int RSA_CIPHERTEXT_LEN = 256; // 256 bytes = 2048 bits
	public static String ASYM_ALGO = "RSA/ECB/PKCS1Padding";
	
	private PublicKey pk;
	
	public Encrypter(PublicKey pk) {
		this.pk = pk;
	}
	
	public byte[] encrypt(byte[] plaintext)
	{
		byte[] encryptedKey = null;
	
		// Generate new AuthEnc (symmetric) key
		AesMacKey symKeyObj = new AesMacKey();
		byte[] symKeyBytes = symKeyObj.getKey();
		
		// Encrypt sk, with the public key
		Cipher cipher;
		try {
			//cipher = Cipher.getInstance("RSA");
			cipher = Cipher.getInstance(ASYM_ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, pk);
			encryptedKey = cipher.doFinal(symKeyBytes);
			//System.out.println("encryptedKey.length = " + encryptedKey.length);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Encrypt the plaintext with the sk
		AesMacAuthEnc authEnc = new AesMacAuthEnc();
		authEnc.setKey(symKeyObj);
		byte[] ciphertext = authEnc.encAuth(plaintext);
		
		//System.out.println("plaintext, ciphertext (auth-enc) = " + plaintext.length +  ", " +ciphertext.length);
		
		byte[] output = new byte[RSA_CIPHERTEXT_LEN + ciphertext.length];
		System.arraycopy(encryptedKey, 0, output, 0, RSA_CIPHERTEXT_LEN);
		System.arraycopy(ciphertext, 0, output, RSA_CIPHERTEXT_LEN, ciphertext.length);
		
		//System.out.println("output = " + output.length);
		
		// Return the encrypted sk (first RSA_CIPHERTEXT_LEN bytes) and the encrypted plaintext concatenated
		return output;
	}
}