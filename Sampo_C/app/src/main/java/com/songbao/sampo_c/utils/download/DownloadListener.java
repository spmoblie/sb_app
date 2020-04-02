package com.songbao.sampo_c.utils.download;

import java.io.File;

/**
 * Created by Beck on 2020/3/21.
 */

public interface DownloadListener {

    void onStart();
    void onProgress(int p);
    void onFinish(File file);
    void onError(String msg);

}
