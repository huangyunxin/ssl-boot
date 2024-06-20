IMAGE_NAME=openjdk:17-slim
CONTAINER_NAME=ssl-boot

docker rm -f $CONTAINER_NAME

docker run --restart always -d -p 8080:8080 \
-e TZ=Asia/Shanghai \
-v ./ssl-1.0.0.jar:/app/app.jar \
-v ./application.yml:/app/application.yml \
-w /app \
--name $CONTAINER_NAME \
$IMAGE_NAME \
java -Dfile.encoding=UTF-8 -jar app.jar