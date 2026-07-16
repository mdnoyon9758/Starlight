package com.barcodereader.di

import android.content.Context
import com.barcodereader.service.ProductLookupService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Service Module
 *
 * Provides service instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideProductLookupService(
        @ApplicationContext context: Context
    ): ProductLookupService {
        return ProductLookupService(context)
    }
}