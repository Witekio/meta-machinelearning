DESCRIPTION = "TensorFlow Lite C++ Library"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=01e86893010a1b87e69a213faa753ebd"

SRCREV = "656e7a2b347c3c6eb76a6c130ed4b1def567b6c1"

SRC_URI[md5sum] = "ad2ba2089114cf03a5b8189bc4c09c59"
SRC_URI[sha256sum] = "23f814d1c076bdf03715dfb6cab3713aa4fbdf040fd5448c43196bd2e97a4c1b"

SRC_URI = " \
	git://github.com/tensorflow/tensorflow.git;branch=r1.10 \
	file://0001-creating-a-new-Makefile-that-triggers-Makefile.inter.patch \
	file://0001-Tailor-our-own-Makefile-for-arm-cross-compilation.patch \
	file://0001-Fix-compilation-error-when-compiling-benchmark_model.patch \
	file://0001-Check-NEON-support.patch \
	file://0001-Curl-no-certificates-check.patch \
	https://storage.googleapis.com/download.tensorflow.org/models/tflite/mobilenet_v1_224_android_quant_2017_11_08.zip \
"

S = "${WORKDIR}/git"

PACKAGES += "${PN}-examples ${PN}-examples-dbg"

do_configure(){
	export HTTP_PROXY=${HTTP_PROXY}
	export HTTPS_PROXY=${HTTPS_PROXY}

	${S}/tensorflow/contrib/lite/download_dependencies.sh
}

DEPENDS += "curl-native unzip-native"
RDEPENDS_${PN}-native += "curl-native unzip-native"

CXXFLAGS += "--std=c++11"
FULL_OPTIMIZATION += "-O3 -DNDEBUG"

do_install(){
	install -d ${D}${libdir}
	cp -r \
		${S}/tensorflow/contrib/lite/gen/lib/* \
		${D}${libdir}

	install -d ${D}${includedir}/tensorflow_lite
	cd ${S}/tensorflow/contrib/lite
	cp --parents \
		$(find . -name "*.h*") \
		${D}${includedir}/tensorflow_lite

	install -d ${D}${bindir}/${PN}-${PV}/examples
	install -m 0555 \
		${S}/tensorflow/contrib/lite/gen/bin/label_image \
		${D}${bindir}/${PN}-${PV}/examples
	install -m 0555 \
		${S}/tensorflow/contrib/lite/examples/label_image/testdata/grace_hopper.bmp \
		${D}${bindir}/${PN}-${PV}/examples
	install -m 0555 \
                ${S}/tensorflow/contrib/lite/gen/bin/minimal \
                ${D}${bindir}/${PN}-${PV}/examples
  install -m 0555 \
          ${S}/tensorflow/contrib/lite/gen/bin/benchmark_model \
          ${D}${bindir}/${PN}-${PV}/examples
	install -m 0555 \
          ${WORKDIR}/mobilenet_quant_v1_224.tflite \
          ${D}${bindir}/${PN}-${PV}/examples
	install -m 0555 \
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

FILES_${PN}-dbg = " \
	/usr/src/debug/tensorflow-lite \
"

FILES_${PN}-examples = " \
	${bindir}/${PN} \
	${bindir}/${PN}-${PV}/examples/label_image \
	${bindir}/${PN}-${PV}/examples/grace_hopper.bmp \
	${bindir}/${PN}-${PV}/examples/minimal \
	${bindir}/${PN}-${PV}/examples/benchmark_model \
	${bindir}/${PN}-${PV}/examples/mobilenet_quant_v1_224.tflite \
	${bindir}/${PN}-${PV}/examples/labels.txt \
"

FILES_${PN}-examples-dbg = " \
	${bindir}/${PN}-${PV}/examples/.debug \
"
