# [Class] Print1
* * *
## 1. Code Review
> 예시 코드에서는 중복된 코드와 메서드가 있는 것을 확인할 수 있습니다. printMap2는 printMap1과 동일한 기능을 가지며, printMap3은 printMap1을 두 번 반복하는 기능을 가지고 있습니다. 따라서 중복된 코드를 제거하고, printMap2와 printMap3을 printMap1을 활용하여 구현하는 것이 좋을 것 같습니다.


## 2. Navigation
* [Source Code](#3-source-code)
* [printMap1()](#5-printmap1)
* [printMap2()](#6-printmap2)
* [printMa3()](#7-printma3)

## 3. Source Code
<details><summary> Commented Code </summary><div markdown="1">

## Commented Code
* * *
 ```java 

//이 코드는 Print1 클래스에 세 개의 메서드를 정의합니다. 첫 번째 메서드인 printMap1은 별을 출력하는 함수입니다. 이중 반복문을 사용하여 2x5형태로 출력합니다. 두 번째 메서드인 printMap2는 printMap1과 동일한 기능을 수행합니다. 마지막으로 printMap3은 printMap1을 두 번 반복하여 출력하는 함수입니다.
public class Print1 {

    String str1;
    int m;

    public void printMap1(){
        for (int i=0;i<2;i++){
            for (int j=0;j<5;j++){
                System.out.println("*");
            }
            System.out.println("\n");
        }
    }
    public void printMap2(){ //printMap1과 동일한 함수
        for (int i=0;i<2;i++){
            for (int j=0;j<5;j++){
                System.out.println("*");
            }
            System.out.println("\n");
        }
    }
    public void printMa3(){ //printMap1이 두번 반복되는 함수
        for (int i=0;i<2;i++){
            for (int j=0;j<5;j++){
                System.out.println("*");
            }
            System.out.println("\n");
        }

        for (int i=0;i<2;i++){
            for (int j=0;j<5;j++){
                System.out.println("*");
            }
            System.out.println("\n");
        }
    }
}

 ``` 
</div></details><details><summary> Clean Code </summary><div markdown="1">

## Clean Code
* * *
 ```java 
public class Print1 {

    String str1;
    int m;

    public void printMap1(){
        printMapHelper();
    }
    
    public void printMap2(){
        printMapHelper();
    }
    
    public void printMap3(){
        printMapHelper();
        printMapHelper();
    }
    
    private void printMapHelper() {
        for (int i=0; i<2; i++){
            for (int j=0; j<5; j++){
                System.out.println("*");
            }
            System.out.println("\n");
        }
    }
}
 ``` 
</div></details>

## 4. Member Fields
member number | type | variable name 
:-:|:---:|:---:
1 |`String`|`str1`
2 |`int`|`m`


## 5. printMap1()
* * *
### Return Type
- `null`
### Parameter Type
param number | type | variable name 
:-:|:---:|:---:

## 6. printMap2()
* * *
### Return Type
- `null`
### Parameter Type
param number | type | variable name 
:-:|:---:|:---:

## 7. printMa3()
* * *
### Return Type
- `null`
### Parameter Type
param number | type | variable name 
:-:|:---:|:---:

* * *
