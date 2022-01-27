package com.vivid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple TestWebApp.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void a2i(){
        System.out.println((myAtoi("2147483646.0001")));
    }
    public int myAtoi(String str) {
        str = str.trim();
        int len = str.length();
        if(len==0 || str == null){
            return 0;
        }
        int i=0;
        long result = 0;
        boolean flag = true;
        if(str.charAt(i) == '+'){
            i++;
        }else if(str.charAt(i) == '-'){
            i++;
            flag = false;
        }else if('0'>str.charAt(i)|| str.charAt(i)>'9'){
            return 0;
        }
        int count = 0;
        for(; i<len; i++){
            char a = str.charAt(i);
            if('0'<=a && a<='9'){
                result = result*10 +(a-'0');
                if(result != 0){
                    count++;
                }
//                if(count > 10){
//                    return flag ? Integer.MAX_VALUE : Integer.MIN_VALUE;
//                }
            }else{
                break;
            }
        }
        result = flag ? result : -result;
        if(result > (long)Integer.MAX_VALUE ||
                result < (long)Integer.MIN_VALUE){
            return flag ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        return (int)result;
    }
}
