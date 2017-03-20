#!/bin/bash
#
# The first argument is the number of hosts (10)
#
# Check for correct usage
if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters, correct usage"
    echo "Arg1: number of hosts making query"
    exit
fi
#

# clean the old stuff
rm -r tests/*
rm ../topologies/topo/*
#

# create directories and initialize input files
mkdir tests
for n in `seq 1 9`; do
    mkdir tests/test$(($n))
    mkdir tests/test$(($n))/origin
    mkdir tests/test$(($n))/peer   
    touch ../topologies/topo/input_$(($n)).txt
done

let 'n=0'
# generate reandom files
for folder in `seq 1 9`; do
    for j in `seq 1 10`; do
	let 'n+=1'
	dd if=/dev/urandom of=tests/test$(printf "$folder")/origin/file_$(printf %03d "$n").bin bs=1024 count=$(printf "$n")
    done
done

# generate input files
for fold in `seq 1 $1`; do	    
    files=($(shuf -i 1-90))
    #echo $files
    #for ((i=1; i<${#files[*]}; i++));
    #do
    #echo ${files[i]}
    #done
    
    for f in "${files[@]}"; do
	echo $f
	echo file_$(printf %03d "$f").bin >> ../topologies/topo/input_$(printf "$fold").txt
    done
done

for uu in `seq 1 9`; do
    echo exit  >> ../topologies/topo/input_$(printf "$fold").txt
done 
