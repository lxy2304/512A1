PIDS=$(lsof -t -i :2324)
for PID in $PIDS; do
    kill -9 $PID
    echo "Killed process with PID: $PID"
done

java -cp ../Client/Command.jar:. Server.TCP.TCPMiddleware $1 $2 $3