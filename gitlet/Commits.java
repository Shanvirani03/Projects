package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Commits class for Gitlet, the tiny stupid version-control system.
 *  @author Shan Ali Virani
 */

public class Commits implements Serializable {

    /**
     * Message provided by the user for the commit argument. */
    private String _message;

    /** Timestamp which is attached to the Commit object. */
    private String _timestamp;

    /** Parent commit. */
    private String _parent;

    /** HashMap used to track the files of a commit. */
    private HashMap<String, Blobs> _trackedFiles;

    /** Unique identifier for commit objects. */
    private String _commitID;

    /** Constructor used in order to create the commit object.
     * @param message This is the commit message by the user.
     * @param parent This is the parent of the Commit.
     * @param timestamp This is the timestamp for when the commit
     *                  was made
     * @param trackedFiles This is the files that the commit
     *                     is tracking. */
    public Commits(String message, String parent, String timestamp,
                   HashMap<String, Blobs> trackedFiles) {
        this._message = message;
        this._parent = parent;
        this._timestamp = timestamp;
        this._trackedFiles = trackedFiles;
    }

    /** Returns the message of the commit. */
    public String getMessage() {
        return _message;
    }

    /** Returns the timestamp of the commit. */
    public String getTimeStamp() {
        return _timestamp;
    }

    /** Returns the commitID of the commit object. */
    public String getCommitID() {
        return _commitID;
    }

    /** Sets the commitID to the commit. */
    public void setID() {
        _commitID = Utils.sha1(_message, _timestamp);
    }

    /** Returns the trackedfiles which a commit is tracking. */
    public HashMap<String, Blobs> getTrackFiles() {
        return _trackedFiles;
    }
}
