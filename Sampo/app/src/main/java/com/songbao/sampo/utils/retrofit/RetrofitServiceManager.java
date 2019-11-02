package com.songbao.sampo.utils.retrofit;


import android.support.annotation.NonNull;

import com.songbao.sampo.AppApplication;
import com.songbao.sampo.AppConfig;
import com.songbao.sampo.utils.ExceptionUtil;
import com.songbao.sampo.utils.LogUtil;
import com.songbao.sampo.utils.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitServiceManager {

    //设缓存有效期为1天
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 1;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    private static final int DEFAULT_TIME_OUT_CONN = 5; //超时时间 5s
    private static final int DEFAULT_TIME_OUT_READ = 10;

    private Retrofit mRetrofit;

    private RetrofitServiceManager(){
        //添加公共参数拦截器
        HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder()
                //.addHeaderParams("X-APP-Token", UserManager.getInstance().getXAppToken())
                .build();
        // 指定缓存路径,缓存大小100Mb
        Cache cache = new Cache(new File(AppApplication.getAppContext().getCacheDir(),
                "HttpCache"), 1024 * 1024 * 100);
        //创建OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .retryOnConnectionFailure(true)
                .addInterceptor(commonInterceptor)
                .addInterceptor(sBaseUrlInterceptor)
                .addInterceptor(sLoggingInterceptor)
                .addInterceptor(sRewriteCacheControlInterceptor)
                .addNetworkInterceptor(sRewriteCacheControlInterceptor)
                .readTimeout(DEFAULT_TIME_OUT_READ, TimeUnit.SECONDS) //读取超时时间
                .writeTimeout(DEFAULT_TIME_OUT_READ, TimeUnit.SECONDS) //写入超时时间
                .connectTimeout(DEFAULT_TIME_OUT_CONN, TimeUnit.SECONDS); //连接超时时间

        //setCard(builder); //设置信任证书

        //创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()) //添加转换器Converter(将Json转为JavaBean)
                .baseUrl(AppConfig.BASE_URL_1)
                .build();
    }

    /**
     * 设置信任证书
     * @param builder
     */
    public void setCard(OkHttpClient.Builder builder) {
        if (builder == null) return;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("ca",
                    cf.generateCertificate(AppApplication.getAppContext().getAssets().open(""))); //拷贝好的证书
            SSLContext sslContext = SSLContext.getInstance("TLS");
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory());
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            ExceptionUtil.handle(e);
        }
    }

    /**
     * BaseUrl拦截器
     */
    private static final Interceptor sBaseUrlInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            //获取request
            Request request = chain.request();
            //获取request的创建者builder
            Request.Builder builder = request.newBuilder();
            //从request中获取headers，通过给定的键url_name
            List<String> headerValues = request.headers("url_head");
            if (headerValues != null && headerValues.size() > 0) {
                //匹配获得新的BaseUrl
                String headerValue = headerValues.get(0);
                HttpUrl newBaseUrl;
                if ("base_1".equals(headerValue)) {
                    return chain.proceed(request);
                } else
                if ("base_2".equals(headerValue)) {
                    newBaseUrl = HttpUrl.parse(AppConfig.BASE_URL_2);
                } else {
                    newBaseUrl = HttpUrl.parse(headerValue);
                }
                LogUtil.i(LogUtil.LOG_HTTP, "BaseUrl value -> " + headerValue);

                //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
                //builder.removeHeader(HttpConfig.HEADER_KEY);

                //从request中获取原有的HttpUrl实例oldHttpUrl
                HttpUrl oldHttpUrl = request.url();
                //重建新的HttpUrl，修改需要修改的url部分
                HttpUrl newFullUrl = oldHttpUrl
                        .newBuilder()
                        .scheme(newBaseUrl.scheme())
                        .host(newBaseUrl.host())
                        .port(newBaseUrl.port())
                        .build();

                //重建这个request，通过builder.url(newFullUrl).build(),然后返回一个response至此结束修改
                return chain.proceed(builder.url(newFullUrl).build());
            } else {
                return chain.proceed(request);
            }
        }
    };

    /**
     * Log信息打印拦截器
     */
    private static final Interceptor sLoggingInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Buffer requestBuffer = new Buffer();
            if (request.body() != null) {
                request.body().writeTo(requestBuffer);
            } else {
                LogUtil.i(LogUtil.LOG_HTTP, "request.body() is null");
            }
            //打印Path信息
            LogUtil.i(LogUtil.LOG_HTTP, "Path : " + request.url() + (request.body() != null ? "?" + _parseParams(request.body(), requestBuffer) : ""));
            Response response = chain.proceed(request);
            MediaType mediaType = response.body().contentType();
            //打印Body信息
            String body = response.body().string();
            LogUtil.i(LogUtil.LOG_HTTP, "Body : " + body);
            return response.newBuilder().body(ResponseBody.create(mediaType, body)).build();
        }
    };

    @NonNull
    private static String _parseParams(RequestBody body, Buffer requestBuffer) throws UnsupportedEncodingException {
        if (body.contentType() != null && !body.contentType().toString().contains("multipart")) {
            return URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8");
        }
        return "null";
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private static final Interceptor sRewriteCacheControlInterceptor = new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.isNetworkAvailable()) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                LogUtil.i(LogUtil.LOG_HTTP, "no network");
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtil.isNetworkAvailable()) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                return originalResponse.newBuilder()
                        .header("Cache-Control", request.cacheControl().toString())
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, " + CACHE_CONTROL_CACHE)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };

    private static class SingletonHolder{
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    /**
     * 获取RetrofitServiceManager
     * @return
     */
    public static RetrofitServiceManager getInstance(){
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取对应的Service
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service){
        return mRetrofit.create(service);
    }
}
