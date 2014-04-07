/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES */

#ifndef _Included_edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES
#define _Included_edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES
 * Method:    createAESCompute
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES_createAESCompute
  (JNIEnv *, jobject);

/*
 * Class:     edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES
 * Method:    createAESInvert
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES_createAESInvert
  (JNIEnv *, jobject);

JNIEXPORT void JNICALL Java_edu_biu_scapi_primitives_prf_openSSL_OpenSSLAES_setKey
  (JNIEnv *, jobject, jlong, jlong, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif