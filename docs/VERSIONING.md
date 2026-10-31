# Versioning and branches

Chestifier targets multiple Minecraft versions and mod loaders through
separate branches, not through a single branch with a wide `minecraft`
range in the mod metadata. Branches are named after the mod loader and the
**exact patch version they were built and launch-tested against**.

## Compatibility range

The `minecraft` dependency in the mod metadata (`fabric.mod.json` on
Fabric, `neoforge.mods.toml` on NeoForge) has a lower bound only
(e.g. `>=26.3`), with no upper bound. The mod is always open to Minecraft
versions newer than the one it was built for, but there is no guarantee
that it works on them. Newer versions are used at your
own risk. If a newer version breaks the mod, pull the file or note it in the comment sections on the platform.

## Repo layout

- `main` - no mod source. Just this file, `README.md`, and `LICENSE`. The
  landing page for the repo; links out to the version branches below.
- `fabric_<exact-mc-version>` - one branch per confirmed-working Minecraft
  version on Fabric, e.g. `fabric_1.21.11`. Each is a complete,
  independent mod project (full source, same `README.md` content as
  `main`, its own `gradle.properties`).
- `neoforge_<exact-mc-version>` - the same for NeoForge, e.g.
  `neoforge_26.3`. NeoForge branches exist for 26.2 and 26.3 only.

Branches are independent siblings, not a line you move "backward" or
"forward" through. A new version branch is created once, by branching off
the closest existing version branch and adjusting
`minecraft_version`/`yarn_mappings` plus whatever mixin targets changed
for that version. A NeoForge branch is created from the Fabric branch of
the same Minecraft version. After that it lives on its own: pushing one version
branch never requires touching another. A fix that applies to more than
one branch gets cherry-picked across, it isn't merged between them.

## Supported versions

| Branch            | Minecraft     | Status    |
|--------------------|---------------|-----------|
| `fabric_26.3`      | 26.3 and later (tag only tested versions) | confirmed working |
| `neoforge_26.2`    | 26.2 and later (tag only tested versions) | confirmed working |
| `fabric_1.21.11`   | 1.21.11       | confirmed working |
| `fabric_1.20.5`    | 1.20.5, 1.20.6 | confirmed working |

Other 1.21.x and 1.20.x patches are untested.

## Releases

### Mod version

A NeoForge branch uses the same mod version as the Fabric branch it was
ported from, e.g. `2.2.0` on both `fabric_26.3` and `neoforge_26.3`. The
same version number means the same features. A fix that only applies to
one loader bumps the patch version on that branch only (e.g. NeoForge
`2.2.1` while Fabric stays at `2.2.0`).

The one exception is `neoforge_26.2` (`2.1.0`), which already includes the
search lock button that `fabric_26.2` (`2.1.0`) does not have.

### Names

File naming: `chestifier-<minecraft_version>_v<mod_version>-<loader>.jar`,
where `<loader>` is `fabric` or `neoforge`, e.g.

```
chestifier-26.3_v2.2.0-fabric.jar
chestifier-26.3_v2.2.0-neoforge.jar
```

Tag naming: `<minecraft_version>_v<mod_version>-<loader>`, e.g.

```
26.3_v2.2.0-fabric
26.3_v2.2.0-neoforge
```
