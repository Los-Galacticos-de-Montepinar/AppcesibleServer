# AppcesibleServer

## Docker

Ejecutar una vez para montar el servidor
```sh
# mientras estamos en el directorio padre de appdata
docker build -t appcesible:1.0 .   
docker run -p 8080:8080 -v "$(pwd)/appdata:/app" -it -d --name appcesible appcesible:1.0
```
```sh
# o con script
./dockerbuild.sh
```
Cada vez que se quiera entrar en el terminal del servidor
```sh
docker exec -it appcesible /bin/bash
```
Leer el readme dentro del servidor para ver como se inicia el servidor

## API

### User

Obtener usuario con ID = 1
```sh
curl -i -X GET http://localhost:8080/user/1
```
Obtener todos los usuarios
```sh
curl -i -X GET http://localhost:8080/user
```
Crear nuevo usuario
```sh
curl -i -X POST -H 'Content-Type: application/json'\
-d '{"userName": "Diego Brando","pfp":4,"userType":0,"idClass":0,"passwd":"pass"}'\
http://localhost:8080/user/new
```
```sh
curl -i -X POST -H 'Content-Type: application/json'\
-d '{"userName": "Lucy Steel","pfp":5,"userType":0,"idClass":1,"passwd":"pass","letterSize":20,"interactionFormat":2,"loginType":1}'\
http://localhost:8080/user/new
```
Actualizar usuario con ID = 1
```sh
curl -i -X POST -H 'Content-Type: application/json' -d '{"letterSize":23}' http://localhost:8080/user/1
```
Obtener profesor con ID = 1
```sh
curl -i -X GET http://localhost:8080/user/teacher/1
```
Obtener todos los profesores = 1
```sh
curl -i -X GET http://localhost:8080/user/teacher
```
Obtener estudiante con ID = 1
```sh
curl -i -X GET http://localhost:8080/user/student/1
```
Obtener todos los estudiantes
```sh
curl -i -X GET http://localhost:8080/user/student
```
Los tipos de usuario son los siguientes
- 0 profesor
- 1 estudiante
- 2 administrador

### Task

Crear una nueva tarea
```sh
curl -i -X POST -H 'Content-Type: application/json'\
-d '{"title":"Clean","desc":"clean a thing"}'\
http://localhost:8080/task/new
```
Crear un paso asociado a una tarea
```sh
curl -i -X POST -H 'Content-Type: application/json'\
-d '{"media":"videolink","order":1,"taskId":1,"desc":"start cleaning"}'\
http://locahost:8080/task/step/new
```
Obtener todas las tareas
```sh
curl -i -X GET http://localhost:8080/task
```
Obtener todos los pasos de una tarea con ID = 1
```sh
curl -i -X GET http://localhost:8080/task/1/steps
```
### Session

Obtener token de sesión
```sh
curl -i -X POST -H 'Content-Type: application/json'\
-d '{"userName": "Gyro Zeppeli","passwd":"pass33"}' http://localhost:8080/session/login
```
Cerrar sesión usando un TOKEN = 4d49b7bd-359e-4aa7-9919-979dfc285e2c
```sh
curl -i -X POST -H 'Content-Type: application/json'\
-d '{"sessionToken": "4d49b7bd-359e-4aa7-9919-979dfc285e2c"}'\
http://localhost:8080/session/logout
```