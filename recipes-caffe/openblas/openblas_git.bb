SUMMARY = "OpenBLAS"
DESCRIPTION = "OpenBLAS is an optimized linear algebra library."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5adf4792c949a00013ce25d476a2abc0"

SRC_URI =  " \
    git://github.com/xianyi/OpenBLAS.git;branch=develop \
    file://0001-c_check-Remove-compiler-options-before-extracting-di.patch \
"

SRCREV = "42466e54fa19ee0d24648b1ca6410062f3494445"

S = "${WORKDIR}/git"

# Attempt to set OPENBLAS_TARGET based on TUNE_FEATURES
python __anonymous() {
    target = d.getVar("OPENBLAS_TARGET", True)

    # Do nothing if the target is set
    if target:
        return

    # Target maps in ascending order
    tgt = [
        # ARM
        "ARMV5", "ARMV6", "ARMV7", "CORTEXA7", "CORTEXA9", "CORTEXA15",
        # AARCH64
        "ARMV8", "CORTEXA57"
    ]

    dic = {
        # ARM
        "ARMV5": { "armv5" },
        "ARMV6" : { "armv6" },
        "ARMV7": { "armv7" },
        "CORTEXA7": { "cortexa7" },
        "CORTEXA9": { "cortexa9" },
        "CORTEXA15": { "cortexa15", "cortexa15-cortexa7" },
        # AARCH64
        "ARMV8": { "aarch64" },
        "CORTEXA57": { "cortexa57", "cortexa57-cortexa53" },
    }

    tune = set(d.getVar("TUNE_FEATURES", True).split())
    for t in tgt:
        if tune & dic[t]:
            target = t

    d.setVar("OPENBLAS_TARGET", "ARMV7")
}

do_compile() {
    oe_runmake HOSTCC=${BUILD_CC} NOFORTRAN=1 TARGET=${OPENBLAS_TARGET}
}

do_install() {
    oe_runmake install DESTDIR=${D} PREFIX=${prefix}
    # Remove empty directories
    rm -fd ${D}${bindir}
}

FILES_${PN} = " \
    ${bindir} \
    ${libdir}/libopenblas_*.so \
    ${libdir}/libopenblas*.so.* \
"

FILES_${PN}-dev = " \
    ${libdir}/libopenblas.so \
    ${libdir}/cmake \
    ${libdir}/pkgconfig \
    ${includedir} \
"
