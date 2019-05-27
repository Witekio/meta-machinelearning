SUMMARY = "GFlags library"
DESCRIPTION = "The gflags package contains a C++ library that implements command line flags processing"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/COPYING.txt;md5=c80d1a3b623f72bb85a4c75b556551df"

SRC_URI = "git://github.com/gflags/gflags.git"
SRCREV = "4a694e87361d08eff5c4c9e9f551b1a0d41f7c40"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_TESTING=OFF -DBUILD_SHARED_LIBS=ON -DINSTALL_SHARED_LIBS=ON -DINSTALL_HEADERS=ON -DREGISTER_INSTALL_PREFIX=OFF"

do_install_append() {
    rm -f ${D}${bindir}/gflags_completions.sh
    rm -fd ${D}${bindir}
}

FILES_${PN}-dev += " \
    ${libdir}/cmake \
"
