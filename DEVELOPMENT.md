# Developing Bility

## Building :bility-core

To push a new version of bility-core to Bintray, run the following:

```
./gradlew bility-core:bintrayUpload
```

## Building :bility-android

To push a new version of bility-android to Bintray, run the following:

```
./gradlew bility-android:bintrayUpload
```

## Developing docs

To develop documentation using Docusaurus, use the following command in `bility-docs`:

```
yarn install
yarn start
```