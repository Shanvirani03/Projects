package gitlet;

import java.io.Serializable;

/** Blobs class for Gitlet, the tiny stupid version-control system.
 *  @author Shan Ali Virani
 */

public class Blobs implements Serializable {

    /** This is the fileName for which the blobs
     * resemble.
     */
    private String _fileName;

    /** This is the contents of the file. */
    private String _content;

    /** This is the ID of the blob. */
    private String identifierOfBlob;

    /** This is the constructor for how the blob is
     * created.
     * @param fileName Name of the file.
     * @param content Contents of the file.
     * @param id ID of the blob.
     */
    Blobs(String fileName, String content, String id) {
        this._fileName = fileName;
        this._content = content;
        this.identifierOfBlob = Utils.sha1("Blobs", fileName, content);
    }

    /** Returns the name of the file. */
    public String getFileName() {
        return _fileName;
    }

    /** Returns the contents of the file. */
    public String getContent() {
        return _content;
    }

    /** Returns the ID of the file. */
    public String getID() {
        return identifierOfBlob;
    }

}
