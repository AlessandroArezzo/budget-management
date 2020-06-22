mongo --eval "rs.initiate();"
until mongo --eval "rs.isMaster()" | grep ismaster | grep true > /dev/null 2>&1;do sleep 1;done
