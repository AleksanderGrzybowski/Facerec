CFLAGS = `pkg-config --cflags opencv` 
LIBS = `pkg-config --libs opencv`
EXTRA=-ggdb --std=c++11


all:
	mkdir -p bin
	g++ $(CFLAGS) $(LIBS) $(EXTRA) train.cpp -o bin/train 
	g++ $(CFLAGS) $(LIBS) $(EXTRA) recognize.cpp -o bin/recognize
	g++ $(CFLAGS) $(LIBS) $(EXTRA) extract_face.cpp -o bin/extract_face

