# Java AES implementation
This is a little Project, I had. 
This here is a simple implementation of the AES Algorythm based on the one from Rijndael. At the moment, it can de- and encrypt files.

## Usage
If you only want to encrypt:  
`[name] inputfile`  
Then the key will be stored as inputfile.aeskey and the outputfile will be stored as inputfile.enc  

For encryption with a specific key  
`[name] -i inputfile -k keyfile -e`  
Then the output will be stored as inputfile.enc

For the decryption  
`[name] -i inputfile -k keyfile -d`  
Here the file to output will be stored (as not other mentioned) as inputfile  minus the last 4 characters (default without .enc)

All arguments:  

    -i inputfile        File for input
    -h                  Display the help  
    -o output           File for output  
    -k keyfile          File of the key  
    -v [optional] mode  Enable the verbose mode  
    -p                  Enable the percentag mode to display the percent finished (May spam some command promts)  
    -e or -d            Encryption or decryption mode  
    -p                  Enable the percentage mode to display percent (Slows down encryption)
 
