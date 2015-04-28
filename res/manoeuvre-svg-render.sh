#!/bin/bash

# fixing the width
grep -rl "802" manoeuvre-svg | xargs sed -i 's/802/162/g'

mkdir manoeuvre-png

# rendering
for i in manoeuvre-svg/*svg; do
   out=${i//svg/png}
   inkscape -z -e manoeuvre-png/manoeuvre_${out##*/} -w 486 -h 780 $i
done

