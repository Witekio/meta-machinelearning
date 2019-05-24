DESCRIPTION = "TensorFlow Lite C++ Library"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=98c6df387846c9077433242544c8f127"

SRCREV_tensorflow = "f78b725d10e1386b614621465810b9e79558bd08"
SRCREV_gemmlowp = "36212ad3651871bc3e9a599f1a6d5324778aea25"
SRCREV_absl = "fcb104594b0bb4b8ac306cb2f55ecdad40974683"
SRCREV_farmhash = "c0fb8db717e10f1634008842731e52c63cc3ead3"
SRCREV_FP16 = "febbb1c163726b5db24bed55cc9dc42529068997"

SRC_URI[mobilenet_v1_0_25_224_quant.md5sum] = "52fa23a37a02f8fd76ec979afca3b26a"
SRC_URI[mobilenet_v1_0_25_224.md5sum] = "b21d34a0b1c86118d9ce20ae5f5a0cc3"
SRC_URI[mobilenet_v1_1_0_224_quant.md5sum] = "36af340c00e60291931cb30ce32d4e86"
SRC_URI[mobilenet_v1_1_0_224.md5sum] = "d5f69cef81ad8afb335d9727a17c462a"
SRC_URI[mobilenet_labels.md5sum] = "ad2ba2089114cf03a5b8189bc4c09c59"

SRC_URI[fft.md5sum] = "4255dd8a74949d123216b1ab91520469"

MAKE_DIR = "tensorflow/lite/tools/make"

require machine-generic.conf
include machine-${MACHINE}.conf

SRC_URI += " \
	git://github.com/tensorflow/tensorflow.git;branch=r${PV};name=tensorflow \
	git://github.com/google/gemmlowp.git;name=gemmlowp;destsuffix=gemmlowp \
	git://github.com/abseil/abseil-cpp.git;branch=lts_2018_12_18;name=absl;destsuffix=absl  \
	git://github.com/google/farmhash.git;name=farmhash;destsuffix=farmhash \
	git://github.com/Maratyszcza/FP16.git;name=FP16;destsuffix=FP16 \
	http://mirror.tensorflow.org/www.kurims.kyoto-u.ac.jp/~ooura/fft.tgz;name=fft;destsuffix=fft \
	https://storage.googleapis.com/download.tensorflow.org/models/tflite/mobilenet_v1_224_android_quant_2017_11_08.zip;name=mobilenet_labels \
	http://download.tensorflow.org/models/mobilenet_v1_2018_08_02/mobilenet_v1_0.25_224_quant.tgz;name=mobilenet_v1_0_25_224_quant \
	http://download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_0.25_224.tgz;name=mobilenet_v1_0_25_224 \
	http://download.tensorflow.org/models/mobilenet_v1_2018_08_02/mobilenet_v1_1.0_224_quant.tgz;name=mobilenet_v1_1_0_224_quant \
	http://download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_1.0_224.tgz;name=mobilenet_v1_1_0_224 \
	file://Makefile \
"

S = "${WORKDIR}/git"

PACKAGES += "${PN}-examples"

BBCLASSEXTEND = "native nativesdk"

DEPENDS += " \
			flatbuffers-native \
    		flatbuffers \
			libeigen \
			gtest \
			"

RDEPENDS_${PN}-dev = "${PN}-staticdev"
EXTRA_OEMAKE += "'STAGING_DIR_HOST=${STAGING_DIR_HOST}' 'WORKDIR=${WORKDIR}' 'CXX=${CXX}' 'CC=${CC}' 'AR=${AR}' 'CXXFLAGS=${CXXFLAGS}' 'CFLAGS=${CFLAGS}'"

do_configure(){
	install -m 0750 ${WORKDIR}/Makefile ${S}/
	install -m 0750 ${WORKDIR}/${MAKEFILE_TF} ${S}/tensorflow/lite/arm_makefile.inc
}

do_install(){
	install -d ${D}${libdir}
	cp -r \
		${S}/tensorflow/lite/gen/lib/* \
		${D}${libdir}
	
	install -d ${D}${includedir}/tensorflow_lite
	cd ${S}/tensorflow/lite
	cp --parents \
		$(find . -name "*.h*") \
		${D}${includedir}/tensorflow_lite

	install -d ${D}${includedir}/tensorflow_lite
	cd ${S}/tensorflow/lite
	cp --parents \
		$(find . -name "*.h*") \
		${D}${includedir}/tensorflow_lite

	install -d ${D}${bindir}/${PN}-${PV}/examples
	for example in ${S}/tensorflow/lite/gen/bin/*
	do
		install -m 0555 $example ${D}${bindir}/${PN}-${PV}/examples
	done

	for example in ${WORKDIR}/*.tflite 
	do
		install -m 0750 $example ${D}${bindir}/${PN}-${PV}/examples
	done

	install -m 0750 \
		${S}/tensorflow/lite/examples/label_image/testdata/grace_hopper.bmp \
		${D}${bindir}/${PN}-${PV}/examples
	
	install -m 0750 \
          ${WORKDIR}/labels.txt \
          ${D}${bindir}/${PN}-${PV}/examples
	cd ${D}${bindir}
	ln -sf ${PN}-${PV} ${PN}
}

ALLOW_EMPTY_${PN} = "1"

FILES_${PN} = ""

FILES_${PN}-dev = " \
	${includedir} \
"

FILES_${PN}-staticdev = " \
	${libdir} \
"

FILES_${PN}-examples = " \
	${bindir}/${PN} \
	${bindir}/${PN}-${PV}/examples/label_image \
	${bindir}/${PN}-${PV}/examples/grace_hopper.bmp \
	${bindir}/${PN}-${PV}/examples/minimal \
	${bindir}/${PN}-${PV}/examples/benchmark_model \
	${bindir}/${PN}-${PV}/examples/mobilenet_v1_0.25_224.tflite \
	${bindir}/${PN}-${PV}/examples/mobilenet_v1_0.25_224_quant.tflite \
	${bindir}/${PN}-${PV}/examples/mobilenet_v1_1.0_224.tflite \
	${bindir}/${PN}-${PV}/examples/mobilenet_v1_1.0_224_quant.tflite \
	${bindir}/${PN}-${PV}/examples/mobilenet_quant_v1_224.tflite \
	${bindir}/${PN}-${PV}/examples/labels.txt \
"
