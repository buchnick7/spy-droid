/* Nethanel Gelernter (C) */

package il.ac.colman.androidtrojan.Channels.PasteBin.authenc;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class AesMacAuthEnc implements AuthEnc {

	public final int MAC_TAG_LEN = 32;
	public final int BLOCK_SIZE = 16;
	public final static String ENC_ALG = "AES/CBC/PKCS5Padding";
	public final static String MAC_ALG = "HmacSHA256";
	private AuthEncKey authEncKey;
	private Cipher encCipher;
	private Cipher decCipher;
	private Mac mac;

	public AesMacAuthEnc()
	{
		try {
			this.encCipher = Cipher.getInstance(ENC_ALG);
			this.decCipher = Cipher.getInstance(ENC_ALG);
            this.mac = Mac.getInstance(MAC_ALG);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	/*
	 * The first BLOCK_SIZE bytes (128 bits) of the ciphertext are the IV	
	 */
	public byte[] decVer(byte[] inCiphertext) throws Exception {
		byte[] macTag = new byte[MAC_TAG_LEN];
		int ciphertextLen = inCiphertext.length - BLOCK_SIZE - MAC_TAG_LEN;
		byte[] IV = new byte[BLOCK_SIZE];
		byte[] ciphertext = new byte[ciphertextLen];
		
		System.arraycopy(inCiphertext, 0, macTag, 0, MAC_TAG_LEN);
		System.arraycopy(inCiphertext, MAC_TAG_LEN, IV, 0, BLOCK_SIZE);
		System.arraycopy(inCiphertext, MAC_TAG_LEN + BLOCK_SIZE, ciphertext, 0, ciphertextLen);
		
		// Decrypt the message:
		byte[] plaintext = null;
		try {
			decCipher.init(Cipher.DECRYPT_MODE, this.authEncKey.getEncKey(), new IvParameterSpec(IV));
			plaintext = decCipher.doFinal(ciphertext);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Verify the MAC:
		byte[] IVandCipher = new byte[BLOCK_SIZE + ciphertext.length];
		System.arraycopy(IV, 0, IVandCipher, 0, BLOCK_SIZE);
		System.arraycopy(ciphertext, 0, IVandCipher, BLOCK_SIZE, ciphertext.length);

		// Calculate the MAC on the IV and the ciphertext
		try {
			this.mac.init(this.authEncKey.getMacKey());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (Arrays.equals(macTag, this.mac.doFinal(IVandCipher)))
			return plaintext;
		else
			throw new Exception("MAC authentication failed");
	}

	@Override
	/*
	 * The first BLOCK_SIZE bytes (128 bits) of the output are the IV	
	 */
	public byte[] encAuth(byte[] plaintext) {
		byte[] ciphertext = null;
		byte[] macTag = new byte[MAC_TAG_LEN];
		byte[] IV = new byte[BLOCK_SIZE];
		random.nextBytes(IV);
		
		try {
			encCipher.init(Cipher.ENCRYPT_MODE, this.authEncKey.getEncKey(), new IvParameterSpec(IV));
			ciphertext = encCipher.doFinal(plaintext);
			//System.out.println("AES - plaintext, ciphertext (AES) = " + plaintext.length +  ", " +ciphertext.length);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] IVandCipher = new byte[BLOCK_SIZE + ciphertext.length];
		System.arraycopy(IV, 0, IVandCipher, 0, BLOCK_SIZE);
		System.arraycopy(ciphertext, 0, IVandCipher, BLOCK_SIZE, ciphertext.length);

		// Calculate the MAC on the IV and the ciphertext
		try {
			this.mac.init(this.authEncKey.getMacKey());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		macTag = this.mac.doFinal(IVandCipher);
		
		// The output is: bytes of the MAC, BLOCK_SIZE bytes of IV, and then the ciphertext. 
		byte[] output = new byte[MAC_TAG_LEN + IVandCipher.length];
		System.arraycopy(macTag, 0, output, 0, MAC_TAG_LEN);
		System.arraycopy(IVandCipher, 0, output, MAC_TAG_LEN, IVandCipher.length);
		return output;
	}

	@Override
	public void genKey() {
		this.authEncKey = new AesMacKey();
	}

	
	@Override
	public AuthEncKey getAuthEncKey() {
		return this.authEncKey;
	}

	@Override
	public void setKey(AuthEncKey authEncKey) {
		this.authEncKey = authEncKey;
		
	}

	@Override
	public void setKey(byte[] key) {
		this.authEncKey = new AesMacKey(key);
	}
}
