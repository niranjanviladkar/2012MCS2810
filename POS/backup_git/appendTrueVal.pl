#!/usr/bin/perl
# The following program is responsible for processing the output of alchemy on test.db
# and converting it into a format on which AUC/F-measure can be computed. In particular,
# it reads the corresponding training files and appends the actual training truth value
# for each query predicate 
 
use strict;

my $pred;
my %predTable;

my $inputfile=$ARGV[0];
my $resultfile=$ARGV[1];
my $dbfile=$ARGV[2];

open(db,$dbfile) || die "could not open the file : $dbfile";

#Make a hashtable of true predicates
while($pred = <db>) {
	 chomp($pred);
	 $pred =~ s/ //g;
	 $predTable{$pred}="";
}
close(db);

my @lines;
my @fields;
my $line;
my $val;
my $prob;
my $str;
my $rest;
my $size;
my $index;

#done reading the db file. Now, read the input file
open(input,$inputfile) || die "could not open the file : $inputfile";
@lines=<input>;
close(input);

#now, write back the results file, along with the ground truth value
open(result,">$resultfile") || die "could not open the file : $resultfile for writing";

foreach $line (@lines) {
	chomp($line);
	@fields = split(' ',$line);
	$pred = $fields[0];
	$prob = $fields[1];
	$val = 0;
   
    #we will simply append the rest of the line
	$rest = "";
	$size=@fields;
	for($index=2;$index<$size;$index++) {
		 $rest = $rest." ".$fields[$index];
	}

	$pred =~ s/ //g;
#check if this predicate appears in the db file without a negation - if yes, set val to 1
    if(defined $predTable{$pred}) {
		 $str = $predTable{$pred};
		 if($str !~ /\!/) {
		  $val = 1;
		 }
	}

    print result "$pred $prob $val $rest\n";
}
close(result);

