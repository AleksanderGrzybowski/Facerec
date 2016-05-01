package facerec;

import java.io.File;

public class Config {
    public static final String CORE_DIR = "core";
    public static final String REGOGNIZE_BINARY_PATH = "./recognize";
    public static final String EXTRACT_FACE_BINARY_PATH = "./extract_face";
    
    public static final String CERT_PATH = "ssl.p12";
    public static final String PUBLIC_HTML = System.getProperty("user.dir") + File.separator + "public";
}
