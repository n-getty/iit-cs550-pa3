#!/usr/bin/perl

use strict;
use warnings;

my $filename = $ARGV[0]; 


my $misses=0;
my $hits=0;
my $i=1;
my $file='';
while ($i < 10) {
    
    $file=$filename . 'out_' . $i . '.txt';
    print $file . "\n";
    open(my $fh2, '<', $file)
	or die "Could not open out file '$file' $!";
    while (my $row = <$fh2>) {
	chomp $row;
	if ($row =~ /LOGGING: Receiving query: .*/){ # row for checking 
	    $hits = $hits + 1;
	}
	elsif ($row =~ /.*Peer.* an invalid version of file .*/) { # row for recording
	    $misses = $misses + 1;
	}
    }
    $i = $i + 1;
}

print 'hits: ' . $hits . "\n";
print 'misses: ' . $misses . "\n";
print $misses / $hits . "\n";
