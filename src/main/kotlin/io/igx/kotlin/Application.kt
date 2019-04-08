package io.igx.kotlin

import com.github.ajalt.clikt.core.subcommands
import io.igx.kotlin.commands.CircuitCommands
import io.igx.kotlin.commands.DriverCommands
import io.igx.kotlin.commands.F1

/**
 * @author vinicius
 *
 */

fun main(args: Array<String>) {
   F1().subcommands(DriverCommands(), CircuitCommands()).main(args)
}