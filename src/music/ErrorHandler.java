/*
 * Music Visualizations: http:/github.com/michaelbrooks/music-visualization
 * Copyright 2012, Michael Brooks. BSD License.
 */

package music;

import java.util.Date;
import java.util.Stack;

/**
 *
 * @author michael
 */
class ErrorHandler {

    private static Stack<Error> errorStack = new Stack<Error>();
    private static Stack<Error> warningStack = new Stack<Error>();
    
    public static void pushError(Exception e, String message, Date time) {
        pushError(new Error(e, message, time));
    }

    public static void pushWarning(Exception e, String message, Date time) {
        pushWarning(new Error(e, message, time));
    }

    public static void pushError(Exception e, String message) {
        pushError(new Error(e, message));
    }

    public static void pushWarning(Exception e, String message) {
        pushWarning(new Error(e, message));
    }

    private static void pushError(Error error)
    {
        errorStack.push(error);

        System.err.println(error.getTime() + " : " + error.getMessage());
        System.err.println(error.getException());
        
        exit();
    }

    private static void pushWarning(Error warning)
    {
        warningStack.push(warning);

        System.err.println(warning.getTime() + " : " + warning.getMessage());
        System.err.println(warning.getException());

    }

    private static void exit()
    {
        System.exit(1);
    }
}

class Error
{
    private Exception exception;
    private String message;
    private Date time;

    public Error(Exception e, String message, Date time)
    {
        this.exception = e;
        this.message = message;
        this.time = time;
    }

    public Error(Exception e, String message)
    {
        this.exception = e;
        this.message = message;
        this.time = new Date();
    }

    public Exception getException() { return exception; }
    public String getMessage() { return message; }
    public Date getTime() { return time; }
}
