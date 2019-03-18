package hu.robnn.auth

import hu.robnn.auth.dao.model.dto.UserDTO
import hu.robnn.auth.enums.UserRole
import hu.robnn.auth.exception.UserException
import hu.robnn.auth.mapper.UserMapper
import hu.robnn.auth.mock.ApplicationContextMock
import hu.robnn.auth.mock.TokenDaoMock
import hu.robnn.auth.mock.UserDaoMock
import hu.robnn.auth.service.UserService
import org.junit.After
import org.junit.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.assertEquals

class UserServiceTest {


    private val userDaoMock = UserDaoMock()
    private val tokenDaoMock = TokenDaoMock()
    private val passwordEncoder = BCryptPasswordEncoder()
    private val userMapper = UserMapper()
    private val applicationContextMock = ApplicationContextMock()
    private val userService = UserService(userDaoMock, tokenDaoMock, passwordEncoder, userMapper, applicationContextMock)

    /**
     * Clears the dao mocks after each tests
     */
    @After
    fun clearDaos() {
        userDaoMock.reset()
        tokenDaoMock.reset()
    }

    /**
     * Simple registration test
     */
    @Test
    fun testUserCreation() {
        userService.registerUser(buildUserDto())

        assertEquals(1, userDaoMock.count())
    }

    /**
     * The same email address is used to register, expected exception
     */
    @Test(expected = UserException::class)
    fun testUserCreationUsedEmail() {
        userService.registerUser(buildUserDto().apply { username = "test1" })
        userService.registerUser(buildUserDto().apply { username = "test2" })
    }

    /**
     * The same username is used to register, expected exception
     */
    @Test(expected = UserException::class)
    fun testUserCreationUsedName() {
        userService.registerUser(buildUserDto().apply { emailAddress = "test1@test.hu" })
        userService.registerUser(buildUserDto().apply { emailAddress = "test2@test.hu" })
    }

    /**
     * Simple login test, the generated token must not be empty
     */
    @Test
    fun testLogin() {
        userService.registerUser(buildUserDto())
        val token = userService.login(buildUserDto())
        assert(token.isNotEmpty())
    }

    /**
     * Login with not registered username, expected exception
     */
    @Test(expected = UserException::class)
    fun testLoginWrongUserName() {
        userService.registerUser(buildUserDto())
        userService.login(buildUserDto().apply { username = "test1" })
    }

    /**
     * Login with wrong password, expected exception
     */
    @Test(expected = UserException::class)
    fun testLoginWrongPassword() {
        userService.registerUser(buildUserDto())
        userService.login(buildUserDto().apply { password = "test1" })
    }

    /**
     * Checks if login is repeated, the token must be equal
     */
    @Test
    fun testLoginDouble() {
        userService.registerUser(buildUserDto())
        val token1 = userService.login(buildUserDto())
        val token2 = userService.login(buildUserDto())
        assertEquals(token1, token2)
    }

    /**
     * Simple authentication test
     */
    @Test
    fun testAuthenticate() {
        userService.registerUser(buildUserDto())
        val token = userService.login(buildUserDto())
        userService.authenticate(token, UserRole.USER)
    }

    /**
     * Simple authentication test with higher role needed then the user's
     */
    @Test(expected = UserException::class)
    fun testAuthenticateWrongRole() {
        userService.registerUser(buildUserDto())
        val token = userService.login(buildUserDto())
        userService.authenticate(token, UserRole.ADMIN)
    }

    /**
     * Simple authentication test with wrong token
     */
    @Test(expected = UserException::class)
    fun testAuthenticateWrongToken() {
        userService.registerUser(buildUserDto())
        userService.login(buildUserDto())
        userService.authenticate("test_token", UserRole.USER)
    }

    /**
     * Test for getting user by token
     */
    @Test
    fun getUserForTokenTest() {
        userService.registerUser(buildUserDto())
        val token = userService.login(buildUserDto())
        val userForToken = userService.getUserForToken(token)
        assert(userForToken.isPresent)
        assertEquals(buildUserDto().emailAddress, userForToken.get().emailAddress)
        assertEquals(buildUserDto().role, userForToken.get().role)
        assertEquals(buildUserDto().username, userForToken.get().username)
        assertEquals(buildUserDto().realName, userForToken.get().realName)
    }


    private fun buildUserDto(): UserDTO {
        val target = UserDTO()
        target.emailAddress = "test@test.hu"
        target.password = "test"
        target.role = "USER"
        target.username = "test"
        target.realName = "Teszt Elek"
        return target
    }
}