```
/** 获取最后一次提交的 SHA 值*/
def getGitInfo() {
    def WINDOWS_CMD_PREFIX = "cmd /c "
    def isWindows = org.gradle.internal.os.OperatingSystem.current().windows
    def cmdPrefix = isWindows ? WINDOWS_CMD_PREFIX : ""
    log("getGitInfo Is Windows? " + isWindows)

    //get lastest commit SHA
    def sha = cmdPrefix + 'git rev-parse --short HEAD'.execute().text.trim()
    return sha
}

def log(String msg) {
    log(null, msg)
}

def log(String tag, String msg) {
    if (loadKeyAtProperty(rootProject.file('gradle.properties'), 'gradleLogEnable') == 'true') {
        println("[日志 LogTrack${tag == null ? "" : tag}]:$msg")
    }
}


static Properties loadProperties(String propertiesPath) {
    return loadProperties(new File(propertiesPath))
}

static Properties loadProperties(File propertiesFile) {
    Properties properties = new Properties()
    if (null == propertiesFile || !propertiesFile.exists()) {
        return properties
    }
    InputStream inputStream = propertiesFile.newDataInputStream()
    properties.load(inputStream)
    return properties
}

static String loadKeyAtProperty(String path, String key) {
    return loadProperties(path).getProperty(key)
}

static String loadKeyAtProperty(File file, String key) {
    return loadProperties(file).getProperty(key)
}


boolean isPluginEnable(String plugin) {
    def local = rootProject.file('gradle.properties')
    Properties properties = loadProperties(local)
    boolean result
    if (isSpeedMode(properties)) {
        result = isTrue(properties.getProperty("use_$plugin"))
    } else {
        result = !isTrue(properties.getProperty("skip_$plugin"))
    }
    log('isPluginEnable', "$plugin=$result")
    result
}

static boolean isTrue(String target) {
    return target == 'true'
}

/*函数在这里声明， 其他地方 直接调用*/
ext {
    getGitInfo = this.&getGitInfo
    loadProperties = this.&loadProperties
    log = this.&log
}
```