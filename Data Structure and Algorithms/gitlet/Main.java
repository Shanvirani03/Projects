package gitlet;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Formatter;
import java.util.ArrayList;



/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Shan Ali Virani
 */
public class Main {

    /** The current working directory. */
    private static File cwd = new File(".");

    /** Folder which contains the files associated gitlet. */
    private static File gitletFolder = Utils.join(cwd, ".gitlet");

    /** Commit directory which stores the commits. */
    private static File commitDir = Utils.join(gitletFolder, "commits");

    /** The path which staged files will follow. */
    private static File stagingDir = Utils.join(gitletFolder,
            "staging");

    /** Path which added files join in the staging area. */
    private static File pathForAddition = Utils.join(stagingDir, "additions");

    /** Path that files for removal files join in the staging area. */
    private static File pathForRemoval = Utils.join(stagingDir, "removals");

    /** Path that commit objects follow. */
    private static File pathForCommit = Utils.join(commitDir, "commits");

    /** Path that the headBranch is stored. */
    private static File pathForHeadBranch = Utils.join(commitDir, "HEAD");

    /** Path which stores the history of commits. */
    private static File pathForCommitHistory = Utils.join(
            commitDir, "commitHistory");

    /** Path that stores all of the branches. */
    private static File pathForAllBranches = Utils.join(
            commitDir, "allBranches");

    /** This is the head for the branches. */
    private static String _headBranch;

    /** Stores all of the commits. */
    private static HashMap<String, Commits> _commitTree;

    /** Stores the branches. */
    private static HashMap<String, Branch> _branches;

    /** Stores the list of linked commit objects. */
    private static LinkedList _allCommitsHistory;

    /** Hashmap which stores the added files in the staging area. */
    private static HashMap<String, Blobs> addedFiles;

