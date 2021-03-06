JFLAGS = -g -d bin -cp bin

vpath %.java src/
vpath %.class bin/

.SUFFIXES: .java .class

.java.class:
	javac $(JFLAGS) $<

all: Message.class Connection.class Server.class Client.class

Connection.class:
	rm -rf bin/Connection.class
	javac $(JFLAGS) src/Connection.java

Message.class:
	rm -rf bin/Message.class
	javac $(JFLAGS) src/Message.java

Server.class:
	rm -rf bin/Server.class
	javac $(JFLAGS) src/Server.java

Client.class:
	rm -rf bin/Client.class
	javac $(JFLAGS) src/Client.java


run: clean all
	@gnome-terminal -e "java -cp bin Server"
	@gnome-terminal -e "java -cp bin Client"
	java -cp bin Client

client: all
	java -cp bin Client

server: all
	java -cp bin Server ServerS1

clean:
	@rm -f bin/*

do: clean all
