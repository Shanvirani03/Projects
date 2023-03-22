package gitlet;

import java.io.Serializable;
import java.util.LinkedList;

/** Branch class for Gitlet, the tiny stupid version-control system.
 *  @author Shan Ali Virani
 */

public class Branch implements Serializable {

    /** This is the name of the branch.
     * */
    private String _name;

    /** This is the branch ID. */
    private String _ID;

    /** This is the history of the branches.
     */
    private LinkedList<String> _history;

    /** This is the constructor used to create
     * branch class.
     * @param name Name of the branch.
     * @param id Unique ID of the branch.
     * @param history History of the branch.
     * @param isActiveBranch Tells whether this is
     *                       an active branch or not.
     */
    public Branch(String name, String id, LinkedList<String> history,
                  boolean isActiveBranch) {
        this._name = name;
        this._ID = id;
        this._history = history;
    }

    /** Returns the name of the branch. */
    public String getName() {
        return _name;
    }

    /** Returns the ID of the branch. */
    public String getID() {
        return _ID;
    }

    /** Returns the history of the branch. */
    public LinkedList<String> getHistory() {
        return _history;
    }

    /** Updates the history of the branch.
     * @param newHistory This is the new history of the
     *                   branch
     * */
    public void updateHistory(LinkedList<String> newHistory) {
        _history = newHistory;
    }

    /** Sets the ID of the branch. */
    public void setID(String id) {
        _ID = id;
    }
}
