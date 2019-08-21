[![Build Status](https://travis-ci.org/CNITV/SQLiggyBank.svg?branch=develop)](https://travis-ci.org/CNITV/SQLiggyBank)
[![codecov.io](https://codecov.io/gh/CNITV/SQLiggyBank/branch/develop/graphs/badge.svg?branch=develop)](http://codecov.io/github/CNITV/SQLiggyBank?branch=develop)

# SQLiggyBank

SQLiggyBank este o aplicatie cross-platform pentru a amenaja un fond al clasei cu usurinta.

Proiectul foloseste un server, client si o baza de date PostgreSQL.

## Documentatie API

Pentru a viziona documentatia API-ului SQLiggyBank, il puteti accesa pe Postman [aici](https://documenter.getpostman.com/view/3806934/RWgwRFa8).

## Documentatie ERD

Pentru a viziona ERD-ul SQLiggyBank, accesati [acest](https://www.lucidchart.com/invitations/accept/854e6f3d-4228-4794-8505-55565f9ea647) link.


Note: As of now, Aug 19 2019, the project is compiled with Java 12 modules in mind. This has numerous consequences; the
most important that need attention when both **compiling** and **executing** are: 
1. lombok doesn't work with java modules (As of today, no fix permanent fix has been released, only workarounds that make
maintenance way harder, so we chose to avoid that). **Solution**: Delombok the whole project before compiling.
2. JavaFX is no longer part of JDK starting with Java 9. This means that JavaFX is a standalone project that needs to be
included in the classpath of the project and as Maven dependencies. They also need to be included in the module-info.java
3. Since JavaFX is no longer part of the JDK, it needs to be included at runtime. This means the execution goes from this:
**java -jar target/sqliggybank.jar** to this **java --module-path PATH/TO/JAVA/FX/lib --add-modules 
javafx.controls,javafx.fxml,javafx.graphics -jar target/sqliggybank.jar**  
**!!!** This means that an additional JAVAFX SDK needs to be downloaded for this to work. It can be downloaded from [here](https://gluonhq.com/products/javafx/).
(At the time of writing this, the community is still working on developing a javafx packager, so this odd behaviour can 
be avoided and there's no need to download an additional JAVAFX SDK anymore; until that time comes, this awkward solution 
is the only way to make this work).
4. Jlink (a tool to make custom runtime images) doesn't work with some Java 12 modules, one of them being okhtpp3, that
is used in this project. A Java 13 Jpackage is being developed so behaviours like these are avoided.