package com.wan.wanandroid.retrofit

import okhttp3.*
import okhttp3.internal.Util.UTF_8
import okhttp3.internal.http.RealResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection.HTTP_MOVED_TEMP
import java.net.HttpURLConnection.HTTP_OK

/**
 * Created by Woody on 2018/3/13.
 */
class RetrofitUtil private constructor(){

    companion object {
        fun getInstance() : Retrofit{
            return Holder.INSTANCE
        }
    }

    private object Holder{
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .addInterceptor(Interceptor{
                    val req: Request = it.request()
                    var resp:Response = it.proceed(req)
                    if(resp.code() == HTTP_MOVED_TEMP){
                        resp.header("location")
                        val json = "{\"url\":\"" + resp.header("location") + "\"}"
                        val buffer:Buffer = Buffer().writeString(json, UTF_8)
                        resp = resp.newBuilder().code(HTTP_OK)
                                .body(RealResponseBody("application/json",
                                        -1,
                                        buffer))
                                .build()
                    }
                    return@Interceptor resp
                })
                .followRedirects(false)
                .build()
        val INSTANCE: Retrofit = Retrofit.Builder()
                .baseUrl("http://www.wanandroid.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(AnnotatedConverters
//                        .add(Gson::class.java, GsonConverterFactory.create())
//                        .build())
                .client(httpClient)
                .build()
    }
}