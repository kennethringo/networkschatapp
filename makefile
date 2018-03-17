JFLAGS = -g -d bin -cp bin

vpath %.java src/
vpath %.class bin/

.SUFFIXES: .java .class

.java.class:
	javac $(JFLAGS) $<

all: Util.class Packet.class Listener.class Server.class Client.class

Listener.class : Server.class

Server.class:
	rm -rf bin/Listener.class bin/Server.class
	javac $(JFLAGS) src/Listener.java src/Server.java

run: clean all
	@gnome-terminal -e "java -cp bin Server"
	@gnome-terminal -e "java -cp bin Client"
	java -cp bin Client

client: all
	java -cp bin Client

server: all
	java -cp bin Server

clean:
	@rm -f bin/*
