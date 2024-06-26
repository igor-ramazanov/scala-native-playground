# Scala Native Playground
Exploring Scala Native + Nix Flakes + Nix Devshell + Direnv.

The Nix sets up the development environment with:
1. `clang`: C/C++ LLVM compiler
1. `sn-bindgen`: Scala Native bindings generator
1. `imagemagick`: `magickwand` API `C` for processing images
1. `sndfile`: `C` library for working with WAV audio files

The `build.sbt` points to `clang` and `sn-bindgen` binaries provided by the Nix Devshell,
and defines `sn-bindgen` bindings.

## Usage
To run the `sndfile` program:

Start an `sbt` shell,
then `run <full path to an example .wav file>`,
e.g. `run /home/igor/personal/scala-native-playground/file.wav`,
and pick the `tech.igorramazanov.scalanativeplayground.SndFileMain` main class.

Output:
```
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

To run the `magickwand` program:

Start an `sbt` shell,
then `run` and pick the `tech.igorramazanov.scalanativeplayground.MagickWandMain` main class.

The program will produce the `logo-extend.png` file (it uses the embeded into binary `logo:` image file).
