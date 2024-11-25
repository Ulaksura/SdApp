package com.example.sdapp
//
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sdapp.databinding.ActivityMainBinding
import com.example.sdapp.ui.FragmentManager
import com.example.sdapp.ui.GenerationFragment
import com.example.sdapp.ui.MainInterface
import com.example.sdapp.ui.NetworkManager
import com.example.sdapp.ui.img2img.Img2ImgFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import kotlin.math.floor


class MainActivity : AppCompatActivity(), MainInterface, ViewTreeObserver.OnWindowFocusChangeListener {

    private lateinit var binding: ActivityMainBinding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_img2img, R.id.navigation_gallery, R.id.navigation_settings
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//    }


    private val img2img: Img2ImgFragment = Img2ImgFragment()
    private val internet: NetworkManager = NetworkManager()
    private val fragments: FragmentManager = FragmentManager()
    private lateinit var errorElement: TextView
    private lateinit var generationCoroutine: Job
    private var pickedImage: Bitmap? = null
    private var imageName: String = ""
    private var hasFocus: Boolean = true
    private val apiUrl: String = "https://stablehorde.net/api/v2/"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_img2img, R.id.navigation_gallery, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        initialize()
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
        initialize()
    }

    private fun initialize() {
        // Request the necessary permissions
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.POST_NOTIFICATIONS)
        for (permission in permissions) {
            requestPermission(permission)
        }
        //showImg2Img()
        errorElement = findViewById(R.id.error)
    }

    private fun requestPermission(permission: String) {
        val temp = ContextCompat.checkSelfPermission(this, permission)
        if(temp == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
        }
    }

    @SuppressLint("CommitTransaction")
//    override fun showImg2Img() {
//
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.container, img2img)
//        fragments.changeFragment(fragmentTransaction)
//        CoroutineScope(Dispatchers.IO).launch {
//            while(!fragments.changedFragment) { delay(10) }
//            runOnUiThread { img2img.imageNameElement.text = imageName }
//        }
//    }
    override fun showImg2Img() {
        CoroutineScope(Dispatchers.Main).launch {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_img2img)
        }
    }

    // Display generation info
    @SuppressLint("CommitTransaction")
    private fun showGeneration(generation: GenerationFragment) {
        CoroutineScope(Dispatchers.Main).launch {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_generate)
        }
    }
//    private fun showGeneration(generation: GenerationFragment) {
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.container, generation)
//        fragments.changeFragment(fragmentTransaction)
//    }

    // Display an image once it's generated
    @SuppressLint("CommitTransaction")
