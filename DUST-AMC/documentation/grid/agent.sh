#!/bin/bash -l

java -cp jade.jar:DUST-AMC.jar jade.MicroBoot -host alegre.science.uva.nl -port 1099 agent:com.logica.test.TestAgent

#java -version 

exit 0