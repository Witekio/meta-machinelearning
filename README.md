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
tract (https://github.com/snipsco/tract/blob/master/README.md) is a tensorflow- and ONNX- compatible inference library.
It loads a Tensorflow or ONNX frozen model from the regular protobuf format, and flows data through it.

In order to use tract for testing purpose, we created a recipe that will fetch and build the tract example, wrote in rust and using the MobileNet v1 224.

The target used is a nitrogen6x, yocto version fido, host machine Ubuntu 16.04

As most of tract is wrote in rust, you will need to add the layer meta-rust-bin (https://github.com/rust-embedded/meta-rust-bin) within your project.
Don't forget to update your bblayers.conf.

Please note,
There is open issue(#48) on the meta-rust-bin github, regarding cargo and rustc not properly updated in the PATH variable.
You can find in the file *tract_1.0.0.bb* the *do_compile_prepend()* that export the PATH variable regarding the above information.
You will need to update this PATH regarding the built targeted cargo and rust on your host machine.


Make sure that *tract* is listed as a dependency to your recipe/package, once build, you will be able to find the example folder *tract-examples* under */usr/bin/*.

Within this folder you will find the needed files to run the example

	* frozen model from the regular protobuf format
	* .png of Grace Hopper
	* .txt holding the labels

Simply run the executable and you should be able to see the result *time and most accurate*.

How it works ?

Simply put, our recipe inherit from cargo a .bbclass from the layer meta-rust-bin providing a pre-built toolchains that will allow us to build tract.
