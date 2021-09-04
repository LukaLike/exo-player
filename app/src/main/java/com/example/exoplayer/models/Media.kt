/*
 * Created by Lukas (LukaLike) on Sat, Sep 4, '21.
 * Copyright (c) 2021. All rights reserved.
 */

package com.example.exoplayer.models

import com.google.gson.annotations.SerializedName

class Media {

    @Suppress("unused")
    @SerializedName("samples")
    var list: ArrayList<Uri?>? = null

    @Suppress("unused")
    class Uri {
        @SerializedName("uri")
        var uri: String? = null
    }

}