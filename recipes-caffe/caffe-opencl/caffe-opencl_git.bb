SUMMARY = "Caffe"
DESCRIPTION = "Caffe is a deep learning framework made with expression, speed, and modularity in mind."
HOMEPAGE = "http://caffe.berkeleyvision.org"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=91d560803ea3d191c457b12834553991"

SRC_URI = "git://github.com/BVLC/caffe.git;branch=opencl \
    file://0001-Allow-setting-numpy-include-dir-from-outside.patch \
    file://0002-cmake-find-Yocto-boost-python-libs.patch \
    file://0003-cmake-fix-RPATHS.patch \
    file://0004-io-change-to-imageio.patch \
    file://0005-classify-demo-added-a-demo-app-for-classifying-image.patch \
"

SRCREV = "3f2b97e93ed5ab612b6d00995294e37a422f0931"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DCMAKE_INSTALL_RPATH_USE_LINK_PATH=FALSE -DUSE_ARM_CROSS_COMPILE=TRUE -DUSE_INTEL_SPATIAL=OFF -DUSE_ISAAC=OFF \
                  -DUSE_HDF5=OFF -DUSE_CLBLAST=ON -DBUILD_SHARED_LIBS=ON -DUSE_CUDNN=OFF -DBUILD_docs=OFF \
                  -DBUILD_python=ON -DBUILD_matlab=OFF -DBLAS=open -DUSE_LEVELDB=OFF -DUSE_CLBLAS=OFF \
                  -DPYTHON_NUMPY_INCLUDE_DIR=${STAGING_DIR_TARGET}/usr/lib/python3.5/site-packages/numpy/core/include \
                  -DPYTHON_EXECUTABLE=${STAGING_BINDIR_NATIVE}/python3-native/python3 \
                  -DPYTHON_INCLUDE_DIRS=${STAGING_INCDIR_TARGET}/python3-native/python3.5m \
                  -DPYTHON_LIBRARIES=${STAGING_LIBDIR_TARGET}/python3.5 \
                  "

DEPENDS += "imx-gpu-viv \
            viennacl \
            boost \
            clblast \
            glog \
            gflags \
            protobuf \
            protobuf-native \
            lmdb  \
            opencv \ 
            openblas \
            snappy \
            leveldb \
            boost\
            boost-native\
            python3 \
            python3-native \
            python3-numpy \
            python3-numpy-native \
            "

inherit python3native

RDEPENDS_${PN} = " \
    python3 \
    python3-imageio \
    python3-six \
    python3-protobuf \
    python3-numpy \
    python3-pillow \
"

PACKAGES += "\
    ${PN}-python \
"


CXXFLAGS += "--std=c++11 -O3 -DNDEBUG -DEGL_API_FB -DGPU_TYPE_VIV -DGL_GLEXT_PROTOTYPES -DENABLE_GPU_RENDER_20"
LDFLAGS += "-lm -lOpenCL -lCLC -ldl -lpthread"

do_configure_append() {
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}/scripts/prebuild/macros.py ${S}/include/caffe/
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}/scripts/prebuild/quantizer_creator.py ${S}/include/caffe/
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}/scripts/prebuild/blob_creator.py ${S}/include/caffe/
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} ${S}/scripts/prebuild/layer_creator.py ${S}/include/caffe/
} 
FILES_${PN}-dev += " \
    ${datadir} \
"

FILES_${PN}-python += " \
    ${prefix}/python \
"
