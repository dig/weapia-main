# minevasion-main
 Make sure you run 1.12.2 BuildTools because this project uses 1.12.2 craftbukkit and BuildTools will create a local gradle repository. https://www.spigotmc.org/wiki/buildtools/#1-12-2

## Main
Authors: Digital
Copyright 2019 Minevasion

#### File Structure
```bash
Main
│
├── bungeecord
│   └── Bungeecord plugin.
│
├── common
│   └── All java projects will include this, should be used for shared utilities.
│
├── core
│   └── All instances will include this, should be used for shared utilities.
│
├── lobby
│   └── Hub plugin.
│
└── master
    └── Controls various main components of the network.
```