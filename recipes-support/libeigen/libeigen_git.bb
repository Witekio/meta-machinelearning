DESCRIPTION = "Eigen is a C++ template library for linear algebra: matrices, vectors, numerical solvers, and related algorithms."
AUTHOR = "Benoît Jacob and Gaël Guennebaud and others"
HOMEPAGE = "http://eigen.tuxfamily.org/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING.MPL2;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "https://bitbucket.org/eigen/eigen/get/20cbc6576426.tar.gz;downloadfilename=20cbc6576426.tar.gz"
SRC_URI[sha256sum] = "74845ea27e19a1bcf63f3f271de62e06798f23e0467bb9d45b83a94918941b23"

S = "${WORKDIR}/eigen-eigen-20cbc6576426"

inherit cmake

FILES_${PN} = "${libdir}"
FILES_${PN}-dev = "${includedir} ${datadir}/eigen3/cmake ${datadir}/cmake/Modules ${datadir}/pkgconfig"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
