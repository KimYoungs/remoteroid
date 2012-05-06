/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_secmem_remoteroid_natives_InputHandler */

#ifndef _Included_org_secmem_remoteroid_natives_InputHandler
#define _Included_org_secmem_remoteroid_natives_InputHandler
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    keyDown
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_keyDown
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    keyUp
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_keyUp
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    keyStroke
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_keyStroke
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    touchDown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_touchDown
  (JNIEnv *, jclass);

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    touchUp
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_touchUp
  (JNIEnv *, jclass);

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    touchSetPtr
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_touchSetPtr
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    touchOnce
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_touchOnce
  (JNIEnv *, jclass, jint, jint);

#ifdef __cplusplus
}
#endif
#endif