FROM debian:8.4

RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee /etc/apt/sources.list.d/webupd8team-java.list && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu xenial main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886 && \
    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

RUN apt-get update && apt-get install -y libopencv-calib3d2.4 libopencv-contrib2.4 libopencv-core2.4 libopencv-features2d2.4 libopencv-flann2.4 libopencv-gpu2.4 libopencv-highgui2.4 libopencv-imgproc2.4 libopencv-legacy2.4 libopencv-ml2.4 libopencv-objdetect2.4 libopencv-ocl2.4 libopencv-photo2.4 libopencv-stitching2.4 libopencv-superres2.4 libopencv-ts2.4 libopencv-video2.4 libopencv-videostab2.4 libopencv2.4-java libopencv2.4-jni opencv-data libdc1394-22 oracle-java8-installer && \
    apt-get clean && rm -rf /var/lib/apt/lists /usr/share/doc/ /usr/share/man/ /usr/share/locale/ /var/cache/oracle-jdk8-installer


RUN mkdir /app
COPY build/libs/facerec-1.0-SNAPSHOT-all.jar ssl.p12 /app/
COPY public /app/public
COPY core/bin /app/core/bin
COPY core/photos /app/core/photos

# hack around OpenCV, see below
RUN ln /dev/null /dev/raw1394

ENV PORT 443
WORKDIR /app

# http://stackoverflow.com/questions/31768441/how-to-persist-ln-in-docker-with-ubuntu
CMD sh -c "ln -s /dev/null /dev/raw1394; java -jar facerec-1.0-SNAPSHOT-all.jar"

