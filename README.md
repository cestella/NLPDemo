Natural Language Processing: A Lab
=========

##Introduction

> It is surprisingly hard for computers to handle free text as smoothly
> and effectively as humans do. So far, the results of the numerous
> efforts to achieve this have been mixed. Indeed, at times it has
> appeared that the complexities of free text are such as to render the
> effort futile. Not so; in fact, successive attempts to address the
> problem of converting free text into actionable knowledge have advanced
> the science of natural language processing and led to demand for
> software that simulates and complements what people are able to do. 
-- [Computational Medical Center](http://computationalmedicine.org/challenge/previous)

The goal of this lab is to demonstrate the general look and feel of a
natural language processing problem.  The domain from which the lab is
taken is clinical (i.e. medical) analysis/informatics.  Understanding
unstructured data in this domain has been of particular interest as
 clinical data gathering has a long history, but clinical unstructured 
data processing has been largely untouched. 


Some of the reasons for this include:
* Complexity/difficulty of natural language processing
* The need for a platform to parallelize this processing

In the course of this lab, you will be exposed to a NLP problem and
asked to solve it using open source NLP libraries and Pig/Hadoop.

##Problem Statement

When analyzing medical reports, it's important to understand which sets
of words are associated with a particular disease. This helps pick out
from within the report which sentences mention the disease without
necessarily mentioning the disease name.  It's also, generally, an
interesting piece of analysis.

A challenge was created in 2007 by the Computational Medical Center to
extract diagnosis codes from free text using NLP.  You can find the
report and the raw data
[here](http://computationalmedicine.org/challenge/previous).
This data associates small sets of sentences with a diagnosis code 
([ICD-9](http://en.wikipedia.org/wiki/Diagnosis_code) ).  I've
pre-processed this into a Ctrl-A separated
[file](https://raw.github.com/cestella/NLPDemo/solution/NLPDemo/src/main/data/sentences.dat) of the following format:
* diagnosis code
* type
* sentences indicating the code

We are going to analyze this data to find statistically improbable pairs
of words.  By which, I mean within the set of sentences associated with
a given diagnosis, pairs of words that appear nearby more statistically
likely than apart.  We are going to do this analysis by creating a
custom Pig UDF which takes the sentences from a given diagnosis code and
outputs the statistically improbable pairs of words.

For instance, given the code 486, which is the diagnosis code for
pneumonia, consider the following list of statistically improbable
phrases:
<table>
<tr><td>new</td><td>leave</td></tr>
<tr><td>airspace</td><td>disease</td></tr>
<tr><td>disease</td><td>describe</td></tr> 
<tr><td>patient</td><td>have</td></tr> 
<tr><td>have</td><td>pneumonia</td></tr> 
<tr><td>change</td><td>compare</td></tr> 
<tr><td>pattern</td><td>mycoplasma</td></tr> 
<tr><td>have</td><td>change</td></tr> 
<tr><td>hemidiaphragm</td><td>heart</td></tr> 
<tr><td>define</td><td>opacity</td></tr>
</table> 

As you can see some of these phrases are spot on (i.e. mycoplasma is
the type of bacteria which causes bacterial pneumonia), but some are
too general.  This illustrates a key point: machine learning, data
science and natural language processing can only take you so far and
work best when their output is comprehensible *and* serves to assist a
human to make the final decision.

##Instructions

The assumption is that you have installed:
* A HDP 1.3 [sandbox](http://hortonworks.com/products/hortonworks-sandbox/) sandbox started at $sandbox_ip.
* [Apache Maven](http://maven.apache.org)
* Some IDE which you prefer to develop Java code in (Eclipse or
  Intellij, likely)

### Getting the Code and setting up the Development Environment
From the terminal, cd into the workspace of your choice and pull down
the code for this project:

	git clone https://github.com/cestella/NLPDemo.git
	cd NLPDemo/NLPDemo

Now, create your IDE metadata using maven for Eclipse:

	mvn eclipse:eclipse -DdownloadSources -DdownloadJavadocs

or Intellij:

	mvn idea:idea -DdownloadSources -DdownloadJavadocs

From there, import the project into your respective IDE as an existing
Java project.  Note: You can also import the maven project directly.

### Completing the Code

Complete the following exercises:

0. Familiarize yourself with the methods in the NLPUtil class and what
they do.  You will use them extensively in step 1. (Hint: you will
re-use one of the functions in the solution for 1)
1. The NLPUtil class is the main work-horse for the actual NLP work and heavily utilizes the [Stanford CoreNLP project](http://nlp.stanford.edu/software/corenlp.shtml).  You
can use this class to complete the Pig UDF GET_SIP, which emits a bag of
tuples representing the statistically improbable pairs of words and their
rank. 
2. The pig script `statistically_improbable_phrases.pig` loads the data and generates the statistically improbable phrases.  Fill in the missing piece that calls the UDF you completed in step 1.
3. (Bonus) The approach to find statistically improbable phrases uses
  [Scaled Mutual
Information](http://matpalm.com/blog/2011/10/22/collocations_1/).  The implementation of which is
located in the `ScaledMutualInformationScorer` class which implements `Scorer`.  There
are a number of other approaches that might do better.  Investigate
alternatives (hint: start
[here](http://matpalm.com/blog/2011/11/05/collocations_2/) and
[here](http://tdunning.blogspot.com/2008/03/surprise-and-coincidence.html)
) and implement your own `Scorer`.
4. (Bonus) Right now we ignore all non-noun or verb words in sentences, but we might do better by ignoring a set of very common verbs (like 'be' or 'have').  Integrate a [stopword list](http://en.wikipedia.org/wiki/Stop_words) by implementing a Guava Predicate to be passed into NLPUtil.getStatisticallyImprobableBigrams() as the filter argument which filters out common verbs.

General hint: the remote branch `solution` contains my solution to 1 and 2.

You will know that you have completed 1 and 2 when you are able to run
the `StatisticallyImprobablePhrasesTest` (or do a `mvn clean package`,
which runs the test).

### Building the Project
From the NLPDemo/NLPDemo directory:

	mvn package

This will bundle the scripts, data and jar file into
NLPDemo-1.0-SNAPSHOT-archive.tar.gz in the target directory.
We now need to upload this to the sandbox:

	ssh root@$sandbox_ip "mkdir ~/nlpdemo"
	scp target/NLPDemo-1.0-SNAPSHOT-archive.tar.gz root@$sandbox_ip:~/nlpdemo


### Preparing the Data (needs to be done once)

Now, ssh into the sandbox

	ssh root@$sandbox_ip
	cd ~/nlpdemo
	tar xzvf NLPDemo-1.0-SNAPSHOT-archive.tar.gz
	./ingest.sh /user/root/nlp

This will put the sentence data in /user/root/nlp/data

### Running the Script

Now, we need to execute the pig script which generates the statistically
improbable phrases for each diagnosis code.

From the sandbox in ~/nlpdemo/ directory

	pig -Dpig.additional.jars=./NLPDemo-1.0-SNAPSHOT.jar -param input=nlp/data -param output=nlp/output pig/statistically_improbable_phrases.pig
	hadoop fs -getmerge nlp/output sips.dat

### Reviewing the Result

Now, let's review some results by looking at the list of all diagnosis
codes with their descriptions:

	./output_codes.sh ./sips.dat

<pre>
462 -- Acute pharyngitis
486 -- Pneumonia, organism unspecified
591 -- Hydronephrosis
511.9 -- Unspecified pleural effusion
518.0 -- Pulmonary collapse
592.0 -- Calculus of kidney
593.1 -- Hypertrophy of kidney
593.5 -- Hydroureter
...
</pre>

Now, pick one, say 486, and look at the summary:

	./code_summary.sh 486 ./sips.dat

<pre>
DIAG_CODE
-----------------
486

DESCRIPTION
=================
Pneumonia, organism unspecified

BIGRAMS w/ SCORE
=================
new leave 0.5046421544038601
airspace  disease 0.4938649825192107
disease describe  0.3827538714080996
patient have  0.3688781006727753
have  pneumonia 0.24695790830960948
change  compare 0.1837338711883127
pattern mycoplasma  0.163521328355978
have  change  0.15040053785497937
hemidiaphragm heart 0.11661268238133117
define  opacity 0.10736281125753386
</pre>
