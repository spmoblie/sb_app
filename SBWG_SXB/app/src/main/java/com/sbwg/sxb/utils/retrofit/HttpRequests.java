package com.sbwg.sxb.utils.retrofit;

import com.sbwg.sxb.utils.ExceptionUtil;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;


public class HttpRequests extends ObjectLoader {

    public static final int HTTP_GET = 108;
    public static final int HTTP_POST = 109;

    private static HttpRequests instance = null;
    private HttpService httpService;

    private HttpRequests() {
        httpService = RetrofitServiceManager.getInstance().create(HttpService.class);
    }

    public static HttpRequests getInstance() {
        if (instance == null) {
            synchronized (HttpRequests.class) {
                if (instance == null) {
                    instance = new HttpRequests();
                }
            }
        }
        return instance;
    }

    /**
     * Http请求
     */
    public Observable<ResponseBody> loadDatas(String paths, Map<String, String> map, int httpType) {
        Observable<ResponseBody> observable = null;
        try {
            switch (httpType) {
                case HTTP_GET:
                    if (map != null) {
                        observable = observe(httpService.get(paths, map));
                    } else {
                        observable = observe(httpService.get(paths));
                    }
                    break;
                case HTTP_POST:
                    if (paths.split("/").length > 1) {
                        String root = paths.split("/")[0];
                        String path = paths.split("/")[1];
                        observable = observe(httpService.post(root, path, map));
                    } else {
                        observable = observe(httpService.post(paths, map));
                    }
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
        return observable;
    }

    public interface HttpService {
        @GET("{path}")
        Observable<ResponseBody> get(@Path("path") String path);
        @GET("{path}")
        Observable<ResponseBody> get(@Path("path") String path, @QueryMap Map<String, String> map);

        @FormUrlEncoded
        @POST("{path}")
        Observable<ResponseBody> post(@Path("path") String path, @FieldMap Map<String, String> map);
        @FormUrlEncoded
        @POST("{root}/{path}")
        Observable<ResponseBody> post(@Path("root") String root, @Path("path") String path, @FieldMap Map<String, String> map);
    }

}
