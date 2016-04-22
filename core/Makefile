CFLAGS = `pkg-config --cflags opencv` 
LIBS = `pkg-config --libs opencv`
EXTRA=-ggdb --std=c++11


all:
	g++ $(CFLAGS) $(LIBS) $(EXTRA) train.cpp -o train 
	g++ $(CFLAGS) $(LIBS) $(EXTRA) recognize.cpp -o recognize
