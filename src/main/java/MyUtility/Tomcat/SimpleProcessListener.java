package MyUtility.Tomcat;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Closeable;
public class SimpleProcessListener implements Closeable {
    private final String className;
    private final HttpSession httpSession;
    float end, value = 0;

    public SimpleProcessListener(Object jspClass, HttpSession httpSession, int end){
        this.end = end;
        this.className = jspClass.getClass().getSimpleName();
        this.httpSession = httpSession;
        if(httpSession != null){
            httpSession.setAttribute(className, this);
        }else{
            throw new RuntimeException("Session is invalid");
        }
    }

    public SimpleProcessListener(Object object, HttpSession httpSession){
        this(object, httpSession, 0);
    }


    public void setValue(int value){
        this.value = value;
    }

    public void setEnd(int end){
        this.end = end;
    }

    public void increase(int addValue){
        this.value += addValue;
    }

    /**
     * Add 1 to value
     */
    public void increase(){
        increase(1);
    }

    public int getPercent(){
        if(end == 0f){
            return 0;
        }
        return (int) Math.ceil((value / end) * 100);
    }

    @Override
    public void close() {
        if(this.httpSession != null){
            httpSession.removeAttribute(className);
        }
    }
    public static void getProcessProgress(HttpServletResponse response, HttpServletRequest request, HttpSession session, java.io.PrintWriter out){
        String processName = request.getParameter("processName");

        if(processName == null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        SimpleProcessListener processListener = (SimpleProcessListener)session.getAttribute(processName);
        if(processListener == null){
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        out.print(processListener.getPercent());
    }
}