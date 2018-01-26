FROM openjdk:8-jdk

RUN curl -sL https://deb.nodesource.com/setup_9.x | bash -
RUN apt-get install -y nodejs

COPY . /source
WORKDIR /source

RUN ./gradlew clean shadow

WORKDIR /source/frontend
RUN git config --global user.email nobody@nobody.com && git config --global user.name Nobody
RUN npm install
RUN npm run build


FROM openjdk:8-jre

RUN apt-get update && apt-get -y install libgtk2.0-0:amd64

RUN mkdir /app
COPY --from=0 /source/build/libs/facerec-1.0-SNAPSHOT-all.jar /source/config.properties /source/ssl.p12 /app/
COPY --from=0 /source/frontend/dist /app/public
COPY --from=0 /source/photos /app/photos

WORKDIR /app

RUN sed -i -e '/public_html=/c\public_html=public' config.properties

ENV FACEREC_PORT 443

CMD sh -c "ln -s /dev/null /dev/raw1394; java -jar facerec-1.0-SNAPSHOT-all.jar"
