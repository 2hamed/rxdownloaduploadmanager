package com.hmomeni.mydownloadmanager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hmomeni.rxdownloaduploadmanager.RxUploadManager;
import com.hmomeni.rxdownloaduploadmanager.interfaces.TransferableCallback;
import com.hmomeni.rxdownloaduploadmanager.pojos.Transferable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TransferableCallback {
	private static final String TAG = "MainActivity";
	List<Transferable> items = new ArrayList<>();
	Map<String, Transferable> map;

	@SuppressWarnings("MissingPermission")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		items.add(new Transferable(
				"https://github.com/2hamed/rxdownloaduploadmanager/archive/master.zip",
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/master.zip",
				Transferable.DIR_DOWNLOAD, 2, this
		));
		items.add(new Transferable(
				"https://github.com/2hamed/PagerSlidingTabStrip/archive/master.zip",
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/master2.zip",
				Transferable.DIR_DOWNLOAD, 4, this
		));
		items.add(new Transferable(
				"https://github.com/2hamed/SlidingMenu/archive/master.zip",
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/master3.zip",
				Transferable.DIR_DOWNLOAD, 3, this
		));
		items.add(new Transferable(
				"https://github.com/2hamed/wellsql/archive/master.zip",
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/master4.zip",
				Transferable.DIR_DOWNLOAD, 5, this
		));
		RxUploadManager.setAutoStart(true);
		RxUploadManager.addToQueue(items);
		map = RxUploadManager.getMap();
	}

	@Override
	public void onProgress(Transferable transferable, int progress) {
		Log.d(TAG, "onProgress: url: " + transferable.getRemoteUrl() + " : " + progress);
	}

	@Override
	public void onFinish(Transferable transferable) {
		Log.d(TAG, "onFinish: " + transferable.getRemoteUrl() + " response: " + transferable.getServerResponse());
	}

	@Override
	public void onFail(Transferable transferable) {
		Log.d(TAG, "onFail: " + transferable.getRemoteUrl() + " response: " + transferable.getServerResponse());
	}
}