//    private fun showImage(imageData: ByteArray, seedUsed: String,
//                          request: Request, prompt: String){
//        val imageDisplay = ImageDisplayFragment()
//        imageDisplay.imageData = imageData
//        imageDisplay.seedUsed = seedUsed
//        imageDisplay.request = request
//        imageDisplay.prompt = prompt
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.container, imageDisplay)
//        fragments.changeFragment(fragmentTransaction)
//    }
    private fun showImage(imageData: ByteArray, seedUsed: String,
                          request: Request, prompt: String){
       // TODO("request передать как url, headers, post")
        CoroutineScope(Dispatchers.Main).launch {
        val bundle = Bundle().apply {
            putByteArray("imageData", imageData)
            putString("seedUsed", seedUsed)
            putString("prompt", prompt)
            putString("url", request.url.toString())
            putString("headers", request.headers.toString())
            putString("post", request.body.toString())
        }

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_imageDisplay, bundle)

        }
    }

    // The function that calls a post a request to AI horde
    override suspend fun generateImage(request: Request, prompt: String) {

        val client = OkHttpClient()
        var response: Any?
        try {
            if(!internet.isConnected(this)){
                runOnUiThread {
                    showImg2Img()
                    displayError("An error occurred with your internet connection!")
                }
                return
            }
            // Begin generating the image
            response = client.newCall(request).execute()
            response = response.body?.string()
            response = JSONObject(response)

            // If the response has a message that means that an error occurred
            if (response.has("message")) {
                val temp = response.get("message").toString()
                runOnUiThread {
                    displayError(temp)
                }
                return
            }

            // Show generation status
            val generation = GenerationFragment()
            //runOnUiThread {
            withContext(Dispatchers.Main) {
                showGeneration(generation)
            }
           // }

            val id = response.get("id").toString()
            generation.id  = id
            val headers = mapOf(
                "accept" to "application/json"
            )
            var newRequest = Request.Builder()
                .url(apiUrl + "generate/check/" + id)
                .headers(headers.toHeaders())
                .build()
            var done = false

            // Wait for generation to finish
            while (!done) {
                response = client.newCall(newRequest).execute()
                response = response.body?.string()
                response = JSONObject(response)
                done = response.get("done").toString().toBoolean()
                if(!internet.isConnected(this)){
                    delay(500)
                    continue
                }
            }
//            while (!done) {
//                if(!internet.isConnected(this)){
//                    delay(500)
//                    continue
//                }
//                response = client.newCall(newRequest).execute()
//                response = response.body?.string()
//                response = JSONObject(response)
//                done = response.get("done").toString().toBoolean()
//                val temp = response.get("wait_time").toString()
//                withContext(Dispatchers.Main) {
//                    generation.displayWaitingTime(temp)
//                }
//                delay(5000)
//            }

            // Get the image and parse it's data
            newRequest = Request.Builder()
                .url(apiUrl + "generate/status/" + id)
                .headers(headers.toHeaders())
                .build()
            response = client.newCall(newRequest).execute()
            response = response.body?.string()
            response = JSONObject(response)
            val imgUrl =
                response.getJSONArray("generations").
                getJSONObject(0).get("img").toString()
            val seedUsed =
                response.getJSONArray("generations").
                getJSONObject(0).get("seed").toString()
            val img = URL(imgUrl)
            var imageData = img.readBytes()

            // Send a notification and wait until app goes back to focus.
            if(!hasFocus) {
                val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "Channel Name"
                    val descriptionText = "Channel Description"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel("Open Stable Diffusion", name, importance).apply {
                        description = descriptionText
                    }
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }
                var notification = NotificationCompat.Builder(applicationContext, "Open Stable Diffusion")
                    .setSmallIcon(R.drawable.baseline_image_24)
                    .setContentTitle("Generation finished")
                    .setContentText("$prompt has finished generating")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(0, notification)
                while(!hasFocus) {
                    delay(500)
                }
            }
            withContext(Dispatchers.Main) {
                showImage(imageData, seedUsed, request, prompt)
            }
        } catch (e: IOException) {
            delay(10)
            runOnUiThread {
                displayError(e.toString())
                showImg2Img()
            }
        }
    }

    override fun onCancelGeneration() {
        generationCoroutine.cancel()
        showImg2Img()
    }

    override fun displayError(error: String) {
        errorElement.text = error
    }

    override fun setGenerationCoroutine(generationCoroutine: Job) {
        this.generationCoroutine = generationCoroutine
    }

    override fun setImage(newImage: Bitmap, imageName: String) {
        pickedImage = newImage
        this.imageName = imageName
        showImg2Img()
    }

    // Allows the user to upload an image
    override fun uploadImage() {
        val intext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intext,0)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val pickedPhoto = data.data
            if (pickedPhoto != null) {
                imageName = getFileName(pickedPhoto)
                img2img.imageNameElement.text = imageName
                pickedImage = MediaStore.Images.Media.getBitmap(this.contentResolver,pickedPhoto)
                pickedImage = resizeImage(pickedImage!!)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Resizes the image to be within 3072 by 3072 pixels
    private fun resizeImage(image: Bitmap) : Bitmap{
        var temp = image
        var width = image.width
        var height = image.height
        if(width > 3072){
            val aspectRatio = 3072.0/width
            width = 3072
            height = Math.max(1, floor(height * aspectRatio).toInt())
            temp = Bitmap.createScaledBitmap(temp, width, height, false)
        }
        if(height > 3072){
            val aspectRatio = 3072.0/height
            height = 3072
            width = Math.max(1, floor(width * aspectRatio).toInt())
            temp = Bitmap.createScaledBitmap(temp, width, height, false)
        }
        return temp
    }

    //  Returns the currently selected image as Base64 encoded string
    override fun getImage(): String? {
        val temp = pickedImage ?: return null
        val stream = ByteArrayOutputStream()
        temp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n","")
    }

    override fun onWindowFocusChanged(focused: Boolean) {
        hasFocus = focused
    }

    private fun getFileName(uri: Uri): String {
        val returnCursor = contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val filename = returnCursor.getString(nameIndex)
        returnCursor.close()
        return filename
    }
}
