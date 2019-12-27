package com.cxwudi.niconico_videodownloader.util;

/**
 * An helper enum to indicate the download process result, they are
 * {@code SUCCESS, FAIL_INITIAL, FAIL_DOWNLOAD, UNKNOWN_STATUS}
 * @author CX无敌
 *
 */
public enum DownloadStatus {
	SUCCESS, 
	FAIL_INITIAL, FAIL_DOWNLOAD, //hard failure
	UNKNOWN_STATUS; //soft failure, don't know if the download success or not
}
