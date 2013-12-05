Natural Language Processing: A Lab
=========

#Introduction

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

#Problem Statement

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
likely than apart.

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

#Instructions

