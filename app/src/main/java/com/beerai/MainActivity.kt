package com.beerai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    var currentImageFile: File? = null
    lateinit var recognizerAPI: RecognizerAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        recognizerAPI = (application as BeerAIApplication).getRecognizerAPI()

        scan_label_button.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                try {
                    currentImageFile = createImageFile()
                } catch (e: IOException) {
                    Toast.makeText(this, getString(R.string.file_error_text), Toast.LENGTH_SHORT).show()
                }
                if (currentImageFile != null) {
                    val imageURI = FileProvider.getUriForFile(this,
                            "com.beerai.fileprovider", currentImageFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search_item -> {
                startActivity(Intent(this, SearchActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            showProgressBar(true)
            val imageFile = RequestBody.create(MediaType.parse("image/*"), currentImageFile)
            val body = MultipartBody.Part.createFormData("image_file", currentImageFile?.name, imageFile)
            recognizerAPI.predict(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                response ->
                                showProgressBar(false)
                                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                            },
                            {
                                error ->
                                showProgressBar(false)
                                Toast.makeText(this, getString(R.string.recognizer_error_text), Toast.LENGTH_SHORT).show()
                                Log.e("RecognizerAPI", error.toString())
                            })
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )
        return image
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            main_content_layout.visibility = View.GONE
            progress_bar.visibility = View.VISIBLE
        } else {
            main_content_layout.visibility = View.VISIBLE
            progress_bar.visibility = View.GONE
        }
    }
}