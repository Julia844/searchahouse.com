package edu.searchahouse.leadrouter.endpoints.aop;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import edu.searchahouse.leadrouter.exceptions.LeadRouterException;

//@formatter:off
/**
 * 
 * Advice class that check if a rest endpoint throws an exception and change the response code and message according.
 * 
 * 
 * 1XX: Information
 * 
 * 2XX: Success
 * 	200 - OK:
 * 			-> Everything worked
 *  201 - Created (mostly use for POST):
 *  		-> The server has successfully created a new resource
 *  		-> Newly created resource's location returned in the Location header
 *  202 - Accepted:
 *  		-> The server has accepted the request, but it is not yet complete
 *  		-> A location to determine the request's current status can be returned in the location header
 * 3XX: Redirection
 * 
 * 4XX: Client Error
 * 	400 - Bad Request:
 * 			-> Malformed syntax
 * 			-> Should not be repeated without modification
 *  401 - Unauthorized:
 *  		-> Authentication is required
 *  		-> Includes a WWW-Authentication header
 *  403 - Forbidden:
 *  		-> Server has understood but refused to honor the request
 *  		-> Should not be repeated without modification
 *  404 - Not Found:
 *  		-> The server cannot find a resource matching a URI
 *  406 - Not Acceptable:
 *  		-> The server can only return response entities that do not match the client's Accept header
 *  409 - Conflict:
 *  		-> The resource is in a state that is in conflict with the request
 *  		-> Client should attempt to rectify the conflict and then resubmit the request
 *  422 - Unprocessable Entity:
 *          -> The resource already exist.
 *   
 * 5XX: Server Error (if the app is responding a 500 code it means we have a bug or didn't thought all possible scenarios.)
 * 
 * 
 * @author Gustavo Orsi
 *
 */
// @formatter:on
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * 
     * Catch <code>IllegalArgumentException</code> exception thrown by any endpoint and change the return code.
     * 
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(LeadRouterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors leadRouterExceptionHandler(LeadRouterException ex) {
        return new VndErrors(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }

    /**
     * 
     * 
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    VndErrors internalServerErrorHandler(Exception ex) {
        return new VndErrors(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getLocalizedMessage());
    }

}
