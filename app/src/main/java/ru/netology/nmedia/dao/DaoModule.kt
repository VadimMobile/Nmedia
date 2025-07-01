package ru.netology.nmedia.dao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import ru.netology.nmedia.db.AppDb

@InstallIn
@Module
object DaoModule {
    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()
}