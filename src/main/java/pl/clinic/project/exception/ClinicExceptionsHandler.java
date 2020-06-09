package pl.clinic.project.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ClinicExceptionsHandler {

    @ResponseBody
    @ExceptionHandler(value = AlreadyExistsException.class)
    public String handleAlreadyExists(AlreadyExistsException ex) {
        return "Niepoprawne dane: " +ex.getMessage();
    }

}
