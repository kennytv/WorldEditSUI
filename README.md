# WorldEditSUI
[![Discord](https://img.shields.io/discord/489135856284729384.svg?label=Discord&logo=discord&logoColor=fff)](https://discord.gg/vGCUzHq)
[![Twitter](https://img.shields.io/twitter/follow/KennyTVN.svg?label=Twitter)](https://twitter.com/KennyTVN)

Sassy Minecraft plugin to show your current WorldEdit selection by displaying particles (which can be greatly customized in the plugin's config).
It aims at a higher performance and lesser memory impact by having a quite simplistic approach to calculating vectors.

You can read up on everything else and see examples on the [**Spigot page**](https://www.spigotmc.org/resources/worldeditsui.60726/).

## Legacy support
This branch supports Minecraft versions from 1.9 upwards.
If you want a jar that supports 1.8.4-1.8.9 as well (everything below 1.8.4 still unsupported), see the `1.8-support` branch / the [**releases download page**](https://github.com/KennyTV/WorldEditSUI/releases).

The default master branch does not contain 1.8 support, since that version seems to be used by only a very small fraction of WESUI users and requires some extra encapsulation and small additions for every new Minecraft release.

## Compiling
Use Maven to compile the project (`mvn clean package`).

## Licence
This project is licensed under the [GNU General Public License](http://www.gnu.org/licenses/gpl-3.0).