package pl.lbiio.quickadoption.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.lbiio.quickadoption.services.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "https://your_goocle_cloud_domain/api/" // this link will be generated after gcloud app deploy

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val customRetrofit = ApiService.createRetrofit(BASE_URL)
        return customRetrofit.create(ApiService::class.java)
    }

}
