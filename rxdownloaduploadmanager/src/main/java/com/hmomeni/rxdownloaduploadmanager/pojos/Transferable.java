package com.hmomeni.rxdownloaduploadmanager.pojos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.hmomeni.rxdownloaduploadmanager.interfaces.TransferableCallback;
import com.hmomeni.rxdownloaduploadmanager.util.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Created by hamed on 8/14/16.in MyDownloadManager
 */
public class Transferable implements Parcelable {
	@IntDef({DIR_DOWNLOAD, DIR_UPLOAD})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Direction {
	}

	@IntDef({STATUS_FINISHED, STATUS_FAILED, STATUS_IN_PROGRESS, STATUS_NOT_STARTED})
	@Retention(RetentionPolicy.SOURCE)
	public @interface Status {
	}

	public static final int STATUS_FINISHED = 0;
	public static final int STATUS_NOT_STARTED = 1;
	public static final int STATUS_FAILED = 2;
	public static final int STATUS_IN_PROGRESS = 3;

	public static final int DIR_DOWNLOAD = 0;
	public static final int DIR_UPLOAD = 1;


	private String remoteUrl;
	private String localPath;
	@Status
	private
	int status = STATUS_NOT_STARTED;
	@Direction
	private
	int direction = DIR_DOWNLOAD;

	private String hash;
	private int progress = 0;
	private int priority = 0;

	private String paramName;

	private TransferableCallback callback;

	private String serverResponse;

	public Transferable(String remoteUrl,
	                    String localPath,
	                    int direction,
	                    int priority,
	                    TransferableCallback callback) {
		this.remoteUrl = remoteUrl;
		this.localPath = localPath;
		this.direction = direction;
		this.priority = priority;
		this.callback = callback;
		createHash();
	}

	public Transferable(String remoteUrl,
	                    String localPath,
	                    int status,
	                    int direction,
	                    String hash,
	                    int progress,
	                    int priority,
	                    String paramName,
	                    TransferableCallback callback, String serverResponse) {
		this.remoteUrl = remoteUrl;
		this.localPath = localPath;
		this.status = status;
		this.direction = direction;
		this.hash = hash;
		this.progress = progress;
		this.priority = priority;
		this.paramName = paramName;
		this.callback = callback;
		this.serverResponse = serverResponse;
	}

	private void createHash() {
		hash = Util.md5(String.format(Locale.ENGLISH, "%s%s%d", remoteUrl, localPath, direction));
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
		createHash();
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
		createHash();
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
		createHash();
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public TransferableCallback getCallback() {
		return callback;
	}

	public void setCallback(TransferableCallback callback) {
		this.callback = callback;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getServerResponse() {
		return serverResponse;
	}

	public void setServerResponse(String serverResponse) {
		this.serverResponse = serverResponse;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.remoteUrl);
		dest.writeString(this.localPath);
		dest.writeInt(this.status);
		dest.writeInt(this.direction);
		dest.writeString(this.hash);
		dest.writeInt(this.progress);
	}

	@SuppressWarnings("WrongConstant")
	protected Transferable(Parcel in) {
		this.remoteUrl = in.readString();
		this.localPath = in.readString();
		this.status = in.readInt();
		this.direction = in.readInt();
		this.hash = in.readString();
		this.progress = in.readInt();
	}

	public static final Parcelable.Creator<Transferable> CREATOR = new Parcelable.Creator<Transferable>() {
		@Override
		public Transferable createFromParcel(Parcel source) {
			return new Transferable(source);
		}

		@Override
		public Transferable[] newArray(int size) {
			return new Transferable[size];
		}
	};
}
