Group 1
Members:
1155014433	Zhou Zhehui
1155014330	Xu Minxuan
1155029187  Jiang yilin

Files description:
InquirySystem: main entrance of the whole system, which contains user role change logic.
User: an abstract class which has declared runAction(int) and subMenu(). All other user classes inherits this class.
Error: helper class for error messages display.
Administrator, Librarian, LibraryDirector, LibraryUser: concrete classes that implements functions can be done by each user.

How to compile and use:
javac -cp .:ojdbc6.jar *.java
java -cp .:ojdbc6.jar InquirySystem
