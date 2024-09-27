all: compile-server-rmi compile-server-tcp

RMIInterface.jar: Server/Interface/IResourceManager.java
	@echo "Compiling RMI server interface"
	javac Server/Interface/IResourceManager.java
	jar cvf RMIInterface.jar Server/Interface/IResourceManager.class

../Client/Command.jar: ../Client/Client/Command.java
	make -C ../Client/ Command.jar

compile-server-rmi: Server/RMI/*.java Server/Interface/IResourceManager.java Server/Common/*.java
	javac Server/RMI/*.java Server/Interface/IResourceManager.java Server/Common/*.java

compile-server-tcp: ../Client/Command.jar Server/TCP/*.java Server/Interface/IResourceManager.java Server/Common/*.java
	javac -cp ../Client/Command.jar Server/TCP/*.java Server/Interface/IResourceManager.java Server/Common/*.java

clean:
	rm -f Server/Interface/*.class Server/Common/*.class Server/RMI/*.class
	rm -f RMIInterface.jar
	rm -f Server/TCP/*.class

