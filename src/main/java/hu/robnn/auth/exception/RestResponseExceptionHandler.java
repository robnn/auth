package hu.robnn.auth.exception;

import hu.robnn.auth.dao.model.dto.Message;
import hu.robnn.auth.dao.model.dto.Severity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UserException.class})
    protected ResponseEntity<Message> handleUserError(RuntimeException ex, WebRequest request){
        Message messageObject = new Message(Severity.ERROR, ((UserException) ex).getErrorCause().name());
        return new ResponseEntity<>(messageObject, HttpStatus.BAD_REQUEST);
    }
}
