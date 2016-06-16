// Including "header" file like this probably shouldn't be done,
// but it's easy and avoids playing with includes or compilation units.

const string fn_haar = "/usr/share/opencv/haarcascades/haarcascade_frontalface_alt.xml";
const int im_width = 92;
const int im_height = 112;

const vector<string> model_filenames = {
    "../models/model-ef.yml",
    "../models/model-ff.yml",
    "../models/model-lbph.yml"
};
