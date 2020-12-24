<p align="center">
    <a href="https://github.com/Ktt-Development/rexedia">
        <img src="https://raw.githubusercontent.com/Ktt-Development/rexedia/main/icon.png" alt="Logo" width="100" height="100">
    </a>
    <h3 align="center">Rexedia</h3>
    <p align="center">
        Metadata editing (including cover art) for bulk files using regular expressions (regex) and back references.
        <br />
        <a href="https://docs.kttdevelopment.com/rexedia/">Documentation</a>
        •
        <a href="https://github.com/Ktt-Development/rexedia/issues">Issues</a>
    </p>
</p>

[![Deploy](https://github.com/Ktt-Development/rexedia/workflows/Deploy/badge.svg)](https://github.com/Ktt-Development/rexedia/actions?query=workflow%3ADeploy)
[![Java CI](https://github.com/Ktt-Development/rexedia/workflows/Java%20CI/badge.svg)](https://github.com/Ktt-Development/rexedia/actions?query=workflow%3A%22Java+CI%22)
[![version](https://img.shields.io/github/v/release/Ktt-Development/rexedia)](https://github.com/Ktt-Development/rexedia/releases)
[![license](https://img.shields.io/github/license/Ktt-Development/rexedia)](https://github.com/Ktt-Development/rexedia/blob/main/LICENSE)

---
**Contents:**
- [Setup](#setup)
- [Features](#features)
- [Arguments](#arguments)
- [Presets](#presets)
---
# Setup

Compiled binaries can be found in releases.
[![releases](https://img.shields.io/github/v/release/Ktt-Development/rexedia)](https://github.com/Ktt-Development/rexedia/releases)

# Features

## 🎬 Wide Media Support

Rexedia supports all file extensions supported by [FFMPEG](https://ffmpeg.org/).

```shell
rexedia -i "file.mp4" -c "(.+)" "$1.png" -o "(.+)" "$1.avi"
```

## ⭐ Features

Complicated tasks simplified:
- Apply cover art and metadata.
- Customize output with regular expressions and back references.
- Support for preset files instead of command-line flags.
- Logging to track changes.

```shell
rexedia -i video.mp4 -p preset.yml
```
```yml
// preset.yml
metadata:  
  - meta: 'title' 
    regex: '(.+)'
    format: '$1'
```

## ✔️ Safe Video Formatting

Rexedia preserves the integrity of the video file.
- Media file integrity is verified at every stage.
- Backup files will always be saved on format failure.
- Use the `-b` flag to keep backup files even on successful formats.

```shell
rexedia -i "file.mp4" -c "(.+)" "$1.png" -b
```

# Arguments
**Regular expression will test against the file name without the extension.**

**At least one input required.**

**A cover, metadata, or output; or preset must be specified. If a preset is specified then the cover, metadata, and output flags will be ignored.**

- `-i` `-input` *[file]* - File or directory path to format. Can be used multiple times.
- `-w` `-walk` *[boolean]* - If true, subdirectories will also be formatted.
- `-b` `-backup` *[boolean]* - Whether to keep backup files are not.
- `-l` `-logging` *[boolean]* - Whether to keep log files or not.
- `-d` `-debug` *[boolean]* - Whether to run debug logging.
- `-pc` `-preserveCover` *[boolean]* - Whether to preserve existing cover art for files with no new cover art.
- `-pm` `-preserveMeta` *[boolean]* - Whether to preserve existing metadata.
- `-p` `-preset` *[boolean]* - The preset file to use. Overrides cover and metadata flags.
- `-c` `-cover` *[regex]* *[string]* - The regular expression and back reference string to use for cover art.
- `-m` `-metadata` *[string]* *[regex]* *[string]* - The metadata tag name, the regular expression, and back reference string to use for metadata. Can be used multiple times.
- `-o` `-output` *[regex]* *[string]* - The regular expression and back reference string to use for output file. If the final string has no extensions then the extension of the input file will be used.

## Example Usage

**Assign video a cover art image with the same name**
```
┬ video.mp4
└ video.png
```
```sh
rexedia -i video.mp4 -c "(.+)" "$1.png"
```

**Assign video a cover art image with the same name and save to new file**
```
┬ video.mp4 → video.avi
└ video.png
```
```sh
rexedia -i video.mp4 -c "(.+)" "$1.png" -o "(.+)" "$1.avi"
```

**Assign video metadata based on the name**
```
─ [S01 E02] video.mp4 → ┌ season_number = 1
                        ├ episode_sort = 2
                        └ show = video
```
```sh
rexedia -i video.mp4 -m "season_number" "\[S0*(\d*) E0*\d*\]" "$1" -m "episode_sort" "\[S0*\d* E0*(\d*)\]" "$1" -m "show" "\[S0*\d* E0*\d*\] (.+)" "$1"
```

# Presets

```yml
cover:  # cover art
  regex: '(.+)'  # the regex capture string
  format: '$1'  # the final cover art name (regex backreferences supported)
metadata:  # list of metadata tags
  - meta: 'name'  # the metadata tag name
    regex: '(.+)'  # the regex capture string
    format: '$1'  # the final metadata value (regex backreferences supported)
output:  # output file
  regex: '(.+)'  # the regex capture string
  format: '$1'  # the final output name (regex backreferences supported)
```

## Example Usage

**Assign video metadata based on the name from preset**
```
┬ [S01 E02] video.mp4 → ┌ season_number = 1
└ preset.yml            ├ episode_sort = 2
                        └ show = video
```
```yml
# preset.yml
metadata:  
  - meta: 'season_number'
    regex: '\[S0*(\d*) E0*\d*\\]'
    format: '$1'
  - meta: 'episode_sort'
    regex: '\[S0*\d* E0*(\d*)\]'
    format: '$1'
  - meta: 'show'
    regex: '\[S0*\d* E0*\d*\] (.+)'
    format: '$1'
```
```sh
rexedia -i video.mp4 -p preset.yml
```
