package com.songbao.sampo_c.utils.download;

import com.songbao.sampo_c.utils.FileManager;
import com.songbao.sampo_c.utils.LogUtil;
import com.songbao.sampo_c.utils.retrofit.HttpRequests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Observer;

/**
 * Created by Beck on 2020/3/21.
 */

public class DownloadUtil {

    private static DownloadUtil instance;

    public static DownloadUtil getInstance() {
        if (instance == null) {
            synchronized (DownloadUtil.class) {
                if (instance == null){
                    instance = new DownloadUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 下载文件函数
     *
     * @param fileUrl 文件下载Url
     * @param savePath 文件保存路径
     * @param downloadListener 下载回调监听
     */
    public void downloadFile(final String fileUrl, final String savePath, final DownloadListener downloadListener) {
        HttpRequests.getInstance()
                .downloadFile(fileUrl)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody body) {
                        new Thread(new FileDownloadRun(savePath, body, downloadListener)).start();
                        LogUtil.i(LogUtil.LOG_HTTP, "onNext" + body.contentLength());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.i(LogUtil.LOG_HTTP, "error message : " + throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        // 结束处理
                        LogUtil.i(LogUtil.LOG_HTTP, "onCompleted");
                    }
                });
    }

    private class FileDownloadRun implements Runnable {
        String mSavePath;
        ResponseBody mResponseBody;
        DownloadListener mDownloadListener;

        public FileDownloadRun(String savePath, ResponseBody responseBody, final DownloadListener downloadListener) {
            mSavePath = savePath;
            mResponseBody = responseBody;
            mDownloadListener = downloadListener;
        }

        @Override
        public void run() {
            writeResponseBodyToSave(mSavePath, mResponseBody, mDownloadListener);
        }
    }

    private boolean writeResponseBodyToSave(String savePath, ResponseBody body, final DownloadListener downloadListener) {
        if (downloadListener != null)
            downloadListener.onStart();
        try {
            //检测存储位置
            FileManager.checkFilePath(savePath);
            File file = new File(savePath);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[1024];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                LogUtil.i(LogUtil.LOG_HTTP, "writeResponseBody fileSize = " + fileSize + " file = " + file.getPath());

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //计算当前下载百分比，并经由回调传出
                    if (downloadListener != null)
                        downloadListener.onProgress((int) (100 * fileSizeDownloaded / fileSize));
                }
                if (downloadListener != null)
                    downloadListener.onFinish(file);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                if (downloadListener != null)
                    downloadListener.onError("" + e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
