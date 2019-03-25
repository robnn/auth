package hu.robnn.auth.social.facebook

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
open class FacebookService(private val userService: UserService,
                           private val userDao: UserDao,
                           private val userMapper: UserMapper,
                           private val facebookMapper: FacebookMapper) {

    private val okHttpClient = OkHttpClient()
    private val baseUrl = "https://graph.facebook.com/me"

    @Transactional
    open fun loginWithFacebookUser(accessToken: AccessToken): Token {
        val request = Request.Builder()
                .url("$baseUrl?access_token=${accessToken.token}&fields=name%2Cemail%2Cpicture%2Cfirst_name%2Clast_name&method=get" +
                        "&pretty=0&sdk=joey&suppress_http_code=1").build()
        try {
            val response = okHttpClient.newCall(request).execute()
            val gson = Gson()
            val facebookUser = gson.fromJson(response.body()?.string(), FacebookUser::class.java)
            return handleFacebookUser(facebookUser)
        } catch (e: Exception) {
            throw UserException(UserError.ERROR_DURING_FACEBOOK_SYNC)
        }
    }

    private fun handleFacebookUser(facebookUser: FacebookUser): Token {
        val userInDb = userDao.findByEmailAddress(facebookUser.email)
        return if(userInDb.isPresent) {
            val tokenString = userService.loginWithoutPassword(userMapper.map(userInDb.get()))
            Token(tokenString)
        } else {
            val registeredUser: UserDTO = registerFacebookUser(facebookUser)
            Token(userService.loginWithoutPassword(registeredUser))
        }
    }

    private fun registerFacebookUser(facebookUser: FacebookUser): UserDTO {
        val userDTO = facebookMapper.mapToUserDTO(facebookUser)
        return userService.registerUser(userDTO, true)
    }
}