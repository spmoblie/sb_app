package com.songbao.sxb.utils.retrofit;

import com.songbao.sxb.utils.ExceptionUtil;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
     * 加载数据
     */
    public Observable<ResponseBody> loadData(String head, String paths, Map<String, String> map, int httpType) {
        Observable<ResponseBody> observable = null;
        try {
            switch (httpType) {
                case HTTP_GET:
                    if (map != null) {
                        observable = observe(httpService.get(head, paths, map));
                    } else {
                        observable = observe(httpService.get(head, paths));
                    }
                    break;
                case HTTP_POST:
                    String[] roots = paths.split("/");
                    switch (roots.length) {
                        case 1:
                            observable = observe(httpService.post(head, paths, map));
                            break;
                        case 2:
                            observable = observe(httpService.post(head, roots[0], roots[1], map));
                            break;
                        case 3:
                            observable = observe(httpService.post(head, roots[0], roots[1], roots[2], map));
                            break;
                        case 4:
                            observable = observe(httpService.post(head, roots[0], roots[1], roots[2], roots[3], map));
                            break;
                        default:
                            observable = observe(httpService.post(head, paths, map));
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
        return observable;
    }

    /**
     * 上传文件
     */
    public Observable<ResponseBody> uploadFile(String head, String paths, List<MultipartBody.Part> partList) {
        Observable<ResponseBody> observable = null;
        try {
            String[] roots = paths.split("/");
            switch (roots.length) {
                case 1:
                    observable = observe(httpService.uploadFile(head, roots[0], partList));
                    break;
                case 2:
                    observable = observe(httpService.uploadFile(head, roots[0], roots[1], partList));
                    break;
                case 3:
                    observable = observe(httpService.uploadFile(head, roots[0], roots[1], roots[2], partList));
                    break;
                default:
                    observable = observe(httpService.uploadFile(head, roots[0], partList));
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
        return observable;
    }

    public interface HttpService {

        @GET("{path}")
        Observable<ResponseBody> get(@Header("url_head") String head, @Path("path") String path);
        @GET("{path}")
        Observable<ResponseBody> get(@Header("url_head") String head, @Path("path") String path, @QueryMap Map<String, String> map);

        @FormUrlEncoded
        @POST("{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("path") String path, @FieldMap Map<String, String> map);
        @FormUrlEncoded
        @POST("{root}/{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("root") String root, @Path("path") String path, @FieldMap Map<String, String> map);
        @FormUrlEncoded
        @POST("{root1}/{root2}/{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("path") String path, @FieldMap Map<String, String> map);
        @FormUrlEncoded
        @POST("{root1}/{root2}/{root3}/{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("root3") String root3, @Path("path") String path, @FieldMap Map<String, String> map);

        @Multipart
        @POST("{path}")
        Observable<ResponseBody> uploadFile(@Header("url_head") String head, @Path("path") String path, @Part List<MultipartBody.Part> partList);
        @Multipart
        @POST("{root}/{path}")
        Observable<ResponseBody> uploadFile(@Header("url_head") String head, @Path("root") String root, @Path("path") String path, @Part List<MultipartBody.Part> partList);
        @Multipart
        @POST("{root1}/{root2}/{path}")
        Observable<ResponseBody> uploadFile(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("path") String path, @Part List<MultipartBody.Part> partList);
    }

}
