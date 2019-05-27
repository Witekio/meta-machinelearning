SUMMARY = "CLBlast"
DESCRIPTION = "A modern, lightweight, performant and tunable OpenCL BLAS library written in C++11."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=aeb40f7c58956a1eb8441f0b51f900bb"

#    file://0001-Reduce-default-WGS3-and-WPT3-sizes.patch

SRC_URI = " \
    git://github.com/CNugteren/CLBlast.git \
"

SRCREV = "1cbe2ea301c6b28a7d1101142ff347471f7dc197"

S = "${WORKDIR}/git"

inherit cmake

CLBLAST_BUILD_OPTIONS = "-cl-mad-enable -cl-unsafe-math-optimizations -cl-fast-relaxed-math"

DEPENDS += "imx-gpu-viv"

CXXFLAGS += "--std=c++11 -O3 -DNDEBUG -DEGL_API_FB -DGPU_TYPE_VIV -DGL_GLEXT_PROTOTYPES -DENABLE_GPU_RENDER_20"
LDFLAGS += "-lm -lOpenCL -lCLC -ldl -lpthread"

FILES_${PN}-dev += "${libdir}/cmake"
