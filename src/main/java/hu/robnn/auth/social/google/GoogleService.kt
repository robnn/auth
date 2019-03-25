package hu.robnn.auth.social.google

import com.google.gson.Gson
import hu.robnn.auth.dao.UserDao
import hu.robnn.auth.dao.model.dto.Token
import hu.robnn.auth.dao.model.dto.UserDTO
import hu.robnn.auth.exception.UserError
import hu.robnn.auth.exception.UserException
import hu.robnn.auth.mapper.UserMapper
import hu.robnn.auth.service.UserService
import hu.robnn.auth.social.AccessToken
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class GoogleService(private val userService: UserService,
                         private val userDao: UserDao,
                         private val userMapper: UserMapper,
                         private val facebookMapper: GoogleMapper) {

    private val okHttpClient = OkHttpClient()
    private val baseUrl = "https://www.googleapis.com/oauth2/v1/userinfo"

    @Transactional
    open fun loginWithGoogleUser(accessToken: AccessToken): Token {
        val request = Request.Builder()
                .url("$baseUrl?access_token=${accessToken.token}&alt=json").build()
        try {
            val response = okHttpClient.newCall(request).execute()
            val gson = Gson()
            val googleUser = gson.fromJson(response.body()?.string(), GoogleUser::class.java)
            return handleGoogleUser(googleUser)
        } catch (e: Exception) {
            throw UserException(UserError.ERROR_DURING_GOOGLE_SYNC)
        }
    }

    private fun handleGoogleUser(googleUser: GoogleUser): Token {
        val userInDb = userDao.findByEmailAddress(googleUser.email)
        return if(userInDb.isPresent) {
            val tokenString = userService.loginWithoutPassword(userMapper.map(userInDb.get()))
            Token(tokenString)
        } else {
            val registeredUser: UserDTO = registerGoogleUser(googleUser)
            Token(userService.loginWithoutPassword(registeredUser))
        }
    }

    private fun registerGoogleUser(googleUser: GoogleUser): UserDTO {
        val userDTO = facebookMapper.mapToUserDTO(googleUser)
        return userService.registerUser(userDTO, true)
    }
}