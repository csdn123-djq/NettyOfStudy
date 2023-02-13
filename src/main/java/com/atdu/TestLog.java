package com.atdu;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class TestLog {
    public static  final Logger LOGGER=LoggerFactory.getLogger(TestLog.class);
   @Test
   public  void test01(){
       LOGGER.error("error");
       LOGGER.warn("waring");
       LOGGER.info("info");
       LOGGER.debug("debug");
       LOGGER.trace("trace");
   }
   @Test
    public  void  test02(){
       LOGGER.info("hello,log4j");
   }
    @Test
    public  void  test03(){
//        String testInfo = "Free flying flowers are like dreams";
//        log.info("The test info is :{}", testInfo);


    }

    public static void main(String[] args) {
        log.info("1");
    }

    }
