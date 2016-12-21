/* Nethanel Gelernter (C) */

package il.ac.colman.androidtrojan.Channels.PasteBin.hybenc;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import il.ac.colman.androidtrojan.Channels.PasteBin.authenc.AesMacAuthEnc;


public class Decrypter {

	private PrivateKey sk;
	
	public Decrypter(PrivateKey sk)
	{
		this.sk = sk;
	}

	
	public Decrypter(){}
	
	
	public byte[] decrypt(byte[] inCiphertext) throws Exception
	{
		// Extract the symmetric key, and the ciphertext:
		byte[] encSymKeyBytes = new byte[Encrypter.RSA_CIPHERTEXT_LEN];
		byte[] ciphertext = new byte[inCiphertext.length - Encrypter.RSA_CIPHERTEXT_LEN];
		System.arraycopy(inCiphertext, 0, encSymKeyBytes, 0, Encrypter.RSA_CIPHERTEXT_LEN);
		System.arraycopy(inCiphertext, Encrypter.RSA_CIPHERTEXT_LEN, ciphertext, 0, inCiphertext.length - Encrypter.RSA_CIPHERTEXT_LEN);
		
	
		// Decrypt the symmetric key, using the private key (sk)
		Cipher cipher;
		byte[] symKeyBytes = null;
		try {
			cipher = Cipher.getInstance(Encrypter.ASYM_ALGO);
			cipher.init(Cipher.DECRYPT_MODE, sk);
			symKeyBytes = cipher.doFinal(encSymKeyBytes);
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
		authEnc.setKey(symKeyBytes);
		byte[] plaintext = authEnc.decVer(ciphertext);
		
		return plaintext;
	}
	
	public byte[] decrypt(PrivateKey sk, byte[] inCiphertext) throws Exception
	{
		// Extract the symmetric key, and the ciphertext:
		byte[] encSymKeyBytes = new byte[Encrypter.RSA_CIPHERTEXT_LEN];
		byte[] ciphertext = new byte[inCiphertext.length - Encrypter.RSA_CIPHERTEXT_LEN];
		System.arraycopy(inCiphertext, 0, encSymKeyBytes, 0, Encrypter.RSA_CIPHERTEXT_LEN);
		System.arraycopy(inCiphertext, Encrypter.RSA_CIPHERTEXT_LEN, ciphertext, 0, inCiphertext.length - Encrypter.RSA_CIPHERTEXT_LEN);
		
	
		// Decrypt the symmetric key, using the private key (sk)
		Cipher cipher;
		byte[] symKeyBytes = null;
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, sk);
			symKeyBytes = cipher.doFinal(encSymKeyBytes);
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
		authEnc.setKey(symKeyBytes);
		byte[] plaintext = authEnc.decVer(ciphertext);
		
		return plaintext;
	}
}