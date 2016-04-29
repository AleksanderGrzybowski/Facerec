#include "opencv2/core/core.hpp"
#include "opencv2/contrib/contrib.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/objdetect/objdetect.hpp"

#include <iostream>
#include <fstream>
#include <sstream>

using namespace cv;
using namespace std;


const string fn_haar = "/usr/share/opencv/haarcascades/haarcascade_frontalface_alt.xml";
const int im_width = 92;
const int im_height = 112;

int main(int argc, const char *argv[]) {
    if (argc != 3) {
        throw runtime_error("Filename not given");
    }
    string input_filename = string(argv[1]);
    string method = string(argv[2]);

    Ptr<FaceRecognizer> model;
    string model_filename;
    
    if (method == "ef") {
    	model = createEigenFaceRecognizer();
    	model_filename = "model-ef.yml";
    } else if (method == "ff") {
    	model = createFisherFaceRecognizer();
    	model_filename = "model-ff.yml";
    } else if (method == "lbph") {
    	model = createLBPHFaceRecognizer();
    	model_filename = "model-lbph.yml";
    } else {
    	throw runtime_error("Wrong method name");
    }
    
    model->load(model_filename);

    CascadeClassifier haar_cascade;
    haar_cascade.load(fn_haar);

    Mat frame = imread(input_filename);
    Mat original = frame.clone();
    Mat gray;
    cvtColor(original, gray, CV_BGR2GRAY);

    vector<Rect_<int>> faces;
    haar_cascade.detectMultiScale(gray, faces);

    if (faces.size() == 0) {
        cout << "no faces" << endl;
        return 1;
    }

    for (auto& face_i: faces) {
        Mat face = gray(face_i);
        Mat face_resized;
        cv::resize(face, face_resized, Size(im_width, im_height), 1.0, 1.0, INTER_CUBIC);

        // To test:
        // imshow("image", face_resized);
        // waitKey(0);

        int prediction = model->predict(face_resized);
        cout << prediction << endl;
    }

    return 0;
}

