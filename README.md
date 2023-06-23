openmrs-module-relationshipmapping
==========================

A module that contains a script to map mastercard record into OpenMRS relationships
Description
-----------
mUzima enables the creation of relationships among contacts and index cases with the OpenMRS relationships feature,
and maps the same into the mastercard record.
However, mastercard records have traditionally been entered directly into EPTS using the FICHA Resumo form
that updates the mastercard (via HTMLFormEntry module).
A challenge with this approach is that the record does not use OpenMRs relationships feature,
that provides a more robust way of handling relationships among contact persons and index cases.

This script is used to map mastercard records (entered directly into EPTS via the HTMLFormEntry module)
into OpenMRS relationships feature data model. For each record in the mastercard, which comprises Observations (obs table)
of an index case patient, the following is done:
 1) A search is done to determine where there already exists a person by the NID specified in the mastercard record.
    If found, the script then checks whether a relationship of the same type as specified in the mastercard
    record doesn't exist, and subsequently creates the relationship.

 2) If the person by the NID was not found or if the NID field was blank, the script then checks whether there exists a relationship
    between the index case and a person with names same as those specified in the mastercard record. If none exists,
    it then  creates a new person and relationship between the person and the index case. If found, makes it the relationship person.

 3) For both 1 and 2 above, if a new relationship was created, Obs for the concepts 1885, 23779, 23780, 23781 are also created for
    related person with values similar to the mastercard record.

 4) In case the relationship was found per criteria in 1 and 2 above, mapping of the mastercard record is skipped and no obs are for the related person are created.

The script therefore affects the person,person_name,relationship, and obs tables.


Building from Source
--------------------
You will need to have Java 1.6+ and Maven 2.x+ installed.  Use the command 'mvn package' to 
compile and package the module.  The .omod file will be in the omod/target folder.

Alternatively you can add the snippet provided in the [Creating Modules](https://wiki.openmrs.org/x/cAEr) page to your 
omod/pom.xml and use the mvn command:

    mvn package -P deploy-web -D deploy.path="../../openmrs-1.8.x/webapp/src/main/webapp"

It will allow you to deploy any changes to your web 
resources such as jsp or js files without re-installing the module. The deploy path says 
where OpenMRS is deployed.

Installation
------------
1. Build the module to produce the .omod file.
2. Use the OpenMRS Administration > Manage Modules screen to upload and install the .omod file.

If uploads are not allowed from the web (changable via a runtime property), you can drop the omod
into the ~/.OpenMRS/modules folder.  (Where ~/.OpenMRS is assumed to be the Application 
Data Directory that the running openmrs is currently using.)  After putting the file in there 
simply restart OpenMRS/tomcat and the module will be loaded and started.
