#!/bin/bash

cd "$( dirname "$0" )"
path=$(pwd)

branch_stack=()
branches=()

# Climb upwards from root and record every encountered *.branch file
while [ "$(readlink -f $path)" != "/" ]; do
  # Prettify the path
  path=$(readlink -f "$path")
  for branch_file in $(find "$path" -maxdepth 1 -mindepth 1 -name '*.branch'); do
  if [ -e "$branch_file" ]; then
    # Store the path for later
    branch_stack[${#branch_stack[@]}]="$branch_file"
  fi
  done
  path="$path/../"
done

# Append the *.branch files content into JVM properties
for ((i=${#branch_stack[@]}-1; i>=0 ; i--));do
  branch_file=${branch_stack[$i]}
  path=$(basename $branch_file)
  content=$(cat "$branch_file")
  name=$(basename "$branch_file")
  echo Found "$branch_file := $content"
  branches[${#branches[@]}]="-D$name=$content"
done

exec java "${branches[@]}" -Xss2m -Xms2g -Xmx2g -jar project/strap/gruj_vs_sbt-launch-0.13.x.jar "$@"