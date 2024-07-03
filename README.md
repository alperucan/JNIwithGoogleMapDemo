This project utilizes the Google Maps API to prompt the user for two different locations, extracting their longitude and latitude coordinates.
These coordinates are then passed to a C++ method via JNI (Java Native Interface).
Using C++, the method calculates the straight-line distance between the two points using the Haversine formula, yielding the distance in kilometers.
