# LiferayPokedex
Individual Development Plan (PDI)

```
docker run -it -m 8g -p 8080:8080 liferay/portal:7.4.3.132-ga132

./gradlew deploy -Ddeploy.docker.container.id=$(docker ps -lq)

```
