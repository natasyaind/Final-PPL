package com.example.ppl.apiservice;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            try {
                // Membuat SSLContext dengan TrustManager yang tidak memvalidasi sertifikat
                SSLContext sslContext = SSLContext.getInstance("TLS");
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);

                // TrustManager untuk tidak memvalidasi sertifikat
                X509TrustManager trustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Tidak ada validasi sertifikat klien
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Tidak ada validasi sertifikat server
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        // Mengembalikan array kosong
                        return new X509Certificate[0];
                    }
                };

                // Inisialisasi SSLContext untuk menerima semua sertifikat
                sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

                // Menambahkan Interceptor untuk Authorization Header dan log level HTTP
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(chain -> {
                            // Pastikan token Anda sudah didapatkan dengan benar
                            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTczNDA2MTM0MiwianRpIjoiZThlMzExZWEtYTA1OS00NzQyLTllMWItYjNlMWI4MzhiYWQzIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6ImFuZHJvaWQiLCJuYmYiOjE3MzQwNjEzNDIsImNzcmYiOiJlMzlmOGVmYi0xYjQ5LTRhMDItYWFhYy0yMmEyY2JjODEyMDgiLCJleHAiOjE3MzQwNjQ5NDJ9.CYoEWz9PF_HuO-vHKtMW9rMuliCY7NufKrFPXK8thVE";

                            // Menambahkan header Authorization ke setiap permintaan
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("Authorization", "Bearer " + token); // Menambahkan header Authorization
                            Request request = requestBuilder.build();
                            Log.d("Interceptor", "Request Headers: " + request.headers().toString());
                            return chain.proceed(request);
                        })
                        .connectTimeout(120, TimeUnit.SECONDS) // Menambahkan timeout koneksi
                        .readTimeout(120, TimeUnit.SECONDS) // Timeout baca
                        .writeTimeout(120, TimeUnit.SECONDS) // Timeout tulis
                        .build();

                // Membuat Retrofit instance dengan OkHttpClient yang sudah dikonfigurasi
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://simkeu.unhas.ac.id:8110/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

            } catch (Exception e) {
                Log.e("RetrofitClient", "Error during SSL setup: " + e.getMessage());
            }
        }
        return retrofit;
    }
}
