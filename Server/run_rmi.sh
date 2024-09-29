#adjust port number if necessary

PIDS=$(lsof -t -i :2324)
for PID in $PIDS; do
    kill -9 $PID
    echo "Killed process with PID: $PID"
done

rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false 2324 &
