#!/bin/bash

lines=$(wmctrl -lG | grep VLC | awk '{OFS=":"; print $1,$5,$6}')
i=1
for line in $lines; do
  window_id=$(echo $line | cut -d ":" -f1)
  width=$(echo $line | cut -d ":" -f2)
  height=$(echo $line | cut -d ":" -f3)

  wmctrl -i -r $window_id -e 0,$((i*40)),200,$width,$height
  i=$((i+1))
done