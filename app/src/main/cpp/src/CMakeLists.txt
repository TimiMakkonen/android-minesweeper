
# Note that headers are optional, and do not affect add_library, but they will not
# show up in IDEs unless they are listed in add_library.

# Private header files
set(PRIVATE_HEADER_FOLDER "android-minesweeper")
set(PRIVATE_HEADER_LIST
    "${PRIVATE_HEADER_FOLDER}/game.h"
)

# Source files
set(SOURCE_FOLDER "android-minesweeper")
set(SOURCE_LIST
    "${SOURCE_FOLDER}/game.cpp"
)

# Swig source files
set(SWIG_SOURCE_FOLDER "android-minesweeper")
set(SWIG_SOURCE_LIST
    "${SOURCE_FOLDER}/game_wrapper.cpp"
)

# +--------------+
# | Swig target: |
# +--------------+
# The reason for this separate Swig target is to allow excluding it from 'clang-tidy'.

set(SWIG_LIBRARY_NAME "swig_target")

add_library(
    "${SWIG_LIBRARY_NAME}"
    OBJECT
    ${SWIG_SOURCE_LIST}
)

set_target_properties(
    "${SWIG_LIBRARY_NAME}"
    PROPERTIES
    CXX_CLANG_TIDY ""
)

# Swig wrapper needs our private directory and minesweeper librarys include directory directory
target_include_directories("${SWIG_LIBRARY_NAME}"
                            PRIVATE
                            "${CMAKE_CURRENT_SOURCE_DIR}"
                            $<TARGET_PROPERTY:libminesweeper,INTERFACE_INCLUDE_DIRECTORIES>
)

# +---------------+
# | Main library: |
# +---------------+

# Make an automatic library - will be static or dynamic based on user setting
add_library("${LIBRARY_NAME}"
            ${PRIVATE_HEADER_LIST}
            ${SOURCE_LIST}
            $<TARGET_OBJECTS:${SWIG_LIBRARY_NAME}>
)

# We need this directory
target_include_directories("${LIBRARY_NAME}" PRIVATE "${CMAKE_CURRENT_SOURCE_DIR}")

# We need this external minesweeper library
target_link_libraries("${LIBRARY_NAME}"
	PRIVATE
	libminesweeper
)

# All users of this library will need at least C++11
target_compile_features("${LIBRARY_NAME}" PUBLIC cxx_std_11)

# If CLANG_TIDY_EXE found and enabled, use it
if(CLANG_TIDY_EXE AND ANDROID_MINESWEEPER_CLANG_TIDY)
    set_target_properties("${LIBRARY_NAME}" PROPERTIES CXX_CLANG_TIDY "${DO_CLANG_TIDY}" )
endif()

# IDEs should put the headers in a nice place
# (This might not work as intended)
# source_group(TREE "${PROJECT_SOURCE_DIR}/include" PREFIX "Header Files" FILES ${HEADER_LIST})