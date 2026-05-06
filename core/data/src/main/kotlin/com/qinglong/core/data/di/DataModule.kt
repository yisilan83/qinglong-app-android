package com.qinglong.core.data.di

import com.qinglong.core.data.local.AuthLocalDataSource
import com.qinglong.core.data.remote.QLRetrofitClient
import com.qinglong.core.data.remote.TokenProvider
import com.qinglong.core.data.repository.AuthRepositoryImpl
import com.qinglong.core.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenProvider(impl: AuthLocalDataSource): TokenProvider
}
