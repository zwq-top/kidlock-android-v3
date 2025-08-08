
package com.example.kidlock

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.kidlock.data.Prefs
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

class AppBlockerService : AccessibilityService() {

    private var lastPkg: String? = null
    private var lastTs: Long = 0L  // ms

    private fun parseTime(s: String): LocalTime {
        return try { LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm")) }
        catch (e: Exception) { LocalTime.of(0,0) }
    }

    private fun inAllowedWindow(): Boolean {
        val (startS, endS) = Prefs.getAllowedWindow(this)
        val now = LocalTime.now()
        val start = parseTime(startS)
        val end = parseTime(endS)
        return if (end.isAfter(start) || end == start) {
            !now.isBefore(start) && !now.isAfter(end)
        } else {
            // window crosses midnight
            !now.isBefore(start) || !now.isAfter(end)
        }
    }

    private fun showLock(pkg: String) {
        val i = Intent(this, LockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                     Intent.FLAG_ACTIVITY_CLEAR_TOP or
                     Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            putExtra("target_pkg", pkg)
        }
        startActivity(i)
    }

    private fun maybeBlock(pkg: String) {
        // Time window check
        if (!inAllowedWindow()) {
            showLock(pkg)
            return
        }
        // Quota check
        val quota = Prefs.getQuota(this)[pkg] ?: 0
        if (quota > 0) {
            val used = Prefs.getUsageToday(this, pkg)
            if (used >= quota) {
                showLock(pkg)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val now = System.currentTimeMillis()
        val pkg = event.packageName?.toString() ?: return

        // Track usage for previous package
        lastPkg?.let { prev ->
            if (lastTs > 0) {
                val elapsedMin = max(0L, now - lastTs) / 60000L
                if (elapsedMin > 0) {
                    Prefs.addUsageMinutes(this, prev, elapsedMin.toInt())
                }
            }
        }

        lastPkg = pkg
        lastTs = now

        val blocked = Prefs.getBlocked(this)
        if (blocked.contains(pkg)) {
            maybeBlock(pkg)
        }
    }

    override fun onInterrupt() {}
}
