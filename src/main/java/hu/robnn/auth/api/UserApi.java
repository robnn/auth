package hu.robnn.auth.api;

import hu.robnn.auth.annotation.Authenticated;
import hu.robnn.auth.dao.model.dto.Token;
import hu.robnn.auth.dao.model.dto.UserDTO;
import hu.robnn.auth.exception.UserError;
import hu.robnn.auth.exception.UserException;
import hu.robnn.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Component
@RestController("/users")
@CrossOrigin
public class UserApi {

    private final UserService userService;

    @Autowired
    public UserApi(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "users", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO user) {
        UserDTO created = userService.registerUser(user);
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

}
