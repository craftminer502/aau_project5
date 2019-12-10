#include "MPN.h"
#include <ctype.h>
#include <string.h>
#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/imgproc.hpp>
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>

using namespace cv;
using namespace std;

RNG rng(12345);
int mpn = 0;

class ImageProcessing {
public:
    int FRAME_SIZE[4] = {
        0,
        0,
        640,
        305
    }; // the area of the bin in the image
    int FRAME_SIZE_SMALL[4] = {
        0,
        7,
        142,
        288
    }; // the area of the bin in the image
    int FRAME_SIZE_MEDIUM[4] = {
        148,
        0,
        405,
        305
    }; // the area of the bin in the image
    int FRAME_SIZE_BIG[4] = {
        571,
        0,
        69,
        302
    }; // the area of the bin in the image
    int small_area_upper_limit = 2000; //the limit of area of small contours is maximum 2000 pixels
    int small_area_lower_limit = 50; //the area of the small wells must be at least 100 pixels
    int medium_area_lower_limit = 300; //the area of the small wells must be at least 100 pixels
    int medium_area_upper_limit = 10000; //the limit of area of big contours is maximum 10000 pixels
    int medium_extra = 0; //variable for extra contours if two merge together
    bool show_img = false; // show images or not?
    bool write_img = false; //saved images as files?
    bool dark = true; //try another threshold if there is lack of light

    void calc_MPN(int big_wells, int small_wells) {
        vector < vector < float >> table;

        string line;
        fstream myfile("/home/momo/Documents/MPNCounter/table/MPN_table.dat");
        if (myfile.is_open()) {

            int counter = 0;
            while (getline(myfile, line)) {
                stringstream ss(line);
                float i;

                table.push_back(vector < float > ());

                while (ss >> i) {
                    table[counter].push_back(i);
                    if (ss.peek() == ' ')
                        ss.ignore();
                }
                counter += 1;
            }
            myfile.close();
        } else {
            cout << "Unable to open file" << endl;
        }
        cout << "Big wells: " << big_wells << endl;
        cout << "Small wells: " << small_wells << endl;
        cout << "MPN = " << table[big_wells][small_wells] << endl;
        mpn = static_cast < int > (table[big_wells][small_wells]);
    }
};
/*
 * Class:     MPN
 * Method:    main
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_MPN_getMPN(JNIEnv * , jobject) {
    return mpn;
}

/*
 * Class:     MPNJava_Sample1_main
 * Method:    main
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_MPN_main(JNIEnv * , jobject) {
    ImageProcessing object;
    VideoCapture cap;

    if (!cap.open(2))
        return 0;

    Mat image;
    cap >> image;

    image = image(Rect(object.FRAME_SIZE[0], object.FRAME_SIZE[1], object.FRAME_SIZE[2], object.FRAME_SIZE[3])); // cut off edges

    imwrite("/home/momo/Downloads/img.jpg", image);

    cvtColor(image, image, CV_BGR2HSV);
    //inRange(image, Scalar(18, 80, 0), Scalar(49, 255, 255), image); //original


    if (object.dark) inRange(image, Scalar(18, 63, 0), Scalar(49, 255, 255), image); //finds more (darker room)
    else inRange(image, Scalar(18, 105, 0), Scalar(49, 255, 255), image); //finds less

    //Make an image copy for the small wells
    Mat image_small = image;
    image_small = image(Rect(object.FRAME_SIZE_SMALL[0], object.FRAME_SIZE_SMALL[1], object.FRAME_SIZE_SMALL[2], object.FRAME_SIZE_SMALL[3])); // cut off edges

    //Make an image copy for the medium wells
    Mat image_medium = image;
    image_medium = image(Rect(object.FRAME_SIZE_MEDIUM[0], object.FRAME_SIZE_MEDIUM[1], object.FRAME_SIZE_MEDIUM[2], object.FRAME_SIZE_MEDIUM[3])); // cut off edges

    //Make an image copy for the big well
    Mat image_big = image;
    image_big = image(Rect(object.FRAME_SIZE_BIG[0], object.FRAME_SIZE_BIG[1], object.FRAME_SIZE_BIG[2], object.FRAME_SIZE_BIG[3])); // cut off edges

    /*
    	imshow("Ima", image);
    	imshow(" big wells", image_big);
    	imshow(" medium wells", image_medium);
    	imshow(" small wells", image_small);

    	imwrite( "/home/christoffer/P5/image_processing/pictures/report_rgb_small.jpg", image_small );
    	imwrite( "/home/christoffer/P5/image_processing/pictures/report_rgb_medium.jpg", image_medium );
    	imwrite( "/home/christoffer/P5/image_processing/pictures/report_rgb_big.jpg", image_big );
    	*/
    //imwrite( "/home/christoffer/P5/image_processing/pictures/testtest.jpg", image );


    //process big well
    Mat elem = getStructuringElement(MORPH_ELLIPSE, Size(15, 15));
    morphologyEx(image_big, image_big, MORPH_CLOSE, elem);
    Mat elem2 = getStructuringElement(MORPH_ELLIPSE, Size(9, 9));
    morphologyEx(image_big, image_big, MORPH_OPEN, elem2);

    //process medium wells
    Mat elem3 = getStructuringElement(MORPH_ELLIPSE, Size(7, 7));
    morphologyEx(image_medium, image_medium, MORPH_CLOSE, elem3);
    Mat elem4 = getStructuringElement(MORPH_ELLIPSE, Size(7, 7));
    //Mat elem4 = getStructuringElement(MORPH_ELLIPSE,Size(25,25)); //darker room
    morphologyEx(image_medium, image_medium, MORPH_OPEN, elem4);

    //	imshow("Imsda", image_medium);

    //process small wells
    Mat elem5 = getStructuringElement(MORPH_ELLIPSE, Size(7, 7));
    morphologyEx(image_small, image_small, MORPH_CLOSE, elem5);
    Mat elem6 = getStructuringElement(MORPH_ELLIPSE, Size(5, 5));
    morphologyEx(image_small, image_small, MORPH_OPEN, elem6);

    //imshow(" morph small wells", image_small);
    //imshow(" morph medium wells", image_medium);

    vector < vector < Point >> contours_big; // vector storing all found contours in image
    vector < vector < Point >> contours_medium; // vector storing all found contours in image
    vector < vector < Point >> contours_small; // vector storing all found contours in image

    // Find contours (input = image, output = contours):
    findContours(image_big, contours_big, RETR_EXTERNAL, CHAIN_APPROX_NONE);
    findContours(image_medium, contours_medium, RETR_EXTERNAL, CHAIN_APPROX_NONE);
    findContours(image_small, contours_small, RETR_EXTERNAL, CHAIN_APPROX_NONE);

    // make a black image for drawing the found contour
    Mat contour_filled_small = cv::Mat::zeros(image_small.size(), CV_8UC3);
    Mat contour_filled_medium = cv::Mat::zeros(image_medium.size(), CV_8UC3);
    Mat contour_filled_big = cv::Mat::zeros(image_big.size(), CV_8UC3);

    vector < int > area_small; //vector storing area of all contours of small wells
    vector < int > area_medium;
    vector < int > area_big;

    // Draw all found contours
    for (int i = 0; i < contours_small.size(); i++) {
        if (contourArea(contours_small[i]) > object.small_area_lower_limit) { //the area must be at least 50
            drawContours(contour_filled_small, contours_small, i, Scalar(0, 0, 255), -1);
            area_small.push_back(contourArea(contours_small[i]));
        }
    }
    for (unsigned int i = 0; i < contours_medium.size(); i++) {
        if (contourArea(contours_medium[i]) > object.medium_area_lower_limit) { //the area must be at least 300
            drawContours(contour_filled_medium, contours_medium, i, Scalar(0, 0, 255), -1);
            area_medium.push_back(contourArea(contours_medium[i]));

            if (contourArea(contours_medium[i]) > 2200 && contourArea(contours_medium[i]) < 4000) { //if two are merged together we count them as two
                object.medium_extra += 1;
            }
        }
        /*
        else{
        		contours_medium.erase(contours_medium.begin() + i);  //remove the contour from the vector since it is too small to be verified
        }
        */
    }


    for (unsigned int i = 0; i < contours_big.size(); i++) {
        drawContours(contour_filled_big, contours_big, i, Scalar(0, 0, 255), -1);
        area_big.push_back(contourArea(contours_big[i]));
    }

    //find the biggest area of each size of wells
    sort(area_small.begin(), area_small.end(), greater < int > ());
    sort(area_medium.begin(), area_medium.end(), greater < int > ());
    sort(area_big.begin(), area_big.end(), greater < int > ());
