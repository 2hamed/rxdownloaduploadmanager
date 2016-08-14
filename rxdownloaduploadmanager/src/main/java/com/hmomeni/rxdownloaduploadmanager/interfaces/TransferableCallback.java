package com.hmomeni.rxdownloaduploadmanager.interfaces;

import com.hmomeni.rxdownloaduploadmanager.pojos.Transferable;

/**
 * Created by hamed on 8/14/16.in MyDownloadManager
 */
public interface TransferableCallback {
	void onProgress(Transferable transferable, int progress);

	void onFinish(Transferable transferable);

	void onFail(Transferable transferable);
}
