# Scala Native Playground
Exploring Scala Native + Nix Flakes + Nix Devshell + Direnv.

* https://scala-native.org/en/latest
* https://github.com/numtide/devshell
* https://nixos-and-flakes.thiscute.world/nixos-with-flakes/introduction-to-flakes
* https://direnv.net
* https://github.com/typelevel/typelevel-nix

The Nix sets up the development environment with:
1. `clang`: C/C++ LLVM compiler https://clang.llvm.org
1. `sn-bindgen`: Scala Native bindings generator https://sn-bindgen.indoorvivants.com
1. `imagemagick`: `magickwand` API `C` for processing images https://imagemagick.org
1. `sndfile`: `C` library for working with WAV audio files https://libsndfile.github.io/libsndfile
1. `jdk`: GraalVM Community Edition: https://www.graalvm.org
1. `metals`: Scala Metals LSP server: https://scalameta.org/metals
1. `sbt`: Scala Built Tool: https://www.scala-sbt.org/index.html
1. `scala-cli`: Scala command-line tool: https://scala-cli.virtuslab.org
1. `scala-fix`: Scala refactoring and linting tool for Scala: https://scalacenter.github.io/scalafix

The `build.sbt` points to `clang` and `sn-bindgen` binaries provided by the `devshell`,
and defines `sn-bindgen` bindings.

You can use this repo without `nix` if all of the above provided by your own environment.

As for `nix` users, `cd` into the repository directory and run `nix develop` to drop into the development environment.\
Or, if you have `direnv` installed, simply `cd` into the repository directory and do `direnv allow`.\
Now, whenever you `cd` into the repository directory the development environment will be activated automatically,
and erased when you `cd` out of the repository directory.

## `sndfile` usage

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

## `magickwand` usage

```
$ sbt magickwand/nativeLink
$ ./modules/magickwand/target/scala-3.5.0-RC2/magickwand
# will produce the `./logo-extend.png` image
```

## CI
This repo uses `nix` based GitHub actions for caching the development environment dependencies,
instead of the traditional approach with [`coursier/setup-action`](https://github.com/coursier/setup-action) and [`coursier/cache-action`](https://github.com/coursier/cache-action):
1. https://github.com/DeterminateSystems/nix-installer-action
1. https://github.com/DeterminateSystems/flake-checker-action
1. https://github.com/DeterminateSystems/magic-nix-cache-action
