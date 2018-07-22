package org.vontech.androidserver.utils

import java.io.BufferedReader
import java.io.InputStreamReader


class CommandRunner {

    companion object {

        // https://www.mkyong.com/java/how-to-execute-shell-command-from-java/
        fun executeCommand(command: String): String {

            val output = StringBuffer()

            val p: Process
            try {
                p = Runtime.getRuntime().exec(command)
                p.waitFor()
                val reader = BufferedReader(InputStreamReader(p.inputStream))

                var line = reader.readLine()
                while (line != null) {
                    output.append(line + "\n")
                    line = reader.readLine()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return output.toString()

        }

        fun gitClone(gitSource: String, saveLocation: String): String {

            return executeCommand("git clone $gitSource $saveLocation")

        }

    }

}