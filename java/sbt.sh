#!/bin/bash

path=$(pwd)

while [ "$(readlink -f $path)" != "/" ]; do
    for branch_file in $(find $path -maxdepth 1 -mindepth 1 -name "*.branch"); do
	content=$(cat "$branch_file" | tr -d '\r\n')
	name=$(basename "$branch_file" | tr '.[a-z]' '_[A-Z]')
	branch_data="$branch_data -D$name=\"$content\""
    done
    path="$path/../"
done

java $branch_data -Xss2m -Xms2g -Xmx2g -jar project/strap/gruj_vs_sbt-launch-0.13.x.jar \"$*\"
