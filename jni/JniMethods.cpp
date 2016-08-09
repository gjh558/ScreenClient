#include <jni.h>
#include "RtspClient.h"
#include <android/log.h>
#include "VideoData.h"
#include <queue>

using namespace std;

queue<VideoData *> video_datas;

#define TAG "0000000000ITLMSCLIENT_NATIVE_MODULE0000000000"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

extern "C" {
    JNIEXPORT jint JNICALL Java_com_example_screenclient_JniClass_startRtspClient(JNIEnv * env, jobject obj, jstring url);
    JNIEXPORT jint JNICALL Java_com_example_screenclient_JniClass_getAFrame(JNIEnv * env, jobject obj, jbyteArray buffer);
    JNIEXPORT void JNICALL Java_com_example_screenclient_JniClass_stopRtspClient(JNIEnv * env, jobject obj);
};

jint
Java_com_example_screenclient_JniClass_startRtspClient(JNIEnv * env, jobject obj, jstring url)
{
	int res = -1;
	const char *theURL = env->GetStringUTFChars(url, 0);
	LOGV("theURL = %s\n", theURL);
	res = StartRtspClient(theURL);
	env->ReleaseStringUTFChars(url, theURL);
	return res;
}

jint
Java_com_example_screenclient_JniClass_getAFrame(JNIEnv * env, jobject obj, jbyteArray buffer)
{
	//int len = env->GetArrayLength (buffer);
	//unsigned char* buf = new unsigned char[len];
	//env->GetByteArrayRegion (buffer, 0, len, reinterpret_cast<jbyte*>(buf));

	if (video_datas.size() > 0) {
		VideoData *p = video_datas.front();
		video_datas.pop();

		env->SetByteArrayRegion (buffer, 0, p->length, reinterpret_cast<jbyte*>(p->data));

		return p->length;
	}

	return 0;
}

void
Java_com_example_screenclient_JniClass_stopRtspClient(JNIEnv * env, jobject obj)
{
	StopRtspClient();
	return;
}
