#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_mapdemo_MainActivity_getDistanceFromNative(JNIEnv *env, jobject thiz, jdouble lat1,
                                                            jdouble lon1, jdouble lat2,
                                                            jdouble lon2) {
    const double R = 6371.0; // Radius of the earth in km
    double dLat = (lat2 - lat1) * (M_PI / 180.0);  // deg2rad
    double dLon = (lon2 - lon1) * (M_PI / 180.0);  // deg2rad
    double a = sin(dLat/2) * sin(dLat/2) +
               cos(lat1 * (M_PI / 180.0)) * cos(lat2 * (M_PI / 180.0)) *
               sin(dLon/2) * sin(dLon/2);
    double c = 2 * atan2(sqrt(a), sqrt(1-a));
    double d = R * c; // Distance in km
    return d;
}