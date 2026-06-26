# [1.0.0-rc.6](https://github.com/matheus-souza/build-bell/compare/v1.0.0-rc.5...v1.0.0-rc.6) (2026-06-26)


### Features

* improve plugin listing following JetBrains Marketplace best practices ([e670941](https://github.com/matheus-souza/build-bell/commit/e6709419d69059968f3988c62650d63171c60842))

# [1.0.0-rc.5](https://github.com/matheus-souza/build-bell/compare/v1.0.0-rc.4...v1.0.0-rc.5) (2026-06-26)


### Bug Fixes

* escape HTML in changeNotes and warn when CHANGELOG section is missing ([c450802](https://github.com/matheus-souza/build-bell/commit/c450802c163e0a7349fea3216d1aec4614a23ab9))

# [1.0.0-rc.4](https://github.com/matheus-souza/build-bell/compare/v1.0.0-rc.3...v1.0.0-rc.4) (2026-06-26)


### Features

* populate What's New from CHANGELOG.md on every release ([0a4ad7d](https://github.com/matheus-souza/build-bell/commit/0a4ad7dd012fd4a5ed2b3ea04fdb9f0cf7c9769d))

# [1.0.0-rc.3](https://github.com/matheus-souza/build-bell/compare/v1.0.0-rc.2...v1.0.0-rc.3) (2026-06-25)


### Features

* add SVG plugin icon for JetBrains Marketplace display ([0d6e569](https://github.com/matheus-souza/build-bell/commit/0d6e569aa6b4425703c4b787bcd429cdd2f4ace1))

# [1.0.0-rc.2](https://github.com/matheus-souza/build-bell/compare/v1.0.0-rc.1...v1.0.0-rc.2) (2026-06-25)


### Bug Fixes

* align intellij SDK version with sinceBuild and use full IC versions for CDN ([26660df](https://github.com/matheus-souza/build-bell/commit/26660df3e180df6f3a6cb13a60bb9feb5578da62))
* revert intellij SDK version and free disk space before plugin verifier ([0d3d43d](https://github.com/matheus-souza/build-bell/commit/0d3d43d6e73edf3cd97970cac661019480a17a6a))
* use patch ideVersions and skip IC entries absent from JetBrains CDN ([d9726f0](https://github.com/matheus-souza/build-bell/commit/d9726f0852ca590f62e9f09cb03e30fe86967fa8))

# 1.0.0-rc.1 (2026-06-25)


### Bug Fixes

* add conventional commit prefix to Android Studio version update PRs ([d43b894](https://github.com/matheus-souza/build-bell/commit/d43b89431eef1fadca337fd4348f8518986f21e7))
* align base IntelliJ SDK to 2023.3.7 matching sinceBuild 233 ([4426db2](https://github.com/matheus-souza/build-bell/commit/4426db2e8ce68ab038091993ca75023fa30a8fea))
* disable build cache in sonar job to ensure JaCoCo captures fresh coverage ([f39039b](https://github.com/matheus-souza/build-bell/commit/f39039b84779edc5b07aa5825695c125a225e621))
* do not downgrade untilBuild when current value is already higher ([785db73](https://github.com/matheus-souza/build-bell/commit/785db73c511d7121c172ffbd5c26a0f95f6684be))
* free disk space before plugin verifier to prevent runner OOM ([ad34a96](https://github.com/matheus-souza/build-bell/commit/ad34a96e551402a99c4ceffd78b44a1e3c3b58b4))
* raise sinceBuild to 233 (Android Studio Koala) ([0974dc0](https://github.com/matheus-souza/build-bell/commit/0974dc047e8c8b002dbda198b11fb8a20f602552)), closes [#8931295](https://github.com/matheus-souza/build-bell/issues/8931295)
* remove absolute path arg from SonarCloud scan step ([a1c357b](https://github.com/matheus-souza/build-bell/commit/a1c357bb7b3ab1abb831302d39b44dea6d11bfc5))
* remove IC-2026.1.1 from ideVersions — IntelliJ Community 2026.x does not exist ([a6f692f](https://github.com/matheus-souza/build-bell/commit/a6f692fee7347e758cfaa2d4690130e707bdd3ad))
* remove PathClassLoader to allow JaCoCo to instrument plugin classes ([82feebd](https://github.com/matheus-souza/build-bell/commit/82feebdbf68aa34013f4d287a51c241228598b96))
* replace JacocoInstrument (non-existent) with JaCoCo Ant offline instrumentation ([d62d371](https://github.com/matheus-souza/build-bell/commit/d62d3712d219faa0a09f900754c51c91d4abbc2e))
* replace unavailable IC-2025.1 with IC-2025.1.2 in plugin verifier ([76e7aec](https://github.com/matheus-souza/build-bell/commit/76e7aec3d2708f60ceeb7a6d2a9796785acc160a))
* resolve 0% JaCoCo coverage with offline instrumentation and classpath ordering ([b134c7e](https://github.com/matheus-souza/build-bell/commit/b134c7ef1d5f8ce54dccca4930edb60d780c3a71))
* revert IntelliJ SDK to 2023.2.2 (android plugin not bundled in 2023.3) ([0aa3775](https://github.com/matheus-souza/build-bell/commit/0aa37758b426630b383a15544f7caa259f4780f4))
* switch to native sonar-scanner to fix 0% coverage upload ([ddf3386](https://github.com/matheus-souza/build-bell/commit/ddf33862d1c79169adc09592c035d6cc36389bb4))
* use GH_PAT for auto-merge to allow release workflow to trigger ([2ce0722](https://github.com/matheus-souza/build-bell/commit/2ce07222a9b17aa7357577155deb0f5a119ad615))
* use GH_PAT in semantic-release to bypass develop branch protection ([8ab0539](https://github.com/matheus-souza/build-bell/commit/8ab05390364a425cb74a8a456bf7c7e53cf6a76f))
* use JaCoCo offline instrumentation to bypass IntelliJ PathClassLoader ([daa1024](https://github.com/matheus-souza/build-bell/commit/daa1024ad97efafb3d4af954d91ca171909c8c46))
* use maybeCreate for jacocoAnt and jacocoRuntime configurations ([7c5b4bf](https://github.com/matheus-souza/build-bell/commit/7c5b4bfdb34cb20d804e0bc4cef4e4af1629517c))
* use specific patch versions in ideVersions for plugin verifier ([492633f](https://github.com/matheus-souza/build-bell/commit/492633faff9578b08c3245b3708fdf1f0622f950))


### Features

* **ci:** add Conventional Commits enforcement and branch policy ([88ed99d](https://github.com/matheus-souza/build-bell/commit/88ed99dcd902afb71b86add03443e8bb78fd944c))
* extend untilBuild using Canary platform major ([9eebebc](https://github.com/matheus-souza/build-bell/commit/9eebebcbda8b6939e919238005ee83c9ca18e07f))
* **release:** add semantic-release with RC and stable channels ([c388761](https://github.com/matheus-souza/build-bell/commit/c3887612d669dcbb93171e06201f15e9c81db7f4))

# Changelog
