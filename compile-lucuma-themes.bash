#! /bin/bash

if [ -z "$1" ]; then
  echo "No output directory supplied"
  exit 2
fi

# Exit on errors - fails sbt publish instead of swallowing errors
set -e

mkdir -p $1

darkTheme="lucuma/lucuma-dark:dark-theme.css"
lightTheme="lucuma/lucuma-light:light-theme.css"

themes=($darkTheme $lightTheme)
for theme in ${themes[@]}; do
  IFS=':'
  read -a parts <<< "$theme"
  themeName="${parts[0]}"
  sassFile="themes/$themeName/theme.scss"
  cssFile="$1/${parts[1]}"
  echo "compiling $sassFile to $cssFile"
  node node_modules/sass/sass.js --no-source-map $sassFile >"$cssFile"
done
