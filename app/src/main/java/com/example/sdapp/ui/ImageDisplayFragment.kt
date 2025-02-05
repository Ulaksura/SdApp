package com.example.sdapp.ui

import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sdapp.R
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageDisplayFragment : Fragment() {
    private lateinit var view: View
    private lateinit var mainInterface: MainInterface
    private lateinit var fileName: String
    public lateinit var imageData: ByteArray
    public lateinit var seedUsed: String
    public lateinit var request: Request
//    public lateinit var requestUrl: String
//    public lateinit var requestHeaders: String
//    public lateinit var requestPost: String
    public lateinit var prompt: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //view = inflater.inflate(R.layout.image_display, container, false)
      // initialize()
        return inflater.inflate(R.layout.image_display, container, false)//view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            imageData = bundle.getByteArray("imageData")!!
            seedUsed = bundle.getString("seedUsed").toString()
            prompt = bundle.getString("prompt").toString()

        val localPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val preferenceWriter = localPreferences.edit()
        var imageCount = localPreferences.getInt("imageCount", 0)
        imageCount++
        preferenceWriter.putInt("imageCount", imageCount)
        preferenceWriter.apply()
        fileName = "stabledif_$imageCount.jpg"

        mainInterface = activity as MainInterface

        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        view.findViewById<ImageView>(R.id.imageDisplayPicture).setImageBitmap(bitmap)

        view.findViewById<TextView>(R.id.seedDisplay).text = "Seed used: " + seedUsed

        //val regenerateElement = view.findViewById<Button>(R.id.regenerate)
//        regenerateElement.setOnClickListener { regenerateImage() }

        val generateElement = view.findViewById<Button>(R.id.generateNew)
        generateElement.setOnClickListener { mainInterface.showImg2Img() }

        val useAsInitElement = view.findViewById<Button>(R.id.useAsInit)
        useAsInitElement.setOnClickListener { mainInterface.setImage(bitmap, fileName) }

        val saveElement = view.findViewById<Button>(R.id.save)
        saveElement.setOnClickListener { saveImage() }
        //initialize()
        }
    }

//    private fun initialize() {
//        val localPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        val preferenceWriter = localPreferences.edit()
//        var imageCount = localPreferences.getInt("imageCount", 0)
//        imageCount++
//        preferenceWriter.putInt("imageCount", imageCount)
//        preferenceWriter.apply()
//        fileName = "stabledif_$imageCount.jpg"
//
//        mainInterface = activity as MainInterface
//
//        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
//        view.findViewById<ImageView>(R.id.imageDisplayPicture).setImageBitmap(bitmap)
//
//        view.findViewById<TextView>(R.id.seedDisplay).text = "Seed used: " + seedUsed
//
//   //     val regenerateElement = view.findViewById<Button>(R.id.regenerate)
////        regenerateElement.setOnClickListener { regenerateImage() }
//
//        val generateElement = view.findViewById<Button>(R.id.generateNew)
//        generateElement.setOnClickListener { mainInterface.showImg2Img() }
//
//        val useAsInitElement = view.findViewById<Button>(R.id.useAsInit)
//        useAsInitElement.setOnClickListener { mainInterface.setImage(bitmap, fileName) }
//
//        val saveElement = view.findViewById<Button>(R.id.save)
//        saveElement.setOnClickListener { saveImage() }
//    }

//    private fun regenerateImage() {
//        val generationCoroutine = CoroutineScope(Dispatchers.IO).launch {
//            mainInterface.generateImage(request, prompt)
//        }
//        mainInterface.setGenerationCoroutine(generationCoroutine)
//    }

    private fun saveImage() {
        val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDirectory, fileName)
        try {
            FileOutputStream(imageFile).use { outputStream ->
                outputStream.write(imageData)
                outputStream.flush()
                MediaScannerConnection.scanFile(context, arrayOf(imageFile.absolutePath), null, null)
                val savedElement = view.findViewById<TextView>(R.id.saved)
                savedElement.text = "Image Saved"
            }
        } catch (e: IOException) {
            mainInterface.displayError(e.toString())
        }
    }
}