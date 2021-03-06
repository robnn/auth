package hu.robnn.auth.api;

import hu.robnn.auth.annotation.Authenticated;
import hu.robnn.auth.dao.model.dto.Token;
import hu.robnn.auth.dao.model.dto.UserDTO;
import hu.robnn.auth.exception.UserError;
import hu.robnn.auth.exception.UserException;
import hu.robnn.auth.social.AccessToken;
import hu.robnn.auth.social.facebook.FacebookService;
import hu.robnn.auth.service.UserService;
import hu.robnn.auth.social.google.GoogleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Component
@RestController("/users")
@CrossOrigin
public class UserApi {

    private final UserService userService;
    private final FacebookService facebookService;
    private final GoogleService googleService;

    @Autowired
    public UserApi(UserService userService, FacebookService facebookService, GoogleService googleService) {
        this.userService = userService;
        this.facebookService = facebookService;
        this.googleService = googleService;
    }

    @RequestMapping(path = "users", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO user) {
        UserDTO created = userService.registerUser(user, false);
        if (created != null) {
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "users/login", method = RequestMethod.POST)
    public ResponseEntity<Token> login(@RequestBody UserDTO user) {
        String token = userService.login(user);
        if (token != null) {
            return new ResponseEntity<>(new Token(token), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Authenticated
    @RequestMapping(path = "users/byToken/{token}", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getUserForToken(@PathVariable() String token){
        Optional<UserDTO> userForToken = userService.getUserForToken(token);
        if(userForToken.isPresent()){
            return new ResponseEntity<>(userForToken.get(), HttpStatus.OK);
        } else {
            throw new UserException(UserError.INVALID_TOKEN);
        }
    }

    @Authenticated(acceptedRoles = { "ADMIN" })
    @RequestMapping(path = "users/addRolesToUser", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> addRolesToUser(@RequestParam String username, @RequestBody Set<String> newRoleCodes) {
        UserDTO modified = userService.addRolesToUser(username, newRoleCodes);
        return new ResponseEntity<>(modified, HttpStatus.OK);
    }

    @RequestMapping(path = "users/login/facebook", method = RequestMethod.POST)
    public ResponseEntity<Token> loginWithFacebook(@RequestBody AccessToken accessToken){
        Token token = facebookService.loginWithFacebookUser(accessToken);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(path = "users/login/google", method = RequestMethod.POST)
    public ResponseEntity<Token> loginWithGoogle(@RequestBody AccessToken accessToken){
        Token token = googleService.loginWithGoogleUser(accessToken);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

}
