inherit cargo

DESCRIPTION = "tract Tensorflow and ONNX inference engine"
LICENSE = "Apache-2.0 & MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=d2da9950b1ad90637e485e363862ee3d"

SRC_URI[md5sum] = "36af340c00e60291931cb30ce32d4e86"
SRC_URI[sha256sum] = "d32432d28673a936b2d6281ab0600c71cf7226dfe4cdcef3012555f691744166"

SRC_URI = " \
        git://github.com/snipsco/tract.git;protocol=https \
        file://0001-change-example-frozen.pb-to-targeted-one.patch \
        file://0002-add-time-tracker.patch \
        https://download.tensorflow.org/models/mobilenet_v1_2018_08_02/mobilenet_v1_1.0_224_quant.tgz \
"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

CARGO_PATH = "${TOPDIR}/tmp/work/x86_64-poky-linux-gnueabi/cargo-bin-cross-arm/1.34.0-r0/image/${TOPDIR}/tmp/sysroots/x86_64-linux/usr/bin:"
RUSTC_PATH = "${TOPDIR}/tmp/work/x86_64-poky-linux-gnueabi/rust-bin-cross-arm/1.34.0-r0/image/${TOPDIR}/tmp/sysroots/x86_64-linux/usr/bin:"

INHIBIT_PACKAGE_DEBUG_SPLIT = '1' 

do_compile_prepend(){
        #add cargo and rustc to PATH, related to issue 48 at rust-embedded/meta-rust-bin on github
        export PATH="${CARGO_PATH}${RUSTC_PATH}${PATH}" 
}

do_install_append(){
        install -d ${D}${bindir}/tract-examples
        install --target-directory=${D}${bindir}/tract-examples -m 0666 ${WORKDIR}/mobilenet_v1_1.0_224_quant_frozen.pb
        install --target-directory=${D}${bindir}/tract-examples -m 0666 ${S}/examples/tensorflow-mobilenet-v2/grace_hopper.jpg 
        install --target-directory=${D}${bindir}/tract-examples  -m 0666 ${S}/examples/tensorflow-mobilenet-v2/imagenet_slim_labels.txt
        cp ${D}${bindir}/tract-mobilenet-v2-example ${D}${bindir}/tract-examples
}  

FILES_${PN} += "${bindir}/tract \
        ${bindir}/tract-examples \
        ${bindir}/tract-examples/grace_hopper.jpg \
        ${bindir}/tract-examples/mobilenet_v1_1.0_224_quant_frozen.pb \
        ${bindir}/tract-examples/imagenet_slim_labels.txt \
        ${bindir}/tract-mobilenet-v2-example \
"
