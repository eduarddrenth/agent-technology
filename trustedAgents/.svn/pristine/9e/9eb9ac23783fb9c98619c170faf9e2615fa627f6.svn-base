This simple SecurityService facilitates security for {@link VerticalCommand}s, for example making sure only agents with a token that can be validated will be allowed to join the Jade Platform. The way it works is
that at a regular container a token is retrieved and connected to an agent AID, at the Main-Container (or mediator) the token is extracted from the AID and validated. Retrieving
a token and validating is a matter of implementing two very simple interfaces. Validating can be done using for example an external LDAP.
The Service can contain a {@link TokenProvider} or a {@link TokenValidator}.

A TokenProvider provides a token for a VerticalCommand issued by a AID, the service then connects the token to the AID


A TokenValidator retrieves a token from a VerticalCommand issued by a AID, the service then calls validate with the token, the command and the {@link AID} as arguments

Building a TokenProvider or a Validator is left to users of this security service.

configuration at main container


    services=org.logica.ctis.security.SecurityService(true)
    org.logica.ctis.security.SecurityService_trustedAgentNames=ams;rma;df
    org.logica.ctis.security.SecurityService_tokenvalidator=validatorClassname


configuration at 'client' container


    services=org.logica.ctis.security.SecurityService
    org.logica.ctis.security.SecurityService_tokenprovider=providerClassname


configuration at split 'client' container


    be-required-services=org.logica.ctis.security.SecurityService
    At the runtime where the 'mediator' lives in leap.properties: org.logica.ctis.security.SecurityService_tokenprovider=providerClassname
    or you can do validation at the mediator: org.logica.ctis.security.SecurityService_tokenvalidator=validatorClassname
    when doing validation at the mediator you probably won't use validation at the main-container

@author Eduard Drenth: Logica, 5-march-2012

