/*
 * HawtJNI header file.
 *
 * Copyright (c) Steven P. Goldsmith. All rights reserved.
 *
 */
#ifndef INCLUDED_PLATFORM_H
#define INCLUDED_PLATFORM_H

#ifdef HAVE_CONFIG_H
  /* configure based build.. we will use what it discovered about the platform */
  #include "config.h"
#else
  #ifdef WIN32
    /* Windows based build */
    #define HAVE_STDLIB_H 1
    #define HAVE_STRINGS_H 1
  #endif
#endif

#ifdef __APPLE__
#import <objc/objc-runtime.h>
#endif

#ifdef HAVE_UNISTD_H
  #include <unistd.h>
#endif

#ifdef HAVE_STDLIB_H
  #include <stdlib.h>
#endif

#ifdef HAVE_STRINGS_H
  #include <string.h>
#endif

#include "u8g2.h"
#include "helper.h"

#endif /* INCLUDED_PLATFORM_H */
