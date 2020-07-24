
# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.8)

project(AndroidMinesweeperLibrary 
        VERSION 0.1.0
        #DESCRIPTION "Minesweeper library prepared to be used with android minesweeper game"
        LANGUAGES CXX
)


set(LIBRARY_NAME "libandroidminesweeper")

option(ANDROID_MINESWEEPER_BUILD_SHARED_LIBS "Build shared libraries instead of static ones" ON)
option(ANDROID_MINESWEEPER_CLANG_TIDY "Use 'clang-tidy' on project source files." OFF)
option(FIND_JNI "Tries to find JNI. Used when manually building cpp portion of this app (for testing)." OFF)


if(ANDROID_MINESWEEPER_BUILD_SHARED_LIBS)
  set(BUILD_SHARED_LIBS ON)
endif()


# Setting up clang-tidy if it is installed and enabled
if(ANDROID_MINESWEEPER_CLANG_TIDY)
  find_program(CLANG_TIDY_EXE
              NAMES "clang-tidy"
              DOC "Path to clang-tidy executable"
  )

  if(NOT CLANG_TIDY_EXE)
    message(STATUS "clang-tidy not found.")
  else()
    message(STATUS "clang-tidy found: ${CLANG_TIDY_EXE}")
    # Allows to set clang-tidy for specific targets
    set(DO_CLANG_TIDY "${CLANG_TIDY_EXE}")
  endif()
endif()

if(FIND_JNI)
  find_package(JNI)
  if(NOT JNI_FOUND)
    message(FATAL_ERROR "JNI not found.")
  else()
    # globally include JNI include directories, if found
    # (consider replacing with target specific version)
    include_directories("${JNI_INCLUDE_DIRS}")
  endif()
  else()
endif()



# Only do these if this is the main project, and not if it is included through add_subdirectory
if(CMAKE_PROJECT_NAME STREQUAL PROJECT_NAME)

    # Set global c++ standard
    set(CMAKE_CXX_STANDARD 11)
    set(CMAKE_CXX_STANDARD_REQUIRED ON)

    # Use -std=c++xx instead of -std=g++xx
    set(CMAKE_CXX_EXTENSIONS OFF)

    # Let's nicely support folders in IDE's
    set_property(GLOBAL PROPERTY USE_FOLDERS ON)

endif()



# The external minesweeper library is here
add_subdirectory(extern/minesweeper)



# The compiled library code is here
add_subdirectory(src)



# +-----------------------------+
# | android presuggested stuff: |
# +-----------------------------+

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

#find_library( # Sets the name of the path variable.
#              log-lib
#
#              # Specifies the name of the NDK library that
#              # you want CMake to locate.
#              log
#)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#target_link_libraries( # Specifies the target library.
#                       native-lib
#
#                       # Links the target library to the log library
#                       # included in the NDK.
#                      ${log-lib} )

#-------------------------------------------------------------------