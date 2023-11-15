package pl.lbiio.quickadoption.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.lbiio.quickadoption.navigation.AppNavigator
import pl.lbiio.quickadoption.navigation.AppNavigatorImpl

@Module
@InstallIn(SingletonComponent::class)
object AppNavigatorModule {

    @Provides
    fun provideAppNavigator(): AppNavigator {
        return AppNavigatorImpl()
    }
}