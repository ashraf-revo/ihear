#!/usr/bin/env bash
sudo add-apt-repository -y ppa:mc3man/trusty-media
sudo apt-get -qq update
sudo apt-get install -y ffmpeg
ffmpeg -version
sudo apt-get install libavdevice-dev
sudo apt-get install libavfilter-dev
sudo apt-get install libopus-dev
sudo apt-get install libvpx-dev
sudo apt-get install pkg-config