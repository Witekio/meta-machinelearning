# meta-machinelearning

This Yocto/OpenEmbedded machine-learning meta-layer provides support for machine learning libraries:

# TensorFlow Lite: 
The TensorFlow Lite (https://www.tensorflow.org/mobile/tflite/) recipe for the FullMetalUpdate project and is an evolution of the layer developped for the RZ/G1 family of System on Chips:
https://github.com/renesas-rz/meta-renesas-ai

In order to add TensorFlow Lite support to your project, make sure
*tensorflow-lite* is listed as a dependency to your recipe/package.
Listing *tensorflow-lite-staticdev* and *tensorflow-lite-dev* in *IMAGE\_INSTALL*
variable could be beneficial when you just want to populate an SDK for
developing an application based on TensorFlow Lite.

After the build is complete the static C++ TensorFlow Lite library
(*libtensorflow-lite.a*) will be generated.

The library can be verified with the TensorFlow Lite image classification sample
application named *label_image* which is included in the build (included by
package *tensorflow-lite-examples*). The sample application is installed under
*/usr/bin/tensorflow-lite/examples/*.

# tract:
tract is a tensorflow- and ONNX- compatible inference library. It loads a Tensorflow or ONNX frozen model from the regular protobuf format, and flows data through it.

In order to use tract for test pupose, we created a recipe that will fetch and build the tract example wrote in rust using the MobileNet v1 224.

Make sure that *tract* is listed as a dependency, you will able to find the example folder *tract-examples* under */usr/bin/tract-examples/*.

Within this folder you will find the needed files to run the example

	* frozen model from the regular protobuf format
	* .png of Grace Hopper
	* .txt holding the labels

Simply run the executable and you should be able to see the result *time and most accurate*.
