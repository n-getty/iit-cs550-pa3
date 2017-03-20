#!/usr/bin/perl

use strict;
use warnings;

my $filename = $ARGV[0]; 

open(my $fh2, '<', $filename)
    or die "Could not open out file '$filename' $!";

my $cur_Time=0;

while (my $row = <$fh2>) {
    chomp $row;
    if ($row =~ /.*LOGGING: Requesting file: .* (\d+).*/){ # row for checking 
	$cur_Time = $1;
    }
    elsif ($row =~ /.*LOGGING: Receiving query: .* (\d+).*/) { # row for recording
	print $1 - $cur_Time;
	print "\n";
    }
}
