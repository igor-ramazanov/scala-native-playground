{
  inputs = {

    devshell = {
      url = "github:numtide/devshell";
      inputs = {
        flake-utils.follows = "flake-utils";
        nixpkgs.follows = "nixpkgs";
      };
    };

    flake-utils = {
      url = "github:numtide/flake-utils";
      inputs.systems.follows = "systems";
    };

    nixpkgs.url = "github:NixOS/nixpkgs";

    sn-bindgen = {
      url = "github:igor-ramazanov/sn-bindgen/update-flake";
      inputs = {
        nixpkgs.follows = "nixpkgs";
        # Can't override transitive inputs, see: https://github.com/NixOS/nix/issues/5790
        # sbt.inputs.flake-utils.follows = "flake-utils";
        systems.follows = "systems";
      };
    };

    systems.url = "github:nix-systems/default";

    typelevel-nix = {
      url = "github:typelevel/typelevel-nix";
      inputs = {
        devshell.follows = "devshell";
        flake-utils.follows = "flake-utils";
        nixpkgs.follows = "nixpkgs";
      };
    };

  };

  outputs =
    { devshell
    , flake-utils
    , nixpkgs
    , sn-bindgen
    , typelevel-nix
    , ...
    }: flake-utils.lib.eachSystem [ "x86_64-linux" ] (system:
    let
      pkgs = import nixpkgs {
        inherit system;
        overlays = [
          devshell.overlays.default
          sn-bindgen.overlays.default
        ];
      };
    in
    {
      formatter = pkgs.alejandra;
      devShell = pkgs.devshell.mkShell rec {
        name = "scala-native-playground";
        imports = [ typelevel-nix.typelevelShell ];

        packages = [ (pkgs.scalafix.override { jre = typelevelShell.jdk.package; }) ];

        typelevelShell = {
          jdk.package = pkgs.graalvm-ce;

          native = {
            enable = true;
            libraries = [
              pkgs.imagemagick_light
              pkgs.libsndfile
              pkgs.sn-bindgen
              pkgs.zlib
            ];
          };

          nodejs.enable = false;
          sbtMicrosites.enable = false;
        };

      };
    });
}
