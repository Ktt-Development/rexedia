# rexedia ![Java CI](https://github.com/Ktt-Development/rexedia/workflows/Java%20CI/badge.svg) [![version](https://img.shields.io/github/v/release/Ktt-Development/rexedia&include_prereleases)](https://github.com/Ktt-Development/rexedia/releases) [![license](https://img.shields.io/github/license/Ktt-Development/rexedia)](https://github.com/Ktt-Development/rexedia/blob/main/LICENSE)

🎬 rexedia :: Metadata editing (including cover art) for bulk files using regular expressions (regex) and back references.

## Arguments
**Regular expression will test against the file name without the extension**

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

### Example Usage

**Assign video a cover art image with the same name**
```
┬ video.mp4
└ video.png
```
`rexedia -i video.mp4 -c "(.+)" "$1.png"`

**Assign video metadata based on the name**
```
─ [S01 E02] video.mp4 → ┌ season_number = 1
                        ├ episode_number = 2
                        └ show = video
```
`rexedia -i video.mp4 -m "season_number" "\[S0*(\d*) E0*\d*\]" "$1" -m "episode_number" "\[S0*\d* E0*(\d*)\]" "$1" -m "show" "\[S0*\d* E0*\d*\] (.+)" "$1"`

## Presets

```yml
cover:  # cover art
  regex: "(.+)"  # the regex capture string
  format: "$1"  # the final cover art name (regex backreferences supported)
metadata:  # list of metadata tags
  - meta: "name"  # the metadata tag name
    regex: "(.+)"  # the regex capture string
    format: "$1"  # the final metadata value (regex backreferences supported)
```

### Example Usage

**Assign video metadata based on the name from preset**
```
┬ [S01 E02] video.mp4 → ┌ season_number = 1
└ preset.yml            ├ episode_number = 2
                        └ show = video
```
`preset.yml`
```yml
metadata:  
  - meta: "season_number"  
    regex: "\[S0*(\d*) E0*\d*\]"
    format: "$1"
  - meta: "episode_number"
    regex: "\[S0*\d* E0*(\d*)\]"
    format: "$1"
  - meta: "show"
    regex: "\[S0*\d* E0*\d*\] (.+)"
    format: "$1"
```
`rexedia -i video.mp4 -p preset.yml`