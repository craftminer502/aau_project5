# Fifth Semester Robotics Project 
The project consists of 4 modules which are a Scanning Station, Festo PLC, UR5, and a TCP/IP server to link the modules. This repository contains the code for the Scanning Station, Festo PLC, and the Server.

The test of the project may be viewed [here](https://www.youtube.com/playlist?list=PLbPWk-S5f9KP3DcD4nHGeJTXxvSul83Ah)

## A Java-based server bound to wireless (PLC Client) and wired (UR5 Client)
An interface was created and a new Server type may be created by extending Server and overriding the following methods.
#### onDisconnect is called upon a java.net.Socket being passed to the thread.
```java
onConnect();
```
#### onDisconnect is called upon a java.io.EOFException occuring, hence allowing for a timeout.
```java
onDisconnect();
```
#### onMessage is called when the server instance is able to read a byte with a value above 0.
```java
onMessage(String);
```
#### A screenshot of the GUI during the first test
[](https://raw.githubusercontent.com/craftminer502/aau_project5/master/guipic.png)
