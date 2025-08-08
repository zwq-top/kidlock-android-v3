
package com.example.kidlock.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import org.json.JSONArray

object Prefs {
    private const val NAME = "kidlock_prefs"
    private const val KEY_PIN = "pin"
    private const val KEY_BLOCKED = "blocked_set"
    private const val KEY_QUOTAS = "quotas_json" // {"pkg": minutes}
    private const val KEY_ALLOWED_START = "allowed_start" // "08:00"
    private const val KEY_ALLOWED_END = "allowed_end"     // "20:00"
    private const val KEY_USAGE_PREFIX = "usage_" // usage_YYYYMMDD json: {"pkg": minutes}

    private fun sp(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun savePin(ctx: Context, pin: String) {
        sp(ctx).edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(ctx: Context): String = sp(ctx).getString(KEY_PIN, "1234") ?: "1234"

    fun saveBlocked(ctx: Context, pkgs: Set<String>) {
        sp(ctx).edit().putStringSet(KEY_BLOCKED, pkgs).apply()
    }

    fun getBlocked(ctx: Context): Set<String> =
        sp(ctx).getStringSet(KEY_BLOCKED, setOf()) ?: emptySet()

    fun saveQuota(ctx: Context, map: Map<String, Int>) {
        val obj = JSONObject()
        map.forEach { (k,v) -> obj.put(k, v) }
        sp(ctx).edit().putString(KEY_QUOTAS, obj.toString()).apply()
    }

    fun getQuota(ctx: Context): Map<String, Int> {
        val raw = sp(ctx).getString(KEY_QUOTAS, "{}") ?: "{}"
        val obj = JSONObject(raw)
        val out = mutableMapOf<String, Int>()
        obj.keys().forEach { k -> out[k] = obj.getInt(k) }
        return out
    }

    fun setAllowedWindow(ctx: Context, start: String, end: String) {
        sp(ctx).edit().putString(KEY_ALLOWED_START, start).putString(KEY_ALLOWED_END, end).apply()
    }

    fun getAllowedWindow(ctx: Context): Pair<String,String> {
        val s = sp(ctx).getString(KEY_ALLOWED_START, "06:00") ?: "06:00"
        val e = sp(ctx).getString(KEY_ALLOWED_END, "21:00") ?: "21:00"
        return s to e
    }

    private fun todayKey(): String {
        val now = java.time.LocalDate.now()
        return "%04d%02d%02d".format(now.year, now.monthValue, now.dayOfMonth)
    }

    fun addUsageMinutes(ctx: Context, pkg: String, minutes: Int) {
        val key = KEY_USAGE_PREFIX + todayKey()
        val raw = sp(ctx).getString(key, "{}") ?: "{}"
        val obj = JSONObject(raw)
        val current = if (obj.has(pkg)) obj.getInt(pkg) else 0
        obj.put(pkg, current + minutes)
        sp(ctx).edit().putString(key, obj.toString()).apply()
    }

    fun getUsageToday(ctx: Context, pkg: String): Int {
        val key = KEY_USAGE_PREFIX + todayKey()
        val raw = sp(ctx).getString(key, "{}") ?: "{}"
        val obj = JSONObject(raw)
        return if (obj.has(pkg)) obj.getInt(pkg) else 0
    }
}
