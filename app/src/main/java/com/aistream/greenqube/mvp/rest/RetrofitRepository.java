package com.aistream.greenqube.mvp.rest;

import android.util.Log;

import com.aistream.greenqube.mvp.gson.JsonExclusionStrategy;
import com.aistream.greenqube.mvp.gson.ResponseDeserializer;
import com.aistream.greenqube.mvp.model.ResponseWrapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 5/17/2017.
 */

public class RetrofitRepository {

    private static class RetrofitRepositoryPublicHolder {
        private static final RetrofitRepository instance = new RetrofitRepository();
    }

    public static RetrofitRepository getInstance() {
        return RetrofitRepositoryPublicHolder.instance;
    }

    private GsonConverterFactory gsonConverterFactory;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;

    public RetrofitRepository() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(ResponseWrapper.class, new ResponseDeserializer())
                .addSerializationExclusionStrategy(new JsonExclusionStrategy())
                .addDeserializationExclusionStrategy(new JsonExclusionStrategy())
                .create();
        OkHttpClient.Builder builder = (new OkHttpClient()).newBuilder();
        builder.readTimeout(15L, TimeUnit.SECONDS);
        builder.connectTimeout(10L, TimeUnit.SECONDS);
        builder.writeTimeout(10L, TimeUnit.SECONDS);
        builder.cache(null);
        try {
            TrustManager[] interceptor = new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext cacheSize = SSLContext.getInstance("TLS");
            cacheSize.init((KeyManager[]) null, interceptor, new SecureRandom());
            SSLSocketFactory cache = cacheSize.getSocketFactory();
            builder.sslSocketFactory(cache);
            builder.hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();
        } catch (Exception var9) {
            throw new RuntimeException(var9);
        }

        HttpLoggingInterceptor interceptor1 = new HttpLoggingInterceptor();
        interceptor1.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor1);
//        int cacheSize1 = 52428800;
//        Cache cache1 = new Cache(OgleApplication.getInstance().getCacheDir(), (long) cacheSize1);
//        builder.cache(cache1);
        gsonConverterFactory = GsonConverterFactory.create(gson);
        okHttpClient = builder.build();
    }

    public <T> T getApiService(Class<T> service, String endpoint) {
        if (retrofit == null || !retrofit.baseUrl().toString().equals(endpoint)) {
            retrofit = (new retrofit2.Retrofit.Builder()).client(okHttpClient).addConverterFactory(gsonConverterFactory).baseUrl(endpoint).build();
            Log.i("RetrofitRepository", "baseUrl: " + retrofit.baseUrl().toString() + " endpoint:" + endpoint);
        }
        return retrofit.create(service);
    }

}
