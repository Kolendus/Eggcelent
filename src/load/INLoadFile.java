package load;

import model.Graph;

public interface INLoadFile {
    public Graph ScanFile(String path) throws Exception;
}
