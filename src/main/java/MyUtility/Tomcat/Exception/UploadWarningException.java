package MyUtility.Tomcat.Exception;

public class UploadWarningException extends Exception{
    public String message;
    public UploadWarningException(String message){
        this.message = message.substring(message.lastIndexOf(":") + 1);
    }

    @Override
    public String getMessage(){
        return this.message;
    }
}
