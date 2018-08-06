#include <jni.h>
#include <opencv2/opencv.hpp>
#include <stdio.h>

using namespace cv;
using namespace std;
/* Header for class com_example_android_opencvdemo_OpenCvMaker */

#ifndef _Included_com_example_android_opencvdemo_OpenCvMaker
#define _Included_com_example_android_opencvdemo_OpenCvMaker
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_example_android_opencvdemo_OpenCvMaker
 * Method:    makeGray
 * Signature: (JJ)I
 */
   int toGray(Mat input, Mat& output);
JNIEXPORT jint JNICALL Java_com_example_android_opencvdemo_OpenCvMaker_makeGray
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_example_android_opencvdemo_OpenCvMaker
 * Method:    makeCanny
 * Signature: (JJ)I
 */
  int toCanny(Mat input, Mat& output);
  JNIEXPORT jint JNICALL Java_com_example_android_opencvdemo_OpenCvMaker_makeCanny
    (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_example_android_opencvdemo_OpenCvMaker
 * Method:    makeDilate
 * Signature: (JJ)I
 */
  int toDilate(Mat input, Mat& output);
  JNIEXPORT jint JNICALL Java_com_example_android_opencvdemo_OpenCvMaker_makeDilate
    (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_example_android_opencvdemo_OpenCvMaker
 * Method:    makeErode
 * Signature: (JJ)I
 */
  int toErode(Mat input, Mat& output);
  JNIEXPORT jint JNICALL Java_com_example_android_opencvdemo_OpenCvMaker_makeErode
    (JNIEnv *, jclass, jlong, jlong);


#ifdef __cplusplus
}
#endif
#endif
