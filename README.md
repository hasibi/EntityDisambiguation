EntityDisambiguation
====================

- To run this program, pass "-disamb" as input argument.
- By this command, the program will read the following files and genarates results.
    - ./output/wikis.log.norm.txt 
    - ./output/geos.log.norm.txt
    
- The results are stored at ./output
    - Prediction results are stored as *.pred.txt
    - Evaluations are stored as *.eval.txt


** NOTE **

For the first time run, you need to first perform Pre-processing.

For pre-processing, pass "-prep ./path/of/file" as input argument.
This will generate the above wiki and geo files.
