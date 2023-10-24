docker build -t appcesible:1.0 .   
docker run -p 8080:8080 -v "$(pwd)/appdata:/app" -it -d --name appcesible appcesible:1.0

#   start with:
#   docker start appcesible