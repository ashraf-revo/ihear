#!/usr/bin/env bash
sudo add-apt-repository -y ppa:jonathonf/ffmpeg-4
sudo apt-get update
sudo apt-get install -y ffmpeg
ffmpeg -version
sudo apt-get install -y libavdevice-dev
sudo apt-get install -y libavfilter-dev
sudo apt-get install -y libopus-dev
sudo apt-get install -y libvpx-dev
sudo apt-get install -y pkg-config
sudo apt-get install -y libsrtp2-dev
sudo apt-get install -y libffi-dev