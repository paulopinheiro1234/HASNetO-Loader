# hasneto-loader
Human-Aware Sensor Network Ontology's template parser and loader.

Usage: ./loader.sh [options] -u username -p password -k knowldgeBaseURL[-i inputXLS]
       -i inputXLS: generate ttl and load it into knowledge base;
                    inputXLS parsing warnings and errors are printed as they
                    are identified, if any
       -c : clears knowldge base
       -o : loads associated ontologies
       -v : verbose mode on, including curl's outputs
       -h : this help

Example: hasnetoloader -c -o -u user -p abcde1234 -k http://localhost/slor4 -i myspreadsheet.xlsx 
         this command will clear the knowledgbase, load associated ontologies, convert myspreadsheet.xlsx
         into turtle (ttl), and load the turtle into the knowledgebase.
