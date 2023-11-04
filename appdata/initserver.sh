#!/bin/bash
touch serverPID

# Limpiar servicios antiguos
if kill $(cat serverPID) 2>/dev/null; then
        echo "Removed previous process"
else
        pkill -f java
        echo "Previous process not found, cleaning all java processes"
fi
sleep 1

# Compilar el servidor
javac src/*.java -d ./class

# Lanzar servidor
cd class
java -classpath ".:sqlite-jdbc-3.43.2.1.jar:slf4j-api-1.7.36.jar" Server &
cd ..

# Guardar PID del servidor
if [ $? -eq 0 ]; then
        echo $! | cat > serverPID
        echo "Server started succesfully!"
else
        echo "Could not start server"
fi

