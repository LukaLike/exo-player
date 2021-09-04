/*
 * Created by Lukas (LukaLike) on Sat, Sep 4, '21.
 * Copyright (c) 2021. All rights reserved.
 */

package com.example.exoplayer.data.service_factories

import com.example.exoplayer.data.services.MessagesService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object MessagesServiceFactory {

    private const val API_BASE_URL = "http://84.32.134.211:5003/"

    fun create(): MessagesService {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MessagesService::class.java)
    }

}