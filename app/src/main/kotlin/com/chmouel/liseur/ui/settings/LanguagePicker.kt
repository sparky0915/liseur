/*
 * SPDX-License-Identifier: MIT
 */

package com.chmouel.liseur.ui.settings

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList as PlatformLocaleList

/**
 * The languages this app carries translations for.
 *
 * `tag` is a BCP-47 language tag and doubles as the identifier the preference
 * stores; `null` means "follow the phone". The labels stay in their own
 * language on purpose — a reader looking for their own language recognises
 * 简体中文 or Русский far quicker than they read "Chinese (Simplified)".
 */
enum class AppLanguage(val tag: String?, val nativeName: String) {
    SYSTEM(null, ""),
    ENGLISH("en", "English"),
    CHINESE_SIMPLIFIED("zh-Hans", "简体中文"),
    GERMAN("de", "Deutsch"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    ITALIAN("it", "Italiano"),
    RUSSIAN("ru", "Русский"),
}

/** Where the reader's choice is kept. */
internal object LanguagePreferences {
    private const val PREFS = "liseur_language"
    private const val KEY_TAG = "tag"

    fun savedTag(context: Context): String? =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_TAG, null)

    fun save(context: Context, tag: String?) {
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().apply {
            if (tag == null) remove(KEY_TAG) else putString(KEY_TAG, tag)
        }.apply()
    }
}

/** The language in use, read back from the preference the picker writes. */
internal fun currentAppLanguage(context: Context): AppLanguage {
    val tag = LanguagePreferences.savedTag(context) ?: return AppLanguage.SYSTEM
    return AppLanguage.entries.firstOrNull { it.tag != null && tag.startsWith(it.tag) } ?: AppLanguage.SYSTEM
}

/**
 * Applies a language.
 *
 * On Android 13 and up the platform owns this: `LocaleManager` stores it,
 * applies it to the running app, restarts the activities itself, and lists the
 * app under Settings ▸ Apps ▸ Language. Below 13 there is no such store, so the
 * choice is read back by [withSavedLanguage] when the next activity builds and
 * this only has to ask for the restart.
 */
internal fun applyAppLanguage(context: Context, language: AppLanguage) {
    LanguagePreferences.save(context, language.tag)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)?.applicationLocales =
            language.tag?.let { PlatformLocaleList.forLanguageTags(it) } ?: PlatformLocaleList.getEmptyLocaleList()
    } else {
        (context as? Activity)?.recreate()
    }
}

/**
 * Wraps a base context in the saved language.
 *
 * Every activity funnels through `attachBaseContext`, the one place a context
 * can be replaced before the activity reads any resource. On Android 13+ the
 * platform has already changed the configuration and this is a no-op.
 */
internal fun Context.withSavedLanguage(): Context {
    val tag = LanguagePreferences.savedTag(this) ?: return this
    val locale = java.util.Locale.forLanguageTag(tag)
    java.util.Locale.setDefault(locale)
    val configuration = Configuration(resources.configuration)
    configuration.setLocales(PlatformLocaleList(locale))
    return createConfigurationContext(configuration)
}
