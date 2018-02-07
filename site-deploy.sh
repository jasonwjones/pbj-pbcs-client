#!/bin/bash

# Just a simple helper script to deploy site files to developer site since
# figuring out how to get Maven to do it is proving too difficult at the moment

mvn site:site
tar -cvzf $(dirname $0)/target/site.tar.gz -C $(dirname $0)/target/site .
scp $(dirname $0)/target/site.tar.gz root@jasonwjones.com:
ssh -t root@jasonwjones.com ./deploy.sh
