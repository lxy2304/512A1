all: compile-client

../Server/RMIInterface.jar: ../Server/Server/Interface/IResourceManager.java
	make -C ../Server/ RMIInterface.jar

Command.jar: Client/Command.java
	@echo "Compiling Command enum"
	javac Client/Command.java
	jar cvf Command.jar Client/Command.class

compile-client: ../Server/RMIInterface.jar
	javac -cp ../Server/RMIInterface.jar Client/*.java

clean:
	rm -f Client/*.class
	rm -f ../Server/RMIInterface.jar

