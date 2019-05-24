SUMMARY = "Tensorflow lite protobuf files - used in ARMNN for Tensorflow lite network models"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=98c6df387846c9077433242544c8f127"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${PV} \
	https://storage.googleapis.com/download.tensorflow.org/models/tflite/mobilenet_v1_224_android_quant_2017_11_08.zip;name=mobilenet_labels \
	http://download.tensorflow.org/models/mobilenet_v1_2018_08_02/mobilenet_v1_0.25_224_quant.tgz;name=mobilenet_v1_0_25_224_quant \
	http://download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_0.25_224.tgz;name=mobilenet_v1_0_25_224 \
	http://download.tensorflow.org/models/mobilenet_v1_2018_08_02/mobilenet_v1_1.0_224_quant.tgz;name=mobilenet_v1_1_0_224_quant \
	http://download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_1.0_224.tgz;name=mobilenet_v1_1_0_224 \
"

SRCREV = "f78b725d10e1386b614621465810b9e79558bd08"
SRC_URI[mobilenet_v1_0_25_224_quant.md5sum] = "52fa23a37a02f8fd76ec979afca3b26a"
SRC_URI[mobilenet_v1_0_25_224.md5sum] = "b21d34a0b1c86118d9ce20ae5f5a0cc3"
SRC_URI[mobilenet_v1_1_0_224_quant.md5sum] = "36af340c00e60291931cb30ce32d4e86"
SRC_URI[mobilenet_v1_1_0_224.md5sum] = "d5f69cef81ad8afb335d9727a17c462a"
SRC_URI[mobilenet_labels.md5sum] = "ad2ba2089114cf03a5b8189bc4c09c59"

S = "${WORKDIR}/git"

do_install() {
    # Install sources + build artifacts as reuired by ARMNN
    install -d ${D}${datadir}/${BPN}

    #Copy the generated scheme
    install -m 0555 ${S}/tensorflow/lite/schema/schema_generated.h ${D}${datadir}/${BPN}
    install -m 0555 ${S}/tensorflow/lite/schema/schema.fbs ${D}${datadir}/${BPN}

    #Copy data use for test the model
    for example in ${WORKDIR}/*.tflite 
	do
		install -m 0555 $example ${D}${datadir}/${BPN}
	done
    install -m 0555 ${WORKDIR}/labels.txt ${D}${datadir}/${BPN}
}
