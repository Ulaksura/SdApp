package com.example.sdapp.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sdapp.DB.AppDatabase
import com.example.sdapp.R
import com.example.sdapp.SharedGalleryViewModel
import com.example.sdapp.authUser
import com.example.sdapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {
    public lateinit var apikeyElement: EditText
    private var apikeyHidden: Boolean = true


    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textUserName:TextView = view.findViewById(R.id.textUserName)
        var name:String=""
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val userDao = db.userDao()

            name = userDao.getUserById(authUser.idAuthUser)!!.login
            withContext(Dispatchers.Main) {
                textUserName.text = name
            }
        }

        val apiKeyUser: EditText = view.findViewById(R.id.editTextApiKey)
        val saveButton: Button = view.findViewById(R.id.saveButton)
        val linkToGetApi:Button = view.findViewById(R.id.apiButton)
        //apiKeyUser.text = authUser.getUserAPI()
        //короче с этой апишкой, надо будет с основного проекта кусок спизидть
        apiKeyUser.setText(authUser.getUserAPI())

        linkToGetApi.setOnClickListener {
            val url = "https://stablehorde.net/register"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        saveButton.setOnClickListener{
            authUser.setUserAPI(apiKeyUser.text.toString().trim())
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(requireContext())
                val userDao = db.userDao()
                userDao.updateUser(authUser.user!!)
            }
            Toast.makeText(requireContext(), "Изменения примененры!", Toast.LENGTH_LONG).show()
        }

        val exitButton: Button = view.findViewById(R.id.logoutButton)
        exitButton.setOnClickListener{
            Toast.makeText(
                requireContext(),
                "Пользователь ${authUser.user?.login} вышел",
                Toast.LENGTH_LONG)
                .show()
            SharedGalleryViewModel().exitUserGallery(requireContext(), authUser.idAuthUser)
            authUser.authUserExit()
            findNavController().navigate(R.id.navigation_auth)

        }

//        apikeyElement = view.findViewById(R.id.editTextApiKey)
//        apikeyElement.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                saveApikey(s.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {}
//        })
//        val localPreferences = getDefaultSharedPreferences(requireContext())
//        val apikey = localPreferences.getString("apikey", null)
//        if(apikey != null){
//            apikeyElement.setText(apikey)
//            authUser.setUserAPI(apikey)
//        }
    }
    private fun changeApikeyVisibility() {
        apikeyHidden = !apikeyHidden
        if(apikeyHidden) apikeyElement.transformationMethod = HideReturnsTransformationMethod.getInstance()
        else apikeyElement.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun saveApikey(apikey: String) {
        val localPreferences = getDefaultSharedPreferences(requireContext())
        val preferenceWriter = localPreferences.edit()
        preferenceWriter.putString("apikey", apikey)
        preferenceWriter.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}