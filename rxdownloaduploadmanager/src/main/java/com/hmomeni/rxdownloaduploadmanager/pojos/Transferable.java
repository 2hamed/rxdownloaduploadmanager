package com.hmomeni.rxdownloaduploadmanager.pojos;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.hmomeni.rxdownloaduploadmanager.interfaces.TransferableCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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


	String remoteUrl;
	String localPath;
	@Status
	int status;
	@Direction
	int direction;

	String hash;
	int progress = 0;
	int priority = 0;

	TransferableCallback callback;

	public Transferable(String remoteUrl, String localPath, int direction, int priority) {
		this.remoteUrl = remoteUrl;
		this.localPath = localPath;
		this.direction = direction;
		this.priority = priority;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
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
