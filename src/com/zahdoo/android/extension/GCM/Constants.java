/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.zahdoo.android.extension.GCM;

public class Constants {
    //You should replace these values with your own
//    public static final String AWS_ACCOUNT_ID = "YOUR_ACCOUNT_ID";
//    public static final String COGNITO_POOL_ID = 
//            "YOUR_COGNITO_POOL_ID";
//    public static final String COGNITO_ROLE_UNAUTH = 
//            "YOUR_COGNITO_UNAUTH_ROLE";
//    public static final String BUCKET_NAME = "YOUR_BUCKET_NAME";
	
	
	public static final String AWS_ACCOUNT_ID = "870073266171";
    public static final String COGNITO_POOL_ID = 
            "us-east-1:adb5c421-c13a-48be-ae8a-5082b932c593";
    
    public static final String COGNITO_ROLE_UNAUTH = 
            "arn:aws:iam::870073266171:role/CADIE_PROD_ROLE_1";
    
    public static final String COGNITO_ROLE_AUTH = 
            "Cognito_CADIE_PROD_S3_POOLAuth_DefaultRole";
    
    public static final String BUCKET_NAME = "CADIE_FILES";
}
