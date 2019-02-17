> 声明

```

ext {
    compileSdkVersion = 26
    buildToolsVersion = "26.0.1"
    minSdkVersion = 17
    targetSdkVersion = 26

    versions = [
            kotlin            : "1.1.4-3",
            support           : "26.0.0-alpha1",
            butterknife       : "8.8.1",
            dagger2           : "2.11",
            rxbinding2        : "2.0.0",
            rxlifecycle2      : "2.2.0",
            retrofit2         : "2.3.0",
            anko              : "0.10.0",
            kotlinx_coroutines: "0.15",
            architecture      : "1.0.0-alpha9",
    ]
    libs = [
            junit                                : "junit:junit:4.12",
            design                               : "com.android.support:design:${versions.support}",
            appcompat_v7                         : "com.android.support:appcompat-v7:${versions.support}",
            recyclerview_v7                      : "com.android.support:recyclerview-v7:${versions.support}",
            palette_v7                           : "com.android.support:palette-v7:${versions.support}",
            cardview_v7                          : "com.android.support:cardview-v7:${versions.support}",
            constraint_layout                    : "com.android.support.constraint:constraint-layout:1.0.2",
            arch_room_runtime                    : "android.arch.persistence.room:runtime:${versions.architecture}",
            arch_room_compiler                   : "android.arch.persistence.room:compiler:${versions.architecture}",
            arch_room_rxjava2                    : "android.arch.persistence.room:rxjava2:${versions.architecture}",
            arch_lifecycle_runtime               : "android.arch.lifecycle:runtime:${versions.architecture}",
            arch_lifecycle_extensions            : "android.arch.lifecycle:extensions:${versions.architecture}",
            arch_lifecycle_compiler              : "android.arch.lifecycle:compiler:${versions.architecture}",

            multidex                             : "com.android.support:multidex:1.0.1",

            dagger2                              : "com.google.dagger:dagger:${versions.dagger2}",
            dagger2_compiler                     : "com.google.dagger:dagger-compiler:${versions.dagger2}",



            rxandroid2                           : "io.reactivex.rxjava2:rxandroid:2.0.1",
            rxjava2                              : "io.reactivex.rxjava2:rxjava:2.1.4",

            rxlifecycle2                         : "com.trello.rxlifecycle2:rxlifecycle:${versions.rxlifecycle2}",
            rxlifecycle2_kotlin                  : "com.trello.rxlifecycle2:rxlifecycle-kotlin:${versions.rxlifecycle2}",
            rxlifecycle2_android                 : "com.trello.rxlifecycle2:rxlifecycle-android:${versions.rxlifecycle2}",
            rxlifecycle2_components              : "com.trello.rxlifecycle2:rxlifecycle-components:${versions.rxlifecycle2}",
            rxlifecycle2_android_lifecycle_kotlin: "com.trello.rxlifecycle2:rxlifecycle-android-lifecycle-kotlin:${versions.rxlifecycle2}",


            rxpermission2                        : "com.tbruyelle.rxpermissions2:rxpermissions:0.9.4@aar",
            rxbinding2                           : "com.jakewharton.rxbinding2:rxbinding:${versions.rxbinding2}",
            rxbinding2_kotlin                    : "com.jakewharton.rxbinding2:rxbinding-kotlin:${versions.rxbinding2}",
            rxkotlin2                            : "io.reactivex.rxjava2:rxkotlin:2.1.0",


            okhttp3                              : "com.squareup.okhttp3:okhttp:3.8.0",
            retrofit2                            : "com.squareup.retrofit2:retrofit:${versions.retrofit2}",
            retrofit2_converter_gson                  : "com.squareup.retrofit2:converter-gson:${versions.retrofit2}",
            retrofit2_adapter_rxjava2                    : "com.squareup.retrofit2:adapter-rxjava2:${versions.retrofit2}",

            commons_codec                        : "org.eclipse.ecf:org.apache.commons.codec:1.9.0.v20170208-1614",




            kotlin_stdlib                        : "org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlin}",
            kotlinx_coroutines                   : "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.kotlinx_coroutines}",

            anko                                 : "org.jetbrains.anko:anko:${versions.anko}",
            anko_common                          : "org.jetbrains.anko:anko-commons:${versions.anko}",
            anko_layout                          : "org.jetbrains.anko:anko-sdk25:${versions.anko}",
            anko_design                          : "org.jetbrains.anko:anko-design:${versions.anko}",
            anko_compat_v7                       : "org.jetbrains.anko:anko-appcompat-v7:${versions.anko}",
            anko_support_v4                      : "org.jetbrains.anko:anko-support-v4:${versions.anko}",
            anko_recyclerview_v7                 : "org.jetbrains.anko:anko-recyclerview-v7:${versions.anko}",
            anko_cardview_v7                     : "org.jetbrains.anko:anko-cardview-v7:${versions.anko}",



            glide                                : "com.github.bumptech.glide:glide:4.1.1",
            gson: "com.google.code.gson:gson:2.8.2",



            butterknife                          : "com.jakewharton:butterknife:${versions.butterknife}",
            butterknife_compiler                 : "com.jakewharton:butterknife-compiler:${versions.butterknife}",


            adapter_helper                       : "com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.24",

    ]

}


```
> 引用

```
    compile libs.arch_room_runtime
    compile libs.arch_room_rxjava2
    kapt libs.arch_room_compiler

    compile libs.arch_lifecycle_runtime
    compile libs.arch_lifecycle_extensions
    kapt libs.arch_lifecycle_compiler
```