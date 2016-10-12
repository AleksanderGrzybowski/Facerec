# Face recognition app

This is very typical face recognition app made for Computer Vision course at Silesian University of Technology. Professor suggested the Android platform, but it would take me a lot of time to learn it, and doing it as a web app makes it almost multiplatform. However, I tested it on my Xperia phone and Google Chrome only.

Backend
* modified recognition examples from OpenCV website in C++, extended with possibilities to pick different methods of recognition
* HTTP API written in Java (Spark microframework) that glues everything together
* Docker is a must, cause we have ugly native dependencies
* "fake" SSL certificate for Chrome to stop complaining

Frontend
* Two pieces, one for recognizing faces, and second for just extracting (useful for collecting training data)
* Decent Webcam.js library for camera support and jQuery+Bootstrap for the rest

![Screenshot 1](screenshots/1.png)
![Screenshot 2](screenshots/2.png)
![Screenshot 3](screenshots/3.png)

