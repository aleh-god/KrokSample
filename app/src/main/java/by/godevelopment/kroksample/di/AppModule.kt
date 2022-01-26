package by.godevelopment.kroksample.di

import android.content.Context
import by.godevelopment.kroksample.data.datasources.network.KrokApi
import by.godevelopment.kroksample.data.datasources.network.KrokRemoteDataSource
import by.godevelopment.kroksample.data.datasources.preferences.KrokPreferences
import by.godevelopment.kroksample.data.repositories.NetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideBaseUrl() : String = "https://krokapp.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL : String,
                        okHttpClient: OkHttpClient
    ) : Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    @Provides
    @Singleton
    fun provideKrokApi(retrofit: Retrofit) = retrofit.create(KrokApi::class.java)

    @Provides
    @Singleton
    fun provideKrokRemoteDataSource(
        krokApi: KrokApi,
        coroutineDispatcher: CoroutineDispatcher
    ): KrokRemoteDataSource = KrokRemoteDataSource(krokApi, coroutineDispatcher)

    @Provides
    @Singleton
    fun provideKrokPreferences(
        @ApplicationContext appContext: Context
    ): KrokPreferences = KrokPreferences(appContext)

    @Provides
    @Singleton
    fun provideNetworkRepository(
        krokRemoteDataSource: KrokRemoteDataSource,
        externalScope: CoroutineScope
    ): NetworkRepository = NetworkRepository(krokRemoteDataSource, externalScope)
}
