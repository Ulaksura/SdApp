package com.example.sdapp.start

import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.text.Editable
import android.text.TextWatcher
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
import com.example.sdapp.security.generateHash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        val userLogin: EditText = view.findViewById(R.id.user_login_auth)
        val userPassword: EditText = view.findViewById(R.id.user_pass_auth)
        val linkToReg: TextView = view.findViewById(R.id.link_to_reg)

        //userLogin.setText("Uwwu")
        //userPassword.setText("Uwwu123@")
        linkToReg.setOnClickListener {
            navController.navigate(R.id.navigation_reg)
        }

        userLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                saveLogin(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        val localPreferences = getDefaultSharedPreferences(requireContext())
        val loginS = localPreferences.getString("login", null)
        if(loginS != null){
            userLogin.setText(loginS)
        }

        val auth: Button = view.findViewById(R.id.button_auth)
        auth.setOnClickListener {

            val login = userLogin.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (login == "" || password == "")
                Toast.makeText(requireContext(), "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(requireContext())
                    val userDao = db.userDao()


                    val isAuth = userDao.getUserByLogin(login)
                    var checkUser = false
                    if(isAuth!=null) {
                        checkUser = isAuth.password == generateHash(password, isAuth.salt)
                    }
                    if(checkUser) {
                        authUser.isAuthUser = true
                        authUser.idAuthUser = isAuth!!.id
                        authUser.user = isAuth
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Пользователь $login авторизован",
                                Toast.LENGTH_LONG
                            )
                                .show()
                          //  userLogin.text.clear()
                            userPassword.text.clear()
                            SharedGalleryViewModel().loadAllImagesFromDatabase(requireContext(),
                                authUser.idAuthUser)
                            navController.navigate(R.id.navigation_img2img)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Неправильный логин или пароль",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            userPassword.text.clear()
                        }
                    }
                }
            }
        }
    }
    private fun saveLogin(login: String) {
        val localPreferences = getDefaultSharedPreferences(requireContext())
        val preferenceWriter = localPreferences.edit()
        preferenceWriter.putString("login", login)
        preferenceWriter.apply()
    }
}
