package com.fieldnation.userprofile.rest;


import static com.fieldnation.userprofile.utils.APIEndPoints.BASE_URL;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fieldnation.userprofile.exceptions.ConnectionHostException;
import com.fieldnation.userprofile.exceptions.NoConnectivityException;
import com.fieldnation.userprofile.exceptions.TimeOutException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class GenericApiClient {

    private static final String TAG = GenericApiClient.class.getName();
    private static Retrofit mRetrofit = null;


    public static Retrofit getRetrofitClient(Context context) {

        //setup logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //setup cache
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 20 * 1024 * 1024; // 20 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new NetworkInterceptor(context))
                .addInterceptor(logging)
                .callTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .cache(cache)
                .build();



        //add cache to the client

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (mRetrofit != null) {
            return mRetrofit;
        } else {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getUnsafeOkHttpClient().build())
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return mRetrofit;
        }
    }


    private static class NetworkInterceptor implements Interceptor {

        private Context context;

        public NetworkInterceptor(Context context) {
            this.context = context;
        }

        @NotNull
        @Override
        public Response intercept(Chain chain) throws IOException {

            if (!isConnected()) {
                throw new NoConnectivityException();
            }

            Request request = chain.request();
            request = request.newBuilder()
                    .header("Accept", "*/*")
                    .header("Content-Type", "application/json")
                    .build();

            if (isConnected()) {
                request = request.newBuilder().header("Cache-Control", "public, max-age=" + 120).build();
            } else {
                request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 365).build();
            }
            long t1 = System.nanoTime();
            Log.d(TAG, String.format("Sending request %s on %s", request.url(), request.headers()));

            try {
                Response response = chain.proceed(request);
                long t2 = System.nanoTime();
                Log.d(TAG, String.format("Received response for %s in %.1fms  %s", response.request().url(), (t2 - t1) / 1e6d, response.toString()));
                return response;

            } catch (SocketTimeoutException e) {
                throw new TimeOutException();
            } catch (ConnectException e) {
                throw new ConnectionHostException();
            } catch (IOException ioException) {
                throw new NoConnectivityException();
            }

        }

        public boolean isConnected() {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        }

    }


    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
