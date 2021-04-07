package MyUtility.Tomcat;

import MyUtility.Tomcat.Exception.UploadWarningException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class RequestHolder{
    /* version 2020/08/11
      Transfer request to map according to type of request.
      If request is multipart/form-data, the upload file would be assign to fileItem(if existed).
      *Only support one file upload

      field: requestMap, fileItem, requestArrayMap
      method: saveFile, getRequestMap, getRequestArrayMap, getRequestBody, getUploadFullFileName, getUploadFileName, getUploadFileNameExtension
    * */
    private Map<String, String> requestMap;
    private Map<String, String[]> requestArrayMap;
    private List<FileItem> fileItemList;
    private List<String> fileFieldNameList;
    private HttpServletRequest request;

    public static String  FILE_NAME_WITH_EXTENSION = "FILE_NAME_WITH_EXTENSION";
    public static String  FILE_EXTENSION = "FILE_EXTENSION";
    public static String  SAVE_FOLDER_PATH = "SAVE_FOLDER_PATH";
    public static String  FILE_ABSOLUTE_PATH = "FILE_ABSOLUTE_PATH";

    public RequestHolder(HttpServletRequest request) throws IOException, FileUploadException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        this.requestMap = new HashMap();
        this.requestArrayMap = new HashMap();
        this.request = request;
        this.fileItemList = new ArrayList<>();
        this.fileFieldNameList = new ArrayList<>();
        if("GET".equalsIgnoreCase(request.getMethod()) || "POST".equalsIgnoreCase(request.getMethod())){
            if(isMultipart){
                // Create a factory for disk-based file items
                FileItemFactory factory = new DiskFileItemFactory();

                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                List<FileItem> items = upload.parseRequest(request);
                Iterator<FileItem> iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    if(!item.isFormField()) {
                        //file slot
                        String originalFileName = item.getName();
                        if(originalFileName.equals("")){
                            //No upload file
                            continue;
                        }else{
                            this.fileItemList.add(item);
                            this.fileFieldNameList.add(item.getFieldName());
                        }
                    }else{
                        //not file slot, get form data
                        String fieldName = item.getFieldName();
                        String fieldValue = null;
                        try {
                            fieldValue = item.getString("UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            //not possible
                            throw new RuntimeException(e);
                        }
                        this.requestMap.put(fieldName, fieldValue);
                    }
                }

            }else{
                for(Object o: request.getParameterMap().entrySet()){
                    Map.Entry<String, String[]> entry = (Map.Entry) o;
                    //request.getParameterMap() will return Map<String, String[]>
                    if(entry.getValue().length == 1){
                        this.requestMap.put(entry.getKey(), entry.getValue()[0]);
                    }
                    this.requestArrayMap.put(entry.getKey(), entry.getValue());
                }
            }
        }else{
            String[] pairs = this.getRequestBody().split("&");
            Map<String, List<String>> tempMap = new HashMap<String, List<String>>();
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                List<String> tempList =  tempMap.getOrDefault(java.net.URLDecoder.decode(pair.substring(0, idx), "UTF-8"), new ArrayList<String>());
                tempList.add(java.net.URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                tempMap.put(java.net.URLDecoder.decode(pair.substring(0, idx), "UTF-8"), tempList);
            }
            for(Map.Entry<String, List<String>> entry: tempMap.entrySet()){
                this.requestArrayMap.put(entry.getKey(), entry.getValue().toArray(new String[0]));
                if(entry.getValue().size() == 1){
                    this.requestMap.put(entry.getKey(), entry.getValue().get(0));
                }
            }
        }

    }
    public Map<String, String> getRequestMap(){
        return this.requestMap;
    }
    public Map<String, String[]> getRequestArrayMap(){
        return this.requestArrayMap;
    }
    public String getRequestBody() throws java.io.IOException{
        return java.net.URLDecoder.decode(
                this.request.getReader().lines().collect(
                        java.util.stream.Collectors.joining(
                                System.lineSeparator()
                        )
                ), "UTF-8"
        );
    }
    public FileSaver getFileSaver(){
        return new FileSaver();
    }
    public FileSaver getFileSaver(String fileFieldName){
        return new FileSaver(fileFieldName);
    }
    public class FileSaver {
        boolean essential = false;
        String folderPath = "", newFileNameWithoutExtension = null;
        String fileName = null, fileExtension = null, fileNameWithoutExtension = null;
        long kbSizeLimit = -1;
        private String[] availableFileExtends;
        private FileItem currentFileItem;
        private int currentFileIndex;
        private boolean replaceIfExist = false;

        GeneralFileSaveErrorCallback noFileError = null;
        SizeLimitErrorCallback sizeLimitError = null;
        FileTypeErrorCallback fileTypeError = null;

        private String getOriginalFileNameWithExtension(){
            if(fileName == null){
                //In ie, fileItem.getName() would give full path containing file name, but chrome would only give file name.
                fileName = RequestHolder.this.fileItemList.get(currentFileIndex).getName().substring(
                        RequestHolder.this.fileItemList.get(currentFileIndex).getName().lastIndexOf("\\") + 1);
            }
            return fileName;
        }

        private String getOriginalFileNameWithoutExtension(){
            if(this.fileNameWithoutExtension == null){
                if(this.getOriginalFileNameWithExtension().contains(".")){
                    this.fileNameWithoutExtension = this.getOriginalFileNameWithExtension().substring(0, this.getOriginalFileNameWithExtension().lastIndexOf("."));
                }else{
                    this.fileNameWithoutExtension = this.getOriginalFileNameWithExtension();
                }
            }
            return  fileNameWithoutExtension;
        }
        private String getOriginalFileExtension(){
            if(fileExtension == null){
                String originalFileName = this.getOriginalFileNameWithExtension();
                if(originalFileName.contains(".")){
                   fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")+1).toLowerCase();
                }else{
                    fileExtension = "";
                }
            }
            return fileExtension;
        }
        public FileSaver(String fileFieldName) {
            this.currentFileIndex = -1;
            if(fileFieldName == null){
                try {
                    this.currentFileIndex = 0;
                    this.currentFileItem = RequestHolder.this.fileItemList.get(this.currentFileIndex);
                }catch (IndexOutOfBoundsException ex){
                    currentFileItem = null;
                }
            }else{
                try{
                    this.currentFileIndex = RequestHolder.this.fileFieldNameList.indexOf(fileFieldName);
                }catch (NullPointerException ex){
                    currentFileItem = null;
                    return;
                }
                this.currentFileItem = RequestHolder.this.fileItemList.get(this.currentFileIndex);
            }
        }

        public FileSaver() {
            this(null);
        }

        public FileSaver setSaveFolder(String folderPath){
            if(!folderPath.endsWith("\\")){
                folderPath += "\\";
            }
            this.folderPath = folderPath;
            return this;
        }

        /***
         * Replace file name.
         * @param newFileNameWithoutExtension
         * @return Instance of FileSaver
         */
        public FileSaver setNewFileNameWithoutExtension(String newFileNameWithoutExtension){
            this.newFileNameWithoutExtension = newFileNameWithoutExtension;
            return this;
        }

        /**
         * @param essential Default false
         * @return Instance of FileSaver
         */
        public FileSaver setEssential(boolean essential){
            this.essential = essential;
            return this;
        }

        /**
         * @param essential Default false
         * @param noFileErrorCallback
         * @return
         */
        public FileSaver setEssential(boolean essential, GeneralFileSaveErrorCallback noFileErrorCallback){
            this.noFileError = noFileErrorCallback;
            return setEssential(essential);
        }

        public FileSaver setKbSizeLimit(long kbSizeLimit) {
            this.kbSizeLimit = kbSizeLimit;
            return this;
        }

        public FileSaver setKbSizeLimit(long kbSizeLimit, SizeLimitErrorCallback sizeLimitErrorCallback){
            this.sizeLimitError = sizeLimitErrorCallback;
            return setKbSizeLimit(kbSizeLimit);
        }

        public FileSaver setAvailableFileExtends(String[] fileExtends){
            this.availableFileExtends = fileExtends;
            return this;
        }
        public FileSaver setAvailableFileExtends(String[] fileExtends, FileTypeErrorCallback fileTypeErrorCallback){
            this.fileTypeError = fileTypeErrorCallback;
            return setAvailableFileExtends(fileExtends);
        }

        /**
         * @param replaceIfExist Default false
         * @return Instance of FileSaver
         */
        public FileSaver replaceIfExist(boolean replaceIfExist){
            this.replaceIfExist = replaceIfExist;
            return this;
        }

        public File save() throws UploadWarningException {
            File file = null;

            if(this.essential && this.currentFileItem == null){
                //check essential
                if(noFileError != null){
                    noFileError.callback();
                }
                throw new UploadWarningException("No upload file");
            }else if(this.currentFileItem != null){
                String fileName = getOriginalFileNameWithoutExtension();

                //check file size
                if(this.kbSizeLimit != -1){
                    if(this.currentFileItem.getSize() > this.kbSizeLimit * 1024){
                        if(sizeLimitError != null){
                            sizeLimitError.callback(this.kbSizeLimit);
                        }
                        throw new UploadWarningException("File size can not be large than " + (this.kbSizeLimit / 1024) + " megabytes");
                    }
                }

                //set new file name exclude extend
                if(this.newFileNameWithoutExtension != null){
                    fileName = this.newFileNameWithoutExtension;
                }

                //check file type
                if(availableFileExtends != null){
                    if("".equals(getOriginalFileExtension()) || Arrays.stream(this.availableFileExtends).noneMatch(s -> getOriginalFileExtension().equalsIgnoreCase(s))){
                        if(fileTypeError != null){
                            fileTypeError.callback(this.availableFileExtends);
                        }
                        throw new UploadWarningException("IncorrectFileTypeError: File type is not available");
                    }
                }

                //start to upload
                file = new File(this.folderPath + fileName + "." + getOriginalFileExtension());
                if(!replaceIfExist){
                    int count = 0;
                    String tempFileName = fileName;
                    while(true){
                        if(file.exists()){
                            count++;
                            fileName = String.format("%s (%d)", tempFileName, count);
                        }else{
                            break;
                        }
                        file = new File(this.folderPath + fileName + "." + getOriginalFileExtension());
                    }
                }else{
                    if(file.exists()){
                        file.delete();
                    }
                }
                try {
                    this.currentFileItem.write(file);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return file;
        }

    }
    public interface GeneralFileSaveErrorCallback {
        void callback();
    }
    public interface SizeLimitErrorCallback {
        void callback(long sizeLimit);
    }
    public interface FileTypeErrorCallback {
        void callback(String[] availableFileType);
    }
}
