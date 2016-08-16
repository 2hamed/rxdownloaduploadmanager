package com.hmomeni.rxdownloaduploadmanager;

import android.Manifest;
import android.support.annotation.RequiresPermission;
import android.support.v4.util.ArrayMap;
import android.webkit.MimeTypeMap;

import com.hmomeni.rxdownloaduploadmanager.pojos.Transferable;
import com.hmomeni.rxdownloaduploadmanager.util.CountingFileRequestBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hamed on 8/14/16.in MyDownloadManager
 */
public class RxUploadManager {
	private static RxUploadManager instance;


	OkHttpClient okHttpClient;
	Map<String, Transferable> map = new ArrayMap<>();
	List<Transferable> list = new ArrayList<>();
	private boolean isUploading = false;
	private static boolean autoStart = false;

	public RxUploadManager() {
		okHttpClient = new OkHttpClient.Builder().build();
	}

	public static void setAutoStart(boolean autoStart) {
		RxUploadManager.autoStart = autoStart;
	}

	public static void start() {
		if (instance != null) {
			instance.processQueue();
		}
	}

	public static Map<String, Transferable> getMap() {
		if (instance == null) {
			return null;
		}
		return instance.map;
	}

	@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
	public static void addToQueue(List<Transferable> items) {
		if (instance == null) {
			instance = new RxUploadManager();
		}
		for (Transferable transferable :
				items) {
			instance.map.put(transferable.getHash(), transferable);
			instance.list.add(transferable);
		}


		Collections.sort(instance.list, new Comparator<Transferable>() {
			@Override
			public int compare(Transferable t1, Transferable t2) {
				if (t1.getPriority() >= t2.getPriority()) {
					return 1;
				}
				return 0;
			}
		});
		if (autoStart) {
			instance.processQueue();
		}
	}


	@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
	public static void addToQueue(Transferable transferable) {
		if (instance == null) {
			instance = new RxUploadManager();
		}
		instance.map.put(transferable.getHash(), transferable);
		instance.list.add(transferable);

		Collections.sort(instance.list, new Comparator<Transferable>() {
			@Override
			public int compare(Transferable t1, Transferable t2) {
				if (t1.getPriority() >= t2.getPriority()) {
					return 1;
				}
				return 0;
			}
		});
		if (autoStart) {
			instance.processQueue();
		}
	}

	private void processQueue() {

		if (isUploading) {
			return;
		}
		if (list.size() == 0) {
			instance = null;
			return;
		}
		final Transferable currentItem;
		currentItem = list.get(0);
		File file = new File(currentItem.getLocalPath());
		if (file.exists()) {

			MultipartBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("media", file.getName(), RequestBody.create(MediaType.parse(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath()))), file)).build();
			CountingFileRequestBody countingFileRequestBody = new CountingFileRequestBody(multipartBody, currentItem.getHash(), new CountingFileRequestBody.ProgressListener() {
				@Override
				public void transferred(String key, int num) {
					currentItem.getCallback().onProgress(currentItem, num);
				}
			});

			Request request = new Request.Builder()
					.tag(currentItem.getHash())
					.url(currentItem.getRemoteUrl())
					.post(countingFileRequestBody)
					.build();

			okHttpClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					handleFinished(currentItem, false);
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					currentItem.setServerResponse(response.body().string());
					handleFinished(currentItem, response.code() == 200);
				}
			});
		} else {
			handleFinished(currentItem, false);
		}

	}

	private void handleFinished(Transferable transferable, boolean success) {

		if (success) {
			transferable.getCallback().onFinish(transferable);
		} else {
			transferable.getCallback().onFail(transferable);
		}

		map.remove(transferable.getHash());
		list.remove(transferable);
		isUploading = false;
		processQueue();
	}

}
