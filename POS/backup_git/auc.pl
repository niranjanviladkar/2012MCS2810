#!/usr/bin/perl -w
# File:   auc.pl
# Author: Daniel Lowd (lowd at cs dot washington dot edu)
# Date:   10/17/2006
# Modified: 7/3/2012. Parag Singla
#
# This file computes the area under the precision-recall curve,
# given a list of probabilities and corresponding true values.
#
# The results file must be in the following format:
#    <predicate> <probability> <tval>
#    <predicate> <probability> <tval>
#    ...
#

if (@ARGV < 1) {
    print "Usage: $0 <results file> \n";
    exit -1;
}

$resultfile = shift;

if (!open(RESULTS, "< $resultfile")) {
    print "Error: could not open results file \"$resultfile\".\n";
    exit -1;
}

# Read in all predicate probabilities
while(<RESULTS>) {
    chomp;
    @currline = split;
    if (@currline != 3) {
        print "Error in \"$resultfile\", line $.: ";
        if (@currline > 3) { print "too many terms.\n"; }
        else { print "too few terms.\n"; }
        exit -1;
    }
    $prob{$currline[0]} = $currline[1];
    $truth{$currline[0]} = $currline[2];
}
close(RESULTS);

#if (!defined %prob) {
#    print "Error: probability file was empty!\n";
#    exit -1;
#}

# Sort the predicates by their inferred probability
@predlist = sort {$prob{$a} <=> $prob{$b}} keys %prob;

# Create a list of threshold probabilities to use
@vallist = sort {$a <=> $b} values %prob;

# Initially, guess that everything is true
$truthTrue = 0;
$guessTrue = 0;
$matchTrue = 0;
for $x (@predlist) {
    if ($truth{$x}) {
        $truthTrue++;
        $matchTrue++;
    }
    $guessTrue++;
}

if ($truthTrue == 0) {
    print "Undefined!\n";
    exit 0;
}

$precision = $matchTrue/$guessTrue;
#$recall = $matchTrue/$truthTrue; # = 1, initially
$recall = $matchTrue;
push @precisions, $precision;
push @recalls, $recall;

# One by one, make them false
$lastval = -1;
while (@vallist) {

    # Get next threshold/predicate
    $v = shift @vallist;
    $p = shift @predlist;

    # Whenever we switch thresholds, record the precision/recall for
    # the last threshold.
    if ($v != $lastval) {
        push @precisions, $matchTrue/$guessTrue;
#push @recalls, $matchTrue/$truthTrue;
        push @recalls, $matchTrue;
        $lastval = $v;
    }
 
    # Guess that this predicate is now false
    $guessTrue--;
    if ($truth{$p}) {
        $matchTrue--;
    }
}


# Now that we have a list of precisions and recalls, integrate 
# using trapezoids.
@f = ();
for $i (1..$#precisions) {
    $j = $i;
    if ($precisions[$i] == NaN || $recalls[$i] == NaN) {
        $j--;
        last;
    }

    for $x (1..($recalls[$i-1] - $recalls[$i]))
    {
      push @f, ($precisions[$i] + $precisions[$i-1])/2; 
    }

    if (0 and $recalls[$i-1] != $recalls[$i])
    {
      push @f, $truthTrue * ($recalls[$i-1] - $recalls[$i])
          * ($precisions[$i] + $precisions[$i-1])/2; 
    }
}

# Assume a line from the final point to the precision axis, to
# complete the curve.
#push @f, $truthTrue * $recalls[$j] * $precisions[$j];
for $x (1..$recalls[$j])
{
  push @f, $precisions[$j];
}

for $currf (@f) {
  $auc += $currf/$truthTrue;
}

$sum = 0;
for $currf (@f) {
  $sum += ($currf - $auc) * ($currf - $auc);
}
#$sd = sqrt($sum/($truthTrue - 1));
#$sd_m = $sd/(sqrt($truthTrue));

print "AUC = $auc\n";
#print "$sd_m\n";
