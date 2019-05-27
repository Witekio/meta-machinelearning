# Find where we're running from, so we can store generated files here.
ifeq ($(origin MAKEFILE_DIR), undefined)
	MAKEFILE_DIR := $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
endif

# Where compiled objects are stored.
OBJDIR := $(MAKEFILE_DIR)/gen/obj/
BINDIR := $(MAKEFILE_DIR)/gen/bin/
LIBDIR := $(MAKEFILE_DIR)/gen/lib/
GENDIR := $(MAKEFILE_DIR)/gen/obj/

# Settings for the host compiler.
CXX ?= $(TOOLCHAIN_PATH)/bin/$(CC_PREFIX)-gcc
CC ?= $(TOOLCHAIN_PATH)/bin/$(CC_PREFIX)-gcc
CFLAGS ?= -O3 -DNDEBUG -DEGL_API_FB
ARFLAGS ?= -r
AR ?= $(TOOLCHAIN_PATH)/bin/$(CC_PREFIX)-ar

INCLUDES := \
-I. \
-I$(MAKEFILE_DIR)/../../../ \
-I$(MAKEFILE_DIR)/../../../../ \
-I$(STAGING_DIR_HOST)$(includedir)/eigen3 \
-I$(WORKDIR)/gemmlowp \
-I$(WORKDIR)/neon_2_sse \
-I$(WORKDIR)/farmhash/src \
-I$(WORKDIR)/absl \
-I$(WORKDIR)/FP16/include \
-I$(STAGING_DIR_HOST)$(includedir)/flatbuffers \
-I$(GENDIR)

LIBS := \
-lstdc++ \
-lpthread \
-lm \
-lEGL \
-lGLESv2

LIBS += -ldl

# This library is the main target for this makefile. It will contain a minimal
# runtime that can be linked in to other programs.
LIB_NAME := libtensorflow-lite.a
LIB_PATH := $(LIBDIR)$(LIB_NAME)

# A small example program that shows how to link against the library.
MINIMAL_PATH := $(BINDIR)minimal

# Benchmark static library and binary
BENCHMARK_LIB_NAME := benchmark-lib.a
BENCHMARK_BINARY_NAME := benchmark_model
BENCHMARK_LIB := $(LIBDIR)$(BENCHMARK_LIB_NAME)
BENCHMARK_BINARY := $(BINDIR)$(BENCHMARK_BINARY_NAME)

LABELIMAGE_PATH := $(BINDIR)label_image

LABELIMAGE_SRCS := \
tensorflow/lite/examples/label_image/bitmap_helpers.cc \
tensorflow/lite/examples/label_image/label_image.cc
LABELIMAGE_OBJS := $(addprefix $(OBJDIR), \
$(patsubst %.cc,%.o,$(patsubst %.c,%.o,$(LABELIMAGE_SRCS))))

# What sources we want to compile, must be kept in sync with the main Bazel
# build files.

PROFILER_SRCS := \
        tensorflow/lite/profiling/time.cc
PROFILE_SUMMARIZER_SRCS := \
	tensorflow/lite/profiling/profile_summarizer.cc \
	tensorflow/core/util/stats_calculator.cc

