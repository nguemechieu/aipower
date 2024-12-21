#!/usr/bin/env bash

ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "script.sh");
processBuilder.start();
