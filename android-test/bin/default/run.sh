#!/usr/bin/env bash
set -xeuo pipefail
ls
docker run \
	-it --rm \
	-p 8080:8080 \
	-v "$PWD":/webserver \
	android \
    sh -c "$@"