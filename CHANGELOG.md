# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added 🐨

- Add Copy Chat Message (Control+Click in chat): Copy chat messages to your clipboard with legacy formatting codes (`&`). Behaviors available:
  - Control+Click: Copy the clicked message
  - Control+Shift+Click: Select a range of messages, from a first clicked message to a second one
  - Compatible with `{{RMT}}` syntax
    - Escaping `/` and `&` with a backslash (`\`).

## [3.0.1] - 2026-04-19

### Changed

- Mod Update Checker now only runs when user joins a server for the first time after Minecraft client launch

### Fixed

- Fix Mod Update Checker cannot display game message

## [3.0.0] - 2026-04-15

This version is a rewrite of the last WikiTools version for the latest Minecraft versions and the Fabric platform.

### Added

- Add Copy Data Tags (Key: N): Copy data tags (NBT) to your clipboard. This feature works on:
  - Entities you are looking at; The player skin texture ID (if any) is also retrieved
  - Items you are hovering over
- Add Copy Item Tooltip (Key X): Copy tooltip data of the item you are hovering over to your clipboard. Formats available:
  - Inventory slot template call
  - Tooltip module data item
- Add Copy Opened UI (Key C): Copy the UI menu you have open into a Wiki UI format to your clipboard. Behaviors available:
  - Fill with blank by default mode
  - Always use Minecraft item name for non skull items mode
- Add Copy Skull ID (Key: Z): Copy the texture ID of a skull to your clipboard. This feature works on:
  - Placed player heads
  - Entities wearing player heads (excluding Players and NPCs)
  - Player head items you are hovering over
- Add View Item ID (when Show Advanced Tooltips is on): Find the SkyBlock item ID of the item you are hovering over and append it to the tooltip shown on screen
- Add Mod Update Checker: Check for new WikiTools release on GitHub and send an update reminder message
- Add license: LGPL-3.0-or-later

[unreleased]: https://github.com/skyblock-wiki/wikitools/compare/v3.0.1...HEAD
[3.0.1]: https://github.com/skyblock-wiki/wikitools/compare/v3.0.0...v3.0.1
[3.0.0]: https://github.com/skyblock-wiki/wikitools/tree/v3.0.0
