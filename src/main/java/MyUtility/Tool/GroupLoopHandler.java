package MyUtility.Tool;

public abstract class GroupLoopHandler<T> {
    public static class GroupLoopHandlerException extends RuntimeException{
        public GroupLoopHandlerException(String message){
            super(message);
        }
    }

    private boolean firstGroup = true;
    private boolean executeCacheSource = false;
    private T source;
    private long manualCount = 0, autoCount = 0;
    public abstract boolean needNewGroup(T source);
    public abstract void initializeGroup(T source);
    public abstract void terminalGroup(T source);


    /**
     * Need put function catchSource into this function to active full process
     */
    public abstract void setCatchSourceInLoop();
    public abstract void process(T source);
    public void catchSource(T source){
        if(!executeCacheSource){
            executeCacheSource = true;
        }
        this.source = source;
        if (needNewGroup(this.source)) {
            if (!firstGroup) {
                terminalGroup(this.source);
            }
            initializeGroup(this.source);
            firstGroup = false;
        }
        process(this.source);
        autoCount++;
    }
    public void increaseCounter(){
        manualCount++;
    }
    public void always(){}
    public long start(){
        try{
            setCatchSourceInLoop();
            if(executeCacheSource){
                terminalGroup(this.source);
            }
            return manualCount == 0? autoCount: manualCount;
        }finally {
            always();
        }
    }
}
