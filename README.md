# ZVotes by Zero Group - JavaEE Course 2014

## Getting Started

### Local
* install postgres 9.3  [link](http://postgresapp.com/)
* setup postgres

    ```
CREATE USER zvotes_db_user;
ALTER USER zvotes_db_user WITH PASSWORD 'zvotes_db_pass';
ALTER USER zvotes_db_user CREATEDB;
ALTER USER zvotes_db_user WITH SUPERUSER;
CREATE DATABASE zvotes_db_name;
GRANT ALL ON DATABASE zvotes_db_name TO zvotes_db_user;
    ```

### Glassfish

* Open http://localhost:4848/
* Goto: Konfigurationen --> server-config --> Sicherheit --> Realms
* Create new one with:
    * name: `uniko-ldap-realm`
    * class-name: `com.sun.enterprise.security.auth.realm.ldap.LDAPRealm`
    * JAAS-Kontext: `ldapRealm`
    * Verzeichnis: `ldaps://ldap.uni-koblenz.de`
    * Basis-DN: `dc=uni-koblenz-landau,dc=de`
    * Gruppen zuweisen: `VALIDUSER`
* Add some additional Attributes:
    * search-filter: `(uid=%s)`
    * group-search-filter: `(member=%d)`
    * group-base-dn: `dc=uni-koblenz-landau,dc=de`


## Fulfilled Specifications

1. __Polls__

    1.1
    
    1.2
    
    1.3
    
    1.4
    
    1.5
    
    1.6

2. __Poll states__

    2.1
    
    2.2
    
    2.3

3. __Organizers__

    3.1
    
    3.2 done
    
        3.2.1 done
        
        3.2.2 We used ldap.
    
    3.3
    
    3.4
    
    3.5

4. __Administrators__

    4.1
    
    4.2
    
    4.3
    
    4.4

5. __Participants__

    5.1
    
    5.2
    
    5.3
    
    5.4
    
    5.5
    
    5.6

6. __Participant lists__

    6.1
    
    6.2
    
    6.3
    
        6.3.1
        
        6.3.2

7. __Tokens__

    7.1
    
    7.2
    
    7.3

8. __Anonymity__

    8.1
    
    8.2
    
    8.3

9. __Participation tracking__

    9.1
    
    9.2

10. __Submitting a vote__

    10.1
    
    10.2
    
    10.3
    
    10.4
    
    10.5
    
    10.6
    
    10.7
    
    10.8
    
    10.9
    
    10.10

11. __Abstain from voting__

    11.1
    
    11.2

12. __Types of items__

    12.1
    
    12.2
    
    12.3
    
    12.4
    
    12.5
    
    12.6
    
    12.7
    
    12.8

13. __Results__

    13.1
    
    13.2
    
    13.3
    
    13.4
    
    13.5
    
    13.6

14. __User interface__

    14.1
    
    14.2
    
    14.3
    
    14.4
    
    14.5
    
    14.6

15. __Security, encrypted communication__

    15.1 We are convinced that the storage of encrypted passwords is generally unsafe because if the secret key gets leaked - all passwords can be decrypted so we choose to hash all passes (with a secret hash)
    
    15.2 We are using the RedirectToHttpsFilter on /*. To include the welcome-file we are using a WelcomeServlet to redirect von / to the actual welcome file.
    
    15.3

16. __Internationalization__

    16.1 ZVotes-web/src/main/java/com/zero/votes/beans/LanguageBean, Languages are represented in a ResourceBundle in ZVotes-web/src/main/resources/com/zero/votes/Locale_LANGUAGE_CODE in Template activated via: <f:view locale="#{language.localeCode}"/>, plus entries in WEB-INF/faces-config.xml, Internationalized Strings available via #{msg.STRINGNAME}
    
    16.2 Available in Footer via LanuageBean and Drop-Down to Select.
    
    16.3 FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    
    16.4 The language switch is realised with a form select in the footer on the right side which autosubmits itself on selection change.

17. __Browser support__

    17.1
