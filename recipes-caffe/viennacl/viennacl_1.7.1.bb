SUMMARY = "ViennaCL"
DESCRIPTION = "ViennaCL is a free open-source linear algebra library for computations on many-core architectures."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI =  " \
    http://downloads.sourceforge.net/project/viennacl/1.7.x/ViennaCL-${PV}.tar.gz \
"

SRC_URI[md5sum] = "00939858309689d32247d6fe60383b88"
SRC_URI[sha256sum] = "a596b77972ad3d2bab9d4e63200b171cd0e709fb3f0ceabcaf3668c87d3a238b"

S = "${WORKDIR}/ViennaCL-${PV}"

inherit cmake

DEPENDS += "imx-gpu-viv"

CXXFLAGS += "--std=c++11 -O3 -DNDEBUG -DEGL_API_FB -DGPU_TYPE_VIV -DGL_GLEXT_PROTOTYPES -DENABLE_GPU_RENDER_20"
LDFLAGS += "-lm -lOpenCL -lCLC -ldl -lpthread"

#do_compile_append() {
#    oe_runmake
#}

do_install_append() {
    install -d ${D}${bindir}
    install ${WORKDIR}/build/examples/tutorial/viennacl-info ${D}${bindir}
}

FILES_${PN}-dev += " \
    ${libdir}/cmake \
"
