package org.vontech.androidserver.drivers.android

import org.vontech.androidserver.logger
import org.vontech.androidserver.utils.CommandRunner
import org.vontech.androidserver.utils.parsers.XMLEditor
import java.io.File

class AndroidDriver {

    /**
     * A collection of constants and methods that are useful for non-instances
     * of the AndroidDriver object
     */
    companion object {

        /** Environment variable for the ANDROID_HOME **/
        const val ANDROID_HOME = "ANDROID_HOME"

        /** Folder info for the Android Tools **/
        const val PLATFORM_PATH = "/platforms"
        const val BUILD_TOOLS = "/build-tools"
        const val EMULATOR = "/tools/emulator"
        const val ADB = "/platform-tools/adb"

        /** Android Key Event Codes **/
        /** https://developer.android.com/reference/android/view/KeyEvent **/
        const val KEYCODE_MENU = 82
        const val KEYCODE_HOME = 3

        /** Android permission strings **/
        const val PERMISSION_SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW"
        const val PERMISSION_INTERNET = "android.permission.INTERNET"

        /** Android dependency configurations / types **/
        const val ANDROID_TEST_IMPLEMENTATION_DEP = "androidTestImplementation"

    }

    fun getAvailablePlatforms(): List<String> {
        val result = CommandRunner.executeCommand("ls " + getAndroidHome() + PLATFORM_PATH)
        return result.split("\n").dropLast(1)
    }

    fun getAvailableBuildTools(): List<String> {
        val result = CommandRunner.executeCommand("ls " + getAndroidHome() + BUILD_TOOLS)
        return result.split("\n").dropLast(1)
    }

    fun getAvailableEmulators(): List<String> {
        val result = CommandRunner.executeCommand("${getAndroidHome() + EMULATOR} -avd -list-avds")
        return result.split("\n").dropLast(1).sorted()
    }

    /**
     * Returns true if ANDROID_HOME is set on this host
     */
    fun isAndroidHomeSet(): Boolean {
        println(System.getenv())
        println(ANDROID_HOME)
        println(System.getenv(ANDROID_HOME))
        val path = System.getenv(ANDROID_HOME)
        return path != null
    }

    fun getAndroidHome(): String? {
        return System.getenv(ANDROID_HOME)
    }

    fun startEmulator(emulator: String) {
        logger?.info("Starting emulator $emulator")
        CommandRunner.executeCommand("${getAndroidHome() + EMULATOR} -avd $emulator -netdelay none -netspeed full", true, false)
    }

    fun getRunningEmulatorNames(): List<String> {
        // Note: We can use the -l flag to get even more device info
        val result = CommandRunner.executeCommand("${getAndroidHome() + ADB} devices")
        val devices: MutableList<String> = mutableListOf()
        result.split("\n")
                .filter { it -> it.contains("device") && !it.contains("List") }
                .forEach { it -> devices.add(it.split("\\s+".toRegex())[0]) }
        return devices
    }

    fun waitForEmulatorOnline() {
        logger?.info("Waiting for emulator to boot...")
        while (true) {
            val result = CommandRunner.executeCommand("${getAndroidHome() + ADB} shell getprop sys.boot_completed")
            if (result.trim() == "1") {
                break
            }
            Thread.sleep(1000)
        }
        logger?.info("Emulator booted and ready")
    }

    fun isEmulatorReady(): Boolean {
        val result = CommandRunner.executeCommand("${getAndroidHome() + ADB} shell getprop sys.boot_completed")
        return result.trim() == "1"
    }

    fun killAllEmulators() {
        getRunningEmulatorNames().forEach { it ->
            logger?.info("Killing device '$it'")
            CommandRunner.executeCommand("${getAndroidHome() + ADB} -s $it emu kill")
        }
    }

    fun wipeEmulator() {
        getRunningEmulatorNames().forEach { it ->
            CommandRunner.executeCommand("${getAndroidHome() + EMULATOR} -avd $it -wipe-data")
            logger?.info("Wiped all data from $it")
        }
    }

    fun emulatorGoHome() {
        CommandRunner.executeCommand("${getAndroidHome() + ADB} shell input keyevent $KEYCODE_MENU")
        CommandRunner.executeCommand("${getAndroidHome() + ADB} shell input keyevent $KEYCODE_HOME")
    }

    fun emulatorOpenApp(packageName: String) {
        CommandRunner.executeCommand("${getAndroidHome() + ADB} shell monkey -p $packageName -c android.intent.category.LAUNCHER 1")
    }

    fun grantEmulatorPermission(appPackage: String, permission: String) {
        CommandRunner.executeCommand("${getAndroidHome() + ADB} shell pm grant $appPackage $permission", true)
        logger?.info("Requested permission '$permission' for app '$appPackage'")
    }

    fun startADBServer() {
        CommandRunner.executeCommand("${getAndroidHome() + ADB} start-server")
        logger?.info("Started ADB server")
    }

    fun killADBServer() {
        CommandRunner.executeCommand("${getAndroidHome() + ADB} kill-server")
    }

    fun addPermissionIfMissing(projectDirectory: String, appModule: String, permission: String) {
        val filePath = File("$projectDirectory$appModule/src/main/AndroidManifest.xml").path
        logger?.info(filePath)
        val editor = XMLEditor(filePath)
        editor.insertPermissionIntoManifest(permission)
        editor.save()
    }

}