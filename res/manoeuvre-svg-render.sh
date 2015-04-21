#!/bin/bash

# fixing the width
grep -rl "802" manoeuvre-svg | xargs sed -i 's/802/162/g'

mkdir manoeuvre-png

# rendering
for i in *svg; do
   inkscape -z -e ${i//svg/png} -w 486 -h 780 $i
done

