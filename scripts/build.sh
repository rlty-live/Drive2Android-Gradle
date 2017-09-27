#!/bin/bash
echo "include ':drive2android'" > settings.gradle && \
./gradlew assemble && \
./gradlew publish && \
echo "include ':consumer'" > settings.gradle
