package org.vontech.utils

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * The Commands class provides static methods and String extensions
 * for executing OS-level commands on the host system
 * @author Aaron Vontell (vontell)
 * @version 0.0.1
 */
class Commands {

    /**
     * Executes a command from the given working directory, returning the obtained
     * string.
     * @param workingDir The working directory to execute this from
     */
    fun String.runCommand(workingDir: File?): String? {
        try {
            val parts = this.split("\\s".toRegex())
            val proc = ProcessBuilder(*parts.toTypedArray())
                    .directory(workingDir)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE)
                    .start()
            proc.waitFor(60, TimeUnit.MINUTES)
            return proc.inputStream.bufferedReader().readText()
        } catch(e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Returns true if the string exists as a file, false otherwise
     */
    fun String.isFile(): Boolean? { return File(this).exists() }

}