FROM java:openjdk-8-jre


RUN apt-get update && apt-get install -y libopencv-calib3d2.4 libopencv-contrib2.4 libopencv-core2.4 libopencv-features2d2.4 libopencv-flann2.4 libopencv-gpu2.4 libopencv-highgui2.4 libopencv-imgproc2.4 libopencv-legacy2.4 libopencv-ml2.4 libopencv-objdetect2.4 libopencv-ocl2.4 libopencv-photo2.4 libopencv-stitching2.4 libopencv-superres2.4 libopencv-ts2.4 libopencv-video2.4 libopencv-videostab2.4 libopencv2.4-java libopencv2.4-jni opencv-data libdc1394-22 && \
    apt-get clean && rm -rf /var/lib/apt/lists /usr/share/doc/ /usr/share/man/ /usr/share/locale/


RUN mkdir /app
COPY build/libs/facerec-1.0-SNAPSHOT-all.jar config.properties ssl.p12 /app/
COPY public /app/public
COPY core/bin /app/core/bin
COPY core/photos /app/core/photos
COPY core/models /app/core/models

# hack around OpenCV, see below
RUN ln /dev/null /dev/raw1394

ENV PORT 443
WORKDIR /app

# http://stackoverflow.com/questions/31768441/how-to-persist-ln-in-docker-with-ubuntu
CMD sh -c "ln -s /dev/null /dev/raw1394; java -jar facerec-1.0-SNAPSHOT-all.jar"

