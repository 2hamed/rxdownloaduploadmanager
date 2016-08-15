package com.hmomeni.rxdownloaduploadmanager;

import android.Manifest;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.support.v4.util.ArrayMap;

import com.hmomeni.rxdownloaduploadmanager.pojos.Transferable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hamed on 8/14/16.in MyDownloadManager
 */
public class RxDownloadManager {
	private static RxDownloadManager instance;


	OkHttpClient okHttpClient;
	Map<String, Transferable> map = new ArrayMap<>();
	List<Transferable> list = new ArrayList<>();
	private boolean isDownloading = false;
	private static boolean autoStart = false;
	private Handler handler;

	public RxDownloadManager() {
		okHttpClient = new OkHttpClient.Builder().build();
		handler = new Handler();
	}

	public static void setAutoStart(boolean autoStart) {
		RxDownloadManager.autoStart = autoStart;
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
			instance = new RxDownloadManager();
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
			instance = new RxDownloadManager();
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
		if (isDownloading) {
			return;
		}
		if (list.size() == 0) {
			instance = null;
			return;
		}
		final Transferable currentItem;
		currentItem = list.get(0);

		new Thread(new Runnable() {
			@Override
			public void run() {
				isDownloading = true;
				Request request = new Request.Builder().url(currentItem.getRemoteUrl()).build();
				File localFile = new File(currentItem.getLocalPath());
				Response response;
				boolean success = false;
				try {
					response = okHttpClient.newCall(request).execute();
					if (response.code() == 200) {
						InputStream inputStream = null;
						OutputStream outputStream = new FileOutputStream(localFile);
						try {
							inputStream = response.body().byteStream();
							byte[] buff = new byte[1024 * 4];
							long downloaded = 0;
							final long target = response.body().contentLength();
							currentItem.getCallback().onProgress(currentItem, 0);
							while (true) {
								int readed = inputStream.read(buff);
								if (readed == -1) {
									break;
								}
								//write buff
								downloaded += readed;
								outputStream.write(buff, 0, readed);
								final long finalDownloaded = downloaded;
								handler.post(new Runnable() {
									@Override
									public void run() {
										currentItem.getCallback().onProgress(currentItem, (int) ((float) finalDownloaded / target * 100F));
									}
								});
							}
							success = true;
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (inputStream != null) {
								outputStream.flush();
								inputStream.close();
								outputStream.close();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				final boolean finalSuccess = success;
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (finalSuccess) {
							currentItem.getCallback().onFinish(currentItem);
						} else {
							currentItem.getCallback().onFail(currentItem);
						}
					}
				});

				map.remove(currentItem.getHash());
				list.remove(currentItem);
				isDownloading = false;
				processQueue();
			}
		}).start();
	}
}
