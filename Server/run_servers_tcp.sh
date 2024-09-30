#!/bin/bash

tmux new-session \; \
	split-window -h \; \
	split-window -v \; \
	split-window -v \; \
	select-layout main-vertical \; \
	select-pane -t 1 \; \
	send-keys "ssh -t $1 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; ./run_server_tcp.sh\"" C-m \; \
	select-pane -t 2 \; \
	send-keys "ssh -t $2 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; ./run_server_tcp.sh\"" C-m \; \
	select-pane -t 3 \; \
	send-keys "ssh -t $3 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; ./run_server_tcp.sh\"" C-m \; \
	select-pane -t 0 \; \
	send-keys "ssh -t $4 \"cd $(pwd) > /dev/null; echo -n 'Connected to '; hostname; sleep .5s; ./run_middleware_tcp.sh $1 $2 $3\"" C-m \;
