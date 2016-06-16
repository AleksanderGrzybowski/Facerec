#include "opencv2/core/core.hpp"
#include "opencv2/contrib/contrib.hpp"
#include "opencv2/highgui/highgui.hpp"

#include <iostream>
#include <fstream>
#include <sstream>
#include <exception>

#include <dirent.h>

using namespace cv;
using namespace std;

// stolen from SO
vector<string> list_files(string directory) {
    vector<string> list;
    DIR *dir;
    struct dirent *ent;
    if ((dir = opendir (directory.c_str())) != NULL) {
        while ((ent = readdir(dir)) != NULL) {
            if (!strcmp(ent->d_name, ".") || !strcmp(ent->d_name, "..") || !strcmp(ent->d_name, ".gitkeep")) continue;
            list.push_back(ent->d_name);
        }
        closedir (dir);
    } else {
      throw runtime_error("Could not open directory " + directory);
    }

    return list;
}

Mat norm_0_255(InputArray _src) {
    Mat src = _src.getMat();
    Mat dst;

    switch(src.channels()) {
        case 1:
            cv::normalize(_src, dst, 0, 255, NORM_MINMAX, CV_8UC1);
            break;
        case 3:
            cv::normalize(_src, dst, 0, 255, NORM_MINMAX, CV_8UC3);
            break;
        default:
            src.copyTo(dst);
            break;
    }

    return dst;
}

void load_photos(string basedir, vector<Mat>& images, vector<int>& labels) {
    cout << "Reading photos" << endl;

    vector<string> people = list_files(basedir);

    for(string& people_dir: people) {
        cout << "reading from directory " << people_dir << endl;
        int classlabel = stoi(people_dir);
        vector<string> people_photos = list_files(basedir + "/" + people_dir);
        
        for (string& photo: people_photos) {
            string path = basedir + "/" + people_dir + "/" + photo;
            cout << "reading path " << path << " label=" << classlabel << endl;
            images.push_back(imread(path, 0));
            labels.push_back(classlabel);
        }
    }

    cout << "Images loaded" << endl;
}

const vector<string> model_filenames = {"../models/model-ef.yml", "../models/model-ff.yml", "../models/model-lbph.yml"};

int main(int argc, const char *argv[]) {
	if (argc != 2) {
		throw runtime_error("Photo folder not given");
	}
	
    vector<Mat> images;
    vector<int> labels;

	string basedir = string(argv[1]);
    load_photos(basedir, images, labels);

    Ptr<FaceRecognizer> model;
    
    
    // EF
    model = createEigenFaceRecognizer();

    cout << "EF Training starts" << endl;
    model->train(images, labels);

    cout << "EF Training finished, saving" << endl;
    model->save(model_filenames[0]);
    
    
    // FF
    model = createFisherFaceRecognizer();

    cout << "FF Training starts" << endl;
    model->train(images, labels);

    cout << "FF Training finished, saving" << endl;
    model->save(model_filenames[1]);
    
    
    // LBPH
    model = createLBPHFaceRecognizer();

    cout << "LBPH Training starts" << endl;
    model->train(images, labels);

    cout << "LBPH Training finished, saving" << endl;
    model->save(model_filenames[2]);

    return 0;
}
