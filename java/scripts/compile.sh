#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


# compile the java program
javac -d $DIR/../lib $DIR/../src/PizzaStore.java

#run the java program
#Use your database name, port number and login
java -cp $DIR/../lib:$DIR/../lib/pg73jdbc3.jar PizzaStore $USER"_project_phase_3_DB" $PGPORT $USER

