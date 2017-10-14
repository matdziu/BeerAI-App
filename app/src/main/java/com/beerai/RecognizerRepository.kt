package com.beerai

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class RecognizerRepository(val recognizerAPI: RecognizerAPI) {

    fun predict(imageFile: File?): Observable<String> {
        val uploadImageFile = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val body = MultipartBody.Part.createFormData("image_file", imageFile?.name, uploadImageFile)
        return recognizerAPI.predict(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}