/*
    cout << "Sorted small\n";
    for (auto x: area_small)
        cout << x << " ";

    cout << "Sorted medium\n";
    for (auto x: area_medium)
        cout << x << " ";
*/

	if(area_small.size() < 1 || area_medium.size() < 1) {
		mpn = -1;
		return 0;
	}


    //check:
    if (area_small[0] > object.small_area_upper_limit || area_medium[0] > object.medium_area_upper_limit) { //areas: 1151, 5384
        cout << "Contours are too big. Try with a new threshold" << endl;
        return 0;
    } else if (contours_medium.size() > 50 || contours_small.size() > 50) {
        cout << "Found too many contours. Try with a new threshold" << endl;
        return 0;
    } else if (contours_medium.size() < 10 || contours_small.size() < 10) {
        cout << "Invalid results" << endl;
		mpn = -1;
        return 0;
    } else {
		cout << "test" << endl;
        int big_wells = contours_big.size();

        if (big_wells > 1) { //the maximum number of this type is one. If it finds more it should just be count as one.
            big_wells = 1;
        }

        big_wells += area_medium.size() + object.medium_extra; //if two are merged together we need to add one extra
        int small_wells = area_small.size();

        // following two if statements are implemented if the program finds 1 or 2 wells more than the maximum. If this happens we consider all the wells to be yellow.
        if (48 < small_wells && small_wells < 51) {
            small_wells = 48;
            cout << "Found 1 or 2 extra small wells. The amount is changed to 48" << endl;
        }
        if (49 < big_wells && big_wells < 52) {
            big_wells = 49;
            cout << "Found 1 or 2 extra big wells. The amount is changed to 49" << endl;
        }

        //cout the MPN
        object.calc_MPN(big_wells, small_wells);

        if (object.show_img) {
            imshow("Image big well", contour_filled_big);
            imshow("Image medium wells", contour_filled_medium);
            imshow("Image small wells", contour_filled_small);
        }

        if (object.write_img) {
            imwrite("/home/christoffer/P5/image_processing/pictures/hsv_threshold.jpg", image);
            imwrite("/home/christoffer/P5/image_processing/pictures/morph_big.jpg", contour_filled_big);
            imwrite("/home/christoffer/P5/image_processing/pictures/morph_medium.jpg", contour_filled_medium);
            imwrite("/home/christoffer/P5/image_processing/pictures/morph_small.jpg", contour_filled_small);
        }
    }

    //waitKey(0);
}
