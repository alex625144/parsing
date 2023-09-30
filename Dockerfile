FROM ubuntu:20.04
RUN apt-get -y update
RUN apt-get install -y python3.8
RUN apt-get -y install tesseract-ocr && apt-get -y install ffmpeg libsm6 libxext6

FROM openjdk:17-alpine
ADD entrypoint.sh entrypoint.sh
COPY target/parsing-0.0.1-SNAPSHOT.jar parsing-0.0.1-SNAPSHOT.jar
RUN chmod 550 entrypoint.sh

RUN apk add --no-cache libstdc++6

ENTRYPOINT ["./entrypoint.sh"]