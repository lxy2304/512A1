echo '  $1 - hostname of Flights'
echo '  $2 - hostname of Cars'
echo '  $3 - hostname of Rooms'


./run_rmi.sh > /dev/null 2>&1

# Run the Middleware
java -Djava.rmi.server.codebase=file:$(pwd)/ Server.RMI.RMIMiddleware $1 $2 $3
