/*
 * Created by Lukas (LukaLike) on Sat, Sep 4, '21.
 * Copyright (c) 2021. All rights reserved.
 */

package com.example.exoplayer.data.services

import com.example.exoplayer.data.models.Message
import io.reactivex.Observable
import retrofit2.http.GET

interface MessagesService {

    @GET("/RandInt")
    fun getMessage(): Observable<Message>

}