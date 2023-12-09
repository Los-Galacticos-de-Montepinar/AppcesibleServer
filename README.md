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
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"userName": "Diego Brando","pfp":4,"userType":0,"idClass":0,"passwd":"pass"}' \
http://localhost:8080/user/new
```
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"userName": "Lucy Steel","pfp":5,"userType":1,"idClass":1,"letterSize":20,"interactionFormat":2,"loginType":1,"passPart0":1,"passPart1":1,"passPart2":1}' \
http://localhost:8080/user/new
```
Actualizar usuario con ID = 1
```sh
curl -i -X POST -H 'Content-Type: application/json' -d '{"letterSize":23}' http://localhost:8080/user/1
```
```sh
curl -i -X POST -H 'Content-Type: application/json' -d '{"loginType":1,"passPart0":1,"passPart1":1,"passPart2":1}' http://localhost:8080/user/1
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
Eliminar usuario con ID = 1
```sh
curl -i -X POST http://localhost:8080/user/delete/1
```
Los tipos de usuario son los siguientes
- 0 profesor
- 1 estudiante
- 2 administrador


### Task

Crear una nueva tarea
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"title":"Clean","desc":"clean a thing"}' \
http://localhost:8080/task/new
```
Crear un paso asociado a una tarea
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"media":3,"order":1,"taskId":1,"desc":"start cleaning"}' \
http://locahost:8080/task/step/new
```
Obtener una tarea con ID = 1
```sh
curl -i -X GET http://localhost:8080/task/1
```
Obtener todas las tareas
```sh
curl -i -X GET http://localhost:8080/task
```
Obtener todos los pasos de una tarea con ID = 1
```sh
curl -i -X GET http://localhost:8080/task/1/steps
```
Crear una peticion de material
```sh
curl -i -XPOST -H 'Content-Type: application/json' \
-d '{"desc": "Do a thing desc","title":"Thing"}' \
http://localhost:8080/task/petition/new
```
Añadir item a una peticion de material con ID = 1
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"item: 1,"count":10}' http://localhost:8080/task/petition/1/item/new
```
Obtener items de una peticion de material con ID = 1
```sh
curl -i -X GET http://localhost:8080/task/petition/1/item
```
Asignar una tarea con ID = 1 a un usuario con ID = 1
```sh
curl -i -X POST -H 'Content-Type: application/json' -d '{"date": "10/11/24","user":1}' http://localhost:8080/task/1/assign
```
Obtener todas las tareas asignadas a un usuario con ID = 1
```sh
curl -i -X GET http://localhost:8080/task/user/1
```
### Session

Comprobar validez de contraseña parcial
```sh
# Si devuelve 0, es que es válida
# No devuelve el token si no se envian los 3 componentes correctos
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"userName": "Lucy Steel","passPart0":1,"passPart1":1,"publicKey":rasfDDFSs}' \
http://localhost:8080/session/login
```

Obtener token de sesión
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"userName": "Gyro Zeppeli","passwd":"pass33","publicKey":hhdfdfDCss}' http://localhost:8080/session/login
```
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"userName": "Lucy Steel","passPart0":1,"passPart1":1,"passPart2":1,"publicKey":rasfDDFSs}' \
http://localhost:8080/session/login
```
Cerrar sesión usando un TOKEN = 4d49b7bd-359e-4aa7-9919-979dfc285e2c
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"sessionToken": "4d49b7bd-359e-4aa7-9919-979dfc285e2c"}' \
http://localhost:8080/session/logout
```
### Items

Añadir nuevo item al inventario
```sh
curl -i -X POST -H 'Content-Type: application/json' \
-d '{"name": "Bulb","image":1,"count":3}' \
http://localhost:8080/item/new
```
Obtener item con ID = 1
```sh
curl -i -X GET http://localhost:8080/item/1
```
Obtener todos los items
```sh
curl -i -X GET http://localhost:8080/item
```

### Clases

Obtener lista de clases
```sh
curl -i -X GET http://localhost:8080/class
```

### Galeria

Obtener lista de media
```sh
curl -i -X GET http://localhost:8080/gallery
```

Descargar media con ID = 1 en un archivo con nombre dl.png
```sh
curl -o dl.png  http://localhost:8080/gallery/1
```
Subir media junto otros datos
```sh
curl -i -Ffiledata=@"test.png" -Fdata='{"username":"user1", "password":"password"}'  http://localhost:8080/gallery/new
```

Subir media
```sh
curl -i -Ffiledata=@"test.png" http://localhost:8080/gallery/new
```


