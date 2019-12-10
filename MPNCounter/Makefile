all:
	@echo 'Compiling JNI header........'
	@javac MPN.java -h .
	@echo 'Compiling shared object.....'
	@g++ -std=c++11 -shared -fPIC -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux  MPN.cpp -o libmpn.so `pkg-config --cflags --libs opencv`
	@echo 'Compiling java program......'
	@javac -d ~/eclipse-workspace/DualServer/bin ~/eclipse-workspace/DualServer/src/*.java

test:
	@echo '................................................................................'
	@echo 'Robotics Project 5.........         _//|.-~~~~-,         .......................'
	@echo 'Building JNI header........       _/66  \       \_@      .......................'
	@javac MPN.java -h .
	@echo 'Building shared object.....      (")_   /   /   |        .......................'
	@g++ -std=c++11 -shared -fPIC -I/usr/lib/jvm/java-11-openjdk-amd64/include/ -I/usr/lib/jvm/java-11-openjdk-amd64/include/linux  MPN.cpp -o libmpn.so `pkg-config --cflags --libs opencv`
	@echo 'Building java program......        ´--´|| |-\  /         .......................'
	@javac -d ~/eclipse-workspace/DualServer/bin ~/eclipse-workspace/DualServer/src/*.java
	@echo 'Running program............            //_/ /_/          .......................'
	@echo '................................................................................'
	@java -cp ~/eclipse-workspace/DualServer/bin/ -Djava.library.path='.:~/Documents/MPNCounter' DualServer
