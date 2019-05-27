SUMMARY = "Open Deep Learning Compiler Stack"
DESCRIPTION = "TVM is a compiler stack for deep learning systems. It is designed to close the gap between the productivity-focused deep learning frameworks, and the performance- and efficiency-focused hardware backends. "
LICENSE = "Apache-2.0"
# Apache-2.0 license applies to mobilenet tarball
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

SRCREV = "f08015e7fde92c835907d4c9b7ad6d3f634e94a5"

require machine-generic.conf
include machine-${MACHINE}.conf

S = "${WORKDIR}/git"

inherit cmake python3native

SETUPTOOLS_INSTALL_ARGS = "--root=${D} \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${libdir} \
    --install-scripts=${PYTHON_SITEPACKAGES_DIR}"

DEPENDS += " \
    zlib \
    ncurses \
    python3 \
    python3-native \
    python3-setuptools-native \
    python3-cython-native \
"

RDEPENDS_${PN} += " \
    python3 \
    python3-numpy \
    python3-decorator \
    python3-tornado \
    python3-pillow \
    packagegroup-core-buildessential \
"

SRC_URI += " \
    gitsm://github.com/dmlc/tvm.git;branch=v${PV};name=tvm \
    file://0001-generate-versioned-library.patch \
    file://0001-fix-path-setuptools.patch \
"

EXTRA_OEMAKE += "'CXX=${CXX}' 'CC=${CC}' 'AR=${AR}' 'CXXFLAGS=${CXXFLAGS}' 'CFLAGS=${CFLAGS}'"


do_compile() {
    cmake --build '${B}' --target runtime -- ${EXTRA_OECMAKE_BUILD}
}

do_install_append() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    install -d ${S}/build
    cp ${WORKDIR}/build/*.so ${S}/build
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    cp -rf ${WORKDIR}/image/${includedir} ${STAGING_INCDIR}/..
    cp -rf ${S}/3rdparty/dlpack/include/ ${STAGING_INCDIR}/..
    cp -rf ${S}/include/ ${STAGING_INCDIR}/..
    export STAGING_INCDIR=${STAGING_INCDIR}
    export STAGING_LIBDIR=${STAGING_LIBDIR}
    export PYTHONPATH=${D}${PYTHON_SITEPACKAGES_DIR}
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}/python/setup.py install ${SETUPTOOLS_INSTALL_ARGS}
    cp -rf ${S}/python/tvm ${D}${PYTHON_SITEPACKAGES_DIR}
    rm -rf ${D}${libdir}/tvm
} 

FILES_${PN} += "${WORKDIR}/build/*.so"
FILES_${PN}-dev += "${libdir}/python3.5/site-packages/*"
INSANE_SKIP_${PN}-dev = "dev-elf"



