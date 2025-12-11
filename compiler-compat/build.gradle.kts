// Copyright (C) 2025 Zac Sweers
// SPDX-License-Identifier: Apache-2.0
plugins {
  id("kotlin-jvm-convention")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.add("-Xcontext-parameters")
    optIn.addAll(
      "org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
      "org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
    )
  }
}

dependencies {
  compileOnly(libs.kotlin.compiler)
  compileOnly(libs.kotlin.stdlib)
  implementation(libs.semver)
}
