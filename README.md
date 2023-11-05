# AppcesibleServer

## Docker

Ejecutar una vez para montar el servidor

    # mientras estamos en el directorio padre de appdata
    docker build -t appcesible:1.0 .   
    docker run -p 8080:8080 -v "$(pwd)/appdata:/app" -it -d --name appcesible appcesible:1.0


    # o con script
    ./dockerbuild.sh

Cada vez que se quiera entrar en el terminal del servidor

    docker exec -it appcesible /bin/bash

Leer el readme dentro del servidor para ver como se inicia el servidor

## API

### User

Obtener usuario con ID

    curl -i -X GET http://localhost:8080/user/ID

Obtener todos los usuarios

    curl -i -X GET http://localhost:8080/user

Crear nuevo usuario

    curl -i -X POST -H 'Content-Type: application/json'\
    -d '{"userName": "Diego Brando","pfp":4,"userType":0,"idClass":0,"passwd":"pass"}'\
    http://localhost:8080/user/new


    curl -i -X POST -H 'Content-Type: application/json'\
    -d '{"userName": "Lucy Steel","pfp":5,"userType":0,"idClass":1,"passwd":"pass","letterSize":20,"interactionFormat":2,"loginType":1}'\
    http://localhost:8080/user/new

Actualizar usuario con ID = 1

    curl -i -X POST -H 'Content-Type: application/json' -d '{"letterSize":23}' http://localhost:8080/user/1

Obtener profesor con ID

    curl -i -X GET http://localhost:8080/user/teacher/ID

Obtener todos los profesores

    curl -i -X GET http://localhost:8080/user/teacher

Obtener estudiante con ID

    curl -i -X GET http://localhost:8080/user/student/ID

Obtener todos los estudiantes

    curl -i -X GET http://localhost:8080/user/student

Los tipos de usuario son los siguientes
- 0 profesor
- 1 estudiante
- 2 administrador

### Session

Obtener token de sesion

    curl -i -X POST -H 'Content-Type: application/json' -d '{"userName": "Gyro Zeppeli","passwd":"pass33"}' http://localhost:8080/session/login

Cerrar session usando un TOKEN

    curl -i -X POST -H 'Content-Type: application/json' -d '{"sessionToken": "TOKEN"}' http://localhost:8080/session/logout

Por ahora el token no es necesario para hacer get y posts de los usuarios, pero recomiendo que la aplicación se programe como si sí fuera necesario, para que cuando se implemente en el servidor no sea necesario repensar la aplicación
