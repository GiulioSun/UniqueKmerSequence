# UniqueKmerSequence

This repo examine the mutations that occur in the human genome (reference genome) . To identify these somatic mutations has been developed a method that uses k-mer(short segment of DNA sequence) counts.

**Description of the algorithm**

Starting from the reference genome, we consider some mutations  that are inserted by substitution within the reference genome. From the new genome we extract the wild-type and mutated regions. From the regions are extracted the k-mers,  that  come from wild-type region or from  mutated region,  from the k-mers  we go to take only the unique. We then consider two samples, one tumor and one normal, so we extract the k-mers from the two samples and count them. After we perform two joins , one to find normal sequences in wild-type sequences and in  mutated sequences and another to find mutation sequences in wild-type sequences and in mutated sequences. In the last steps we calculate the median counts of mutant k-mers and wild-type k-mers within the normal sample and the tumor sample. The differences between the two median counts will serve us to implement the binomial test that will allow us to validate or not a certain mutation.

**Requirements**

This code has been tested with the following software versions:

Apache Spark 2.4.8
Neo4j 4.2.1

**Packages used in java**

* org.apache.spark.api.java.function
* org.apache.log4j
* org.apache.commons.math3.stat.inference
* org.apache.spark
* org.apache.spark.api.java
* org.neo4j.driver
* org.apache.spark.sql

**Link material**

*link to download the files*

https://dna-discovery.stanford.edu/publicmaterial/software/kmervc/

**Script Command Line Usage**

Input files

* chrT.fa : file of the reference genome
* Mut.txt : file of mutations
* normal-1.fq : file of the normal sample
* tumor-1.fq : file of the tumor sample

**Output**

Intermediate Output : Median counts of wild-type and mutant k-mers in tumor sample and in normal sample

Final Output : Binomial test

**Description of the java-eclipse classes**

* NewGen: the new genome built by inserting the mutations into the reference genome
* KmerRif: k-mers of mutated and wild-type regions
* KmerSample: k-mers of the normal sample and the tumor sample
* Mutation: substitutions with mutations in certain positions within the reference genome
* Count: The class that contains the attributes ( kmer,count, regionwt,type, regionmut)
* Filter: These symbols @, 5, +, >, are deleted from substring
* FileReader: This class is used to read both the reference genome file and the mutation file
* Sequence
* Tumor: wrapper class that encapsulates information
* Normal: wrapper class that encapsulates information

Main:
1.Read input files

2.Insert the mutations in the reference genome

3.The wildtype regions and mutated regions from which I get the k-mers

4.I choose the unique k-mers of mutated regions and wildtype regions

5.Import of normal sample and tumor sample files

6.Filtering the files of the two samples

7.Find the k-mers of the two samples

8.k-mers counts

9.We perform a join to find normal sequences in wild-type and mutated sequences

10.Calculation of the median of wild-type and mutant counts for each mutation (the median was calculated for the normal sample and for the tumor sample)

11.Calculation of the test binomial test to validate the mutation

12.Connection with neo4j

13.Creation of these nodes (normNode, tumorNode, regionNode) and related relationships
