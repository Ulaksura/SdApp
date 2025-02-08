package com.example.sdapp.start

import com.example.sdapp.DB.User

class AuthUser(
    var isAuthUser: Boolean = false,
    var idAuthUser:Int = 0,
    var user: User?,
    var UserAPI: String = ""){

    @JvmName("usApiGetter")
    fun getUserAPI():String{
        if(UserAPI == ""){
            UserAPI = user?.apiKey.toString()
        }
        return if(UserAPI == null) "" else UserAPI
    }
    @JvmName("usApiSetter")
    fun setUserAPI(api:String){
        UserAPI = api
        user?.apiKey = api
        //потом ещё юзера в бд перезаписать надо как-то..
    }

    fun authUserExit(){
        isAuthUser = false
        idAuthUser = 0
        user = null
        UserAPI = ""
    }
}