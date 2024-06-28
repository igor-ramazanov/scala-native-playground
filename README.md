# Scala Native Playground
Exploring Scala Native + Nix Flakes + Nix Devshell + Direnv.

* https://scala-native.org/en/latest
* https://github.com/numtide/devshell
* https://nixos-and-flakes.thiscute.world/nixos-with-flakes/introduction-to-flakes
* https://direnv.net

The Nix sets up the development environment with:
1. `clang`: C/C++ LLVM compiler https://clang.llvm.org
1. `sn-bindgen`: Scala Native bindings generator https://sn-bindgen.indoorvivants.com
1. `imagemagick`: `magickwand` API `C` for processing images https://imagemagick.org
1. `sndfile`: `C` library for working with WAV audio files https://libsndfile.github.io/libsndfile

The `build.sbt` points to `clang` and `sn-bindgen` binaries provided by the Nix Devshell,
and defines `sn-bindgen` bindings.

## `sndfile` Usage

```
$ sbt sndfile/nativeLink
$ ./modules/sndfile/target/scala-3.5.0-RC2/sndfile ./file.wav

Channels: 2
Format: 65538
Frames: 7623627
Sample rate: 48000
Sections: 1
Seekable: 1
Album: null
Artist: RavioliCode
Comment: https://soundcloud.com/raviolicode/babangida
Copyright: null
Date: 20170223
Genre: Hip-hop & Rap
License: null
Software: Lavf58.76.100
Title: babangida - купол
Tracknumber: null
```

## `magickwand` Usage

```
$ sbt magickwand/nativeLink
$ ./modules/magickwand/target/scala-3.5.0-RC2/magickwand
xdg-open ./logo-extend.png
```
