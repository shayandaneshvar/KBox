FROM adoptopenjdk/openjdk11:alpine-jre-nightly
RUN  apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/kbox
RUN echo $JAVA_HOME
ADD @project.build.finalName@.jar /usr/local/kbox/
COPY run.sh .
RUN chmod +x run.sh
ENTRYPOINT ["./run.sh"]
