package com.eisenhower.matrix

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application-класс с поддержкой Hilt.
 * @HiltAndroidApp запускает генерацию кода Hilt и создаёт корневой компонент DI.
 * Должен быть указан в AndroidManifest.xml как android:name=".EisenhowerApp".
 */
@HiltAndroidApp
class EisenhowerApp : Application()
