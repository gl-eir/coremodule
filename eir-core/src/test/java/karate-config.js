// function(){
//
//    var env = karate.env; // get java system property 'karate.envData' from TestRunner.java
//
//      karate.log('karate.envData system property was:', env);
//
//    if (!env) {
//        env = 'dev'; // a custom 'intelligent' default
//      }
//
//    var config = { // base config JSON
//        requestBodyIs:'{"title": "Welcome To Karate","body": "Karate Automation Framework","userId": 20}'
//        , requestUrl:'https://jsonplaceholder.typicode.com/posts'
//      };
//    if (env == 'TEST') {
//        // over-ride only those that need to be
//        config.requestBodyIs = '{"title": "Welcome To Karate QA ","QA body": "Karate Automation Framework","userId": 20}';
//    } else if (env == 'STG') {
//        config.requestBodyIs = '{"title": "Welcome To Karate STG","STG body": "Karate Automation Framework","userId": 20}';
//    }
//
//    return config;
//    }

    function fn() {

        var config = {
            baseUrl : 'http://localhost:8081'
        };

        return config;
    }