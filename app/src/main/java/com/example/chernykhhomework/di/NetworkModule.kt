package com.example.chernykhhomework.di

import com.example.chernykhhomework.data.network.api.AuthorizationApi
import com.example.chernykhhomework.data.network.api.LoansDataSourceApi
import com.example.chernykhhomework.data.network.interceptor.LogInErrorInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@Module
class NetworkModule {

    @Provides
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .build()

    @Provides
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    fun provideOkHttpClientWithErrorInterceptor(interceptor: LogInErrorInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

    @Provides
    fun provideRetrofit(moshiConverterFactory: MoshiConverterFactory): Retrofit =
        Retrofit.Builder()
            .baseUrl(LoansDataSourceApi.BASE_URL)
            .addConverterFactory(moshiConverterFactory)
            .build()


    @Provides
    fun provideLoansRetrofitService(retrofit: Retrofit): LoansDataSourceApi =
        retrofit.create(LoansDataSourceApi::class.java)

    @Provides
    fun provideAuthorizationRetrofitService(retrofit: Retrofit): AuthorizationApi =
        retrofit.create(AuthorizationApi::class.java)

}