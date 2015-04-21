#!/bin/bash

# fixing the width
grep -rl "802" . | xargs sed -i 's/802/162/g'

mkdir png

# rendering
for i in *svg; do
   inkscape -z -e png/${i/svg/png} -w 486 -h 780 $i
done

