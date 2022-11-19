# Gitlet Design Document
author: Harshaan Sall & Shan Virani

# 1. Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely
format and style a text file. Organize your design document in a way that
will make it easy for you or a course-staff member to read.

# Classes and Data Structures

## Blobs
### Fields
1. String _content: this string will contain the contents contained within a file to be staged and committed.
2. String _name: The name of our blob object.

## Commit
### Fields
1. String _message: the commit message that accompanies a specific instance of a commit.
2. String _Parent: The parent of a commit object.
3. Hashmap <String, Blobs>: A hashmap that contains the name of the file and the "blobs" or content of the files.
4. String _timeStamp: the timestamp of an initial commit object.
5. String _id: the id that accompanies a commit, used in gitlet's log.

## Branch
### Fields
String _name: the name of this branch object.
LinkedList _history: a linked list of commit objects that represents the history of commits.

### Commands
#### Fields
Branch: the branch for this repo that contains the commit history and blobs.


# 2. Algorithms
## Blobs
1. Blob(String name, String content): this method serves as the constructor for blob objects. The parameters for this
   constructor are two strings, name and content.
2. getName(): this method is a getter method for the name instance variable for blob objects.
3. getContents(): this is a getter method for the content instance variable for blob objects.

## Commit
1. Commit(String message, String parent, HashMap<String, Blob> files, String timestamp):
   This method serves as a constructor for commit objects. This method takes parameters messase, parent,
   files, and timestamp which all initialize the respective instance variables.
2. getMessage() is a getter method that returns the private instance variable message.
3. getParent() is a getter method that returns the private instance variable parent.
4. getFiles() is a getter method that returns the private instance variable files.
5. getTimestamp() is a getter method that returns the private instance variable.

## Branch
1. Branch(String name, LinkedList _hist): this is the constructor for a branch object which takes in
   name and hist as parameters and initializes them to the instance variables.
2. getName() this method is a getter method for the name instance variable for blob objects.
3. getHist() this method is a getter method for the history instance variable for blob objects.


## 3. Persistence

In order to ensure Persistence includes using a Tree datastructure, and HashMap. Initially,
we would serialize the contents of the HashMap and turn it into bytes which we can later
deserialize.

First, when "git add" is called, we would make a new LinkedList that sets the file/s in the staging
area. Then, once "commit" is called, we would add another branch to our tree and insert the
Blob's as the contents that are connected to our tree. Each add will make a new LinkedList
while commits will add the LinkedList, move pointers, and create blobs.

By doing so, this should ensure that all future calls will be consistent and work as described
above.

## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to
visualize the structure and workflow of your program.


