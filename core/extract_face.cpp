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
    if (argc != 2) {
        throw runtime_error("Filename not given");
    }
    string input_filename = string(argv[1]);
    
    // TODO only every second!
    srand(time(0));

    CascadeClassifier haar_cascade;
    haar_cascade.load(fn_haar);

    Mat frame = imread(input_filename);
    Mat original = frame.clone();
    Mat gray;
    cvtColor(original, gray, CV_BGR2GRAY);

    vector<Rect_<int>> faces;
    haar_cascade.detectMultiScale(gray, faces);

    if (faces.size() == 0) {
        return 1;
    }

	// assuming there is only one face
	Mat face = gray(faces[0]);
	Mat face_resized;
	cv::resize(face, face_resized, Size(im_width, im_height), 1.0, 1.0, INTER_CUBIC);

	// TODO better way
	string output_filename = string("/tmp/extract_face") + to_string(rand() % 1000000) + ".jpg";
	imwrite(output_filename.c_str(), face_resized);
	
	FILE* fp = fopen(output_filename.c_str(), "rb");
	
	int ch;
	while ((ch = fgetc(fp)) != -1) {
		cout << (char)ch;
	}
	
	fclose(fp);
    return 0;
}

