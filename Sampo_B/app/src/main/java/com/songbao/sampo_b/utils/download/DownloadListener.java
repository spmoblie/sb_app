package com.songbao.sampo_b.utils.download;

import java.io.File;

public interface DownloadListener {

    void onStart();
    void onProgress(int p);
    void onFinish(File file);
    void onError(String msg);

}
