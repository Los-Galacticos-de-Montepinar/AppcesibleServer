docker build -t appcesible:1.0 .   
docker run -p 8080:8080 -v "$(pwd)/appdata:/app" -it -d --name appcesible appcesible:1.0

#   start with:
#   docker start appcesible
#	docker exec -it appcesible /bin/bash
#
#	Para acceder a la BD
#		sqlite3 db/appcesible.db
#
#	docker stop appcesible