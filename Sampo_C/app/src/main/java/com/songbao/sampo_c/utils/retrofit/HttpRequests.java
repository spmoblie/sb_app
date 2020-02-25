package com.songbao.sampo_c.utils.retrofit;

import com.songbao.sampo_c.utils.ExceptionUtil;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
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
    public Observable<ResponseBody> loadData(String head, String paths, Map<String, Object> map, int httpType) {
        Observable<ResponseBody> observable = null;
        try {
            String[] roots = paths.split("/");
            switch (httpType) {
                case HTTP_GET:
                    if (map == null) {
                        observable = observe(httpService.get(head, paths));
                    } else {
                        switch (roots.length) {
                            default:
                            case 1:
                                observable = observe(httpService.get(head, paths, map));
                                break;
                            case 2:
                                observable = observe(httpService.get(head, roots[0], roots[1], map));
                                break;
                            case 3:
                                observable = observe(httpService.get(head, roots[0], roots[1], roots[2], map));
                                break;
                            case 4:
                                observable = observe(httpService.get(head, roots[0], roots[1], roots[2], roots[3], map));
                                break;
                        }
                    }
                    break;
                case HTTP_POST:
                    switch (roots.length) {
                        default:
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
                    }
                    break;
            }
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
        return observable;
    }

    /**
     * 提交Json格式数据
     */
    public Observable<ResponseBody> postJsonData(String head, String paths, JSONObject jsonObj) {
        Observable<ResponseBody> observable = null;
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonObj.toString());
            String[] roots = paths.split("/");
            switch (roots.length) {
                default:
                case 1:
                    observable = observe(httpService.postJson(head, paths, body));
                    break;
                case 2:
                    observable = observe(httpService.postJson(head, roots[0], roots[1], body));
                    break;
                case 3:
                    observable = observe(httpService.postJson(head, roots[0], roots[1], roots[2], body));
                    break;
                case 4:
                    observable = observe(httpService.postJson(head, roots[0], roots[1], roots[2], roots[3], body));
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
                default:
                case 1:
                    observable = observe(httpService.uploadFile(head, roots[0], partList));
                    break;
                case 2:
                    observable = observe(httpService.uploadFile(head, roots[0], roots[1], partList));
                    break;
                case 3:
                    observable = observe(httpService.uploadFile(head, roots[0], roots[1], roots[2], partList));
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
        Observable<ResponseBody> get(@Header("url_head") String head, @Path("path") String path, @QueryMap Map<String, Object> map);
        @GET("{root}/{path}")
        Observable<ResponseBody> get(@Header("url_head") String head, @Path("root") String root, @Path("path") String path, @QueryMap Map<String, Object> map);
        @GET("{root1}/{root2}/{path}")
        Observable<ResponseBody> get(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("path") String path, @QueryMap Map<String, Object> map);
        @GET("{root1}/{root2}/{root3}/{path}")
        Observable<ResponseBody> get(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("root3") String root3, @Path("path") String path, @QueryMap Map<String, Object> map);

        @FormUrlEncoded
        @POST("{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("path") String path, @FieldMap Map<String, Object> map);
        @FormUrlEncoded
        @POST("{root}/{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("root") String root, @Path("path") String path, @FieldMap Map<String, Object> map);
        @FormUrlEncoded
        @POST("{root1}/{root2}/{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("path") String path, @FieldMap Map<String, Object> map);
        @FormUrlEncoded
        @POST("{root1}/{root2}/{root3}/{path}")
        Observable<ResponseBody> post(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("root3") String root3, @Path("path") String path, @FieldMap Map<String, Object> map);

        @POST("{path}")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        Observable<ResponseBody> postJson(@Header("url_head") String head, @Path("path") String path, @Body RequestBody body);
        @POST("{root}/{path}")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        Observable<ResponseBody> postJson(@Header("url_head") String head, @Path("root") String root, @Path("path") String path, @Body RequestBody body);
        @POST("{root1}/{root2}/{path}")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        Observable<ResponseBody> postJson(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("path") String path, @Body RequestBody body);
        @POST("{root1}/{root2}/{root3}/{path}")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        Observable<ResponseBody> postJson(@Header("url_head") String head, @Path("root1") String root1, @Path("root2") String root2, @Path("root3") String root3, @Path("path") String path, @Body RequestBody body);

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
