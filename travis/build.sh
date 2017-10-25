#!/bin/bash
set -ex
if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]; then     # pull request
    bundle exec fastlane test
elif [[ -z "$TRAVIS_TAG" && "$TRAVIS_BRANCH" == "master" ]]; then  # non-tag commits to master branch
    bundle exec fastlane test
    bundle exec fastlane assemble
elif [[ -z "$TRAVIS_TAG" && "$TRAVIS_BRANCH" =~ ^stable-.* ]]; then # non-tag commits to stable branches
    ./setup_git_crypt.sh
    openssl aes-256-cbc -K $encrypted_89e57280a3a1_key -iv $encrypted_89e57280a3a1_iv -in git-crypt-android-certificates.key.enc -out git-crypt-android-certificates.key -d
    git clone https://github.com/Sage-Bionetworks/android-certificates ../android-certificates
    pushd ../android-certificates
    /tmp/git-crypt-ccdcc76f8e1a639847a8accd801f5a284194e43f/git-crypt unlock $TRAVIS_BUILD_DIR/git-crypt-android-certificates.key
    popd
    bundle exec fastlane test
    bundle exec fastlane alpha alias:"$KEY_ALIAS" storepass:"$KEYSTORE_PASSWORD" keypass:"$KEY_PASSWORD" signed_apk_path:"app/build/outputs/apk/app-release.apk"
fi
exit $?
