# AppcesibleServer

## Docker

Ejecutar una vez para montar el servidor

    docker build -t appcesible:1.0 .   
    docker run -p 8080:8080 -v "$(pwd)/appdata:/app" -it -d --name appcesible appcesible:1.0

Cada vez que se quiera entrar en el terminal del servidor

    docker exec -it appcesible /bin/bash

Leer el readme dentro del servidor para ver como se inicia el servidor

## API
Obtener usuario con id

    curl -i -X GET http://localhost:8080/user/id

Obtener todos los usuarios

    curl -i -X GET http://localhost:8080/user

Crear nuevo usuario

    curl -i -X POST -H 'Content-Type: application/json' -d '{"userName": "Diego Brando","pfp":4,"userType":0,"idClass":0,"passwd":"pass"}' http://localhost:8080/user/new

Obtener profesor con id

    curl -i -X GET http://localhost:8080/user/teacher/id

Obtener todos los profesores

    curl -i -X GET http://localhost:8080/user/teacher

Obtener estudiante con id

    curl -i -X GET http://localhost:8080/user/student/id

Obtener todos los estudiantes

    curl -i -X GET http://localhost:8080/user/student

Los tipos de usuario son los siguientes
- 0 profesor
- 1 estudiante
- 2 administrador
