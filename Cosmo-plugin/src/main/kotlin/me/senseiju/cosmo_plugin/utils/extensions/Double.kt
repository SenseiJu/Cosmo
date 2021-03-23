package me.senseiju.sennetmc.utils.extensions

/**
 * Rounds the double to some number of decimal places
 *
 * @param dp The number of decimal places to round to
 *
 * @return the rounded number
 */
fun Double.round(dp: Int = 2): Double {
    return "%.${dp}f".format(this).toDouble()
}