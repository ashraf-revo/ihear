#!/usr/bin/env bash
sudo add-apt-repository ppa:mc3man/trusty-media
sudo apt-get update
sudo apt-get install ffmpeg
ffmpeg -version
sudo apt-get install libavdevice-dev
sudo apt-get install libavfilter-dev
sudo apt-get install libopus-dev
sudo apt-get install libvpx-dev
sudo apt-get install pkg-config