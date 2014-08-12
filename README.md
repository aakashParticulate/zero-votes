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

## Fulfilled Specifications

1. Polls

    1.1
    
    1.2
    
    1.3
    
    1.4
    
    1.5
    
    1.6

2. Poll states

    2.1
    
    2.2
    
    2.3

3. Organizers

    3.1
    
    3.2
    
        3.2.1
        
        3.2.2
    
    3.3
    
    3.4
    
    3.5

4. Administrators

    4.1
    
    4.2
    
    4.3
    
    4.4

5. Participants

    5.1
    
    5.2
    
    5.3
    
    5.4
    
    5.5
    
    5.6

6. Participant lists

    6.1
    
    6.2
    
    6.3
    
        6.3.1
        
        6.3.2

7. Tokens

    7.1
    
    7.2
    
    7.3

8. Anonymity

    8.1
    
    8.2
    
    8.3

9. Participation tracking

    9.1
    
    9.2

10. Submitting a vote

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

11. Abstain from voting

    11.1
    
    11.2

12. Types of items

    12.1
    
    12.2
    
    12.3
    
    12.4
    
    12.5
    
    12.6
    
    12.7
    
    12.8

13. Results

    13.1
    
    13.2
    
    13.3
    
    13.4
    
    13.5
    
    13.6

14. User interface

    14.1
    
    14.2
    
    14.3
    
    14.4
    
    14.5
    
    14.6

15. Security, encrypted communication

    15.1 We are convinced that the storage of encrypted passwords is generally unsafe because if the secret key gets leaked - all passwords can be decrypted so we choose to hash all passes (with a secret hash)
    
    15.2
    
    15.3

16. Internationalization

    16.1 ZVotes-web/src/main/java/com/zero/votes/beans/LanguageBean, Languages are represented in a ResourceBundle in ZVotes-web/src/main/resources/com/zero/votes/Locale_LANGUAGE_CODE in Template activated via: <f:view locale="#{language.localeCode}"/>, plus entries in WEB-INF/faces-config.xml, Internationalized Strings available via #{msg.STRINGNAME}
    
    16.2 Available in Footer via LanuageBean and Drop-Down to Select.
    
    16.3 FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    
    16.4 The language switch is realised with a form select in the footer on the right side which autosubmits itself on selection change.

17. Browser support

    17.1
