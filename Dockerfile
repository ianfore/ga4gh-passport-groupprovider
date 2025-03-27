FROM trinodb/trino:latest

# GroupProvider Plugin and dependencies
RUN mkdir /usr/lib/trino/plugin/ga4gh-passport-groupprovider
COPY target/ga4gh-passport-groupprovider-393.jar /usr/lib/trino/plugin/ga4gh-passport-groupprovider
COPY target/dependency/*.jar /usr/lib/trino/plugin/ga4gh-passport-groupprovider

# Remove the default catalogs in the trino docker image
RUN rm /etc/trino/catalog/jmx.properties
RUN rm /etc/trino/catalog/memory.properties  
RUN rm /etc/trino/catalog/tpcds.properties  
RUN rm /etc/trino/catalog/tpch.properties

# Configure GroupProvider
COPY docker/security/group-provider.properties  /etc/trino


# The following could be included in the image, but as they include security information it
# is likely preferable to add them to the conatiner at run time

# Catalogs
#COPY ../../trino-catalogs/catalog/tutorial.properties  /etc/trino/catalog

# Access control method and data
# COPY docker/security/access-control.properties  /etc/trino
# COPY docker/security/rules.json  /etc/trino