    /** Hashmap which stores the files for removal in the staging area. */
    private static HashMap<String, Blobs> removedFiles;

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        switch (args[0]) {
        case "find":
            find(args[1]);
            break;
        case "rm-branch":
            rmBranch(args[1]);
            break;
        case "branch":
            branch(args[1]);
            break;
        case "init":
            init();
            break;
        case "add":
            add(args[1]);
            break;
        case "log":
            log();
            break;
        case "rm":
            rm(args[1]);
            break;
        case "status":
            status();
            break;
        case "global-log":
            globalLog();
            break;
        default:
            extendedMain(args);
        }
    }

    /**
     * Extension of Main function.
     * Usage: java gitlet.Main ARGS, where ARGS contains
     *      *  <COMMAND> <OPERAND> .... */
    public static void extendedMain(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        switch (args[0]) {
        case "reset":
            reset(args[1]);
            break;
        case "commit":
            if (args[1].length() == 0) {
                System.out.println("Please enter a commit message.");
            }
            commit(args[1]);
            break;
        case "checkout":
            if (args.length == 3) {
                checkout1(args[2]);
                break;
            } else if (args.length == 4) {
                if (!args[2].equals("--")) {
                    System.out.println("Incorrect operands.");
                }
                checkout2(args[1], args[3]);
                break;
            } else {
                checkout3(args[1]);
                break;
            }
        default:
            System.out.println("No command with that name exists.");
        }
    }
    /**
     * Creates a new Gitlet version-control system in the
     * current directory. This system will automatically start
     * with one commit: a commit that contains no files
     * and has the commit message initial commit
     * (just like that, with no punctuation). It will have
     * a single branch: master, which initially points to this
     * initial commit, and master will be the current branch.
     * The timestamp for this initial commit will be
     * 00:00:00 UTC, Thursday, 1 January 1970 in whatever
     * format you choose for dates
     * (this is called "The (Unix) Epoch", represented internally
     * by the time 0.) Since the initial commit in
     * all repositories created by Gitlet will have exactly
     * the same content, it follows that all repositories
     * will automatically share this commit
     * (they will all have the same UID) and all commits in all repositories
     * will trace back to it.
     */
    @SuppressWarnings("unchecked")
    public static void init() {
        if (gitletFolder.exists()
                || commitDir.exists()
               || stagingDir.exists()) {
            System.out.println("A Gitlet version-control system "
                   + "already exists in the current directory");
        }
        gitletFolder.mkdirs();
        commitDir.mkdirs();
        stagingDir.mkdirs();
        removedFiles = new HashMap<String, Blobs>();
        addedFiles = new HashMap<String, Blobs>();
        HashMap<String, Blobs> zeroTrackedFiles = new HashMap<>();
        Commits firstCommit = new Commits("initial commit",
                null, makeUTCTime().toString(),
                zeroTrackedFiles);
        firstCommit.setID();
        _commitTree = new HashMap<>();
        _commitTree.put(firstCommit.getCommitID(), firstCommit);
        _allCommitsHistory = new LinkedList<>();
        _allCommitsHistory.add(firstCommit.getCommitID());
        File pathForCommitsH = Utils.join(commitDir, "_allCommitsHistory");
        Utils.writeObject(pathForCommitsH, _allCommitsHistory);
        _branches = new HashMap<>();
        LinkedList<String> branchHistory = new LinkedList<>();
        branchHistory.add(firstCommit.getCommitID());
        Branch masterBranch = new Branch("master",
                firstCommit.getCommitID(),
                branchHistory, true);
        _branches.put("master", masterBranch);
        _headBranch = "master";
        writeHelper();
    }

    /** Getter method to return Gitlet Folder. */
    public static File getGitletFolder() {
        return gitletFolder;
    }

    /** Getter method to return Commit Directory. */
    public static File getCommitDir() {
        return commitDir;
    }

    /** Getter method to return files to be added
     * in the staging area. */
    public static HashMap getAddedFiles() {
        return addedFiles;
    }

    /** Getter method to return files to be removed
     * in the staging area. */
    public static HashMap getRemovedFiles() {
        return removedFiles;
    }

    /** Clears all of the files in the staging area. */
    public static void clear() {
        addedFiles.clear();
        removedFiles.clear();
    }

    /** Used to set standard time.
     * @return standardizedComputerTime*/
    public static Timestamp makeUTCTime() {
        Date date = new Date();
        date.setTime(0);
        java.sql.Timestamp standardizedComputerTime = new
                java.sql.Timestamp(date.getTime());
        return standardizedComputerTime;
    }

    /** Used to create the current time.
     * @return Returns the current time */
    public static Timestamp makeTime() {
        Date date = new Date();
        java.sql.Timestamp currentTime = new java.sql.Timestamp(date.getTime());
        return currentTime;
    }

    /** Helper Method to writeObjects for certain
     * classes which require many writings. **/
    public static void writeHelper() {
        Utils.writeObject(pathForAllBranches, _branches);
        Utils.writeObject(pathForHeadBranch, _headBranch);
        Utils.writeObject(pathForCommitHistory, _allCommitsHistory);
        Utils.writeObject(pathForRemoval, removedFiles);
        Utils.writeObject(pathForCommit, _commitTree);
        Utils.writeObject(pathForAddition, addedFiles);
        Utils.writeObject(pathForAllBranches, _branches);
    }

    /** Helper Method to readObjects for certain classes
     * which require many readings. **/
    @SuppressWarnings("unchecked")
    public static void readHelper() {
        _commitTree = Utils.readObject(pathForCommit, HashMap.class);
        _headBranch = Utils.readObject(pathForHeadBranch, String.class);
        _allCommitsHistory = Utils.readObject(
                pathForCommitHistory, LinkedList.class);
        _branches = Utils.readObject(pathForAllBranches, HashMap.class);
        addedFiles = Utils.readObject(pathForAddition, HashMap.class);
        removedFiles = Utils.readObject(pathForRemoval, HashMap.class);
    }

    /**
     * Adds a copy of the file as it currently exists to the
     * staging area (see the description of the commit command).
     * For this reason, adding a file is also called staging the
     * file for addition. Staging an already-staged file
     * overwrites the previous entry in the staging area with
     * the new contents. The staging area should be somewhere
     * in .gitlet. If the current working version of the file
     * is identical to the version in the current commit, do not
     * stage it to be added, and remove it from the staging
     * area if it is already there (as can happen when a file is
     * changed, added, and then changed back). The file will no
     * longer be staged for removal (see gitlet rm), if it
     * was at the time of the command.
     * @param file this is the file name that
     *             must be added.
     */

    public static void add(String file) {
        readHelper();
        File addedFile = Utils.join(cwd, file);
        if (!addedFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        String contents = Utils.readContentsAsString(addedFile);
        String id = Utils.sha1(contents);
        Blobs blobForFile = new Blobs(file, contents, id);
        if (addedFiles.containsKey(file)) {
            addedFiles.remove(file);
            addedFiles.put(file, blobForFile);
        } else {
            addedFiles.put(file, blobForFile);
            Branch head = _branches.get(_headBranch);
            String headID = head.getID();
            Commits currentCommit = _commitTree.get(headID);
            HashMap<String, Blobs> recent = currentCommit.getTrackFiles();
            if (recent.containsKey(file)) {
                Blobs current = recent.get(file);
                String content = current.getContent();
                if (content.equals(contents)) {
                    addedFiles.remove(file);
                    removedFiles.remove(file);
                }
            }
            Utils.writeObject(pathForAddition, addedFiles);
            Utils.writeObject(pathForRemoval, removedFiles);
        }
    }

    /**
     * Saves a snapshot of tracked files in the current commit
     * and staging area so they can be restored at a
     * later time, creating a new commit. The commit is said to
     * be tracking the saved files. By default, each
     * commit's snapshot of files will be exactly the same as its
     * parent commit's snapshot of files; it will keep
     * versions of files exactly as they are, and not update them.
     * A commit will only update the contents of files it
     * is tracking that have been staged for addition at the
     * time of commit, in which case the commit will now include
     * the version of the file that was staged instead of the
     * version it got from its parent. A commit will save and
     * start tracking any files that were staged for addition
     * but weren't tracked by its parent. Finally, files tracked
     * in the current commit may be untracked in the new commit
     * as a result being staged for removal by the rm command
     * (below).The bottom line: By default a commit is the
     * same as its parent. Files staged for addition and removal
     * are the updates to the commit. Of course, the date
     * (and likely the message) will also be different from the
     * parent.
     * @param message This is the message by the user that
     *                must be used in the commit.
     */
    @SuppressWarnings("unchecked")
    public static void commit(String message) throws GitletException {
        readHelper();
        if (removedFiles.isEmpty() && addedFiles.isEmpty()) {
            System.out.println("No changes added to the commit.");
        }
        for (Object name : removedFiles.keySet()) {
            addedFiles.remove(name);
        }
        String parentBranchIdentifier = _branches.get(
                _headBranch).getID();
        Commits lastCommit = _commitTree.get(
                parentBranchIdentifier);
        HashMap<String, Blobs> last = lastCommit.getTrackFiles();
        for (Object name : removedFiles.keySet()) {
            addedFiles.remove(name);
        }
        for (String name : last.keySet()) {
            if (!addedFiles.containsKey(name)) {
                Blobs newBlob = last.get(name);
                addedFiles.put(name, newBlob);
            }
        }
        Commits newCommit = new Commits(message,
                parentBranchIdentifier, makeTime().toString(),
                addedFiles);
        newCommit.setID();
        Branch currentBranch = _branches.get(_headBranch);
        currentBranch.setID(newCommit.getCommitID());
        LinkedList<String> branchHistory = currentBranch.getHistory();
        branchHistory.add(newCommit.getCommitID());
        currentBranch.updateHistory(branchHistory);
        _commitTree.put(newCommit.getCommitID(), newCommit);
        Utils.writeObject(pathForHeadBranch, _headBranch);
        Utils.writeObject(pathForAllBranches, _branches);
        Utils.writeObject(pathForCommit, _commitTree);
        clear();
        Utils.writeObject(pathForRemoval, removedFiles);
        Utils.writeObject(pathForAddition, addedFiles);
        _allCommitsHistory = Utils.readObject(
                pathForCommitHistory, LinkedList.class);
        _allCommitsHistory.add(newCommit.getCommitID());
        Utils.writeObject(pathForCommitHistory, _allCommitsHistory);
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it
     * for removal and remove the file from the working
     * directory if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).
     * @param removedFile This is the name of the file that must
     *                    be removed.
     */
    @SuppressWarnings("unchecked")
    public static void rm(String removedFile) {
        readHelper();
        File pathForRemovals = Utils.join(cwd, removedFile);
        Commits mostRecentCommit = _commitTree.get(
                _branches.get(_headBranch).getID());
        HashMap<String, Blobs> identifer = mostRecentCommit.getTrackFiles();
        if (!addedFiles.containsKey(removedFile)
                && !identifer.containsKey(removedFile)) {
            System.out.println("No reason to remove the file.");
        }
        addedFiles.remove(removedFile);
        if (!identifer.isEmpty()) {
            if (identifer.containsKey(removedFile)) {
                Blobs removedBlob = identifer.get(removedFile);
                removedFiles.put(removedFile, removedBlob);
                if (pathForRemovals.exists()) {
                    pathForRemovals.delete();
                }
            }
        }
        writeHelper();
    }

    /**
     * Starting at the current head commit, display information
     * about each commit backwards along the commit tree
     * until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge
     * commits. (In regular Git, this is what you get with git log
     * --first-parent). This set of commit nodes is called
     * the commit's history. For every node in this history,
     * the information it should display is the commit id, the
     * time the commit was made, and the commit message.
     */
    @SuppressWarnings("unchecked")
    public static void log() {
        readHelper();
        Branch head = _branches.get(_headBranch);
        for (int item = _branches.get(
                _headBranch).getHistory().size() - 1;
             item >= 0; item--) {
            String commit = head.getHistory().get(item);
            Commits get = _commitTree.get(commit);
            System.out.println("===");
            System.out.println("commit " + get.getCommitID());
            String time = get.getTimeStamp();
            Timestamp getTime = Timestamp.valueOf(time);
            Formatter format = new Formatter();
            format.format("%tc", getTime);
            String date = format.toString();
            date = date.replace("PST ", "");
            date = date.replace("PDT ", "");
            System.out.println("Date: " + date + " -0800");
            System.out.println(get.getMessage() + "\n");
        }
    }

    /**
     * Takes the version of the file as it exists in the head
     * commit, the front of the current branch, and puts it in
     * the working directory, overwriting the version of the file
     * that's already there if there is one. The new version
     * of the file is not staged.
     * @param file This is the name of the file.
     */
    @SuppressWarnings("unchecked")
    public static void checkout1(String file) {
        readHelper();
        Branch head = _branches.get(_headBranch);
        String id = head.getID();
        Commits first = _commitTree.get(id);
        HashMap<String, Blobs> tFile = first.getTrackFiles();
        if (!tFile.containsKey(file)) {
            System.out.println("File does not exist in that commit.");
        }
        Blobs findBlob = tFile.get(file);
        String contents = findBlob.getContent();
        Utils.writeContents(Utils.join(
                cwd, file), contents);
    }

    /**
     * Takes the version of the file as it exists in the commit
     * with the given id, and puts it in the working
     * directory, overwriting the version of the file
     * that's already there if there is one. The new version of the file
     * is not staged.
     * @param id This is the unique ID for the commit.
     * @param file This is the name of the file.
     */
    @SuppressWarnings("unchecked")
    public static void checkout2(String id, String file) {
        readHelper();
        if (id.length() == 8) {
            for (String val : _commitTree.keySet()) {
                String x = id.substring(0, 8);
                if (x.equals(id)) {
                    id = val;
                }
            }
        }
        if (!_commitTree.containsKey(id)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commits getCommit = _commitTree.get(id);
        HashMap<String, Blobs> getFiles = getCommit.getTrackFiles();
        if (!getFiles.containsKey(file)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blobs findBlob = getFiles.get(file);
        String contents = findBlob.getContent();
        File pathForFile = Utils.join(cwd, file);
        Utils.writeContents(pathForFile, contents);
        Utils.writeObject(pathForCommit, _commitTree);
    }

    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter.
     * Hint: there is a useful method in gitlet.Utils that
     * will help you iterate over files within a directory.
     */
    @SuppressWarnings("unchecked")
    public static void globalLog() {
        readHelper();
        for (int item = _allCommitsHistory.size() - 1; item >= 0; item--) {
            String commit = (String) _allCommitsHistory.get(item);
            Commits get = _commitTree.get(commit);
            System.out.println("===");
            System.out.println("commit " + get.getCommitID());
            String time = get.getTimeStamp();
            Timestamp getTime = Timestamp.valueOf(time);
            Formatter format = new Formatter();
            format.format("%tc", getTime);
            String date = format.toString();
            date = date.replace("PST ", "");
            date = date.replace("PDT ", "");
            System.out.println("Date: " + date + " -0800");
            System.out.println(get.getMessage() + "\n");
        }
    }

    /**
     *Prints out the ids of all commits that have the given
     * commit message, one per line. If there are multiple
     * such commits, it prints the ids out on separate lines.
     * The commit message is a single operand; to indicate a
     * multiword message, put the operand in
     * quotation marks, as for the commit command above.
     * @param message This is the message that
     *                we are looking for in the commits.
     */
    @SuppressWarnings("unchecked")
    public static void find(String message) {
        readHelper();
        int counter = 0;
        for (Commits messages : _commitTree.values()) {
            if (messages.getMessage().equals(message)) {
                System.out.println(messages.getCommitID());
                counter++;
            }
        }
        if (counter == 0) {
            System.out.println("Found no commit with that message.");
        }
        Utils.writeObject(pathForCommit, _commitTree);
    }

    /**
     * Displays what branches currently exist, and marks the current branch
     * with a *. Also displays what files have
     * been staged for addition or removal.
     */
    @SuppressWarnings("unchecked")
    public static void status() {
        if (!gitletFolder.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }
        readHelper();
        System.out.println("=== Branches ===");
        List<String> listOfFiles = new ArrayList<>(_branches.keySet());
        Collections.sort(listOfFiles);
        for (String branch : listOfFiles) {
            if (branch.equals(_headBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println("\n" + "=== Staged Files ===");
        listOfFiles.clear();
        for (Object filename : addedFiles.keySet()) {
            listOfFiles.add((String) filename);
        }
        Collections.sort(listOfFiles);
        for (String stagedFiles : listOfFiles) {
            System.out.println(stagedFiles);
        }
        System.out.println("\n" + "=== Removed Files ===");
        listOfFiles.clear();
        for (Object stagedFiles : removedFiles.keySet()) {
            listOfFiles.add((String) stagedFiles);
        }
        Collections.sort(listOfFiles);
        for (String stagedFiles : listOfFiles) {
            System.out.println(stagedFiles);
        }
        System.out.println("\n"
                + "=== Modifications Not "
                + "Staged For Commit ===");
        System.out.println("\n"
                + "=== Untracked Files ==="
                + "\n");
    }

    /**
     * Creates a new branch with the given name, and points it at
     * the current head node. A branch is nothing more
     * than a name for a reference (a SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to
     * the newly created branch (just as in real Git).
     * Before you ever call branch, your code should be running with
     * a default branch called "master".
     * @param newBranch This is the newBranch that must be
     *                  added.
     */
    @SuppressWarnings("unchecked")
    public static void branch(String newBranch) {
        readHelper();
        if (_branches.containsKey(newBranch)) {
            System.out.println("A branch with that name already exists.");
        }
        LinkedList<String> hist = new LinkedList<>(
                _branches.get(
                        _headBranch).getHistory());
        Branch newest = new Branch(newBranch,
                _branches.get(
                        _headBranch).getID(),
                hist, false);
        _branches.put(newBranch, newest);
        Utils.writeObject(pathForAllBranches, _branches);
    }

    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it
     * for removal and remove the file from
     * the working directory if the user has not already done so
     * (do not remove it unless it is tracked in the current commit)
     * @param removeBranch This is branch that must be
     *                     removed.
     */
    @SuppressWarnings("unchecked")
    public static void rmBranch(String removeBranch) {
        readHelper();
        if (!_branches.containsKey(removeBranch)) {
            System.out.println("branch with that name does not exist.");
        } else if (_headBranch.equals(removeBranch)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            _branches.remove(removeBranch);
            Utils.writeObject(pathForAllBranches, _branches);
        }
    }

    /**
     *Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch's head to that commit node.
     * See the intro for an example of what happens to the
     * head pointer after using reset. The [commit id]
     * may be abbreviated as for checkout. The staging area is cleared.
     * The command is essentially checkout of an
     * arbitrary commit that also changes the current branch head.
     * @param id THis is the unique ID.
     */
    @SuppressWarnings("unchecked")
    public static void reset(String id) {
        readHelper();
        if (!_commitTree.containsKey(id)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        HashMap<String, Blobs> tracked = _commitTree.get(id).getTrackFiles();
        for (String filename
                : Utils.plainFilenamesIn(cwd)) {
            if (tracked.containsKey(filename)) {
                String filesReq = tracked.get(filename).getContent();
                if (!filesReq.equals(Utils.readContentsAsString(
                        Utils.join(cwd, filename)))) {
                    System.out.println("There is an untracked file "
                            + "in the way; delete it, or "
                            + "add and commit it first.");
                    return;
                }
            }
        }
        writeHelper();
    }

    /**
     *Takes all files in the commit at the head of the given branch,
     * and puts them in the working directory,
     * overwriting the versions of the files that are already
     * there if they exist.
     * Also, at the end of this command, the given branch
     * will now be considered the
     * current branch (HEAD). Any files that are tracked in the current
     * branch but are not present in the checked-out branch are deleted.
     * The staging area is cleared, unless the
     * checked-out branch is the current branch.
     * @param arg This is the name of the new
     *            pointer.
     */
    @SuppressWarnings("unchecked")
    public static void checkout3(String arg) {
        readHelper();
        if (_headBranch.equals(arg)) {
            System.out.println("No need to checkout the current branch.");
        } else if (!_branches.containsKey(arg)) {
            System.out.println("No such branch exists.");
        } else {
            Commits commit = _commitTree.get(
                    _branches.get(_headBranch).getID());
            HashMap<String, Blobs> tracked = commit.getTrackFiles();
            Commits wanted = _commitTree.get(_branches.get(arg).getID());
            HashMap<String, Blobs> tracked1 = wanted.getTrackFiles();
            List<String> files = Utils.plainFilenamesIn(cwd);
            assert files != null;
            for (String name : files) {
                String contents = Utils.readContentsAsString(
                        Utils.join(cwd, name));
                if (!tracked.containsKey(name)) {
                    if (tracked1.containsKey(name)) {
                        String contents1 = tracked1.get(name).getContent();
                        if (!contents1.equals(contents)) {
                            System.out.println("There is an untracked "
                                    + "file in the"
                                    + "way; delete it, or add and commit it"
                                    + "first.");
                            return;
                        }
                    }
                }
            }
            for (Blobs blob : tracked1.values()) {
                Utils.writeContents(Utils.join(cwd, blob.getFileName()),
                        blob.getContent());
            }
            for (Blobs blob : tracked.values()) {
                if (!tracked1.containsKey(blob.getFileName())) {
                    Utils.join(cwd, blob.getFileName()).delete();
                }
            }
            if (!_headBranch.equals(arg)) {
                clear();
            }
            _headBranch = arg;
            writeHelper();
        }
    }
}
