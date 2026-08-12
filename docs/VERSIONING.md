# Versioning and branches

Chestifier targets multiple Minecraft versions through separate branches,
not through a single branch with a wide `minecraft` range in
`fabric.mod.json`. Branches are named after the **exact patch version
they were built and launch-tested against**, and `minecraft` in
`fabric.mod.json` is always an exact pin (e.g. `~1.21.11`), never an
open-ended range, unless a range has actually been verified across every
patch it claims to cover. 

## Repo layout

- `main` - no mod source. Just this file, `README.md`, and `LICENSE`. The
  landing page for the repo; links out to the version branches below.
- `fabric_<exact-mc-version>` - one branch per confirmed-working Minecraft
  version, e.g. `fabric_1.21.11`. Each is a complete, independent mod
  project (full source, same `README.md` content as `main`, its own
  `gradle.properties`).

Branches are independent siblings, not a line you move "backward" or
"forward" through. A new version branch is created once, by branching off
the closest existing version branch and adjusting
`minecraft_version`/`yarn_mappings` plus whatever mixin targets changed
for that version. After that it lives on its own: pushing one version
branch never requires touching another. A fix that applies to more than
one branch gets cherry-picked across, it isn't merged between them.

## Supported versions

| Branch            | Minecraft   | Status    |
|--------------------|-------------|-----------|
| `fabric_1.21.11`   | 1.21.11     | confirmed working |

Other 1.21.x patches are untested. 

## Releases

File naming: `chestifier-<minecraft_version>-fabric_<fabric_version>-<mod_version>.jar`, e.g.

```
chestifier-1.21.9-fabric_0.19.3-1.1.0.jar
chestifier-1.20.2-fabric_0.18.4-1.0.2.jar
```

Tag naming: `<minecraft_version>-fabric_<fabric_version>-<mod_version>`, e.g.

```
1.21.9-fabric_0.19.3-1.1.0
1.20.2-fabric_0.18.4-1.0.2
```

Each release gets a GitHub Release from that tag, built from the matching
`fabric_<mc-version>` branch, with the jar attached. The release
description states the exact Minecraft version (or verified range) and
Fabric Loader version required. Mirror the same file per supported
Minecraft version when uploading to CurseForge and Modrinth.
