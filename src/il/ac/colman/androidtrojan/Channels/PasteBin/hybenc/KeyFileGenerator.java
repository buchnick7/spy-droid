/* Nethanel Gelernter (C) */

package il.ac.colman.androidtrojan.Channels.PasteBin.hybenc;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import android.content.res.AssetManager;


public class KeyFileGenerator {

	public static void GenerateKeyFiles(String pkFile, String skFile) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(Encrypter.RSA_CIPHERTEXT_LEN * 8);
		KeyPair kp = kpg.genKeyPair();
		
		KeyFactory fact = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
		  RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
		  RSAPrivateKeySpec.class);
	
		saveToFile(pkFile, pub.getModulus(),
		  pub.getPublicExponent());
		saveToFile(skFile, priv.getModulus(),
		  priv.getPrivateExponent());
	}
	
	public static void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws IOException {
		ObjectOutputStream oout = new ObjectOutputStream(
		new BufferedOutputStream(new FileOutputStream(fileName)));
		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			throw new IOException("Unexpected error", e);
		} finally {
			oout.close();
		}
	}
	
	static public PublicKey readPublicKeyFromFile(String keyFileName) throws IOException {
		  InputStream in = new FileInputStream(keyFileName);
		  ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		  try {
		    BigInteger m = (BigInteger) oin.readObject();
		    BigInteger e = (BigInteger) oin.readObject();
		    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    PublicKey pubKey = fact.generatePublic(keySpec);
		    return pubKey;
		  } catch (Exception e) {
		    throw new RuntimeException("Spurious serialisation error", e);
		  } finally {
		    oin.close();
		  }
	}
	
	static public PrivateKey readPrivateKeyFromFile(String keyFileName) throws IOException {
		  InputStream in = new FileInputStream(keyFileName);
		  ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		  try {
		    BigInteger m = (BigInteger) oin.readObject();
		    BigInteger e = (BigInteger) oin.readObject();
		    RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    PrivateKey priKey = fact.generatePrivate(keySpec);
		    return priKey;
		  } catch (Exception e) {
		    throw new RuntimeException("Spurious serialisation error", e);
		  } finally {
		    oin.close();
		  }
	}
	
	static public PublicKey readPublicKeyFromAsset(AssetManager asset, String keyFileFullPath) throws IOException {
		InputStream in = asset.open(keyFileFullPath);
/*		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
		    Log.e("wtf", line);
		}
		br.close();
		
		
			InputStream in = new FileInputStream(keyFileName);
			*/
		  ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		  try {
		    BigInteger m = (BigInteger) oin.readObject();
		    BigInteger e = (BigInteger) oin.readObject();
		    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    PublicKey pubKey = fact.generatePublic(keySpec);
		    return pubKey;
		  } catch (Exception e) {
		    throw new RuntimeException("Spurious serialisation error", e);
		  } finally {
		    oin.close();
		  }
	}
	
	static public PrivateKey readPrivateKeyFromAsset(AssetManager asset, String keyFileFullPath) throws IOException {
		  InputStream in = asset.open(keyFileFullPath);
		  ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		  try {
		    BigInteger m = (BigInteger) oin.readObject();
		    BigInteger e = (BigInteger) oin.readObject();
		    RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    PrivateKey priKey = fact.generatePrivate(keySpec);
		    return priKey;
		  } catch (Exception e) {
		    throw new RuntimeException("Spurious serialisation error", e);
		  } finally {
		    oin.close();
		  }
	}
	
}
