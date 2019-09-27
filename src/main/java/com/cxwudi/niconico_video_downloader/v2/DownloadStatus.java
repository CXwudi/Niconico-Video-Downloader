package com.cxwudi.niconico_video_downloader.v2;

/**
 * An helper enum to indicate the download process result, they are
 * {@code SUCCESS, FAIL_INITIAL, FAIL_DOWNLOAD, FAIL_RENAME, FAIL_UNKNOWN}
 * @author CX无敌
 *
 */
public enum DownloadStatus {
	SUCCESS, 
	FAIL_INITIAL, FAIL_DOWNLOAD, //hard failure
	FAIL_RENAME, //soft failure
	FAIL_UNKNOWN //don't know what's wrong
}
