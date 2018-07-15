#!/usr/bin/env bash
set -xeuo pipefail
ls
docker run \
	-it --rm \
	-v "$PWD":/application \
	android \
    sh -c "$@"