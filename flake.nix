{
  inputs = {
    devshell.url = "github:numtide/devshell";
    flake-utils.url = "github:numtide/flake-utils";
    sn-bindgen.url = "github:igor-ramazanov/sn-bindgen/update-flake";
    sn-bindgen.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs =
    { devshell
    , nixpkgs
    , flake-utils
    , sn-bindgen
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
      devShell = pkgs.devshell.mkShell {
        name = "scala-native-playground";
        motd = "Entered scala-native-playground development environment";
        packages =
          let
            libs = pkgs.lib.flatten (builtins.map
              (e: [
                (pkgs.lib.getDev e)
                (pkgs.lib.getLib e)
              ]) [
              pkgs.imagemagick_light
              pkgs.libsndfile
              pkgs.sn-bindgen
              pkgs.zlib
            ]);
          in
          [
            pkgs.clang_18
            pkgs.llvmPackages_18.libcxx
          ] ++ libs;
        env = [
          {
            name = "LIBRARY_PATH";
            prefix = "$DEVSHELL_DIR/lib";
          }
          {
            name = "C_INCLUDE_PATH";
            prefix = "$DEVSHELL_DIR/include";
          }
        ];
      };
    });
}
