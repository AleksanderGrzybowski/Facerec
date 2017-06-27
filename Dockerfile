FROM java:openjdk-8-jre

RUN apt-get update && apt-get -y install libgtk2.0-0:amd64 && rm -rf /var/lib/apt/lists /usr/share/doc/ /usr/share/man/ /usr/share/locale/

RUN mkdir /app
COPY build/libs/facerec-1.0-SNAPSHOT-all.jar config.properties ssl.p12 /app/
COPY frontend/dist /app/public
COPY photos /app/photos

WORKDIR /app

RUN sed  -i -e '/public_html=/c\public_html=public' config.properties

ENV FACEREC_PORT 443

CMD sh -c "ln -s /dev/null /dev/raw1394; java -jar facerec-1.0-SNAPSHOT-all.jar"
