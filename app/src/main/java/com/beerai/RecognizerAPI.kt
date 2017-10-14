package com.beerai

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import rx.Observable


interface RecognizerAPI {

    @Multipart
    @POST("/predict")
    fun predict(@Part body: MultipartBody.Part): Observable<String>
}