# Face recognition app

This is very typical face recognition app made for Computer Vision course at Silesian University of Technology. 

Backend
* modified recognition examples from OpenCV website in C++, extended with possibilities to pick different methods of recognition
* HTTP API written in Java (Spring Boot) that glues everything together
* Docker is a must, cause we have ugly native dependencies
* "fake" SSL certificate, cause Chrome is picky about using camera from so-called "untrusted domains", with apparently no way of overriding this :(

Frontend
* Two modules, one for recognizing faces, and second for extracting (useful for training and testing)
* Decent Webcam.js library for camera support and jQuery+Bootstrap for the rest


