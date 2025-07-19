package in.ecomexpress.sruti.model.starttrip;

import java.io.File;

/**
 * Created by 63091 on 15-10-2019.
 */

public class ReturnImage {
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private File file;
    private String filePath;
}
