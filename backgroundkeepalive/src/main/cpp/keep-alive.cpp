//
// Created by shenhua on 2017-11-08-0008.
//
#include <jni.h>
#include <string>
#include <android/log.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <sys/stat.h>

#define THREAD_COUNT 1
#define LOG_TAG       "JNI LOG"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

JavaVM *jvm = NULL;
jobject jobj = NULL;
const char *jServiceName;

void *thread(void *arg);

int isServiceExist();

void runService();

extern "C"
JNIEXPORT void JNICALL
Java_com_shenhua_backgroundkeepalive_MainActivity_startJniService(JNIEnv *env, jobject instance,
                                                                  jstring service) {
    LOGD("jni start......");
    jServiceName = env->GetStringUTFChars(service, JNI_FALSE);
    env->GetJavaVM(&jvm);
    jobj = env->NewGlobalRef(instance);

    pthread_t pt[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; ++i) {
        pthread_create(&pt[i], NULL, &thread, (void *) i);
    }
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("GetEnv failed!");
        return result;
    }
    return JNI_VERSION_1_4;
}

void *thread(void *arg) {
    JNIEnv *env;
    jclass jclazz;
    jmethodID jmid;
    if (jvm->AttachCurrentThread(&env, NULL) != JNI_OK) {
        LOGE("%s: AttachCurrentThread failed.", __FUNCTION__);
        return NULL;
    }
    jclazz = env->GetObjectClass(jobj);
    if (jclazz == NULL) {
        LOGE("GetObjectClass Error.");
        goto error;
    }
    jmid = env->GetStaticMethodID(jclazz, "fromJNI", "(I)V");
    if (jmid == NULL) {
        LOGE("GetStaticMethodID Error.");
        goto error;
    }
    env->CallStaticVoidMethod(jclazz, jmid, (uintptr_t) arg);
    // fork一个进程父进程
    pid_t pid;
    struct rlimit r;
    if ((pid = fork()) < 0) {
        LOGD("deamon 1--");
        perror("fork");
        exit(0);
    } else if (pid != 0) {
        LOGD("deamon 2--");
    }
    // 第二次fork出来的子进程与父进程脱离了关系
    setsid();
    umask(0);
    LOGD("deamon 3--");
    if ((pid = fork()) < 0) {
        LOGD("deamon 4--");
        perror("fork");
        exit(0);
    } else if (pid != 0) {
        LOGD("deamon 5--");
    }
    // 进程活动时，其工作目录所在的文件系统不能卸下
    chdir("/");
    LOGD("deamon 6--");
    if (r.rlim_max == RLIM_INFINITY) {
        r.rlim_max = 1024;
    }
    int isExit;
    isExit = -3;
    // isExit = isDeamonExist();
    LOGE("start >>> %d: isDeamonExist.", isExit);
    if (isExit == -1) {
        return NULL;
    }
    while (1) {
        LOGE("service start success.-----------------");
        isExit = isServiceExist();
        runService();
        LOGD("+++  %d: service id isExit..", isExit);
        sleep(5);
    }

    error:
    if (jvm->DetachCurrentThread() != JNI_OK) {
        LOGE("%s: DetachCurrentThread failed.", __FUNCTION__);
    }
    pthread_exit(0);
}

// always return 0;
int isServiceExist() {
    char buf[1024];
    char command[1024];
    FILE *file;
    int ret = 0;
    sprintf(command, "pid of %s", jServiceName);
    if ((file = popen(command, "r")) == NULL) {
        LOGE("isServiceExist popen failed");
        exit(1);
    }
    if ((fgets(buf, 1024, file)) != NULL) {
        ret = 1;
        LOGD("isServiceExist pid is:%s", buf);
    }
    pclose(file);
    return ret;
}

void runService() {
    FILE *file;
    char command[1024];
    sprintf(command, "am startservice -a %s", jServiceName);
    LOGD(">>>> run cmd:%s", command);
    if ((file = popen(command, "r")) == NULL) {
        LOGE("runService popen failed");
        exit(1);
    } else {
        LOGD(">>>> runService popen success");
    }
    pclose(file);
}