package com.example.sdapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sdapp.R
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class GenerationFragment : Fragment() {
    private val internet: NetworkManager = NetworkManager()
    //private lateinit var view: View
    private lateinit var mainInterface: MainInterface
    private lateinit var timerElement: TextView
    public var id: String? = null
    private val apiUrl: String = "https://stablehorde.net/api/v2/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //view = inflater.inflate(R.layout.generation, container, false)
//        initialize()
        return inflater.inflate(R.layout.generation, container, false)//view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainInterface = activity as MainInterface

        timerElement = view.findViewById<TextView>(R.id.timeLeft)
        val navController = findNavController()

        val cancelElement = view.findViewById<Button>(R.id.cancel)
        cancelElement.setOnClickListener{
           // CoroutineScope(IO).launch { cancelGeneration() }
            cancelGeneration()
            navController.navigate(R.id.navigation_img2img)
        }
    }
//    private fun initialize() {
//        mainInterface = activity as MainInterface
//
//        timerElement = view.findViewById(R.id.timeLeft)
//
//        val cancelElement = view.findViewById<Button>(R.id.cancel)
//        cancelElement.setOnClickListener {CoroutineScope(IO).launch { cancelGeneration() }}
//    }

    // Cancel the remote generation request
    private fun cancelGeneration() {
        try {
            if(!internet.isConnected(requireContext())){
                val act = activity ?: return
                act.runOnUiThread {
//                    val controller = findNavController(R.id.nav_host_fragment_activity_main)
//                    controller.navigate(R.id.navigation_img2img)
                    mainInterface.displayError("An error occurred with your internet connection!")
                }
                return
            }
            if(id != null) {
                val client = OkHttpClient()
                val request: Request = Request.Builder().url(apiUrl + "generate/status/" + id).delete().build()
                client.newCall(request).execute()
            }
            mainInterface.onCancelGeneration()
        } catch (e: IOException) {
            val act = activity ?: return
            act.runOnUiThread {
                mainInterface.displayError(e.toString())
            }
        }
    }

    public fun displayWaitingTime(timeLeft: String = "wait a moment") {
        timerElement.text = "Wait a minute"//"Estimated wait time left: "+timeLeft+" s"
    }
}