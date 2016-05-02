FROM java:8
RUN apt-get update && apt-get install -y libopencv-dev
RUN mkdir /app
COPY build/libs/facerec-1.0-SNAPSHOT-all.jar /app
COPY public /app/public
COPY ssl.p12 /app
COPY core/bin /app/core/bin
COPY core/photos /app/core/photos

ENV PORT 443

WORKDIR /app
CMD java -jar facerec-1.0-SNAPSHOT-all.jar