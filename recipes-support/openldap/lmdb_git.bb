SUMMARY = "LMDB library"
DESCRIPTION = "Lightning Memory-Mapped Database (LMDB): an embeddable, open-source, high-performance database library"

LICENSE = "OpenLDAP"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=153d07ef052c4a37a8fac23bc6031972"

SRC_URI = " \
    git://git.openldap.org/openldap.git;branch=mdb.master \
    file://makefile.patch \
"

SRCREV = "4d2154397afd90ca519bfa102b2aad515159bd50"

S = "${WORKDIR}/git/libraries/liblmdb"

TARGET_CC_ARCH += "-pthread"

do_compile() {
    oe_runmake "CC=${CC}"
}

do_install() {
    oe_runmake DESTDIR=${D} install
}