# Benchmark sources
BENCHMARK_SRCS_DIR := tensorflow/lite/tools/benchmark
BENCHMARK_ALL_SRCS := $(TFLITE_CC_SRCS) \
        $(wildcard $(BENCHMARK_SRCS_DIR)/*.cc) \
        $(PROFILE_SUMMARIZER_SRCS)

BENCHMARK_SRCS := $(filter-out \
        $(wildcard $(BENCHMARK_SRCS_DIR)/*_test.cc), \
    $(BENCHMARK_ALL_SRCS))

BENCHMARK_OBJS := $(addprefix $(OBJDIR), \
$(patsubst %.cc,%.o,$(patsubst %.c,%.o,$(BENCHMARK_SRCS))))

MINIMAL_SRCS := \
tensorflow/lite/examples/minimal/minimal.cc
MINIMAL_OBJS := $(addprefix $(OBJDIR), \
$(patsubst %.cc,%.o,$(patsubst %.c,%.o,$(MINIMAL_SRCS))))

CORE_CC_ALL_SRCS := \
$(wildcard tensorflow/lite/*.cc) \
$(wildcard tensorflow/lite/*.c) \
$(wildcard tensorflow/lite/c/*.c) \
$(wildcard tensorflow/lite/experimental/c/*.c) \
$(wildcard tensorflow/lite/experimental/c/*.cc) \
$(wildcard tensorflow/lite/core/*.cc) \
$(wildcard tensorflow/lite/core/api/*.cc) \
$(wildcard tensorflow/lite/kernels/*.cc) \
$(wildcard tensorflow/lite/kernels/internal/*.cc) \
$(wildcard tensorflow/lite/kernels/internal/optimized/*.cc) \
$(wildcard tensorflow/lite/kernels/internal/reference/*.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/*.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/add_bias.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/fuse_add_to_conv.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/fuse_mul_to_conv.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/general_transformations.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/make_fully_connected.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/make_padding.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/merge_padding_with.cc) \
$(wildcard tensorflow/lite/delegates/gpu/common/transformations/remove_noop.cc) \
$(wildcard tensorflow/lite/delegates/gpu/*.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/*.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/compiler/*.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/converters/*.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/add.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/concat.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/conv.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/depthwise_conv.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/elementwise.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/fully_connected.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/lstm.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/max_unpooling.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/mul.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/pad.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/pooling.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/prelu.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/registry.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/relu.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/reshape.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/slice.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/softmax.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/sub.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/transpose_conv.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/kernels/upsampling_bilinear.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/runtime/*.cc) \
$(wildcard tensorflow/lite/delegates/gpu/gl/workgroups/*.cc) \
$(PROFILER_SRCS) \
$(wildcard $(WORKDIR)/farmhash/src/farmhash.cc) \
$(wildcard $(WORKDIR)/fft/fftsg.c) \
$(wildcard $(WORKDIR)/absl/absl/base/*.cc) \
$(wildcard $(WORKDIR)/absl/absl/base/internal/raw_logging.cc) \
$(wildcard $(WORKDIR)/absl/absl/base/internal/throw_delegate.cc) \
$(wildcard $(WORKDIR)/absl/absl/numeric/int128.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/ascii.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/charconv.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/internal/charconv_bigint.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/internal/charconv_parse.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/internal/memutil.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/internal/str_format/*.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/match.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/numbers.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/str_cat.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/str_replace.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/str_split.cc) \
$(wildcard $(WORKDIR)/absl/absl/strings/string_view.cc) \
$(wildcard $(WORKDIR)/absl/absl/types/bad_any_cast.cc) \
$(wildcard $(WORKDIR)/absl/absl/types/bad_variant_access.cc) 

# Remove any duplicates.
CORE_CC_ALL_SRCS := $(sort $(CORE_CC_ALL_SRCS))
CORE_CC_EXCLUDE_SRCS := \
$(wildcard tensorflow/lite/*test.cc) \
$(wildcard tensorflow/lite/*/*test.cc) \
$(wildcard tensorflow/lite/*/*/*test.cc) \
$(wildcard tensorflow/lite/*/*/*/*test.cc) \
$(wildcard tensorflow/lite/kernels/test_util.cc) \
$(LABELIMAGE_SRCS)

CORE_CC_EXCLUDE_SRCS += tensorflow/lite/minimal_logging_android.cc
CORE_CC_EXCLUDE_SRCS += tensorflow/lite/minimal_logging_ios.cc
CORE_CC_EXCLUDE_SRCS += tensorflow/lite/nnapi_delegate.cc

$(info $$CORE_CC_ALL_SRCS is [${CORE_CC_ALL_SRCS}])
$(info $$CORE_CC_EXCLUDE_SRCS is [${CORE_CC_EXCLUDE_SRCS}])

# Filter out all the excluded files.
TF_LITE_CC_SRCS := $(filter-out $(CORE_CC_EXCLUDE_SRCS), $(CORE_CC_ALL_SRCS))

# File names of the intermediate files target compilation generates.
TF_LITE_CC_OBJS := $(addprefix $(OBJDIR), \
$(patsubst %.cc,%.o,$(patsubst %.c,%.o,$(TF_LITE_CC_SRCS))))
LIB_OBJS := $(TF_LITE_CC_OBJS)

$(info $$TF_LITE_CC_SRCS is [${TF_LITE_CC_SRCS}])
$(info $$TF_LITE_CC_OBJS is [${TF_LITE_CC_OBJS}])

# For normal manually-created TensorFlow C++ source files.
$(OBJDIR)%.o: %.cc
	@mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

# For normal manually-created TensorFlow C++ source files.
$(OBJDIR)%.o: %.c
	@mkdir -p $(dir $@)
	$(CC) $(CCFLAGS) $(INCLUDES) -c $< -o $@

# The target that's compiled if there's no command-line arguments.
all: $(LIB_PATH) $(LABELIMAGE_PATH) $(MINIMAL_PATH) $(BENCHMARK_BINARY)

# Gathers together all the objects we've compiled into a single '.a' archive.
$(LIB_PATH): $(LIB_OBJS)
	@mkdir -p $(dir $@)
	$(AR) $(ARFLAGS) $(LIB_PATH) $(LIB_OBJS)

$(MINIMAL_PATH): $(MINIMAL_OBJS) $(LIB_PATH)
	@mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS) $(INCLUDES) \
	-o $(MINIMAL_PATH) $(MINIMAL_OBJS) \
	$(LIBFLAGS) $(LIB_PATH) $(LDFLAGS) $(LIBS)

$(BENCHMARK_LIB) : $(LIB_PATH) $(BENCHMARK_OBJS)
	@mkdir -p $(dir $@)
	$(AR) $(ARFLAGS) $(BENCHMARK_LIB) $(LIB_OBJS) $(BENCHMARK_OBJS)

benchmark_lib: $(BENCHMARK_LIB)
$(info $(BENCHMARK_BINARY))
$(BENCHMARK_BINARY) : $(BENCHMARK_LIB)
	@mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS) $(INCLUDES) \
	-o $(BENCHMARK_BINARY) \
	$(LIBFLAGS) $(BENCHMARK_LIB) $(LDFLAGS) $(LIBS)

benchmark: $(BENCHMARK_BINARY)

$(LABELIMAGE_PATH): $(LABELIMAGE_OBJS) $(LIB_PATH)
	@mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS) $(INCLUDES) \
	-o $(LABELIMAGE_PATH) $(LABELIMAGE_OBJS) \
	$(LIBFLAGS) $(LIB_PATH) $(LDFLAGS) $(LIBS)

# Gets rid of all generated files.
clean:
	rm -rf $(MAKEFILE_DIR)/gen

# Gets rid of target files only, leaving the host alone. Also leaves the lib
# directory untouched deliberately, so we can persist multiple architectures
# across builds for iOS and Android.
cleantarget:
	rm -rf $(OBJDIR)
	rm -rf $(BINDIR)

$(DEPDIR)/%.d: ;
.PRECIOUS: $(DEPDIR)/%.d

-include $(patsubst %,$(DEPDIR)/%.d,$(basename $(TF_CC_SRCS)))
